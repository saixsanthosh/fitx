package com.fitx.app.ui.viewmodel

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fitx.app.domain.model.ActivitySessionDetail
import com.fitx.app.domain.model.ActivityType
import com.fitx.app.domain.model.CustomFood
import com.fitx.app.domain.model.ExerciseLog
import com.fitx.app.domain.model.FoodItem
import com.fitx.app.domain.model.GoalType
import com.fitx.app.domain.model.Gender
import com.fitx.app.domain.model.Habit
import com.fitx.app.domain.model.HealthMetrics
import com.fitx.app.domain.model.MealEntry
import com.fitx.app.domain.model.SettingsPreferences
import com.fitx.app.domain.model.TaskItem
import com.fitx.app.domain.model.UserProfile
import com.fitx.app.domain.model.WeightEntry
import com.fitx.app.domain.model.WorkoutTemplate
import com.fitx.app.domain.model.ServingPreset
import com.fitx.app.domain.repository.ActivityRepository
import com.fitx.app.domain.repository.HabitRepository
import com.fitx.app.domain.repository.NutritionRepository
import com.fitx.app.domain.repository.PlannerRepository
import com.fitx.app.domain.repository.SettingsRepository
import com.fitx.app.domain.repository.UserProfileRepository
import com.fitx.app.domain.repository.WeightRepository
import com.fitx.app.domain.repository.WorkoutRepository
import com.fitx.app.domain.usecase.CalculateHealthMetricsUseCase
import com.fitx.app.domain.usecase.ObserveDashboardUseCase
import com.fitx.app.data.repository.SyncQueueRepository
import com.fitx.app.service.ActivityTrackingService
import com.fitx.app.service.AppMaintenanceManager
import com.fitx.app.service.ReminderScheduler
import com.fitx.app.service.TaskReminderScheduler
import com.fitx.app.service.TrackingStore
import com.fitx.app.service.sync.CloudSyncScheduler
import com.fitx.app.ui.widget.TodoWidgetProvider
import com.fitx.app.util.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.math.round

@HiltViewModel
class MainViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val plannerRepository: PlannerRepository,
    private val reminderScheduler: ReminderScheduler
) : ViewModel() {
    val settings: StateFlow<SettingsPreferences> = settingsRepository.observeSettings().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SettingsPreferences()
    )

    init {
        val today = DateUtils.todayEpochDay()
        viewModelScope.launch {
            kotlinx.coroutines.flow.combine(
                settingsRepository.observeSettings(),
                plannerRepository.observeTasks(today)
            ) { prefs, tasks ->
                val pendingTasks = tasks.filterNot { it.isCompleted }
                val timedTaskMinutes = pendingTasks
                    .filter { it.reminderEnabled }
                    .mapNotNull { it.timeMinutesOfDay }
                val suggestedReminderHour = when {
                    timedTaskMinutes.isNotEmpty() -> ((timedTaskMinutes.minOrNull() ?: 480) / 60 - 1).coerceIn(6, 22)
                    pendingTasks.count { it.priority == TaskItem.PRIORITY_HIGH } >= 2 -> 7
                    pendingTasks.isNotEmpty() -> 8
                    else -> 9
                }
                val suggestedHydrationInterval = when {
                    pendingTasks.size >= 8 -> 3
                    pendingTasks.size >= 4 -> 4
                    else -> 5
                }
                Triple(prefs, suggestedReminderHour, suggestedHydrationInterval)
            }.collect { (prefs, suggestedReminderHour, suggestedHydrationInterval) ->
                if (
                    prefs.smartRemindersEnabled &&
                    (prefs.smartReminderHour != suggestedReminderHour ||
                        prefs.hydrationIntervalHours != suggestedHydrationInterval)
                ) {
                    settingsRepository.setSmartReminderTuning(
                        reminderHour = suggestedReminderHour,
                        hydrationIntervalHours = suggestedHydrationInterval
                    )
                }
                val hour = if (prefs.smartRemindersEnabled) suggestedReminderHour else prefs.smartReminderHour
                val interval = if (prefs.smartRemindersEnabled) suggestedHydrationInterval else prefs.hydrationIntervalHours
                reminderScheduler.syncReminders(
                    enabled = prefs.notificationsEnabled,
                    weightReminderHour = hour,
                    hydrationIntervalHours = interval
                )
            }
        }
    }
}

