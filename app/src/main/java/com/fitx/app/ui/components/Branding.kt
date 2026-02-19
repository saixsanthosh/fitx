package com.fitx.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun FitxBrandBackground(content: @Composable () -> Unit) {
    val colors = MaterialTheme.colorScheme
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        colors.background,
                        colors.surface,
                        colors.surfaceVariant
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(1400f, 2400f)
                )
            )
    ) {
        content()
    }
}

@Composable
fun AnimatedFitxLogo(
    modifier: Modifier = Modifier,
    showWordmark: Boolean = true,
    logoSize: Dp = 184.dp
) {
    val colors = MaterialTheme.colorScheme
    val transition = rememberInfiniteTransition(label = "fitx_logo")
    val pulse = transition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "logo_pulse"
    )
    val sweep = transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 9000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "logo_sweep"
    )
    val halo = transition.animateFloat(
        initialValue = 0.20f,
        targetValue = 0.65f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2200),
            repeatMode = RepeatMode.Reverse
        ),
        label = "logo_halo"
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(contentAlignment = Alignment.Center) {
            Canvas(modifier = Modifier.size(logoSize)) {
                val center = this.center
                val radius = size.minDimension / 2f

                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            colors.secondary.copy(alpha = halo.value),
                            colors.primary.copy(alpha = 0.05f)
                        )
                    ),
                    radius = radius * pulse.value
                )

                rotate(degrees = sweep.value, pivot = center) {
                    drawArc(
                        brush = Brush.sweepGradient(
                            colors = listOf(
                                colors.primary,
                                colors.tertiary,
                                colors.secondary,
                                colors.primary
                            )
                        ),
                        startAngle = 0f,
                        sweepAngle = 300f,
                        useCenter = false,
                        topLeft = Offset(radius * 0.24f, radius * 0.24f),
                        size = Size(radius * 1.52f, radius * 1.52f),
                        style = Stroke(width = 14f)
                    )
                }

                drawCircle(
                    color = colors.surface,
                    radius = radius * 0.56f
                )

                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            colors.primary.copy(alpha = 0.95f),
                            colors.tertiary.copy(alpha = 0.86f)
                        )
                    ),
                    radius = radius * 0.52f
                )
            }
            Text(
                text = "FX",
                style = MaterialTheme.typography.displayMedium,
                color = colors.onPrimary
            )
        }

        if (showWordmark) {
            Text(
                text = "Fitx",
                style = MaterialTheme.typography.displayMedium,
                color = colors.onBackground
            )
            Text(
                text = "Train smarter every day",
                style = MaterialTheme.typography.bodyLarge,
                color = colors.onBackground.copy(alpha = 0.72f)
            )
        }
    }
}

@Composable
fun FitxSplash(onFinished: () -> Unit) {
    var show by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        show = true
        delay(1800L)
        onFinished()
    }

    FitxBrandBackground {
        AnimatedVisibility(
            visible = show,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                AnimatedFitxLogo()
            }
        }
    }
}
