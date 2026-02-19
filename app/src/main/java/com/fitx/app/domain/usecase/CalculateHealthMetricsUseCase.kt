package com.fitx.app.domain.usecase

import com.fitx.app.domain.model.HealthMetrics
import com.fitx.app.domain.model.UserProfile
import com.fitx.app.util.HealthCalculator
import javax.inject.Inject

class CalculateHealthMetricsUseCase @Inject constructor() {
    operator fun invoke(profile: UserProfile): HealthMetrics {
        return HealthCalculator.calculateAll(profile)
    }
}