@HiltViewModel
class DashboardViewModel @Inject constructor(
    observeDashboardUseCase: ObserveDashboardUseCase
) : ViewModel() {
    val summary = observeDashboardUseCase().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = com.fitx.app.domain.model.DashboardSummary(
            healthMetrics = null,
            latestWeightKg = null,
            weeklyWeights = emptyList(),
            todayTasks = emptyList(),
            completedTasks = 0,
            todayDistanceMeters = 0.0,
            todayCaloriesBurned = 0,
            todaySteps = 0,
            todayMealCalories = 0.0,
            weeklyDistanceMeters = 0.0,
            weeklyCaloriesBurned = 0,
            weeklySteps = 0,
            weeklySessionCount = 0,
            weeklyActiveDays = 0,
            weeklyWeightChangeKg = null,
            todayScore = 0,
            todayScoreBreakdown = com.fitx.app.domain.model.TodayScoreBreakdown(
                activity = 0,
                nutrition = 0,
                tasks = 0,
                consistency = 0
            )
        )
    )
}

data class ProfileFormState(
    val heightCm: String = "",
    val weightKg: String = "",
    val age: String = "",
    val gender: Gender = Gender.MALE,
    val activityLevel: com.fitx.app.domain.model.ActivityLevel = com.fitx.app.domain.model.ActivityLevel.MODERATE,
    val goalType: GoalType = GoalType.MAINTAIN,
    val goalWeightKg: String = ""
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userProfileRepository: UserProfileRepository,
    private val calculateHealthMetricsUseCase: CalculateHealthMetricsUseCase
) : ViewModel() {
    private val _form = MutableStateFlow(ProfileFormState())
    val form: StateFlow<ProfileFormState> = _form

    private val _metrics = MutableStateFlow<HealthMetrics?>(null)
    val metrics: StateFlow<HealthMetrics?> = _metrics

    init {
        viewModelScope.launch {
            userProfileRepository.observeProfile().collect { profile ->
                if (profile != null) {
                    _form.value = ProfileFormState(
                        heightCm = profile.heightCm.toString(),
                        weightKg = profile.weightKg.toString(),
                        age = profile.age.toString(),
                        gender = profile.gender,
                        activityLevel = profile.activityLevel,
                        goalType = profile.goalType,
                        goalWeightKg = profile.goalWeightKg.toString()
                    )
                    _metrics.value = calculateHealthMetricsUseCase(profile)
                }
            }
        }
    }

    fun updateForm(update: (ProfileFormState) -> ProfileFormState) {
        _form.update(update)
    }

    fun saveProfile() {
        val form = _form.value
        val profile = UserProfile(
            heightCm = form.heightCm.toDoubleOrNull() ?: 170.0,
            weightKg = form.weightKg.toDoubleOrNull() ?: 70.0,
            age = form.age.toIntOrNull() ?: 25,
            gender = form.gender,
            activityLevel = form.activityLevel,
            goalType = form.goalType,
            goalWeightKg = form.goalWeightKg.toDoubleOrNull() ?: (form.weightKg.toDoubleOrNull() ?: 70.0)
        )
        viewModelScope.launch {
            userProfileRepository.upsertProfile(profile)
            _metrics.value = calculateHealthMetricsUseCase(profile)
        }
    }
}

