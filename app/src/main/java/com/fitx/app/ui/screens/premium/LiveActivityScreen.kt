package com.fitx.app.ui.screens.premium

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fitx.app.ui.components.premium.AnimatedProgressRing
import com.fitx.app.ui.theme.premium.*
import kotlinx.coroutines.delay

/**
 * Live Activity Tracking Screen
 * Real-time activity tracking with stunning animations
 */

@Composable
fun LiveActivityScreen(
    activityType: ActivityType = ActivityType.WALKING,
    theme: PremiumTheme = PremiumThemes.ElectricBlue,
    onStopActivity: () -> Unit = {}
) {
    var isTracking by remember { mutableStateOf(false) }
    var isPaused by remember { mutableStateOf(false) }
    var distance by remember { mutableStateOf(0.0f) }
    var duration by remember { mutableStateOf(0) }
    var calories by remember { mutableStateOf(0) }
    var heartRate by remember { mutableStateOf(0) }
    
    // Simulate live data updates
    LaunchedEffect(isTracking, isPaused) {
        if (isTracking && !isPaused) {
            while (true) {
                delay(1000)
                duration++
                distance += 0.02f
                calories = (distance * 60).toInt()
                heartRate = (120..150).random()
            }
        }
    }
    
    Box(modifier = Modifier.fillMaxSize()) {
        // Animated background
        FloatingParticles(
            modifier = Modifier.fillMaxSize(),
            color = theme.primaryColor.copy(alpha = 0.15f),
            particleCount = 30
        )
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header
            ActivityHeader(
                activityType = activityType,
                isTracking = isTracking,
                theme = theme
            )
            
            // Main stats with animated ring
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                // Distance ring
                AnimatedProgressRing(
                    progress = (distance / 10f).coerceIn(0f, 1f),
                    size = 280.dp,
                    strokeWidth = 24.dp,
                    gradientColors = listOf(theme.gradientStart, theme.gradientEnd),
                    showPercentage = false
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AnimatedCounter(
                            value = String.format("%.2f", distance),
                            fontSize = 56.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "km",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                        )
                    }
                }
                
                // Secondary stats
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    LiveStatCard(
                        value = formatDuration(duration),
                        label = "Duration",
                        icon = Icons.Default.Timer,
                        color = theme.primaryColor
                    )
                    LiveStatCard(
                        value = calories.toString(),
                        label = "Calories",
                        icon = Icons.Default.LocalFireDepartment,
                        color = theme.secondaryColor
                    )
                    LiveStatCard(
                        value = heartRate.toString(),
                        label = "BPM",
                        icon = Icons.Default.Favorite,
                        color = theme.accentColor,
                        isPulsing = isTracking && !isPaused
                    )
                }
            }
            
            // Control buttons
            ActivityControls(
                isTracking = isTracking,
                isPaused = isPaused,
                theme = theme,
                onStart = { isTracking = true },
                onPause = { isPaused = !isPaused },
                onStop = {
                    isTracking = false
                    isPaused = false
                    onStopActivity()
                }
            )
        }
        
        // Celebration when milestone reached
        if (distance >= 5f && distance < 5.1f) {
            ParticleExplosion(
                modifier = Modifier.fillMaxSize(),
                trigger = true,
                colors = listOf(
                    theme.primaryColor,
                    theme.secondaryColor,
                    theme.accentColor
                )
            )
        }
    }
}

@Composable
private fun ActivityHeader(
    activityType: ActivityType,
    isTracking: Boolean,
    theme: PremiumTheme
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = activityType.displayName,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            
            AnimatedVisibility(visible = isTracking) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PulsingDot(color = theme.accentColor)
                    Text(
                        text = "Live Tracking",
                        fontSize = 14.sp,
                        color = theme.accentColor,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
        
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(theme.primaryColor, theme.secondaryColor)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = activityType.icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Composable
private fun LiveStatCard(
    value: String,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    isPulsing: Boolean = false
) {
    val scale = if (isPulsing) {
        rememberPulseAnimation(minScale = 0.95f, maxScale = 1.05f, durationMillis = 800)
    } else {
        1f
    }
    
    GlassCard(
        modifier = Modifier
            .width(100.dp)
            .height(120.dp),
        cornerRadius = 20.dp
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier
                    .size(32.dp)
                    .then(if (isPulsing) Modifier.pulseEffect(scale) else Modifier)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = label,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun ActivityControls(
    isTracking: Boolean,
    isPaused: Boolean,
    theme: PremiumTheme,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onStop: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (!isTracking) {
            PremiumButton(
                text = "Start Activity",
                onClick = onStart,
                modifier = Modifier.weight(1f),
                gradient = listOf(theme.gradientStart, theme.gradientEnd),
                height = 64.dp,
                icon = {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            )
        } else {
            GradientIconButton(
                onClick = onPause,
                modifier = Modifier.size(64.dp),
                gradient = if (isPaused) {
                    listOf(theme.accentColor, theme.accentColor)
                } else {
                    listOf(Color(0xFFFFA500), Color(0xFFFF6B6B))
                },
                icon = {
                    Icon(
                        imageVector = if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            )
            
            PremiumButton(
                text = "Finish",
                onClick = onStop,
                modifier = Modifier.weight(1f),
                gradient = listOf(Color(0xFFEF4444), Color(0xFFDC2626)),
                height = 64.dp,
                icon = {
                    Icon(
                        imageVector = Icons.Default.Stop,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            )
        }
    }
}

@Composable
private fun PulsingDot(color: Color) {
    val scale = rememberPulseAnimation(minScale = 0.8f, maxScale = 1.2f, durationMillis = 1000)
    
    Box(
        modifier = Modifier
            .size(12.dp)
            .pulseEffect(scale)
            .clip(CircleShape)
            .background(color)
    )
}

@Composable
private fun AnimatedCounter(
    value: String,
    fontSize: androidx.compose.ui.unit.TextUnit,
    color: Color
) {
    Text(
        text = value,
        fontSize = fontSize,
        fontWeight = FontWeight.Bold,
        color = color
    )
}

private fun formatDuration(seconds: Int): String {
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    val secs = seconds % 60
    return if (hours > 0) {
        String.format("%d:%02d:%02d", hours, minutes, secs)
    } else {
        String.format("%02d:%02d", minutes, secs)
    }
}

enum class ActivityType(
    val displayName: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    WALKING("Walking", Icons.Default.DirectionsWalk),
    RUNNING("Running", Icons.Default.DirectionsRun),
    CYCLING("Cycling", Icons.Default.DirectionsBike),
    WORKOUT("Workout", Icons.Default.FitnessCenter)
}
