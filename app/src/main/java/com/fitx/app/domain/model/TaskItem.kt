package com.fitx.app.domain.model

data class TaskItem(
    val taskId: Long = 0,
    val title: String,
    val description: String,
    val dateEpochDay: Long,
    val isCompleted: Boolean,
    val repeatDaily: Boolean,
    val timeMinutesOfDay: Int? = null,
    val priority: Int = PRIORITY_MEDIUM,
    val reminderEnabled: Boolean = false
) {
    companion object {
        const val PRIORITY_LOW = 1
        const val PRIORITY_MEDIUM = 2
        const val PRIORITY_HIGH = 3
    }
}
