package com.fitx.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "habit_completion",
    foreignKeys = [
        ForeignKey(
            entity = HabitEntity::class,
            parentColumns = ["habitId"],
            childColumns = ["habitId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["habitId"]), Index(value = ["dateEpochDay"]), Index(value = ["habitId", "dateEpochDay"], unique = true)]
)
data class HabitCompletionEntity(
    @PrimaryKey(autoGenerate = true) val completionId: Long = 0,
    val habitId: Long,
    val dateEpochDay: Long,
    val count: Int
)

