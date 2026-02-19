package com.fitx.app.domain.usecase

import com.fitx.app.domain.model.DashboardSummary
import com.fitx.app.domain.repository.ActivityRepository
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
    private val calculateHealthMetricsUseCase: CalculateHealthMetricsUseCase
) {
    operator fun invoke(dateEpochDay: Long = DateUtils.todayEpochDay()): Flow<DashboardSummary> {
        return combine(
            userProfileRepository.observeProfile(),
            activityRepository.observeSessions(),
            plannerRepository.observeTasks(dateEpochDay),
            weightRepository.observeEntries()
        ) { profile, sessions, tasks, weights ->
            val todaySessions = sessions.filter {
                DateUtils.epochDayFromMillis(it.startTimeMillis) == dateEpochDay
            }
            val weekly = weights.take(7)
            DashboardSummary(
                healthMetrics = profile?.let { calculateHealthMetricsUseCase(it) },
                latestWeightKg = weights.firstOrNull()?.weightKg,
                weeklyWeights = weekly,
                todayTasks = tasks,
                completedTasks = tasks.count { it.isCompleted },
                todayDistanceMeters = todaySessions.sumOf { it.distanceMeters },
                todayCaloriesBurned = todaySessions.sumOf { it.caloriesBurned },
                todaySteps = todaySessions.sumOf { it.steps }
            )
        }
    }
}

