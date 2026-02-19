package com.fitx.app.domain.repository

import com.fitx.app.domain.model.ActivityPoint
import com.fitx.app.domain.model.ActivitySession
import com.fitx.app.domain.model.ActivitySessionDetail
import kotlinx.coroutines.flow.Flow

interface ActivityRepository {
    fun observeSessions(): Flow<List<ActivitySession>>
    fun observeSessionDetail(sessionId: Long): Flow<ActivitySessionDetail?>
    suspend fun saveSession(session: ActivitySession, points: List<ActivityPoint>): Long
}

