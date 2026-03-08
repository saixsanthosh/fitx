package com.fitx.app.ui.screens.dashboard

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fitx.app.ui.components.premium.AnimatedProgressRing
import com.fitx.app.ui.theme.premium.*
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import java.text.SimpleDateFormat
import java.util.*

/**
 * Feature 2: Enhanced Dashboard
 * Complete dashboard with all metrics and quick actions
 */

@Composable
fun EnhancedDashboardScreen(
    theme: PremiumTheme = PremiumThemes.ElectricBlue,
    dashboardData: DashboardData = DashboardData(),
    onRefresh: () -> Unit = {},
    onQuickAction: (QuickAction) -> Unit = {},
    onNavigateToFeature: (String) -> Unit = {},
    isRefreshing: Boolean = false
) {
    var showCelebration by remember { mutableStateOf(false) }
    
    // Check for milestones
    LaunchedEffect(dashboardData.steps) {
        if (dashboardData.steps >= 10000 && dashboardData.steps < 10010) {
            showCelebration = true
        }
    }
    
    Box(modifier = Modifier.fillMaxSize()) {
        // Background particles
        FloatingParticles(
            modifier = Modifier.fillMaxSize(),
            color = theme.primaryColor.copy(alpha = 0.1f),
            particleCount = 20
        )
        
        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing),
            onRefresh = onRefresh
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item { Spacer(modifier = Modifier.height(8.dp)) }
                
                // Greeting Header
                item {
                    GreetingHeader(
                        userName = dashboardData.userName,
                        theme = theme
                    )
                }
                
                // Hero Stat - Main Metric
                item {
                    HeroStatCard(
                        mainValue = dashboardData.steps.toString(),
                        mainUnit = "steps",
                        label = "Today's Steps",
                        progress = dashboardData.steps / 10000f,
                        goal = "10,000",
                        gradient = listOf(theme.gradientStart, theme.gradientEnd),
                        icon = Icons.Default.DirectionsWalk
                    )
                }
                
                // Quick Stats Row
                item {
                    QuickStatsRow(
                        distance = dashboardData.distance,
                        calories = dashboardData.calories,
                        activeMinutes = dashboardData.activeMinutes,
                        theme = theme
                    )
                }
                
                // Daily Mission Card
                item {
                    DailyMissionCard(
                        missions = dashboardData.dailyMissions,
                        theme = theme,
                        onMissionClick = { onNavigateToFeature(it.route) }
                    )
                }
                
                // Quick Actions
                item {
                    Text(
                        text = "Quick Actions",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
                
                item {
                    QuickActionsGrid(
                        theme = theme,
                        onActionClick = onQuickAction
                    )
                }
                
                // Today's Overview
                item {
                    Text(
                        text = "Today's Overview",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
                
                // Nutrition Summary
                item {
                    NutritionSummaryCard(
                        caloriesConsumed = dashboardData.caloriesConsumed,
                        caloriesGoal = dashboardData.caloriesGoal,
                        protein = dashboardData.protein,
                        carbs = dashboardData.carbs,
                        fat = dashboardData.fat,
                        theme = theme,
                        onClick = { onNavigateToFeature("nutrition") }
                    )
                }
                
                // Water Intake
                item {
                    WaterIntakeCard(
                        currentIntake = dashboardData.waterIntake,
                        goal = dashboardData.waterGoal,
                        theme = theme,
                        onClick = { onNavigateToFeature("water") }
                    )
                }
                
                // Tasks Progress
                item {
                    TasksProgressCard(
                        completedTasks = dashboardData.completedTasks,
                        totalTasks = dashboardData.totalTasks,
                        theme = theme,
                        onClick = { onNavigateToFeature("tasks") }
                    )
                }
                
                // Workout Status
                if (dashboardData.hasWorkoutToday) {
                    item {
                        WorkoutStatusCard(
                            workoutName = dashboardData.todayWorkout,
                            isCompleted = dashboardData.workoutCompleted,
                            theme = theme,
                            onClick = { onNavigateToFeature("workout") }
                        )
                    }
                }
                
                // Habit Streak
                item {
                    HabitStreakCard(
                        currentStreak = dashboardData.habitStreak,
                        theme = theme,
                        onClick = { onNavigateToFeature("habits") }
                    )
                }
                
                // Health Score
                item {
                    HealthScoreCard(
                        score = dashboardData.healthScore,
                        theme = theme,
                        onClick = { onNavigateToFeature("health_score") }
                    )
                }
                
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
        
        // Floating Action Button
        FloatingActionButton(
            onClick = { onQuickAction(QuickAction.ADD) },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp),
            icon = {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Quick Add",
                    tint = Color.White
                )
            },
            backgroundColor = theme.primaryColor
        )
        
        // Celebration Effect
        if (showCelebration) {
            ParticleExplosion(
                modifier = Modifier.fillMaxSize(),
                trigger = showCelebration,
                colors = listOf(
                    theme.primaryColor,
                    theme.secondaryColor,
                    theme.accentColor
                ),
                onComplete = { showCelebration = false }
            )
        }
    }
}

@Composable
private fun GreetingHeader(
    userName: String,
    theme: PremiumTheme
) {
    val currentHour = remember { Calendar.getInstance().get(Calendar.HOUR_OF_DAY) }
    val greeting = when (currentHour) {
        in 0..11 -> "Good Morning"
        in 12..16 -> "Good Afternoon"
        else -> "Good Evening"
    }
    
    val date = remember {
        SimpleDateFormat("EEEE, MMMM d", Locale.getDefault()).format(Date())
    }
    
    Column {
        Text(
            text = "$greeting, $userName",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = date,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun HeroStatCard(
    mainValue: String,
    mainUnit: String,
    label: String,
    progress: Float,
    goal: String,
    gradient: List<Color>,
    icon: ImageVector
) {
    HeroCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp),
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
                        fontSize = 16.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = mainValue,
                            fontSize = 56.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = mainUnit,
                            fontSize = 24.sp,
                            color = Color.White.copy(alpha = 0.8f),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                }
                
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
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
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                    Text(
                        text = "${(progress * 100).toInt()}%",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = progress.coerceIn(0f, 1f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = Color.White,
                    trackColor = Color.White.copy(alpha = 0.3f)
                )
            }
        }
    }
}

