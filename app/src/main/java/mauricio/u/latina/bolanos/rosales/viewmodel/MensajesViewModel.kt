package mauricio.u.latina.bolanos.rosales.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import mauricio.u.latina.bolanos.rosales.data.repository.MensajesRepository
import mauricio.u.latina.bolanos.rosales.model.Mensajes

class MensajesViewModel(
    private val repository: MensajesRepository
) : ViewModel() {


    sealed class UiState {
        object Loading : UiState()
        data class Success(val mensajes: List<Mensajes>) : UiState()
        data class Error(val message: String) : UiState()
    }

    // Estado observable
    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    // Eventos
    sealed class UiEvent {
        data class SendMessage(val remitente: String, val contenido: String) : UiEvent()
        data class DeleteMessage(val id: String) : UiEvent()
        data class FilterBySender(val remitente: String) : UiEvent()
    }

    init {
        loadMessages()
    }

    fun handleEvent(event: UiEvent) {
        when (event) {
            is UiEvent.SendMessage -> sendMessage(event.remitente, event.contenido)
            is UiEvent.DeleteMessage -> deleteMessage(event.id)
            is UiEvent.FilterBySender -> filterBySender(event.remitente)
        }
    }

    private fun loadMessages() {
        viewModelScope.launch {
            repository.obtenerTodosLosMensajes()
                .catch { e ->
                    _uiState.value = UiState.Error("Error: ${e.message}")
                }
                .collect { mensajes ->
                    _uiState.value = UiState.Success(mensajes)
                }
        }
    }

    private fun sendMessage(remitente: String, contenido: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                repository.crearMensaje(remitente, contenido)
                // Recargar mensajes después de enviar
                loadMessages()
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Error al enviar: ${e.message}")
            }
        }
    }

    private fun deleteMessage(id: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                repository.eliminarMensaje(id)
                // Recargar mensajes después de eliminar
                loadMessages()
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Error al eliminar: ${e.message}")
            }
        }
    }

    private fun filterBySender(remitente: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                repository.obtenerMensajesPorRemitente(remitente)
                    .collect { mensajes ->
                        _uiState.value = UiState.Success(mensajes)
                    }
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Error al filtrar: ${e.message}")
            }
        }
    }
}