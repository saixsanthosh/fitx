package com.fitx.app.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "weight_entry",
    indices = [Index(value = ["dateEpochDay"], unique = true)]
)
data class WeightEntryEntity(
    @PrimaryKey(autoGenerate = true) val entryId: Long = 0,
    val dateEpochDay: Long,
    val weightKg: Double
)

