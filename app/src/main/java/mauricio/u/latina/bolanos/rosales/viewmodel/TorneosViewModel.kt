package mauricio.u.latina.bolanos.rosales.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import mauricio.u.latina.bolanos.rosales.data.repository.TorneosRepository
import mauricio.u.latina.bolanos.rosales.model.Torneos
import javax.inject.Inject

class TorneosViewModel @Inject constructor(
    private val repository: TorneosRepository
) : ViewModel() {
    private val _torneosState = MutableStateFlow<List<Torneos>>(emptyList())
    val torneosState: StateFlow<List<Torneos>> = _torneosState.asStateFlow()

    private val _errorState = MutableSharedFlow<String>()
    val errorState: SharedFlow<String> = _errorState.asSharedFlow()

    init {
        cargarTorneos()
    }

    private fun cargarTorneos() {
        viewModelScope.launch {
            repository.observarTorneos()
                .catch { e -> _errorState.emit(e.message ?: "Error desconocido") }
                .collect { torneos ->
                    _torneosState.value = torneos
                }
        }
    }

    fun crearTorneo(torneo: Torneos) {
        viewModelScope.launch {
            try {
                repository.guardarTorneo(torneo)
            } catch (e: Exception) {
                _errorState.emit("Error al guardar: ${e.message}")
            }
        }
    }

    fun eliminarTorneo(id: String) {
        viewModelScope.launch {
            try {
                repository.eliminarTorneo(id)
            } catch (e: Exception) {
                _errorState.emit("Error al eliminar: ${e.message}")
            }
        }
    }
}