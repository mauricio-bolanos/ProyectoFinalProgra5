package mauricio.u.latina.bolanos.rosales.data.repository

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import mauricio.u.latina.bolanos.rosales.data.database.interfaces.CanalesDao
import mauricio.u.latina.bolanos.rosales.model.Canales

class CanalesRepository(
    private val canalesDao: CanalesDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    // Estado de las operaciones
    private val _operationStatus = MutableStateFlow<OperationStatus>(OperationStatus.Idle)
    val operationStatus: StateFlow<OperationStatus> = _operationStatus

    sealed class OperationStatus {
        object Idle : OperationStatus()
        object Loading : OperationStatus()
        data class Success(val message: String) : OperationStatus()
        data class Error(val exception: Throwable) : OperationStatus()
    }

    // Añadir un nuevo canal
    suspend fun addCanal(canal: Canales): String = withContext(ioDispatcher) {
        _operationStatus.value = OperationStatus.Loading
        try {
            val canalId = canalesDao.addCanal(canal)
            _operationStatus.value = OperationStatus.Success("Canal añadido correctamente")
            canalId
        } catch (e: Exception) {
            _operationStatus.value = OperationStatus.Error(e)
            ""
        }
    }

    // Obtener un canal por ID
    suspend fun getCanalById(canalId: String): Canales? = withContext(ioDispatcher) {
        _operationStatus.value = OperationStatus.Loading
        try {
            val canal = canalesDao.getCanalById(canalId)
            _operationStatus.value = if (canal != null) {
                OperationStatus.Success("Canal obtenido correctamente")
            } else {
                OperationStatus.Error(Exception("Canal no encontrado"))
            }
            canal
        } catch (e: Exception) {
            _operationStatus.value = OperationStatus.Error(e)
            null
        }
    }

    // Actualizar un canal
    suspend fun updateCanal(canal: Canales): Boolean = withContext(ioDispatcher) {
        _operationStatus.value = OperationStatus.Loading
        try {
            val success = canalesDao.updateCanal(canal)
            _operationStatus.value = if (success) {
                OperationStatus.Success("Canal actualizado correctamente")
            } else {
                OperationStatus.Error(Exception("Error al actualizar canal"))
            }
            success
        } catch (e: Exception) {
            _operationStatus.value = OperationStatus.Error(e)
            false
        }
    }

    // Eliminar un canal
    suspend fun deleteCanal(canalId: String): Boolean = withContext(ioDispatcher) {
        _operationStatus.value = OperationStatus.Loading
        try {
            val success = canalesDao.deleteCanal(canalId)
            _operationStatus.value = if (success) {
                OperationStatus.Success("Canal eliminado correctamente")
            } else {
                OperationStatus.Error(Exception("Error al eliminar canal"))
            }
            success
        } catch (e: Exception) {
            _operationStatus.value = OperationStatus.Error(e)
            false
        }
    }

    // Obtener todos los canales como Flow
    fun getAllCanales(): Flow<List<Canales>> = canalesDao.getAllCanalesAsFlow()

    // Obtener canales por tipo
    fun getCanalesByType(tipo: String): Flow<List<Canales>> {
        return canalesDao.getAllCanalesAsFlow()
            .map { canales -> canales.filter { it.tipo_de_canal == tipo } }
    }

    // Obtener canales públicos (donde miembros_permiso es true)
    fun getCanalesPublicos(): Flow<List<Canales>> {
        return canalesDao.getAllCanalesAsFlow()
            .map { canales -> canales.filter { it.miembros_permiso } }
    }
}