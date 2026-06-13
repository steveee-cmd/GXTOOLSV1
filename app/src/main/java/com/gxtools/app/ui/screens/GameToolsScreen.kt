package com.gxtools.app.ui.screens

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gxtools.app.data.PresetManager
import com.gxtools.app.model.CrosshairConfig
import com.gxtools.app.model.CrosshairShape
import com.gxtools.app.overlay.OverlayService
import com.gxtools.app.shizuku.ShizukuHelper
import com.gxtools.app.ui.components.CrosshairPreview
import com.gxtools.app.ui.theme.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// Preset warna crosshair persis screenshot
private val CROSSHAIR_COLORS = listOf(
    Color.Red, Color.Green, Color.Blue, Color.Yellow, Color.Cyan, Color.Magenta, Color.White
)

// Preset warna crosshair (Long ARGB)
private val CROSSHAIR_COLORS_LONG = listOf(
    0xFFFF0000L, 0xFF00FF00L, 0xFF0000FFL, 0xFFFFFF00L, 0xFF00FFFFL, 0xFFFF00FFL, 0xFFFFFFFFL
)

// Preset warna crosshair shape button (rounded squares)
private val SHAPE_COLORS = listOf(
    Color.White, Color(0xFFE53935), Color(0xFFD4641A), Color(0xFFFFB300), Color(0xFFFFEB3B), Color.Blue
)

private val RESOLUTION_PRESETS = listOf(
    Triple("720p (HD)", 720, 1280),
    Triple("1080p (FHD)", 1080, 1920),
    Triple("900p (Gaming)", 900, 1600),
    Triple("540p (Ultra Low)", 540, 960),
)
private val DPI_PRESETS = listOf(
    Pair("160 (Low)", 160), Pair("240 (Medium)", 240), Pair("320 (Normal)", 320),
    Pair("400 (High)", 400), Pair("480 (XHigh)", 480),
)

