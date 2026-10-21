package app.opencut

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import app.opencut.ui.screen.EditorScreen
import app.opencut.ui.theme.OpenCutTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OpenCutTheme {
                EditorScreen(
                    onCloseApp = { finish() }
                )
            }
        }
    }
}
