package mauricio.u.latina.bolanos.rosales.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import mauricio.u.latina.bolanos.rosales.model.Canales
import mauricio.u.latina.bolanos.rosales.data.repository.CanalesRepository
import mauricio.u.latina.bolanos.rosales.data.repository.CanalesRepository.OperationStatus
import javax.inject.Inject

@HiltViewModel
class CanalesViewModel @Inject constructor(
    private val canalesRepository: CanalesRepository
) : ViewModel() {

    // Estado de las operaciones
    private val _operationStatus = MutableStateFlow<OperationStatus>(OperationStatus.Idle)
    val operationStatus: StateFlow<OperationStatus> = _operationStatus.asStateFlow()

    // Lista de todos los canales
    val allCanales: StateFlow<List<Canales>> = canalesRepository.getAllCanales()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Canales filtrados por tipo
    private val _canalesByType = MutableStateFlow<List<Canales>>(emptyList())
    val canalesByType: StateFlow<List<Canales>> = _canalesByType.asStateFlow()

    // Canales públicos (con miembros_permiso = true)
    val canalesPublicos: StateFlow<List<Canales>> = canalesRepository.getCanalesPublicos()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Canal actualmente seleccionado
    private val _selectedCanal = MutableStateFlow<Canales?>(null)
    val selectedCanal: StateFlow<Canales?> = _selectedCanal.asStateFlow()

    // Añadir un nuevo canal
    fun addCanal(canal: Canales) {
        viewModelScope.launch {
            val canalId = canalesRepository.addCanal(canal)
            if (canalId.isNotEmpty()) {
                _operationStatus.value = OperationStatus.Success("Canal creado con ID: $canalId")
            }
        }
    }

    // Obtener canal por ID
    fun getCanalById(canalId: String) {
        viewModelScope.launch {
            _operationStatus.value = OperationStatus.Loading
            val canal = canalesRepository.getCanalById(canalId)
            _selectedCanal.value = canal
            _operationStatus.value = if (canal != null) {
                OperationStatus.Success("Canal obtenido correctamente")
            } else {
                OperationStatus.Error(Exception("Canal no encontrado"))
            }
        }
    }

    // Actualizar un canal
    fun updateCanal(canal: Canales) {
        viewModelScope.launch {
            val success = canalesRepository.updateCanal(canal)
            _operationStatus.value = if (success) {
                OperationStatus.Success("Canal actualizado correctamente")
            } else {
                OperationStatus.Error(Exception("Error al actualizar canal"))
            }
        }
    }

    // Eliminar un canal
    fun deleteCanal(canalId: String) {
        viewModelScope.launch {
            val success = canalesRepository.deleteCanal(canalId)
            _operationStatus.value = if (success) {
                OperationStatus.Success("Canal eliminado correctamente")
            } else {
                OperationStatus.Error(Exception("Error al eliminar canal"))
            }
            // Si eliminamos el canal seleccionado, lo limpiamos
            if (_selectedCanal.value?.id == canalId) {
                _selectedCanal.value = null
            }
        }
    }

    // Filtrar canales por tipo
    fun filterCanalesByType(tipo: String) {
        viewModelScope.launch {
            canalesRepository.getCanalesByType(tipo)
                .collect { canales ->
                    _canalesByType.value = canales
                    _operationStatus.value =
                        OperationStatus.Success("${canales.size} canales de tipo $tipo")
                }
        }
    }

    // Seleccionar un canal
    fun selectCanal(canal: Canales) {
        _selectedCanal.value = canal
    }

    // Limpiar selección
    fun clearSelection() {
        _selectedCanal.value = null
    }

    // Resetear estado de operación
    fun resetOperationStatus() {
        _operationStatus.value = OperationStatus.Idle
    }
}