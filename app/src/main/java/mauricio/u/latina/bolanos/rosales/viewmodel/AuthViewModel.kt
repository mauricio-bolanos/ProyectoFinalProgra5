package mauricio.u.latina.bolanos.rosales.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import androidx.compose.runtime.State


@HiltViewModel
class AuthViewModel @Inject constructor(
    private val auth: FirebaseAuth
) : ViewModel() {


    private val _uiState = mutableStateOf<AuthUiState>(AuthUiState.Initial)
    val uiState: State<AuthUiState> = _uiState

    private val _currentUser = mutableStateOf<FirebaseUser?>(null)
    val currentUser: State<FirebaseUser?> = _currentUser

    init {
        auth.addAuthStateListener { firebaseAuth ->
            _currentUser.value = firebaseAuth.currentUser
        }
    }

    fun login(email: String, password: String) {
        _uiState.value = AuthUiState.Loading
        if (!isValidEmail(email)) {
            _uiState.value = AuthUiState.Error("Por favor ingresa un email válido")
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _uiState.value = AuthUiState.Success(auth.currentUser)
                } else {
                    _uiState.value = AuthUiState.Error(task.exception?.message ?: "Error desconocido")
                }
            }
    }
    private fun isValidEmail(email: String): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        return email.matches(emailPattern.toRegex())
    }

    fun register(email: String, password: String, name: String) {
        _uiState.value = AuthUiState.Loading
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Actualizar el perfil con el nombre
                    val user = auth.currentUser
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(name)
                        .build()

                    user?.updateProfile(profileUpdates)
                        ?.addOnCompleteListener { updateTask ->
                            if (updateTask.isSuccessful) {
                                _uiState.value = AuthUiState.Success(user)
                            } else {
                                _uiState.value = AuthUiState.Error(updateTask.exception?.message ?: "Error al actualizar perfil")
                            }
                        }
                } else {
                    _uiState.value = AuthUiState.Error(task.exception?.message ?: "Error desconocido")
                }
            }
    }

    fun logout() {
        auth.signOut()
        _uiState.value = AuthUiState.Initial
    }

    fun resetError() {
        _uiState.value = AuthUiState.Initial
    }
}

sealed class AuthUiState {
    object Initial : AuthUiState()
    object Loading : AuthUiState()
    data class Success(val user: FirebaseUser?) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}