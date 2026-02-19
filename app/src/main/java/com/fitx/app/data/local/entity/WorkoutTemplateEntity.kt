package com.fitx.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workout_template")
data class WorkoutTemplateEntity(
    @PrimaryKey(autoGenerate = true) val templateId: Long = 0,
    val name: String,
    val description: String
)

