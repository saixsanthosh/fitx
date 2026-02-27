package com.fitx.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.fitx.app.data.local.entity.ActivityPointEntity
import com.fitx.app.data.local.entity.ActivitySessionEntity
import com.fitx.app.data.local.entity.ActivitySessionWithPoints
import kotlinx.coroutines.flow.Flow

@Dao
interface ActivityDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(entity: ActivitySessionEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSessions(entities: List<ActivitySessionEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPoints(points: List<ActivityPointEntity>)

    @Query("SELECT * FROM activity_session ORDER BY startTimeMillis DESC")
    fun observeSessions(): Flow<List<ActivitySessionEntity>>

    @Query("SELECT * FROM activity_session ORDER BY startTimeMillis DESC")
    suspend fun getAllSessions(): List<ActivitySessionEntity>

    @Query("SELECT * FROM activity_point ORDER BY pointId ASC")
    suspend fun getAllPoints(): List<ActivityPointEntity>

    @Transaction
    @Query("SELECT * FROM activity_session WHERE sessionId = :sessionId")
    fun observeSessionWithPoints(sessionId: Long): Flow<ActivitySessionWithPoints?>

    @Query("DELETE FROM activity_point")
    suspend fun clearAllPoints()

    @Query("DELETE FROM activity_session")
    suspend fun clearAllSessions()
}

