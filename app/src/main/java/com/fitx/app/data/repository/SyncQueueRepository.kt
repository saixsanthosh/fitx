package com.fitx.app.data.repository

import com.fitx.app.data.local.dao.SyncQueueDao
import com.fitx.app.data.local.entity.SyncQueueEntity
import com.fitx.app.service.sync.SyncOperationType
import com.google.gson.Gson
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow

@Singleton
class SyncQueueRepository @Inject constructor(
    private val syncQueueDao: SyncQueueDao,
    private val gson: Gson
) {
    suspend fun enqueueUpsert(entityType: String, entityId: String, payload: Any) {
        syncQueueDao.insert(
            SyncQueueEntity(
                entityType = entityType,
                entityId = entityId,
                operationType = SyncOperationType.UPSERT,
                payloadJson = gson.toJson(payload),
                createdAtMillis = System.currentTimeMillis()
            )
        )
    }

    suspend fun enqueueDelete(entityType: String, entityId: String) {
        syncQueueDao.insert(
            SyncQueueEntity(
                entityType = entityType,
                entityId = entityId,
                operationType = SyncOperationType.DELETE,
                payloadJson = null,
                createdAtMillis = System.currentTimeMillis()
            )
        )
    }

    suspend fun getPending(limit: Int = 50): List<SyncQueueEntity> {
        return syncQueueDao.getByStatus(SyncQueueEntity.STATUS_PENDING, limit)
    }

    suspend fun markDone(queueId: Long) {
        syncQueueDao.updateStatus(queueId, SyncQueueEntity.STATUS_DONE)
    }

    suspend fun markFailed(queueId: Long, error: String) {
        syncQueueDao.markFailed(queueId, error.take(500))
    }

    fun observePendingCount(): Flow<Int> {
        return syncQueueDao.observeCountByStatus(SyncQueueEntity.STATUS_PENDING)
    }

    suspend fun cleanupDone(keepForDays: Long = 7) {
        val cutoff = System.currentTimeMillis() - (keepForDays * 24 * 60 * 60 * 1000)
        syncQueueDao.deleteByStatusOlderThan(SyncQueueEntity.STATUS_DONE, cutoff)
    }
}
