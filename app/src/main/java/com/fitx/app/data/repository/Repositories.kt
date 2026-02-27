package com.fitx.app.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.room.withTransaction
import com.fitx.app.BuildConfig
import com.fitx.app.data.local.AppDatabase
import com.fitx.app.data.local.dao.ActivityDao
import com.fitx.app.data.local.dao.HabitDao
import com.fitx.app.data.local.dao.MealDao
import com.fitx.app.data.local.dao.TaskDao
import com.fitx.app.data.local.dao.UserProfileDao
import com.fitx.app.data.local.dao.WeightDao
import com.fitx.app.data.local.dao.WorkoutDao
import com.fitx.app.data.local.entity.HabitCompletionEntity
import com.fitx.app.data.local.mapper.toDomain
import com.fitx.app.data.local.mapper.toEntity
import com.fitx.app.data.remote.UsdaApiService
import com.fitx.app.data.remote.dto.FoodDto
import com.fitx.app.data.remote.dto.FoodSearchRequest
import com.fitx.app.domain.model.ActivityPoint
import com.fitx.app.domain.model.ActivitySession
import com.fitx.app.domain.model.ActivitySessionDetail
import com.fitx.app.domain.model.AuthUser
import com.fitx.app.domain.model.CustomFood
import com.fitx.app.domain.model.FoodItem
import com.fitx.app.domain.model.Habit
import com.fitx.app.domain.model.HabitProgress
import com.fitx.app.domain.model.MealEntry
import com.fitx.app.domain.model.ServingPreset
import com.fitx.app.domain.model.SettingsPreferences
import com.fitx.app.domain.model.TaskItem
import com.fitx.app.domain.model.UserProfile
import com.fitx.app.domain.model.WeightEntry
import com.fitx.app.domain.model.WorkoutTemplate
import com.fitx.app.domain.model.ExerciseLog
import com.fitx.app.domain.repository.ActivityRepository
import com.fitx.app.domain.repository.AuthRepository
import com.fitx.app.domain.repository.HabitRepository
import com.fitx.app.domain.repository.NutritionRepository
import com.fitx.app.domain.repository.PlannerRepository
import com.fitx.app.domain.repository.SettingsRepository
import com.fitx.app.domain.repository.UserProfileRepository
import com.fitx.app.domain.repository.WeightRepository
import com.fitx.app.domain.repository.WorkoutRepository
import com.fitx.app.service.sync.CloudSyncScheduler
import com.fitx.app.service.sync.SyncEntityType
import com.fitx.app.util.DateUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

@Singleton
class UserProfileRepositoryImpl @Inject constructor(
    private val userProfileDao: UserProfileDao,
    private val syncQueueRepository: SyncQueueRepository,
    private val cloudSyncScheduler: CloudSyncScheduler
) : UserProfileRepository {
    override fun observeProfile(): Flow<UserProfile?> {
        return userProfileDao.observeProfile().map { it?.toDomain() }
    }

    override suspend fun upsertProfile(profile: UserProfile) {
        userProfileDao.upsert(profile.toEntity())
        syncQueueRepository.enqueueUpsert(
            entityType = SyncEntityType.PROFILE,
            entityId = profile.id.toString(),
            payload = mapOf(
                "id" to profile.id,
                "heightCm" to profile.heightCm,
                "weightKg" to profile.weightKg,
                "age" to profile.age,
                "gender" to profile.gender.name,
                "activityLevel" to profile.activityLevel.name,
                "goalType" to profile.goalType.name,
                "goalWeightKg" to profile.goalWeightKg
            )
        )
        cloudSyncScheduler.syncNow()
    }
}

@Singleton
class ActivityRepositoryImpl @Inject constructor(
    private val appDatabase: AppDatabase,
    private val activityDao: ActivityDao
) : ActivityRepository {
    override fun observeSessions(): Flow<List<ActivitySession>> {
        return activityDao.observeSessions().map { list -> list.map { it.toDomain() } }
    }

    override fun observeSessionDetail(sessionId: Long): Flow<ActivitySessionDetail?> {
        return activityDao.observeSessionWithPoints(sessionId).map { it?.toDomain() }
    }

    override suspend fun saveSession(session: ActivitySession, points: List<ActivityPoint>): Long {
        return appDatabase.withTransaction {
            val newSessionId = activityDao.insertSession(session.copy(sessionId = 0).toEntity())
            val pointEntities = points.map { point -> point.toEntity(newSessionId) }
            activityDao.insertPoints(pointEntities)
            newSessionId
        }
    }
}

