package com.fitx.app.ui.screens.workout

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fitx.app.ui.components.premium.AnimatedProgressRing
import com.fitx.app.ui.theme.premium.*
import kotlinx.coroutines.delay
import kotlin.math.*

/**
 * IMMERSIVE 3D WORKOUT EXPERIENCE
 * Revolutionary workout screen with 3D environment
 * NO COMPETITOR HAS THIS LEVEL OF IMMERSION!
 */

@Composable
fun Immersive3DWorkoutScreen(
    theme: PremiumTheme = PremiumThemes.ElectricBlue,
    workoutData: WorkoutData = WorkoutData(),
    onStartWorkout: () -> Unit = {},
    onPauseWorkout: () -> Unit = {},
    onCompleteWorkout: () -> Unit = {}
) {
    var isWorkoutActive by remember { mutableStateOf(false) }
    var currentExerciseIndex by remember { mutableStateOf(0) }
    var currentSet by remember { mutableStateOf(1) }
    var restTimer by remember { mutableStateOf(0) }
    
    val hapticEngine = rememberHapticEngine()
    
    Box(modifier = Modifier.fillMaxSize()) {
        // 3D Environment Background
        Immersive3DEnvironment(
            theme = theme,
            intensity = if (isWorkoutActive) 1f else 0.3f
        )
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Stats Bar
            Workout3DStatsBar(
                workoutData = workoutData,
                theme = theme
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Main Exercise Display
            if (workoutData.exercises.isNotEmpty()) {
                Parallax3DCard(
                    modifier = Modifier.fillMaxWidth(),
                    depth = 40f
                ) {
                    Exercise3DCard(
                        exercise = workoutData.exercises[currentExerciseIndex],
                        currentSet = currentSet,
                        isActive = isWorkoutActive,
                        theme = theme
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Rest Timer (if resting)
            if (restTimer > 0) {
                RestTimer3D(
                    timeRemaining = restTimer,
                    theme = theme
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Exercise Progress
            ExerciseProgress3D(
                currentExercise = currentExerciseIndex + 1,
                totalExercises = workoutData.exercises.size,
                theme = theme
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Control Buttons
            Workout3DControls(
                isActive = isWorkoutActive,
                onStart = {
                    isWorkoutActive = true
                    hapticEngine.playSuccess()
                    onStartWorkout()
                },
                onPause = {
                    isWorkoutActive = false
                    hapticEngine.playClick()
                    onPauseWorkout()
                },
                onNext = {
                    if (currentSet < workoutData.exercises[currentExerciseIndex].sets) {
                        currentSet++
                        restTimer = 60
                    } else {
                        currentSet = 1
                        if (currentExerciseIndex < workoutData.exercises.size - 1) {
                            currentExerciseIndex++
                            restTimer = 90
                        } else {
                            isWorkoutActive = false
                            hapticEngine.playGoalReached()
                            onCompleteWorkout()
                        }
                    }
                    hapticEngine.playMilestone()
                },
                theme = theme
            )
        }
        
        // Particle effects during workout
        if (isWorkoutActive) {
            FloatingParticles(
                modifier = Modifier.fillMaxSize(),
                color = theme.primaryColor.copy(alpha = 0.3f),
                particleCount = 40
            )
        }
    }
    
    // Rest timer countdown
    LaunchedEffect(restTimer) {
        if (restTimer > 0) {
            delay(1000)
            restTimer--
            if (restTimer % 5 == 0) {
                hapticEngine.playTick()
            }
        }
    }
}

@Composable
private fun Immersive3DEnvironment(
    theme: PremiumTheme,
    intensity: Float
) {
    Box(modifier = Modifier.fillMaxSize()) {
        // Aurora background
        AuroraEffect(
            modifier = Modifier.fillMaxSize(),
            colors = listOf(
                theme.primaryColor.copy(alpha = 0.2f * intensity),
                theme.secondaryColor.copy(alpha = 0.2f * intensity),
                theme.accentColor.copy(alpha = 0.2f * intensity)
            )
        )
        
        // Plasma overlay
        PlasmaEffect(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { alpha = 0.1f * intensity }
        )
        
        // Holographic grid
        Holographic3DEffect(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { alpha = 0.15f * intensity }
        )
    }
}

@Composable
private fun Workout3DStatsBar(
    workoutData: WorkoutData,
    theme: PremiumTheme
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Stat3DCard(
            value = workoutData.duration,
            label = "Duration",
            icon = Icons.Default.Timer,
            color = theme.primaryColor
        )
        Stat3DCard(
            value = workoutData.calories.toString(),
            label = "Calories",
            icon = Icons.Default.LocalFireDepartment,
            color = theme.secondaryColor
        )
        Stat3DCard(
            value = workoutData.heartRate.toString(),
            label = "BPM",
            icon = Icons.Default.Favorite,
            color = theme.accentColor
        )
    }
}

@Composable
private fun Stat3DCard(
    value: String,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color
) {
    Box {
        NeonGlowEffect(
            modifier = Modifier
                .size(100.dp, 80.dp)
                .clip(RoundedCornerShape(16.dp)),
            color = color,
            intensity = 0.8f
        )
        
        GlassCard(
            modifier = Modifier.size(100.dp, 80.dp),
            cornerRadius = 16.dp
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
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = value,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = label,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
private fun Exercise3DCard(
    exercise: Exercise,
    currentSet: Int,
    isActive: Boolean,
    theme: PremiumTheme
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
    ) {
        // Crystal shard background
        CrystalShardEffect(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(32.dp)),
            color = theme.primaryColor
        )
        
        // Metallic shine overlay
        MetallicShineEffect(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(32.dp)),
            baseColor = theme.secondaryColor
        )
        
        GlassCard(
            modifier = Modifier.fillMaxSize(),
            cornerRadius = 32.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Exercise name
                Text(
                    text = exercise.name,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                // 3D animated rep counter
                Animated3DRepCounter(
                    currentSet = currentSet,
                    totalSets = exercise.sets,
                    repsPerSet = exercise.reps,
                    isActive = isActive,
                    theme = theme
                )
                
                // Set indicator
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    repeat(exercise.sets) { index ->
                        SetIndicator3D(
                            isCompleted = index < currentSet - 1,
                            isCurrent = index == currentSet - 1,
                            theme = theme
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun Animated3DRepCounter(
    currentSet: Int,
    totalSets: Int,
    repsPerSet: Int,
    isActive: Boolean,
    theme: PremiumTheme
) {
    val infiniteTransition = rememberInfiniteTransition(label = "rep_counter")
    
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isActive) 1.1f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )
    
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = if (isActive) 360f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )
    
    Box(
        modifier = Modifier
            .size(180.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                rotationZ = rotation * 0.1f
            },
        contentAlignment = Alignment.Center
    ) {
        // Outer ring with electric arc
        Box(modifier = Modifier.fillMaxSize()) {
            ElectricArcEffect(
                modifier = Modifier.fillMaxSize(),
                color = theme.accentColor
            )
        }
        
        // Progress ring
        AnimatedProgressRing(
            progress = currentSet.toFloat() / totalSets,
            size = 160.dp,
            strokeWidth = 12.dp,
            gradientColors = listOf(theme.primaryColor, theme.secondaryColor)
        )
        
        // Center text
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = repsPerSet.toString(),
                fontSize = 56.sp,
                fontWeight = FontWeight.Bold,
                color = theme.primaryColor
            )
            Text(
                text = "REPS",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun SetIndicator3D(
    isCompleted: Boolean,
    isCurrent: Boolean,
    theme: PremiumTheme
) {
    val scale by animateFloatAsState(
        targetValue = if (isCurrent) 1.2f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "scale"
    )
    
    Box(
        modifier = Modifier
            .size(40.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
    ) {
        if (isCurrent) {
            NeonGlowEffect(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape),
                color = theme.primaryColor,
                intensity = 1.5f
            )
        }
        
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .background(
                    when {
                        isCompleted -> theme.accentColor
                        isCurrent -> theme.primaryColor
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isCompleted) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun RestTimer3D(
    timeRemaining: Int,
    theme: PremiumTheme
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
    ) {
        LiquidMorphEffect(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(24.dp)),
            colors = listOf(
                theme.secondaryColor.copy(alpha = 0.6f),
                theme.accentColor.copy(alpha = 0.6f)
            )
        )
        
        GlassCard(
            modifier = Modifier.fillMaxSize(),
            cornerRadius = 24.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Rest Time",
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Text(
                        text = "${timeRemaining}s",
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        color = theme.accentColor
                    )
                }
                
                AnimatedProgressRing(
                    progress = timeRemaining / 90f,
                    size = 80.dp,
                    strokeWidth = 8.dp,
                    gradientColors = listOf(theme.secondaryColor, theme.accentColor)
                )
            }
        }
    }
}

@Composable
private fun ExerciseProgress3D(
    currentExercise: Int,
    totalExercises: Int,
    theme: PremiumTheme
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Exercise $currentExercise of $totalExercises",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "${(currentExercise.toFloat() / totalExercises * 100).toInt()}%",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = theme.primaryColor
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
        ) {
            NeonGlowEffect(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(6.dp)),
                color = theme.primaryColor,
                intensity = 0.8f
            )
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(currentExercise.toFloat() / totalExercises)
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(theme.primaryColor, theme.secondaryColor)
                            )
                        )
                )
            }
        }
    }
}

@Composable
private fun Workout3DControls(
    isActive: Boolean,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onNext: () -> Unit,
    theme: PremiumTheme
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        // Start/Pause button
        Box {
            NeonGlowEffect(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape),
                color = if (isActive) theme.secondaryColor else theme.primaryColor,
                intensity = 1.5f
            )
            
            FloatingActionButton(
                onClick = if (isActive) onPause else onStart,
                modifier = Modifier.size(72.dp),
                icon = {
                    Icon(
                        imageVector = if (isActive) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isActive) "Pause" else "Start",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                },
                backgroundColor = if (isActive) theme.secondaryColor else theme.primaryColor
            )
        }
        
        // Next button
        Box {
            NeonGlowEffect(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape),
                color = theme.accentColor,
                intensity = 1.2f
            )
            
            FloatingActionButton(
                onClick = onNext,
                modifier = Modifier.size(72.dp),
                icon = {
                    Icon(
                        imageVector = Icons.Default.SkipNext,
                        contentDescription = "Next",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                },
                backgroundColor = theme.accentColor
            )
        }
    }
}

// Data classes
data class WorkoutData(
    val duration: String = "00:00",
    val calories: Int = 0,
    val heartRate: Int = 0,
    val exercises: List<Exercise> = emptyList()
)

data class Exercise(
    val name: String,
    val sets: Int,
    val reps: Int,
    val weight: Float = 0f
)
