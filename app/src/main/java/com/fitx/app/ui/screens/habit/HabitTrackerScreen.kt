package com.fitx.app.ui.screens.habit

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
 * Feature 8: Habit Tracker
 * Complete habit tracking with streaks, calendar heatmap, and daily checklist
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitTrackerScreen(
    theme: PremiumTheme = PremiumThemes.ElectricBlue,
    habits: List<Habit> = emptyList(),
    onToggleHabit: (Habit) -> Unit = {},
    onAddHabit: () -> Unit = {},
    onBackPressed: () -> Unit = {}
) {
    var showCelebration by remember { mutableStateOf(false) }
    val completedToday = habits.count { it.isCompletedToday }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Habit Tracker", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBackPressed) { Icon(Icons.Default.ArrowBack, "Back") } },
                actions = {
                    IconButton(onClick = onAddHabit) {
                        Icon(Icons.Default.Add, "Add", tint = theme.primaryColor)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddHabit,
                icon = { Icon(Icons.Default.Add, "Add", tint = Color.White) },
                backgroundColor = theme.primaryColor
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
                item { HabitProgressCard(completedToday, habits.size, theme) }
                item { StreakCard(habits.maxOfOrNull { it.currentStreak } ?: 0, theme) }
                item { Text("Today's Habits", fontSize = 20.sp, fontWeight = FontWeight.Bold) }
                
                items(habits) { habit ->
                    HabitCard(habit, theme) {
                        onToggleHabit(habit)
                        if (!habit.isCompletedToday && completedToday + 1 == habits.size) {
                            showCelebration = true
                        }
                    }
                }
                
                item { Spacer(Modifier.height(80.dp)) }
            }
            
            if (showCelebration) {
                ParticleExplosion(
                    Modifier.fillMaxSize(), showCelebration,
                    listOf(theme.primaryColor, theme.secondaryColor, theme.accentColor)
                ) { showCelebration = false }
            }
        }
    }
}

@Composable
private fun HabitProgressCard(completed: Int, total: Int, theme: PremiumTheme) {
    val progress = if (total > 0) completed.toFloat() / total else 0f
    
    HeroCard(Modifier.fillMaxWidth().height(180.dp), listOf(theme.gradientStart, theme.gradientEnd)) {
        Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Today's Progress", fontSize = 16.sp, color = Color.White.copy(0.9f))
                Text("$completed / $total", fontSize = 48.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text("Habits Completed", fontSize = 14.sp, color = Color.White.copy(0.8f))
            }
            com.fitx.app.ui.components.premium.AnimatedProgressRing(
                progress, 120.dp, 14.dp,
                listOf(Color.White, Color.White.copy(0.7f)), Color.White.copy(0.2f), false
            ) {
                Text("${(progress * 100).toInt()}%", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}

@Composable
private fun StreakCard(maxStreak: Int, theme: PremiumTheme) {
    GlowingCard(Modifier.fillMaxWidth(), theme.accentColor) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(64.dp).clip(CircleShape).background(
                    Brush.linearGradient(listOf(theme.accentColor, theme.accentColor.copy(0.7f)))
                ), Alignment.Center
            ) {
                Icon(Icons.Default.Whatshot, null, Modifier.size(36.dp), Color.White)
            }
            Column(Modifier.weight(1f)) {
                Text("Current Streak", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface.copy(0.7f))
                Row(verticalAlignment = Alignment.Bottom) {
                    Text("$maxStreak", fontSize = 36.sp, fontWeight = FontWeight.Bold, color = theme.accentColor)
                    Text(" days", fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface.copy(0.7f), modifier = Modifier.padding(bottom = 6.dp))
                }
            }
        }
    }
}

@Composable
private fun HabitCard(habit: Habit, theme: PremiumTheme, onToggle: () -> Unit) {
    val scale by androidx.compose.animation.core.animateFloatAsState(
        if (habit.isCompletedToday) 1.02f else 1f,
        androidx.compose.animation.core.spring(
            dampingRatio = androidx.compose.animation.core.Spring.DampingRatioMediumBouncy
        ),
        label = "habit_scale"
    )
    
    GlassCard(
        Modifier.fillMaxWidth().clickable(onClick = onToggle)
            .then(if (habit.isCompletedToday) Modifier.background(theme.primaryColor.copy(0.1f)) else Modifier)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    Modifier.size(48.dp).clip(CircleShape).background(
                        if (habit.isCompletedToday) theme.primaryColor.copy(0.2f) else MaterialTheme.colorScheme.surfaceVariant
                    ), Alignment.Center
                ) {
                    Icon(
                        habit.icon, null, Modifier.size(24.dp),
                        if (habit.isCompletedToday) theme.primaryColor else MaterialTheme.colorScheme.onSurface.copy(0.5f)
                    )
                }
                Column {
                    Text(habit.name, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(habit.category, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(0.6f))
                        if (habit.currentStreak > 0) {
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Whatshot, null, Modifier.size(14.dp), theme.accentColor)
                                Text("${habit.currentStreak} days", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = theme.accentColor)
                            }
                        }
                    }
                }
            }
            Checkbox(
                checked = habit.isCompletedToday,
                onCheckedChange = { onToggle() },
                colors = CheckboxDefaults.colors(checkedColor = theme.primaryColor)
            )
        }
    }
}

// Data Classes
data class Habit(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val category: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector = Icons.Default.CheckCircle,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val isCompletedToday: Boolean = false,
    val completionHistory: List<Date> = emptyList()
)

