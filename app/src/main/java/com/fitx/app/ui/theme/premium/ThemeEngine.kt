package com.fitx.app.ui.theme.premium

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color

/**
 * Premium Theme Engine with Dynamic Color System
 * Supports unlimited custom themes with smooth transitions
 */

@Stable
data class PremiumTheme(
    val id: String,
    val name: String,
    val primaryColor: Color,
    val secondaryColor: Color,
    val accentColor: Color,
    val gradientStart: Color,
    val gradientEnd: Color,
    val surfaceGlow: Color
)

object PremiumThemes {
    val ElectricBlue = PremiumTheme(
        id = "electric_blue",
        name = "Electric Blue",
        primaryColor = Color(0xFF3A86FF),
        secondaryColor = Color(0xFF8338EC),
        accentColor = Color(0xFF06FFA5),
        gradientStart = Color(0xFF3A86FF),
        gradientEnd = Color(0xFF8338EC),
        surfaceGlow = Color(0xFF3A86FF).copy(alpha = 0.15f)
    )
    
    val NeonPurple = PremiumTheme(
        id = "neon_purple",
        name = "Neon Purple",
        primaryColor = Color(0xFF8B5CF6),
        secondaryColor = Color(0xFFEC4899),
        accentColor = Color(0xFFFBBF24),
        gradientStart = Color(0xFF8B5CF6),
        gradientEnd = Color(0xFFEC4899),
        surfaceGlow = Color(0xFF8B5CF6).copy(alpha = 0.15f)
    )
    
    val CyberGreen = PremiumTheme(
        id = "cyber_green",
        name = "Cyber Green",
        primaryColor = Color(0xFF10B981),
        secondaryColor = Color(0xFF06B6D4),
        accentColor = Color(0xFFF59E0B),
        gradientStart = Color(0xFF10B981),
        gradientEnd = Color(0xFF06B6D4),
        surfaceGlow = Color(0xFF10B981).copy(alpha = 0.15f)
    )
    
    val SunsetOrange = PremiumTheme(
        id = "sunset_orange",
        name = "Sunset Orange",
        primaryColor = Color(0xFFF97316),
        secondaryColor = Color(0xFFEF4444),
        accentColor = Color(0xFFFBBF24),
        gradientStart = Color(0xFFF97316),
        gradientEnd = Color(0xFFEF4444),
        surfaceGlow = Color(0xFFF97316).copy(alpha = 0.15f)
    )
    
    val OceanBlue = PremiumTheme(
        id = "ocean_blue",
        name = "Ocean Blue",
        primaryColor = Color(0xFF0EA5E9),
        secondaryColor = Color(0xFF3B82F6),
        accentColor = Color(0xFF06B6D4),
        gradientStart = Color(0xFF0EA5E9),
        gradientEnd = Color(0xFF3B82F6),
        surfaceGlow = Color(0xFF0EA5E9).copy(alpha = 0.15f)
    )
    
    val RoseGold = PremiumTheme(
        id = "rose_gold",
        name = "Rose Gold",
        primaryColor = Color(0xFFF43F5E),
        secondaryColor = Color(0xFFEC4899),
        accentColor = Color(0xFFFBBF24),
        gradientStart = Color(0xFFF43F5E),
        gradientEnd = Color(0xFFEC4899),
        surfaceGlow = Color(0xFFF43F5E).copy(alpha = 0.15f)
    )
    
    val MidnightBlue = PremiumTheme(
        id = "midnight_blue",
        name = "Midnight Blue",
        primaryColor = Color(0xFF1E40AF),
        secondaryColor = Color(0xFF6366F1),
        accentColor = Color(0xFF06B6D4),
        gradientStart = Color(0xFF1E40AF),
        gradientEnd = Color(0xFF6366F1),
        surfaceGlow = Color(0xFF1E40AF).copy(alpha = 0.15f)
    )
    
    val LavaRed = PremiumTheme(
        id = "lava_red",
        name = "Lava Red",
        primaryColor = Color(0xFFDC2626),
        secondaryColor = Color(0xFFF97316),
        accentColor = Color(0xFFFBBF24),
        gradientStart = Color(0xFFDC2626),
        gradientEnd = Color(0xFFF97316),
        surfaceGlow = Color(0xFFDC2626).copy(alpha = 0.15f)
    )
    
    val ForestGreen = PremiumTheme(
        id = "forest_green",
        name = "Forest Green",
        primaryColor = Color(0xFF059669),
        secondaryColor = Color(0xFF10B981),
        accentColor = Color(0xFF84CC16),
        gradientStart = Color(0xFF059669),
        gradientEnd = Color(0xFF10B981),
        surfaceGlow = Color(0xFF059669).copy(alpha = 0.15f)
    )
    
    val GalaxyPurple = PremiumTheme(
        id = "galaxy_purple",
        name = "Galaxy Purple",
        primaryColor = Color(0xFF7C3AED),
        secondaryColor = Color(0xFFA855F7),
        accentColor = Color(0xFFEC4899),
        gradientStart = Color(0xFF7C3AED),
        gradientEnd = Color(0xFFA855F7),
        surfaceGlow = Color(0xFF7C3AED).copy(alpha = 0.15f)
    )
    
    val AllThemes = listOf(
        ElectricBlue,
        NeonPurple,
        CyberGreen,
        SunsetOrange,
        OceanBlue,
        RoseGold,
        MidnightBlue,
        LavaRed,
        ForestGreen,
        GalaxyPurple
    )
}

@Composable
fun animatedColorScheme(
    theme: PremiumTheme,
    isDark: Boolean
): ColorScheme {
    val primary by animateColorAsState(
        targetValue = theme.primaryColor,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "primary"
    )
    
    val secondary by animateColorAsState(
        targetValue = theme.secondaryColor,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "secondary"
    )
    
    val accent by animateColorAsState(
        targetValue = theme.accentColor,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "accent"
    )
    
    return if (isDark) {
        darkColorScheme(
            primary = primary,
            secondary = secondary,
            tertiary = accent,
            background = Color(0xFF0A0A0A),
            surface = Color(0xFF121212),
            surfaceVariant = Color(0xFF1E1E1E),
            onPrimary = Color.White,
            onSecondary = Color.White,
            onBackground = Color.White,
            onSurface = Color.White
        )
    } else {
        lightColorScheme(
            primary = primary,
            secondary = secondary,
            tertiary = accent,
            background = Color(0xFFFAFAFA),
            surface = Color.White,
            surfaceVariant = Color(0xFFF5F5F5),
            onPrimary = Color.White,
            onSecondary = Color.White,
            onBackground = Color(0xFF1A1A1A),
            onSurface = Color(0xFF1A1A1A)
        )
    }
}
