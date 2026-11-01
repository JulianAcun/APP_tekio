package com.tekio.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val TekioRed = Color(0xFFE50914)
val TekioRedDark = Color(0xFF8B0F14)
val TekioBackground = Color(0xFF0B0D10)
val TekioSurface = Color(0xFF14171B)
val TekioSurfaceVariant = Color(0xFF1C2026)
val TekioOnSurface = Color(0xFFF2F2F2)
val TekioMuted = Color(0xFF9AA0A6)
val TekioAmber = Color(0xFFF5A300)
val TekioGrayChip = Color(0xFF3A3F47)

private val TekioColors = darkColorScheme(
    primary = TekioRed,
    onPrimary = Color.White,
    background = TekioBackground,
    onBackground = TekioOnSurface,
    surface = TekioSurface,
    onSurface = TekioOnSurface,
    surfaceVariant = TekioSurfaceVariant,
    onSurfaceVariant = TekioMuted,
    outline = TekioRedDark
)

@Composable
fun TekioTheme(content: @Composable () -> Unit) {
    // La app usa siempre el tema oscuro rojo del diseño.
    MaterialTheme(colorScheme = TekioColors, content = content)
}