@HiltViewModel
class ActivityViewModel @Inject constructor(
    private val activityRepository: ActivityRepository,
    userProfileRepository: UserProfileRepository,
    settingsRepository: SettingsRepository,
    @ApplicationContext private val appContext: Context
) : ViewModel() {

    val trackingState = TrackingStore.state

    val sessions = activityRepository.observeSessions().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    val settings = settingsRepository.observeSettings().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SettingsPreferences()
    )

    private val selectedSessionId = MutableStateFlow<Long?>(null)
    val selectedSessionDetail: StateFlow<ActivitySessionDetail?> = selectedSessionId
        .flatMapLatest { id ->
            if (id == null) {
                kotlinx.coroutines.flow.flowOf(null)
            } else {
                activityRepository.observeSessionDetail(id)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    private val _profileWeight = MutableStateFlow(70.0)
    val profileWeight: StateFlow<Double> = _profileWeight

    init {
        viewModelScope.launch {
            userProfileRepository.observeProfile().collect { profile ->
                if (profile != null) {
                    _profileWeight.value = profile.weightKg
                }
            }
        }
    }

    fun startTracking(activityType: ActivityType) {
        val intent = Intent(appContext, ActivityTrackingService::class.java).apply {
            action = ActivityTrackingService.ACTION_START
            putExtra(ActivityTrackingService.EXTRA_ACTIVITY_TYPE, activityType.name)
            putExtra(ActivityTrackingService.EXTRA_WEIGHT_KG, _profileWeight.value)
        }
        ContextCompat.startForegroundService(appContext, intent)
    }

    fun stopTracking() {
        val intent = Intent(appContext, ActivityTrackingService::class.java).apply {
            action = ActivityTrackingService.ACTION_STOP
        }
        appContext.startService(intent)
    }

    fun loadSession(sessionId: Long) {
        selectedSessionId.value = sessionId
    }
}

@HiltViewModel
class WeightViewModel @Inject constructor(
    private val weightRepository: WeightRepository
) : ViewModel() {
    val entries = weightRepository.observeEntries().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    val weeklyAverage = weightRepository.observeWeeklyAverage().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null
    )

    private val _weightInput = MutableStateFlow("")
    val weightInput: StateFlow<String> = _weightInput

    private val _editingEntryId = MutableStateFlow<Long?>(null)
    val editingEntryId: StateFlow<Long?> = _editingEntryId

    fun updateWeightInput(value: String) {
        _weightInput.value = value
    }

    fun loadForEdit(entry: WeightEntry) {
        _editingEntryId.value = entry.entryId
        _weightInput.value = entry.weightKg.toString()
    }

    fun saveToday() {
        val weight = _weightInput.value.toDoubleOrNull() ?: return
        val entry = WeightEntry(
            entryId = _editingEntryId.value ?: 0,
            dateEpochDay = DateUtils.todayEpochDay(),
            weightKg = weight
        )
        viewModelScope.launch {
            weightRepository.upsertEntry(entry)
            _editingEntryId.value = null
            _weightInput.value = ""
        }
    }

    fun deleteEntry(entryId: Long) {
        viewModelScope.launch {
            weightRepository.deleteEntry(entryId)
        }
    }
}

