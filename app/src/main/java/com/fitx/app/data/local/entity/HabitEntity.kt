package com.fitx.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habit")
data class HabitEntity(
    @PrimaryKey(autoGenerate = true) val habitId: Long = 0,
    val name: String,
    val targetPerDay: Int,
    val enabled: Boolean
)

