package com.fitx.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fitx.app.ui.viewmodel.AuthViewModel
import com.fitx.app.ui.viewmodel.SettingsViewModel

@Composable
fun SettingsRoute(
    viewModel: SettingsViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onOpenHealthCheck: () -> Unit
) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val pendingSyncCount by viewModel.pendingSyncCount.collectAsStateWithLifecycle()
    val systemMessage by viewModel.systemMessage.collectAsStateWithLifecycle()
    val authUser by authViewModel.currentUser.collectAsStateWithLifecycle()
    FitxScreenScaffold(topBar = { ScreenTopBar("Settings", onBack) }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "App Controls",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.92f)),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Dark Theme")
                    Switch(
                        checked = settings.darkTheme,
                        onCheckedChange = { viewModel.setTheme(it) }
                    )
                }
            }
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.92f)),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Smart Reminders")
                        Switch(
                            checked = settings.smartRemindersEnabled,
                            onCheckedChange = { viewModel.setSmartReminders(it) }
                        )
                    }
                    Text(
                        "Adaptive schedule: weight reminder ${formatHour(settings.smartReminderHour)}, hydration every ${settings.hydrationIntervalHours}h.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.92f)),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Haptic Feedback")
                    Switch(
                        checked = settings.hapticsEnabled,
                        onCheckedChange = { viewModel.setHaptics(it) }
                    )
                }
            }
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.92f)),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Notifications")
                    Switch(
                        checked = settings.notificationsEnabled,
                        onCheckedChange = { viewModel.setNotifications(it) }
                    )
                }
            }
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.92f)),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Cloud Sync", fontWeight = FontWeight.Bold)
                    Text("Pending changes: $pendingSyncCount", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("Offline changes are queued and synced when internet is available.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Button(
                        onClick = { viewModel.syncNow() },
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text("Sync Now")
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { viewModel.exportBackup() },
                            modifier = Modifier.weight(1f)
                        ) { Text("Export Backup") }
                        Button(
                            onClick = { viewModel.restoreLatestBackup() },
                            modifier = Modifier.weight(1f)
                        ) { Text("Restore Backup") }
                    }
                    Button(onClick = { viewModel.shareDiagnostics() }) {
                        Text("Share Diagnostics")
                    }
                }
            }
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.92f)),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("System Health", fontWeight = FontWeight.Bold)
                    Text("Run one-tap checks for Firebase, API, permissions, map, and battery.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Button(onClick = onOpenHealthCheck) { Text("Open Health Check") }
                }
            }
            if (!systemMessage.isNullOrBlank()) {
                Text(
                    text = systemMessage.orEmpty(),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            if (authUser != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.92f)),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Signed in as", fontWeight = FontWeight.Bold)
                        Text(authUser?.email ?: "Google account", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Button(
                            onClick = { authViewModel.signOut() },
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Text("Sign Out")
                        }
                    }
                }
            }
            Text(
                "Theme, notifications, and haptic preferences are saved in DataStore.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

private fun formatHour(hour24: Int): String {
    val normalized = hour24.coerceIn(0, 23)
    val suffix = if (normalized < 12) "AM" else "PM"
    val display = when {
        normalized == 0 -> 12
        normalized > 12 -> normalized - 12
        else -> normalized
    }
    return "$display:00 $suffix"
}
