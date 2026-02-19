package com.fitx.app.service.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class CloudSyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val cloudSyncProcessor: CloudSyncProcessor
) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        return when (cloudSyncProcessor.syncPendingBatch()) {
            SyncResult.Success -> Result.success()
            SyncResult.Deferred -> Result.success()
            SyncResult.Retry -> Result.retry()
        }
    }
}
