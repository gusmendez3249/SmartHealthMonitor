package mx.utng.smarthealthmonitor.wear.presentation
 
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import mx.utng.smarthealthmonitor.wear.presentation.theme.SmartHealthWearTheme

class WearMainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SmartHealthWearTheme {
                // TODO Ej.02: reemplazar con WearNavGraph
                WearDashboardScreen()
            }
        }
    }
}

@Composable
fun WearDashboardScreen() {
    // Placeholder to make it compile, to be replaced in Ej.02
}
