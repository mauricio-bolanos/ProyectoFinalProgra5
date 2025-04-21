package mauricio.u.latina.bolanos.rosales

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dagger.hilt.android.AndroidEntryPoint
import mauricio.u.latina.bolanos.rosales.ui.theme.ProyectoFinalTheme
import mauricio.u.latina.bolanos.rosales.userinterfaz.navigation.AppNav

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProyectoFinalTheme {
                AppNav()
            }
        }
    }
}