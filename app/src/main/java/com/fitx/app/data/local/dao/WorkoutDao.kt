package com.fitx.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fitx.app.data.local.entity.ExerciseLogEntity
import com.fitx.app.data.local.entity.WorkoutTemplateEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {
    @Query("SELECT * FROM workout_template ORDER BY templateId DESC")
    fun observeTemplates(): Flow<List<WorkoutTemplateEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTemplate(entity: WorkoutTemplateEntity): Long

    @Query("SELECT * FROM exercise_log WHERE dateEpochDay = :dateEpochDay ORDER BY logId DESC")
    fun observeExerciseLogs(dateEpochDay: Long): Flow<List<ExerciseLogEntity>>

    @Query("SELECT * FROM exercise_log ORDER BY logId DESC")
    fun observeAllExerciseLogs(): Flow<List<ExerciseLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExerciseLog(entity: ExerciseLogEntity): Long
}
