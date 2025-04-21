package mauricio.u.latina.bolanos.rosales.data.database.interfaces

import com.google.firebase.database.*
import kotlinx.coroutines.tasks.await
import mauricio.u.latina.bolanos.rosales.model.Torneos
import javax.inject.Inject

class TorneosDao @Inject constructor(
    private val database: FirebaseDatabase
) {
    private val torneosRef: DatabaseReference by lazy {
        database.getReference("torneos")
    }

    // CREATE o UPDATE
    suspend fun guardarTorneo(torneo: Torneos): String {
        return if (torneo.id.isEmpty()) {
            val newRef = torneosRef.push()
            torneo.id = newRef.key ?: throw Exception("Error al generar ID")
            newRef.setValue(torneo.toMap()).await()
            torneo.id
        } else {
            torneosRef.child(torneo.id).setValue(torneo.toMap()).await()
            torneo.id
        }
    }

    // READ (Todos los torneos)
    suspend fun obtenerTodos(): List<Torneos> {
        val snapshot = torneosRef.get().await()
        return snapshot.children.mapNotNull { child ->
            child.getValue(Torneos::class.java)?.copy(id = child.key ?: "")
        }
    }

    // READ (Por ID)
    suspend fun obtenerPorId(id: String): Torneos? {
        val snapshot = torneosRef.child(id).get().await()
        return snapshot.getValue(Torneos::class.java)?.copy(id = id)
    }

    // DELETE
    suspend fun eliminarTorneo(id: String) {
        torneosRef.child(id).removeValue().await()
    }


    fun observarTorneos(callback: (List<Torneos>) -> Unit): ValueEventListener {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val torneos = snapshot.children.mapNotNull { child ->
                    child.getValue(Torneos::class.java)?.copy(id = child.key ?: "")
                }
                callback(torneos)
            }

            override fun onCancelled(error: DatabaseError) {
                // Manejar error
            }
        }
        torneosRef.addValueEventListener(listener)
        return listener
    }

    fun dejarDeObservar(listener: ValueEventListener) {
        torneosRef.removeEventListener(listener)
    }
}