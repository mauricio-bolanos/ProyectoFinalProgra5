package mauricio.u.latina.bolanos.rosales.model

import android.icu.text.SimpleDateFormat
import java.util.*

data class Users(
    var id: String = "", // Firebase usa IDs como String
    val name: String = "",
    val correo: String = "",
    val contrasenha: String = "", // Considerar encriptar esta información
    val estado_actividad: Boolean = false,
    val fecha_de_registro: String = "", // Usamos String para fecha en Firebase
    val rol: String = ""
) {
    // Constructor sin parámetros requerido por Firebase
    constructor() : this("", "", "", "", false, "", "")

    companion object {
        private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

        fun crear(
            name: String,
            correo: String,
            contrasenha: String,
            rol: String = "usuario"
        ): Users {
            return Users(
                name = name,
                correo = correo,
                contrasenha = contrasenha,
                estado_actividad = true,
                fecha_de_registro = dateFormat.format(Date()),
                rol = rol
            )
        }
    }

    fun toMap(): Map<String, Any?> {
        return mapOf(
            "name" to name,
            "correo" to correo,
            "estado_actividad" to estado_actividad,
            "fecha_de_registro" to fecha_de_registro,
            "rol" to rol
        )
    }
}