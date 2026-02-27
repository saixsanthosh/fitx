package com.fitx.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fitx.app.ui.viewmodel.AuthViewModel
import com.fitx.app.ui.viewmodel.SettingsViewModel

@Composable
fun SettingsRoute(
    viewModel: SettingsViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val pendingSyncCount by viewModel.pendingSyncCount.collectAsStateWithLifecycle()
    val authUser by authViewModel.currentUser.collectAsStateWithLifecycle()
    FitxScreenScaffold(topBar = { ScreenTopBar("Settings", onBack) }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
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
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
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
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
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
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Cloud Sync")
                    Text("Pending changes: $pendingSyncCount")
                    Text("Offline changes are queued and synced when internet is available.")
                    Button(
                        onClick = { viewModel.syncNow() },
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text("Sync Now")
                    }
                }
            }
            if (authUser != null) {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Signed in as")
                        Text(authUser?.email ?: "Google account")
                        Button(
                            onClick = { authViewModel.signOut() },
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Text("Sign Out")
                        }
                    }
                }
            }
            Text("Theme, notifications, and haptic preferences are saved in DataStore.")
        }
    }
}
