package com.fitx.app.service

import com.fitx.app.domain.model.ActivityPoint
import com.fitx.app.domain.model.ActivityType

data class TrackingState(
    val isTracking: Boolean = false,
    val activityType: ActivityType = ActivityType.WALKING,
    val startTimeMillis: Long = 0L,
    val durationSeconds: Long = 0L,
    val distanceMeters: Double = 0.0,
    val averageSpeedMps: Double = 0.0,
    val caloriesBurned: Int = 0,
    val steps: Int = 0,
    val isAutoPaused: Boolean = false,
    val autoPauseCount: Int = 0,
    val pathPoints: List<ActivityPoint> = emptyList()
)

