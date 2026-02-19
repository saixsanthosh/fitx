package com.fitx.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "exercise_log",
    foreignKeys = [
        ForeignKey(
            entity = WorkoutTemplateEntity::class,
            parentColumns = ["templateId"],
            childColumns = ["templateId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index(value = ["templateId"]), Index(value = ["dateEpochDay"])]
)
data class ExerciseLogEntity(
    @PrimaryKey(autoGenerate = true) val logId: Long = 0,
    val templateId: Long?,
    val dateEpochDay: Long,
    val exerciseName: String,
    val sets: Int,
    val reps: Int,
    val weightKg: Double,
    val notes: String
)

