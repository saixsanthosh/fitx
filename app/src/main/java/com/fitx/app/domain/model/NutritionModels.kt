package com.fitx.app.domain.model

data class FoodItem(
    val fdcId: Long,
    val name: String,
    val calories: Double,
    val protein: Double,
    val carbs: Double,
    val fat: Double,
    val baseGrams: Double = 100.0
)

data class MealEntry(
    val mealEntryId: Long = 0,
    val dateEpochDay: Long,
    val mealType: String,
    val foodName: String,
    val grams: Double,
    val calories: Double,
    val protein: Double,
    val carbs: Double,
    val fat: Double,
    val source: String
)

data class ServingPreset(
    val label: String,
    val grams: Double
)

data class CustomFood(
    val id: String,
    val name: String,
    val caloriesPer100g: Double,
    val proteinPer100g: Double,
    val carbsPer100g: Double,
    val fatPer100g: Double,
    val servings: List<ServingPreset> = emptyList()
)
