package mauricio.u.latina.bolanos.rosales.data.repository

import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await
import mauricio.u.latina.bolanos.rosales.model.ConfigUsuario

class ConfigRepository {
    private val database = FirebaseDatabase.getInstance()
    private val configRef = database.getReference("configuraciones")

    // Obtener configuración
    suspend fun obtenerConfig(userId: String): ConfigUsuario {
        val snapshot = configRef.child(userId).get().await()
        return snapshot.getValue(ConfigUsuario::class.java) ?: ConfigUsuario().apply { id = userId }
    }

    // Guardar configuración
    suspend fun guardarConfig(config: ConfigUsuario) {
        configRef.child(config.id).setValue(config).await()
    }
}