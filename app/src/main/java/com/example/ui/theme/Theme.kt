package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = AccentAmber,
    onPrimary = CharcoalBg,
    primaryContainer = AccentAmberSubtle,
    onPrimaryContainer = AccentAmberLight,
    secondary = TextSecondary,
    onSecondary = CharcoalBg,
    tertiary = MementoCyan,
    onTertiary = CharcoalBg,
    background = CharcoalBg,
    onBackground = TextPrimary,
    surface = CharcoalSurface,
    onSurface = TextPrimary,
    surfaceVariant = CharcoalSurfaceCard,
    onSurfaceVariant = TextSecondary,
    outline = CharcoalBorder,
    outlineVariant = CharcoalBorderSubtle
)

@Composable
fun ContinueTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = AppTypography,
        content = content
    )
}
