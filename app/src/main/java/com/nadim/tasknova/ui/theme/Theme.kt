package com.nadim.tasknova.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary        = AriaGreen,
    secondary      = AriaAccent,
    background     = AriaDark,
    surface        = AriaSurface,
    onPrimary      = Color.Black,
    onSecondary    = Color.Black,
    onBackground   = TextPrimary,
    onSurface      = TextPrimary,
    error          = PriorityHigh
)

private val LightColorScheme = lightColorScheme(
    primary        = Color(0xFF00A86B),
    secondary      = Color(0xFF0088AA),
    background     = Color(0xFFF5F5F5),
    surface        = Color(0xFFFFFFFF),
    onPrimary      = Color.White,
    onSecondary    = Color.White,
    onBackground   = Color(0xFF1A1A1A),
    onSurface      = Color(0xFF1A1A1A)
)

@Composable
fun TaskNovaTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = Typography,
        content     = content
    )
}