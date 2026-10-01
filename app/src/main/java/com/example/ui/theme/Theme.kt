package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val NVpnDarkColorScheme = darkColorScheme(
    primary = CyberEmerald,
    onPrimary = ObsidianBackground,
    primaryContainer = SlateCardElevated,
    onPrimaryContainer = CyberEmerald,
    secondary = ElectricCyan,
    onSecondary = ObsidianBackground,
    secondaryContainer = SlateCardSurface,
    onSecondaryContainer = ElectricCyan,
    tertiary = VipGold,
    onTertiary = ObsidianBackground,
    background = ObsidianBackground,
    onBackground = TextPrimaryWhite,
    surface = ObsidianSurface,
    onSurface = TextPrimaryWhite,
    surfaceVariant = SlateCardSurface,
    onSurfaceVariant = TextSecondarySlate,
    error = AlertCrimson,
    onError = Color.White,
    outline = GlassBorderColor
)

@Composable
fun NVpnTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = NVpnDarkColorScheme,
        typography = Typography,
        content = content
    )
}
