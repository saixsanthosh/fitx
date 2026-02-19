package com.fitx.app.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "meal_entry",
    indices = [Index(value = ["dateEpochDay"])]
)
data class MealEntryEntity(
    @PrimaryKey(autoGenerate = true) val mealEntryId: Long = 0,
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
