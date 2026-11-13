package dev.kurokawa.localsend.expressive.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.kurokawa.localsend.expressive.app.AppSettings
import dev.kurokawa.localsend.expressive.resources.Res
import dev.kurokawa.localsend.expressive.resources.google_sans_flex
import org.jetbrains.compose.resources.Font

// Seed schemes: a calm teal close to LocalSend's identity. Used when the
// user picks "LocalSend colors", and on desktop (no wallpaper to sample).
internal val LightScheme = lightColorScheme(
    primary = Color(0xFF00696D),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFF6FF7FB),
    secondary = Color(0xFF4A6364),
    tertiary = Color(0xFF4C5F7D),
    surface = Color(0xFFF4FBFA),
)

internal val DarkScheme = darkColorScheme(
    primary = Color(0xFF4CDADD),
    onPrimary = Color(0xFF003738),
    primaryContainer = Color(0xFF004F52),
    secondary = Color(0xFFB1CBCC),
    tertiary = Color(0xFFB2C6E8),
    surface = Color(0xFF0E1515),
)


// Monochrome schemes (v0.9.0): true neutrals with scheme-guaranteed
// contrast — Ernest runs a monochrome setup, and this is also the
// "Monochrome" color style in Settings. Every container/on pair is
// hand-checked to stay legible; nothing here can produce the
// black-on-black headings of the dynamic-monochrome case.
internal val MonoLightScheme = lightColorScheme(
    primary = Color(0xFF5A5A5A),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFE2E2E2),
    onPrimaryContainer = Color(0xFF1A1A1A),
    secondary = Color(0xFF5F5F5F),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFE8E8E8),
    onSecondaryContainer = Color(0xFF1C1C1C),
    tertiary = Color(0xFF6A6A6A),
    onTertiary = Color(0xFFFFFFFF),
    background = Color(0xFFFAFAFA),
    onBackground = Color(0xFF1A1A1A),
    surface = Color(0xFFFAFAFA),
    onSurface = Color(0xFF1A1A1A),
    surfaceVariant = Color(0xFFE8E8E8),
    onSurfaceVariant = Color(0xFF494949),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFF5F5F5),
    surfaceContainer = Color(0xFFF0F0F0),
    surfaceContainerHigh = Color(0xFFEAEAEA),
    surfaceContainerHighest = Color(0xFFE2E2E2),
    outline = Color(0xFF7A7A7A),
    outlineVariant = Color(0xFFC9C9C9),
    error = Color(0xFFBA1A1A),
    onError = Color(0xFFFFFFFF),
)

internal val MonoDarkScheme = darkColorScheme(
    primary = Color(0xFFC6C6C6),
    onPrimary = Color(0xFF1B1B1B),
    primaryContainer = Color(0xFF474747),
    onPrimaryContainer = Color(0xFFE2E2E2),
    secondary = Color(0xFFC6C6C6),
    onSecondary = Color(0xFF1B1B1B),
    secondaryContainer = Color(0xFF3F3F3F),
    onSecondaryContainer = Color(0xFFE5E5E5),
    tertiary = Color(0xFFBDBDBD),
    onTertiary = Color(0xFF1B1B1B),
    background = Color(0xFF131313),
    onBackground = Color(0xFFE6E6E6),
    surface = Color(0xFF131313),
    onSurface = Color(0xFFE6E6E6),
    surfaceVariant = Color(0xFF2E2E2E),
    onSurfaceVariant = Color(0xFFC9C9C9),
    surfaceContainerLowest = Color(0xFF0D0D0D),
    surfaceContainerLow = Color(0xFF1B1B1B),
    surfaceContainer = Color(0xFF1F1F1F),
    surfaceContainerHigh = Color(0xFF292929),
    surfaceContainerHighest = Color(0xFF333333),
    outline = Color(0xFF919191),
    outlineVariant = Color(0xFF474747),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
)

// Material 3 Expressive shape scale: bigger and softer than the classic
// defaults. (The JB alpha05 Shapes constructor only takes the five classic
// tiers; expressive components derive their emphasized shapes from these.)
private val ExpressiveShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(16.dp),
    medium = RoundedCornerShape(20.dp),
    large = RoundedCornerShape(28.dp),
    extraLarge = RoundedCornerShape(36.dp),
)

/** Wallpaper-derived scheme on Android; null where the platform has none. */
@Composable
expect fun platformDynamicColorScheme(dark: Boolean): ColorScheme?

