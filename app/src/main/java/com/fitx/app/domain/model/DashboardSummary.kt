package com.fitx.app.domain.model

data class DashboardSummary(
    val healthMetrics: HealthMetrics?,
    val latestWeightKg: Double?,
    val weeklyWeights: List<WeightEntry>,
    val todayTasks: List<TaskItem>,
    val completedTasks: Int,
    val todayDistanceMeters: Double,
    val todayCaloriesBurned: Int,
    val todaySteps: Int
)

