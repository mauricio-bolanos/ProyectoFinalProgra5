package mauricio.u.latina.bolanos.rosales.userinterfaz.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import mauricio.u.latina.bolanos.rosales.ui.screens.HomeScreen
import mauricio.u.latina.bolanos.rosales.userinterfaz.login.LoginScreen
import mauricio.u.latina.bolanos.rosales.userinterfaz.login.RegisterScreen
import mauricio.u.latina.bolanos.rosales.viewmodel.AuthViewModel
import mauricio.u.latina.bolanos.rosales.viewmodel.ListaJugadoresViewModel
import mauricio.u.latina.bolanos.rosales.viewmodel.PartidasViewModel
import mauricio.u.latina.bolanos.rosales.viewmodel.TorneosViewModel

@Composable
fun AppNav(viewModelStoreOwner: ViewModelStoreOwner = LocalViewModelStoreOwner.current!!) {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = hiltViewModel()
    val currentUser = authViewModel.currentUser.value

    LaunchedEffect(currentUser) {
        if (currentUser != null && navController.currentDestination?.route?.startsWith("auth/") == true) {
            navController.navigate("home") {
                popUpTo("auth") { inclusive = true }
            }
        } else if (currentUser == null && navController.currentDestination?.route?.startsWith("auth/") != true) {
            navController.navigate("auth") {
                popUpTo(navController.graph.startDestinationId) { inclusive = true }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = if (currentUser != null) "home" else "auth"
    ) {
        authNavigation(navController, viewModelStoreOwner, authViewModel)
        mainNavigation(navController, viewModelStoreOwner)
    }
}

private fun NavGraphBuilder.authNavigation(
    navController: NavController,
    viewModelStoreOwner: ViewModelStoreOwner,
    authViewModel: AuthViewModel
) {
    navigation(startDestination = "login", route = "auth") {
        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("home") {
                        popUpTo("auth") { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate("register")
                },
                viewModel = authViewModel
            )
        }

        composable("register") {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate("home") {
                        popUpTo("auth") { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                viewModel = authViewModel
            )
        }
    }
}

private fun NavGraphBuilder.mainNavigation(
    navController: NavController,
    viewModelStoreOwner: ViewModelStoreOwner
) {
    composable("home") {

        val authViewModel: AuthViewModel = hiltViewModel(viewModelStoreOwner)
        val partidasViewModel: PartidasViewModel = hiltViewModel()
        val torneosViewModel: TorneosViewModel = hiltViewModel()
        val listaJugadoresViewModel: ListaJugadoresViewModel = hiltViewModel()


        HomeScreen(
            navController = navController,
            partidasViewModel = partidasViewModel,
            torneosViewModel = torneosViewModel,
            listaJugadoresViewModel = listaJugadoresViewModel,
            onlogout = {
                authViewModel.logout()
                navController.navigate("auth") {
                    popUpTo("home") { inclusive = true }
                }
            }
        )
    }
}