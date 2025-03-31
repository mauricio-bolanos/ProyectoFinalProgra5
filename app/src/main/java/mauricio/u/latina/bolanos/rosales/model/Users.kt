package mauricio.u.latina.bolanos.rosales.model

import java.util.*

data class Users(
    var id: String = "", // Firebase usa IDs como String
    val name: String = "",
    val correo: String = "",
    val contrasenha: String = "", // Considera encriptar esta información
    val estado_actividad: Boolean = false,
    val fecha_de_registro: String = "", // Usamos String para fecha en Firebase
    val rol: String = ""
) {
    // Constructor sin parámetros requerido por Firebase
    constructor() : this("", "", "", "", false, "", "")
}