@HiltViewModel
class WorkoutViewModel @Inject constructor(
    private val workoutRepository: WorkoutRepository
) : ViewModel() {
    data class PersonalRecord(
        val exerciseName: String,
        val maxWeightKg: Double,
        val maxVolume: Double
    )

    val templates = workoutRepository.observeTemplates().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    private val selectedDate = MutableStateFlow(DateUtils.todayEpochDay())
    val logs = selectedDate.flatMapLatest { date ->
        workoutRepository.observeExerciseLogs(date)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    val personalRecords = workoutRepository.observeAllExerciseLogs()
        .map { logs ->
            logs
                .groupBy { it.exerciseName.trim().lowercase() }
                .map { (_, exerciseLogs) ->
                    val displayName = exerciseLogs.first().exerciseName.trim()
                    val maxWeight = exerciseLogs.maxOf { it.weightKg }
                    val maxVolume = exerciseLogs.maxOf { it.weightKg * it.sets * it.reps }
                    PersonalRecord(
                        exerciseName = displayName,
                        maxWeightKg = maxWeight,
                        maxVolume = maxVolume
                    )
                }
                .sortedByDescending { it.maxVolume }
                .take(8)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun addTemplate(name: String, description: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            workoutRepository.addTemplate(
                WorkoutTemplate(name = name.trim(), description = description.trim())
            )
        }
    }

    fun addLog(
        name: String,
        sets: Int,
        reps: Int,
        weightKg: Double,
        notes: String,
        templateId: Long? = null
    ) {
        if (name.isBlank()) return
        viewModelScope.launch {
            workoutRepository.addExerciseLog(
                ExerciseLog(
                    templateId = templateId,
                    dateEpochDay = selectedDate.value,
                    exerciseName = name.trim(),
                    sets = sets,
                    reps = reps,
                    weightKg = weightKg,
                    notes = notes.trim()
                )
            )
        }
    }
}

@HiltViewModel
class HabitViewModel @Inject constructor(
    private val habitRepository: HabitRepository
) : ViewModel() {
    private val today = DateUtils.todayEpochDay()

    val habits = habitRepository.observeHabitsWithProgress(today).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    fun addHabit(name: String, targetPerDay: Int) {
        if (name.isBlank()) return
        viewModelScope.launch {
            habitRepository.addHabit(
                Habit(
                    name = name.trim(),
                    targetPerDay = targetPerDay.coerceAtLeast(1),
                    enabled = true
                )
            )
        }
    }

    fun incrementHabit(habitId: Long) {
        viewModelScope.launch {
            habitRepository.incrementHabit(habitId, today)
        }
    }
}

@HiltViewModel
class PlannerViewModel @Inject constructor(
    private val plannerRepository: PlannerRepository,
    private val taskReminderScheduler: TaskReminderScheduler,
    settingsRepository: SettingsRepository,
    @ApplicationContext private val appContext: Context
) : ViewModel() {
    private val _selectedDate = MutableStateFlow(DateUtils.todayEpochDay())
    val selectedDate: StateFlow<Long> = _selectedDate
    val settings: StateFlow<SettingsPreferences> = settingsRepository.observeSettings().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SettingsPreferences()
    )

    val tasks = _selectedDate.flatMapLatest { date ->
        plannerRepository.observeTasks(date)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    fun moveDate(deltaDays: Long) {
        _selectedDate.update { it + deltaDays }
    }

    fun setDate(epochDay: Long) {
        _selectedDate.value = epochDay
    }

    fun addTask(
        title: String,
        description: String,
        repeatDaily: Boolean,
        timeMinutesOfDay: Int?,
        priority: Int,
        reminderEnabled: Boolean
    ) {
        if (title.isBlank()) return
        viewModelScope.launch {
            val task = TaskItem(
                title = title.trim(),
                description = description.trim(),
                dateEpochDay = _selectedDate.value,
                isCompleted = false,
                repeatDaily = repeatDaily,
                timeMinutesOfDay = timeMinutesOfDay,
                priority = priority,
                reminderEnabled = reminderEnabled
            )
            val taskId = plannerRepository.addTask(task)
            taskReminderScheduler.schedule(task.copy(taskId = taskId))
            TodoWidgetProvider.refreshAll(appContext)
        }
    }

    fun updateTask(task: TaskItem) {
        if (task.title.isBlank()) return
        viewModelScope.launch {
            plannerRepository.updateTask(task.copy(title = task.title.trim(), description = task.description.trim()))
            taskReminderScheduler.schedule(task)
            TodoWidgetProvider.refreshAll(appContext)
        }
    }

    fun toggleTask(task: TaskItem, completed: Boolean) {
        viewModelScope.launch {
            plannerRepository.toggleTask(task.taskId, completed)
            val updated = task.copy(isCompleted = completed)
            if (completed) {
                taskReminderScheduler.cancel(task.taskId)
            } else {
                taskReminderScheduler.schedule(updated)
            }
            TodoWidgetProvider.refreshAll(appContext)
        }
    }

    fun deleteTask(taskId: Long) {
        viewModelScope.launch {
            plannerRepository.deleteTask(taskId)
            taskReminderScheduler.cancel(taskId)
            TodoWidgetProvider.refreshAll(appContext)
        }
    }
}

@OptIn(FlowPreview::class)
@HiltViewModel
class NutritionViewModel @Inject constructor(
    private val nutritionRepository: NutritionRepository
) : ViewModel() {
    val offlineCatalogCount: Int = nutritionRepository.getOfflineCatalogCount()
    private var allFoodsPage: Int = 0
    private var allFoodsMode: Boolean = false
    private var activeSearchJob: Job? = null
    private var skipDebouncedQuery: String? = null
    private val queryInput = MutableStateFlow("")

    private val _selectedDate = MutableStateFlow(DateUtils.todayEpochDay())
    val selectedDate: StateFlow<Long> = _selectedDate

    val meals = _selectedDate.flatMapLatest { date ->
        nutritionRepository.observeMeals(date)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    val customFoods = nutritionRepository.observeCustomFoods().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    val favoriteFoods = nutritionRepository.observeFavoriteFoods().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    val recentFoods = nutritionRepository.observeRecentFoods().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    private val _results = MutableStateFlow<List<FoodItem>>(emptyList())
    val results: StateFlow<List<FoodItem>> = _results

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _loadingMoreFoods = MutableStateFlow(false)
    val loadingMoreFoods: StateFlow<Boolean> = _loadingMoreFoods

    private val _canLoadMoreFoods = MutableStateFlow(false)
    val canLoadMoreFoods: StateFlow<Boolean> = _canLoadMoreFoods

    private val _searchMessage = MutableStateFlow<String?>(null)
    val searchMessage: StateFlow<String?> = _searchMessage

    private val _actionMessage = MutableStateFlow<String?>(null)
    val actionMessage: StateFlow<String?> = _actionMessage

    init {
        loadAllFoods(reset = true)
        viewModelScope.launch {
            queryInput
                .drop(1)
                .debounce(350)
                .distinctUntilChanged()
                .collect { query ->
                    val normalized = query.trim()
                    if (skipDebouncedQuery == normalized) {
                        skipDebouncedQuery = null
                        return@collect
                    }
                    if (normalized.isBlank()) {
                        loadAllFoods(reset = true)
                    } else {
                        performSearch(normalized)
                    }
                }
        }
    }

    fun onSearchQueryChanged(query: String) {
        queryInput.value = query
    }

    fun searchFoods(query: String) {
        searchFoodsNow(query)
    }

    fun searchFoodsNow(query: String) {
        val normalized = query.trim()
        skipDebouncedQuery = normalized
        if (normalized.isBlank()) {
            queryInput.value = ""
            loadAllFoods(reset = true)
            return
        }
        queryInput.value = normalized
        performSearch(normalized)
    }

    private fun performSearch(query: String) {
        allFoodsMode = false
        _canLoadMoreFoods.value = false
        activeSearchJob?.cancel()
        activeSearchJob = viewModelScope.launch {
            _loading.value = true
            val result = runCatching { nutritionRepository.searchFoods(query) }
            _results.value = result.getOrElse { emptyList() }
            _searchMessage.value = when {
                result.isFailure -> "Could not reach USDA. Showing offline foods ($offlineCatalogCount items)."
                _results.value.isEmpty() -> "No foods found. Try egg, rice, oats, chicken."
                else -> null
            }
            _loading.value = false
        }
    }

    fun loadAllFoods(reset: Boolean = true) {
        activeSearchJob?.cancel()
        viewModelScope.launch {
            _loading.value = true
            allFoodsMode = true

            val firstRemotePage = nutritionRepository.browseFoods(pageNumber = 1, pageSize = 200)
            if (firstRemotePage.isNotEmpty()) {
                var pageCursor = 1
                var merged = firstRemotePage
                // Prefetch first few USDA pages to show a large catalog immediately.
                repeat(2) {
                    val next = nutritionRepository.browseFoods(pageNumber = pageCursor + 1, pageSize = 200)
                    if (next.isEmpty()) return@repeat
                    pageCursor += 1
                    merged = (merged + next).distinctBy { "${it.fdcId}_${it.name.lowercase()}" }
                }

                allFoodsPage = pageCursor
                _canLoadMoreFoods.value = nutritionRepository
                    .browseFoods(pageNumber = pageCursor + 1, pageSize = 200)
                    .isNotEmpty()
                _results.value = merged
                _searchMessage.value = "Showing USDA online foods (${merged.size}). Tap Load More for more pages."
            } else {
                val offlineFallback = nutritionRepository.searchFoods("")
                allFoodsPage = 0
                _canLoadMoreFoods.value = false
                _results.value = offlineFallback
                _searchMessage.value = "USDA unavailable. Showing offline foods (${offlineFallback.size} items)."
            }

            if (!reset) {
                _searchMessage.value = "List refreshed."
            }
            _loading.value = false
        }
    }

    fun loadMoreFoods() {
        if (!allFoodsMode || _loadingMoreFoods.value || !_canLoadMoreFoods.value) return
        viewModelScope.launch {
            _loadingMoreFoods.value = true
            val nextPage = allFoodsPage + 1
            val more = nutritionRepository.browseFoods(pageNumber = nextPage, pageSize = 200)

            if (more.isEmpty()) {
                _canLoadMoreFoods.value = false
                _searchMessage.value = "No more foods available from USDA pages right now."
            } else {
                val merged = (_results.value + more).distinctBy { "${it.fdcId}_${it.name.lowercase()}" }
                val addedCount = (merged.size - _results.value.size).coerceAtLeast(0)
                _results.value = merged
                allFoodsPage = nextPage
                _searchMessage.value = "Loaded $addedCount more foods. Total ${merged.size}."
            }
            _loadingMoreFoods.value = false
        }
    }

    fun findFoodByBarcode(barcode: String) {
        val normalized = barcode.trim()
        if (normalized.isBlank()) return
        allFoodsMode = false
        _canLoadMoreFoods.value = false
        activeSearchJob?.cancel()
        skipDebouncedQuery = normalized
        queryInput.value = normalized
        activeSearchJob = viewModelScope.launch {
            _loading.value = true
            val result = runCatching {
                nutritionRepository.lookupFoodByBarcode(normalized)
            }
            val food = result.getOrNull()
            if (food != null) {
                _results.value = listOf(food)
                _searchMessage.value = "Barcode matched: ${food.name}"
            } else {
                _results.value = emptyList()
                _searchMessage.value = if (result.isFailure) {
                    "Barcode lookup failed. Check internet or USDA key, then retry."
                } else {
                    "No USDA food found for barcode $normalized."
                }
            }
            _loading.value = false
        }
    }

    fun copyYesterdayMeals() {
        viewModelScope.launch {
            val today = _selectedDate.value
            val copied = nutritionRepository.copyMeals(
                fromEpochDay = today - 1,
                toEpochDay = today
            )
            _actionMessage.value = if (copied > 0) {
                "Copied $copied meals from yesterday."
            } else {
                "No meals found yesterday to copy."
            }
        }
    }

    fun addFood(
        foodItem: FoodItem,
        mealType: String = "Meal",
        grams: Double = 100.0
    ) {
        viewModelScope.launch {
            val safeGrams = grams.coerceAtLeast(1.0)
            val base = foodItem.baseGrams.coerceAtLeast(1.0)
            val caloriesPer100 = (foodItem.calories / base) * 100.0
            val proteinPer100 = (foodItem.protein / base) * 100.0
            val carbsPer100 = (foodItem.carbs / base) * 100.0
            val fatPer100 = (foodItem.fat / base) * 100.0
            nutritionRepository.recordRecentFood(foodItem)
            nutritionRepository.addMeal(
                MealEntry(
                    dateEpochDay = _selectedDate.value,
                    mealType = mealType,
                    foodName = foodItem.name,
                    grams = safeGrams,
                    calories = round1((caloriesPer100 * safeGrams) / 100.0),
                    protein = round1((proteinPer100 * safeGrams) / 100.0),
                    carbs = round1((carbsPer100 * safeGrams) / 100.0),
                    fat = round1((fatPer100 * safeGrams) / 100.0),
                    source = "USDA_ONLINE"
                )
            )
        }
    }

    fun saveCustomFood(
        name: String,
        caloriesPer100g: Double,
        proteinPer100g: Double,
        carbsPer100g: Double,
        fatPer100g: Double,
        servingLabel: String?,
        servingGrams: Double?
    ) {
        val normalizedName = name.trim()
        if (normalizedName.isBlank()) return
        val id = normalizedName.lowercase().replace(Regex("\\s+"), "_")
        val presets = if (!servingLabel.isNullOrBlank() && servingGrams != null && servingGrams > 0.0) {
            listOf(ServingPreset(label = servingLabel.trim(), grams = servingGrams))
        } else {
            emptyList()
        }
        viewModelScope.launch {
            nutritionRepository.saveCustomFood(
                CustomFood(
                    id = id,
                    name = normalizedName,
                    caloriesPer100g = caloriesPer100g.coerceAtLeast(0.0),
                    proteinPer100g = proteinPer100g.coerceAtLeast(0.0),
                    carbsPer100g = carbsPer100g.coerceAtLeast(0.0),
                    fatPer100g = fatPer100g.coerceAtLeast(0.0),
                    servings = presets
                )
            )
        }
    }

    fun saveServingPreset(foodId: String, label: String, grams: Double) {
        val normalizedLabel = label.trim().ifBlank { "${round1(grams)} g" }
        val safeGrams = grams.coerceAtLeast(1.0)
        viewModelScope.launch {
            nutritionRepository.saveServingPreset(
                foodId = foodId,
                preset = ServingPreset(label = normalizedLabel, grams = safeGrams)
            )
        }
    }

    fun addCustomFood(food: CustomFood, mealType: String = "Meal", grams: Double) {
        val safeGrams = grams.coerceAtLeast(1.0)
        viewModelScope.launch {
            nutritionRepository.recordRecentFood(food.toFoodItem())
            nutritionRepository.addMeal(
                MealEntry(
                    dateEpochDay = _selectedDate.value,
                    mealType = mealType,
                    foodName = food.name,
                    grams = safeGrams,
                    calories = round1((food.caloriesPer100g * safeGrams) / 100.0),
                    protein = round1((food.proteinPer100g * safeGrams) / 100.0),
                    carbs = round1((food.carbsPer100g * safeGrams) / 100.0),
                    fat = round1((food.fatPer100g * safeGrams) / 100.0),
                    source = "CUSTOM"
                )
            )
        }
    }

    fun addCustomFoodServing(food: CustomFood, mealType: String, serving: ServingPreset) {
        addCustomFood(food = food, mealType = mealType, grams = serving.grams)
    }

    fun toggleFavoriteFood(foodItem: FoodItem) {
        viewModelScope.launch {
            nutritionRepository.toggleFavoriteFood(foodItem)
        }
    }

    fun deleteMeal(mealEntryId: Long) {
        viewModelScope.launch {
            nutritionRepository.deleteMeal(mealEntryId)
        }
    }

    private fun CustomFood.toFoodItem(): FoodItem {
        return FoodItem(
            fdcId = id.hashCode().toLong(),
            name = name,
            calories = caloriesPer100g,
            protein = proteinPer100g,
            carbs = carbsPer100g,
            fat = fatPer100g,
            baseGrams = 100.0
        )
    }

    private fun round1(value: Double): Double {
        return round(value * 10.0) / 10.0
    }
}

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val reminderScheduler: ReminderScheduler,
    private val maintenanceManager: AppMaintenanceManager,
    private val syncQueueRepository: SyncQueueRepository,
    private val cloudSyncScheduler: CloudSyncScheduler,
    @ApplicationContext private val appContext: Context
) : ViewModel() {
    val settings = settingsRepository.observeSettings().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SettingsPreferences()
    )

    val pendingSyncCount = syncQueueRepository.observePendingCount().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = 0
    )

    private val _systemMessage = MutableStateFlow<String?>(null)
    val systemMessage: StateFlow<String?> = _systemMessage

    fun setTheme(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setDarkTheme(enabled)
        }
    }

    fun setNotifications(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setNotifications(enabled)
            val current = settings.value
            reminderScheduler.syncReminders(
                enabled = enabled,
                weightReminderHour = current.smartReminderHour,
                hydrationIntervalHours = current.hydrationIntervalHours
            )
        }
    }

    fun setHaptics(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setHaptics(enabled)
        }
    }

    fun setSmartReminders(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setSmartReminders(enabled)
            val current = settings.value
            reminderScheduler.syncReminders(
                enabled = current.notificationsEnabled,
                weightReminderHour = current.smartReminderHour,
                hydrationIntervalHours = current.hydrationIntervalHours
            )
        }
    }

    fun syncNow() {
        cloudSyncScheduler.syncNow()
    }

    fun exportBackup() {
        viewModelScope.launch {
            val result = runCatching { maintenanceManager.exportBackup() }
            _systemMessage.value = result.fold(
                onSuccess = { "Backup exported: ${it.name}" },
                onFailure = { "Backup export failed: ${it.message.orEmpty()}" }
            )
        }
    }

    fun restoreLatestBackup() {
        viewModelScope.launch {
            val result = runCatching { maintenanceManager.restoreLatestBackup() }
            _systemMessage.value = result.fold(
                onSuccess = { it },
                onFailure = { "Restore failed: ${it.message.orEmpty()}" }
            )
        }
    }

    fun shareDiagnostics() {
        viewModelScope.launch {
            val result = runCatching { maintenanceManager.exportDiagnostics() }
            result.onSuccess { diagnosticsFile ->
                val fileUri = FileProvider.getUriForFile(
                    appContext,
                    "${appContext.packageName}.fileprovider",
                    diagnosticsFile
                )
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_STREAM, fileUri)
                    putExtra(Intent.EXTRA_SUBJECT, "Fitx Diagnostics")
                    putExtra(Intent.EXTRA_TEXT, "Fitx diagnostics report attached.")
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                appContext.startActivity(
                    Intent.createChooser(shareIntent, "Share diagnostics")
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                )
                _systemMessage.value = "Diagnostics exported and ready to share."
            }.onFailure {
                _systemMessage.value = "Diagnostics export failed: ${it.message.orEmpty()}"
            }
        }
    }
}

@HiltViewModel
class HealthCheckViewModel @Inject constructor(
    private val maintenanceManager: AppMaintenanceManager
) : ViewModel() {
    private val _checks = MutableStateFlow(maintenanceManager.buildHealthChecks())
    val checks: StateFlow<List<com.fitx.app.domain.model.HealthCheckItem>> = _checks

    fun runHealthCheck() {
        _checks.value = maintenanceManager.buildHealthChecks()
    }
}
