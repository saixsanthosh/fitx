package com.fitx.app

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.fitx.app.service.sync.CloudSyncScheduler
import com.fitx.app.util.NotificationHelper
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class FitxApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var notificationHelper: NotificationHelper

    @Inject
    lateinit var cloudSyncScheduler: CloudSyncScheduler

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        notificationHelper.createChannels()
        cloudSyncScheduler.schedulePeriodicSync()
        runCatching {
            FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)
            val existingHandler = Thread.getDefaultUncaughtExceptionHandler()
            Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
                FirebaseCrashlytics.getInstance().recordException(throwable)
                existingHandler?.uncaughtException(thread, throwable)
            }
        }
    }
}
