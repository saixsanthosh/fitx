package com.fitx.app.service

import android.annotation.SuppressLint
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.os.Looper
import androidx.core.app.ServiceCompat
import androidx.lifecycle.LifecycleService
import com.fitx.app.domain.model.ActivityPoint
import com.fitx.app.domain.model.ActivitySession
import com.fitx.app.domain.model.ActivityType
import com.fitx.app.domain.repository.ActivityRepository
import com.fitx.app.util.CalorieEstimator
import com.fitx.app.util.DateUtils
import com.fitx.app.util.NotificationHelper
import com.fitx.app.util.PermissionUtils
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ActivityTrackingService : LifecycleService(), SensorEventListener {

    @Inject
    lateinit var activityRepository: ActivityRepository

    @Inject
    lateinit var notificationHelper: NotificationHelper

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var sensorManager: SensorManager? = null
    private var stepCounterSensor: Sensor? = null

    private var timerJob: Job? = null
    private var previousLocation: Location? = null
    private var initialStepCounter: Float? = null
    private var trackedWeightKg: Double = 70.0
    private var trackedType: ActivityType = ActivityType.WALKING

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            val latest = result.lastLocation ?: return
            onLocationUpdate(latest)
        }
    }

    override fun onCreate() {
        super.onCreate()
        notificationHelper.createChannels()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        stepCounterSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> handleStart(intent)
            ACTION_STOP -> handleStop()
        }
        return START_STICKY
    }

    private fun handleStart(intent: Intent) {
        if (TrackingStore.state.value.isTracking) return
        trackedType = ActivityType.entries.firstOrNull {
            it.name == intent.getStringExtra(EXTRA_ACTIVITY_TYPE)
        } ?: ActivityType.WALKING
        trackedWeightKg = intent.getDoubleExtra(EXTRA_WEIGHT_KG, 70.0)

        val now = System.currentTimeMillis()
        TrackingStore.set(
            TrackingState(
                isTracking = true,
                activityType = trackedType,
                startTimeMillis = now
            )
        )
        previousLocation = null
        initialStepCounter = null

        startForeground(
            NotificationHelper.TRACKING_NOTIFICATION_ID,
            notificationHelper.buildTrackingNotification("Starting session...")
        )
        registerStepCounter()
        startLocationUpdates()
        startTicker()
    }

    private fun handleStop() {
        val finalState = TrackingStore.state.value
        if (!finalState.isTracking) {
            stopSelf()
            return
        }
        stopTicker()
        stopLocationUpdates()
        unregisterStepCounter()

        val end = System.currentTimeMillis()
        val session = ActivitySession(
            activityType = finalState.activityType,
            startTimeMillis = finalState.startTimeMillis,
            endTimeMillis = end,
            durationSeconds = finalState.durationSeconds,
            distanceMeters = finalState.distanceMeters,
            averageSpeedMps = finalState.averageSpeedMps,
            caloriesBurned = finalState.caloriesBurned,
            steps = finalState.steps
        )

        serviceScope.launch {
            if (session.durationSeconds > 0) {
                activityRepository.saveSession(session, finalState.pathPoints)
            }
        }
        TrackingStore.reset()
        ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun startTicker() {
        timerJob?.cancel()
        timerJob = serviceScope.launch {
            while (isActive) {
                val current = TrackingStore.state.value
                if (!current.isTracking) break
                val duration = ((System.currentTimeMillis() - current.startTimeMillis) / 1000L).coerceAtLeast(0)
                val speed = if (duration > 0) current.distanceMeters / duration else 0.0
                val calories = CalorieEstimator.estimate(
                    type = current.activityType,
                    weightKg = trackedWeightKg,
                    durationSeconds = duration
                )
                TrackingStore.update {
                    it.copy(
                        durationSeconds = duration,
                        averageSpeedMps = speed,
                        caloriesBurned = calories
                    )
                }
                val updated = TrackingStore.state.value
                notificationHelper.createChannels()
                val content = "Dist ${"%.2f".format(updated.distanceMeters / 1000)} km  " +
                    "Time ${DateUtils.formatDuration(updated.durationSeconds)}"
                startForeground(
                    NotificationHelper.TRACKING_NOTIFICATION_ID,
                    notificationHelper.buildTrackingNotification(content)
                )
                delay(1000L)
            }
        }
    }

    private fun stopTicker() {
        timerJob?.cancel()
        timerJob = null
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        if (!PermissionUtils.hasLocationPermissions(this)) return
        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 3000L)
            .setMinUpdateDistanceMeters(3f)
            .build()
        fusedLocationClient.requestLocationUpdates(
            request,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private fun onLocationUpdate(location: Location) {
        val currentState = TrackingStore.state.value
        if (!currentState.isTracking) return

        val prev = previousLocation
        val delta = if (prev == null) 0.0 else prev.distanceTo(location).toDouble().coerceAtLeast(0.0)
        previousLocation = location

        val point = ActivityPoint(
            sessionId = 0,
            latitude = location.latitude,
            longitude = location.longitude,
            timestampMillis = System.currentTimeMillis()
        )
        TrackingStore.update {
            it.copy(
                distanceMeters = it.distanceMeters + delta,
                pathPoints = it.pathPoints + point
            )
        }
    }

    private fun registerStepCounter() {
        val sensor = stepCounterSensor ?: return
        sensorManager?.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    private fun unregisterStepCounter() {
        sensorManager?.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        val sensorEvent = event ?: return
        if (sensorEvent.sensor.type != Sensor.TYPE_STEP_COUNTER) return
        val absolute = sensorEvent.values.firstOrNull() ?: return
        if (initialStepCounter == null) {
            initialStepCounter = absolute
            return
        }
        val steps = (absolute - (initialStepCounter ?: absolute)).toInt().coerceAtLeast(0)
        TrackingStore.update { it.copy(steps = steps) }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit

    override fun onDestroy() {
        stopTicker()
        stopLocationUpdates()
        unregisterStepCounter()
        super.onDestroy()
    }

    companion object {
        const val ACTION_START = "com.fitx.app.service.START_TRACKING"
        const val ACTION_STOP = "com.fitx.app.service.STOP_TRACKING"
        const val EXTRA_ACTIVITY_TYPE = "extra_activity_type"
        const val EXTRA_WEIGHT_KG = "extra_weight_kg"
    }
}

