package com.fitx.app.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "sync_queue",
    indices = [Index(value = ["status", "createdAtMillis"])]
)
data class SyncQueueEntity(
    @PrimaryKey(autoGenerate = true) val queueId: Long = 0,
    val entityType: String,
    val entityId: String,
    val operationType: String,
    val payloadJson: String?,
    val createdAtMillis: Long,
    val status: String = STATUS_PENDING,
    val retryCount: Int = 0,
    val lastError: String? = null
) {
    companion object {
        const val STATUS_PENDING = "PENDING"
        const val STATUS_DONE = "DONE"
    }
}
