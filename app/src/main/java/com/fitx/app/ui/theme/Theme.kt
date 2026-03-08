package com.fitx.app.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Fitx Dark Color Scheme (Default)
private val FitxDarkColorScheme = darkColorScheme(
    primary = Color(0xFF3A86FF),
    onPrimary = Color.White,
    primaryContainer = Color(0xFF2A5FBF),
    onPrimaryContainer = Color(0xFFD4E3FF),
    
    secondary = Color(0xFF8338EC),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF6328BC),
    onSecondaryContainer = Color(0xFFE8D4FF),
    
    tertiary = Color(0xFF06FFA5),
    onTertiary = Color.Black,
    tertiaryContainer = Color(0xFF05CC84),
    onTertiaryContainer = Color(0xFFD4FFF0),
    
    error = Color(0xFFFF006E),
    onError = Color.White,
    errorContainer = Color(0xFFCC0058),
    onErrorContainer = Color(0xFFFFD4E3),
    
    background = Color(0xFF121212),
    onBackground = Color(0xFFE8E8E8),
    
    surface = Color(0xFF1E1E1E),
    onSurface = Color(0xFFE8E8E8),
    surfaceVariant = Color(0xFF2A2A2A),
    onSurfaceVariant = Color(0xFFD0D0D0),
    
    outline = Color(0xFF3A3A3A),
    outlineVariant = Color(0xFF2A2A2A),
    
    scrim = Color(0xFF000000),
    
    inverseSurface = Color(0xFFE8E8E8),
    inverseOnSurface = Color(0xFF1E1E1E),
    inversePrimary = Color(0xFF3A86FF),
    
    surfaceTint = Color(0xFF3A86FF)
)

// Fitx Light Color Scheme (Optional)
private val FitxLightColorScheme = lightColorScheme(
    primary = Color(0xFF3A86FF),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD4E3FF),
    onPrimaryContainer = Color(0xFF001C3A),
    
    secondary = Color(0xFF8338EC),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE8D4FF),
    onSecondaryContainer = Color(0xFF2A0052),
    
    tertiary = Color(0xFF06FFA5),
    onTertiary = Color.Black,
    tertiaryContainer = Color(0xFFD4FFF0),
    onTertiaryContainer = Color(0xFF003826),
    
    error = Color(0xFFFF006E),
    onError = Color.White,
    errorContainer = Color(0xFFFFD4E3),
    onErrorContainer = Color(0xFF410011),
    
    background = Color(0xFFFAFAFA),
    onBackground = Color(0xFF1A1A1A),
    
    surface = Color.White,
    onSurface = Color(0xFF1A1A1A),
    surfaceVariant = Color(0xFFF0F0F0),
    onSurfaceVariant = Color(0xFF444444),
    
    outline = Color(0xFFD0D0D0),
    outlineVariant = Color(0xFFE8E8E8),
    
    scrim = Color(0xFF000000),
    
    inverseSurface = Color(0xFF2A2A2A),
    inverseOnSurface = Color(0xFFF0F0F0),
    inversePrimary = Color(0xFF8AB4FF),
    
    surfaceTint = Color(0xFF3A86FF)
)

@Composable
fun FitxTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> FitxDarkColorScheme
        else -> FitxLightColorScheme
    }
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.surface.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }
    
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
