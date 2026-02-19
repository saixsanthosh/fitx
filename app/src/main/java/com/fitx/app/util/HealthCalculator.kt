package com.fitx.app.util

import com.fitx.app.domain.model.ActivityLevel
import com.fitx.app.domain.model.Gender
import com.fitx.app.domain.model.GoalType
import com.fitx.app.domain.model.HealthMetrics
import com.fitx.app.domain.model.UserProfile
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.roundToInt

object HealthCalculator {

    fun calculateBmi(weightKg: Double, heightCm: Double): Double {
        if (heightCm <= 0.0) return 0.0
        val heightM = heightCm / 100.0
        return weightKg / heightM.pow(2)
    }

    fun calculateBmr(profile: UserProfile): Double {
        val base = (10 * profile.weightKg) + (6.25 * profile.heightCm) - (5 * profile.age)
        return when (profile.gender) {
            Gender.MALE -> base + 5
            Gender.FEMALE -> base - 161
            Gender.OTHER -> base - 78
        }
    }

    fun calculateTdee(bmr: Double, activityLevel: ActivityLevel): Double {
        return bmr * activityLevel.multiplier
    }

    fun calculateDailyCalories(tdee: Double, goalType: GoalType): Int {
        val adjustment = when (goalType) {
            GoalType.LOSE -> -500
            GoalType.MAINTAIN -> 0
            GoalType.GAIN -> 300
        }
        return (tdee + adjustment).roundToInt().coerceAtLeast(1200)
    }

    fun projectWeeksToGoal(currentWeight: Double, targetWeight: Double): Int {
        val diff = abs(targetWeight - currentWeight)
        if (diff < 0.1) return 0
        val weeklyChangeKg = 0.5
        return (diff / weeklyChangeKg).roundToInt().coerceAtLeast(1)
    }

    fun calculateAll(profile: UserProfile): HealthMetrics {
        val bmi = calculateBmi(profile.weightKg, profile.heightCm)
        val bmr = calculateBmr(profile)
        val tdee = calculateTdee(bmr, profile.activityLevel)
        return HealthMetrics(
            bmi = bmi,
            bmr = bmr,
            tdee = tdee,
            dailyCalorieTarget = calculateDailyCalories(tdee, profile.goalType),
            projectedWeeksToGoal = projectWeeksToGoal(profile.weightKg, profile.goalWeightKg)
        )
    }
}

