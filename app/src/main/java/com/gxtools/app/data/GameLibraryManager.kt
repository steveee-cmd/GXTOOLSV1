package com.gxtools.app.data

import android.content.Context
import com.gxtools.app.model.GameEntry
import org.json.JSONArray
import org.json.JSONObject

/**
 * Menyimpan dan memuat daftar game di library GX Tools.
 * Data disimpan di SharedPreferences sebagai JSON array.
 */
object GameLibraryManager {

    private const val PREFS_NAME = "gxtools_prefs"
    private const val KEY_GAMES = "game_library"

    fun saveGames(context: Context, games: List<GameEntry>) {
        val arr = JSONArray()
        games.forEach { arr.put(it.toJson()) }
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit().putString(KEY_GAMES, arr.toString()).apply()
    }

    fun loadGames(context: Context): List<GameEntry> {
        val raw = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_GAMES, null) ?: return emptyList()
        return runCatching {
            val arr = JSONArray(raw)
            (0 until arr.length()).map { GameEntry.fromJson(arr.getJSONObject(it)) }
        }.getOrDefault(emptyList())
    }

    fun addGame(context: Context, entry: GameEntry) {
        val current = loadGames(context).toMutableList()
        if (current.none { it.packageName == entry.packageName }) {
            current.add(entry)
            saveGames(context, current)
        }
    }

    fun removeGame(context: Context, packageName: String) {
        val current = loadGames(context).toMutableList()
        current.removeAll { it.packageName == packageName }
        saveGames(context, current)
    }
}
