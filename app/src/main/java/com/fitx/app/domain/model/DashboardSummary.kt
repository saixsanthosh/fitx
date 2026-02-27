package com.fitx.app.domain.model

data class DashboardSummary(
    val healthMetrics: HealthMetrics?,
    val latestWeightKg: Double?,
    val weeklyWeights: List<WeightEntry>,
    val todayTasks: List<TaskItem>,
    val completedTasks: Int,
    val todayDistanceMeters: Double,
    val todayCaloriesBurned: Int,
    val todaySteps: Int,
    val todayMealCalories: Double,
    val weeklyDistanceMeters: Double,
    val weeklyCaloriesBurned: Int,
    val weeklySteps: Int,
    val weeklySessionCount: Int,
    val weeklyActiveDays: Int,
    val weeklyWeightChangeKg: Double?,
    val todayScore: Int,
    val todayScoreBreakdown: TodayScoreBreakdown
)

data class TodayScoreBreakdown(
    val activity: Int,
    val nutrition: Int,
    val tasks: Int,
    val consistency: Int
)

