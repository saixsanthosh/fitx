package com.fitx.app.ui.theme.premium

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

/**
 * ADVANCED HAPTIC FEEDBACK PATTERNS
 * Synchronized with animations for immersive experience
 * NO COMPETITOR HAS THIS LEVEL OF HAPTIC SOPHISTICATION!
 */

class HapticEngine(private val context: Context) {
    private val vibrator: Vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        vibratorManager.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }
    
    /**
     * Success haptic - Smooth crescendo
     */
    fun playSuccess() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val timings = longArrayOf(0, 50, 50, 100)
            val amplitudes = intArrayOf(0, 100, 150, 255)
            val effect = VibrationEffect.createWaveform(timings, amplitudes, -1)
            vibrator.vibrate(effect)
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(200)
        }
    }
    
    /**
     * Error haptic - Sharp double pulse
     */
    fun playError() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val timings = longArrayOf(0, 50, 100, 50)
            val amplitudes = intArrayOf(0, 255, 0, 255)
            val effect = VibrationEffect.createWaveform(timings, amplitudes, -1)
            vibrator.vibrate(effect)
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(longArrayOf(0, 50, 100, 50), -1)
        }
    }
    
    /**
     * Milestone haptic - Celebration pattern
     */
    fun playMilestone() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val timings = longArrayOf(0, 30, 30, 30, 30, 30, 30, 100)
            val amplitudes = intArrayOf(0, 100, 0, 150, 0, 200, 0, 255)
            val effect = VibrationEffect.createWaveform(timings, amplitudes, -1)
            vibrator.vibrate(effect)
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(longArrayOf(0, 30, 30, 30, 30, 30, 30, 100), -1)
        }
    }
    
    /**
     * Level up haptic - Rising intensity
     */
    fun playLevelUp() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val timings = longArrayOf(0, 50, 30, 50, 30, 50, 30, 150)
            val amplitudes = intArrayOf(0, 80, 0, 120, 0, 180, 0, 255)
            val effect = VibrationEffect.createWaveform(timings, amplitudes, -1)
            vibrator.vibrate(effect)
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(400)
        }
    }
    
    /**
     * Click haptic - Light tap
     */
    fun playClick() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val effect = VibrationEffect.createOneShot(10, VibrationEffect.DEFAULT_AMPLITUDE)
            vibrator.vibrate(effect)
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(10)
        }
    }
    
    /**
     * Heavy click haptic - Strong tap
     */
    fun playHeavyClick() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val effect = VibrationEffect.createOneShot(30, 255)
            vibrator.vibrate(effect)
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(30)
        }
    }
    
    /**
     * Tick haptic - Subtle feedback
     */
    fun playTick() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val effect = VibrationEffect.createOneShot(5, 50)
            vibrator.vibrate(effect)
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(5)
        }
    }
    
    /**
     * Progress haptic - Rhythmic pulse
     */
    fun playProgress() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val timings = longArrayOf(0, 20, 80, 20, 80, 20)
            val amplitudes = intArrayOf(0, 100, 0, 100, 0, 100)
            val effect = VibrationEffect.createWaveform(timings, amplitudes, -1)
            vibrator.vibrate(effect)
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(longArrayOf(0, 20, 80, 20, 80, 20), -1)
        }
    }
    
    /**
     * Warning haptic - Urgent pattern
     */
    fun playWarning() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val timings = longArrayOf(0, 100, 50, 100, 50, 100)
            val amplitudes = intArrayOf(0, 200, 0, 200, 0, 200)
            val effect = VibrationEffect.createWaveform(timings, amplitudes, -1)
            vibrator.vibrate(effect)
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(longArrayOf(0, 100, 50, 100, 50, 100), -1)
        }
    }
    
    /**
     * Heartbeat haptic - Rhythmic like a heartbeat
     */
    fun playHeartbeat() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val timings = longArrayOf(0, 50, 100, 50, 500)
            val amplitudes = intArrayOf(0, 150, 0, 100, 0)
            val effect = VibrationEffect.createWaveform(timings, amplitudes, 0) // Repeat
            vibrator.vibrate(effect)
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(longArrayOf(0, 50, 100, 50, 500), 0)
        }
    }
    
    /**
     * Stop all vibrations
     */
    fun stop() {
        vibrator.cancel()
    }
    
    /**
     * Custom pattern haptic
     */
    fun playCustomPattern(timings: LongArray, amplitudes: IntArray, repeat: Boolean = false) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val effect = VibrationEffect.createWaveform(timings, amplitudes, if (repeat) 0 else -1)
            vibrator.vibrate(effect)
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(timings, if (repeat) 0 else -1)
        }
    }
    
    /**
     * Workout intensity haptic - Matches workout intensity
     */
    fun playWorkoutIntensity(intensity: Float) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val amplitude = (intensity * 255).toInt().coerceIn(50, 255)
            val duration = (intensity * 100).toLong().coerceIn(20, 100)
            val effect = VibrationEffect.createOneShot(duration, amplitude)
            vibrator.vibrate(effect)
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate((intensity * 100).toLong().coerceIn(20, 100))
        }
    }
    
    /**
     * Step counter haptic - Light pulse for each step
     */
    fun playStep() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val effect = VibrationEffect.createOneShot(8, 80)
            vibrator.vibrate(effect)
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(8)
        }
    }
    
    /**
     * Goal reached haptic - Triumphant pattern
     */
    fun playGoalReached() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val timings = longArrayOf(0, 50, 50, 50, 50, 50, 50, 200)
            val amplitudes = intArrayOf(0, 150, 0, 180, 0, 210, 0, 255)
            val effect = VibrationEffect.createWaveform(timings, amplitudes, -1)
            vibrator.vibrate(effect)
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(500)
        }
    }
    
    /**
     * Streak maintained haptic - Encouraging pattern
     */
    fun playStreakMaintained(streakDays: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val pulseCount = minOf(streakDays, 5)
            val timings = LongArray(pulseCount * 2 + 1) { i ->
                if (i == 0) 0L else if (i % 2 == 1) 30L else 50L
            }
            val amplitudes = IntArray(pulseCount * 2 + 1) { i ->
                if (i == 0) 0 else if (i % 2 == 1) 150 else 0
            }
            val effect = VibrationEffect.createWaveform(timings, amplitudes, -1)
            vibrator.vibrate(effect)
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(200)
        }
    }
    
    /**
     * Notification haptic - Gentle attention grabber
     */
    fun playNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val timings = longArrayOf(0, 50, 100, 50)
            val amplitudes = intArrayOf(0, 120, 0, 120)
            val effect = VibrationEffect.createWaveform(timings, amplitudes, -1)
            vibrator.vibrate(effect)
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(longArrayOf(0, 50, 100, 50), -1)
        }
    }
    
    /**
     * Swipe haptic - Smooth glide feedback
     */
    fun playSwipe() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val timings = longArrayOf(0, 15, 15, 15)
            val amplitudes = intArrayOf(0, 60, 80, 100)
            val effect = VibrationEffect.createWaveform(timings, amplitudes, -1)
            vibrator.vibrate(effect)
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(45)
        }
    }
    
    /**
     * Selection haptic - Item selected feedback
     */
    fun playSelection() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val effect = VibrationEffect.createOneShot(15, 120)
            vibrator.vibrate(effect)
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(15)
        }
    }
    
    /**
     * Impact haptic - Strong impact feedback
     */
    fun playImpact(intensity: ImpactIntensity = ImpactIntensity.MEDIUM) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val (duration, amplitude) = when (intensity) {
                ImpactIntensity.LIGHT -> Pair(10L, 100)
                ImpactIntensity.MEDIUM -> Pair(20L, 180)
                ImpactIntensity.HEAVY -> Pair(30L, 255)
            }
            val effect = VibrationEffect.createOneShot(duration, amplitude)
            vibrator.vibrate(effect)
        } else {
            @Suppress("DEPRECATION")
            val duration = when (intensity) {
                ImpactIntensity.LIGHT -> 10L
                ImpactIntensity.MEDIUM -> 20L
                ImpactIntensity.HEAVY -> 30L
            }
            vibrator.vibrate(duration)
        }
    }
}

