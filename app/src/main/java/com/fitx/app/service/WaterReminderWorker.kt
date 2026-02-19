package com.fitx.app.service

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.fitx.app.util.NotificationHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class WaterReminderWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val notificationHelper: NotificationHelper
) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        notificationHelper.showReminder(
            notificationId = 2002,
            title = "Hydration reminder",
            body = "Drink water and stay on track."
        )
        return Result.success()
    }
}

