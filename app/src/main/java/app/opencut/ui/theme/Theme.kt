package app.opencut.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = CapCutCyan,
    onPrimary = PureBlack,
    primaryContainer = CapCutTeal,
    secondary = CapCutBlue,
    onSecondary = TextPrimary,
    background = PureBlack,
    onBackground = TextPrimary,
    surface = DarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkElevated,
    onSurfaceVariant = TextSecondary,
    outline = BorderZinc,
    error = CapCutRed
)

@Composable
fun OpenCutTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
