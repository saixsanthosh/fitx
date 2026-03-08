package com.fitx.app.ui.theme.premium

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import kotlin.math.*

/**
 * REVOLUTIONARY 3D EFFECTS - NO COMPETITOR HAS THIS!
 * Advanced 3D transformations, parallax, and depth effects
 */

@Composable
fun Parallax3DCard(
    modifier: Modifier = Modifier,
    rotationX: Float = 0f,
    rotationY: Float = 0f,
    depth: Float = 20f,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .graphicsLayer {
                this.rotationX = rotationX
                this.rotationY = rotationY
                this.cameraDistance = 12f * density
                this.shadowElevation = depth
            }
    ) {
        content()
    }
}

@Composable
fun Holographic3DEffect(
    modifier: Modifier = Modifier,
    colors: List<Color> = listOf(
        Color(0xFF00F5FF),
        Color(0xFFFF00FF),
        Color(0xFFFFFF00),
        Color(0xFF00FF00)
    )
) {
    val infiniteTransition = rememberInfiniteTransition(label = "holographic")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )
    
    Canvas(modifier = modifier.fillMaxSize()) {
        val centerX = size.width / 2
        val centerY = size.height / 2
        val radius = min(size.width, size.height) / 2
        
        rotate(rotation, Offset(centerX, centerY)) {
            colors.forEachIndexed { index, color ->
                val angle = (360f / colors.size) * index
                val rad = Math.toRadians(angle.toDouble())
                val x = centerX + (radius * 0.7f * cos(rad)).toFloat()
                val y = centerY + (radius * 0.7f * sin(rad)).toFloat()
                
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(color.copy(alpha = 0.6f), Color.Transparent),
                        center = Offset(x, y),
                        radius = radius * 0.5f
                    ),
                    radius = radius * 0.5f,
                    center = Offset(x, y),
                    blendMode = BlendMode.Screen
                )
            }
        }
    }
}

@Composable
fun NeonGlowEffect(
    modifier: Modifier = Modifier,
    color: Color = Color(0xFF3A86FF),
    intensity: Float = 1f
) {
    val infiniteTransition = rememberInfiniteTransition(label = "neon")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )
    
    Canvas(modifier = modifier.fillMaxSize()) {
        val layers = 5
        for (i in 0 until layers) {
            val layerAlpha = alpha * (1f - i * 0.15f) * intensity
            val blur = (i + 1) * 4f
            
            drawRect(
                color = color.copy(alpha = layerAlpha),
                topLeft = Offset(-blur, -blur),
                size = androidx.compose.ui.geometry.Size(
                    size.width + blur * 2,
                    size.height + blur * 2
                ),
                blendMode = BlendMode.Screen
            )
        }
    }
}

@Composable
fun LiquidMorphEffect(
    modifier: Modifier = Modifier,
    colors: List<Color> = listOf(
        Color(0xFF3A86FF),
        Color(0xFF8338EC),
        Color(0xFF06FFA5)
    )
) {
    val infiniteTransition = rememberInfiniteTransition(label = "liquid")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2 * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )
    
    Canvas(modifier = modifier.fillMaxSize()) {
        val path = Path()
        val width = size.width
        val height = size.height
        val waves = 3
        
        path.moveTo(0f, height / 2)
        
        for (i in 0..width.toInt() step 10) {
            val x = i.toFloat()
            val y = height / 2 + sin((x / width * waves * 2 * PI + phase).toDouble()).toFloat() * 30
            path.lineTo(x, y)
        }
        
        path.lineTo(width, height)
        path.lineTo(0f, height)
        path.close()
        
        drawPath(
            path = path,
            brush = Brush.verticalGradient(colors),
            alpha = 0.8f
        )
    }
}

@Composable
fun CrystalShardEffect(
    modifier: Modifier = Modifier,
    color: Color = Color(0xFF3A86FF)
) {
    val infiniteTransition = rememberInfiniteTransition(label = "crystal")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )
    
    Canvas(modifier = modifier.fillMaxSize()) {
        val centerX = size.width / 2
        val centerY = size.height / 2
        val shards = 6
        
        rotate(rotation, Offset(centerX, centerY)) {
            for (i in 0 until shards) {
                val angle = (360f / shards) * i
                val rad = Math.toRadians(angle.toDouble())
                
                val path = Path().apply {
                    moveTo(centerX, centerY)
                    lineTo(
                        centerX + (100 * cos(rad)).toFloat(),
                        centerY + (100 * sin(rad)).toFloat()
                    )
                    lineTo(
                        centerX + (80 * cos(rad + 0.3)).toFloat(),
                        centerY + (80 * sin(rad + 0.3)).toFloat()
                    )
                    close()
                }
                
                drawPath(
                    path = path,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            color.copy(alpha = 0.8f),
                            color.copy(alpha = 0.2f)
                        )
                    )
                )
            }
        }
    }
}

