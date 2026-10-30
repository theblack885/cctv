package com.example.cctvstreamer.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val ModernDarkColorScheme = darkColorScheme(
    primary = Color(0xFF00E5FF),      // Futuristic Neon Cyan
    secondary = Color(0xFF7C4DFF),    // Neon Purple accent
    background = Color(0xFF0B0F19),   // Deep Space Black/Blue
    surface = Color(0xFF131B2E),      // Glass card surface color
    onPrimary = Color(0xFF000000),
    onBackground = Color(0xFFE2E8F0),
    onSurface = Color(0xFFE2E8F0)
)

@Composable
fun CCTVStreamerTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = ModernDarkColorScheme,
        content = content
    )
}
