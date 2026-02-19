package com.fitx.app.domain.model

enum class Gender {
    MALE,
    FEMALE,
    OTHER
}

enum class ActivityLevel(val multiplier: Double) {
    SEDENTARY(1.2),
    LIGHT(1.375),
    MODERATE(1.55),
    VERY_ACTIVE(1.725),
    ATHLETE(1.9)
}

enum class GoalType {
    LOSE,
    MAINTAIN,
    GAIN
}

data class UserProfile(
    val id: Int = 1,
    val heightCm: Double,
    val weightKg: Double,
    val age: Int,
    val gender: Gender,
    val activityLevel: ActivityLevel,
    val goalType: GoalType,
    val goalWeightKg: Double
)

