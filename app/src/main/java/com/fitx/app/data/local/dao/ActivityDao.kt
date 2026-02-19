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
    suspend fun insertPoints(points: List<ActivityPointEntity>)

    @Query("SELECT * FROM activity_session ORDER BY startTimeMillis DESC")
    fun observeSessions(): Flow<List<ActivitySessionEntity>>

    @Transaction
    @Query("SELECT * FROM activity_session WHERE sessionId = :sessionId")
    fun observeSessionWithPoints(sessionId: Long): Flow<ActivitySessionWithPoints?>
}

