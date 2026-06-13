package com.gxtools.app.overlay

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.View
import com.gxtools.app.model.CrosshairConfig
import com.gxtools.app.model.CrosshairShape

/**
 * View kecil & transparan yang menggambar crosshair sesuai [config].
 * View ini ditempel ke window lewat WindowManager di [OverlayService].
 */
class CrosshairOverlayView(context: Context) : View(context) {

    var config: CrosshairConfig = CrosshairConfig()
        set(value) {
            field = value
            invalidate()
        }

    private val paint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
    }

    private fun dp(value: Float): Float = value * resources.displayMetrics.density

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val cx = width / 2f
        val cy = height / 2f
        val size = dp(config.sizeDp)
        val thickness = dp(config.thicknessDp)

        paint.color = config.colorArgb.toInt()
        paint.alpha = (config.opacity.coerceIn(0f, 1f) * 255).toInt()
        paint.strokeWidth = thickness

        when (config.shape) {
            CrosshairShape.CROSS -> {
                canvas.drawLine(cx - size, cy, cx + size, cy, paint)
                canvas.drawLine(cx, cy - size, cx, cy + size, paint)
            }
            CrosshairShape.X_SHAPE -> {
                val d = size * 0.7f
                canvas.drawLine(cx - d, cy - d, cx + d, cy + d, paint)
                canvas.drawLine(cx - d, cy + d, cx + d, cy - d, paint)
            }
            CrosshairShape.T_SHAPE -> {
                canvas.drawLine(cx - size, cy, cx + size, cy, paint)
                canvas.drawLine(cx, cy, cx, cy + size, paint)
            }
            CrosshairShape.CIRCLE -> {
                canvas.drawCircle(cx, cy, size, paint)
            }
            CrosshairShape.DOT -> {
                val dotPaint = Paint(paint).apply { style = Paint.Style.FILL }
                canvas.drawCircle(cx, cy, thickness * 1.5f, dotPaint)
            }
        }
    }

    companion object {
        /** Ukuran area gambar overlay dalam dp, harus cukup besar untuk semua shape. */
        const val CANVAS_SIZE_DP = 160
    }
}
