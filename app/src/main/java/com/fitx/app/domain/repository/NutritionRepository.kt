package com.fitx.app.domain.repository

import com.fitx.app.domain.model.FoodItem
import com.fitx.app.domain.model.MealEntry
import kotlinx.coroutines.flow.Flow

interface NutritionRepository {
    suspend fun searchFoods(query: String): List<FoodItem>
    suspend fun browseFoods(pageNumber: Int, pageSize: Int = 200): List<FoodItem>
    fun getOfflineCatalogCount(): Int
    fun observeMeals(dateEpochDay: Long): Flow<List<MealEntry>>
    suspend fun addMeal(mealEntry: MealEntry)
    suspend fun deleteMeal(mealEntryId: Long)
}
