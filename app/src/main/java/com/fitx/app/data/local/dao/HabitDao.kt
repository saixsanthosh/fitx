package com.fitx.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fitx.app.data.local.entity.HabitCompletionEntity
import com.fitx.app.data.local.entity.HabitEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {
    @Query("SELECT * FROM habit WHERE enabled = 1 ORDER BY habitId DESC")
    fun observeHabits(): Flow<List<HabitEntity>>

    @Query("SELECT * FROM habit ORDER BY habitId DESC")
    suspend fun getAllHabits(): List<HabitEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(entity: HabitEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabits(entities: List<HabitEntity>)

    @Query("SELECT * FROM habit_completion WHERE dateEpochDay = :dateEpochDay")
    fun observeCompletionsByDate(dateEpochDay: Long): Flow<List<HabitCompletionEntity>>

    @Query("SELECT * FROM habit_completion ORDER BY completionId ASC")
    suspend fun getAllCompletions(): List<HabitCompletionEntity>

    @Query("SELECT * FROM habit_completion WHERE habitId = :habitId ORDER BY dateEpochDay DESC")
    suspend fun getHabitCompletions(habitId: Long): List<HabitCompletionEntity>

    @Query("SELECT * FROM habit_completion WHERE habitId = :habitId AND dateEpochDay = :dateEpochDay LIMIT 1")
    suspend fun getCompletion(habitId: Long, dateEpochDay: Long): HabitCompletionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCompletion(entity: HabitCompletionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompletions(entities: List<HabitCompletionEntity>)

    @Query("DELETE FROM habit_completion")
    suspend fun clearAllCompletions()

    @Query("DELETE FROM habit")
    suspend fun clearAllHabits()
}

