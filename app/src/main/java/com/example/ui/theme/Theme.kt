package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = AntiqueGoldLight,
    onPrimary = EmeraldDark,
    primaryContainer = DarkCard,
    onPrimaryContainer = SoftIvoryText,
    secondary = ForestGreenAccent,
    onSecondary = SoftIvoryText,
    tertiary = GoldAccentSoft,
    onTertiary = EmeraldDark,
    background = DarkBackground,
    onBackground = SoftIvoryText,
    surface = DarkSurface,
    onSurface = SoftIvoryText,
    surfaceVariant = DarkCard,
    onSurfaceVariant = SoftIvoryMuted,
    outline = DarkCardBorder,
    outlineVariant = DarkCardGlass
)

private val LightColorScheme = lightColorScheme(
    primary = EmeraldPrimary,
    onPrimary = Color.White,
    primaryContainer = WarmIvorySurface,
    onPrimaryContainer = TextPrimaryDark,
    secondary = AntiqueGoldDark,
    onSecondary = Color.White,
    tertiary = ForestGreenAccent,
    onTertiary = Color.White,
    background = WarmIvoryBackground,
    onBackground = TextPrimaryDark,
    surface = WarmIvorySurface,
    onSurface = TextPrimaryDark,
    surfaceVariant = WarmIvoryCard,
    onSurfaceVariant = TextSecondaryDark,
    outline = WarmIvoryCardBorder,
    outlineVariant = Color(0xFFDFD8C8)
)

@Composable
fun DaroodTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
