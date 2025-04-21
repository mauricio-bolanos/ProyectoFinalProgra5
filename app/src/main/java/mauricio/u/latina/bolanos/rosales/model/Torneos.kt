package mauricio.u.latina.bolanos.rosales.model

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import java.text.SimpleDateFormat
import java.util.*

@IgnoreExtraProperties
data class Torneos(
    @Exclude @get:Exclude var id: String = "",  // Firebase usa String como ID
    val nombre: String = "",
    val descripcion: String = "",
    val fechaDeInicio: String = "",
    val fechaDeFinalizacion: String = "",
    val estado: String = "activo" // Ejemplo de campo adicional
) {
    // Constructor sin parámetros requerido por Firebase
    constructor() : this("", "", "", "", "", "activo")

    companion object {
        private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

        fun crear(
            nombre: String,
            descripcion: String,
            fechaInicio: Date,
            fechaFin: Date
        ): Torneos {
            return Torneos(
                nombre = nombre,
                descripcion = descripcion,
                fechaDeInicio = dateFormat.format(fechaInicio),
                fechaDeFinalizacion = dateFormat.format(fechaFin)
            )
        }
    }

    fun toMap(): Map<String, Any?> {
        return mapOf(
            "nombre" to nombre,
            "descripcion" to descripcion,
            "fechaDeInicio" to fechaDeInicio,
            "fechaDeFinalizacion" to fechaDeFinalizacion,
            "estado" to estado
        )
    }
}