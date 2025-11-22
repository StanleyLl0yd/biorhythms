package com.sl.biorhythms.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = OrbitBlueBright,
    onPrimary = DeepSpace,
    secondary = OrbitTealBright,
    onSecondary = DeepSpace,
    tertiary = OrbitRoseBright,
    onTertiary = DeepSpace,
    background = DeepSpace,
    onBackground = StarGlow,
    surface = DeepSurface,
    onSurface = StarGlow,
)

private val LightColorScheme = lightColorScheme(
    primary = OrbitBlue,
    onPrimary = Color.White,
    secondary = OrbitTeal,
    onSecondary = Color.White,
    tertiary = OrbitRose,
    onTertiary = Color.White,
    background = LightBackground,
    onBackground = DeepSpace,
    surface = LightSurface,
    onSurface = DeepSpace,
)

@Composable
fun BiorhythmsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
