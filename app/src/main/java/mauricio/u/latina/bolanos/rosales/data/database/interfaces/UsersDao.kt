package mauricio.u.latina.bolanos.rosales.data.database.interfaces


import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import mauricio.u.latina.bolanos.rosales.model.Users
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class UsersDao {
    private val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("users")

    fun addUser(user: Users, callback: (Boolean, String?) -> Unit) {
        val userId = databaseReference.push().key ?: ""
        user.id = userId

        databaseReference.child(userId).setValue(user)
            .addOnSuccessListener {
                callback(true, userId)
            }
            .addOnFailureListener { e ->
                callback(false, e.message)
            }
    }

    fun getUserById(userId: String, callback: (Users?) -> Unit) {
        databaseReference.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(Users::class.java)
                callback(user)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(null)
            }
        })
    }

    fun updateUser(user: Users, callback: (Boolean) -> Unit) {
        databaseReference.child(user.id).setValue(user)
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener {
                callback(false)
            }
    }

    fun deleteUser(userId: String, callback: (Boolean) -> Unit) {
        databaseReference.child(userId).removeValue()
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener {
                callback(false)
            }
    }

    fun getAllUsers(callback: (List<Users>) -> Unit) {
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val usersList = snapshot.children.mapNotNull { it.getValue(Users::class.java) }
                callback(usersList)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(emptyList())
            }
        })
    }

    suspend fun getAllUsersSuspend(): List<Users> = suspendCoroutine { continuation ->
        getAllUsers { users ->
            continuation.resume(users)
        }
    }
    fun getAllUsersAsFlow(): Flow<List<Users>> {
        val flow = MutableStateFlow<List<Users>>(emptyList())

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val usersList = mutableListOf<Users>()
                for (userSnapshot in snapshot.children) {
                    val user = userSnapshot.getValue(Users::class.java)
                    user?.let { usersList.add(it) }
                }
                flow.value = usersList
            }

            override fun onCancelled(error: DatabaseError) {
                return
            }
        }

        databaseReference.addValueEventListener(listener)

        return flow
    }
}