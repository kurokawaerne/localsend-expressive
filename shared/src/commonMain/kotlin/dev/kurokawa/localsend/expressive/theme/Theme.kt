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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.kurokawa.localsend.expressive.app.AppSettings
import dev.kurokawa.localsend.expressive.resources.Res
import dev.kurokawa.localsend.expressive.resources.gsf_display_bold
import dev.kurokawa.localsend.expressive.resources.gsf_display_extrabold
import dev.kurokawa.localsend.expressive.resources.gsf_display_medium
import dev.kurokawa.localsend.expressive.resources.gsf_display_semibold
import dev.kurokawa.localsend.expressive.resources.gsf_wordmark_semibold
import dev.kurokawa.localsend.expressive.resources.gsf_wordmark_semibold_italic
import dev.kurokawa.localsend.expressive.resources.gsf_text_bold
import dev.kurokawa.localsend.expressive.resources.gsf_text_medium
import dev.kurokawa.localsend.expressive.resources.gsf_text_regular
import dev.kurokawa.localsend.expressive.resources.gsf_text_semibold
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
 * Google Sans Flex as STATIC instances (v0.10.0). The variable font
 * rendered only at its default instance on Android — every weight and
 * the ROND axis were ignored there, which is why headings looked wrong
 * on Ernest's phone while desktop (Skia honours variationSettings)
 * looked right. The instances are generated from the variable font
 * with the exact recipes the variable version used: text = wdth 100 /
 * ROND 35, display = wdth 118 / ROND 100, opsz pinned at the default
 * 18. Nine files, 1.23 MB total vs the variable font's 3.91 MB.
 */
@Composable
private fun textFamily(): FontFamily {
    val regular = Font(Res.font.gsf_text_regular, FontWeight.Normal)
    val medium = Font(Res.font.gsf_text_medium, FontWeight.Medium)
    val semiBold = Font(Res.font.gsf_text_semibold, FontWeight.SemiBold)
    val bold = Font(Res.font.gsf_text_bold, FontWeight.Bold)
    // FontFamily identity MUST be stable: rebuilding it on every
    // recomposition re-measures every text in the app — that was the
    // Settings-page stutter Ernest reported.
    return remember(regular, medium, semiBold, bold) {
        FontFamily(regular, medium, semiBold, bold)
    }
}

@Composable
private fun displayFamily(): FontFamily {
    val medium = Font(Res.font.gsf_display_medium, FontWeight.Medium)
    val semiBold = Font(Res.font.gsf_display_semibold, FontWeight.SemiBold)
    val bold = Font(Res.font.gsf_display_bold, FontWeight.Bold)
    val extraBold = Font(Res.font.gsf_display_extrabold, FontWeight.ExtraBold)
    return remember(medium, semiBold, bold, extraBold) {
        FontFamily(medium, semiBold, bold, extraBold)
    }
}

/**
 * The wordmark cut from Ernest's mockups: Google Sans Flex CONDENSED
 * (wdth 80, ROND 100, wght 600) — his branding is the same font,
 * styled differently (narrow), not the wide display cut.
 */
@Composable
internal fun wordmarkFamily(): FontFamily {
    val regular = Font(Res.font.gsf_wordmark_semibold, FontWeight.SemiBold)
    return remember { FontFamily(regular) }
}

/** The wordmark's slanted twin (same condensed cut, slnt -10) for "Ex". */
@Composable
internal fun wordmarkItalicFamily(): FontFamily {
    val italic = Font(Res.font.gsf_wordmark_semibold_italic, FontWeight.SemiBold)
    return remember { FontFamily(italic) }
}

@Composable
private fun expressiveTypography(): Typography {
    val display = displayFamily()
    val text = textFamily()
    val base = Typography()
    return remember(display, text) {
        // UDYAT-style styled typography (v0.12.4): EVERY style — base
        // and emphasized — is deliberately set in a Google Sans Flex
        // cut. Before, only the 15 base styles were mapped, so every
        // *Emphasized style (group headers, nav rows, emphasized
        // buttons) silently fell back to the platform font. Display
        // sizes use the display optical cut; emphasized = the same
        // cut one weight step stronger.
        fun styled(
            style: androidx.compose.ui.text.TextStyle,
            family: FontFamily,
            weight: FontWeight,
        ) = style.copy(fontFamily = family, fontWeight = weight)
        Typography(
            displayLarge = styled(base.displayLarge, display, FontWeight.Bold),
            displayMedium = styled(base.displayMedium, display, FontWeight.Bold),
            displaySmall = styled(base.displaySmall, display, FontWeight.SemiBold),
            displayLargeEmphasized = styled(base.displayLargeEmphasized, display, FontWeight.ExtraBold),
            displayMediumEmphasized = styled(base.displayMediumEmphasized, display, FontWeight.ExtraBold),
            displaySmallEmphasized = styled(base.displaySmallEmphasized, display, FontWeight.Bold),
            headlineLarge = styled(base.headlineLarge, display, FontWeight.SemiBold),
            headlineMedium = styled(base.headlineMedium, display, FontWeight.SemiBold),
            headlineSmall = styled(base.headlineSmall, display, FontWeight.SemiBold),
            headlineLargeEmphasized = styled(base.headlineLargeEmphasized, display, FontWeight.Bold),
            headlineMediumEmphasized = styled(base.headlineMediumEmphasized, display, FontWeight.Bold),
            headlineSmallEmphasized = styled(base.headlineSmallEmphasized, display, FontWeight.Bold),
            titleLarge = styled(base.titleLarge, display, FontWeight.SemiBold),
            titleMedium = styled(base.titleMedium, text, FontWeight.Medium),
            titleSmall = styled(base.titleSmall, text, FontWeight.Medium),
            titleLargeEmphasized = styled(base.titleLargeEmphasized, display, FontWeight.Bold),
            titleMediumEmphasized = styled(base.titleMediumEmphasized, text, FontWeight.SemiBold),
            titleSmallEmphasized = styled(base.titleSmallEmphasized, text, FontWeight.SemiBold),
            bodyLarge = styled(base.bodyLarge, text, FontWeight.Normal),
            bodyMedium = styled(base.bodyMedium, text, FontWeight.Normal),
            bodySmall = styled(base.bodySmall, text, FontWeight.Normal),
            bodyLargeEmphasized = styled(base.bodyLargeEmphasized, text, FontWeight.Medium),
            bodyMediumEmphasized = styled(base.bodyMediumEmphasized, text, FontWeight.Medium),
            bodySmallEmphasized = styled(base.bodySmallEmphasized, text, FontWeight.Medium),
            labelLarge = styled(base.labelLarge, text, FontWeight.Medium),
            labelMedium = styled(base.labelMedium, text, FontWeight.Medium),
            labelSmall = styled(base.labelSmall, text, FontWeight.Medium),
            labelLargeEmphasized = styled(base.labelLargeEmphasized, text, FontWeight.SemiBold),
            labelMediumEmphasized = styled(base.labelMediumEmphasized, text, FontWeight.SemiBold),
            labelSmallEmphasized = styled(base.labelSmallEmphasized, text, FontWeight.SemiBold),
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
