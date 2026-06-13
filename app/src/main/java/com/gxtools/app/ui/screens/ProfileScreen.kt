package com.gxtools.app.ui.screens

import android.os.Build
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gxtools.app.ui.theme.*

@Composable
fun ProfileScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GXBackground)
            .verticalScroll(rememberScrollState())
    ) {
        GXTopBar(title = "Profile")

        Spacer(Modifier.height(12.dp))

        // Level Lingkungan Card
        GXCard(modifier = Modifier.padding(horizontal = 16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Level Lingkungan", color = GXText, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(Modifier.height(4.dp))
                    Text("Berikan izin untuk membuka alat game tingkat lanjut.", color = GXTextSecondary, fontSize = 12.sp)
                }
                Spacer(Modifier.width(16.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(GXOrange.copy(alpha = 0.15f))
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.Top) {
                        Text("0", color = GXOrange, fontWeight = FontWeight.Black, fontSize = 28.sp)
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.ArrowUpward, contentDescription = null, tint = GXOrange, modifier = Modifier.size(13.dp))
                            Text("/2", color = GXTextSecondary, fontSize = 13.sp)
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // Info Sistem Card
        GXCard(modifier = Modifier.padding(horizontal = 16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Info Sistem", color = GXText, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(GXSurfaceVariant)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text("V1.2.7-12750", color = GXTextSecondary, fontSize = 11.sp)
                    }
                    Icon(Icons.Default.Settings, contentDescription = null, tint = GXTextSecondary, modifier = Modifier.size(20.dp))
                }
            }

            Spacer(Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                SysInfoBox(modifier = Modifier.weight(1f), icon = Icons.Default.PhoneAndroid, label = "Model Perangkat", value = Build.MODEL)
                SysInfoBox(modifier = Modifier.weight(1f), icon = Icons.Default.FlashOn, label = "Total RAM", value = getFormattedRam())
            }

            Spacer(Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                SysInfoBox(modifier = Modifier.weight(1f), icon = Icons.Default.Android, label = "Versi Android", value = "${Build.VERSION.RELEASE} (SDK ${Build.VERSION.SDK_INT})")
                SysInfoBox(modifier = Modifier.weight(1f), icon = Icons.Default.TrackChanges, label = "ABI yang Didukung", value = Build.SUPPORTED_ABIS.firstOrNull() ?: "arm64-v8a")
            }
        }

        Spacer(Modifier.height(20.dp))

        Text(
            "DUKUNG KAMI!",
            modifier = Modifier.padding(horizontal = 16.dp),
            color = GXOrange,
            fontWeight = FontWeight.Black,
            fontSize = 12.sp,
            letterSpacing = 1.5.sp
        )

        Spacer(Modifier.height(8.dp))

        GXCard(modifier = Modifier.padding(horizontal = 16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Gabung Saluran Resmi", color = GXText, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Bergabunglah dengan saluran resmi untuk memantau pembaruan, informasi penting, dan pengumuman resmi secara langsung.",
                        color = GXTextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )
                }
                Spacer(Modifier.width(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Icon(Icons.Default.Chat, contentDescription = "WhatsApp", tint = GXOrange, modifier = Modifier.size(28.dp))
                    Icon(Icons.Default.MusicNote, contentDescription = "TikTok", tint = GXOrange, modifier = Modifier.size(28.dp))
                }
            }
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun SysInfoBox(modifier: Modifier = Modifier, icon: ImageVector, label: String, value: String) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(GXBackground)
            .padding(12.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Icon(icon, contentDescription = null, tint = GXOrange, modifier = Modifier.size(20.dp))
            Text(label, color = GXTextSecondary, fontSize = 11.sp)
            Text(value, color = GXText, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
    }
}

private fun getFormattedRam(): String {
    return try {
        val file = java.io.File("/proc/meminfo")
        val line = file.readLines().firstOrNull { it.startsWith("MemTotal:") } ?: return "~4 GB"
        val kb = line.filter { it.isDigit() }.toLongOrNull() ?: return "~4 GB"
        val gb = kb / (1024.0 * 1024.0)
        "%.1f GB".format(gb)
    } catch (e: Exception) {
        "~4 GB"
    }
}
