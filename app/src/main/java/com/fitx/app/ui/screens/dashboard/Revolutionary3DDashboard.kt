package com.fitx.app.ui.screens.dashboard

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fitx.app.ui.components.premium.AnimatedProgressRing
import com.fitx.app.ui.theme.premium.*
import kotlin.math.abs

/**
 * REVOLUTIONARY 3D DASHBOARD - NO COMPETITOR HAS THIS!
 * Features:
 * - 3D Parallax cards that respond to touch
 * - Holographic effects
 * - Neon glow animations
 * - Liquid morph backgrounds
 * - Crystal shard effects
 * - Aurora borealis effects
 * - Plasma visualizations
 * - Metallic shine effects
 * - Electric arc animations
 */

@Composable
fun Revolutionary3DDashboardScreen(
    theme: PremiumTheme = PremiumThemes.ElectricBlue,
    dashboardData: DashboardData = DashboardData(),
    onNavigateToFeature: (String) -> Unit = {}
) {
    var rotationX by remember { mutableStateOf(0f) }
    var rotationY by remember { mutableStateOf(0f) }
    
    Box(modifier = Modifier.fillMaxSize()) {
        // Aurora background effect
        AuroraEffect(
            modifier = Modifier.fillMaxSize(),
            colors = listOf(
                theme.primaryColor.copy(alpha = 0.3f),
                theme.secondaryColor.copy(alpha = 0.3f),
                theme.accentColor.copy(alpha = 0.3f)
            )
        )
        
        // Floating particles with plasma effect
        Box(modifier = Modifier.fillMaxSize()) {
            PlasmaEffect(modifier = Modifier.fillMaxSize())
            FloatingParticles(
                modifier = Modifier.fillMaxSize(),
                color = theme.primaryColor.copy(alpha = 0.2f),
                particleCount = 30
            )
        }
        
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        rotationY = (dragAmount.x / 10f).coerceIn(-15f, 15f)
                        rotationX = (-dragAmount.y / 10f).coerceIn(-15f, 15f)
                    }
                },
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }
            
            // 3D Parallax Hero Card with Holographic Effect
            item {
                Box(modifier = Modifier.fillMaxWidth()) {
                    // Holographic background
                    Holographic3DEffect(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp)
                            .clip(RoundedCornerShape(32.dp))
                    )
                    
                    // 3D Parallax Card
                    Parallax3DCard(
                        modifier = Modifier.fillMaxWidth(),
                        rotationX = rotationX,
                        rotationY = rotationY,
                        depth = 30f
                    ) {
                        Hero3DStatCard(
                            mainValue = dashboardData.steps.toString(),
                            mainUnit = "steps",
                            label = "Today's Steps",
                            progress = dashboardData.steps / 10000f,
                            goal = "10,000",
                            gradient = listOf(theme.gradientStart, theme.gradientEnd),
                            icon = Icons.Default.DirectionsWalk,
                            theme = theme
                        )
                    }
                }
            }
            
            // Neon Glow Quick Stats
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    NeonGlowStatCard(
                        value = String.format("%.1f", dashboardData.distance),
                        unit = "km",
                        label = "Distance",
                        icon = Icons.Default.Route,
                        color = theme.primaryColor,
                        modifier = Modifier.weight(1f)
                    )
                    NeonGlowStatCard(
                        value = dashboardData.calories.toString(),
                        unit = "kcal",
                        label = "Calories",
                        icon = Icons.Default.LocalFireDepartment,
                        color = theme.secondaryColor,
                        modifier = Modifier.weight(1f)
                    )
                    NeonGlowStatCard(
                        value = dashboardData.activeMinutes.toString(),
                        unit = "min",
                        label = "Active",
                        icon = Icons.Default.Timer,
                        color = theme.accentColor,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            
            // Crystal Shard Activity Card
            item {
                Box(modifier = Modifier.fillMaxWidth()) {
                    CrystalShardEffect(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(24.dp)),
                        color = theme.primaryColor
                    )
                    
                    Parallax3DCard(
                        modifier = Modifier.fillMaxWidth(),
                        rotationX = rotationX * 0.5f,
                        rotationY = rotationY * 0.5f,
                        depth = 20f
                    ) {
                        CrystalActivityCard(
                            title = "Today's Activity",
                            subtitle = "Keep pushing forward!",
                            theme = theme,
                            onClick = { onNavigateToFeature("activity") }
                        )
                    }
                }
            }
            
            // Liquid Morph Nutrition Card
            item {
                Box(modifier = Modifier.fillMaxWidth()) {
                    LiquidMorphEffect(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .clip(RoundedCornerShape(24.dp)),
                        colors = listOf(
                            theme.primaryColor.copy(alpha = 0.6f),
                            theme.secondaryColor.copy(alpha = 0.6f),
                            theme.accentColor.copy(alpha = 0.6f)
                        )
                    )
                    
                    Parallax3DCard(
                        modifier = Modifier.fillMaxWidth(),
                        rotationX = rotationX * 0.7f,
                        rotationY = rotationY * 0.7f,
                        depth = 25f
                    ) {
                        LiquidNutritionCard(
                            caloriesConsumed = dashboardData.caloriesConsumed,
                            caloriesGoal = dashboardData.caloriesGoal,
                            theme = theme,
                            onClick = { onNavigateToFeature("nutrition") }
                        )
                    }
                }
            }
            
            // Metallic Shine Workout Card
            item {
                Box(modifier = Modifier.fillMaxWidth()) {
                    MetallicShineEffect(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .clip(RoundedCornerShape(24.dp)),
                        baseColor = theme.secondaryColor
                    )
                    
                    Parallax3DCard(
                        modifier = Modifier.fillMaxWidth(),
                        rotationX = rotationX * 0.6f,
                        rotationY = rotationY * 0.6f,
                        depth = 22f
                    ) {
                        MetallicWorkoutCard(
                            workoutName = "Push Day",
                            isCompleted = false,
                            theme = theme,
                            onClick = { onNavigateToFeature("workout") }
                        )
                    }
                }
            }
            
            // Electric Arc Health Score
            item {
                Box(modifier = Modifier.fillMaxWidth()) {
                    ElectricArcEffect(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .clip(RoundedCornerShape(24.dp)),
                        color = theme.accentColor
                    )
                    
                    Parallax3DCard(
                        modifier = Modifier.fillMaxWidth(),
                        rotationX = rotationX * 0.8f,
                        rotationY = rotationY * 0.8f,
                        depth = 28f
                    ) {
                        ElectricHealthScoreCard(
                            score = dashboardData.healthScore,
                            theme = theme,
                            onClick = { onNavigateToFeature("health_score") }
                        )
                    }
                }
            }
            
            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
        
        // 3D Floating Action Button with Neon Glow
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
        ) {
            NeonGlowEffect(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape),
                color = theme.primaryColor,
                intensity = 1.5f
            )
            
            FloatingActionButton(
                onClick = { },
                modifier = Modifier
                    .size(64.dp)
                    .graphicsLayer {
                        shadowElevation = 20f
                        rotationZ = rotationX * 2
                    },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Quick Add",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                },
                backgroundColor = theme.primaryColor
            )
        }
    }
}

