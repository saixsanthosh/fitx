package com.fitx.app.service.sync

object SyncEntityType {
    const val PROFILE = "profiles"
    const val WEIGHT = "weights"
    const val TASK = "tasks"
    const val MEAL = "meals"
    const val WORKOUT_TEMPLATE = "workout_templates"
    const val EXERCISE_LOG = "exercise_logs"
}

object SyncOperationType {
    const val UPSERT = "UPSERT"
    const val DELETE = "DELETE"
}