@Singleton
class WeightRepositoryImpl @Inject constructor(
    private val weightDao: WeightDao,
    private val syncQueueRepository: SyncQueueRepository,
    private val cloudSyncScheduler: CloudSyncScheduler
) : WeightRepository {
    override fun observeEntries(): Flow<List<WeightEntry>> {
        return weightDao.observeEntries().map { list -> list.map { it.toDomain() } }
    }

    override fun observeWeeklyAverage(days: Int): Flow<Double?> {
        val safeDays = days.coerceAtLeast(1)
        val start = DateUtils.todayEpochDay() - (safeDays - 1)
        return weightDao.observeAverageSince(start)
    }

    override suspend fun upsertEntry(entry: WeightEntry) {
        val insertedId = weightDao.upsert(entry.toEntity())
        val resolvedId = if (entry.entryId != 0L) entry.entryId else insertedId
        syncQueueRepository.enqueueUpsert(
            entityType = SyncEntityType.WEIGHT,
            entityId = resolvedId.toString(),
            payload = mapOf(
                "entryId" to resolvedId,
                "dateEpochDay" to entry.dateEpochDay,
                "weightKg" to entry.weightKg
            )
        )
        cloudSyncScheduler.syncNow()
    }

    override suspend fun deleteEntry(entryId: Long) {
        weightDao.deleteById(entryId)
        syncQueueRepository.enqueueDelete(
            entityType = SyncEntityType.WEIGHT,
            entityId = entryId.toString()
        )
        cloudSyncScheduler.syncNow()
    }
}

@Singleton
class WorkoutRepositoryImpl @Inject constructor(
    private val workoutDao: WorkoutDao,
    private val syncQueueRepository: SyncQueueRepository,
    private val cloudSyncScheduler: CloudSyncScheduler
) : WorkoutRepository {
    override fun observeTemplates(): Flow<List<WorkoutTemplate>> {
        return workoutDao.observeTemplates().map { list -> list.map { it.toDomain() } }
    }

    override fun observeExerciseLogs(dateEpochDay: Long): Flow<List<ExerciseLog>> {
        return workoutDao.observeExerciseLogs(dateEpochDay).map { list -> list.map { it.toDomain() } }
    }

    override fun observeAllExerciseLogs(): Flow<List<ExerciseLog>> {
        return workoutDao.observeAllExerciseLogs().map { list -> list.map { it.toDomain() } }
    }

    override suspend fun addTemplate(template: WorkoutTemplate) {
        val templateId = workoutDao.insertTemplate(template.toEntity())
        syncQueueRepository.enqueueUpsert(
            entityType = SyncEntityType.WORKOUT_TEMPLATE,
            entityId = templateId.toString(),
            payload = mapOf(
                "templateId" to templateId,
                "name" to template.name,
                "description" to template.description
            )
        )
        cloudSyncScheduler.syncNow()
    }

    override suspend fun addExerciseLog(log: ExerciseLog) {
        val logId = workoutDao.insertExerciseLog(log.toEntity())
        syncQueueRepository.enqueueUpsert(
            entityType = SyncEntityType.EXERCISE_LOG,
            entityId = logId.toString(),
            payload = mapOf(
                "logId" to logId,
                "templateId" to log.templateId,
                "dateEpochDay" to log.dateEpochDay,
                "exerciseName" to log.exerciseName,
                "sets" to log.sets,
                "reps" to log.reps,
                "weightKg" to log.weightKg,
                "notes" to log.notes
            )
        )
        cloudSyncScheduler.syncNow()
    }
}

@Singleton
class HabitRepositoryImpl @Inject constructor(
    private val habitDao: HabitDao
) : HabitRepository {
    override fun observeHabitsWithProgress(dateEpochDay: Long): Flow<List<HabitProgress>> {
        return combine(
            habitDao.observeHabits(),
            habitDao.observeCompletionsByDate(dateEpochDay)
        ) { habits, completions ->
            habits.map { habitEntity ->
                val todayCount = completions.firstOrNull { it.habitId == habitEntity.habitId }?.count ?: 0
                val streak = calculateStreak(
                    completions = habitDao.getHabitCompletions(habitEntity.habitId),
                    targetPerDay = habitEntity.targetPerDay,
                    fromDate = dateEpochDay
                )
                HabitProgress(
                    habit = habitEntity.toDomain(),
                    todayCount = todayCount,
                    streakDays = streak
                )
            }
        }
    }

    override suspend fun addHabit(habit: Habit) {
        habitDao.insertHabit(habit.toEntity())
    }

    override suspend fun incrementHabit(habitId: Long, dateEpochDay: Long) {
        val current = habitDao.getCompletion(habitId, dateEpochDay)
        val updated = if (current == null) {
            HabitCompletionEntity(
                habitId = habitId,
                dateEpochDay = dateEpochDay,
                count = 1
            )
        } else {
            current.copy(count = current.count + 1)
        }
        habitDao.upsertCompletion(updated)
    }

    private fun calculateStreak(
        completions: List<HabitCompletionEntity>,
        targetPerDay: Int,
        fromDate: Long
    ): Int {
        val byDate = completions.associateBy { it.dateEpochDay }
        var streak = 0
        var cursor = fromDate
        while (true) {
            val completion = byDate[cursor] ?: break
            if (completion.count < targetPerDay) break
            streak += 1
            cursor -= 1
        }
        return streak
    }
}