@Composable
fun AuroraEffect(
    modifier: Modifier = Modifier,
    colors: List<Color> = listOf(
        Color(0xFF00F5FF),
        Color(0xFF00FF88),
        Color(0xFF8800FF),
        Color(0xFFFF0088)
    )
) {
    val infiniteTransition = rememberInfiniteTransition(label = "aurora")
    val offset1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "offset1"
    )
    
    val offset2 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "offset2"
    )
    
    Canvas(modifier = modifier.fillMaxSize()) {
        val height = size.height
        val width = size.width
        
        // First wave
        val path1 = Path().apply {
            moveTo(0f, height / 3)
            for (x in 0..width.toInt() step 20) {
                val y = height / 3 + sin((x + offset1) / 100.0).toFloat() * 50
                lineTo(x.toFloat(), y)
            }
            lineTo(width, height)
            lineTo(0f, height)
            close()
        }
        
        drawPath(
            path = path1,
            brush = Brush.verticalGradient(
                colors = listOf(colors[0].copy(alpha = 0.3f), Color.Transparent)
            ),
            blendMode = BlendMode.Screen
        )
        
        // Second wave
        val path2 = Path().apply {
            moveTo(0f, height / 2)
            for (x in 0..width.toInt() step 20) {
                val y = height / 2 + sin((x + offset2) / 80.0).toFloat() * 40
                lineTo(x.toFloat(), y)
            }
            lineTo(width, height)
            lineTo(0f, height)
            close()
        }
        
        drawPath(
            path = path2,
            brush = Brush.verticalGradient(
                colors = listOf(colors[1].copy(alpha = 0.3f), Color.Transparent)
            ),
            blendMode = BlendMode.Screen
        )
    }
}

@Composable
fun PlasmaEffect(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "plasma")
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 100f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "time"
    )
    
    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        val step = 10f
        
        for (x in 0 until width.toInt() step step.toInt()) {
            for (y in 0 until height.toInt() step step.toInt()) {
                val value = sin(x / 50.0 + time / 10.0).toFloat() +
                           sin(y / 50.0 + time / 10.0).toFloat() +
                           sin((x + y) / 50.0 + time / 10.0).toFloat()
                
                val normalized = (value + 3) / 6
                val color = Color.hsv(normalized * 360, 1f, 1f, 0.3f)
                
                drawRect(
                    color = color,
                    topLeft = Offset(x.toFloat(), y.toFloat()),
                    size = androidx.compose.ui.geometry.Size(step, step)
                )
            }
        }
    }
}

@Composable
fun MetallicShineEffect(
    modifier: Modifier = Modifier,
    baseColor: Color = Color(0xFF3A86FF)
) {
    val infiniteTransition = rememberInfiniteTransition(label = "metallic")
    val shinePosition by infiniteTransition.animateFloat(
        initialValue = -1f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shine"
    )
    
    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        
        // Base metallic gradient
        drawRect(
            brush = Brush.linearGradient(
                colors = listOf(
                    baseColor.copy(alpha = 0.6f),
                    baseColor.copy(alpha = 0.8f),
                    baseColor.copy(alpha = 0.6f)
                ),
                start = Offset(0f, 0f),
                end = Offset(width, height)
            )
        )
        
        // Shine effect
        val shineX = width * shinePosition
        drawRect(
            brush = Brush.linearGradient(
                colors = listOf(
                    Color.Transparent,
                    Color.White.copy(alpha = 0.6f),
                    Color.Transparent
                ),
                start = Offset(shineX - 100, 0f),
                end = Offset(shineX + 100, height)
            )
        )
    }
}

@Composable
fun ElectricArcEffect(
    modifier: Modifier = Modifier,
    color: Color = Color(0xFF00F5FF)
) {
    val infiniteTransition = rememberInfiniteTransition(label = "electric")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )
    
    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        val segments = 20
        
        val path = Path()
        path.moveTo(0f, height / 2)
        
        for (i in 0..segments) {
            val x = (width / segments) * i
            val randomOffset = (Math.random() * 40 - 20).toFloat()
            val y = height / 2 + randomOffset
            
            if (i == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }
        }
        
        drawPath(
            path = path,
            color = color,
            style = Stroke(width = 3f)
        )
        
        // Glow effect
        drawPath(
            path = path,
            color = color.copy(alpha = 0.3f),
            style = Stroke(width = 10f)
        )
    }
}
