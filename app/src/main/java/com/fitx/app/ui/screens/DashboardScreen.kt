package com.fitx.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fitx.app.domain.model.TaskItem
import com.fitx.app.ui.components.AnimatedFitxLogo
import com.fitx.app.ui.components.WeightLineChart
import com.fitx.app.ui.viewmodel.DashboardViewModel
import com.fitx.app.util.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardRoute(
    viewModel: DashboardViewModel = hiltViewModel(),
    onOpenProfile: () -> Unit,
    onOpenActivity: () -> Unit,
    onOpenWeight: () -> Unit,
    onOpenWorkout: () -> Unit,
    onOpenHabits: () -> Unit,
    onOpenPlanner: () -> Unit,
    onOpenNutrition: () -> Unit,
    onOpenSettings: () -> Unit
) {
    val summary by viewModel.summary.collectAsStateWithLifecycle()
    val healthMetrics = summary.healthMetrics
    val taskCount = summary.todayTasks.size.coerceAtLeast(1)
    val completionPercent = ((summary.completedTasks.toFloat() / taskCount) * 100f).toInt().coerceIn(0, 100)
    var animateCards by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { animateCards = true }

    FitxScreenScaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Fitx", fontWeight = FontWeight.ExtraBold)
                        Text(
                            DateUtils.formatEpochDay(DateUtils.todayEpochDay()),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75f)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                AnimatedVisibility(
                    visible = animateCards,
                    enter = fadeIn(animationSpec = tween(180)) + slideInVertically(initialOffsetY = { it / 3 }, animationSpec = tween(220))
                ) {
                    HeroCard(
                        completionPercent = completionPercent,
                        dailyTarget = healthMetrics?.dailyCalorieTarget ?: 0,
                        weeklyGoalText = healthMetrics?.projectedWeeksToGoal?.let { "$it weeks to goal" } ?: "Set profile to unlock projection"
                    )
                }
            }

            item {
                AnimatedVisibility(
                    visible = animateCards,
                    enter = fadeIn(animationSpec = tween(180, delayMillis = 60)) + slideInVertically(initialOffsetY = { it / 3 }, animationSpec = tween(220, delayMillis = 60))
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        StatTile(
                            title = "BMI",
                            value = healthMetrics?.bmi?.let { "%.1f".format(it) } ?: "--",
                            subtitle = "Body index",
                            accent = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.weight(1f)
                        )
                        StatTile(
                            title = "Daily Target",
                            value = healthMetrics?.dailyCalorieTarget?.let { "$it kcal" } ?: "--",
                            subtitle = "Calories",
                            accent = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            item {
                AnimatedVisibility(
                    visible = animateCards,
                    enter = fadeIn(animationSpec = tween(180, delayMillis = 110)) + slideInVertically(initialOffsetY = { it / 3 }, animationSpec = tween(220, delayMillis = 110))
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        StatTile(
                            title = "Activity",
                            value = "${"%.2f".format(summary.todayDistanceMeters / 1000)} km",
                            subtitle = "${summary.todaySteps} steps",
                            accent = Color(0xFF22C55E),
                            modifier = Modifier.weight(1f)
                        )
                        StatTile(
                            title = "Tasks",
                            value = "${summary.completedTasks}/${summary.todayTasks.size}",
                            subtitle = "Completed",
                            accent = Color(0xFF4FA7A0),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            item {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.94f)
                    )
                ) {
                    WeightLineChart(values = summary.weeklyWeights.map { it.weightKg }.reversed())
                }
            }

            item {
                SectionLabel("Daily Systems")
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ModuleCard(
                        title = "Fixed Daily List",
                        subtitle = "Habits and streaks",
                        onClick = onOpenHabits,
                        modifier = Modifier.weight(1f)
                    )
                    ModuleCard(
                        title = "Planner List",
                        subtitle = "Date-based to-do",
                        onClick = onOpenPlanner,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                SectionLabel("Tracking Modules")
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ModuleCard(
                        title = "Weight",
                        subtitle = "Logs and trends",
                        onClick = onOpenWeight,
                        modifier = Modifier.weight(1f)
                    )
                    ModuleCard(
                        title = "Workout",
                        subtitle = "Templates and history",
                        onClick = onOpenWorkout,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ModuleCard(
                        title = "Nutrition",
                        subtitle = "Calories and macros",
                        onClick = onOpenNutrition,
                        modifier = Modifier.weight(1f)
                    )
                    ModuleCard(
                        title = "Cycling & Walking",
                        subtitle = "GPS + step sessions",
                        onClick = onOpenActivity,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ModuleCard(
                        title = "Profile",
                        subtitle = "BMR / TDEE / goals",
                        onClick = onOpenProfile,
                        modifier = Modifier.weight(1f)
                    )
                    ModuleCard(
                        title = "Settings",
                        subtitle = "Theme and reminders",
                        onClick = onOpenSettings,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            if (summary.todayTasks.isNotEmpty()) {
                item { SectionLabel("Today Focus") }
                items(summary.todayTasks.take(4), key = { it.taskId }) { task ->
                    DashboardTaskItem(task = task)
                }
            }
        }
    }
}

@Composable
private fun HeroCard(
    completionPercent: Int,
    dailyTarget: Int,
    weeklyGoalText: String
) {
    val colors = MaterialTheme.colorScheme
    val animatedCompletion by animateFloatAsState(
        targetValue = completionPercent.toFloat(),
        animationSpec = tween(durationMillis = 700),
        label = "dashboard_completion"
    )
    val completionText = animatedCompletion.toInt().coerceIn(0, 100)
    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(
                        listOf(
                            colors.primary.copy(alpha = 0.18f),
                            colors.tertiary.copy(alpha = 0.14f),
                            colors.secondary.copy(alpha = 0.16f)
                        )
                    )
                )
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AnimatedFitxLogo(showWordmark = false, logoSize = 76.dp)
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text("My Plan For Today", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text("$completionText% complete", color = colors.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
                Text("Target $dailyTarget kcal", color = colors.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
                Text(weeklyGoalText, color = colors.onSurfaceVariant, style = MaterialTheme.typography.labelLarge)
            }
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(colors.surfaceVariant.copy(alpha = 0.75f), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "$completionText%",
                    style = MaterialTheme.typography.titleMedium,
                    color = colors.onSurface,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun StatTile(
    title: String,
    value: String,
    subtitle: String,
    accent: Color,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surfaceVariant.copy(alpha = 0.95f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            accent.copy(alpha = 0.16f),
                            colors.surfaceVariant.copy(alpha = 0.95f)
                        )
                    )
                )
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Text(title.uppercase(), style = MaterialTheme.typography.labelLarge, color = colors.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.headlineSmall, color = colors.onSurface, fontWeight = FontWeight.Bold)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = colors.onSurfaceVariant)
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ModuleCard(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surface.copy(alpha = 0.98f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        listOf(
                            colors.surface.copy(alpha = 0.98f),
                            colors.surfaceVariant.copy(alpha = 0.84f)
                        )
                    )
                )
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = colors.onSurface)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = colors.onSurfaceVariant)
        }
    }
}

@Composable
private fun DashboardTaskItem(task: TaskItem) {
    val colors = MaterialTheme.colorScheme
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surfaceVariant.copy(alpha = 0.92f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(task.title, fontWeight = FontWeight.SemiBold, color = colors.onSurface)
                if (task.description.isNotBlank()) {
                    Text(task.description, style = MaterialTheme.typography.bodySmall, color = colors.onSurfaceVariant)
                }
            }
            val status = if (task.isCompleted) "Done" else "Pending"
            Text(status, style = MaterialTheme.typography.labelLarge, color = if (task.isCompleted) Color(0xFF22C55E) else colors.secondary)
        }
    }
}
