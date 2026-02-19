package com.fitx.app.domain.repository

import com.fitx.app.domain.model.TaskItem
import kotlinx.coroutines.flow.Flow

interface PlannerRepository {
    fun observeTasks(dateEpochDay: Long): Flow<List<TaskItem>>
    suspend fun addTask(taskItem: TaskItem): Long
    suspend fun updateTask(taskItem: TaskItem)
    suspend fun toggleTask(taskId: Long, completed: Boolean)
    suspend fun deleteTask(taskId: Long)
}