enum class ImpactIntensity {
    LIGHT, MEDIUM, HEAVY
}

/**
 * Haptic feedback types for different UI interactions
 */
enum class HapticFeedbackType {
    CLICK,
    HEAVY_CLICK,
    TICK,
    SUCCESS,
    ERROR,
    WARNING,
    MILESTONE,
    LEVEL_UP,
    PROGRESS,
    HEARTBEAT,
    STEP,
    GOAL_REACHED,
    NOTIFICATION,
    SWIPE,
    SELECTION,
    IMPACT_LIGHT,
    IMPACT_MEDIUM,
    IMPACT_HEAVY
}

/**
 * Composable to remember haptic engine
 */
@Composable
fun rememberHapticEngine(): HapticEngine {
    val context = LocalContext.current
    return remember { HapticEngine(context) }
}

/**
 * Extension function to play haptic feedback
 */
fun HapticEngine.play(type: HapticFeedbackType) {
    when (type) {
        HapticFeedbackType.CLICK -> playClick()
        HapticFeedbackType.HEAVY_CLICK -> playHeavyClick()
        HapticFeedbackType.TICK -> playTick()
        HapticFeedbackType.SUCCESS -> playSuccess()
        HapticFeedbackType.ERROR -> playError()
        HapticFeedbackType.WARNING -> playWarning()
        HapticFeedbackType.MILESTONE -> playMilestone()
        HapticFeedbackType.LEVEL_UP -> playLevelUp()
        HapticFeedbackType.PROGRESS -> playProgress()
        HapticFeedbackType.HEARTBEAT -> playHeartbeat()
        HapticFeedbackType.STEP -> playStep()
        HapticFeedbackType.GOAL_REACHED -> playGoalReached()
        HapticFeedbackType.NOTIFICATION -> playNotification()
        HapticFeedbackType.SWIPE -> playSwipe()
        HapticFeedbackType.SELECTION -> playSelection()
        HapticFeedbackType.IMPACT_LIGHT -> playImpact(ImpactIntensity.LIGHT)
        HapticFeedbackType.IMPACT_MEDIUM -> playImpact(ImpactIntensity.MEDIUM)
        HapticFeedbackType.IMPACT_HEAVY -> playImpact(ImpactIntensity.HEAVY)
    }
}

