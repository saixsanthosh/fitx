package com.fitx.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fitx.app.data.local.entity.MealEntryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MealDao {
    @Query("SELECT * FROM meal_entry WHERE dateEpochDay = :dateEpochDay ORDER BY mealEntryId DESC")
    fun observeMeals(dateEpochDay: Long): Flow<List<MealEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeal(entity: MealEntryEntity): Long

    @Query("DELETE FROM meal_entry WHERE mealEntryId = :mealEntryId")
    suspend fun deleteMeal(mealEntryId: Long)
}
