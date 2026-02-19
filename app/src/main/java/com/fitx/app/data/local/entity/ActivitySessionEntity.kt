package com.fitx.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "activity_session")
data class ActivitySessionEntity(
    @PrimaryKey(autoGenerate = true) val sessionId: Long = 0,
    val activityType: String,
    val startTimeMillis: Long,
    val endTimeMillis: Long,
    val durationSeconds: Long,
    val distanceMeters: Double,
    val averageSpeedMps: Double,
    val caloriesBurned: Int,
    val steps: Int
)

