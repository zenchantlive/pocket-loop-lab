package com.pocketlooplab.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkScheme = darkColorScheme(
    primary = Color(0xFF5BE6C7),
    secondary = Color(0xFFFFD166),
    tertiary = Color(0xFFFF5A66),
    background = Color(0xFF0D1117),
    surface = Color(0xFF151B24),
    onPrimary = Color(0xFF06231E),
    onSecondary = Color(0xFF2B2100),
    onTertiary = Color(0xFF2B070A),
    onBackground = Color(0xFFEAF2F5),
    onSurface = Color(0xFFEAF2F5)
)

@Composable
fun PocketLoopLabTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkScheme,
        content = content
    )
}
