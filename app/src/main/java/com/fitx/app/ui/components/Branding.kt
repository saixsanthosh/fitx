package com.fitx.app.ui.components

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun FitxBrandBackground(content: @Composable () -> Unit) {
    val colors = MaterialTheme.colorScheme
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = colors.primary.copy(alpha = 0.06f),
                radius = size.minDimension * 0.34f,
                center = Offset(x = size.width * 0.92f, y = size.height * 0.1f)
            )
            drawCircle(
                color = colors.primary.copy(alpha = 0.04f),
                radius = size.minDimension * 0.42f,
                center = Offset(x = size.width * 0.05f, y = size.height * 0.88f)
            )
            drawCircle(
                color = colors.surfaceVariant.copy(alpha = 0.35f),
                radius = size.minDimension * 0.2f,
                center = Offset(x = size.width * 0.78f, y = size.height * 0.8f)
            )
        }
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
        initialValue = 0.985f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2400),
            repeatMode = RepeatMode.Reverse
        ),
        label = "logo_pulse"
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(contentAlignment = Alignment.Center) {
            Canvas(modifier = Modifier.size(logoSize * pulse.value)) {
                val radius = size.minDimension / 2f
                drawCircle(
                    color = colors.surfaceVariant,
                    radius = radius
                )
                drawCircle(
                    color = colors.primary.copy(alpha = 0.16f),
                    radius = radius * 0.9f
                )
                drawCircle(
                    color = colors.primary,
                    radius = radius * 0.64f,
                    style = Stroke(width = 10f)
                )
            }
            Text(
                text = "FX",
                style = MaterialTheme.typography.displayMedium,
                color = colors.primary
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
        delay(1600L)
        onFinished()
    }

    FitxBrandBackground {
        AnimatedVisibility(
            visible = show,
            enter = fadeIn(animationSpec = tween(260)),
            exit = fadeOut(animationSpec = tween(220))
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                AnimatedFitxLogo()
            }
        }
    }
}
