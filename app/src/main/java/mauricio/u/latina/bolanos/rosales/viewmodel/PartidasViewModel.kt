package mauricio.u.latina.bolanos.rosales.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import mauricio.u.latina.bolanos.rosales.data.repository.PartidasRepository
import mauricio.u.latina.bolanos.rosales.model.Partidas
import javax.inject.Inject

class PartidasViewModel @Inject constructor(
    private val repository: PartidasRepository
) : ViewModel() {
    private val _partidasState = MutableStateFlow<List<Partidas>>(emptyList())
    val partidasState: StateFlow<List<Partidas>> = _partidasState.asStateFlow()

    private val _operacionState = MutableSharedFlow<String>()
    val operacionState: SharedFlow<String> = _operacionState.asSharedFlow()

    init {
        cargarPartidas()
    }

    private fun cargarPartidas() {
        viewModelScope.launch {
            repository.observarPartidas()
                .catch { e -> _operacionState.emit("Error: ${e.message}") }
                .collect { partidas ->
                    _partidasState.value = partidas.sortedBy { it.getFechaHoraDate() }
                }
        }
    }

    fun crearPartida(partida: Partidas) {
        viewModelScope.launch {
            try {
                val id = repository.guardarPartida(partida)
                _operacionState.emit("Partida creada con ID: $id")
            } catch (e: Exception) {
                _operacionState.emit("Error al crear: ${e.message}")
            }
        }
    }

    fun eliminarPartida(id: String) {
        viewModelScope.launch {
            try {
                if (repository.eliminarPartida(id)) {
                    _operacionState.emit("Partida eliminada")
                } else {
                    _operacionState.emit("Error al eliminar")
                }
            } catch (e: Exception) {
                _operacionState.emit("Error: ${e.message}")
            }
        }
    }
}