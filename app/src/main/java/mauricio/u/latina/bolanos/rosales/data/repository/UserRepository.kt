package mauricio.u.latina.bolanos.rosales.data.repository


import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext
import mauricio.u.latina.bolanos.rosales.data.database.interfaces.UsersDao
import mauricio.u.latina.bolanos.rosales.model.Users
import java.lang.Exception

class UserRepository(
    private val usersDao: UsersDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    // Flujo para el estado de las operaciones
    private val _operationStatus = MutableStateFlow<OperationStatus>(OperationStatus.Idle)
    val operationStatus: StateFlow<OperationStatus> = _operationStatus

    sealed class OperationStatus {
        object Idle : OperationStatus()
        object Loading : OperationStatus()
        data class Success(val message: String) : OperationStatus()
        data class Error(val exception: Throwable) : OperationStatus()
    }

    // Añadir un nuevo usuario
    suspend fun addUser(user: Users): String = withContext(ioDispatcher) {
        _operationStatus.value = OperationStatus.Loading
        try {
            val userId = usersDao.addUser(user)
            _operationStatus.value = OperationStatus.Success("Usuario añadido correctamente")
            userId
        } catch (e: Exception) {
            _operationStatus.value = OperationStatus.Error(e)
            ""
        }
    }

    // Obtener un usuario por ID
    suspend fun getUserById(userId: String): Users? = withContext(ioDispatcher) {
        _operationStatus.value = OperationStatus.Loading
        try {
            val user = usersDao.getUserById(userId)
            _operationStatus.value = if (user != null) {
                OperationStatus.Success("Usuario obtenido correctamente")
            } else {
                OperationStatus.Error(Exception("Usuario no encontrado"))
            }
            user
        } catch (e: Exception) {
            _operationStatus.value = OperationStatus.Error(e)
            null
        }
    }

    suspend fun updateUser(user: Users): Boolean = withContext(ioDispatcher) {
        _operationStatus.value = OperationStatus.Loading
        try {
            val success = usersDao.updateUser(user)
            _operationStatus.value = if (success) {
                OperationStatus.Success("Usuario actualizado correctamente")
            } else {
                OperationStatus.Error(Exception("Error al actualizar usuario"))
            }
            success
        } catch (e: Exception) {
            _operationStatus.value = OperationStatus.Error(e)
            false
        }
    }

    suspend fun deleteUser(userId: String): Boolean = withContext(ioDispatcher) {
        _operationStatus.value = OperationStatus.Loading
        try {
            val success = usersDao.deleteUser(userId)
            _operationStatus.value = if (success) {
                OperationStatus.Success("Usuario eliminado correctamente")
            } else {
                OperationStatus.Error(Exception("Error al eliminar usuario"))
            }
            success
        } catch (e: Exception) {
            _operationStatus.value = OperationStatus.Error(e)
            false
        }
    }

    // Obtener todos los usuarios como Flow
    fun getAllUsers(): Flow<List<Users>> {
        return usersDao.getAllUsersAsFlow()
    }


    // Obtener usuarios por rol con Flow
    fun getUsersByRoleFlow(role: String): Flow<List<Users>> = usersDao.getAllUsersAsFlow()
        .map { users -> users.filter { it.rol == role } }
        .onStart { _operationStatus.value = OperationStatus.Loading }
        .catch { e ->
            _operationStatus.value = OperationStatus.Error(e)
            emit(emptyList())
        }
}