package mauricio.u.latina.bolanos.rosales.model

import com.google.firebase.database.Exclude

data class Canales(
    @Exclude @get:Exclude var id: String = "", // Firebase usa IDs como String
    val nombre: String = "",
    val tipo_de_canal: String = "",
    val miembros_permiso: Boolean = false
) {
    // Constructor sin parámetros requerido por Firebase
    constructor() : this("", "", "", false)

    // Función para convertir a mapa (útil para Firebase)
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "nombre" to nombre,
            "tipo_de_canal" to tipo_de_canal,
            "miembros_permiso" to miembros_permiso
        )
    }
}