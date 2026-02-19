package com.fitx.app.ui.viewmodel

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fitx.app.domain.model.ActivitySessionDetail
import com.fitx.app.domain.model.ActivityType
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
import com.fitx.app.service.ReminderScheduler
import com.fitx.app.service.TaskReminderScheduler
import com.fitx.app.service.TrackingStore
import com.fitx.app.service.sync.CloudSyncScheduler
import com.fitx.app.ui.widget.TodoWidgetProvider
import com.fitx.app.util.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class MainViewModel @Inject constructor(
    settingsRepository: SettingsRepository,
    private val reminderScheduler: ReminderScheduler
) : ViewModel() {
    val settings: StateFlow<SettingsPreferences> = settingsRepository.observeSettings().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SettingsPreferences()
    )

    init {
        viewModelScope.launch {
            settings.collect { prefs ->
                reminderScheduler.syncReminders(prefs.notificationsEnabled)
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
            todaySteps = 0
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
    @ApplicationContext private val appContext: Context
) : ViewModel() {

    val trackingState = TrackingStore.state

    val sessions = activityRepository.observeSessions().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
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
    @ApplicationContext private val appContext: Context
) : ViewModel() {
    private val _selectedDate = MutableStateFlow(DateUtils.todayEpochDay())
    val selectedDate: StateFlow<Long> = _selectedDate

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

@HiltViewModel
class NutritionViewModel @Inject constructor(
    private val nutritionRepository: NutritionRepository
) : ViewModel() {
    val offlineCatalogCount: Int = nutritionRepository.getOfflineCatalogCount()
    private var allFoodsPage: Int = 0
    private var allFoodsMode: Boolean = false

    private val _selectedDate = MutableStateFlow(DateUtils.todayEpochDay())
    val selectedDate: StateFlow<Long> = _selectedDate

    val meals = _selectedDate.flatMapLatest { date ->
        nutritionRepository.observeMeals(date)
    }.stateIn(
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

    init {
        loadAllFoods(reset = true)
    }

    fun searchFoods(query: String) {
        if (query.isBlank()) {
            loadAllFoods(reset = true)
            return
        }
        allFoodsMode = false
        _canLoadMoreFoods.value = false
        viewModelScope.launch {
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

    fun addFood(
        foodItem: FoodItem,
        mealType: String = "Meal",
        grams: Double = 100.0
    ) {
        viewModelScope.launch {
            val safeGrams = grams.coerceAtLeast(1.0)
            val ratio = safeGrams / foodItem.baseGrams.coerceAtLeast(1.0)
            nutritionRepository.addMeal(
                MealEntry(
                    dateEpochDay = _selectedDate.value,
                    mealType = mealType,
                    foodName = foodItem.name,
                    grams = safeGrams,
                    calories = foodItem.calories * ratio,
                    protein = foodItem.protein * ratio,
                    carbs = foodItem.carbs * ratio,
                    fat = foodItem.fat * ratio,
                    source = "USDA_ONLINE"
                )
            )
        }
    }

    fun deleteMeal(mealEntryId: Long) {
        viewModelScope.launch {
            nutritionRepository.deleteMeal(mealEntryId)
        }
    }
}

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val reminderScheduler: ReminderScheduler,
    private val syncQueueRepository: SyncQueueRepository,
    private val cloudSyncScheduler: CloudSyncScheduler
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

    fun setTheme(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setDarkTheme(enabled)
        }
    }

    fun setNotifications(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setNotifications(enabled)
            reminderScheduler.syncReminders(enabled)
        }
    }

    fun syncNow() {
        cloudSyncScheduler.syncNow()
    }
}
