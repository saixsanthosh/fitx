package com.fitx.app.domain.repository

import com.fitx.app.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface UserProfileRepository {
    fun observeProfile(): Flow<UserProfile?>
    suspend fun upsertProfile(profile: UserProfile)
}

