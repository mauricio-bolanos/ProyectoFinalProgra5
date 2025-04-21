package mauricio.u.latina.bolanos.rosales.data.repository

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import mauricio.u.latina.bolanos.rosales.data.database.interfaces.PartidasDao
import mauricio.u.latina.bolanos.rosales.model.Partidas
import javax.inject.Inject

class PartidasRepository @Inject constructor(
    private val partidasDao: PartidasDao
) {
    suspend fun guardarPartida(partida: Partidas): String {
        return partidasDao.guardarPartida(partida)
    }

    suspend fun obtenerPartidas(): List<Partidas> {
        return partidasDao.obtenerTodas()
    }

    suspend fun eliminarPartida(id: String): Boolean {
        return try {
            partidasDao.eliminarPartida(id)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun observarPartidas(): Flow<List<Partidas>> {
        return callbackFlow {
            val listener = partidasDao.observarPartidas { partidas ->
                trySend(partidas)
            }
            awaitClose {
                partidasDao.dejarDeObservar(listener)
            }
        }
    }
}