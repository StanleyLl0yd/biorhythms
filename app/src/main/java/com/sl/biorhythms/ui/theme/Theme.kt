package com.sl.biorhythms.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import com.sl.biorhythms.AppThemeMode

private val LightColorScheme = lightColorScheme(
    primary = OrbitBlue,
    secondary = OrbitTeal,
    tertiary = OrbitRose,
    background = LightBackground,
    surface = LightSurface,
    onPrimary = StarGlow,
    onSecondary = StarGlow,
    onTertiary = StarGlow,
    onBackground = DeepSpace,
    onSurface = DeepSpace,
)

private val DarkColorScheme = darkColorScheme(
    primary = OrbitBlueBright,
    secondary = OrbitTealBright,
    tertiary = OrbitRoseBright,
    background = DeepSpace,
    surface = DeepSurface,
    onPrimary = DeepSpace,
    onSecondary = DeepSpace,
    onTertiary = DeepSpace,
    onBackground = StarGlow,
    onSurface = StarGlow,
)

@Composable
fun BiorhythmsTheme(
    themeMode: AppThemeMode,
    content: @Composable () -> Unit,
) {
    val darkTheme = when (themeMode) {
        AppThemeMode.SYSTEM -> isSystemInDarkTheme()
        AppThemeMode.LIGHT -> false
        AppThemeMode.DARK -> true
    }

    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}