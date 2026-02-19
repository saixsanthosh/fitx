package com.fitx.app.data.local.mapper

import com.fitx.app.data.local.entity.ActivityPointEntity
import com.fitx.app.data.local.entity.ActivitySessionEntity
import com.fitx.app.data.local.entity.ActivitySessionWithPoints
import com.fitx.app.data.local.entity.ExerciseLogEntity
import com.fitx.app.data.local.entity.HabitCompletionEntity
import com.fitx.app.data.local.entity.HabitEntity
import com.fitx.app.data.local.entity.MealEntryEntity
import com.fitx.app.data.local.entity.TaskEntity
import com.fitx.app.data.local.entity.UserProfileEntity
import com.fitx.app.data.local.entity.WeightEntryEntity
import com.fitx.app.data.local.entity.WorkoutTemplateEntity
import com.fitx.app.domain.model.ActivityLevel
import com.fitx.app.domain.model.ActivityPoint
import com.fitx.app.domain.model.ActivitySession
import com.fitx.app.domain.model.ActivitySessionDetail
import com.fitx.app.domain.model.ActivityType
import com.fitx.app.domain.model.ExerciseLog
import com.fitx.app.domain.model.Gender
import com.fitx.app.domain.model.GoalType
import com.fitx.app.domain.model.Habit
import com.fitx.app.domain.model.HabitCompletion
import com.fitx.app.domain.model.MealEntry
import com.fitx.app.domain.model.TaskItem
import com.fitx.app.domain.model.UserProfile
import com.fitx.app.domain.model.WeightEntry
import com.fitx.app.domain.model.WorkoutTemplate

private fun parseGender(value: String): Gender = Gender.entries.firstOrNull { it.name == value } ?: Gender.OTHER

private fun parseActivityLevel(value: String): ActivityLevel {
    return ActivityLevel.entries.firstOrNull { it.name == value } ?: ActivityLevel.MODERATE
}

private fun parseGoal(value: String): GoalType = GoalType.entries.firstOrNull { it.name == value } ?: GoalType.MAINTAIN

private fun parseActivityType(value: String): ActivityType {
    return ActivityType.entries.firstOrNull { it.name == value } ?: ActivityType.WALKING
}

fun UserProfileEntity.toDomain(): UserProfile = UserProfile(
    id = id,
    heightCm = heightCm,
    weightKg = weightKg,
    age = age,
    gender = parseGender(gender),
    activityLevel = parseActivityLevel(activityLevel),
    goalType = parseGoal(goalType),
    goalWeightKg = goalWeightKg
)

fun UserProfile.toEntity(): UserProfileEntity = UserProfileEntity(
    id = id,
    heightCm = heightCm,
    weightKg = weightKg,
    age = age,
    gender = gender.name,
    activityLevel = activityLevel.name,
    goalType = goalType.name,
    goalWeightKg = goalWeightKg
)

fun ActivitySessionEntity.toDomain(): ActivitySession = ActivitySession(
    sessionId = sessionId,
    activityType = parseActivityType(activityType),
    startTimeMillis = startTimeMillis,
    endTimeMillis = endTimeMillis,
    durationSeconds = durationSeconds,
    distanceMeters = distanceMeters,
    averageSpeedMps = averageSpeedMps,
    caloriesBurned = caloriesBurned,
    steps = steps
)

fun ActivitySession.toEntity(): ActivitySessionEntity = ActivitySessionEntity(
    sessionId = sessionId,
    activityType = activityType.name,
    startTimeMillis = startTimeMillis,
    endTimeMillis = endTimeMillis,
    durationSeconds = durationSeconds,
    distanceMeters = distanceMeters,
    averageSpeedMps = averageSpeedMps,
    caloriesBurned = caloriesBurned,
    steps = steps
)

fun ActivityPointEntity.toDomain(): ActivityPoint = ActivityPoint(
    pointId = pointId,
    sessionId = sessionId,
    latitude = latitude,
    longitude = longitude,
    timestampMillis = timestampMillis
)

