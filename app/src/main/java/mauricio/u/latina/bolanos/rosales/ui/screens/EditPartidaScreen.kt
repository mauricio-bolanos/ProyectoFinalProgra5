package mauricio.u.latina.bolanos.rosales.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import mauricio.u.latina.bolanos.rosales.model.Partidas
import mauricio.u.latina.bolanos.rosales.viewmodel.PartidasViewModel

@Composable
fun EditPartidaScreen(
    partida: Partidas,
    partidasViewModel: PartidasViewModel = hiltViewModel(),
    navController: NavController
) {
    var fechaYHora by remember { mutableStateOf(partida.fechaYHoraPartida) }
    var jugadores by remember { mutableStateOf(partida.jugadores) }
    var sustitutos by remember { mutableStateOf(partida.sustitutos) }
    var anfitrion by remember { mutableStateOf(partida.anfitrion) }
    var rival by remember { mutableStateOf(partida.rival) }
    var estado by remember { mutableStateOf(partida.estado) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = fechaYHora,
            onValueChange = { fechaYHora = it },
            label = { Text("Fecha y Hora") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = jugadores,
            onValueChange = { jugadores = it },
            label = { Text("Jugadores") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = sustitutos,
            onValueChange = { sustitutos = it },
            label = { Text("Sustitutos") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = anfitrion,
            onValueChange = { anfitrion = it },
            label = { Text("Anfitrión") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = rival,
            onValueChange = { rival = it },
            label = { Text("Rival") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = estado,
            onValueChange = { estado = it },
            label = { Text("Estado") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val partidaActualizada = partida.copy(
                    fechaYHoraPartida = fechaYHora,
                    jugadores = jugadores,
                    sustitutos = sustitutos,
                    anfitrion = anfitrion,
                    rival = rival,
                    estado = estado
                )
                partidasViewModel.crearPartida(partidaActualizada) // asumiendo que `crearPartida` sobreescribe si el ID ya existe
                navController.popBackStack()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar cambios")
        }
    }
}