@Singleton
class PlannerRepositoryImpl @Inject constructor(
    private val taskDao: TaskDao,
    private val syncQueueRepository: SyncQueueRepository,
    private val cloudSyncScheduler: CloudSyncScheduler
) : PlannerRepository {
    override fun observeTasks(dateEpochDay: Long): Flow<List<TaskItem>> {
        return taskDao.observeTasks(dateEpochDay).map { list -> list.map { it.toDomain() } }
    }

    override suspend fun addTask(taskItem: TaskItem): Long {
        val taskId = taskDao.insertTask(taskItem.toEntity())
        syncQueueRepository.enqueueUpsert(
            entityType = SyncEntityType.TASK,
            entityId = taskId.toString(),
            payload = mapOf(
                "taskId" to taskId,
                "title" to taskItem.title,
                "description" to taskItem.description,
                "dateEpochDay" to taskItem.dateEpochDay,
                "isCompleted" to taskItem.isCompleted,
                "repeatDaily" to taskItem.repeatDaily,
                "timeMinutesOfDay" to taskItem.timeMinutesOfDay,
                "priority" to taskItem.priority,
                "reminderEnabled" to taskItem.reminderEnabled
            )
        )
        cloudSyncScheduler.syncNow()
        return taskId
    }

    override suspend fun updateTask(taskItem: TaskItem) {
        val taskId = taskDao.insertTask(taskItem.toEntity())
        val resolvedId = if (taskItem.taskId == 0L) taskId else taskItem.taskId
        syncQueueRepository.enqueueUpsert(
            entityType = SyncEntityType.TASK,
            entityId = resolvedId.toString(),
            payload = mapOf(
                "taskId" to resolvedId,
                "title" to taskItem.title,
                "description" to taskItem.description,
                "dateEpochDay" to taskItem.dateEpochDay,
                "isCompleted" to taskItem.isCompleted,
                "repeatDaily" to taskItem.repeatDaily,
                "timeMinutesOfDay" to taskItem.timeMinutesOfDay,
                "priority" to taskItem.priority,
                "reminderEnabled" to taskItem.reminderEnabled
            )
        )
        cloudSyncScheduler.syncNow()
    }

    override suspend fun toggleTask(taskId: Long, completed: Boolean) {
        taskDao.updateCompleted(taskId, completed)
        taskDao.getTaskById(taskId)?.let { updated ->
            syncQueueRepository.enqueueUpsert(
                entityType = SyncEntityType.TASK,
                entityId = taskId.toString(),
                payload = mapOf(
                    "taskId" to updated.taskId,
                    "title" to updated.title,
                    "description" to updated.description,
                    "dateEpochDay" to updated.dateEpochDay,
                    "isCompleted" to updated.isCompleted,
                    "repeatDaily" to updated.repeatDaily,
                    "timeMinutesOfDay" to updated.timeMinutesOfDay,
                    "priority" to updated.priority,
                    "reminderEnabled" to updated.reminderEnabled
                )
            )
            cloudSyncScheduler.syncNow()
        }
    }

    override suspend fun deleteTask(taskId: Long) {
        taskDao.deleteTask(taskId)
        syncQueueRepository.enqueueDelete(
            entityType = SyncEntityType.TASK,
            entityId = taskId.toString()
        )
        cloudSyncScheduler.syncNow()
    }
}

