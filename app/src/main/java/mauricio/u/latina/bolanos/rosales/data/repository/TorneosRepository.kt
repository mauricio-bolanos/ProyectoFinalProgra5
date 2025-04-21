package mauricio.u.latina.bolanos.rosales.data.repository


import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import mauricio.u.latina.bolanos.rosales.data.database.interfaces.TorneosDao
import mauricio.u.latina.bolanos.rosales.model.Torneos
import javax.inject.Inject

class TorneosRepository @Inject constructor(
    private val torneosDao: TorneosDao
) {
    suspend fun guardarTorneo(torneo: Torneos): String {
        return torneosDao.guardarTorneo(torneo)
    }

    suspend fun obtenerTorneos(): List<Torneos> {
        return torneosDao.obtenerTodos()
    }

    fun observarTorneos(): Flow<List<Torneos>> {
        return callbackFlow {
            val listener = torneosDao.observarTorneos { torneos ->
                trySend(torneos)
            }

            awaitClose {
                torneosDao.dejarDeObservar(listener) // Asegúrate de tener esta función en TorneosDao
            }
        }
    }
    suspend fun eliminarTorneo(id: String): Boolean {
        return try {
            torneosDao.eliminarTorneo(id)
            true // Éxito
        } catch (e: Exception) {
            false // Falla
        }
    }
}