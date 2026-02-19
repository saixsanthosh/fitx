package com.fitx.app.domain.model

data class HealthMetrics(
    val bmi: Double,
    val bmr: Double,
    val tdee: Double,
    val dailyCalorieTarget: Int,
    val projectedWeeksToGoal: Int
)

