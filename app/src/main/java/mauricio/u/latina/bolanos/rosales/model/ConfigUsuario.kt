package mauricio.u.latina.bolanos.rosales.model

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class ConfigUsuario(
    var notificaciones: Boolean = true,
    var visualizarPerfil: Boolean = true,  // Usa camelCase por convención en Kotlin
    var sonidoNotificaciones: Boolean = true,

    @Exclude @get:Exclude
    var id: String = ""
) {
    // Constructor sin parámetros para Firebase
    constructor() : this(true, true, true, "")
}