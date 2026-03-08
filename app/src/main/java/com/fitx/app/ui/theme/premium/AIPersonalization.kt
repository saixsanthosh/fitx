package com.fitx.app.ui.theme.premium

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import java.time.LocalTime
import kotlin.random.Random

/**
 * AI-POWERED PERSONALIZED UI SYSTEM
 * Adapts to user behavior, time of day, and preferences
 * NO COMPETITOR HAS THIS LEVEL OF INTELLIGENCE!
 */

/**
 * AI Theme Selector based on user behavior and time
 */
class AIThemeEngine {
    private var userActivityLevel = 0f
    private var userMoodScore = 0.5f
    private var timeOfDay = LocalTime.now()
    
    fun getPersonalizedTheme(
        activityLevel: Float,
        moodScore: Float,
        currentTime: LocalTime = LocalTime.now()
    ): PremiumTheme {
        userActivityLevel = activityLevel
        userMoodScore = moodScore
        timeOfDay = currentTime
        
        return when {
            // Morning - Energetic themes
            currentTime.hour in 5..11 && activityLevel > 0.7f -> PremiumThemes.CyberGreen
            currentTime.hour in 5..11 -> PremiumThemes.OceanBlue
            
            // Afternoon - Productive themes
            currentTime.hour in 12..17 && activityLevel > 0.6f -> PremiumThemes.ElectricBlue
            currentTime.hour in 12..17 -> PremiumThemes.SunsetOrange
            
            // Evening - Calm themes
            currentTime.hour in 18..21 && moodScore > 0.6f -> PremiumThemes.RoseGold
            currentTime.hour in 18..21 -> PremiumThemes.MidnightBlue
            
            // Night - Dark themes
            currentTime.hour in 22..23 || currentTime.hour in 0..4 -> PremiumThemes.GalaxyPurple
            
            // High activity - Energetic
            activityLevel > 0.8f -> PremiumThemes.LavaRed
            
            // Low mood - Uplifting
            moodScore < 0.4f -> PremiumThemes.NeonPurple
            
            // Default
            else -> PremiumThemes.ElectricBlue
        }
    }
    
    fun getAdaptiveColors(baseTheme: PremiumTheme): AdaptiveColors {
        val energyMultiplier = userActivityLevel.coerceIn(0.5f, 1.5f)
        
        return AdaptiveColors(
            primary = adjustColorBrightness(baseTheme.primaryColor, energyMultiplier),
            secondary = adjustColorBrightness(baseTheme.secondaryColor, energyMultiplier),
            accent = adjustColorBrightness(baseTheme.accentColor, energyMultiplier),
            background = if (timeOfDay.hour in 22..23 || timeOfDay.hour in 0..6) {
                Color(0xFF0A0A0A) // Darker at night
            } else {
                Color(0xFF121212)
            }
        )
    }
    
    private fun adjustColorBrightness(color: Color, multiplier: Float): Color {
        return Color(
            red = (color.red * multiplier).coerceIn(0f, 1f),
            green = (color.green * multiplier).coerceIn(0f, 1f),
            blue = (color.blue * multiplier).coerceIn(0f, 1f),
            alpha = color.alpha
        )
    }
}

data class AdaptiveColors(
    val primary: Color,
    val secondary: Color,
    val accent: Color,
    val background: Color
)

/**
 * AI-powered animation speed based on user preferences
 */
class AIAnimationEngine {
    private var userPreferredSpeed = 1f
    private var userMotionSensitivity = 1f
    
    fun getAdaptiveAnimationDuration(baseDuration: Int): Int {
        return (baseDuration / userPreferredSpeed).toInt()
    }
    
    fun getAdaptiveSpring(): SpringSpec<Float> {
        return spring(
            dampingRatio = Spring.DampingRatioMediumBouncy * userMotionSensitivity,
            stiffness = Spring.StiffnessMedium / userMotionSensitivity
        )
    }
    
    fun updateUserPreferences(speed: Float, sensitivity: Float) {
        userPreferredSpeed = speed.coerceIn(0.5f, 2f)
        userMotionSensitivity = sensitivity.coerceIn(0.5f, 1.5f)
    }
}

/**
 * AI-powered layout optimizer
 * Adjusts UI density based on user interaction patterns
 */
class AILayoutEngine {
    private var userTapAccuracy = 0.8f
    private var userScrollSpeed = 1f
    
