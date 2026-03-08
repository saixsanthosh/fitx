package com.fitx.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fitx.app.ui.screens.achievements.AchievementsScreen
import com.fitx.app.ui.screens.achievements.getSampleAchievements
import com.fitx.app.ui.screens.health.HealthScoreScreen
import com.fitx.app.ui.screens.premium.PremiumDashboardScreen
import com.fitx.app.ui.screens.premium.ThemeSettingsScreen
import com.fitx.app.ui.screens.reports.WeeklyReportScreen
import com.fitx.app.ui.screens.water.WaterData
import com.fitx.app.ui.screens.water.WaterIntake
import com.fitx.app.ui.screens.water.WaterTrackerScreen
import com.fitx.app.ui.theme.premium.PremiumThemes
import com.fitx.app.ui.viewmodel.SettingsViewModel

@Composable
fun UiShowcaseRoute(
    onBack: () -> Unit,
    onOpenPremiumDashboard: () -> Unit,
    onOpenThemeLab: () -> Unit,
    onOpenWater: () -> Unit,
    onOpenHealthScore: () -> Unit,
    onOpenAchievements: () -> Unit,
    onOpenWeeklyReport: () -> Unit
) {
    FitxScreenScaffold(topBar = { ScreenTopBar("UI Showcase", onBack) }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Premium UI Routes",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "These screens are now reachable without replacing the stable data-backed flows used by the main app.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall
            )
            ShowcaseLinkCard("Premium Dashboard", "Animated showcase dashboard", onOpenPremiumDashboard)
            ShowcaseLinkCard("Theme Lab", "Preview premium theme selector", onOpenThemeLab)
            ShowcaseLinkCard("Water Tracker", "Premium hydration UI", onOpenWater)
            ShowcaseLinkCard("Health Score", "Premium health score UI", onOpenHealthScore)
            ShowcaseLinkCard("Achievements", "Badges and unlock visuals", onOpenAchievements)
            ShowcaseLinkCard("Weekly Report", "Premium analytics report", onOpenWeeklyReport)
        }
    }
}

@Composable
fun UiPremiumDashboardRoute(onBack: () -> Unit) {
    FitxScreenScaffold(topBar = { ScreenTopBar("Premium Dashboard", onBack) }) { padding ->
        PremiumDashboardScreen(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        )
    }
}

@Composable
fun UiThemeLabRoute(
    onBack: () -> Unit,
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val settings by settingsViewModel.settings.collectAsStateWithLifecycle()
    val selectedTheme = remember { mutableStateOf(PremiumThemes.ElectricBlue) }

    ThemeSettingsScreen(
        currentTheme = selectedTheme.value,
        onThemeSelected = { selectedTheme.value = it },
        isDarkMode = settings.darkTheme,
        onDarkModeToggle = settingsViewModel::setTheme,
        onBackPressed = onBack
    )
}

@Composable
fun UiWaterRoute(onBack: () -> Unit) {
    val intakeHistory = remember {
        mutableStateListOf(
            WaterIntake(amount = 500),
            WaterIntake(amount = 250),
            WaterIntake(amount = 300)
        )
    }
    val total = intakeHistory.sumOf { it.amount }
    val goal = 2500
    val remaining = (goal - total).coerceAtLeast(0)
    val glassesRemaining = ((remaining + 249) / 250).coerceAtLeast(0)

    WaterTrackerScreen(
        waterData = WaterData(
            current = total,
            goal = goal,
            remaining = remaining,
            glassesRemaining = glassesRemaining
        ),
        intakeHistory = intakeHistory,
        onAddWater = { amount ->
            intakeHistory.add(0, WaterIntake(amount = amount))
        },
        onDeleteIntake = { intake ->
            intakeHistory.remove(intake)
        },
        onBackPressed = onBack
    )
}

@Composable
fun UiHealthScoreRoute(onBack: () -> Unit) {
    HealthScoreScreen(onBackPressed = onBack)
}

@Composable
fun UiAchievementsRoute(onBack: () -> Unit) {
    AchievementsScreen(
        achievements = getSampleAchievements(),
        onBackPressed = onBack
    )
}

@Composable
fun UiWeeklyReportRoute(onBack: () -> Unit) {
    WeeklyReportScreen(onBackPressed = onBack)
}

@Composable
private fun ShowcaseLinkCard(
    title: String,
    subtitle: String,
    onOpen: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.92f)),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(title, fontWeight = FontWeight.Bold)
            Text(
                text = subtitle,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall
            )
            Button(onClick = onOpen) {
                Text("Open")
            }
        }
    }
}
