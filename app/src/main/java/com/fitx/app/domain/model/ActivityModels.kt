package com.fitx.app.domain.model

enum class ActivityType {
    WALKING,
    CYCLING
}

data class ActivitySession(
    val sessionId: Long = 0,
    val activityType: ActivityType,
    val startTimeMillis: Long,
    val endTimeMillis: Long,
    val durationSeconds: Long,
    val distanceMeters: Double,
    val averageSpeedMps: Double,
    val caloriesBurned: Int,
    val steps: Int
)

data class ActivityPoint(
    val pointId: Long = 0,
    val sessionId: Long = 0,
    val latitude: Double,
    val longitude: Double,
    val timestampMillis: Long
)

data class ActivitySessionDetail(
    val session: ActivitySession,
    val points: List<ActivityPoint>
)