    fun getAdaptiveSpacing(): Float {
        // More spacing for users with lower tap accuracy
        return when {
            userTapAccuracy < 0.6f -> 24f
            userTapAccuracy < 0.8f -> 16f
            else -> 12f
        }
    }
    
    fun getAdaptiveCardSize(): Float {
        // Larger cards for users with lower tap accuracy
        return when {
            userTapAccuracy < 0.6f -> 1.2f
            userTapAccuracy < 0.8f -> 1.1f
            else -> 1f
        }
    }
    
    fun getAdaptiveScrollBehavior(): ScrollBehavior {
        return when {
            userScrollSpeed > 1.5f -> ScrollBehavior.FAST
            userScrollSpeed < 0.5f -> ScrollBehavior.SLOW
            else -> ScrollBehavior.NORMAL
        }
    }
    
    fun updateUserBehavior(tapAccuracy: Float, scrollSpeed: Float) {
        userTapAccuracy = tapAccuracy.coerceIn(0f, 1f)
        userScrollSpeed = scrollSpeed.coerceIn(0.1f, 3f)
    }
}

enum class ScrollBehavior {
    SLOW, NORMAL, FAST
}

/**
 * AI-powered content prioritization
 * Shows most relevant content first based on user behavior
 */
class AIContentEngine {
    private val featureUsageCount = mutableMapOf<String, Int>()
    private val featureLastUsed = mutableMapOf<String, Long>()
    private val featureCompletionRate = mutableMapOf<String, Float>()
    
    fun trackFeatureUsage(featureId: String) {
        featureUsageCount[featureId] = (featureUsageCount[featureId] ?: 0) + 1
        featureLastUsed[featureId] = System.currentTimeMillis()
    }
    
    fun trackFeatureCompletion(featureId: String, completed: Boolean) {
        val currentRate = featureCompletionRate[featureId] ?: 0.5f
        val newRate = if (completed) {
            (currentRate + 0.1f).coerceAtMost(1f)
        } else {
            (currentRate - 0.05f).coerceAtLeast(0f)
        }
        featureCompletionRate[featureId] = newRate
    }
    
    fun getPrioritizedFeatures(features: List<String>): List<String> {
        return features.sortedByDescending { featureId ->
            val usageScore = (featureUsageCount[featureId] ?: 0) * 10
            val recencyScore = getRecencyScore(featureId)
            val completionScore = (featureCompletionRate[featureId] ?: 0.5f) * 100
            
            usageScore + recencyScore + completionScore
        }
    }
    
    private fun getRecencyScore(featureId: String): Float {
        val lastUsed = featureLastUsed[featureId] ?: return 0f
        val hoursSinceUsed = (System.currentTimeMillis() - lastUsed) / (1000 * 60 * 60)
        return when {
            hoursSinceUsed < 1 -> 100f
            hoursSinceUsed < 6 -> 50f
            hoursSinceUsed < 24 -> 25f
            else -> 10f
        }
    }
}

/**
 * AI-powered smart suggestions
 */
class AISmartSuggestions {
    fun getPersonalizedSuggestions(
        userStats: UserStats,
        currentTime: LocalTime = LocalTime.now()
    ): List<SmartSuggestion> {
        val suggestions = mutableListOf<SmartSuggestion>()
        
        // Activity suggestions
        if (userStats.stepsToday < 5000 && currentTime.hour in 9..18) {
            suggestions.add(
                SmartSuggestion(
                    title = "Take a Walk",
                    description = "You're ${5000 - userStats.stepsToday} steps away from your halfway goal!",
                    priority = SuggestionPriority.HIGH,
                    action = "start_activity"
                )
            )
        }
        
        // Nutrition suggestions
        if (userStats.caloriesConsumed < userStats.caloriesGoal * 0.3f && currentTime.hour > 12) {
            suggestions.add(
                SmartSuggestion(
                    title = "Log Your Lunch",
                    description = "Don't forget to track your meals for accurate nutrition data",
                    priority = SuggestionPriority.MEDIUM,
                    action = "log_meal"
                )
            )
        }
        
        // Hydration suggestions
        if (userStats.waterIntake < 4 && currentTime.hour > 14) {
            suggestions.add(
                SmartSuggestion(
                    title = "Stay Hydrated",
                    description = "You've only had ${userStats.waterIntake} glasses today",
                    priority = SuggestionPriority.HIGH,
                    action = "log_water"
                )
            )
        }
        
        // Workout suggestions
        if (!userStats.hasWorkedOutToday && currentTime.hour in 17..20) {
            suggestions.add(
                SmartSuggestion(
                    title = "Evening Workout",
                    description = "Perfect time for your training session!",
                    priority = SuggestionPriority.MEDIUM,
                    action = "start_workout"
                )
            )
        }
        
        // Habit suggestions
        if (userStats.habitStreak > 0 && !userStats.habitsCompletedToday) {
            suggestions.add(
                SmartSuggestion(
                    title = "Keep Your Streak!",
                    description = "Don't break your ${userStats.habitStreak}-day streak",
                    priority = SuggestionPriority.HIGH,
                    action = "check_habits"
                )
            )
        }
        
        // Rest suggestions
        if (userStats.activeMinutes > 120 && currentTime.hour > 20) {
            suggestions.add(
                SmartSuggestion(
                    title = "Great Work Today!",
                    description = "You've been very active. Time to rest and recover.",
                    priority = SuggestionPriority.LOW,
                    action = "view_stats"
                )
            )
        }
        
        return suggestions.sortedByDescending { it.priority.value }
    }
}

