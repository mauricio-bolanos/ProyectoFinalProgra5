package mauricio.u.latina.bolanos.rosales.data.database.interfaces

import com.google.firebase.database.*
import kotlinx.coroutines.tasks.await
import mauricio.u.latina.bolanos.rosales.model.ListaJugadores
import javax.inject.Inject

class ListaJugadoresDao @Inject constructor(
    private val database: FirebaseDatabase
) {
    private val listaJugadoresRef: DatabaseReference by lazy {
        database.getReference("listaJugadores")
    }

    // CREATE/UPDATE
    suspend fun guardarJugador(jugador: ListaJugadores): String {
        return if (jugador.idLista.isEmpty()) {
            val newRef = listaJugadoresRef.push()
            jugador.idLista = newRef.key ?: throw Exception("Error al generar ID")
            newRef.setValue(jugador.toMap()).await()
            jugador.idLista
        } else {
            listaJugadoresRef.child(jugador.idLista).setValue(jugador.toMap()).await()
            jugador.idLista
        }
    }

    // READ (Por Partida)
    suspend fun obtenerPorPartida(idPartida: String): List<ListaJugadores> {
        val snapshot = listaJugadoresRef
            .orderByChild("idPartida")
            .equalTo(idPartida)
            .get()
            .await()

        return snapshot.children.mapNotNull { child ->
            child.getValue(ListaJugadores::class.java)?.copy(idLista = child.key ?: "")
        }.sortedBy { it.posicion }
    }

    // READ (Por Torneo)
    suspend fun obtenerPorTorneo(idTorneo: String): List<ListaJugadores> {
        val snapshot = listaJugadoresRef
            .orderByChild("idTorneo")
            .equalTo(idTorneo)
            .get()
            .await()

        return snapshot.children.mapNotNull { child ->
            child.getValue(ListaJugadores::class.java)?.copy(idLista = child.key ?: "")
        }.sortedBy { it.posicion }
    }

    // DELETE
    suspend fun eliminarJugador(idLista: String) {
        listaJugadoresRef.child(idLista).removeValue().await()
    }

    // Escucha cambios en tiempo real para una partida
    fun observarJugadoresPartida(idPartida: String, callback: (List<ListaJugadores>) -> Unit): ValueEventListener {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val jugadores = snapshot.children.mapNotNull { child ->
                    child.getValue(ListaJugadores::class.java)?.copy(idLista = child.key ?: "")
                }.sortedBy { it.posicion }
                callback(jugadores)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(emptyList())
            }
        }
        listaJugadoresRef
            .orderByChild("idPartida")
            .equalTo(idPartida)
            .addValueEventListener(listener)
        return listener
    }

    fun dejarDeObservar(listener: ValueEventListener) {
        listaJugadoresRef.removeEventListener(listener)
    }
}