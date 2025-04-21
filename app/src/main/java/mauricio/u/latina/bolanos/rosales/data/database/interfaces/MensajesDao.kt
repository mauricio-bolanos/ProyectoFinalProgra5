package mauricio.u.latina.bolanos.rosales.data.database.interfaces

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import mauricio.u.latina.bolanos.rosales.model.Mensajes
import javax.inject.Inject

class MensajesDao @Inject constructor(
    private val database: FirebaseDatabase
) {
    private val mensajesRef: DatabaseReference by lazy {
        database.getReference("canales").apply {
            keepSynced(true) // Mantener datos sincronizados offline
        }
    }

    // Crear un nuevo mensaje
    suspend fun crearMensaje(mensaje: Mensajes): String {
        val newRef = mensajesRef.push()
        mensaje.id = newRef.key ?: throw Exception("No se pudo generar ID para el mensaje")
        newRef.setValue(mensaje).await()
        return mensaje.id
    }

    // Obtener un mensaje por su ID
    suspend fun obtenerMensajePorId(id: String): Mensajes? {
        val snapshot = mensajesRef.child(id).get().await()
        return snapshot.getValue<Mensajes>()?.copy(id = id)
    }

    // Obtener todos los mensajes ordenados por fecha/hora
    suspend fun obtenerTodosLosMensajes(): List<Mensajes> {
        val snapshot = mensajesRef.orderByChild("fechaHora").get().await()
        return snapshot.children.mapNotNull { child ->
            child.getValue<Mensajes>()?.copy(id = child.key ?: "")
        }
    }

    // Obtener mensajes de un remitente específico
    suspend fun obtenerMensajesPorRemitente(remitente: String): List<Mensajes> {
        val snapshot = mensajesRef
            .orderByChild("remitente")
            .equalTo(remitente)
            .get().await()

        return snapshot.children.mapNotNull { child ->
            child.getValue<Mensajes>()?.copy(id = child.key ?: "")
        }
    }

    // Actualizar un mensaje existente
    suspend fun actualizarMensaje(mensaje: Mensajes) {
        require(mensaje.id.isNotEmpty()) { "El ID del mensaje no puede estar vacío para actualizar" }
        mensajesRef.child(mensaje.id).setValue(mensaje).await()
    }

    // Eliminar un mensaje por su ID
    suspend fun eliminarMensaje(id: String) {
        mensajesRef.child(id).removeValue().await()
    }

    // Escuchar cambios en la colección de mensajes (para actualizaciones en tiempo real)
    fun observarMensajes(callback: (List<Mensajes>) -> Unit): ValueEventListener {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val mensajes = snapshot.children.mapNotNull { child ->
                    child.getValue<Mensajes>()?.copy(id = child.key ?: "")
                }.sortedBy { it.fechaHora }
                callback(mensajes)
            }

            override fun onCancelled(error: DatabaseError) {
                // Manejar el error según sea necesario
            }
        }

        mensajesRef.orderByChild("fechaHora").addValueEventListener(listener)
        return listener
    }

    // Funcion para dejar de escuchar cambios
    fun dejarDeObservar(listener: ValueEventListener) {
        mensajesRef.removeEventListener(listener)
    }
    // En la clase MensajesDao
    fun observarMensajesFlow(): Flow<List<Mensajes>> {
        return callbackFlow {
            val listener = observarMensajes { mensajes ->
                trySend(mensajes)
            }

            awaitClose {
                dejarDeObservar(listener)
            }
        }
    }
}