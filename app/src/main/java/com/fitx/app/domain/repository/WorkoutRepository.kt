package com.fitx.app.domain.repository

import com.fitx.app.domain.model.ExerciseLog
import com.fitx.app.domain.model.WorkoutTemplate
import kotlinx.coroutines.flow.Flow

interface WorkoutRepository {
    fun observeTemplates(): Flow<List<WorkoutTemplate>>
    fun observeExerciseLogs(dateEpochDay: Long): Flow<List<ExerciseLog>>
    fun observeAllExerciseLogs(): Flow<List<ExerciseLog>>
    suspend fun addTemplate(template: WorkoutTemplate)
    suspend fun addExerciseLog(log: ExerciseLog)
}

