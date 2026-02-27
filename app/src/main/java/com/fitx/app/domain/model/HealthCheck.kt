package com.fitx.app.domain.model

data class HealthCheckItem(
    val title: String,
    val status: HealthCheckStatus,
    val detail: String
)

enum class HealthCheckStatus {
    OK,
    WARN,
    ERROR
}
