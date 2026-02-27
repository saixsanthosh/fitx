package com.fitx.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fitx.app.data.local.entity.WeightEntryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WeightDao {
    @Query("SELECT * FROM weight_entry ORDER BY dateEpochDay DESC")
    fun observeEntries(): Flow<List<WeightEntryEntity>>

    @Query("SELECT * FROM weight_entry ORDER BY dateEpochDay DESC")
    suspend fun getAllEntries(): List<WeightEntryEntity>

    @Query("SELECT AVG(weightKg) FROM weight_entry WHERE dateEpochDay >= :startEpochDay")
    fun observeAverageSince(startEpochDay: Long): Flow<Double?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: WeightEntryEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<WeightEntryEntity>)

    @Delete
    suspend fun delete(entity: WeightEntryEntity)

    @Query("DELETE FROM weight_entry WHERE entryId = :entryId")
    suspend fun deleteById(entryId: Long)

    @Query("DELETE FROM weight_entry")
    suspend fun clearAll()
}
