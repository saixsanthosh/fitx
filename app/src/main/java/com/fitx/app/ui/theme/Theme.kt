package com.fitx.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

private val LightColors = lightColorScheme(
    primary = PrimaryLight,
    onPrimary = OnPrimaryLight,
    primaryContainer = AccentContainerLight,
    onPrimaryContainer = OnPrimaryLight,
    secondary = SecondaryLight,
    onSecondary = OnSecondaryLight,
    secondaryContainer = AccentContainerLight,
    onSecondaryContainer = PrimaryLight,
    tertiary = TertiaryLight,
    onTertiary = OnTertiaryLight,
    tertiaryContainer = AccentContainerLight,
    onTertiaryContainer = PrimaryLight,
    surface = SurfaceLight,
    surfaceVariant = SurfaceVariantLight,
    background = BackgroundLight,
    onBackground = OnBackgroundLight,
    onSurface = OnSurfaceLight,
    onSurfaceVariant = OnSurfaceVariantLight,
    outline = OutlineLight,
    surfaceTint = PrimaryLight,
    error = ErrorLight,
    onError = OnErrorLight
)

private val DarkColors = darkColorScheme(
    primary = PrimaryDark,
    onPrimary = OnPrimaryDark,
    primaryContainer = AccentContainerDark,
    onPrimaryContainer = OnPrimaryDark,
    secondary = SecondaryDark,
    onSecondary = OnSecondaryDark,
    secondaryContainer = AccentContainerDark,
    onSecondaryContainer = PrimaryDark,
    tertiary = TertiaryDark,
    onTertiary = OnTertiaryDark,
    tertiaryContainer = AccentContainerDark,
    onTertiaryContainer = PrimaryDark,
    surface = SurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    background = BackgroundDark,
    onBackground = OnBackgroundDark,
    onSurface = OnSurfaceDark,
    onSurfaceVariant = OnSurfaceVariantDark,
    outline = OutlineDark,
    surfaceTint = PrimaryDark,
    error = ErrorDark,
    onError = OnErrorDark
)

private val FitxShapes = Shapes(
    small = RoundedCornerShape(14.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(20.dp)
)

@Composable
fun FitxTheme(
    darkTheme: Boolean,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = FitxTypography,
        shapes = FitxShapes,
        content = content
    )
}
