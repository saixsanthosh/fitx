package com.fitx.app.service.sync

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CloudSyncScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun schedulePeriodicSync() {
        val request = PeriodicWorkRequestBuilder<CloudSyncWorker>(6, TimeUnit.HOURS)
            .setConstraints(networkConstraint())
            .build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            PERIODIC_SYNC_WORK,
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }

    fun syncNow() {
        val request = OneTimeWorkRequestBuilder<CloudSyncWorker>()
            .setConstraints(networkConstraint())
            .build()
        WorkManager.getInstance(context).enqueueUniqueWork(
            ONE_TIME_SYNC_WORK,
            ExistingWorkPolicy.KEEP,
            request
        )
    }

    private fun networkConstraint(): Constraints {
        return Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
    }

    companion object {
        private const val PERIODIC_SYNC_WORK = "cloud_periodic_sync_work"
        private const val ONE_TIME_SYNC_WORK = "cloud_one_time_sync_work"
    }
}