// === GAME TOOLS SCREEN - persis screenshot ===
@Composable
fun GameToolsScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Tab state: Semua / Visual / Konfigurasi
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Semua", "Visual", "Konfigurasi")

    // Feature states
    var gyroEnabled by remember { mutableStateOf(true) }
    var crosshairEnabled by remember { mutableStateOf(true) }
    var monitorEnabled by remember { mutableStateOf(true) }

    // Crosshair config
    var config by remember { mutableStateOf(PresetManager.loadCurrentConfig(context)) }
    fun updateConfig(new: CrosshairConfig) {
        config = new
        PresetManager.saveCurrentConfig(context, new)
        OverlayService.start(context)
    }

    // Monitor checkboxes
    var showCpu by remember { mutableStateOf(true) }
    var showGpu by remember { mutableStateOf(false) }
    var showRam by remember { mutableStateOf(true) }
    var showBattery by remember { mutableStateOf(true) }
    var showTemp by remember { mutableStateOf(true) }
    var showFps by remember { mutableStateOf(true) }
    var showTime by remember { mutableStateOf(true) }

    // Shizuku & display states (Konfigurasi tab)
    var shizukuInstalled by remember { mutableStateOf(ShizukuHelper.isShizukuInstalled(context)) }
    var shizukuRunning by remember { mutableStateOf(ShizukuHelper.isShizukuRunning()) }
    var shizukuPermission by remember { mutableStateOf(ShizukuHelper.hasPermission()) }
    var currentRes by remember { mutableStateOf<Pair<Int, Int>?>(null) }
    var currentDpi by remember { mutableStateOf<Int?>(null) }
    var customWidth by remember { mutableStateOf("") }
    var customHeight by remember { mutableStateOf("") }
    var customDpi by remember { mutableStateOf("") }
    val snackState = remember { SnackbarHostState() }

    fun showSnack(msg: String) { scope.launch { snackState.showSnackbar(msg) } }

    fun loadDisplayValues() {
        if (shizukuPermission) {
            scope.launch(Dispatchers.IO) {
                val res = ShizukuHelper.getCurrentResolution()
                val dpi = ShizukuHelper.getCurrentDpi()
                withContext(Dispatchers.Main) { currentRes = res; currentDpi = dpi }
            }
        }
    }

    fun refreshShizuku() {
        shizukuInstalled = ShizukuHelper.isShizukuInstalled(context)
        shizukuRunning = ShizukuHelper.isShizukuRunning()
        shizukuPermission = ShizukuHelper.hasPermission()
        loadDisplayValues()
    }

    LaunchedEffect(Unit) { refreshShizuku() }

    Scaffold(
        containerColor = GXBackground,
        snackbarHost = { SnackbarHost(snackState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(GXBackground)
        ) {
            GXTopBar(title = "Game Tools")

            // Tab bar: Semua | Visual | Konfigurasi
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                tabs.forEachIndexed { idx, label ->
                    val selected = selectedTab == idx
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50.dp))
                            .background(if (selected) GXOrange else GXSurface)
                            .clickable { selectedTab = idx }
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            if (idx == 0) Icon(Icons.Default.SportsEsports, contentDescription = null, tint = if (selected) GXText else GXTextSecondary, modifier = Modifier.size(15.dp))
                            if (idx == 1) Icon(Icons.Default.Visibility, contentDescription = null, tint = if (selected) GXText else GXTextSecondary, modifier = Modifier.size(15.dp))
                            if (idx == 2) Icon(Icons.Default.Settings, contentDescription = null, tint = if (selected) GXText else GXTextSecondary, modifier = Modifier.size(15.dp))
                            Text(label, color = if (selected) GXText else GXTextSecondary, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal, fontSize = 13.sp)
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // KONFIGURASI TAB - Resolusi & DPI
                if (selectedTab == 2) {
                    KonfigurasiTab(
                        shizukuInstalled = shizukuInstalled,
                        shizukuRunning = shizukuRunning,
                        shizukuPermission = shizukuPermission,
                        currentRes = currentRes,
                        currentDpi = currentDpi,
                        customWidth = customWidth,
                        customHeight = customHeight,
                        customDpi = customDpi,
                        onCustomWidthChange = { customWidth = it },
                        onCustomHeightChange = { customHeight = it },
                        onCustomDpiChange = { customDpi = it },
                        onInstallShizuku = {
                            try {
                                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=${ShizukuHelper.SHIZUKU_PACKAGE}")).also { it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) })
                            } catch (e: Exception) {
                                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://shizuku.rikka.app/")).also { it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) })
                            }
                        },
                        onRequestPermission = { ShizukuHelper.requestPermission() },
                        onRefreshShizuku = { refreshShizuku() },
                        onRefreshDisplay = { loadDisplayValues() },
                        onSetResolution = { w, h ->
                            scope.launch(Dispatchers.IO) {
                                val ok = ShizukuHelper.setResolution(w, h)
                                withContext(Dispatchers.Main) {
                                    showSnack(if (ok) "Resolusi → ${w}x${h}" else "Gagal ubah resolusi")
                                    if (ok) loadDisplayValues()
                                }
                            }
                        },
                        onResetResolution = {
                            scope.launch(Dispatchers.IO) {
                                val ok = ShizukuHelper.resetResolution()
                                withContext(Dispatchers.Main) {
                                    showSnack(if (ok) "Resolusi direset ke default" else "Gagal reset")
                                    if (ok) loadDisplayValues()
                                }
                            }
                        },
                        onSetDpi = { dpi ->
                            scope.launch(Dispatchers.IO) {
                                val ok = ShizukuHelper.setDpi(dpi)
                                withContext(Dispatchers.Main) {
                                    showSnack(if (ok) "DPI → $dpi" else "Gagal ubah DPI")
                                    if (ok) loadDisplayValues()
                                }
                            }
                        },
                        onResetDpi = {
                            scope.launch(Dispatchers.IO) {
                                val ok = ShizukuHelper.resetDpi()
                                withContext(Dispatchers.Main) {
                                    showSnack(if (ok) "DPI direset ke default" else "Gagal reset DPI")
                                    if (ok) loadDisplayValues()
                                }
                            }
                        }
                    )
                }

                // SEMUA + VISUAL TAB
                if (selectedTab == 0 || selectedTab == 1) {
                    // Stop All card
                    if (selectedTab == 0) {
                        GXCard {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Hentikan Semua Fitur", color = GXText, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                    Text("Semua fitur yang aktif akan segera ditutup", color = GXTextSecondary, fontSize = 12.sp)
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(GXRed)
                                        .clickable { OverlayService.stop(context) }
                                        .padding(horizontal = 16.dp, vertical = 10.dp)
                                ) {
                                    Text("Berhenti", color = GXText, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                }
                            }
                        }
                    }

                    // Kalibrasi Gyro card
                    GXCard {
                        GXSectionHeader(
                            title = "Kalibrasi Gyro",
                            subtitle = "Panduan visual untuk penyelarasan giroskop selama permainan",
                            enabled = gyroEnabled,
                            onToggle = { gyroEnabled = it }
                        )
                        if (gyroEnabled) {
                            Spacer(Modifier.height(12.dp))
                            GXSliderRow("Dot Size", config.sizeDp, 8f..80f, unit = "dp") {
                                updateConfig(config.copy(sizeDp = it))
                            }
                            GXSliderRow("Sensitivity", config.opacity * 100, 10f..100f, unit = "") {
                                updateConfig(config.copy(opacity = it / 100f))
                            }
                            Spacer(Modifier.height(8.dp))
                            Text("Color", color = GXTextSecondary, fontSize = 12.sp)
                            Spacer(Modifier.height(6.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                CROSSHAIR_COLORS_LONG.forEachIndexed { idx, colorLong ->
                                    val isSelected = config.colorArgb == colorLong
                                    Box(
                                        modifier = Modifier
                                            .size(30.dp)
                                            .clip(CircleShape)
                                            .background(CROSSHAIR_COLORS[idx])
                                            .then(if (isSelected) Modifier.border(2.5.dp, GXText, CircleShape) else Modifier)
                                            .clickable { updateConfig(config.copy(colorArgb = colorLong)) }
                                    )
                                }
                            }
                        }
                    }

                    // Crosshair card
                    GXCard {
                        GXSectionHeader(
                            title = "Crosshair",
                            subtitle = "Titik referensi di layar untuk penyelarasan tampilan",
                            enabled = crosshairEnabled,
                            onToggle = { crosshairEnabled = it }
                        )
                        if (crosshairEnabled) {
                            Spacer(Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                // Shape selector (4 bentuk vertikal)
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    CrosshairShape.values().forEach { shape ->
                                        val sel = config.shape == shape
                                        Box(
                                            modifier = Modifier
                                                .size(44.dp)
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(if (sel) GXOrange else GXSurfaceVariant)
                                                .clickable { updateConfig(config.copy(shape = shape)) },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                when (shape) {
                                                    CrosshairShape.PLUS -> "+"
                                                    CrosshairShape.CROSS -> "✚"
                                                    CrosshairShape.DOT -> "•"
                                                    else -> "⊕"
                                                },
                                                color = if (sel) GXText else GXTextSecondary,
                                                fontSize = 18.sp
                                            )
                                        }
                                    }
                                }
                                // Preview crosshair (circle)
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(1f)
                                        .clip(CircleShape)
                                        .background(GXSurfaceVariant),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CrosshairPreview(config = config)
                                    Text("${(config.offsetXDp + config.offsetYDp).toInt() + 181}°", color = GXOrange, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                                // Size slider (vertical)
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Size", color = GXTextSecondary, fontSize = 11.sp)
                                    Text("${config.sizeDp.toInt() / 20 + 1}.0x", color = GXText, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            Spacer(Modifier.height(8.dp))
                            GXSliderRow("Alpha", config.opacity * 100, 0f..100f, unit = "%") {
                                updateConfig(config.copy(opacity = it / 100f))
                            }
                            Spacer(Modifier.height(8.dp))
                            // Color presets (rounded squares)
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                SHAPE_COLORS.forEach { color ->
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(color)
                                            .clickable {
                                                val colorLong = (color.red * 255).toLong().shl(16) or
                                                        (color.green * 255).toLong().shl(8) or
                                                        (color.blue * 255).toLong() or 0xFF000000L
                                                updateConfig(config.copy(colorArgb = colorLong))
                                            }
                                    )
                                }
                            }
                        }
                    }

                    // Monitor Sesi card
                    GXCard {
                        GXSectionHeader(
                            title = "Monitor Sesi",
                            subtitle = "Pantau performa perangkat saat kamu bermain",
                            enabled = monitorEnabled,
                            onToggle = { monitorEnabled = it }
                        )
                        if (monitorEnabled) {
                            Spacer(Modifier.height(12.dp))
                            val items = listOf(
                                "CPU Information" to showCpu,
                                "GPU Information" to showGpu,
                                "RAM Information" to showRam,
                                "Battery Information" to showBattery,
                                "Temperature Information" to showTemp,
                                "FPS Information" to showFps,
                                "Time Information" to showTime,
                            )
                            items.forEach { (label, checked) ->
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(label, color = GXText, fontSize = 14.sp)
                                    GXCheckbox(
                                        checked = checked,
                                        onCheckedChange = {
                                            when (label) {
                                                "CPU Information" -> showCpu = it
                                                "GPU Information" -> showGpu = it
                                                "RAM Information" -> showRam = it
                                                "Battery Information" -> showBattery = it
                                                "Temperature Information" -> showTemp = it
                                                "FPS Information" -> showFps = it
                                                "Time Information" -> showTime = it
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun GXSliderRow(label: String, value: Float, range: ClosedFloatingPointRange<Float>, unit: String, onChange: (Float) -> Unit) {
    Column {
        Text("$label: ${value.toInt()}$unit", color = GXTextSecondary, fontSize = 12.sp)
        Slider(
            value = value,
            onValueChange = onChange,
            valueRange = range,
            colors = SliderDefaults.colors(
                thumbColor = GXOrange,
                activeTrackColor = GXOrange,
                inactiveTrackColor = GXSurfaceVariant
            )
        )
    }
}

@Composable
private fun GXCheckbox(checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Box(
        modifier = Modifier
            .size(24.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(if (checked) GXOrange else GXSurface)
            .border(1.5.dp, if (checked) GXOrange else GXBorder, RoundedCornerShape(6.dp))
            .clickable { onCheckedChange(!checked) },
        contentAlignment = Alignment.Center
    ) {
        if (checked) {
            Icon(Icons.Default.Check, contentDescription = null, tint = GXText, modifier = Modifier.size(16.dp))
        }
    }
}

// === KONFIGURASI TAB - Resolusi & DPI ===
@Composable
private fun KonfigurasiTab(
    shizukuInstalled: Boolean,
    shizukuRunning: Boolean,
    shizukuPermission: Boolean,
    currentRes: Pair<Int, Int>?,
    currentDpi: Int?,
    customWidth: String,
    customHeight: String,
    customDpi: String,
    onCustomWidthChange: (String) -> Unit,
    onCustomHeightChange: (String) -> Unit,
    onCustomDpiChange: (String) -> Unit,
    onInstallShizuku: () -> Unit,
    onRequestPermission: () -> Unit,
    onRefreshShizuku: () -> Unit,
    onRefreshDisplay: () -> Unit,
    onSetResolution: (Int, Int) -> Unit,
    onResetResolution: () -> Unit,
    onSetDpi: (Int) -> Unit,
    onResetDpi: () -> Unit,
) {
    // Status Shizuku card
    GXCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(
                    if (shizukuPermission) Icons.Default.CheckCircle else Icons.Default.Warning,
                    contentDescription = null,
                    tint = if (shizukuPermission) GXGreen else GXOrange,
                    modifier = Modifier.size(20.dp)
                )
                Text("Status Shizuku", color = GXText, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(GXSurfaceVariant)
                    .clickable { onRefreshShizuku() }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(Icons.Default.Refresh, contentDescription = null, tint = GXTextSecondary, modifier = Modifier.size(14.dp))
                    Text("Refresh", color = GXTextSecondary, fontSize = 12.sp)
                }
            }
        }
        Spacer(Modifier.height(10.dp))
        GXStatusRow("Shizuku Terinstall", shizukuInstalled)
        GXStatusRow("Shizuku Berjalan", shizukuRunning)
        GXStatusRow("Izin Diberikan", shizukuPermission)

        if (!shizukuInstalled) {
            Spacer(Modifier.height(8.dp))
            GXButton("Install Shizuku", onClick = onInstallShizuku)
        } else if (!shizukuRunning) {
            Spacer(Modifier.height(8.dp))
            Text("Buka app Shizuku & ikuti instruksi (pairing via WiFi ADB atau USB debugging).", color = GXTextSecondary, fontSize = 12.sp)
        } else if (!shizukuPermission) {
            Spacer(Modifier.height(8.dp))
            GXButton("Izinkan GX Tools", onClick = onRequestPermission)
        }
    }

    if (shizukuPermission) {
        // Status layar saat ini
        GXCard {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Layar Saat Ini", color = GXText, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(GXSurfaceVariant)
                        .clickable { onRefreshDisplay() }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, tint = GXTextSecondary, modifier = Modifier.size(14.dp))
                }
            }
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Resolusi", color = GXTextSecondary, fontSize = 13.sp)
                Text(currentRes?.let { "${it.first}x${it.second}" } ?: "—", color = GXOrange, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
            Spacer(Modifier.height(4.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("DPI", color = GXTextSecondary, fontSize = 13.sp)
                Text(currentDpi?.toString() ?: "—", color = GXOrange, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }

        // Preset Resolusi
        GXCard {
            Text("Preset Resolusi", color = GXText, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Spacer(Modifier.height(10.dp))
            RESOLUTION_PRESETS.forEach { (label, w, h) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(GXSurfaceVariant)
                        .clickable { onSetResolution(w, h) }
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(label, color = GXText, fontSize = 14.sp)
                    Text("${w}x${h}", color = GXOrange, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.height(10.dp))
            Text("Custom Resolusi", color = GXTextSecondary, fontSize = 12.sp)
            Spacer(Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                GXTextField(value = customWidth, onValueChange = onCustomWidthChange, label = "Width", modifier = Modifier.weight(1f))
                GXTextField(value = customHeight, onValueChange = onCustomHeightChange, label = "Height", modifier = Modifier.weight(1f))
            }
            Spacer(Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    modifier = Modifier.weight(1f).clip(RoundedCornerShape(10.dp)).background(GXOrange).clickable {
                        val w = customWidth.toIntOrNull(); val h = customHeight.toIntOrNull()
                        if (w != null && h != null && w >= 360 && h >= 640) onSetResolution(w, h)
                    }.padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) { Text("Terapkan", color = GXText, fontWeight = FontWeight.Bold, fontSize = 13.sp) }
                Box(
                    modifier = Modifier.weight(1f).clip(RoundedCornerShape(10.dp)).background(GXSurfaceVariant).clickable { onResetResolution() }.padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) { Text("Reset Default", color = GXTextSecondary, fontSize = 13.sp) }
            }
        }

        // Preset DPI
        GXCard {
            Text("Preset DPI", color = GXText, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Spacer(Modifier.height(10.dp))
            DPI_PRESETS.forEach { (label, dpi) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(GXSurfaceVariant)
                        .clickable { onSetDpi(dpi) }
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(label, color = GXText, fontSize = 14.sp)
                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = GXOrange, modifier = Modifier.size(18.dp))
                }
            }
            Spacer(Modifier.height(10.dp))
            Text("Custom DPI", color = GXTextSecondary, fontSize = 12.sp)
            Spacer(Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                GXTextField(value = customDpi, onValueChange = onCustomDpiChange, label = "DPI (100–600)", modifier = Modifier.weight(1f))
                Box(
                    modifier = Modifier.clip(RoundedCornerShape(10.dp)).background(GXOrange).clickable {
                        val dpi = customDpi.toIntOrNull()
                        if (dpi != null && dpi in 100..600) onSetDpi(dpi)
                    }.padding(horizontal = 16.dp, vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) { Text("Set", color = GXText, fontWeight = FontWeight.Bold, fontSize = 13.sp) }
            }
            Spacer(Modifier.height(6.dp))
            Box(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(GXSurfaceVariant).clickable { onResetDpi() }.padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) { Text("Reset DPI ke Default", color = GXTextSecondary, fontSize = 13.sp) }

            Spacer(Modifier.height(8.dp))
            Text("⚠ Setelah ubah resolusi/DPI, layar akan berubah. Kalau layar jadi aneh, reset ke default.", color = GXOrange.copy(alpha = 0.8f), fontSize = 11.sp)
        }
    }
}

@Composable
private fun GXStatusRow(label: String, ok: Boolean) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = GXTextSecondary, fontSize = 13.sp)
        Text(if (ok) "✓ Ya" else "✗ Tidak", color = if (ok) GXGreen else GXRed, fontWeight = FontWeight.Bold, fontSize = 13.sp)
    }
}

@Composable
private fun GXButton(text: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(GXOrange).clickable(onClick = onClick).padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) { Text(text, color = GXText, fontWeight = FontWeight.Bold, fontSize = 14.sp) }
}

@Composable
private fun GXTextField(value: String, onValueChange: (String) -> Unit, label: String, modifier: Modifier = Modifier) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = GXTextDim, fontSize = 12.sp) },
        modifier = modifier,
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = GXOrange,
            unfocusedBorderColor = GXBorder,
            focusedTextColor = GXText,
            unfocusedTextColor = GXText,
            cursorColor = GXOrange,
            focusedLabelColor = GXOrange,
        )
    )
}