@Composable
private fun QuickStatsRow(
    distance: Float,
    calories: Int,
    activeMinutes: Int,
    theme: PremiumTheme
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        QuickStatCard(
            value = String.format("%.1f", distance),
            unit = "km",
            label = "Distance",
            icon = Icons.Default.Route,
            color = theme.primaryColor,
            modifier = Modifier.weight(1f)
        )
        QuickStatCard(
            value = calories.toString(),
            unit = "kcal",
            label = "Calories",
            icon = Icons.Default.LocalFireDepartment,
            color = theme.secondaryColor,
            modifier = Modifier.weight(1f)
        )
        QuickStatCard(
            value = activeMinutes.toString(),
            unit = "min",
            label = "Active",
            icon = Icons.Default.Timer,
            color = theme.accentColor,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun QuickStatCard(
    value: String,
    unit: String,
    label: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    GlassCard(
        modifier = modifier.height(110.dp),
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
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = value,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = " $unit",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
            Text(
                text = label,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun DailyMissionCard(
    missions: List<DailyMission>,
    theme: PremiumTheme,
    onMissionClick: (DailyMission) -> Unit
) {
    val completedCount = missions.count { it.isCompleted }
    val progress = if (missions.isNotEmpty()) completedCount.toFloat() / missions.size else 0f
    
    GlowingCard(
        modifier = Modifier.fillMaxWidth(),
        glowColor = theme.primaryColor
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Daily Missions",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "$completedCount of ${missions.size} completed",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
                
                AnimatedProgressRing(
                    progress = progress,
                    size = 60.dp,
                    strokeWidth = 6.dp,
                    gradientColors = listOf(theme.primaryColor, theme.secondaryColor),
                    showPercentage = false
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            missions.take(3).forEach { mission ->
                MissionItem(
                    mission = mission,
                    theme = theme,
                    onClick = { onMissionClick(mission) }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun MissionItem(
    mission: DailyMission,
    theme: PremiumTheme,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (mission.isCompleted) {
                    theme.primaryColor.copy(alpha = 0.1f)
                } else {
                    MaterialTheme.colorScheme.surfaceVariant
                }
            )
            .clickable(onClick = onClick)
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (mission.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
            contentDescription = null,
            tint = if (mission.isCompleted) theme.primaryColor else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
            modifier = Modifier.size(24.dp)
        )
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = mission.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = mission.description,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
        
        Text(
            text = "+${mission.points}",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = theme.accentColor
        )
    }
}

@Composable
private fun QuickActionsGrid(
    theme: PremiumTheme,
    onActionClick: (QuickAction) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(QuickAction.values()) { action ->
            QuickActionCard(
                action = action,
                theme = theme,
                onClick = { onActionClick(action) }
            )
        }
    }
}

@Composable
private fun QuickActionCard(
    action: QuickAction,
    theme: PremiumTheme,
    onClick: () -> Unit
) {
    GlassCard(
        modifier = Modifier
            .width(120.dp)
            .height(120.dp)
            .clickable(onClick = onClick)
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
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(action.color, action.color.copy(alpha = 0.7f))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = action.icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = action.label,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

// Additional card components continued in next part...

// Data Classes
data class DashboardData(
    val userName: String = "User",
    val steps: Int = 0,
    val distance: Float = 0f,
    val calories: Int = 0,
    val activeMinutes: Int = 0,
    val caloriesConsumed: Int = 0,
    val caloriesGoal: Int = 2000,
    val protein: Int = 0,
    val carbs: Int = 0,
    val fat: Int = 0,
    val waterIntake: Int = 0,
    val waterGoal: Int = 8,
    val completedTasks: Int = 0,
    val totalTasks: Int = 0,
    val hasWorkoutToday: Boolean = false,
    val todayWorkout: String = "",
    val workoutCompleted: Boolean = false,
    val habitStreak: Int = 0,
    val healthScore: Int = 0,
    val dailyMissions: List<DailyMission> = emptyList()
)

data class DailyMission(
    val id: String,
    val title: String,
    val description: String,
    val points: Int,
    val isCompleted: Boolean,
    val route: String
)

enum class QuickAction(
    val label: String,
    val icon: ImageVector,
    val color: Color
) {
    ADD("Add", Icons.Default.Add, Color(0xFF3A86FF)),
    WORKOUT("Workout", Icons.Default.FitnessCenter, Color(0xFF8338EC)),
    MEAL("Log Meal", Icons.Default.Restaurant, Color(0xFF06FFA5)),
    WATER("Water", Icons.Default.WaterDrop, Color(0xFF00B4D8)),
    WEIGHT("Weight", Icons.Default.MonitorWeight, Color(0xFFFF006E)),
    TASK("Task", Icons.Default.CheckCircle, Color(0xFFFBBF24))
}

// Placeholder components for remaining cards
@Composable
private fun NutritionSummaryCard(
    caloriesConsumed: Int,
    caloriesGoal: Int,
    protein: Int,
    carbs: Int,
    fat: Int,
    theme: PremiumTheme,
    onClick: () -> Unit
) {
    GlowingCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        glowColor = theme.secondaryColor
    ) {
        Text("Nutrition Summary - Implementation in NutritionTrackerScreen.kt")
    }
}

@Composable
private fun WaterIntakeCard(
    currentIntake: Int,
    goal: Int,
    theme: PremiumTheme,
    onClick: () -> Unit
) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Text("Water Intake - Implementation in WaterTrackerScreen.kt")
    }
}

@Composable
private fun TasksProgressCard(
    completedTasks: Int,
    totalTasks: Int,
    theme: PremiumTheme,
    onClick: () -> Unit
) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Text("Tasks Progress - Implementation in TaskPlannerScreen.kt")
    }
}

@Composable
private fun WorkoutStatusCard(
    workoutName: String,
    isCompleted: Boolean,
    theme: PremiumTheme,
    onClick: () -> Unit
) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Text("Workout Status - Implementation in WorkoutPlannerScreen.kt")
    }
}

@Composable
private fun HabitStreakCard(
    currentStreak: Int,
    theme: PremiumTheme,
    onClick: () -> Unit
) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Text("Habit Streak - Implementation in HabitTrackerScreen.kt")
    }
}

@Composable
private fun HealthScoreCard(
    score: Int,
    theme: PremiumTheme,
    onClick: () -> Unit
) {
    GlowingCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        glowColor = theme.accentColor
    ) {
        Text("Health Score - Implementation in HealthScoreScreen.kt")
    }
}
