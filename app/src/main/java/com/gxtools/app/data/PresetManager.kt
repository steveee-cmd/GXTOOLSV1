package com.gxtools.app.data

import android.content.Context
import android.os.Environment
import com.gxtools.app.model.CrosshairConfig
import org.json.JSONObject
import java.io.File

/**
 * Mengelola preset crosshair.
 *
 * - Konfigurasi "current" disimpan di SharedPreferences (dipakai overlay service).
 * - Preset bernama disimpan sebagai file .json di folder
 *   /Android/data/<package>/files/Download/gxtools_presets/
 *   (folder app-specific, jadi tidak perlu izin storage tambahan di Android 10+)
 */
object PresetManager {

    private const val PREFS_NAME = "gxtools_prefs"
    private const val KEY_CURRENT = "current_config"

    private fun presetDir(context: Context): File {
        val base = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
            ?: context.filesDir
        val dir = File(base, "gxtools_presets")
        if (!dir.exists()) dir.mkdirs()
        return dir
    }

    fun saveCurrentConfig(context: Context, config: CrosshairConfig) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_CURRENT, config.toJson().toString()).apply()
    }

    fun loadCurrentConfig(context: Context): CrosshairConfig {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val raw = prefs.getString(KEY_CURRENT, null) ?: return CrosshairConfig()
        return runCatching { CrosshairConfig.fromJson(JSONObject(raw)) }
            .getOrDefault(CrosshairConfig())
    }

    fun savePreset(context: Context, config: CrosshairConfig) {
        val safeName = config.name.ifBlank { "preset" }
            .replace(Regex("[^A-Za-z0-9_-]"), "_")
        val file = File(presetDir(context), "$safeName.json")
        file.writeText(config.toJson().toString())
    }

    fun listPresets(context: Context): List<CrosshairConfig> {
        val dir = presetDir(context)
        val files = dir.listFiles { f -> f.extension == "json" } ?: emptyArray()
        return files.mapNotNull { f ->
            runCatching { CrosshairConfig.fromJson(JSONObject(f.readText())) }.getOrNull()
        }.sortedBy { it.name.lowercase() }
    }

    fun deletePreset(context: Context, name: String) {
        val safeName = name.replace(Regex("[^A-Za-z0-9_-]"), "_")
        File(presetDir(context), "$safeName.json").delete()
    }
}
