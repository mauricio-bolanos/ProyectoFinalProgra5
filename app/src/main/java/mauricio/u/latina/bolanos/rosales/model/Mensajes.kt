package mauricio.u.latina.bolanos.rosales.model

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import java.text.SimpleDateFormat
import java.util.*

@IgnoreExtraProperties
data class Mensajes(
    @Exclude @get:Exclude var id: String = "",
    val remitente: String = "",
    val contenido: String = "",
    val fechaHora: String = ""
) {
    // Constructor sin parámetros requerido por Firebase
    constructor() : this("", "", "", "")

    companion object {
        private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

        // Factory method para crear nuevo mensaje con fecha actual
        fun crear(
            remitente: String,
            contenido: String,
            fechaHora: Date = Calendar.getInstance().time
        ): Mensajes {
            return Mensajes(
                remitente = remitente,
                contenido = contenido,
                fechaHora = dateFormat.format(fechaHora)
            )
        }

        // Convertir mapa de Firebase a objeto Mensajes
        fun fromMap(map: Map<String, Any>, id: String): Mensajes {
            return Mensajes(
                id = id,
                remitente = map["remitente"] as? String ?: "",
                contenido = map["contenido"] as? String ?: "",
                fechaHora = map["fechaHora"] as? String ?: ""
            )
        }
    }

    // Convertir a mapa para Firebase (excluyendo el ID)
    fun toMap(): Map<String, Any> {
        return mapOf(
            "remitente" to remitente,
            "contenido" to contenido,
            "fechaHora" to fechaHora
        )
    }

    // Validación básica del mensaje
    fun isValid(): Boolean {
        return remitente.isNotBlank() && contenido.isNotBlank() && fechaHora.isNotBlank()
    }

    // Formatear fecha para mostrar
    fun fechaFormateada(): String {
        return try {
            val date = dateFormat.parse(fechaHora)
            SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(date)
        } catch (e: Exception) {
            fechaHora
        }
    }

    // Función para actualizar contenido (crea nueva instancia)
    fun actualizarContenido(nuevoContenido: String): Mensajes {
        return this.copy(contenido = nuevoContenido)
    }
}