fun ActivityPoint.toEntity(sessionId: Long): ActivityPointEntity = ActivityPointEntity(
    pointId = pointId,
    sessionId = sessionId,
    latitude = latitude,
    longitude = longitude,
    timestampMillis = timestampMillis
)

fun ActivitySessionWithPoints.toDomain(): ActivitySessionDetail = ActivitySessionDetail(
    session = session.toDomain(),
    points = points.map { it.toDomain() }
)

fun WeightEntryEntity.toDomain(): WeightEntry = WeightEntry(
    entryId = entryId,
    dateEpochDay = dateEpochDay,
    weightKg = weightKg
)

fun WeightEntry.toEntity(): WeightEntryEntity = WeightEntryEntity(
    entryId = entryId,
    dateEpochDay = dateEpochDay,
    weightKg = weightKg
)

fun WorkoutTemplateEntity.toDomain(): WorkoutTemplate = WorkoutTemplate(
    templateId = templateId,
    name = name,
    description = description
)

fun WorkoutTemplate.toEntity(): WorkoutTemplateEntity = WorkoutTemplateEntity(
    templateId = templateId,
    name = name,
    description = description
)

fun ExerciseLogEntity.toDomain(): ExerciseLog = ExerciseLog(
    logId = logId,
    templateId = templateId,
    dateEpochDay = dateEpochDay,
    exerciseName = exerciseName,
    sets = sets,
    reps = reps,
    weightKg = weightKg,
    notes = notes
)

fun ExerciseLog.toEntity(): ExerciseLogEntity = ExerciseLogEntity(
    logId = logId,
    templateId = templateId,
    dateEpochDay = dateEpochDay,
    exerciseName = exerciseName,
    sets = sets,
    reps = reps,
    weightKg = weightKg,
    notes = notes
)

fun HabitEntity.toDomain(): Habit = Habit(
    habitId = habitId,
    name = name,
    targetPerDay = targetPerDay,
    enabled = enabled
)

fun Habit.toEntity(): HabitEntity = HabitEntity(
    habitId = habitId,
    name = name,
    targetPerDay = targetPerDay,
    enabled = enabled
)

fun HabitCompletionEntity.toDomain(): HabitCompletion = HabitCompletion(
    completionId = completionId,
    habitId = habitId,
    dateEpochDay = dateEpochDay,
    count = count
)

fun HabitCompletion.toEntity(): HabitCompletionEntity = HabitCompletionEntity(
    completionId = completionId,
    habitId = habitId,
    dateEpochDay = dateEpochDay,
    count = count
)

fun TaskEntity.toDomain(): TaskItem = TaskItem(
    taskId = taskId,
    title = title,
    description = description,
    dateEpochDay = dateEpochDay,
    isCompleted = isCompleted,
    repeatDaily = repeatDaily,
    timeMinutesOfDay = timeMinutesOfDay,
    priority = priority,
    reminderEnabled = reminderEnabled
)

fun TaskItem.toEntity(): TaskEntity = TaskEntity(
    taskId = taskId,
    title = title,
    description = description,
    dateEpochDay = dateEpochDay,
    isCompleted = isCompleted,
    repeatDaily = repeatDaily,
    timeMinutesOfDay = timeMinutesOfDay,
    priority = priority,
    reminderEnabled = reminderEnabled
)

fun MealEntryEntity.toDomain(): MealEntry = MealEntry(
    mealEntryId = mealEntryId,
    dateEpochDay = dateEpochDay,
    mealType = mealType,
    foodName = foodName,
    grams = grams,
    calories = calories,
    protein = protein,
    carbs = carbs,
    fat = fat,
    source = source
)

fun MealEntry.toEntity(): MealEntryEntity = MealEntryEntity(
    mealEntryId = mealEntryId,
    dateEpochDay = dateEpochDay,
    mealType = mealType,
    foodName = foodName,
    grams = grams,
    calories = calories,
    protein = protein,
    carbs = carbs,
    fat = fat,
    source = source
)
