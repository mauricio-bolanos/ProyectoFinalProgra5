package mauricio.u.latina.bolanos.rosales.data.database.interfaces

import com.google.firebase.database.*
import kotlinx.coroutines.tasks.await
import mauricio.u.latina.bolanos.rosales.model.Partidas
import javax.inject.Inject

class PartidasDao @Inject constructor(
    private val database: FirebaseDatabase
) {
    private val partidasRef: DatabaseReference by lazy {
        database.getReference("partidas").apply {
            keepSynced(true) // Mantener datos sincronizados offline
        }
    }

    // CREATE/UPDATE
    suspend fun guardarPartida(partida: Partidas): String {
        return if (partida.id.isEmpty()) {
            val newRef = partidasRef.push()
            partida.id = newRef.key ?: throw Exception("Error al generar ID")
            newRef.setValue(partida.toMap()).await()
            partida.id
        } else {
            partidasRef.child(partida.id).setValue(partida.toMap()).await()
            partida.id
        }
    }

    // READ (Todas las partidas)
    suspend fun obtenerTodas(): List<Partidas> {
        val snapshot = partidasRef.get().await()
        return snapshot.children.mapNotNull { child ->
            child.getValue(Partidas::class.java)?.copy(id = child.key ?: "")
        }
    }

    // READ (Por ID)
    suspend fun obtenerPorId(id: String): Partidas? {
        val snapshot = partidasRef.child(id).get().await()
        return snapshot.getValue(Partidas::class.java)?.copy(id = id)
    }

    // DELETE
    suspend fun eliminarPartida(id: String) {
        partidasRef.child(id).removeValue().await()
    }

    // Escucha cambios en tiempo real
    fun observarPartidas(callback: (List<Partidas>) -> Unit): ValueEventListener {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val partidas = snapshot.children.mapNotNull { child ->
                    child.getValue(Partidas::class.java)?.copy(id = child.key ?: "")
                }.sortedBy { it.getFechaHoraDate() }
                callback(partidas)
            }

            override fun onCancelled(error: DatabaseError) {
                // Opcional: callback con lista vacía o manejo de error
                callback(emptyList())
            }
        }
        partidasRef.addValueEventListener(listener)
        return listener
    }

    // Función para dejar de observar (NUEVA)
    fun dejarDeObservar(listener: ValueEventListener) {
        partidasRef.removeEventListener(listener)
    }
}