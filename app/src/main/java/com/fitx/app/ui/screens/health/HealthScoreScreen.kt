package com.fitx.app.ui.screens.health

import androidx.compose.animation.*
import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fitx.app.ui.components.premium.AnimatedProgressRing
import com.fitx.app.ui.theme.premium.*

/**
 * Feature 17: Health Score System - UNBEATABLE DESIGN
 * Comprehensive health scoring with category breakdown and insights
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthScoreScreen(
    theme: PremiumTheme = PremiumThemes.ElectricBlue,
    healthScore: HealthScoreData = HealthScoreData(),
    onBackPressed: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Health Score", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.Default.ArrowBack, "Back")
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
                
                // Main Score Card
                item {
                    MainScoreCard(healthScore.totalScore, theme)
                }
                
                // Score Breakdown
                item {
                    Text("Score Breakdown", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
                
                item {
                    ScoreBreakdownCard(healthScore.categories, theme)
                }
                
                // Trend Chart
                item {
                    Text("30-Day Trend", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
                
                item {
                    TrendChartCard(healthScore.trendData, theme)
                }
                
                // Improvement Tips
                item {
                    Text("Improvement Tips", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
                
                items(healthScore.improvementTips) { tip ->
                    ImprovementTipCard(tip, theme)
                }
                
                item { Spacer(Modifier.height(80.dp)) }
            }
        }
    }
}

@Composable
private fun MainScoreCard(score: Int, theme: PremiumTheme) {
    HeroCard(
        Modifier.fillMaxWidth().height(280.dp),
        listOf(theme.gradientStart, theme.gradientEnd)
    ) {
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Your Health Score", fontSize = 18.sp, color = Color.White.copy(0.9f))
            Spacer(Modifier.height(16.dp))
            
            AnimatedProgressRing(
                score / 100f, 200.dp, 20.dp,
                listOf(Color.White, Color.White.copy(0.7f)),
                Color.White.copy(0.2f), false
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(score.toString(), fontSize = 72.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text("out of 100", fontSize = 16.sp, color = Color.White.copy(0.9f))
                }
            }
            
            Spacer(Modifier.height(16.dp))
            Text(
                getScoreRating(score),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
private fun ScoreBreakdownCard(categories: List<ScoreCategory>, theme: PremiumTheme) {
    GlowingCard(Modifier.fillMaxWidth(), theme.primaryColor.copy(0.3f)) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            categories.forEach { category ->
                CategoryScoreItem(category, theme)
            }
        }
    }
}

@Composable
private fun CategoryScoreItem(category: ScoreCategory, theme: PremiumTheme) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier.size(40.dp).clip(CircleShape).background(category.color.copy(0.2f)),
                    Alignment.Center
                ) {
                    Icon(category.icon, null, Modifier.size(20.dp), category.color)
                }
                Column {
                    Text(category.name, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                    Text("${category.weight}% weight", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(0.6f))
                }
            }
            Text("${category.score}/100", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = category.color)
        }
        
        LinearProgressIndicator(
            progress = category.score / 100f,
            Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
            color = category.color,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}

@Composable
private fun TrendChartCard(trendData: List<Int>, theme: PremiumTheme) {
    GlassCard(Modifier.fillMaxWidth().height(200.dp)) {
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.ShowChart, null, Modifier.size(64.dp), theme.primaryColor.copy(0.5f))
            Spacer(Modifier.height(16.dp))
            Text("Trend Chart", fontSize = 18.sp, fontWeight = FontWeight.Medium)
            Text("Use MPAndroidChart for production", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(0.6f))
        }
    }
}

@Composable
private fun ImprovementTipCard(tip: ImprovementTip, theme: PremiumTheme) {
    GlassCard(Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.Top) {
            Box(
                Modifier.size(48.dp).clip(CircleShape).background(
                    Brush.linearGradient(listOf(tip.color, tip.color.copy(0.7f)))
                ), Alignment.Center
            ) {
                Icon(tip.icon, null, Modifier.size(24.dp), Color.White)
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(tip.title, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text(tip.description, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface.copy(0.7f), lineHeight = 20.sp)
                Text(tip.impact, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = tip.color)
            }
        }
    }
}

// Data Classes
data class HealthScoreData(
    val totalScore: Int = 85,
    val categories: List<ScoreCategory> = listOf(
        ScoreCategory("Activity", 90, 30, Icons.Default.DirectionsRun, Color(0xFF3A86FF)),
        ScoreCategory("Nutrition", 85, 30, Icons.Default.Restaurant, Color(0xFF06FFA5)),
        ScoreCategory("Habits", 80, 20, Icons.Default.CheckCircle, Color(0xFF8338EC)),
        ScoreCategory("Tasks", 75, 20, Icons.Default.Task, Color(0xFFFBBF24))
    ),
    val trendData: List<Int> = listOf(75, 78, 80, 82, 85, 83, 85),
    val improvementTips: List<ImprovementTip> = listOf(
        ImprovementTip(
            "Increase Daily Steps",
            "Try to reach 10,000 steps daily for optimal health",
            "+5 points potential",
            Icons.Default.DirectionsWalk,
            Color(0xFF3A86FF)
        ),
        ImprovementTip(
            "Improve Protein Intake",
            "Aim for 150g of protein daily to support muscle growth",
            "+3 points potential",
            Icons.Default.Restaurant,
            Color(0xFF06FFA5)
        ),
        ImprovementTip(
            "Maintain Habit Streak",
            "Keep your daily habits consistent for better results",
            "+2 points potential",
            Icons.Default.Whatshot,
            Color(0xFF8338EC)
        )
    )
)

data class ScoreCategory(
    val name: String,
    val score: Int,
    val weight: Int,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val color: Color
)

data class ImprovementTip(
    val title: String,
    val description: String,
    val impact: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val color: Color
)

private fun getScoreRating(score: Int): String {
    return when {
        score >= 90 -> "Excellent!"
        score >= 80 -> "Great!"
        score >= 70 -> "Good"
        score >= 60 -> "Fair"
        else -> "Needs Improvement"
    }
}

