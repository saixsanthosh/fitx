package com.fitx.app.util

import com.fitx.app.domain.model.ActivityType
import kotlin.math.roundToInt

object CalorieEstimator {
    fun estimate(
        type: ActivityType,
        weightKg: Double,
        durationSeconds: Long
    ): Int {
        if (durationSeconds <= 0L) return 0
        val durationMinutes = durationSeconds / 60.0
        val met = when (type) {
            ActivityType.WALKING -> 3.8
            ActivityType.CYCLING -> 7.5
        }
        return ((met * 3.5 * weightKg) / 200.0 * durationMinutes).roundToInt().coerceAtLeast(0)
    }
}

