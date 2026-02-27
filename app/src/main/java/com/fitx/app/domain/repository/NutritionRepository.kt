package com.fitx.app.domain.repository

import com.fitx.app.domain.model.CustomFood
import com.fitx.app.domain.model.FoodItem
import com.fitx.app.domain.model.MealEntry
import com.fitx.app.domain.model.ServingPreset
import kotlinx.coroutines.flow.Flow

interface NutritionRepository {
    suspend fun searchFoods(query: String): List<FoodItem>
    suspend fun lookupFoodByBarcode(barcode: String): FoodItem?
    suspend fun browseFoods(pageNumber: Int, pageSize: Int = 200): List<FoodItem>
    fun getOfflineCatalogCount(): Int
    fun observeMeals(dateEpochDay: Long): Flow<List<MealEntry>>
    suspend fun addMeal(mealEntry: MealEntry)
    suspend fun deleteMeal(mealEntryId: Long)
    suspend fun copyMeals(fromEpochDay: Long, toEpochDay: Long): Int
    fun observeCustomFoods(): Flow<List<CustomFood>>
    suspend fun saveCustomFood(customFood: CustomFood)
    suspend fun saveServingPreset(foodId: String, preset: ServingPreset)
    fun observeFavoriteFoods(): Flow<List<FoodItem>>
    fun observeRecentFoods(): Flow<List<FoodItem>>
    suspend fun toggleFavoriteFood(foodItem: FoodItem)
    suspend fun recordRecentFood(foodItem: FoodItem)
}
