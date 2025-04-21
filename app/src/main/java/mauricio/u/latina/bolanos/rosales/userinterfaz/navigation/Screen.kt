package mauricio.u.latina.bolanos.rosales.userinterfaz.navigation

sealed class Screen(val route: String) {
    object Login : Screen("auth")
    object Register : Screen("register")
    object Home : Screen("home")
    object AddPartida : Screen("addPartida")
}