package com.fitx.app.domain.model

data class Habit(
    val habitId: Long = 0,
    val name: String,
    val targetPerDay: Int,
    val enabled: Boolean
)

data class HabitCompletion(
    val completionId: Long = 0,
    val habitId: Long,
    val dateEpochDay: Long,
    val count: Int
)

data class HabitProgress(
    val habit: Habit,
    val todayCount: Int,
    val streakDays: Int
)

