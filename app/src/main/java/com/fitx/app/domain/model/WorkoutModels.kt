package com.fitx.app.domain.model

data class WorkoutTemplate(
    val templateId: Long = 0,
    val name: String,
    val description: String
)

data class ExerciseLog(
    val logId: Long = 0,
    val templateId: Long?,
    val dateEpochDay: Long,
    val exerciseName: String,
    val sets: Int,
    val reps: Int,
    val weightKg: Double,
    val notes: String
)

