package com.fitx.app.domain.repository

import com.fitx.app.domain.model.WeightEntry
import kotlinx.coroutines.flow.Flow

interface WeightRepository {
    fun observeEntries(): Flow<List<WeightEntry>>
    fun observeWeeklyAverage(days: Int = 7): Flow<Double?>
    suspend fun upsertEntry(entry: WeightEntry)
    suspend fun deleteEntry(entryId: Long)
}

