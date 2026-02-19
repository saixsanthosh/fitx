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
