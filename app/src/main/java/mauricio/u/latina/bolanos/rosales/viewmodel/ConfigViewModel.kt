package mauricio.u.latina.bolanos.rosales.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import mauricio.u.latina.bolanos.rosales.data.repository.ConfigRepository
import mauricio.u.latina.bolanos.rosales.model.ConfigUsuario

class ConfigViewModel(private val repository: ConfigRepository) : ViewModel() {
    private val _configState = MutableStateFlow<ConfigUsuario?>(null)
    val configState: StateFlow<ConfigUsuario?> = _configState.asStateFlow()

    // Cargar configuración
    fun cargarConfig(userId: String) {
        viewModelScope.launch {
            _configState.value = repository.obtenerConfig(userId)
        }
    }

    // Actualizar configuración
    fun actualizarConfig(config: ConfigUsuario) {
        viewModelScope.launch {
            repository.guardarConfig(config)
            _configState.value = config // Actualizar el estado local
        }
    }
}