data class UserStats(
    val stepsToday: Int = 0,
    val caloriesConsumed: Int = 0,
    val caloriesGoal: Int = 2000,
    val waterIntake: Int = 0,
    val hasWorkedOutToday: Boolean = false,
    val habitStreak: Int = 0,
    val habitsCompletedToday: Boolean = false,
    val activeMinutes: Int = 0
)

data class SmartSuggestion(
    val title: String,
    val description: String,
    val priority: SuggestionPriority,
    val action: String
)

enum class SuggestionPriority(val value: Int) {
    LOW(1),
    MEDIUM(2),
    HIGH(3),
    URGENT(4)
}

/**
 * AI-powered predictive loading
 * Preloads content user is likely to access next
 */
class AIPredictiveLoader {
    private val navigationPatterns = mutableMapOf<String, MutableList<String>>()
    
    fun trackNavigation(from: String, to: String) {
        if (!navigationPatterns.containsKey(from)) {
            navigationPatterns[from] = mutableListOf()
        }
        navigationPatterns[from]?.add(to)
    }
    
    fun predictNextScreens(currentScreen: String, count: Int = 3): List<String> {
        val patterns = navigationPatterns[currentScreen] ?: return emptyList()
        
        // Count frequency of each destination
        val frequency = patterns.groupingBy { it }.eachCount()
        
        // Return top N most frequent destinations
        return frequency.entries
            .sortedByDescending { it.value }
            .take(count)
            .map { it.key }
    }
}

/**
 * Composable for AI-powered smart suggestion card
 */
@Composable
fun AISmartSuggestionCard(
    suggestion: SmartSuggestion,
    theme: PremiumTheme,
    onActionClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val priorityColor = when (suggestion.priority) {
        SuggestionPriority.URGENT -> Color(0xFFFF006E)
        SuggestionPriority.HIGH -> theme.primaryColor
        SuggestionPriority.MEDIUM -> theme.secondaryColor
        SuggestionPriority.LOW -> theme.accentColor
    }
    
    var isVisible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        delay(100)
        isVisible = true
    }
    
    androidx.compose.animation.AnimatedVisibility(
        visible = isVisible,
        enter = androidx.compose.animation.fadeIn() + 
                androidx.compose.animation.slideInVertically(initialOffsetY = { it / 2 })
    ) {
        GlowingCard(
            modifier = modifier.fillMaxWidth(),
            glowColor = priorityColor
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = suggestion.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = suggestion.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
                
                Button(
                    onClick = onActionClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = priorityColor
                    ),
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text("Go")
                }
            }
        }
    }
}

/**
 * AI-powered adaptive dashboard that reorganizes based on usage
 */
@Composable
fun AIAdaptiveDashboard(
    contentEngine: AIContentEngine,
    features: List<DashboardFeature>,
    modifier: Modifier = Modifier
) {
    val prioritizedFeatures = remember(features) {
        contentEngine.getPrioritizedFeatures(features.map { it.id })
            .mapNotNull { id -> features.find { it.id == id } }
    }
    
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        prioritizedFeatures.forEach { feature ->
            feature.content()
        }
    }
}

data class DashboardFeature(
    val id: String,
    val content: @Composable () -> Unit
)
