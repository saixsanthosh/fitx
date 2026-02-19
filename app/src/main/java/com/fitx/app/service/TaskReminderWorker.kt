package com.fitx.app.service

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.fitx.app.util.NotificationHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class TaskReminderWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val notificationHelper: NotificationHelper
) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        val taskId = inputData.getLong(KEY_TASK_ID, 0L)
        val title = inputData.getString(KEY_TITLE).orEmpty().ifBlank { "Task reminder" }
        val body = inputData.getString(KEY_BODY).orEmpty().ifBlank { "You have a pending task in Fitx." }
        val notificationId = (3000 + (taskId % 100000L)).toInt()
        notificationHelper.showReminder(
            notificationId = notificationId,
            title = title,
            body = body
        )
        return Result.success()
    }

    companion object {
        const val KEY_TASK_ID = "task_id"
        const val KEY_TITLE = "task_title"
        const val KEY_BODY = "task_body"
    }
}
