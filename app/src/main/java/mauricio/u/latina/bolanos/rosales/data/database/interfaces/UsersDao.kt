package mauricio.u.latina.bolanos.rosales.data.database.interfaces


import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import mauricio.u.latina.bolanos.rosales.data.database.ArcanumDatabase
import mauricio.u.latina.bolanos.rosales.model.Users
import javax.inject.Inject

class UsersDao @Inject constructor(
    private val arcanumDatabase: ArcanumDatabase
) {
    private val usersReference = arcanumDatabase.usersReference

    // Añadir un nuevo usuario (suspend function)
    suspend fun addUser(user: Users): String {
        return try {
            val newUserRef = usersReference.push()
            val userId = newUserRef.key ?: throw Exception("No se pudo generar ID de usuario")
            val userWithId = user.copy(id = userId)
            newUserRef.setValue(userWithId).await()
            userId
        } catch (e: Exception) {
            throw Exception("Error al añadir usuario: ${e.message}")
        }
    }

    // Obtener usuario por ID (suspend function)
    suspend fun getUserById(userId: String): Users? {
        return try {
            val snapshot = arcanumDatabase.getUserReferenceById(userId).get().await()
            snapshot.getValue(Users::class.java)
        } catch (e: Exception) {
            throw Exception("Error al obtener usuario: ${e.message}")
        }
    }

    // Actualizar usuario (suspend function)
    suspend fun updateUser(user: Users): Boolean {
        return try {
            arcanumDatabase.getUserReferenceById(user.id).setValue(user).await()
            true
        } catch (e: Exception) {
            throw Exception("Error al actualizar usuario: ${e.message}")
        }
    }

    // Eliminar usuario (suspend function)
    suspend fun deleteUser(userId: String): Boolean {
        return try {
            arcanumDatabase.getUserReferenceById(userId).removeValue().await()
            true
        } catch (e: Exception) {
            throw Exception("Error al eliminar usuario: ${e.message}")
        }
    }

    // Obtener todos los usuarios (Flow)
    fun getAllUsersAsFlow(): Flow<List<Users>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val users = snapshot.children.mapNotNull { it.getValue(Users::class.java) }
                trySend(users)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        usersReference.addValueEventListener(listener)
        awaitClose { usersReference.removeEventListener(listener) }
    }

    // Obtener usuarios por rol (Flow)
    fun getUsersByRoleFlow(role: String): Flow<List<Users>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val users = snapshot.children.mapNotNull { it.getValue(Users::class.java) }
                    .filter { it.rol == role }
                trySend(users)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        usersReference.addValueEventListener(listener)
        awaitClose { usersReference.removeEventListener(listener) }
    }

    // Funcion tradicional con callback
    fun getAllUsers(callback: (List<Users>) -> Unit) {
        usersReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val users = snapshot.children.mapNotNull { it.getValue(Users::class.java) }
                callback(users)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(emptyList())
            }
        })
    }
}