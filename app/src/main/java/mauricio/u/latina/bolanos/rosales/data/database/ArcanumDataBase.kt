package mauricio.u.latina.bolanos.rosales.data.database

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import mauricio.u.latina.bolanos.rosales.model.Users

class ArcanumDatabase private constructor() {
    // Referencia a la base de datos de Firebase
    private val database: FirebaseDatabase = Firebase.database

    // Referencias a las diferentes tablas/colecciones
    val usersReference = database.getReference("users")

    companion object {
        @Volatile private var INSTANCE: ArcanumDatabase? = null

        fun getInstance(): ArcanumDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = ArcanumDatabase()
                INSTANCE = instance
                instance
            }
        }
    }

    // Métodos específicos para operaciones con Users
    fun getUserReferenceById(userId: String) = usersReference.child(userId)

    // Configuración inicial de la base de datos
    fun configureDatabase() {
        database.setPersistenceEnabled(true) // Habilita persistencia offline
    }
}