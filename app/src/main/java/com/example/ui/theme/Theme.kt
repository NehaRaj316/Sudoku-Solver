package com.example.ui.theme

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
    primary = IndigoPrimaryDark,
    onPrimary = IndigoOnPrimaryDark,
    primaryContainer = IndigoPrimaryContainerDark,
    onPrimaryContainer = IndigoOnPrimaryContainerDark,
    secondary = CyanSecondaryDark,
    onSecondary = Color.Black,
    secondaryContainer = CyanSecondaryContainerDark,
    onSecondaryContainer = CyanOnSecondaryContainerDark,
    tertiary = AmberTertiaryDark,
    onTertiary = Color.Black,
    tertiaryContainer = AmberTertiaryContainerDark,
    onTertiaryContainer = AmberOnTertiaryContainerDark,
    background = BackgroundDark,
    surface = SurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    outline = OutlineDark,
    error = Color(0xFFF87171)
)

private val LightColorScheme = lightColorScheme(
    primary = IndigoPrimaryLight,
    onPrimary = IndigoOnPrimaryLight,
    primaryContainer = IndigoPrimaryContainerLight,
    onPrimaryContainer = IndigoOnPrimaryContainerLight,
    secondary = CyanSecondaryLight,
    onSecondary = Color.White,
    secondaryContainer = CyanSecondaryContainerLight,
    onSecondaryContainer = CyanOnSecondaryContainerLight,
    tertiary = AmberTertiaryLight,
    onTertiary = Color.White,
    tertiaryContainer = AmberTertiaryContainerLight,
    onTertiaryContainer = AmberOnTertiaryContainerLight,
    background = BackgroundLight,
    surface = SurfaceLight,
    surfaceVariant = SurfaceVariantLight,
    outline = OutlineLight,
    error = Color(0xFFDC2626)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Set false to ensure our custom Sudoku indigo branding is preserved
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