@Singleton
class NutritionRepositoryImpl @Inject constructor(
    private val mealDao: MealDao,
    private val usdaApiService: UsdaApiService,
    private val syncQueueRepository: SyncQueueRepository,
    private val cloudSyncScheduler: CloudSyncScheduler,
    private val dataStore: DataStore<Preferences>,
    private val gson: Gson
) : NutritionRepository {
    override suspend fun searchFoods(query: String): List<FoodItem> {
        val normalizedQuery = query.trim()
        if (normalizedQuery.isBlank()) return browseFoods(pageNumber = 1, pageSize = 200).ifEmpty { OfflineFoodCatalog.items }

        if (BuildConfig.USDA_API_KEY.isBlank()) {
            return searchLocalFoods(normalizedQuery)
        }

        val remoteResults = runCatching {
            usdaApiService.searchFoods(
                apiKey = BuildConfig.USDA_API_KEY,
                request = FoodSearchRequest(query = normalizedQuery, pageSize = 200, pageNumber = 1)
            ).foods.map { it.toFoodItem() }
        }.getOrElse { emptyList() }

        if (remoteResults.isNotEmpty()) {
            return remoteResults
                .distinctBy { "${it.fdcId}_${it.name.lowercase()}" }
                .take(400)
        }
        return searchLocalFoods(normalizedQuery)
    }

    override suspend fun browseFoods(pageNumber: Int, pageSize: Int): List<FoodItem> {
        if (pageNumber < 1 || pageSize < 1 || BuildConfig.USDA_API_KEY.isBlank()) return emptyList()
        return runCatching {
            usdaApiService.listFoods(
                apiKey = BuildConfig.USDA_API_KEY,
                pageSize = pageSize.coerceAtMost(200),
                pageNumber = pageNumber
            ).map { it.toFoodItem() }.filter { it.name.isNotBlank() }
        }.getOrElse { emptyList() }
    }

    override fun getOfflineCatalogCount(): Int = OfflineFoodCatalog.totalItems

    override fun observeMeals(dateEpochDay: Long): Flow<List<MealEntry>> {
        return mealDao.observeMeals(dateEpochDay).map { list -> list.map { it.toDomain() } }
    }

    override suspend fun addMeal(mealEntry: MealEntry) {
        val mealEntryId = mealDao.insertMeal(mealEntry.toEntity())
        syncQueueRepository.enqueueUpsert(
            entityType = SyncEntityType.MEAL,
            entityId = mealEntryId.toString(),
            payload = mapOf(
                "mealEntryId" to mealEntryId,
                "dateEpochDay" to mealEntry.dateEpochDay,
                "mealType" to mealEntry.mealType,
                "foodName" to mealEntry.foodName,
                "grams" to mealEntry.grams,
                "calories" to mealEntry.calories,
                "protein" to mealEntry.protein,
                "carbs" to mealEntry.carbs,
                "fat" to mealEntry.fat,
                "source" to mealEntry.source
            )
        )
        cloudSyncScheduler.syncNow()
    }

    override suspend fun deleteMeal(mealEntryId: Long) {
        mealDao.deleteMeal(mealEntryId)
        syncQueueRepository.enqueueDelete(
            entityType = SyncEntityType.MEAL,
            entityId = mealEntryId.toString()
        )
        cloudSyncScheduler.syncNow()
    }

    override fun observeCustomFoods(): Flow<List<CustomFood>> {
        return dataStore.data.map { prefs ->
            parseCustomFoodsJson(prefs[CUSTOM_FOODS_KEY].orEmpty())
        }
    }

    override suspend fun saveCustomFood(customFood: CustomFood) {
        val foods = observeCustomFoods().first().toMutableList()
        val existing = foods.indexOfFirst { it.id == customFood.id || it.name.equals(customFood.name, ignoreCase = true) }
        if (existing >= 0) {
            foods[existing] = customFood
        } else {
            foods.add(customFood)
        }
        writeCustomFoods(foods)
    }

    override suspend fun saveServingPreset(foodId: String, preset: ServingPreset) {
        val foods = observeCustomFoods().first().toMutableList()
        val index = foods.indexOfFirst { it.id == foodId }
        if (index < 0) return
        val current = foods[index]
        val nextServings = (current.servings + preset)
            .distinctBy { "${it.label.lowercase()}_${it.grams}" }
            .sortedBy { it.grams }
        foods[index] = current.copy(servings = nextServings)
        writeCustomFoods(foods)
    }

    private fun FoodDto.toFoodItem(): FoodItem {
        val calories = nutrientValue("energy")
        val protein = nutrientValue("protein")
        val carbs = nutrientValue("carbohydrate")
        val fat = nutrientValue("lipid", "fat")
        val base = servingBaseGrams()
        return FoodItem(
            fdcId = fdcId,
            name = description,
            calories = calories,
            protein = protein,
            carbs = carbs,
            fat = fat,
            baseGrams = base
        )
    }

    private fun FoodDto.servingBaseGrams(): Double {
        val unit = servingSizeUnit?.trim()?.lowercase().orEmpty()
        val size = servingSize ?: 0.0
        return if (size > 0 && (unit == "g" || unit == "gm" || unit.contains("gram"))) {
            size
        } else {
            100.0
        }
    }

    private fun FoodDto.nutrientValue(vararg keywords: String): Double {
        val match = foodNutrients.firstOrNull { nutrient ->
            keywords.any { key -> nutrient.nutrientName.contains(key, ignoreCase = true) }
        }
        return match?.value ?: 0.0
    }

    private fun searchLocalFoods(query: String): List<FoodItem> {
        val normalized = query.lowercase().trim()
        val tokens = normalized.split(Regex("\\s+")).filter { it.isNotBlank() }
        return OfflineFoodCatalog.items
            .asSequence()
            .map { item ->
                val name = item.name.lowercase()
                val score = when {
                    name == normalized -> 100
                    name.startsWith(normalized) -> 80
                    tokens.isNotEmpty() && tokens.all { token -> name.contains(token) } -> 70
                    name.contains(normalized) -> 50
                    tokens.any { token -> name.contains(token) } -> 25
                    else -> 0
                }
                item to score
            }
            .filter { (_, score) -> score > 0 }
            .sortedByDescending { (_, score) -> score }
            .map { (item, _) -> item }
            .take(250)
            .toList()
    }

    private fun parseCustomFoodsJson(json: String): List<CustomFood> {
        if (json.isBlank()) return emptyList()
        return runCatching {
            val listType = object : TypeToken<List<CustomFood>>() {}.type
            gson.fromJson<List<CustomFood>>(json, listType) ?: emptyList()
        }.getOrElse { emptyList() }
    }

    private suspend fun writeCustomFoods(foods: List<CustomFood>) {
        val json = gson.toJson(foods)
        dataStore.edit { prefs ->
            prefs[CUSTOM_FOODS_KEY] = json
        }
    }
}

