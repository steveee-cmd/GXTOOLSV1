package com.gxtools.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.gxtools.app.model.CrosshairConfig
import com.gxtools.app.model.CrosshairShape

/**
 * Menggambar preview crosshair sesuai [config] di dalam kotak persegi.
 * Logikanya sengaja dibuat mirror dari CrosshairOverlayView agar
 * preview di editor sama persis dengan tampilan overlay asli.
 */
@Composable
fun CrosshairPreview(
    config: CrosshairConfig,
    modifier: Modifier = Modifier,
    boxSize: androidx.compose.ui.unit.Dp = 160.dp
) {
    Canvas(
        modifier = modifier
            .size(boxSize)
            .background(Color(0xFF000000))
    ) {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val sizePx = config.sizeDp * density
        val thicknessPx = (config.thicknessDp * density).coerceAtLeast(1f)
        val color = Color(config.colorArgb).copy(alpha = config.opacity.coerceIn(0f, 1f))
        val stroke = Stroke(width = thicknessPx, cap = StrokeCap.Round)

        when (config.shape) {
            CrosshairShape.CROSS -> {
                drawLine(color, Offset(cx - sizePx, cy), Offset(cx + sizePx, cy), thicknessPx, StrokeCap.Round)
                drawLine(color, Offset(cx, cy - sizePx), Offset(cx, cy + sizePx), thicknessPx, StrokeCap.Round)
            }
            CrosshairShape.X_SHAPE -> {
                val d = sizePx * 0.7f
                drawLine(color, Offset(cx - d, cy - d), Offset(cx + d, cy + d), thicknessPx, StrokeCap.Round)
                drawLine(color, Offset(cx - d, cy + d), Offset(cx + d, cy - d), thicknessPx, StrokeCap.Round)
            }
            CrosshairShape.T_SHAPE -> {
                drawLine(color, Offset(cx - sizePx, cy), Offset(cx + sizePx, cy), thicknessPx, StrokeCap.Round)
                drawLine(color, Offset(cx, cy), Offset(cx, cy + sizePx), thicknessPx, StrokeCap.Round)
            }
            CrosshairShape.CIRCLE -> {
                drawCircle(color, radius = sizePx, center = Offset(cx, cy), style = stroke)
            }
            CrosshairShape.DOT -> {
                drawCircle(color, radius = thicknessPx * 1.5f, center = Offset(cx, cy))
            }
        }
    }
}
