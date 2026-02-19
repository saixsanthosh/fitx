package com.fitx.app.service

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReminderScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun syncReminders(enabled: Boolean) {
        if (!enabled) {
            WorkManager.getInstance(context).cancelUniqueWork(WEIGHT_WORK)
            WorkManager.getInstance(context).cancelUniqueWork(WATER_WORK)
            return
        }
        scheduleWeightReminder()
        scheduleWaterReminder()
    }

    private fun scheduleWeightReminder() {
        val request = PeriodicWorkRequestBuilder<WeightReminderWorker>(1, TimeUnit.DAYS)
            .setInitialDelay(initialDelayUntil(8, 0), TimeUnit.MILLISECONDS)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                    .build()
            )
            .build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WEIGHT_WORK,
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }

    private fun scheduleWaterReminder() {
        val request = PeriodicWorkRequestBuilder<WaterReminderWorker>(4, TimeUnit.HOURS)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                    .build()
            )
            .build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WATER_WORK,
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }

    private fun initialDelayUntil(hour: Int, minute: Int): Long {
        val now = LocalDateTime.now()
        val targetTime = LocalTime.of(hour, minute)
        val next = if (now.toLocalTime().isBefore(targetTime)) {
            now.withHour(hour).withMinute(minute).withSecond(0).withNano(0)
        } else {
            now.plusDays(1).withHour(hour).withMinute(minute).withSecond(0).withNano(0)
        }
        return Duration.between(now, next).toMillis()
    }

    companion object {
        private const val WEIGHT_WORK = "weight_reminder_work"
        private const val WATER_WORK = "water_reminder_work"
    }
}

