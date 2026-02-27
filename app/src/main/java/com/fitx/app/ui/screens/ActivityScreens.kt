@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.fitx.app.ui.screens

import android.view.HapticFeedbackConstants
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fitx.app.domain.model.ActivitySession
import com.fitx.app.domain.model.ActivityType
import com.fitx.app.ui.components.MetricCard
import com.fitx.app.ui.components.RouteMapPreview
import com.fitx.app.ui.viewmodel.ActivityViewModel
import com.fitx.app.util.ActivityShareUtil
import com.fitx.app.util.BatteryOptimizationHelper
import com.fitx.app.util.DateUtils
import com.fitx.app.util.PermissionUtils
import kotlin.math.floor

@Composable
fun ActivityStartRoute(
    viewModel: ActivityViewModel = hiltViewModel(),
    onOpenLive: () -> Unit,
    onOpenHistory: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val state by viewModel.trackingState.collectAsStateWithLifecycle()
    var selectedType by remember { mutableStateOf(ActivityType.WALKING) }
    val hasLocation = PermissionUtils.hasLocationPermissions(context)
    val hasStepSensor = PermissionUtils.hasStepCounterSensor(context)
    val hasSteps = if (hasStepSensor) {
        PermissionUtils.hasActivityRecognitionPermission(context)
    } else {
        true
    }
    val permissionsLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        val hasLocationNow = PermissionUtils.hasLocationPermissions(context)
        val hasStepsNow = if (hasStepSensor) {
            PermissionUtils.hasActivityRecognitionPermission(context)
        } else {
            true
        }
        if (hasLocationNow && hasStepsNow) {
            viewModel.startTracking(selectedType)
            onOpenLive()
        }
    }

    FitxScreenScaffold(topBar = { ScreenTopBar("Cycling & Walking", onBack) }) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Card(
                    shape = RoundedCornerShape(26.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.20f),
                                        MaterialTheme.colorScheme.tertiary.copy(alpha = 0.14f)
                                    )
                                )
                            )
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text("Start New Session", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text("Track distance, speed, calories and steps with GPS + sensors.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        if (state.isTracking) {
                            Text(
                                "Session already running: ${state.activityType.name.lowercase().replaceFirstChar { it.uppercase() }}",
                                color = MaterialTheme.colorScheme.secondary,
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ActivityTypeCard("Walking", "Step-friendly mode", selectedType == ActivityType.WALKING, Icons.AutoMirrored.Filled.DirectionsWalk, { selectedType = ActivityType.WALKING }, Modifier.weight(1f))
                    ActivityTypeCard("Cycling", "GPS speed mode", selectedType == ActivityType.CYCLING, Icons.AutoMirrored.Filled.DirectionsBike, { selectedType = ActivityType.CYCLING }, Modifier.weight(1f))
                }
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    PermissionChipCard("Location", hasLocation, Modifier.weight(1f))
                    PermissionChipCard(if (hasStepSensor) "Activity Sensor" else "Steps Optional", hasSteps, Modifier.weight(1f))
                }
            }
            if (!hasStepSensor) {
                item {
                    Text(
                        "Step sensor not available on this phone. Distance and GPS tracking still work.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            item {
                Button(
                    onClick = {
                        if (hasLocation && hasSteps) {
                            viewModel.startTracking(selectedType)
                            onOpenLive()
                        } else {
                            permissionsLauncher.launch(requiredTrackingPermissions(includeActivityRecognition = hasStepSensor))
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                    Text("Start ${selectedType.name.lowercase().replaceFirstChar { it.uppercase() }} Session")
                }
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilledTonalButton(onClick = onOpenHistory, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Default.History, contentDescription = null)
                        Text("History")
                    }
                    FilledTonalButton(onClick = { BatteryOptimizationHelper.requestIgnoreOptimization(context) }, modifier = Modifier.weight(1f)) {
                        Text("Battery Safe")
                    }
                }
            }
        }
    }
}

