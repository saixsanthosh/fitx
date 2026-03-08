package com.fitx.app.ui.screens.reports

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import java.text.SimpleDateFormat
import java.util.*

/**
 * Feature 13: Weekly Health Report - UNBEATABLE DESIGN
 * Comprehensive weekly analytics with stunning visualizations
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeeklyReportScreen(
    theme: PremiumTheme = PremiumThemes.ElectricBlue,
    weeklyData: WeeklyReportData = WeeklyReportData(),
    onExportPDF: () -> Unit = {},
    onShareReport: () -> Unit = {},
    onBackPressed: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Weekly Report", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onShareReport) {
                        Icon(Icons.Default.Share, "Share", tint = theme.primaryColor)
                    }
                    IconButton(onClick = onExportPDF) {
                        Icon(Icons.Default.PictureAsPdf, "Export", tint = theme.secondaryColor)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Box(Modifier.fillMaxSize()) {
            FloatingParticles(Modifier.fillMaxSize(), theme.primaryColor.copy(alpha = 0.1f))
            
            LazyColumn(
                Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item { Spacer(Modifier.height(8.dp)) }
                
                // Date Range Header
                item {
                    DateRangeCard(weeklyData.startDate, weeklyData.endDate, theme)
                }
                
                // Overall Score
                item {
                    OverallScoreCard(weeklyData.overallScore, theme)
                }
                
                // Activity Summary
                item {
                    Text("Activity Summary", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
                
                item {
                    ActivitySummaryCard(weeklyData.activitySummary, theme)
                }
                
                // Nutrition Summary
                item {
                    Text("Nutrition Summary", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
                
                item {
                    NutritionSummaryCard(weeklyData.nutritionSummary, theme)
                }
                
                // Weight Progress
                item {
                    Text("Weight Progress", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
                
                item {
                    WeightProgressCard(weeklyData.weightChange, theme)
                }
                
                // Habits & Tasks
                item {
                    Text("Habits & Tasks", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
                
                item {
                    HabitsTasksCard(weeklyData.habitsCompleted, weeklyData.tasksCompleted, theme)
                }
                
                // Achievements
                item {
                    Text("This Week's Achievements", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
                
                item {
                    AchievementsCard(weeklyData.achievements, theme)
                }
                
                // Insights
                item {
                    Text("Insights & Recommendations", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
                
                item {
                    InsightsCard(weeklyData.insights, theme)
                }
                
                item { Spacer(Modifier.height(80.dp)) }
            }
        }
    }
}

@Composable
private fun DateRangeCard(startDate: String, endDate: String, theme: PremiumTheme) {
    HeroCard(
        Modifier.fillMaxWidth().height(100.dp),
        listOf(theme.gradientStart, theme.gradientEnd)
    ) {
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Week of", fontSize = 14.sp, color = Color.White.copy(0.9f))
            Text("$startDate - $endDate", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}

@Composable
private fun OverallScoreCard(score: Int, theme: PremiumTheme) {
    GlowingCard(Modifier.fillMaxWidth().height(200.dp), theme.primaryColor) {
        Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("Overall Health Score", fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface.copy(0.7f))
                Spacer(Modifier.height(8.dp))
                Text(score.toString(), fontSize = 64.sp, fontWeight = FontWeight.Bold, color = theme.primaryColor)
                Text("out of 100", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface.copy(0.6f))
            }
            AnimatedProgressRing(
                score / 100f, 140.dp, 16.dp,
                listOf(theme.primaryColor, theme.secondaryColor),
                MaterialTheme.colorScheme.surfaceVariant, false
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.EmojiEvents, null, Modifier.size(40.dp), theme.primaryColor)
                    Text("${score}%", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun ActivitySummaryCard(summary: ActivitySummary, theme: PremiumTheme) {
    GlassCard(Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard("${summary.totalWorkouts}", "Workouts", theme.primaryColor, Modifier.weight(1f))
                StatCard("${String.format("%.1f", summary.totalDistance)} km", "Distance", theme.secondaryColor, Modifier.weight(1f))
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard("${summary.totalCalories}", "Calories", theme.accentColor, Modifier.weight(1f))
                StatCard("${summary.activeMinutes} min", "Active Time", Color(0xFFFBBF24), Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun NutritionSummaryCard(summary: NutritionSummary, theme: PremiumTheme) {
    GlassCard(Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Daily Averages", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            
            NutritionBar("Calories", summary.avgCalories, 2000, theme.primaryColor)
            NutritionBar("Protein", summary.avgProtein, 150, theme.secondaryColor)
            NutritionBar("Carbs", summary.avgCarbs, 250, theme.accentColor)
            NutritionBar("Fat", summary.avgFat, 65, Color(0xFFFBBF24))
        }
    }
}

@Composable
private fun NutritionBar(label: String, value: Int, goal: Int, color: Color) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, fontSize = 14.sp)
            Text("$value / $goal", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = color)
        }
        LinearProgressIndicator(
            progress = (value.toFloat() / goal).coerceIn(0f, 1f),
            Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
            color = color,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}

@Composable
private fun WeightProgressCard(change: Float, theme: PremiumTheme) {
    GlassCard(Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(64.dp).clip(CircleShape).background(
                    Brush.linearGradient(
                        listOf(
                            if (change < 0) theme.primaryColor else theme.secondaryColor,
                            if (change < 0) theme.primaryColor.copy(0.7f) else theme.secondaryColor.copy(0.7f)
                        )
                    )
                ), Alignment.Center
            ) {
                Icon(
                    if (change < 0) Icons.Default.TrendingDown else Icons.Default.TrendingUp,
                    null, Modifier.size(32.dp), Color.White
                )
            }
            Column(Modifier.weight(1f)) {
                Text("Weight Change", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface.copy(0.7f))
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        "${if (change > 0) "+" else ""}${String.format("%.1f", change)}",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (change < 0) theme.primaryColor else theme.secondaryColor
                    )
                    Text(" kg", fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface.copy(0.7f), modifier = Modifier.padding(bottom = 4.dp))
                }
            }
        }
    }
}

@Composable
private fun HabitsTasksCard(habitsCompleted: Int, tasksCompleted: Int, theme: PremiumTheme) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        GlassCard(Modifier.weight(1f).height(120.dp)) {
            Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                Icon(Icons.Default.CheckCircle, null, Modifier.size(32.dp), theme.primaryColor)
                Spacer(Modifier.height(8.dp))
                Text("$habitsCompleted", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = theme.primaryColor)
                Text("Habits", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(0.7f))
            }
        }
        GlassCard(Modifier.weight(1f).height(120.dp)) {
            Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                Icon(Icons.Default.Task, null, Modifier.size(32.dp), theme.secondaryColor)
                Spacer(Modifier.height(8.dp))
                Text("$tasksCompleted", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = theme.secondaryColor)
                Text("Tasks", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(0.7f))
            }
        }
    }
}

@Composable
private fun AchievementsCard(achievements: List<String>, theme: PremiumTheme) {
    GlassCard(Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            achievements.forEach { achievement ->
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.EmojiEvents, null, Modifier.size(24.dp), theme.accentColor)
                    Text(achievement, fontSize = 14.sp)
                }
            }
            if (achievements.isEmpty()) {
                Text("No new achievements this week", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface.copy(0.6f))
            }
        }
    }
}

@Composable
private fun InsightsCard(insights: List<String>, theme: PremiumTheme) {
    GlassCard(Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            insights.forEach { insight ->
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.Top) {
                    Icon(Icons.Default.Lightbulb, null, Modifier.size(20.dp), theme.primaryColor)
                    Text(insight, fontSize = 14.sp, lineHeight = 20.sp)
                }
            }
        }
    }
}

@Composable
private fun StatCard(value: String, label: String, color: Color, modifier: Modifier) {
    Box(
        modifier.height(80.dp).clip(RoundedCornerShape(16.dp)).background(color.copy(0.15f)).padding(12.dp),
        Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = color)
            Text(label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(0.7f))
        }
    }
}

// Data Classes
data class WeeklyReportData(
    val startDate: String = "Jan 1",
    val endDate: String = "Jan 7",
    val overallScore: Int = 85,
    val activitySummary: ActivitySummary = ActivitySummary(),
    val nutritionSummary: NutritionSummary = NutritionSummary(),
    val weightChange: Float = -0.5f,
    val habitsCompleted: Int = 42,
    val tasksCompleted: Int = 35,
    val achievements: List<String> = listOf("Completed 5 workouts", "Reached 10km milestone"),
    val insights: List<String> = listOf(
        "Great consistency this week! You completed 5 workouts.",
        "Your nutrition is on track. Keep it up!",
        "Consider increasing your water intake."
    )
)

data class ActivitySummary(
    val totalWorkouts: Int = 5,
    val totalDistance: Float = 25.5f,
    val totalCalories: Int = 2450,
    val activeMinutes: Int = 420
)

data class NutritionSummary(
    val avgCalories: Int = 1950,
    val avgProtein: Int = 145,
    val avgCarbs: Int = 230,
    val avgFat: Int = 60
)

