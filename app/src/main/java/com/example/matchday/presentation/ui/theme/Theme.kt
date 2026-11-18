package com.example.matchday.presentation.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = MatchdayGreen,
    onPrimary = MatchdayOnGreen,
    primaryContainer = MatchdayGreenDark,
    onPrimaryContainer = MatchdayText,
    secondary = MatchdayTextSecondary,
    onSecondary = MatchdayBackground,
    secondaryContainer = MatchdaySurfaceVariant,
    onSecondaryContainer = MatchdayText,
    tertiary = MatchdayGreen,
    onTertiary = MatchdayOnGreen,
    tertiaryContainer = MatchdayGreenDark,
    onTertiaryContainer = MatchdayText,
    background = MatchdayBackground,
    onBackground = MatchdayText,
    surface = MatchdaySurface,
    onSurface = MatchdayText,
    surfaceVariant = MatchdaySurfaceVariant,
    onSurfaceVariant = MatchdayTextSecondary,
    surfaceTint = MatchdayGreen,
    inverseSurface = MatchdayText,
    inverseOnSurface = MatchdayBackground,
    inversePrimary = MatchdayGreenDark,
    outline = MatchdayTextSecondary,
    outlineVariant = MatchdaySurfaceVariant,
    surfaceDim = MatchdayBackground,
    surfaceBright = MatchdaySurfaceVariant,
    surfaceContainerLowest = MatchdayBackground,
    surfaceContainerLow = MatchdaySurface,
    surfaceContainer = MatchdaySurface,
    surfaceContainerHigh = MatchdaySurfaceVariant,
    surfaceContainerHighest = MatchdaySurfaceVariant
)

// One fixed dark theme keeps all screens consistent on every device.
@Composable
fun MatchdayTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
