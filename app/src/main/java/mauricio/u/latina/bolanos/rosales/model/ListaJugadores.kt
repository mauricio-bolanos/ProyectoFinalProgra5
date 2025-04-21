package mauricio.u.latina.bolanos.rosales.model

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class ListaJugadores(
    @Exclude @get:Exclude var idLista: String = "",  // Firebase usa String como ID
    val idPartida: String = "",
    val idTorneo: String = "",
    val nombreJugador: String = "",
    val posicion: Int = 0,  // Para ordenar jugadores
    val esTitular: Boolean = true
) {
    // Constructor sin parámetros requerido por Firebase
    constructor() : this("", "", "", "", 0, true)

    companion object {
        fun crear(
            idPartida: String,
            idTorneo: String,
            nombreJugador: String,
            posicion: Int = 0,
            esTitular: Boolean = true
        ): ListaJugadores {
            return ListaJugadores(
                idPartida = idPartida,
                idTorneo = idTorneo,
                nombreJugador = nombreJugador,
                posicion = posicion,
                esTitular = esTitular
            )
        }
    }

    fun toMap(): Map<String, Any?> {
        return mapOf(
            "idPartida" to idPartida,
            "idTorneo" to idTorneo,
            "nombreJugador" to nombreJugador,
            "posicion" to posicion,
            "esTitular" to esTitular
        )
    }
}