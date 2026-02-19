package com.fitx.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: Int = 1,
    val heightCm: Double,
    val weightKg: Double,
    val age: Int,
    val gender: String,
    val activityLevel: String,
    val goalType: String,
    val goalWeightKg: Double
)
