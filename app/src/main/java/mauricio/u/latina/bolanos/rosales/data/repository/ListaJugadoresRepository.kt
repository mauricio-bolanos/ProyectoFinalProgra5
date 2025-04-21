package mauricio.u.latina.bolanos.rosales.data.repository

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import mauricio.u.latina.bolanos.rosales.data.database.interfaces.ListaJugadoresDao
import mauricio.u.latina.bolanos.rosales.model.ListaJugadores
import javax.inject.Inject

class ListaJugadoresRepository @Inject constructor(
    private val dao: ListaJugadoresDao
) {
    suspend fun guardarJugador(jugador: ListaJugadores): String {
        return dao.guardarJugador(jugador)
    }

    suspend fun obtenerPorPartida(idPartida: String): List<ListaJugadores> {
        return dao.obtenerPorPartida(idPartida)
    }

    suspend fun obtenerPorTorneo(idTorneo: String): List<ListaJugadores> {
        return dao.obtenerPorTorneo(idTorneo)
    }

    suspend fun eliminarJugador(idLista: String): Boolean {
        return try {
            dao.eliminarJugador(idLista)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun observarJugadoresPartida(idPartida: String): Flow<List<ListaJugadores>> {
        return callbackFlow {
            val listener = dao.observarJugadoresPartida(idPartida) { jugadores ->
                trySend(jugadores)
            }
            awaitClose {
                dao.dejarDeObservar(listener)
            }
        }
    }
}