@Composable
fun LiveTrackingRoute(
    viewModel: ActivityViewModel = hiltViewModel(),
    onStop: () -> Unit,
    onBack: () -> Unit
) {
    val state by viewModel.trackingState.collectAsStateWithLifecycle()
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val view = LocalView.current
    val distanceKm = state.distanceMeters / 1000.0
    val speedKmh = state.averageSpeedMps * 3.6
    val distanceGoalKm = if (state.activityType == ActivityType.WALKING) 5.0 else 20.0
    val progress = (distanceKm / distanceGoalKm).toFloat().coerceIn(0f, 1f)
    val pulse = rememberTrackingPulse(state.isTracking)

    val milestoneStepKm = if (state.activityType == ActivityType.WALKING) 1 else 2
    val milestoneCount = floor(distanceKm / milestoneStepKm.toDouble()).toInt()
    var lastHapticMilestone by rememberSaveable { mutableStateOf(0) }

    LaunchedEffect(state.isTracking) {
        if (!state.isTracking) lastHapticMilestone = 0
    }
    LaunchedEffect(milestoneCount, settings.hapticsEnabled, state.isTracking) {
        if (state.isTracking && settings.hapticsEnabled && milestoneCount > lastHapticMilestone && milestoneCount > 0) {
            view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
            lastHapticMilestone = milestoneCount
        }
    }

    val primaryTitle = if (state.activityType == ActivityType.CYCLING) "Current Speed" else "Distance"
    val primaryValue = if (state.activityType == ActivityType.CYCLING) "${"%.2f".format(speedKmh)} km/h" else "${"%.2f".format(distanceKm)} km"
    val secondaryValue = if (state.activityType == ActivityType.CYCLING) "${"%.2f".format(distanceKm)} km" else "${"%.2f".format(speedKmh)} km/h"
    val secondaryTitle = if (state.activityType == ActivityType.CYCLING) "Distance" else "Current Speed"

    FitxScreenScaffold(topBar = { ScreenTopBar("Live Tracking", onBack) }) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.18f),
                                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.95f)
                                    )
                                )
                            )
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text(state.activityType.name.lowercase().replaceFirstChar { it.uppercase() }, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            Box(
                                modifier = Modifier
                                    .size((14.dp.value * pulse).dp)
                                    .background(if (state.isTracking) Color(0xFF22C55E) else MaterialTheme.colorScheme.outline, RoundedCornerShape(50))
                            )
                        }
                        Text(primaryTitle, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(primaryValue, style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.SemiBold)
                        Text("${"%.2f".format(distanceKm)} / ${"%.1f".format(distanceGoalKm)} km goal", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        androidx.compose.material3.LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth())
                    }
                }
            }
            item { RouteMapPreview(points = state.pathPoints, height = 170.dp) }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ActivityMiniMetric(Icons.Default.Timer, "Duration", DateUtils.formatDuration(state.durationSeconds), Modifier.weight(1f))
                    ActivityMiniMetric(Icons.Default.LocationOn, secondaryTitle, secondaryValue, Modifier.weight(1f))
                }
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ActivityMiniMetric(Icons.Default.LocalFireDepartment, "Calories", "${state.caloriesBurned} kcal", Modifier.weight(1f))
                    ActivityMiniMetric(Icons.AutoMirrored.Filled.DirectionsWalk, "Steps", state.steps.toString(), Modifier.weight(1f))
                }
            }
            item {
                Button(
                    onClick = {
                        viewModel.stopTracking()
                        onStop()
                    },
                    enabled = state.isTracking,
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Stop Session") }
            }
        }
    }
}

@Composable
fun ActivityHistoryRoute(
    viewModel: ActivityViewModel = hiltViewModel(),
    onSessionClick: (Long) -> Unit,
    onBack: () -> Unit
) {
    val sessions by viewModel.sessions.collectAsStateWithLifecycle()
    FitxScreenScaffold(topBar = { ScreenTopBar("Activity History", onBack) }) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (sessions.isEmpty()) {
                item {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.92f))
                    ) {
                        Text(
                            "No sessions yet. Start a walking or cycling session first.",
                            modifier = Modifier.padding(14.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            items(sessions, key = { it.sessionId }) { session ->
                val accent = if (session.activityType == ActivityType.WALKING) Color(0xFF22C55E) else Color(0xFF38BDF8)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    onClick = { onSessionClick(session.sessionId) }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.horizontalGradient(
                                    listOf(accent.copy(alpha = 0.16f), MaterialTheme.colorScheme.surface.copy(alpha = 0.98f))
                                )
                            )
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(session.activityType.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Text("${"%.2f".format(session.distanceMeters / 1000)} km  |  ${DateUtils.formatDuration(session.durationSeconds)}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            "${session.caloriesBurned} kcal  |  ${session.steps} steps  |  ${"%.2f".format(session.averageSpeedMps * 3.6)} km/h",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SessionDetailRoute(
    viewModel: ActivityViewModel,
    onBack: () -> Unit
) {
    val detail by viewModel.selectedSessionDetail.collectAsStateWithLifecycle()
    FitxScreenScaffold(topBar = { ScreenTopBar("Session Details", onBack) }) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (detail == null) {
                item { Text("Loading...", color = MaterialTheme.colorScheme.onSurfaceVariant) }
            } else {
                val session = detail!!.session
                item { RouteMapPreview(points = detail!!.points) }
                item { MetricCard("Activity", session.activityType.name) }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        MetricCard("Distance", "${"%.2f".format(session.distanceMeters / 1000)} km", modifier = Modifier.weight(1f))
                        MetricCard("Duration", DateUtils.formatDuration(session.durationSeconds), modifier = Modifier.weight(1f))
                    }
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        MetricCard("Avg Speed", "${"%.2f".format(session.averageSpeedMps * 3.6)} km/h", modifier = Modifier.weight(1f))
                        MetricCard("Calories", "${session.caloriesBurned} kcal", modifier = Modifier.weight(1f))
                    }
                }
                item { MetricCard("Steps", session.steps.toString(), "GPS points ${detail!!.points.size}") }
            }
        }
    }
}

