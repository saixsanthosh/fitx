package com.fitx.app.domain.usecase

import com.fitx.app.domain.model.DashboardSummary
import com.fitx.app.domain.model.TodayScoreBreakdown
import com.fitx.app.domain.repository.ActivityRepository
import com.fitx.app.domain.repository.NutritionRepository
import com.fitx.app.domain.repository.PlannerRepository
import com.fitx.app.domain.repository.UserProfileRepository
import com.fitx.app.domain.repository.WeightRepository
import com.fitx.app.util.DateUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class ObserveDashboardUseCase @Inject constructor(
    private val userProfileRepository: UserProfileRepository,
    private val activityRepository: ActivityRepository,
    private val plannerRepository: PlannerRepository,
    private val weightRepository: WeightRepository,
    private val nutritionRepository: NutritionRepository,
    private val calculateHealthMetricsUseCase: CalculateHealthMetricsUseCase
) {
    operator fun invoke(dateEpochDay: Long = DateUtils.todayEpochDay()): Flow<DashboardSummary> {
        val weekStart = dateEpochDay - 6
        return combine(
            userProfileRepository.observeProfile(),
            activityRepository.observeSessions(),
            plannerRepository.observeTasks(dateEpochDay),
            weightRepository.observeEntries(),
            nutritionRepository.observeMeals(dateEpochDay)
        ) { profile, sessions, tasks, weights, meals ->
            val todaySessions = sessions.filter {
                DateUtils.epochDayFromMillis(it.startTimeMillis) == dateEpochDay
            }
            val weeklySessions = sessions.filter {
                val epochDay = DateUtils.epochDayFromMillis(it.startTimeMillis)
                epochDay in weekStart..dateEpochDay
            }
            val weeklyActiveDays = weeklySessions
                .map { DateUtils.epochDayFromMillis(it.startTimeMillis) }
                .distinct()
                .size
            val weekly = weights.take(7)
            val weeklyWeightEntries = weights
                .filter { it.dateEpochDay in weekStart..dateEpochDay }
                .sortedBy { it.dateEpochDay }
            val weeklyWeightChange = if (weeklyWeightEntries.size >= 2) {
                weeklyWeightEntries.last().weightKg - weeklyWeightEntries.first().weightKg
            } else {
                null
            }

            val dailyTarget = profile?.let { calculateHealthMetricsUseCase(it) }?.dailyCalorieTarget?.toDouble()
            val mealCalories = meals.sumOf { it.calories }
            val activityScore = calculateActivityScore(
                distanceMeters = todaySessions.sumOf { it.distanceMeters },
                steps = todaySessions.sumOf { it.steps },
                caloriesBurned = todaySessions.sumOf { it.caloriesBurned }
            )
            val nutritionScore = calculateNutritionScore(
                mealCalories = mealCalories,
                dailyTarget = dailyTarget
            )
            val tasksScore = calculateTaskScore(
                completed = tasks.count { it.isCompleted },
                total = tasks.size
            )
            val consistencyScore = calculateConsistencyScore(
                hasMeal = meals.isNotEmpty(),
                hasActivity = todaySessions.isNotEmpty(),
                hasWeightLog = weights.any { it.dateEpochDay == dateEpochDay },
                hasAnyTaskDone = tasks.any { it.isCompleted }
            )
            val overallTodayScore = weightedScore(
                activity = activityScore,
                nutrition = nutritionScore,
                tasks = tasksScore,
                consistency = consistencyScore
            )

            DashboardSummary(
                healthMetrics = profile?.let { calculateHealthMetricsUseCase(it) },
                latestWeightKg = weights.firstOrNull()?.weightKg,
                weeklyWeights = weekly,
                todayTasks = tasks,
                completedTasks = tasks.count { it.isCompleted },
                todayDistanceMeters = todaySessions.sumOf { it.distanceMeters },
                todayCaloriesBurned = todaySessions.sumOf { it.caloriesBurned },
                todaySteps = todaySessions.sumOf { it.steps },
                todayMealCalories = mealCalories,
                weeklyDistanceMeters = weeklySessions.sumOf { it.distanceMeters },
                weeklyCaloriesBurned = weeklySessions.sumOf { it.caloriesBurned },
                weeklySteps = weeklySessions.sumOf { it.steps },
                weeklySessionCount = weeklySessions.size,
                weeklyActiveDays = weeklyActiveDays,
                weeklyWeightChangeKg = weeklyWeightChange,
                todayScore = overallTodayScore,
                todayScoreBreakdown = TodayScoreBreakdown(
                    activity = activityScore,
                    nutrition = nutritionScore,
                    tasks = tasksScore,
                    consistency = consistencyScore
                )
            )
        }
    }

    private fun calculateActivityScore(distanceMeters: Double, steps: Int, caloriesBurned: Int): Int {
        val distanceScore = (distanceMeters / 4_000.0).coerceIn(0.0, 1.0) * 100.0
        val stepsScore = (steps / 7_500.0).coerceIn(0.0, 1.0) * 100.0
        val calorieScore = (caloriesBurned / 350.0).coerceIn(0.0, 1.0) * 100.0
        return ((distanceScore * 0.45) + (stepsScore * 0.35) + (calorieScore * 0.2)).toInt().coerceIn(0, 100)
    }

    private fun calculateNutritionScore(mealCalories: Double, dailyTarget: Double?): Int {
        if (dailyTarget == null || dailyTarget <= 0.0) return 65
        val deltaRatio = kotlin.math.abs(mealCalories - dailyTarget) / dailyTarget
        return ((1.0 - deltaRatio.coerceIn(0.0, 1.0)) * 100.0).toInt().coerceIn(0, 100)
    }

    private fun calculateTaskScore(completed: Int, total: Int): Int {
        if (total <= 0) return 55
        return ((completed.toDouble() / total.toDouble()) * 100.0).toInt().coerceIn(0, 100)
    }

    private fun calculateConsistencyScore(
        hasMeal: Boolean,
        hasActivity: Boolean,
        hasWeightLog: Boolean,
        hasAnyTaskDone: Boolean
    ): Int {
        var score = 0
        if (hasMeal) score += 25
        if (hasActivity) score += 35
        if (hasWeightLog) score += 20
        if (hasAnyTaskDone) score += 20
        return score.coerceIn(0, 100)
    }

    private fun weightedScore(
        activity: Int,
        nutrition: Int,
        tasks: Int,
        consistency: Int
    ): Int {
        val weighted = (activity * 0.4) + (nutrition * 0.25) + (tasks * 0.25) + (consistency * 0.1)
        return weighted.toInt().coerceIn(0, 100)
    }
}

