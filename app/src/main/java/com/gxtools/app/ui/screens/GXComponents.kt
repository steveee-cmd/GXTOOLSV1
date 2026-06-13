package com.gxtools.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gxtools.app.ui.theme.*

/** Top bar persis seperti screenshot: "By FahrezONE | ID xxxxx" di kanan */
@Composable
fun GXTopBar(title: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            title,
            color = GXText,
            fontWeight = FontWeight.Black,
            fontSize = 28.sp
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("By FahrezONE", color = GXTextSecondary, fontSize = 12.sp)
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(GXOrange.copy(alpha = 0.2f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text("ID d09bdc2b", color = GXOrange, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

/** Card style GX: dark background dengan rounded corners */
@Composable
fun GXCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(GXSurface)
            .padding(16.dp)
    ) {
        Column(content = content)
    }
}

/** Toggle switch style GX: oranye kalau aktif */
@Composable
fun GXSectionHeader(
    title: String,
    subtitle: String,
    enabled: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = GXText, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            if (subtitle.isNotEmpty()) {
                Spacer(Modifier.height(2.dp))
                Text(subtitle, color = GXTextSecondary, fontSize = 12.sp)
            }
        }
        androidx.compose.material3.Switch(
            checked = enabled,
            onCheckedChange = onToggle,
            colors = androidx.compose.material3.SwitchDefaults.colors(
                checkedThumbColor = GXText,
                checkedTrackColor = GXOrange,
                uncheckedThumbColor = GXTextDim,
                uncheckedTrackColor = GXSurfaceVariant
            )
        )
    }
}
