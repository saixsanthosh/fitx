package com.fitx.app.ui.theme.premium

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/**
 * Premium Particle System
 * Creates stunning particle effects for celebrations and achievements
 */

data class Particle(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    var life: Float,
    var maxLife: Float,
    var size: Float,
    var color: Color,
    var rotation: Float = 0f,
    var rotationSpeed: Float = 0f
)

@Composable
fun ParticleExplosion(
    modifier: Modifier = Modifier,
    trigger: Boolean,
    colors: List<Color> = listOf(
        Color(0xFFFFD700),
        Color(0xFFFF6B6B),
        Color(0xFF4ECDC4),
        Color(0xFF45B7D1),
        Color(0xFFFFA07A)
    ),
    particleCount: Int = 50,
    onComplete: () -> Unit = {}
) {
    var particles by remember { mutableStateOf<List<Particle>>(emptyList()) }
    var isAnimating by remember { mutableStateOf(false) }
    
    LaunchedEffect(trigger) {
        if (trigger && !isAnimating) {
            isAnimating = true
            particles = List(particleCount) {
                val angle = Random.nextFloat() * 2 * Math.PI.toFloat()
                val speed = Random.nextFloat() * 5f + 3f
                Particle(
                    x = 0.5f,
                    y = 0.5f,
                    vx = cos(angle) * speed,
                    vy = sin(angle) * speed,
                    life = 1f,
                    maxLife = Random.nextFloat() * 0.5f + 0.5f,
                    size = Random.nextFloat() * 8f + 4f,
                    color = colors.random(),
                    rotation = Random.nextFloat() * 360f,
                    rotationSpeed = Random.nextFloat() * 10f - 5f
                )
            }
            
            while (particles.any { it.life > 0 }) {
                delay(16) // ~60 FPS
                particles = particles.map { particle ->
                    particle.copy(
                        x = particle.x + particle.vx * 0.016f,
                        y = particle.y + particle.vy * 0.016f,
                        vy = particle.vy + 0.2f, // Gravity
                        life = (particle.life - 0.016f / particle.maxLife).coerceAtLeast(0f),
                        rotation = particle.rotation + particle.rotationSpeed
                    )
                }
            }
            
            particles = emptyList()
            isAnimating = false
            onComplete()
        }
    }
    
    Canvas(modifier = modifier.fillMaxSize()) {
        particles.forEach { particle ->
            val alpha = particle.life
            drawCircle(
                color = particle.color.copy(alpha = alpha),
                radius = particle.size,
                center = Offset(
                    x = particle.x * size.width,
                    y = particle.y * size.height
                )
            )
        }
    }
}

@Composable
fun ConfettiEffect(
    modifier: Modifier = Modifier,
    isActive: Boolean,
    colors: List<Color> = listOf(
        Color(0xFFFFD700),
        Color(0xFFFF6B6B),
        Color(0xFF4ECDC4),
        Color(0xFF45B7D1),
        Color(0xFFFFA07A),
        Color(0xFFFF69B4)
    )
) {
    var particles by remember { mutableStateOf<List<Particle>>(emptyList()) }
    
    LaunchedEffect(isActive) {
        if (isActive) {
            particles = List(100) {
                Particle(
                    x = Random.nextFloat(),
                    y = -0.1f,
                    vx = Random.nextFloat() * 2f - 1f,
                    vy = Random.nextFloat() * 3f + 2f,
                    life = 1f,
                    maxLife = Random.nextFloat() * 2f + 2f,
                    size = Random.nextFloat() * 6f + 3f,
                    color = colors.random(),
                    rotation = Random.nextFloat() * 360f,
                    rotationSpeed = Random.nextFloat() * 20f - 10f
                )
            }
            
            while (isActive && particles.any { it.life > 0 }) {
                delay(16)
                particles = particles.map { particle ->
                    particle.copy(
                        x = particle.x + particle.vx * 0.01f,
                        y = particle.y + particle.vy * 0.01f,
                        life = if (particle.y > 1.1f) 0f else (particle.life - 0.016f / particle.maxLife).coerceAtLeast(0f),
                        rotation = particle.rotation + particle.rotationSpeed
                    )
                }
                
                // Add new particles from top
                if (Random.nextFloat() < 0.3f) {
                    particles = particles + Particle(
                        x = Random.nextFloat(),
                        y = -0.1f,
                        vx = Random.nextFloat() * 2f - 1f,
                        vy = Random.nextFloat() * 3f + 2f,
                        life = 1f,
                        maxLife = Random.nextFloat() * 2f + 2f,
                        size = Random.nextFloat() * 6f + 3f,
                        color = colors.random(),
                        rotation = Random.nextFloat() * 360f,
                        rotationSpeed = Random.nextFloat() * 20f - 10f
                    )
                }
            }
        } else {
            particles = emptyList()
        }
    }
    
    Canvas(modifier = modifier.fillMaxSize()) {
        particles.forEach { particle ->
            drawConfettiPiece(particle)
        }
    }
}

private fun DrawScope.drawConfettiPiece(particle: Particle) {
    val alpha = particle.life
    drawRect(
        color = particle.color.copy(alpha = alpha),
        topLeft = Offset(
            x = particle.x * size.width - particle.size / 2,
            y = particle.y * size.height - particle.size / 2
        ),
        size = androidx.compose.ui.geometry.Size(particle.size, particle.size * 1.5f)
    )
}

@Composable
fun FloatingParticles(
    modifier: Modifier = Modifier,
    color: Color = Color.White.copy(alpha = 0.3f),
    particleCount: Int = 20
) {
    var particles by remember {
        mutableStateOf(
            List(particleCount) {
                Particle(
                    x = Random.nextFloat(),
                    y = Random.nextFloat(),
                    vx = Random.nextFloat() * 0.5f - 0.25f,
                    vy = Random.nextFloat() * -0.5f - 0.25f,
                    life = 1f,
                    maxLife = 1f,
                    size = Random.nextFloat() * 4f + 2f,
                    color = color
                )
            }
        )
    }
    
    LaunchedEffect(Unit) {
        while (true) {
            delay(16)
            particles = particles.map { particle ->
                var newX = particle.x + particle.vx * 0.01f
                var newY = particle.y + particle.vy * 0.01f
                
                if (newX < 0f || newX > 1f) newX = Random.nextFloat()
                if (newY < -0.1f) newY = 1.1f
                if (newY > 1.1f) newY = -0.1f
                
                particle.copy(x = newX, y = newY)
            }
        }
    }
    
    Canvas(modifier = modifier.fillMaxSize()) {
        particles.forEach { particle ->
            drawCircle(
                color = particle.color,
                radius = particle.size,
                center = Offset(
                    x = particle.x * size.width,
                    y = particle.y * size.height
                )
            )
        }
    }
}