@Composable
fun ActivityFinishRoute(
    viewModel: ActivityViewModel = hiltViewModel(),
    onOpenHistory: () -> Unit,
    onBackToStart: () -> Unit
) {
    val sessions by viewModel.sessions.collectAsStateWithLifecycle()
    val detail by viewModel.selectedSessionDetail.collectAsStateWithLifecycle()
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val view = LocalView.current
    var showSharePreview by rememberSaveable { mutableStateOf(false) }
    var hapticDoneForSession by rememberSaveable { mutableStateOf(0L) }

    val latestSession = sessions.firstOrNull()
    val summaryMessage = latestSession?.let { buildAppreciationMessage(it) }
    val primaryTarget = latestSession?.let {
        if (it.activityType == ActivityType.CYCLING) it.averageSpeedMps * 3.6 else it.distanceMeters / 1000.0
    } ?: 0.0
    val animatedPrimary by animateFloatAsState(
        targetValue = primaryTarget.toFloat(),
        animationSpec = tween(durationMillis = 780, easing = androidx.compose.animation.core.FastOutSlowInEasing),
        label = "primary_count_up"
    )
    val pulse = rememberPrimaryPulse()

    LaunchedEffect(latestSession?.sessionId) {
        if (latestSession != null) viewModel.loadSession(latestSession.sessionId)
    }
    LaunchedEffect(latestSession?.sessionId, settings.hapticsEnabled) {
        val sessionId = latestSession?.sessionId ?: return@LaunchedEffect
        if (settings.hapticsEnabled && hapticDoneForSession != sessionId) {
            view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
            hapticDoneForSession = sessionId
        }
    }

    FitxScreenScaffold(topBar = { ScreenTopBar("Session Summary", onBackToStart) }) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (latestSession == null) {
                item { MetricCard("No session found", "Start a new walk or ride", "Your next completed session will appear here.") }
            } else {
                item {
                    Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    Brush.verticalGradient(
                                        listOf(
                                            MaterialTheme.colorScheme.primary.copy(alpha = 0.18f),
                                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.94f)
                                        )
                                    )
                                )
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text("Great work", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text(summaryMessage.orEmpty(), color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                text = if (latestSession.activityType == ActivityType.CYCLING) "${"%.2f".format(animatedPrimary)} km/h avg" else "${"%.2f".format(animatedPrimary)} km",
                                style = MaterialTheme.typography.displaySmall,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier
                                    .padding(top = 2.dp)
                                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f), RoundedCornerShape(14.dp))
                                    .padding(horizontal = (12.dp.value * pulse).dp, vertical = 6.dp)
                            )
                        }
                    }
                }
                if (detail != null) item { RouteMapPreview(points = detail!!.points, height = 180.dp) }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        MetricCard("Duration", DateUtils.formatDuration(latestSession.durationSeconds), modifier = Modifier.weight(1f))
                        MetricCard("Calories", "${latestSession.caloriesBurned} kcal", modifier = Modifier.weight(1f))
                    }
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        MetricCard("Distance", "${"%.2f".format(latestSession.distanceMeters / 1000)} km", modifier = Modifier.weight(1f))
                        MetricCard("Steps", latestSession.steps.toString(), modifier = Modifier.weight(1f))
                    }
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = { showSharePreview = true }, modifier = Modifier.weight(1f)) {
                            Icon(Icons.Default.Share, contentDescription = null)
                            Text("Share", modifier = Modifier.padding(start = 6.dp))
                        }
                        FilledTonalButton(onClick = onOpenHistory, modifier = Modifier.weight(1f)) {
                            Text("Open History")
                        }
                    }
                }
            }
        }
    }

    if (showSharePreview && latestSession != null) {
        AlertDialog(
            onDismissRequest = { showSharePreview = false },
            title = { Text("Share Preview") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("A dark share card PNG will be generated with this session summary.")
                    Text(
                        "${latestSession.activityType.name.lowercase().replaceFirstChar { it.uppercase() }} | " +
                            "${"%.2f".format(latestSession.distanceMeters / 1000)} km | ${latestSession.caloriesBurned} kcal"
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    ActivityShareUtil.shareSessionCard(context, latestSession)
                    showSharePreview = false
                }) { Text("Share PNG") }
            },
            dismissButton = { TextButton(onClick = { showSharePreview = false }) { Text("Cancel") } }
        )
    }
}

