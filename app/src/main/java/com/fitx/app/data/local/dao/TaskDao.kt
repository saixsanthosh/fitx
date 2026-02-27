package com.fitx.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fitx.app.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query(
        "SELECT * FROM task_item " +
            "WHERE dateEpochDay = :dateEpochDay OR repeatDaily = 1 " +
            "ORDER BY isCompleted ASC, priority DESC, " +
            "CASE WHEN timeMinutesOfDay IS NULL THEN 1 ELSE 0 END, " +
            "timeMinutesOfDay ASC, taskId DESC"
    )
    fun observeTasks(dateEpochDay: Long): Flow<List<TaskEntity>>

    @Query("SELECT * FROM task_item ORDER BY taskId DESC")
    suspend fun getAllTasks(): List<TaskEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(entity: TaskEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(entities: List<TaskEntity>)

    @Query("SELECT * FROM task_item WHERE taskId = :taskId LIMIT 1")
    suspend fun getTaskById(taskId: Long): TaskEntity?

    @Query("UPDATE task_item SET isCompleted = :completed WHERE taskId = :taskId")
    suspend fun updateCompleted(taskId: Long, completed: Boolean)

    @Query("DELETE FROM task_item WHERE taskId = :taskId")
    suspend fun deleteTask(taskId: Long)

    @Query(
        "SELECT COUNT(*) FROM task_item " +
            "WHERE (dateEpochDay = :dateEpochDay OR repeatDaily = 1) AND isCompleted = 0"
    )
    suspend fun getPendingTaskCount(dateEpochDay: Long): Int

    @Query(
        "SELECT * FROM task_item " +
            "WHERE (dateEpochDay = :dateEpochDay OR repeatDaily = 1) AND isCompleted = 0 " +
            "ORDER BY priority DESC, " +
            "CASE WHEN timeMinutesOfDay IS NULL THEN 1 ELSE 0 END, " +
            "timeMinutesOfDay ASC, taskId DESC LIMIT 1"
    )
    suspend fun getTopPendingTask(dateEpochDay: Long): TaskEntity?

    @Query(
        "SELECT * FROM task_item " +
            "WHERE (dateEpochDay = :dateEpochDay OR repeatDaily = 1) AND isCompleted = 0 " +
            "ORDER BY priority DESC, " +
            "CASE WHEN timeMinutesOfDay IS NULL THEN 1 ELSE 0 END, " +
            "timeMinutesOfDay ASC, taskId DESC LIMIT :limit"
    )
    suspend fun getTopPendingTasks(dateEpochDay: Long, limit: Int): List<TaskEntity>

    @Query(
        "SELECT COUNT(*) FROM task_item " +
            "WHERE dateEpochDay = :dateEpochDay OR repeatDaily = 1"
    )
    suspend fun getTaskCountForDate(dateEpochDay: Long): Int

    @Query("UPDATE task_item SET isCompleted = 1 WHERE taskId = :taskId")
    suspend fun completeTask(taskId: Long)

    @Query("DELETE FROM task_item")
    suspend fun clearAll()
}
