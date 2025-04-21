package mauricio.u.latina.bolanos.rosales.data.database

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class ArcanumDatabase private constructor() {
    // Referencia a la base de datos de Firebase
    private val database: FirebaseDatabase = Firebase.database

    // Referencias a las diferentes tablas/colecciones
    val usersReference = database.getReference("users")
    val canalesReference = database.getReference("canales")
    val partidasReference = database.getReference("partidas")
    val torneosReference = database.getReference("torneos")
    val listaJugadoresReference = database.getReference("lista_jugadores")
    val mensajesReference = database.getReference("mensajes")
    val configUsuarioReference = database.getReference("config_usuario")

    // Funciones para obtener referencias específicas
    fun getCanalReferenceById(canalId: String) = canalesReference.child(canalId)
    fun getUserReferenceById(userId: String) = usersReference.child(userId)
    fun getPartidaReferenceById(partidaId: String) = partidasReference.child(partidaId)
    fun getTorneoReferenceById(torneoId: String) = torneosReference.child(torneoId)
    fun getListaJugadoresReferenceById(listaId: String) = listaJugadoresReference.child(listaId)
    fun getMensajeReferenceById(mensajeId: String) = mensajesReference.child(mensajeId)
    fun getConfigUsuarioReferenceById(configId: String) = configUsuarioReference.child(configId)

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



    // Configuración inicial de la base de datos
    fun configureDatabase() {
        database.setPersistenceEnabled(true) // Habilita persistencia offline
    }
}