package mauricio.u.latina.bolanos.rosales.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import mauricio.u.latina.bolanos.rosales.model.Partidas
import mauricio.u.latina.bolanos.rosales.viewmodel.PartidasViewModel

@Composable
fun AddPartidaScreen(
    navController: NavController,
    partidasViewModel: PartidasViewModel = hiltViewModel()
) {
    var fechaYHora by remember { mutableStateOf("") }
    var jugadores by remember { mutableStateOf("") }
    var sustitutos by remember { mutableStateOf("") }
    var anfitrion by remember { mutableStateOf("") }
    var rival by remember { mutableStateOf("") }
    var estado by remember { mutableStateOf("") }

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
                if (fechaYHora.isNotBlank() && jugadores.isNotBlank()) {
                    val nuevaPartida = Partidas(
                        fechaYHoraPartida = fechaYHora,
                        jugadores = jugadores,
                        sustitutos = sustitutos,
                        anfitrion = anfitrion,
                        rival = rival,
                        estado = estado
                    )
                    partidasViewModel.crearPartida(nuevaPartida)
                    navController.popBackStack()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Agregar Partida")
        }
    }
}
