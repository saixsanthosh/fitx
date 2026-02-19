package com.fitx.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "activity_point",
    foreignKeys = [
        ForeignKey(
            entity = ActivitySessionEntity::class,
            parentColumns = ["sessionId"],
            childColumns = ["sessionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["sessionId"])]
)
data class ActivityPointEntity(
    @PrimaryKey(autoGenerate = true) val pointId: Long = 0,
    val sessionId: Long,
    val latitude: Double,
    val longitude: Double,
    val timestampMillis: Long
)