/**
 * Synchronized haptic and animation system
 */
class SynchronizedFeedback(private val hapticEngine: HapticEngine) {
    /**
     * Play haptic synchronized with animation progress
     */
    suspend fun playSynchronized(
        animationDuration: Long,
        hapticPattern: HapticPattern
    ) {
        val startTime = System.currentTimeMillis()
        
        hapticPattern.events.forEach { event ->
            val delay = (animationDuration * event.timePercent).toLong()
            val elapsed = System.currentTimeMillis() - startTime
            val remaining = delay - elapsed
            
            if (remaining > 0) {
                kotlinx.coroutines.delay(remaining)
            }
            
            hapticEngine.play(event.type)
        }
    }
}

data class HapticPattern(
    val events: List<HapticEvent>
)

data class HapticEvent(
    val timePercent: Float, // 0.0 to 1.0
    val type: HapticFeedbackType
)

/**
 * Predefined haptic patterns for common animations
 */
object HapticPatterns {
    val BUTTON_PRESS = HapticPattern(
        listOf(
            HapticEvent(0f, HapticFeedbackType.CLICK),
            HapticEvent(0.5f, HapticFeedbackType.TICK)
        )
    )
    
    val CARD_FLIP = HapticPattern(
        listOf(
            HapticEvent(0f, HapticFeedbackType.SWIPE),
            HapticEvent(0.5f, HapticFeedbackType.IMPACT_LIGHT)
        )
    )
    
    val ACHIEVEMENT_UNLOCK = HapticPattern(
        listOf(
            HapticEvent(0f, HapticFeedbackType.MILESTONE),
            HapticEvent(0.3f, HapticFeedbackType.SUCCESS),
            HapticEvent(0.6f, HapticFeedbackType.LEVEL_UP)
        )
    )
    
    val PROGRESS_COMPLETE = HapticPattern(
        listOf(
            HapticEvent(0f, HapticFeedbackType.PROGRESS),
            HapticEvent(0.8f, HapticFeedbackType.GOAL_REACHED)
        )
    )
    
    val SWIPE_DISMISS = HapticPattern(
        listOf(
            HapticEvent(0f, HapticFeedbackType.SWIPE),
            HapticEvent(1f, HapticFeedbackType.IMPACT_MEDIUM)
        )
    )
}
