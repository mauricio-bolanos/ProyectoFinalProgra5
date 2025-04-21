package mauricio.u.latina.bolanos.rosales.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import mauricio.u.latina.bolanos.rosales.viewmodel.ListaJugadoresViewModel
import mauricio.u.latina.bolanos.rosales.viewmodel.PartidasViewModel
import mauricio.u.latina.bolanos.rosales.viewmodel.TorneosViewModel

@Composable
fun HomeScreen(
    navController: NavController,
    partidasViewModel: PartidasViewModel = hiltViewModel(),
    torneosViewModel: TorneosViewModel = hiltViewModel(),
    listaJugadoresViewModel: ListaJugadoresViewModel = hiltViewModel(),
    onlogout: () -> Unit,
) {
    val partidasState = partidasViewModel.partidasState.collectAsState()
    val partidas = partidasState.value
    val torneosState = torneosViewModel.torneosState.collectAsState()
    val torneos = torneosState.value
    val jugadoresState = listaJugadoresViewModel.jugadoresState.collectAsState()
    val jugadores = jugadoresState.value

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            item {
                Text("📅 Partidas", style = MaterialTheme.typography.headlineMedium, modifier = Modifier.padding(bottom = 8.dp))
            }

            items(partidas) { partida ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable {
                            navController.navigate("editPartida/${partida.id}")
                        },
                    elevation = CardDefaults.cardElevation()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Fecha y Hora: ${partida.fechaYHoraPartida}", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Jugadores:")
                        jugadores.filter { it.idPartida == partida.id }.forEach {
                            Text("- ${it.nombreJugador}")
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text("🏆 Torneos", style = MaterialTheme.typography.headlineMedium, modifier = Modifier.padding(vertical = 8.dp))
            }

            items(torneos) { torneo ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    elevation = CardDefaults.cardElevation()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Fecha y Hora: ${torneo.fechaDeInicio}", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Jugadores:")
                        jugadores.filter { it.idTorneo == torneo.id }.forEach {
                            Text("- ${it.nombreJugador}")
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = { onlogout() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Text("Cerrar sesión")
                }
            }
        }

        // FAB para agregar nueva partida
        FloatingActionButton(
            onClick = { navController.navigate("addPartida") },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Text("+")
        }
    }
}
