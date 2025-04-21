package mauricio.u.latina.bolanos.rosales.model

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import java.text.SimpleDateFormat
import java.util.*

@IgnoreExtraProperties
data class Partidas(
    @Exclude @get:Exclude var id: String = "",  // Firebase usa String como ID
    val fechaYHoraPartida: String = "",
    val jugadores: String = "",  // Considerar usar List<String> si son múltiples jugadores
    val sustitutos: String = "",
    val anfitrion: String = "",
    val rival: String = "",
    val estado: String = "pendiente" // Ej: pendiente, en_progreso, finalizada
) {
    // Constructor sin parámetros requerido por Firebase
    constructor() : this("", "", "", "", "", "", "pendiente")

    companion object {
        private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

        fun crear(
            jugadores: String,
            sustitutos: String,
            anfitrion: String,
            rival: String,
            fechaHora: Date = Date()
        ): Partidas {
            return Partidas(
                fechaYHoraPartida = dateFormat.format(fechaHora),
                jugadores = jugadores,
                sustitutos = sustitutos,
                anfitrion = anfitrion,
                rival = rival
            )
        }
    }

    fun toMap(): Map<String, Any?> {
        return mapOf(
            "fechaYHoraPartida" to fechaYHoraPartida,
            "jugadores" to jugadores,
            "sustitutos" to sustitutos,
            "anfitrion" to anfitrion,
            "rival" to rival,
            "estado" to estado
        )
    }

    fun getFechaHoraDate(): Date {
        return try {
            dateFormat.parse(fechaYHoraPartida) ?: Date()
        } catch (e: Exception) {
            Date()
        }
    }
}