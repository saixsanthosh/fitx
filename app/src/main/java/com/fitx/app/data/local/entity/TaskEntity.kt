package com.fitx.app.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "task_item",
    indices = [Index(value = ["dateEpochDay"])]
)
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val taskId: Long = 0,
    val title: String,
    val description: String,
    val dateEpochDay: Long,
    val isCompleted: Boolean,
    val repeatDaily: Boolean,
    val timeMinutesOfDay: Int?,
    val priority: Int,
    val reminderEnabled: Boolean
)
