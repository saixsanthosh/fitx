package com.fitx.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fitx.app.data.local.entity.SyncQueueEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SyncQueueDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: SyncQueueEntity): Long

    @Query(
        "SELECT * FROM sync_queue " +
            "WHERE status = :status " +
            "ORDER BY createdAtMillis ASC " +
            "LIMIT :limit"
    )
    suspend fun getByStatus(status: String, limit: Int): List<SyncQueueEntity>

    @Query("UPDATE sync_queue SET status = :status, lastError = NULL WHERE queueId = :queueId")
    suspend fun updateStatus(queueId: Long, status: String)

    @Query(
        "UPDATE sync_queue " +
            "SET retryCount = retryCount + 1, lastError = :error " +
            "WHERE queueId = :queueId"
    )
    suspend fun markFailed(queueId: Long, error: String)

    @Query("SELECT COUNT(*) FROM sync_queue WHERE status = :status")
    fun observeCountByStatus(status: String): Flow<Int>

    @Query("DELETE FROM sync_queue WHERE status = :status AND createdAtMillis < :olderThanMillis")
    suspend fun deleteByStatusOlderThan(status: String, olderThanMillis: Long): Int
}
