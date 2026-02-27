package com.fitx.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fitx.app.ui.viewmodel.DashboardViewModel
import kotlin.math.abs

@Composable
fun WeeklyInsightsRoute(
    viewModel: DashboardViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val summary = viewModel.summary.collectAsStateWithLifecycle().value
    val scoreProgress = (summary.todayScore / 100f).coerceIn(0f, 1f)
    val distanceKm = summary.weeklyDistanceMeters / 1000.0
    val coachTitle = when {
        summary.todayScore >= 85 -> "Excellent week"
        summary.todayScore >= 70 -> "Strong momentum"
        summary.todayScore >= 55 -> "Good base, optimize next"
        else -> "Reset and rebuild"
    }
    val coachMessage = when {
        summary.todayScore >= 85 -> "Keep consistency high. Push one extra workout or longer activity block next week."
        summary.todayScore >= 70 -> "Increase task completion and nutrition precision to move into top performance."
        summary.todayScore >= 55 -> "Add one planned activity day and keep meals closer to target calories."
        else -> "Start with basics: complete tasks, log meals daily, and walk at least 20 minutes."
    }

    FitxScreenScaffold(topBar = { ScreenTopBar("Weekly Insights", onBack) }) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.92f)
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Today Score", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text("${summary.todayScore}/100", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold)
                        LinearProgressIndicator(
                            progress = { scoreProgress },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text(
                            "Activity ${summary.todayScoreBreakdown.activity} • Nutrition ${summary.todayScoreBreakdown.nutrition} • Tasks ${summary.todayScoreBreakdown.tasks}",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    InsightTile(
                        title = "Distance",
                        value = "${"%.1f".format(distanceKm)} km",
                        subtitle = "Last 7 days",
                        modifier = Modifier.weight(1f)
                    )
                    InsightTile(
                        title = "Sessions",
                        value = summary.weeklySessionCount.toString(),
                        subtitle = "${summary.weeklyActiveDays} active days",
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    InsightTile(
                        title = "Calories Burned",
                        value = "${summary.weeklyCaloriesBurned}",
                        subtitle = "Weekly total",
                        modifier = Modifier.weight(1f)
                    )
                    InsightTile(
                        title = "Steps",
                        value = "${summary.weeklySteps}",
                        subtitle = "Weekly total",
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                val delta = summary.weeklyWeightChangeKg
                val weightText = when {
                    delta == null -> "Not enough logs"
                    delta > 0 -> "+${"%.1f".format(delta)} kg"
                    else -> "${"%.1f".format(delta)} kg"
                }
                val trend = when {
                    delta == null -> "Add at least 2 weekly entries"
                    abs(delta) < 0.2 -> "Stable trend"
                    delta > 0 -> "Upward trend"
                    else -> "Downward trend"
                }
                InsightTile(
                    title = "Weight Trend",
                    value = weightText,
                    subtitle = trend,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.92f)),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text("Weekly Coach", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(coachTitle, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        Text(coachMessage, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

@Composable
private fun InsightTile(
    title: String,
    value: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f)),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(title, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.labelMedium)
            Text(value, color = MaterialTheme.colorScheme.onSurface, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
        }
    }
}
