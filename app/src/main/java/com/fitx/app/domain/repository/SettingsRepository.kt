package com.fitx.app.domain.repository

import com.fitx.app.domain.model.SettingsPreferences
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun observeSettings(): Flow<SettingsPreferences>
    suspend fun setDarkTheme(enabled: Boolean)
    suspend fun setNotifications(enabled: Boolean)
    suspend fun setHaptics(enabled: Boolean)
    suspend fun setSmartReminders(enabled: Boolean)
    suspend fun setSmartReminderTuning(reminderHour: Int, hydrationIntervalHours: Int)
}

