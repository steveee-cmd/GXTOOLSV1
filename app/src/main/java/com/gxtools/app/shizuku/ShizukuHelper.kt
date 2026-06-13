package com.gxtools.app.shizuku

import android.content.Context
import android.content.pm.PackageManager

/**
 * Helper untuk cek & request izin Shizuku,
 * lalu jalankan perintah shell via Shizuku (bukan root).
 *
 * Dependensi: rikka.shizuku:api & rikka.shizuku:provider (lihat build.gradle)
 */
object ShizukuHelper {

    const val SHIZUKU_PACKAGE = "moe.shizuku.privileged.api"
    const val REQUEST_CODE = 1001

    /** Apakah Shizuku app terinstall di device? */
    fun isShizukuInstalled(context: Context): Boolean {
        return try {
            context.packageManager.getPackageInfo(SHIZUKU_PACKAGE, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    /** Apakah Shizuku service sedang berjalan? */
    fun isShizukuRunning(): Boolean {
        return try {
            rikka.shizuku.Shizuku.pingBinder()
        } catch (e: Exception) {
            false
        }
    }

    /** Apakah kita sudah punya izin Shizuku? */
    fun hasPermission(): Boolean {
        return try {
            if (rikka.shizuku.Shizuku.isPreV11()) {
                false
            } else {
                rikka.shizuku.Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED
            }
        } catch (e: Exception) {
            false
        }
    }

    /** Minta izin Shizuku. Hasilnya balik via Shizuku.OnRequestPermissionResultListener. */
    fun requestPermission() {
        rikka.shizuku.Shizuku.requestPermission(REQUEST_CODE)
    }

    /**
     * Jalankan shell command via Shizuku.
     * Return: output string atau null kalau gagal.
     */
    fun exec(command: String): String? {
        return try {
            val process = rikka.shizuku.Shizuku.newProcess(arrayOf("sh", "-c", command), null, null)
            val output = process.inputStream.bufferedReader().readText()
            process.waitFor()
            output.trim()
        } catch (e: Exception) {
            null
        }
    }

    // ---- Resolusi & DPI ----

    /** Dapatkan resolusi layar saat ini (width x height). */
    fun getCurrentResolution(): Pair<Int, Int>? {
        val output = exec("wm size") ?: return null
        val line = output.lines().lastOrNull { it.contains("size:") } ?: return null
        val match = Regex("(\\d+)x(\\d+)").find(line) ?: return null
        val (w, h) = match.destructured
        return Pair(w.toInt(), h.toInt())
    }

    /** Dapatkan DPI saat ini. */
    fun getCurrentDpi(): Int? {
        val output = exec("wm density") ?: return null
        val line = output.lines().lastOrNull { it.contains("density:") } ?: return null
        return Regex("(\\d+)").find(line)?.value?.toIntOrNull()
    }

    /** Ganti resolusi layar. */
    fun setResolution(width: Int, height: Int): Boolean {
        return exec("wm size ${width}x${height}") != null
    }

    /** Reset resolusi ke default. */
    fun resetResolution(): Boolean {
        return exec("wm size reset") != null
    }

    /** Ganti DPI layar. */
    fun setDpi(dpi: Int): Boolean {
        return exec("wm density $dpi") != null
    }

    /** Reset DPI ke default. */
    fun resetDpi(): Boolean {
        return exec("wm density reset") != null
    }
}
