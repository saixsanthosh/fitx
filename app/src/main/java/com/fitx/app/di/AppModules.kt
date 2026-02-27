package com.fitx.app.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.room.Room
import com.fitx.app.data.local.AppDatabase
import com.fitx.app.data.local.dao.ActivityDao
import com.fitx.app.data.local.dao.HabitDao
import com.fitx.app.data.local.dao.MealDao
import com.fitx.app.data.local.dao.SyncQueueDao
import com.fitx.app.data.local.dao.TaskDao
import com.fitx.app.data.local.dao.UserProfileDao
import com.fitx.app.data.local.dao.WeightDao
import com.fitx.app.data.local.dao.WorkoutDao
import com.fitx.app.data.remote.AppUpdateApiService
import com.fitx.app.data.remote.UsdaApiService
import com.fitx.app.data.repository.AppUpdateRepositoryImpl
import com.fitx.app.data.repository.ActivityRepositoryImpl
import com.fitx.app.data.repository.AuthRepositoryImpl
import com.fitx.app.data.repository.HabitRepositoryImpl
import com.fitx.app.data.repository.NutritionRepositoryImpl
import com.fitx.app.data.repository.PlannerRepositoryImpl
import com.fitx.app.data.repository.SettingsRepositoryImpl
import com.fitx.app.data.repository.UserProfileRepositoryImpl
import com.fitx.app.data.repository.WeightRepositoryImpl
import com.fitx.app.data.repository.WorkoutRepositoryImpl
import com.fitx.app.domain.repository.AppUpdateRepository
import com.fitx.app.domain.repository.ActivityRepository
import com.fitx.app.domain.repository.AuthRepository
import com.fitx.app.domain.repository.HabitRepository
import com.fitx.app.domain.repository.NutritionRepository
import com.fitx.app.domain.repository.PlannerRepository
import com.fitx.app.domain.repository.SettingsRepository
import com.fitx.app.domain.repository.UserProfileRepository
import com.fitx.app.domain.repository.WeightRepository
import com.fitx.app.domain.repository.WorkoutRepository
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "fitx.db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideUserProfileDao(database: AppDatabase): UserProfileDao = database.userProfileDao()

    @Provides
    fun provideActivityDao(database: AppDatabase): ActivityDao = database.activityDao()

    @Provides
    fun provideWeightDao(database: AppDatabase): WeightDao = database.weightDao()

    @Provides
    fun provideWorkoutDao(database: AppDatabase): WorkoutDao = database.workoutDao()

    @Provides
    fun provideHabitDao(database: AppDatabase): HabitDao = database.habitDao()

    @Provides
    fun provideTaskDao(database: AppDatabase): TaskDao = database.taskDao()

    @Provides
    fun provideMealDao(database: AppDatabase): MealDao = database.mealDao()

    @Provides
    fun provideSyncQueueDao(database: AppDatabase): SyncQueueDao = database.syncQueueDao()
}

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideGson(): Gson = Gson()

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val logger = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
        return OkHttpClient.Builder()
            .addInterceptor(logger)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.nal.usda.gov/fdc/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Singleton
    fun provideUsdaApiService(retrofit: Retrofit): UsdaApiService {
        return retrofit.create(UsdaApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideAppUpdateApiService(retrofit: Retrofit): AppUpdateApiService {
        return retrofit.create(AppUpdateApiService::class.java)
    }
}

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {
    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            produceFile = { context.preferencesDataStoreFile("fitx_settings.preferences_pb") }
        )
    }
}

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {
    @Provides
    @Singleton
    fun provideFirebaseAuth(@ApplicationContext context: Context): FirebaseAuth? {
        return runCatching {
            val app = FirebaseApp.getApps(context).firstOrNull() ?: FirebaseApp.initializeApp(context)
            app?.let { FirebaseAuth.getInstance(it) }
        }.getOrNull()
    }

    @Provides
    @Singleton
    fun provideFirebaseFirestore(@ApplicationContext context: Context): FirebaseFirestore? {
        return runCatching {
            val app = FirebaseApp.getApps(context).firstOrNull() ?: FirebaseApp.initializeApp(context)
            app?.let { FirebaseFirestore.getInstance(it) }
        }.getOrNull()
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindUserProfileRepository(impl: UserProfileRepositoryImpl): UserProfileRepository

    @Binds
    abstract fun bindActivityRepository(impl: ActivityRepositoryImpl): ActivityRepository

    @Binds
    abstract fun bindWeightRepository(impl: WeightRepositoryImpl): WeightRepository

    @Binds
    abstract fun bindWorkoutRepository(impl: WorkoutRepositoryImpl): WorkoutRepository

    @Binds
    abstract fun bindHabitRepository(impl: HabitRepositoryImpl): HabitRepository

    @Binds
    abstract fun bindPlannerRepository(impl: PlannerRepositoryImpl): PlannerRepository

    @Binds
    abstract fun bindNutritionRepository(impl: NutritionRepositoryImpl): NutritionRepository

    @Binds
    abstract fun bindSettingsRepository(impl: SettingsRepositoryImpl): SettingsRepository

    @Binds
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    abstract fun bindAppUpdateRepository(impl: AppUpdateRepositoryImpl): AppUpdateRepository
}
