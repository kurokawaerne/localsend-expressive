package dev.kurokawa.localsend.expressive.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// Seed-inspired schemes (a calm teal seed, close to LocalSend's identity).
private val LightScheme = lightColorScheme(
    primary = Color(0xFF00696D),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFF6FF7FB),
    secondary = Color(0xFF4A6364),
    tertiary = Color(0xFF4C5F7D),
    surface = Color(0xFFF4FBFA),
)

private val DarkScheme = darkColorScheme(
    primary = Color(0xFF4CDADD),
    onPrimary = Color(0xFF003738),
    primaryContainer = Color(0xFF004F52),
    secondary = Color(0xFFB1CBCC),
    tertiary = Color(0xFFB2C6E8),
    surface = Color(0xFF0E1515),
)

// Expressive leans on larger, softer corners than classic Material 3 defaults.
// TODO(M4): switch to MaterialExpressiveTheme with spring MotionScheme once the
// experimental API is verified identical on Android and desktop material3.
private val ExpressiveShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(20.dp),
    large = RoundedCornerShape(28.dp),
    extraLarge = RoundedCornerShape(36.dp),
)

@Composable
fun LocalSendExpressiveTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkScheme else LightScheme,
        shapes = ExpressiveShapes,
        content = content,
    )
}
