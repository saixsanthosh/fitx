package com.fitx.app.service

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Environment
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.room.withTransaction
import com.fitx.app.BuildConfig
import com.fitx.app.data.local.AppDatabase
import com.fitx.app.data.local.entity.ActivityPointEntity
import com.fitx.app.data.local.entity.ActivitySessionEntity
import com.fitx.app.data.local.entity.ExerciseLogEntity
import com.fitx.app.data.local.entity.HabitCompletionEntity
import com.fitx.app.data.local.entity.HabitEntity
import com.fitx.app.data.local.entity.MealEntryEntity
import com.fitx.app.data.local.entity.TaskEntity
import com.fitx.app.data.local.entity.UserProfileEntity
import com.fitx.app.data.local.entity.WeightEntryEntity
import com.fitx.app.data.local.entity.WorkoutTemplateEntity
import com.fitx.app.data.local.entity.SyncQueueEntity
import com.fitx.app.domain.model.HealthCheckItem
import com.fitx.app.domain.model.HealthCheckStatus
import com.fitx.app.util.BatteryOptimizationHelper
import com.fitx.app.util.PermissionUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.first

@Singleton
class AppMaintenanceManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val appDatabase: AppDatabase,
    private val dataStore: DataStore<Preferences>,
    private val gson: Gson,
    private val firebaseAuth: FirebaseAuth?,
    private val firebaseFirestore: FirebaseFirestore?
) {
    suspend fun exportBackup(): File {
        val snapshot = appDatabase.withTransaction {
            val prefs = dataStore.data.first()
            FitxBackupPayload(
                schemaVersion = 1,
                exportedAtMillis = System.currentTimeMillis(),
                userProfile = appDatabase.userProfileDao().getProfile(),
                sessions = appDatabase.activityDao().getAllSessions(),
                points = appDatabase.activityDao().getAllPoints(),
                weights = appDatabase.weightDao().getAllEntries(),
                workoutTemplates = appDatabase.workoutDao().getAllTemplates(),
                exerciseLogs = appDatabase.workoutDao().getAllExerciseLogs(),
                habits = appDatabase.habitDao().getAllHabits(),
                habitCompletions = appDatabase.habitDao().getAllCompletions(),
                tasks = appDatabase.taskDao().getAllTasks(),
                meals = appDatabase.mealDao().getAllMeals(),
                customFoodsJson = prefs[CUSTOM_FOODS_KEY].orEmpty(),
                favoriteFoodsJson = prefs[FAVORITE_FOODS_KEY].orEmpty(),
                recentFoodsJson = prefs[RECENT_FOODS_KEY].orEmpty()
            )
        }
        val file = File(
            backupDirectory().apply { mkdirs() },
            "fitx-backup-${System.currentTimeMillis()}.json"
        )
        file.writeText(gson.toJson(snapshot))
        return file
    }

    suspend fun restoreLatestBackup(): String {
        val latest = backupDirectory()
            .takeIf { it.exists() }
            ?.listFiles()
            ?.filter { it.extension.equals("json", ignoreCase = true) }
            ?.maxByOrNull { it.lastModified() }
            ?: error("No backup file found. Export once first.")

        val payload = gson.fromJson(latest.readText(), FitxBackupPayload::class.java)
            ?: error("Backup format invalid.")

        appDatabase.withTransaction {
            appDatabase.activityDao().clearAllPoints()
            appDatabase.activityDao().clearAllSessions()
            appDatabase.workoutDao().clearAllExerciseLogs()
            appDatabase.workoutDao().clearAllTemplates()
            appDatabase.habitDao().clearAllCompletions()
            appDatabase.habitDao().clearAllHabits()
            appDatabase.taskDao().clearAll()
            appDatabase.mealDao().clearAll()
            appDatabase.weightDao().clearAll()
            appDatabase.userProfileDao().clearAll()

            payload.userProfile?.let { appDatabase.userProfileDao().upsert(it) }
            if (payload.sessions.isNotEmpty()) appDatabase.activityDao().insertSessions(payload.sessions)
            if (payload.points.isNotEmpty()) appDatabase.activityDao().insertPoints(payload.points)
            if (payload.weights.isNotEmpty()) appDatabase.weightDao().insertAll(payload.weights)
            if (payload.workoutTemplates.isNotEmpty()) appDatabase.workoutDao().insertTemplates(payload.workoutTemplates)
            if (payload.exerciseLogs.isNotEmpty()) appDatabase.workoutDao().insertExerciseLogs(payload.exerciseLogs)
            if (payload.habits.isNotEmpty()) appDatabase.habitDao().insertHabits(payload.habits)
            if (payload.habitCompletions.isNotEmpty()) appDatabase.habitDao().insertCompletions(payload.habitCompletions)
            if (payload.tasks.isNotEmpty()) appDatabase.taskDao().insertTasks(payload.tasks)
            if (payload.meals.isNotEmpty()) appDatabase.mealDao().insertMeals(payload.meals)
        }

        dataStore.edit { prefs ->
            prefs[CUSTOM_FOODS_KEY] = payload.customFoodsJson
            prefs[FAVORITE_FOODS_KEY] = payload.favoriteFoodsJson
            prefs[RECENT_FOODS_KEY] = payload.recentFoodsJson
        }

        return "Restored backup ${latest.name}"
    }

    suspend fun exportDiagnostics(): File {
        val healthChecks = buildHealthChecks()
        val pending = appDatabase.syncQueueDao().getCountByStatus(SyncQueueEntity.STATUS_PENDING)
        val done = appDatabase.syncQueueDao().getCountByStatus(SyncQueueEntity.STATUS_DONE)
        val settings = dataStore.data.first()
        val diagnosticsText = buildString {
            appendLine("Fitx Diagnostics")
            appendLine("Generated: ${Instant.ofEpochMilli(System.currentTimeMillis())}")
            appendLine("App: ${BuildConfig.APPLICATION_ID} ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})")
            appendLine("Android SDK: ${Build.VERSION.SDK_INT}")
            appendLine("Pending sync queue: $pending")
            appendLine("Completed sync queue: $done")
            appendLine("Notifications enabled: ${settings[NOTIFICATIONS_KEY] ?: true}")
            appendLine("Smart reminders: ${settings[SMART_REMINDERS_KEY] ?: true}")
            appendLine("Reminder hour: ${settings[SMART_REMINDER_HOUR_KEY] ?: 8}")
            appendLine("Hydration interval: ${settings[HYDRATION_INTERVAL_HOURS_KEY] ?: 4}")
            appendLine("Health checks:")
            healthChecks.forEach { item ->
                appendLine("- ${item.title}: ${item.status} (${item.detail})")
            }
        }

        val diagnosticsDir = File(context.cacheDir, "share_cards").apply { mkdirs() }
        val file = File(diagnosticsDir, "fitx-diagnostics-${System.currentTimeMillis()}.txt")
        file.writeText(diagnosticsText)
        return file
    }

    fun buildHealthChecks(): List<HealthCheckItem> {
        val isUsdaConfigured = BuildConfig.USDA_API_KEY.isNotBlank()
        val hasLocation = PermissionUtils.hasLocationPermissions(context)
        val hasNotifications = PermissionUtils.hasNotificationPermission(context)
        val isBatteryOptimizedOff = BatteryOptimizationHelper.isIgnoringOptimizations(context)
        val hasInternet = hasInternetConnection()
        val mapLibraryAvailable = runCatching { Class.forName("org.osmdroid.views.MapView") }.isSuccess

        val firebaseReady = firebaseAuth != null && firebaseFirestore != null
        val crashlyticsLinked = runCatching { Class.forName("com.google.firebase.crashlytics.FirebaseCrashlytics") }.isSuccess

        return listOf(
            HealthCheckItem(
                title = "Firebase",
                status = if (firebaseReady) HealthCheckStatus.OK else HealthCheckStatus.WARN,
                detail = if (firebaseReady) "Auth + Firestore ready" else "Missing google-services setup"
            ),
            HealthCheckItem(
                title = "USDA API Key",
                status = if (isUsdaConfigured) HealthCheckStatus.OK else HealthCheckStatus.WARN,
                detail = if (isUsdaConfigured) "Configured" else "Set USDA_API_KEY for online food search"
            ),
            HealthCheckItem(
                title = "Location Permission",
                status = if (hasLocation) HealthCheckStatus.OK else HealthCheckStatus.ERROR,
                detail = if (hasLocation) "Granted" else "Required for live tracking"
            ),
            HealthCheckItem(
                title = "Notifications Permission",
                status = if (hasNotifications) HealthCheckStatus.OK else HealthCheckStatus.WARN,
                detail = if (hasNotifications) "Granted" else "Reminders may be blocked"
            ),
            HealthCheckItem(
                title = "Battery Optimization",
                status = if (isBatteryOptimizedOff) HealthCheckStatus.OK else HealthCheckStatus.WARN,
                detail = if (isBatteryOptimizedOff) "Background tracking stable" else "Allow ignore optimization for better tracking"
            ),
            HealthCheckItem(
                title = "Map Engine",
                status = if (mapLibraryAvailable) HealthCheckStatus.OK else HealthCheckStatus.ERROR,
                detail = if (mapLibraryAvailable) {
                    if (hasInternet) "OSMDroid available and network online" else "OSMDroid available (offline mode)"
                } else {
                    "Map library missing"
                }
            ),
            HealthCheckItem(
                title = "Crash Reporting",
                status = if (crashlyticsLinked && firebaseReady) HealthCheckStatus.OK else HealthCheckStatus.WARN,
                detail = if (crashlyticsLinked && firebaseReady) "Crashlytics linked" else "Enable Firebase Crashlytics"
            )
        )
    }

    private fun backupDirectory(): File {
        val base = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) ?: context.filesDir
        return File(base, "fitx_backups")
    }

    private fun hasInternetConnection(): Boolean {
        val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = manager.activeNetwork ?: return false
        val caps = manager.getNetworkCapabilities(network) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}