/**
 * Google Sans Flex (variable font, SIL OFL) with its axes driven per role:
 * display type is wider and fully rounded; text stays narrower with a hint
 * of rounding. Same recipe the Minus app uses for its expressive look.
 */
@Composable
private fun sansFlexFamily(width: Float, roundness: Float): FontFamily {
    fun settings(weight: Int) = FontVariation.Settings(
        FontVariation.weight(weight),
        FontVariation.width(width),
        FontVariation.Setting("ROND", roundness),
    )
    val normal = Font(Res.font.google_sans_flex, FontWeight.Normal, variationSettings = settings(400))
    val medium = Font(Res.font.google_sans_flex, FontWeight.Medium, variationSettings = settings(500))
    val semiBold = Font(Res.font.google_sans_flex, FontWeight.SemiBold, variationSettings = settings(620))
    val bold = Font(Res.font.google_sans_flex, FontWeight.Bold, variationSettings = settings(720))
    val extraBold = Font(Res.font.google_sans_flex, FontWeight.ExtraBold, variationSettings = settings(800))
    // FontFamily identity MUST be stable: rebuilding it on every
    // recomposition re-measures every text in the app — that was the
    // Settings-page stutter Ernest reported.
    return remember(normal, medium, semiBold, bold, extraBold) {
        FontFamily(normal, medium, semiBold, bold, extraBold)
    }
}

@Composable
private fun expressiveTypography(): Typography {
    val display = sansFlexFamily(width = 118f, roundness = 100f)
    val text = sansFlexFamily(width = 100f, roundness = 35f)
    val base = Typography()
    return remember(display, text) {
    Typography(
        displayLarge = base.displayLarge.copy(fontFamily = display, fontWeight = FontWeight.Bold),
        displayMedium = base.displayMedium.copy(fontFamily = display, fontWeight = FontWeight.Bold),
        displaySmall = base.displaySmall.copy(fontFamily = display, fontWeight = FontWeight.SemiBold),
        headlineLarge = base.headlineLarge.copy(fontFamily = display, fontWeight = FontWeight.SemiBold),
        headlineMedium = base.headlineMedium.copy(fontFamily = display, fontWeight = FontWeight.SemiBold),
        headlineSmall = base.headlineSmall.copy(fontFamily = display, fontWeight = FontWeight.SemiBold),
        titleLarge = base.titleLarge.copy(fontFamily = display, fontWeight = FontWeight.SemiBold),
        titleMedium = base.titleMedium.copy(fontFamily = text, fontWeight = FontWeight.Medium),
        titleSmall = base.titleSmall.copy(fontFamily = text, fontWeight = FontWeight.Medium),
        bodyLarge = base.bodyLarge.copy(fontFamily = text),
        bodyMedium = base.bodyMedium.copy(fontFamily = text),
        bodySmall = base.bodySmall.copy(fontFamily = text),
        labelLarge = base.labelLarge.copy(fontFamily = text, fontWeight = FontWeight.Medium),
        labelMedium = base.labelMedium.copy(fontFamily = text, fontWeight = FontWeight.Medium),
        labelSmall = base.labelSmall.copy(fontFamily = text, fontWeight = FontWeight.Medium),
    )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun LocalSendExpressiveTheme(
    settings: AppSettings,
    content: @Composable () -> Unit,
) {
    val dark = when (settings.themeMode) {
        "light" -> false
        "dark" -> true
        else -> isSystemInDarkTheme()
    }
    // Minus's model (copied with its themes): a chosen color scheme
    // wins outright; dynamic color (Material You) applies only to the
    // brand scheme. "dynamic" is the pre-v0.9.0 colorMode value and
    // still means brand + Material You.
    val minusScheme = minusColorScheme(settings.colorMode, dark)
    val materialYou = settings.dynamicColor || settings.colorMode == "dynamic"
    val dynamic = if (minusScheme == null &&
        settings.colorMode != "mono" && materialYou
    ) {
        platformDynamicColorScheme(dark)
    } else {
        null
    }
    val typography = expressiveTypography()
    val motionScheme = remember { MotionScheme.expressive() }
    val scheme = when {
        minusScheme != null -> minusScheme
        settings.colorMode == "mono" -> if (dark) MonoDarkScheme else MonoLightScheme
        dynamic != null -> dynamic
        dark -> DarkScheme
        else -> LightScheme
    }
    MaterialExpressiveTheme(
        colorScheme = scheme,
        motionScheme = motionScheme,
        shapes = ExpressiveShapes,
        typography = typography,
        content = content,
    )
}
