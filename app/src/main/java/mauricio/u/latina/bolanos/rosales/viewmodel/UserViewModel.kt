package mauricio.u.latina.bolanos.rosales.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import mauricio.u.latina.bolanos.rosales.data.database.interfaces.UsersDao
import mauricio.u.latina.bolanos.rosales.model.Users
import mauricio.u.latina.bolanos.rosales.data.repository.UserRepository.OperationStatus
import javax.inject.Inject

class UserViewModel @Inject constructor(
    private val usersDao: UsersDao
) : ViewModel() {

    // Estado de las operaciones
    private val _operationStatus = MutableStateFlow<OperationStatus>(OperationStatus.Idle)
    val operationStatus: StateFlow<OperationStatus> = _operationStatus.asStateFlow()
    // Lista de todos los usuarios con StateFlow
    private val _allUsers = MutableStateFlow<List<Users>>(emptyList())
    val allUsers: StateFlow<List<Users>> = _allUsers.asStateFlow()

    // Función para cargar usuarios con callback
    fun loadUsers(callback: (List<Users>) -> Unit = {}) {
        viewModelScope.launch {
            _operationStatus.value = OperationStatus.Loading
            usersDao.getAllUsers { users ->
                _allUsers.value = users
                callback(users) // Ejecutamos el callback proporcionado
                _operationStatus.value = OperationStatus.Success("${users.size} usuarios cargados")
            }
        }
    }

    // Usuarios filtrados por rol
    private val _usersByRole = MutableStateFlow<List<Users>>(emptyList())
    val usersByRole: StateFlow<List<Users>> = _usersByRole.asStateFlow()

    // Usuario actual seleccionado
    private val _selectedUser = MutableStateFlow<Users?>(null)
    val selectedUser: StateFlow<Users?> = _selectedUser.asStateFlow()

    // Función para añadir un nuevo usuario
    fun addUser(user: Users) {
        viewModelScope.launch {
            val userId = usersDao.addUser(user)
            if (userId.isNotEmpty()) {
                _operationStatus.value = OperationStatus.Success("Usuario creado con ID: $userId")
            }
        }
    }

    // Función para obtener un usuario por ID
    fun getUserById(userId: String) {
        viewModelScope.launch {
            _operationStatus.value = OperationStatus.Loading
            val user = usersDao.getUserById(userId)
            _selectedUser.value = user
            _operationStatus.value = if (user != null) {
                OperationStatus.Success("Usuario obtenido correctamente")
            } else {
                OperationStatus.Error(Exception("Usuario no encontrado"))
            }
        }
    }

    // Función para actualizar un usuario
    fun updateUser(user: Users) {
        viewModelScope.launch {
            val success = usersDao.updateUser(user)
            _operationStatus.value = if (success) {
                OperationStatus.Success("Usuario actualizado correctamente")
            } else {
                OperationStatus.Error(Exception("Error al actualizar usuario"))
            }
        }
    }

    // Función para eliminar un usuario
    fun deleteUser(userId: String) {
        viewModelScope.launch {
            val success = usersDao.deleteUser(userId)
            _operationStatus.value = if (success) {
                OperationStatus.Success("Usuario eliminado correctamente")
            } else {
                OperationStatus.Error(Exception("Error al eliminar usuario"))
            }
        }
    }

    // Función para filtrar usuarios por rol
    fun getUsersByRole(role: String) {
        viewModelScope.launch {
            _operationStatus.value = OperationStatus.Loading
            usersDao.getUsersByRoleFlow(role)
                .catch { e ->
                    _operationStatus.value = OperationStatus.Error(e)
                    _usersByRole.value = emptyList()
                }
                .collect { users ->
                    _usersByRole.value = users
                    _operationStatus.value =
                        OperationStatus.Success("${users.size} usuarios encontrados con rol $role")
                }
        }
    }

    // Función para resetear el estado de operaciones
    fun resetOperationStatus() {
        _operationStatus.value = OperationStatus.Idle
    }

    // Función para seleccionar un usuario
    fun selectUser(user: Users) {
        _selectedUser.value = user
    }

    // Función para limpiar la selección
    fun clearSelection() {
        _selectedUser.value = null
    }
}