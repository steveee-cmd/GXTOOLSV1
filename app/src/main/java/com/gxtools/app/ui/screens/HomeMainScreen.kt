package com.gxtools.app.ui.screens

import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import com.gxtools.app.data.GameLibraryManager
import com.gxtools.app.model.GameEntry
import com.gxtools.app.ui.theme.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// === HOME SCREEN - persis Game Corner screenshot ===
@Composable
fun HomeMainScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var games by remember { mutableStateOf(GameLibraryManager.loadGames(context)) }
    var showAddDialog by remember { mutableStateOf(false) }
    var installedApps by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
    var loadingApps by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    val iconCache = remember { mutableStateMapOf<String, ImageBitmap>() }

    fun getIcon(packageName: String): ImageBitmap? {
        return iconCache[packageName] ?: try {
            val bmp = context.packageManager.getApplicationIcon(packageName).toBitmap().asImageBitmap()
            iconCache[packageName] = bmp; bmp
        } catch (e: Exception) { null }
    }

    fun refreshGames() { games = GameLibraryManager.loadGames(context) }

    LaunchedEffect(showAddDialog) {
        if (showAddDialog && installedApps.isEmpty()) {
            loadingApps = true
            withContext(Dispatchers.IO) {
                val pm = context.packageManager
                val apps = pm.getInstalledApplications(PackageManager.GET_META_DATA)
                    .filter { pm.getLaunchIntentForPackage(it.packageName) != null }
                    .map { Pair(it.packageName, try { pm.getApplicationLabel(it).toString() } catch (e: Exception) { it.packageName }) }
                    .sortedBy { it.second.lowercase() }
                withContext(Dispatchers.Main) { installedApps = apps; loadingApps = false }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GXBackground)
            .verticalScroll(rememberScrollState())
    ) {
        GXTopBar(title = "Home")

        // Banner Game Corner persis screenshot (gradient merah-oranye-hitam dengan ROG logo feel)
        Box(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
                .height(100.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(Color(0xFF7A1A0A), Color(0xFF3A0A02), GXBackground)
                    )
                )
        ) {
            // Decorative shape kanan atas
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 16.dp)
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(GXOrange.copy(alpha = 0.08f))
            )
            Column(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 20.dp)
            ) {
                Text("GAME", color = GXTextSecondary, fontSize = 10.sp, letterSpacing = 2.sp)
                Text("CORNER", color = GXText, fontWeight = FontWeight.Black, fontSize = 24.sp, letterSpacing = 1.sp)
                Text("V1.2.7-12750", color = GXOrange, fontSize = 11.sp, fontWeight = FontWeight.Medium)
            }
        }

        Spacer(Modifier.height(12.dp))

        // Stats bar: Batas Koleksi | Energi | Game Space
        Box(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(GXSurface)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Batas Koleksi", color = GXTextSecondary, fontSize = 11.sp)
                    Text("${games.size} / 10", color = GXText, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                Box(modifier = Modifier.width(1.dp).height(36.dp).background(GXBorder))
                Spacer(Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Energi", color = GXTextSecondary, fontSize = 11.sp)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("40 / 600", color = GXText, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Spacer(Modifier.width(6.dp))
                        Icon(Icons.Default.Add, contentDescription = null, tint = GXOrange, modifier = Modifier.size(16.dp))
                    }
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(GXSurfaceVariant)
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Column {
                            Text("Game", color = GXTextSecondary, fontSize = 10.sp)
                            Text("Space", color = GXText, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        Icon(Icons.Default.RocketLaunch, contentDescription = null, tint = GXTextSecondary, modifier = Modifier.size(22.dp))
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Perpustakaan Game header
        Row(
            modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("PERPUSTAKAAN GAME", color = GXText, fontWeight = FontWeight.Black, fontSize = 13.sp, letterSpacing = 1.sp)
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(GXSurfaceVariant)
                    .clickable { showAddDialog = true }
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Tambah Game", color = GXText, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    Icon(Icons.Default.Add, contentDescription = null, tint = GXText, modifier = Modifier.size(16.dp))
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        if (games.isEmpty()) {
            // Empty state persis screenshot
            Box(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(GXSurface),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Android, contentDescription = null, tint = GXOrange, modifier = Modifier.size(36.dp))
                    Text("Tidak Ada Game / Aplikasi Terdeteksi!", color = GXText, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text(
                        "Tambahkan aplikasi yang diinginkan untuk memulai AxeronSpace.",
                        color = GXTextSecondary,
                        fontSize = 12.sp
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                games.forEach { entry ->
                    val pm = context.packageManager
                    val appLabel = try {
                        pm.getApplicationLabel(pm.getApplicationInfo(entry.packageName, 0)).toString()
                    } catch (e: Exception) { entry.packageName }
                    val displayName = entry.customLabel.ifBlank { appLabel }
                    val icon = getIcon(entry.packageName)

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(GXSurface)
                            .padding(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            if (icon != null) {
                                Image(bitmap = icon, contentDescription = null, modifier = Modifier.size(48.dp).clip(RoundedCornerShape(10.dp)))
                            } else {
                                Box(Modifier.size(48.dp).clip(RoundedCornerShape(10.dp)).background(GXOrange.copy(alpha = 0.15f)), contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.SportsEsports, contentDescription = null, tint = GXOrange)
                                }
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(displayName, color = GXText, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text(entry.packageName, color = GXTextSecondary, fontSize = 11.sp, maxLines = 1)
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(GXOrange)
                                        .clickable {
                                            val intent = pm.getLaunchIntentForPackage(entry.packageName)
                                            intent?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                            intent?.let { context.startActivity(it) }
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.PlayArrow, contentDescription = "Play", tint = GXText, modifier = Modifier.size(20.dp))
                                }
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(GXSurfaceVariant)
                                        .clickable {
                                            GameLibraryManager.removeGame(context, entry.packageName)
                                            refreshGames()
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = GXRed, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))
    }

    // Add game dialog
    if (showAddDialog) {
        AlertDialog(
            containerColor = GXSurface,
            onDismissRequest = { showAddDialog = false; searchQuery = "" },
            title = { Text("Pilih Game", color = GXText, fontWeight = FontWeight.Bold) },
            text = {
                Column(modifier = Modifier.height(400.dp)) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Cari app...", color = GXTextDim) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GXOrange,
                            unfocusedBorderColor = GXBorder,
                            focusedTextColor = GXText,
                            unfocusedTextColor = GXText
                        )
                    )
                    Spacer(Modifier.height(8.dp))
                    if (loadingApps) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = GXOrange)
                        }
                    } else {
                        val added = games.map { it.packageName }.toSet()
                        val filtered = installedApps.filter {
                            searchQuery.isBlank() || it.second.contains(searchQuery, true) || it.first.contains(searchQuery, true)
                        }
                        androidx.compose.foundation.lazy.LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            androidx.compose.foundation.lazy.items(filtered) { (pkg, label) ->
                                val isAdded = pkg in added
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isAdded) GXSurfaceVariant else GXBackground)
                                        .clickable(enabled = !isAdded) {
                                            GameLibraryManager.addGame(context, GameEntry(packageName = pkg))
                                            refreshGames()
                                            showAddDialog = false
                                            searchQuery = ""
                                        }
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Text(label, color = if (isAdded) GXTextDim else GXText, modifier = Modifier.weight(1f))
                                    if (isAdded) Text("✓", color = GXOrange, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showAddDialog = false; searchQuery = "" }) {
                    Text("Tutup", color = GXOrange)
                }
            }
        )
    }
}
