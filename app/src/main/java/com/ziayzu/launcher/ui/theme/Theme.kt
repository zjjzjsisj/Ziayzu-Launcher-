package com.ziayzu.launcher.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val NebulaInk = darkColorScheme(
    primary = Mint500,
    onPrimary = Ink900,
    primaryContainer = Mint700,
    onPrimaryContainer = Mint300,
    secondary = Coral500,
    onSecondary = Ink900,
    secondaryContainer = Coral700,
    onSecondaryContainer = Coral300,
    tertiary = Violet500,
    onTertiary = Ink900,
    tertiaryContainer = Violet700,
    onTertiaryContainer = Violet300,
    error = DangerRed,
    onError = Ink900,
    background = Ink800,
    onBackground = TextPrimary,
    surface = Ink800,
    onSurface = TextPrimary,
    surfaceVariant = Ink600,
    onSurfaceVariant = TextSecondary,
    outline = LineColor,
    outlineVariant = LineDim
)

@Composable
fun ZiayzuTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = NebulaInk,
        typography = ZiayzuTypography,
        content = content
    )
}