private fun buildAppreciationMessage(session: ActivitySession): String {
    val distanceKm = session.distanceMeters / 1000.0
    val pace = if (session.durationSeconds > 0) (session.durationSeconds / 60.0) / distanceKm.coerceAtLeast(0.1) else 0.0
    return when {
        session.activityType == ActivityType.CYCLING && distanceKm >= 20 -> "Massive ride. You crushed the long-distance target today."
        session.activityType == ActivityType.CYCLING && session.averageSpeedMps * 3.6 >= 22 -> "Strong pace on the bike. Consistent speed and control."
        session.activityType == ActivityType.WALKING && distanceKm >= 8 -> "Excellent walk. High distance with steady endurance."
        session.activityType == ActivityType.WALKING && session.steps >= 10000 -> "10k+ steps milestone reached. Great consistency."
        session.caloriesBurned >= 500 -> "High-energy session complete. Your effort level is clearly improving."
        pace > 0.0 -> "Nice work. You kept a ${"%.1f".format(pace)} min/km pace."
        else -> "Session complete. Small wins done daily make major progress."
    }
}

@Composable
private fun ActivityTypeCard(
    title: String,
    subtitle: String,
    selected: Boolean,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    Card(
        modifier = modifier,
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) colors.primary.copy(alpha = 0.18f) else colors.surfaceVariant.copy(alpha = 0.9f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Icon(icon, contentDescription = null, tint = if (selected) colors.primary else colors.onSurfaceVariant)
            Text(title, fontWeight = FontWeight.Bold, color = colors.onSurface)
            Text(subtitle, color = colors.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun PermissionChipCard(
    title: String,
    ok: Boolean,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val accent = if (ok) Color(0xFF22C55E) else colors.secondary
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surfaceVariant.copy(alpha = 0.9f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(title, color = colors.onSurface)
            FilterChip(
                selected = ok,
                onClick = {},
                enabled = false,
                label = { Text(if (ok) "Ready" else "Needed") },
                leadingIcon = {
                    Icon(
                        imageVector = if (ok) Icons.Default.PlayArrow else Icons.Default.History,
                        contentDescription = null,
                        tint = accent
                    )
                }
            )
        }
    }
}

@Composable
private fun ActivityMiniMetric(
    icon: ImageVector,
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surfaceVariant.copy(alpha = 0.9f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(icon, contentDescription = null, tint = colors.tertiary)
            Text(title.uppercase(), style = MaterialTheme.typography.labelMedium, color = colors.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = colors.onSurface)
        }
    }
}

@Composable
private fun rememberTrackingPulse(isTracking: Boolean): Float {
    val transition = rememberInfiniteTransition(label = "live_pulse")
    val scale by transition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(animation = tween(900, easing = LinearEasing), repeatMode = RepeatMode.Reverse),
        label = "pulse_scale"
    )
    return if (isTracking) scale else 1f
}

@Composable
private fun rememberPrimaryPulse(): Float {
    val transition = rememberInfiniteTransition(label = "summary_pulse")
    val scale by transition.animateFloat(
        initialValue = 1f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(animation = tween(900, easing = LinearEasing), repeatMode = RepeatMode.Reverse),
        label = "summary_scale"
    )
    return scale
}
