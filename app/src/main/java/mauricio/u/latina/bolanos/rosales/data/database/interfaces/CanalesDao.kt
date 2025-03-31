package mauricio.u.latina.bolanos.rosales.data.database.interfaces

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import mauricio.u.latina.bolanos.rosales.data.database.ArcanumDatabase
import mauricio.u.latina.bolanos.rosales.model.Canales
import javax.inject.Inject

class CanalesDao @Inject constructor(
    private val arcanumDatabase: ArcanumDatabase
) {
    private val canalesReference = arcanumDatabase.canalesReference

    suspend fun addCanal(canal: Canales): String {
        return try {
            val newCanalRef = canalesReference.push()
            val canalId = newCanalRef.key ?: throw Exception("No se pudo generar ID de canal")
            val canalWithId = canal.copy(id = canalId)
            newCanalRef.setValue(canalWithId.toMap()).await()
            canalId
        } catch (e: Exception) {
            throw Exception("Error al añadir canal: ${e.message}")
        }
    }

    suspend fun getCanalById(canalId: String): Canales? {
        return try {
            val snapshot = canalesReference.child(canalId).get().await()
            snapshot.getValue(Canales::class.java)?.copy(id = canalId)
        } catch (e: Exception) {
            throw Exception("Error al obtener canal: ${e.message}")
        }
    }

    suspend fun updateCanal(canal: Canales): Boolean {
        return try {
            canalesReference.child(canal.id).updateChildren(canal.toMap()).await()
            true
        } catch (e: Exception) {
            throw Exception("Error al actualizar canal: ${e.message}")
        }
    }

    suspend fun deleteCanal(canalId: String): Boolean {
        return try {
            canalesReference.child(canalId).removeValue().await()
            true
        } catch (e: Exception) {
            throw Exception("Error al eliminar canal: ${e.message}")
        }
    }

    fun getAllCanalesAsFlow(): Flow<List<Canales>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val canales = snapshot.children.mapNotNull {
                    it.getValue(Canales::class.java)?.copy(id = it.key ?: "")
                }
                trySend(canales)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        canalesReference.addValueEventListener(listener)
        awaitClose { canalesReference.removeEventListener(listener) }
    }

    // Versión con callback para compatibilidad
    fun getAllCanales(callback: (List<Canales>) -> Unit) {
        canalesReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val canales = snapshot.children.mapNotNull {
                    it.getValue(Canales::class.java)?.copy(id = it.key ?: "")
                }
                callback(canales)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(emptyList())
            }
        })
    }
}