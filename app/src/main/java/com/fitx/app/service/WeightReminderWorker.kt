package com.fitx.app.service

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.fitx.app.util.NotificationHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class WeightReminderWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val notificationHelper: NotificationHelper
) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        notificationHelper.showReminder(
            notificationId = 2001,
            title = "Weight check-in",
            body = "Log your weight for today in Fitx."
        )
        return Result.success()
    }
}

