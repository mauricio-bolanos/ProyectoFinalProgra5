package mauricio.u.latina.bolanos.rosales.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import mauricio.u.latina.bolanos.rosales.data.repository.ListaJugadoresRepository
import mauricio.u.latina.bolanos.rosales.model.ListaJugadores
import javax.inject.Inject

class ListaJugadoresViewModel @Inject constructor(
    private val repository: ListaJugadoresRepository
) : ViewModel() {
    private val _jugadoresState = MutableStateFlow<List<ListaJugadores>>(emptyList())
    val jugadoresState: StateFlow<List<ListaJugadores>> = _jugadoresState.asStateFlow()

    private val _operacionState = MutableSharedFlow<String>()
    val operacionState: SharedFlow<String> = _operacionState.asSharedFlow()

    fun cargarJugadoresPartida(idPartida: String) {
        viewModelScope.launch {
            repository.observarJugadoresPartida(idPartida)
                .catch { e -> _operacionState.emit("Error: ${e.message}") }
                .collect { jugadores ->
                    _jugadoresState.value = jugadores
                }
        }
    }

    fun agregarJugador(jugador: ListaJugadores) {
        viewModelScope.launch {
            try {
                val id = repository.guardarJugador(jugador)
                _operacionState.emit("Jugador agregado con ID: $id")
            } catch (e: Exception) {
                _operacionState.emit("Error al agregar: ${e.message}")
            }
        }
    }

    fun eliminarJugador(idLista: String) {
        viewModelScope.launch {
            try {
                if (repository.eliminarJugador(idLista)) {
                    _operacionState.emit("Jugador eliminado")
                } else {
                    _operacionState.emit("Error al eliminar")
                }
            } catch (e: Exception) {
                _operacionState.emit("Error: ${e.message}")
            }
        }
    }
}