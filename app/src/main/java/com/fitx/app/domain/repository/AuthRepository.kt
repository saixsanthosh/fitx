package com.fitx.app.domain.repository

import com.fitx.app.domain.model.AuthUser
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun observeCurrentUser(): Flow<AuthUser?>
    suspend fun signInWithGoogleIdToken(idToken: String): Result<AuthUser>
    suspend fun signOut()
}

