package com.fitx.app.service

import android.content.Context
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.fitx.app.domain.model.TaskItem
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskReminderScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun schedule(task: TaskItem) {
        if (task.taskId == 0L) return
        if (!task.reminderEnabled || task.timeMinutesOfDay == null || task.isCompleted) {
            cancel(task.taskId)
            return
        }

        val data = Data.Builder()
            .putLong(TaskReminderWorker.KEY_TASK_ID, task.taskId)
            .putString(TaskReminderWorker.KEY_TITLE, task.title)
            .putString(TaskReminderWorker.KEY_BODY, task.description.ifBlank { "It's time for your task." })
            .build()

        if (task.repeatDaily) {
            val request = PeriodicWorkRequestBuilder<TaskReminderWorker>(1, TimeUnit.DAYS)
                .setInputData(data)
                .setInitialDelay(initialDelayToNextOccurrence(task.timeMinutesOfDay), TimeUnit.MILLISECONDS)
                .build()
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                periodicWorkName(task.taskId),
                ExistingPeriodicWorkPolicy.UPDATE,
                request
            )
            WorkManager.getInstance(context).cancelUniqueWork(oneTimeWorkName(task.taskId))
            return
        }

        val triggerMillis = taskDateTimeMillis(task.dateEpochDay, task.timeMinutesOfDay)
        val delay = triggerMillis - System.currentTimeMillis()
        if (delay <= 0L) {
            cancel(task.taskId)
            return
        }

        val request = OneTimeWorkRequestBuilder<TaskReminderWorker>()
            .setInputData(data)
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            oneTimeWorkName(task.taskId),
            ExistingWorkPolicy.REPLACE,
            request
        )
        WorkManager.getInstance(context).cancelUniqueWork(periodicWorkName(task.taskId))
    }

    fun cancel(taskId: Long) {
        WorkManager.getInstance(context).cancelUniqueWork(oneTimeWorkName(taskId))
        WorkManager.getInstance(context).cancelUniqueWork(periodicWorkName(taskId))
    }

    private fun oneTimeWorkName(taskId: Long): String = "task_reminder_once_$taskId"
    private fun periodicWorkName(taskId: Long): String = "task_reminder_daily_$taskId"

    private fun taskDateTimeMillis(dateEpochDay: Long, minutesOfDay: Int): Long {
        val date = LocalDate.ofEpochDay(dateEpochDay)
        val hour = (minutesOfDay / 60).coerceIn(0, 23)
        val minute = (minutesOfDay % 60).coerceIn(0, 59)
        val dateTime = LocalDateTime.of(date, LocalTime.of(hour, minute))
        return dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }

    private fun initialDelayToNextOccurrence(minutesOfDay: Int): Long {
        val now = LocalDateTime.now()
        val hour = (minutesOfDay / 60).coerceIn(0, 23)
        val minute = (minutesOfDay % 60).coerceIn(0, 59)
        val targetTime = LocalTime.of(hour, minute)
        val next = if (now.toLocalTime().isBefore(targetTime)) {
            now.withHour(hour).withMinute(minute).withSecond(0).withNano(0)
        } else {
            now.plusDays(1).withHour(hour).withMinute(minute).withSecond(0).withNano(0)
        }
        return Duration.between(now, next).toMillis()
    }
}