private val DARK_THEME_KEY = booleanPreferencesKey("dark_theme_enabled")
private val NOTIFICATIONS_KEY = booleanPreferencesKey("notifications_enabled")
private val HAPTICS_KEY = booleanPreferencesKey("haptics_enabled")
private val CUSTOM_FOODS_KEY = stringPreferencesKey("custom_foods_json")

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : SettingsRepository {
    override fun observeSettings(): Flow<SettingsPreferences> {
        return dataStore.data.map { prefs ->
            SettingsPreferences(
                darkTheme = prefs[DARK_THEME_KEY] ?: true,
                notificationsEnabled = prefs[NOTIFICATIONS_KEY] ?: true,
                hapticsEnabled = prefs[HAPTICS_KEY] ?: true
            )
        }
    }

    override suspend fun setDarkTheme(enabled: Boolean) {
        dataStore.edit { prefs -> prefs[DARK_THEME_KEY] = enabled }
    }

    override suspend fun setNotifications(enabled: Boolean) {
        dataStore.edit { prefs -> prefs[NOTIFICATIONS_KEY] = enabled }
    }

    override suspend fun setHaptics(enabled: Boolean) {
        dataStore.edit { prefs -> prefs[HAPTICS_KEY] = enabled }
    }
}

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth?
) : AuthRepository {

    override fun observeCurrentUser(): Flow<AuthUser?> {
        val auth = firebaseAuth ?: return flowOf(null)
        return callbackFlow {
            val listener = FirebaseAuth.AuthStateListener { firebase ->
                trySend(firebase.currentUser?.toDomain())
            }
            auth.addAuthStateListener(listener)
            trySend(auth.currentUser?.toDomain())
            awaitClose {
                auth.removeAuthStateListener(listener)
            }
        }
    }

    override suspend fun signInWithGoogleIdToken(idToken: String): Result<AuthUser> {
        val auth = firebaseAuth ?: return Result.failure(
            IllegalStateException("Firebase is not configured on this build.")
        )
        return runCatching {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val authResult = auth.signInWithCredential(credential).await()
            authResult.user?.toDomain() ?: error("Unable to complete Google sign-in.")
        }
    }

    override suspend fun signOut() {
        firebaseAuth?.signOut()
    }
}

private fun FirebaseUser.toDomain(): AuthUser = AuthUser(
    uid = uid,
    displayName = displayName,
    email = email,
    photoUrl = photoUrl?.toString()
)
