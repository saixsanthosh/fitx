package com.fitx.app.ui.screens.workout

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.fitx.app.ui.theme.premium.*
import java.util.*

/**
 * Feature 7: Workout Planner
 * Complete workout planning with templates, exercise logging, and history
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutPlannerScreen(
    theme: PremiumTheme = PremiumThemes.ElectricBlue,
    workouts: List<Workout> = emptyList(),
    templates: List<WorkoutTemplate> = emptyList(),
    onStartWorkout: (WorkoutTemplate) -> Unit = {},
    onViewHistory: () -> Unit = {},
    onBackPressed: () -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf(0) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Workout Planner", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBackPressed) { Icon(Icons.Default.ArrowBack, "Back") } },
                actions = {
                    IconButton(onClick = onViewHistory) {
                        Icon(Icons.Default.History, "History", tint = theme.primaryColor)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Box(Modifier.fillMaxSize()) {
            FloatingParticles(Modifier.fillMaxSize(), theme.primaryColor.copy(alpha = 0.1f))
            
            Column(Modifier.fillMaxSize().padding(padding)) {
                TabRow(selectedTab, Modifier.fillMaxWidth()) {
                    Tab(selectedTab == 0, { selectedTab = 0 }) { Text("Templates", Modifier.padding(16.dp)) }
                    Tab(selectedTab == 1, { selectedTab = 1 }) { Text("My Workouts", Modifier.padding(16.dp)) }
                }
                
                when (selectedTab) {
                    0 -> WorkoutTemplatesTab(templates, theme, onStartWorkout)
                    1 -> MyWorkoutsTab(workouts, theme)
                }
            }
        }
    }
}

@Composable
private fun WorkoutTemplatesTab(
    templates: List<WorkoutTemplate>,
    theme: PremiumTheme,
    onStart: (WorkoutTemplate) -> Unit
) {
    LazyColumn(
        Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(Modifier.height(8.dp)) }
        item { WorkoutStatsCard(theme) }
        item { Text("Workout Templates", fontSize = 20.sp, fontWeight = FontWeight.Bold) }
        
        items(templates) { template ->
            WorkoutTemplateCard(template, theme) { onStart(template) }
        }
        
        item { Spacer(Modifier.height(80.dp)) }
    }
}

@Composable
private fun WorkoutStatsCard(theme: PremiumTheme) {
    HeroCard(Modifier.fillMaxWidth().height(160.dp), listOf(theme.gradientStart, theme.gradientEnd)) {
        Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("This Week", fontSize = 16.sp, color = Color.White.copy(0.9f))
                Text("12", fontSize = 48.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text("Workouts Completed", fontSize = 14.sp, color = Color.White.copy(0.8f))
            }
            Column(verticalArrangement = Arrangement.spacedBy(12.dp), horizontalAlignment = Alignment.End) {
                StatBadge("420 min", Icons.Default.Timer)
                StatBadge("1,240 kcal", Icons.Default.LocalFireDepartment)
                StatBadge("5 PRs", Icons.Default.EmojiEvents)
            }
        }
    }
}

@Composable
private fun StatBadge(text: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Row(
        Modifier.clip(RoundedCornerShape(20.dp)).background(Color.White.copy(0.2f)).padding(horizontal = 12.dp, vertical = 6.dp),
        Arrangement.spacedBy(6.dp), Alignment.CenterVertically
    ) {
        Icon(icon, null, Modifier.size(16.dp), Color.White)
        Text(text, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color.White)
    }
}

@Composable
private fun WorkoutTemplateCard(template: WorkoutTemplate, theme: PremiumTheme, onStart: () -> Unit) {
    GlowingCard(Modifier.fillMaxWidth().clickable(onClick = onStart), theme.primaryColor) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    Modifier.size(56.dp).clip(CircleShape).background(
                        Brush.linearGradient(listOf(theme.primaryColor, theme.secondaryColor))
                    ), Alignment.Center
                ) {
                    Icon(template.icon, null, Modifier.size(28.dp), Color.White)
                }
                Column {
                    Text(template.name, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text("${template.exercises.size} exercises â€¢ ${template.duration} min", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(0.7f))
                    Text(template.category, fontSize = 11.sp, color = theme.primaryColor)
                }
            }
            Icon(Icons.Default.ChevronRight, null, Modifier.size(24.dp), MaterialTheme.colorScheme.onSurface.copy(0.5f))
        }
    }
}

@Composable
private fun MyWorkoutsTab(workouts: List<Workout>, theme: PremiumTheme) {
    LazyColumn(
        Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(Modifier.height(8.dp)) }
        item { Text("Recent Workouts", fontSize = 20.sp, fontWeight = FontWeight.Bold) }
        
        items(workouts) { workout ->
            WorkoutCard(workout, theme)
        }
        
        item { Spacer(Modifier.height(80.dp)) }
    }
}

@Composable
private fun WorkoutCard(workout: Workout, theme: PremiumTheme) {
    GlassCard(Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text(workout.name, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text(workout.date, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(0.6f))
                }
                if (workout.isCompleted) {
                    Icon(Icons.Default.CheckCircle, null, Modifier.size(24.dp), theme.primaryColor)
                }
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                WorkoutStat(Icons.Default.Timer, "${workout.duration} min")
                WorkoutStat(Icons.Default.FitnessCenter, "${workout.exercises} exercises")
                WorkoutStat(Icons.Default.LocalFireDepartment, "${workout.calories} kcal")
            }
        }
    }
}

@Composable
private fun WorkoutStat(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, Modifier.size(16.dp), MaterialTheme.colorScheme.onSurface.copy(0.7f))
        Text(text, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(0.7f))
    }
}

// Data Classes
data class WorkoutTemplate(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val category: String,
    val exercises: List<String>,
    val duration: Int,
    val icon: androidx.compose.ui.graphics.vector.ImageVector = Icons.Default.FitnessCenter
)

data class Workout(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val date: String,
    val duration: Int,
    val exercises: Int,
    val calories: Int,
    val isCompleted: Boolean = true
)

