package mauricio.u.latina.bolanos.rosales.data.repository

import android.icu.text.SimpleDateFormat
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import mauricio.u.latina.bolanos.rosales.model.Users
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val database: FirebaseDatabase
) {
    private val auth: FirebaseAuth = Firebase.auth
    private val usuariosRef = database.getReference("usuarios")

    suspend fun loginWithEmail(correo: String, contrasenha: String): Result<Users> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(correo, contrasenha).await()
            val firebaseUser = authResult.user ?: return Result.failure(Exception("Usuario no encontrado"))

            // Obtener datos adicionales de Firebase Database
            val usuario = obtenerUsuarioDesdeDB(firebaseUser.uid)

            Result.success(usuario ?: throw Exception("Datos de usuario incompletos"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun registerWithEmail(usuario: Users): Result<Users> {
        return try {
            // 1. Crear usuario en Firebase Auth
            val authResult = auth.createUserWithEmailAndPassword(usuario.correo, usuario.contrasenha).await()
            val firebaseUser = authResult.user ?: return Result.failure(Exception("Error al crear usuario"))

            // 2. Guardar datos adicionales en Realtime Database
            val nuevoUsuario = usuario.copy(
                id = firebaseUser.uid,
                fecha_de_registro = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
            )

            usuariosRef.child(firebaseUser.uid).setValue(nuevoUsuario.toMap()).await()

            Result.success(nuevoUsuario)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun obtenerUsuarioDesdeDB(userId: String): Users? {
        return try {
            val snapshot = usuariosRef.child(userId).get().await()
            snapshot.getValue(Users::class.java)?.copy(id = userId)
        } catch (e: Exception) {
            null
        }
    }

    fun getCurrentUser(): Users? {
        val user = auth.currentUser ?: return null
        return Users(
            id = user.uid,
            correo = user.email ?: "",
            name = user.displayName ?: "",
            estado_actividad = true,
            rol = "usuario" // Valor por defecto, se actualizará al cargar de la DB
        )
    }

    suspend fun loadCurrentUserDetails(): Users? {
        val user = auth.currentUser ?: return null
        return obtenerUsuarioDesdeDB(user.uid)
    }

    fun logout() {
        auth.signOut()
    }
}