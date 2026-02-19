package com.fitx.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.fitx.app.data.local.dao.ActivityDao
import com.fitx.app.data.local.dao.HabitDao
import com.fitx.app.data.local.dao.MealDao
import com.fitx.app.data.local.dao.SyncQueueDao
import com.fitx.app.data.local.dao.TaskDao
import com.fitx.app.data.local.dao.UserProfileDao
import com.fitx.app.data.local.dao.WeightDao
import com.fitx.app.data.local.dao.WorkoutDao
import com.fitx.app.data.local.entity.ActivityPointEntity
import com.fitx.app.data.local.entity.ActivitySessionEntity
import com.fitx.app.data.local.entity.ExerciseLogEntity
import com.fitx.app.data.local.entity.HabitCompletionEntity
import com.fitx.app.data.local.entity.HabitEntity
import com.fitx.app.data.local.entity.MealEntryEntity
import com.fitx.app.data.local.entity.SyncQueueEntity
import com.fitx.app.data.local.entity.TaskEntity
import com.fitx.app.data.local.entity.UserProfileEntity
import com.fitx.app.data.local.entity.WeightEntryEntity
import com.fitx.app.data.local.entity.WorkoutTemplateEntity

@Database(
    entities = [
        UserProfileEntity::class,
        ActivitySessionEntity::class,
        ActivityPointEntity::class,
        WeightEntryEntity::class,
        WorkoutTemplateEntity::class,
        ExerciseLogEntity::class,
        HabitEntity::class,
        HabitCompletionEntity::class,
        TaskEntity::class,
        MealEntryEntity::class,
        SyncQueueEntity::class
    ],
    version = 4,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userProfileDao(): UserProfileDao
    abstract fun activityDao(): ActivityDao
    abstract fun weightDao(): WeightDao
    abstract fun workoutDao(): WorkoutDao
    abstract fun habitDao(): HabitDao
    abstract fun taskDao(): TaskDao
    abstract fun mealDao(): MealDao
    abstract fun syncQueueDao(): SyncQueueDao
}
