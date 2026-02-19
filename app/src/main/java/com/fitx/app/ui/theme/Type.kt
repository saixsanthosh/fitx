package com.fitx.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val displayFamily = FontFamily.Serif
private val bodyFamily = FontFamily.SansSerif
private val labelFamily = FontFamily.Monospace

val FitxTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = displayFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 50.sp,
        letterSpacing = (-1).sp
    ),
    displayMedium = TextStyle(
        fontFamily = displayFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 38.sp,
        letterSpacing = (-0.5).sp
    ),
    headlineMedium = TextStyle(
        fontFamily = displayFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 30.sp
    ),
    titleLarge = TextStyle(
        fontFamily = displayFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 23.sp
    ),
    titleMedium = TextStyle(
        fontFamily = bodyFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = bodyFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = bodyFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    ),
    labelLarge = TextStyle(
        fontFamily = labelFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 13.sp
    ),
    labelMedium = TextStyle(
        fontFamily = labelFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp
    )
)

