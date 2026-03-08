package com.fitx.app.ui.screens.settings

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

/**
 * Feature 29: Complete Settings - UNBEATABLE DESIGN
 * Comprehensive settings with all options and beautiful UI
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompleteSettingsScreen(
    theme: PremiumTheme = PremiumThemes.ElectricBlue,
    settings: AppSettings = AppSettings(),
    onSettingChanged: (String, Any) -> Unit = { _, _ -> },
    onNavigateToTheme: () -> Unit = {},
    onNavigateToNotifications: () -> Unit = {},
    onNavigateToBackup: () -> Unit = {},
    onLogout: () -> Unit = {},
    onDeleteAccount: () -> Unit = {},
    onBackPressed: () -> Unit = {}
) {
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", fontWeight = FontWeight.Bold) },
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
                
                // Profile Section
                item {
                    ProfileCard(theme)
                }
                
                // Appearance Section
                item {
                    SettingsSection("Appearance", Icons.Default.Palette)
                }
                
                item {
                    SettingItem(
                        title = "Theme",
                        subtitle = "Customize app colors",
                        icon = Icons.Default.ColorLens,
                        onClick = onNavigateToTheme
                    )
                }
                
                item {
                    SettingToggle(
                        title = "Dark Mode",
                        subtitle = "Use dark theme",
                        icon = Icons.Default.DarkMode,
                        checked = settings.isDarkMode,
                        onCheckedChange = { onSettingChanged("darkMode", it) },
                        theme = theme
                    )
                }
                
                item {
                    SettingToggle(
                        title = "Haptic Feedback",
                        subtitle = "Vibration on interactions",
                        icon = Icons.Default.Vibration,
                        checked = settings.hapticFeedback,
                        onCheckedChange = { onSettingChanged("haptic", it) },
                        theme = theme
                    )
                }
                
                // Notifications Section
                item {
                    SettingsSection("Notifications", Icons.Default.Notifications)
                }
                
                item {
                    SettingItem(
                        title = "Notification Settings",
                        subtitle = "Manage all notifications",
                        icon = Icons.Default.NotificationsActive,
                        onClick = onNavigateToNotifications
                    )
                }
                
                item {
                    SettingToggle(
                        title = "Water Reminders",
                        subtitle = "Remind to drink water",
                        icon = Icons.Default.WaterDrop,
                        checked = settings.waterReminders,
                        onCheckedChange = { onSettingChanged("waterReminders", it) },
                        theme = theme
                    )
                }
                
                item {
                    SettingToggle(
                        title = "Activity Reminders",
                        subtitle = "Remind to stay active",
                        icon = Icons.Default.DirectionsRun,
                        checked = settings.activityReminders,
                        onCheckedChange = { onSettingChanged("activityReminders", it) },
                        theme = theme
                    )
                }
                
                // Goals Section
                item {
                    SettingsSection("Goals", Icons.Default.Flag)
                }
                
                item {
                    SettingSlider(
                        title = "Daily Steps Goal",
                        value = settings.stepsGoal,
                        range = 5000f..20000f,
                        onValueChange = { onSettingChanged("stepsGoal", it.toInt()) },
                        theme = theme
                    )
                }
                
                item {
                    SettingSlider(
                        title = "Daily Calorie Goal",
                        value = settings.calorieGoal.toFloat(),
                        range = 1500f..3000f,
                        onValueChange = { onSettingChanged("calorieGoal", it.toInt()) },
                        theme = theme
                    )
                }
                
                item {
                    SettingSlider(
                        title = "Daily Water Goal (glasses)",
                        value = settings.waterGoal.toFloat(),
                        range = 4f..12f,
                        onValueChange = { onSettingChanged("waterGoal", it.toInt()) },
                        theme = theme
                    )
                }
                
                // Units Section
                item {
                    SettingsSection("Units", Icons.Default.Straighten)
                }
                
                item {
                    SettingToggle(
                        title = "Metric System",
                        subtitle = "Use km, kg, cm",
                        icon = Icons.Default.Public,
                        checked = settings.useMetric,
                        onCheckedChange = { onSettingChanged("useMetric", it) },
                        theme = theme
                    )
                }
                
                // Data & Privacy Section
                item {
                    SettingsSection("Data & Privacy", Icons.Default.Security)
                }
                
                item {
                    SettingItem(
                        title = "Backup & Restore",
                        subtitle = "Manage your data",
                        icon = Icons.Default.Backup,
                        onClick = onNavigateToBackup
                    )
                }
                
                item {
                    SettingItem(
                        title = "Export Data",
                        subtitle = "Download your data",
                        icon = Icons.Default.Download,
                        onClick = { }
                    )
                }
                
                item {
                    SettingToggle(
                        title = "Privacy Mode",
                        subtitle = "Hide sensitive stats",
                        icon = Icons.Default.VisibilityOff,
                        checked = settings.privacyMode,
                        onCheckedChange = { onSettingChanged("privacyMode", it) },
                        theme = theme
                    )
                }
                
                // Account Section
                item {
                    SettingsSection("Account", Icons.Default.Person)
                }
                
                item {
                    SettingItem(
                        title = "Logout",
                        subtitle = "Sign out of your account",
                        icon = Icons.Default.Logout,
                        onClick = { showLogoutDialog = true },
                        isDestructive = false
                    )
                }
                
                item {
                    SettingItem(
                        title = "Delete Account",
                        subtitle = "Permanently delete your data",
                        icon = Icons.Default.DeleteForever,
                        onClick = { showDeleteDialog = true },
                        isDestructive = true
                    )
                }
                
                // About Section
                item {
                    SettingsSection("About", Icons.Default.Info)
                }
                
                item {
                    AboutCard(theme)
                }
                
                item { Spacer(Modifier.height(80.dp)) }
            }
        }
    }
    
    // Logout Dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Logout") },
            text = { Text("Are you sure you want to logout?") },
            confirmButton = {
                Button(onClick = { onLogout(); showLogoutDialog = false }) {
                    Text("Logout")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
    
    // Delete Account Dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Account") },
            text = { Text("This action cannot be undone. All your data will be permanently deleted.") },
            confirmButton = {
                Button(
                    onClick = { onDeleteAccount(); showDeleteDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun ProfileCard(theme: PremiumTheme) {
    HeroCard(
        Modifier.fillMaxWidth().height(120.dp),
        listOf(theme.gradientStart, theme.gradientEnd)
    ) {
        Row(Modifier.fillMaxSize(), Arrangement.spacedBy(16.dp), Alignment.CenterVertically) {
            Box(
                Modifier.size(64.dp).clip(CircleShape).background(Color.White.copy(0.2f)),
                Alignment.Center
            ) {
                Icon(Icons.Default.Person, null, Modifier.size(36.dp), Color.White)
            }
            Column {
                Text("John Doe", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text("john.doe@email.com", fontSize = 14.sp, color = Color.White.copy(0.8f))
            }
        }
    }
}

@Composable
private fun SettingsSection(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Row(
        Modifier.fillMaxWidth().padding(top = 8.dp),
        Arrangement.spacedBy(8.dp),
        Alignment.CenterVertically
    ) {
        Icon(icon, null, Modifier.size(20.dp), MaterialTheme.colorScheme.primary)
        Text(title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun SettingItem(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    isDestructive: Boolean = false
) {
    GlassCard(Modifier.fillMaxWidth().clickable(onClick = onClick)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(48.dp).clip(CircleShape).background(
                    if (isDestructive) Color(0xFFEF4444).copy(0.15f)
                    else MaterialTheme.colorScheme.primary.copy(0.15f)
                ),
                Alignment.Center
            ) {
                Icon(
                    icon, null, Modifier.size(24.dp),
                    if (isDestructive) Color(0xFFEF4444) else MaterialTheme.colorScheme.primary
                )
            }
            Column(Modifier.weight(1f)) {
                Text(title, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                Text(subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(0.6f))
            }
            Icon(Icons.Default.ChevronRight, null, Modifier.size(24.dp))
        }
    }
}

@Composable
private fun SettingToggle(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    theme: PremiumTheme
) {
    GlassCard(Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(48.dp).clip(CircleShape).background(theme.primaryColor.copy(0.15f)),
                Alignment.Center
            ) {
                Icon(icon, null, Modifier.size(24.dp), theme.primaryColor)
            }
            Column(Modifier.weight(1f)) {
                Text(title, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                Text(subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(0.6f))
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = theme.primaryColor
                )
            )
        }
    }
}

@Composable
private fun SettingSlider(
    title: String,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit,
    theme: PremiumTheme
) {
    GlassCard(Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(title, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                Text(value.toInt().toString(), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = theme.primaryColor)
            }
            Slider(
                value = value,
                onValueChange = onValueChange,
                valueRange = range,
                colors = SliderDefaults.colors(
                    thumbColor = theme.primaryColor,
                    activeTrackColor = theme.primaryColor
                )
            )
        }
    }
}

@Composable
private fun AboutCard(theme: PremiumTheme) {
    GlassCard(Modifier.fillMaxWidth()) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(Icons.Default.FitnessCenter, null, Modifier.size(48.dp), theme.primaryColor)
            Text("Fitx", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text("Version 1.0.0", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface.copy(0.7f))
            Text("Premium Fitness Companion", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(0.6f))
        }
    }
}

// Data Classes
data class AppSettings(
    val isDarkMode: Boolean = true,
    val hapticFeedback: Boolean = true,
    val waterReminders: Boolean = true,
    val activityReminders: Boolean = true,
    val stepsGoal: Float = 10000f,
    val calorieGoal: Int = 2000,
    val waterGoal: Int = 8,
    val useMetric: Boolean = true,
    val privacyMode: Boolean = false
)

