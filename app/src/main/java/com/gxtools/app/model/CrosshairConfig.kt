package com.gxtools.app.model

import org.json.JSONObject

enum class CrosshairShape {
    CROSS, DOT, CIRCLE, T_SHAPE, X_SHAPE
}

/**
 * Holds all the settings for a single crosshair preset.
 */
data class CrosshairConfig(
    val name: String = "Default",
    val shape: CrosshairShape = CrosshairShape.CROSS,
    val colorArgb: Long = 0xFF00FF00, // hijau default
    val sizeDp: Float = 28f,
    val thicknessDp: Float = 2f,
    val opacity: Float = 1f,        // 0f..1f
    val offsetXDp: Float = 0f,       // posisi geser dari tengah layar
    val offsetYDp: Float = 0f
) {
    fun toJson(): JSONObject = JSONObject().apply {
        put("name", name)
        put("shape", shape.name)
        put("color", colorArgb)
        put("size", sizeDp)
        put("thickness", thicknessDp)
        put("opacity", opacity)
        put("offsetX", offsetXDp)
        put("offsetY", offsetYDp)
    }

    companion object {
        fun fromJson(json: JSONObject): CrosshairConfig = CrosshairConfig(
            name = json.optString("name", "Preset"),
            shape = runCatching { CrosshairShape.valueOf(json.optString("shape", "CROSS")) }
                .getOrDefault(CrosshairShape.CROSS),
            colorArgb = json.optLong("color", 0xFF00FF00),
            sizeDp = json.optDouble("size", 28.0).toFloat(),
            thicknessDp = json.optDouble("thickness", 2.0).toFloat(),
            opacity = json.optDouble("opacity", 1.0).toFloat(),
            offsetXDp = json.optDouble("offsetX", 0.0).toFloat(),
            offsetYDp = json.optDouble("offsetY", 0.0).toFloat()
        )
    }
}
