package com.gxtools.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val GXColorScheme = darkColorScheme(
    primary            = GXOrange,
    onPrimary          = GXText,
    primaryContainer   = GXOrangeDeep,
    onPrimaryContainer = GXText,
    secondary          = GXOrangeBright,
    onSecondary        = GXText,
    background         = GXBackground,
    onBackground       = GXText,
    surface            = GXSurface,
    onSurface          = GXText,
    surfaceVariant     = GXSurfaceVariant,
    onSurfaceVariant   = GXTextSecondary,
    outline            = GXBorder,
    error              = GXRed,
    onError            = GXText,
)

@Composable
fun GXToolsTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = GXColorScheme,
        typography  = AppTypography,
        content     = content
    )
}
