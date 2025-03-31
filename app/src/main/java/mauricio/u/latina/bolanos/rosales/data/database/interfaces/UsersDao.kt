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
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class UsersDao @Inject constructor(
    private val arcanumDatabase: ArcanumDatabase
) {
    private val usersReference = arcanumDatabase.usersReference

    // Añadir un nuevo usuario (suspend function)
    suspend fun addUser(user: Users): String {
        val newRef = usersReference.push()
        val userId = newRef.key ?: throw Exception("No se pudo generar ID")
        newRef.setValue(user.copy(id = userId)).await()
        return userId
    }

    // Obtener usuario por ID (suspend function)
    suspend fun getUserById(userId: String): Users? {
        return suspendCoroutine { continuation ->
            usersReference.child(userId).get()
                .addOnSuccessListener { snapshot ->
                    continuation.resume(snapshot.getValue(Users::class.java))
                }
                .addOnFailureListener { e ->
                    continuation.resume(null)
                }
        }
    }

    suspend fun updateUser(user: Users): Boolean {
        return suspendCoroutine { continuation ->
            usersReference.child(user.id).setValue(user)
                .addOnSuccessListener {
                    continuation.resume(true)
                }
                .addOnFailureListener {
                    continuation.resume(false)
                }
        }
    }

    suspend fun deleteUser(userId: String): Boolean {
        return suspendCoroutine { continuation ->
            usersReference.child(userId).removeValue()
                .addOnSuccessListener {
                    continuation.resume(true)
                }
                .addOnFailureListener {
                    continuation.resume(false)
                }
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