package com.fitx.app.domain.model

data class SettingsPreferences(
    val useSystemTheme: Boolean = false,
    val darkTheme: Boolean = true,
    val notificationsEnabled: Boolean = true,
    val hapticsEnabled: Boolean = true,
    val smartRemindersEnabled: Boolean = true,
    val smartReminderHour: Int = 8,
    val hydrationIntervalHours: Int = 4,
    val guestModeEnabled: Boolean = false
)

