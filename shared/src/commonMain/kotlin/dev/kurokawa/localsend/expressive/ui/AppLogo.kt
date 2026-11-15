package dev.kurokawa.localsend.expressive.ui

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.graphics.drawscope.withTransform

/**
 * The app logo: the official LocalSend radar mark (Apache-2.0,
 * (c) the LocalSend project) with the per-element green-to-blue
 * gradients in Google's construction — the SAME geometry and
 * gradients as the launcher icon (ic_launcher_foreground), kept in
 * 108-unit icon space and scaled to whatever size it is given.
 * Used by the splash; the launcher uses the vector form.
 */
private class LogoElement(
    val pathData: String,
    val colors: List<Color>,
    /** Linear gradient start, or the radial center. */
    val startOrCenter: Offset,
    /** Linear gradient end; unused for radial elements. */
    val end: Offset = Offset.Zero,
    /** Radial gradient radius; 0 means linear. */
    val radialRadius: Float = 0f,
) {
    constructor(
        pathData: String,
        colors: List<Color>,
        center: Offset,
        radius: Float,
    ) : this(pathData, colors, center, Offset.Zero, radius)
}

private val LogoElements = listOf(
    LogoElement("M38.38,54.03A15.60,15.60,0.00,0,1,54.00,38.44A15.60,15.60,0.00,0,1,69.62,54.03A15.60,15.60,0.00,0,1,54.00,69.44A15.60,15.60,0.00,0,1,38.38,54.03Z", listOf(Color(0xFF23C163), Color(0xFF0AA5C6)), Offset(46.75f, 46.75f), 16.72f),
    LogoElement("M79.45,40.14A4.46,4.46,0.00,0,1,73.47,39.12A24.69,24.69,0.00,0,0,68.94,34.59A4.45,4.45,0.00,0,1,67.92,28.62A29.11,29.11,0.00,0,1,79.45,40.14Z", listOf(Color(0xFF0DBB63), Color(0xFF01ABB3)), Offset(67.90f, 28.58f), Offset(79.50f, 40.10f)),
    LogoElement("M34.53,39.12A4.46,4.46,0.00,0,1,28.56,40.14A29.11,29.11,0.00,0,1,40.08,28.62A4.45,4.45,0.00,0,1,39.07,34.59A24.69,24.69,0.00,0,0,34.53,39.12Z", listOf(Color(0xFF5CCE6C), Color(0xFF2DC364)), Offset(28.50f, 28.58f), Offset(40.10f, 40.10f)),
    LogoElement("M83.00,54.03C83.00,56.80,82.61,59.53,81.84,62.18A4.45,4.45,0.00,0,1,78.33,57.25C78.61,55.12,78.61,52.96,78.33,50.83A4.45,4.45,0.00,0,1,81.84,45.90C82.61,48.54,83.00,51.29,83.00,54.03Z", listOf(Color(0xFF05A7C1), Color(0xFF3294F4)), Offset(78.31f, 45.90f), Offset(83.00f, 62.19f)),
    LogoElement("M25.00,54.03C25.00,51.29,25.39,48.54,26.17,45.90A4.45,4.45,0.00,0,1,29.67,50.83C29.39,52.95,29.39,55.12,29.67,57.25A4.45,4.45,0.00,0,1,26.17,62.18A28.93,28.93,0.00,0,1,25.00,54.03Z", listOf(Color(0xFF40C768), Color(0xFF15BE60)), Offset(25.00f, 45.90f), Offset(29.69f, 62.19f)),
    LogoElement("M67.92,79.46A4.45,4.45,0.00,0,1,68.94,73.49A24.69,24.69,0.00,0,0,73.47,68.95A4.46,4.46,0.00,0,1,79.45,67.94A29.11,29.11,0.00,0,1,67.92,79.46Z", listOf(Color(0xFF1E9CDD), Color(0xFF368AFF)), Offset(67.90f, 67.90f), Offset(79.50f, 79.50f)),
    LogoElement("M40.08,79.46A29.11,29.11,0.00,0,1,28.56,67.94A4.46,4.46,0.00,0,1,34.53,68.96A24.69,24.69,0.00,0,0,39.07,73.49C40.92,74.90,41.35,77.51,40.08,79.46Z", listOf(Color(0xFF0DBB63), Color(0xFF01AAB7)), Offset(28.50f, 67.90f), Offset(41.38f, 79.50f)),
    LogoElement("M54.00,83.00A29.02,29.02,0.00,0,1,45.85,81.84A4.46,4.46,0.00,0,1,50.79,78.34C52.93,78.62,55.08,78.62,57.21,78.34A4.46,4.46,0.00,0,1,62.15,81.84C59.51,82.61,56.75,83.00,54.00,83.00Z", listOf(Color(0xFF05A7C1), Color(0xFF3294F4)), Offset(45.90f, 78.31f), Offset(62.10f, 83.00f)),
    LogoElement("M57.64,29.70C55.36,29.37,53.08,29.45,50.79,29.66A4.46,4.46,0.00,0,1,45.85,26.16A29.03,29.03,0.00,0,1,54.00,25.00L54.00,25.00C56.76,25.00,59.50,25.39,62.15,26.16A4.45,4.45,0.00,0,1,57.64,29.70Z", listOf(Color(0xFF40C768), Color(0xFF15BE60)), Offset(45.90f, 25.00f), Offset(62.10f, 29.69f)),
)

@Composable
fun AppLogo(modifier: Modifier = Modifier) {
    val paths = remember {
        LogoElements.map { PathParser().parsePathString(it.pathData).toPath() }
    }
    Canvas(modifier) {
        val k = size.minDimension / 108f
        val dx = (size.width - 108f * k) / 2f
        val dy = (size.height - 108f * k) / 2f
        withTransform({
            translate(dx, dy)
            // Pivot MUST be explicit: the default pivot is the canvas
            // center, which flings the 108-space drawing off-canvas
            // (caught by the splash render, 2026-11-15).
            scale(k, k, Offset.Zero)
        }) {
            LogoElements.forEachIndexed { i, el ->
                val brush = if (el.radialRadius > 0f) {
                    Brush.radialGradient(
                        colors = el.colors,
                        center = el.startOrCenter,
                        radius = el.radialRadius,
                    )
                } else {
                    Brush.linearGradient(
                        colors = el.colors,
                        start = el.startOrCenter,
                        end = el.end,
                    )
                }
                drawPath(paths[i], brush)
            }
        }
    }
}
