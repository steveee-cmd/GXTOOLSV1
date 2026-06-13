package com.gxtools.app.model

import org.json.JSONObject

/**
 * Representasi satu game di library GX Tools.
 * packageName: package ID game (misal "com.mobile.legends")
 * customLabel: nama tampilan custom (opsional, kalau kosong pakai nama app bawaan)
 */
data class GameEntry(
    val packageName: String,
    val customLabel: String = ""
) {
    fun toJson(): JSONObject = JSONObject().apply {
        put("packageName", packageName)
        put("customLabel", customLabel)
    }

    companion object {
        fun fromJson(json: JSONObject): GameEntry = GameEntry(
            packageName = json.getString("packageName"),
            customLabel = json.optString("customLabel", "")
        )
    }
}
