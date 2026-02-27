package com.fitx.app.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.fitx.app.BuildConfig
import com.fitx.app.data.remote.AppUpdateApiService
import com.fitx.app.domain.model.AppUpdateInfo
import com.fitx.app.domain.repository.AppUpdateRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.first

private val LAST_PROMPTED_UPDATE_VERSION_KEY = stringPreferencesKey("last_prompted_update_version")

@Singleton
class AppUpdateRepositoryImpl @Inject constructor(
    private val appUpdateApiService: AppUpdateApiService,
    private val dataStore: DataStore<Preferences>
) : AppUpdateRepository {
    override suspend fun getAvailableUpdate(currentVersion: String): AppUpdateInfo? {
        val info = runCatching {
            appUpdateApiService.fetchVersionInfo(BuildConfig.UPDATE_INFO_URL)
        }.getOrNull() ?: return null

        val latest = info.latestVersion.trim()
        if (latest.isBlank()) return null
        if (compareVersions(latest, currentVersion) <= 0) return null

        val lastPrompted = dataStore.data.first()[LAST_PROMPTED_UPDATE_VERSION_KEY].orEmpty()
        if (lastPrompted == latest) return null

        val message = info.message?.trim().takeUnless { it.isNullOrBlank() }
            ?: "A newer Fitx build is available."
        val downloadUrl = info.downloadUrl?.trim().takeUnless { it.isNullOrBlank() }
            ?: BuildConfig.UPDATE_FALLBACK_URL

        return AppUpdateInfo(
            version = latest,
            message = message,
            downloadUrl = downloadUrl
        )
    }

    override suspend fun markPrompted(version: String) {
        val normalized = version.trim()
        if (normalized.isBlank()) return
        dataStore.edit { prefs ->
            prefs[LAST_PROMPTED_UPDATE_VERSION_KEY] = normalized
        }
    }

    private fun compareVersions(left: String, right: String): Int {
        val leftParts = parseVersionParts(left)
        val rightParts = parseVersionParts(right)
        val max = maxOf(leftParts.size, rightParts.size)
        for (i in 0 until max) {
            val l = leftParts.getOrElse(i) { 0 }
            val r = rightParts.getOrElse(i) { 0 }
            if (l != r) return l.compareTo(r)
        }
        return 0
    }

    private fun parseVersionParts(version: String): List<Int> {
        return version
            .trim()
            .split(Regex("[^0-9]+"))
            .filter { it.isNotBlank() }
            .map { it.toIntOrNull() ?: 0 }
    }
}
