package com.fitx.app.domain.repository

import com.fitx.app.domain.model.AppUpdateInfo

interface AppUpdateRepository {
    suspend fun getAvailableUpdate(currentVersion: String): AppUpdateInfo?
    suspend fun markPrompted(version: String)
}