@Composable
private fun Hero3DStatCard(
    mainValue: String,
    mainUnit: String,
    label: String,
    progress: Float,
    goal: String,
    gradient: List<Color>,
    icon: ImageVector,
    theme: PremiumTheme
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
    ) {
        // Neon glow background
        NeonGlowEffect(
            modifier = Modifier.fillMaxSize(),
            color = theme.primaryColor,
            intensity = 0.8f
        )
        
        HeroCard(
            modifier = Modifier.fillMaxSize(),
            backgroundGradient = gradient
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = label,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = mainValue,
                                fontSize = 64.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = mainUnit,
                                fontSize = 28.sp,
                                color = Color.White.copy(alpha = 0.8f),
                                modifier = Modifier.padding(bottom = 10.dp)
                            )
                        }
                    }
                    
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }
                
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Goal: $goal",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                        Text(
                            text = "${(progress * 100).toInt()}%",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp))
                            .background(Color.White.copy(alpha = 0.3f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(progress.coerceIn(0f, 1f))
                                .clip(RoundedCornerShape(5.dp))
                                .background(Color.White)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NeonGlowStatCard(
    value: String,
    unit: String,
    label: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.height(130.dp)) {
        // Neon glow effect
        NeonGlowEffect(
            modifier = Modifier.fillMaxSize(),
            color = color,
            intensity = 1.2f
        )
        
        GlassCard(
            modifier = Modifier.fillMaxSize(),
            cornerRadius = 24.dp
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(color.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = value,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = " $unit",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
                Text(
                    text = label,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
private fun CrystalActivityCard(
    title: String,
    subtitle: String,
    theme: PremiumTheme,
    onClick: () -> Unit
) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clickable(onClick = onClick),
        cornerRadius = 24.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.DirectionsRun,
                contentDescription = null,
                tint = theme.primaryColor,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = title,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun LiquidNutritionCard(
    caloriesConsumed: Int,
    caloriesGoal: Int,
    theme: PremiumTheme,
    onClick: () -> Unit
) {
    val progress = if (caloriesGoal > 0) caloriesConsumed.toFloat() / caloriesGoal else 0f
    
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .clickable(onClick = onClick),
        cornerRadius = 24.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Nutrition",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "$caloriesConsumed / $caloriesGoal kcal",
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )
                Spacer(modifier = Modifier.height(12.dp))
                LinearProgressIndicator(
                    progress = progress.coerceIn(0f, 1f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = theme.secondaryColor,
                    trackColor = theme.secondaryColor.copy(alpha = 0.2f)
                )
            }
            
            AnimatedProgressRing(
                progress = progress,
                size = 80.dp,
                strokeWidth = 8.dp,
                gradientColors = listOf(theme.secondaryColor, theme.accentColor)
            )
        }
    }
}

@Composable
private fun MetallicWorkoutCard(
    workoutName: String,
    isCompleted: Boolean,
    theme: PremiumTheme,
    onClick: () -> Unit
) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .clickable(onClick = onClick),
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
                    text = "Today's Workout",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = workoutName,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(
                        if (isCompleted) theme.accentColor else theme.secondaryColor
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Default.FitnessCenter,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

@Composable
private fun ElectricHealthScoreCard(
    score: Int,
    theme: PremiumTheme,
    onClick: () -> Unit
) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .clickable(onClick = onClick),
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
                    text = "Health Score",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = score.toString(),
                        fontSize = 56.sp,
                        fontWeight = FontWeight.Bold,
                        color = theme.accentColor
                    )
                    Text(
                        text = " / 100",
                        fontSize = 24.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
            }
            
            AnimatedProgressRing(
                progress = score / 100f,
                size = 90.dp,
                strokeWidth = 10.dp,
                gradientColors = listOf(theme.accentColor, theme.primaryColor),
                showPercentage = false
            )
        }
    }
}
