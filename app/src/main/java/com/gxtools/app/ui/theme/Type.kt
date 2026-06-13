package com.gxtools.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val AppTypography = Typography(
    displayLarge  = TextStyle(fontWeight = FontWeight.Black,  fontSize = 32.sp, letterSpacing = (-0.5).sp),
    titleLarge    = TextStyle(fontWeight = FontWeight.Bold,   fontSize = 24.sp, letterSpacing = 0.sp),
    titleMedium   = TextStyle(fontWeight = FontWeight.Bold,   fontSize = 16.sp, letterSpacing = 0.sp),
    titleSmall    = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 14.sp),
    bodyLarge     = TextStyle(fontWeight = FontWeight.Normal, fontSize = 16.sp),
    bodyMedium    = TextStyle(fontWeight = FontWeight.Normal, fontSize = 14.sp, color = androidx.compose.ui.graphics.Color(0xFFAAAAAA)),
    bodySmall     = TextStyle(fontWeight = FontWeight.Normal, fontSize = 12.sp, color = androidx.compose.ui.graphics.Color(0xFF888888)),
    labelSmall    = TextStyle(fontWeight = FontWeight.Medium, fontSize = 10.sp, letterSpacing = 0.8.sp),
)
