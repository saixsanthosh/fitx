package com.fitx.app.service.sync

import com.fitx.app.data.remote.CloudSyncRemoteDataSource
import com.fitx.app.data.repository.SyncQueueRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CloudSyncProcessor @Inject constructor(
    private val syncQueueRepository: SyncQueueRepository,
    private val cloudSyncRemoteDataSource: CloudSyncRemoteDataSource
) {
    suspend fun syncPendingBatch(limit: Int = 50): SyncResult {
        val queue = syncQueueRepository.getPending(limit)
        if (queue.isEmpty()) {
            syncQueueRepository.cleanupDone()
            return SyncResult.Success
        }

        queue.forEach { item ->
            val result = cloudSyncRemoteDataSource.push(item)
            if (result.isSuccess) {
                syncQueueRepository.markDone(item.queueId)
                return@forEach
            }

            val error = result.exceptionOrNull()?.message ?: "Unknown sync error"
            syncQueueRepository.markFailed(item.queueId, error)

            if (error.contains("No authenticated user", ignoreCase = true)) {
                return SyncResult.Deferred
            }
            return SyncResult.Retry
        }

        syncQueueRepository.cleanupDone()
        return SyncResult.Success
    }
}

enum class SyncResult {
    Success,
    Retry,
    Deferred
}