data class FitxBackupPayload(
    val schemaVersion: Int,
    val exportedAtMillis: Long,
    val userProfile: UserProfileEntity?,
    val sessions: List<ActivitySessionEntity>,
    val points: List<ActivityPointEntity>,
    val weights: List<WeightEntryEntity>,
    val workoutTemplates: List<WorkoutTemplateEntity>,
    val exerciseLogs: List<ExerciseLogEntity>,
    val habits: List<HabitEntity>,
    val habitCompletions: List<HabitCompletionEntity>,
    val tasks: List<TaskEntity>,
    val meals: List<MealEntryEntity>,
    val customFoodsJson: String,
    val favoriteFoodsJson: String,
    val recentFoodsJson: String
)

private val NOTIFICATIONS_KEY = booleanPreferencesKey("notifications_enabled")
private val SMART_REMINDERS_KEY = booleanPreferencesKey("smart_reminders_enabled")
private val SMART_REMINDER_HOUR_KEY = intPreferencesKey("smart_reminder_hour")
private val HYDRATION_INTERVAL_HOURS_KEY = intPreferencesKey("hydration_interval_hours")
private val CUSTOM_FOODS_KEY = stringPreferencesKey("custom_foods_json")
private val FAVORITE_FOODS_KEY = stringPreferencesKey("favorite_foods_json")
private val RECENT_FOODS_KEY = stringPreferencesKey("recent_foods_json")
