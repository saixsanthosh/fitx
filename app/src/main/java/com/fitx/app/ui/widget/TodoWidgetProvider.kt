package com.fitx.app.ui.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.View
import android.widget.RemoteViews
import androidx.room.Room
import com.fitx.app.MainActivity
import com.fitx.app.R
import com.fitx.app.data.local.AppDatabase
import com.fitx.app.data.local.entity.TaskEntity
import com.fitx.app.domain.model.TaskItem
import com.fitx.app.util.DateUtils
import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.roundToInt

class TodoWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        appWidgetIds.forEach { appWidgetId ->
            appWidgetManager.updateAppWidget(appWidgetId, createRemoteViews(context))
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        when (intent.action) {
            ACTION_REFRESH_WIDGET -> refreshAll(context)
            ACTION_COMPLETE_TASK -> {
                val taskId = intent.getLongExtra(EXTRA_TASK_ID, 0L)
                if (taskId > 0L) {
                    completeTask(context, taskId)
                    refreshAll(context)
                }
            }
        }
    }

    companion object {
        const val ACTION_REFRESH_WIDGET = "com.fitx.app.action.REFRESH_TODO_WIDGET"
        const val ACTION_COMPLETE_TASK = "com.fitx.app.action.COMPLETE_TASK"
        const val EXTRA_TASK_ID = "extra_task_id"

        fun refreshAll(context: Context) {
            val manager = AppWidgetManager.getInstance(context)
            val componentName = ComponentName(context, TodoWidgetProvider::class.java)
            val ids = manager.getAppWidgetIds(componentName)
            if (ids.isEmpty()) return
            ids.forEach { appWidgetId ->
                manager.updateAppWidget(appWidgetId, createRemoteViews(context))
            }
        }

        private fun createRemoteViews(context: Context): RemoteViews {
            val views = RemoteViews(context.packageName, R.layout.todo_widget)
            val snapshot = loadSnapshot(context)

            val completedCount = (snapshot.totalCount - snapshot.pendingCount).coerceAtLeast(0)
            val progress = if (snapshot.totalCount <= 0) 0 else ((completedCount * 100f) / snapshot.totalCount).roundToInt()
            val today = LocalDate.ofEpochDay(DateUtils.todayEpochDay())
            val dateLabel = today.format(DateTimeFormatter.ofPattern("EEEE, d MMM", Locale.getDefault()))

            views.setTextViewText(R.id.widget_date_label, dateLabel)
            views.setTextViewText(R.id.widget_completion_percent, "$progress%")
            views.setTextViewText(R.id.widget_pending_count, "${snapshot.pendingCount} pending")
            views.setTextViewText(R.id.widget_done_count, "$completedCount done")
            views.setTextViewText(
                R.id.widget_state_label,
                when {
                    snapshot.pendingCount == 0 && snapshot.totalCount > 0 -> "All clear for today"
                    snapshot.pendingCount == 0 -> "Tap to add your first task"
                    snapshot.pendingCount == 1 -> "One priority left"
                    else -> "Top priorities"
                }
            )
            views.setProgressBar(R.id.widget_progress, 100, progress, false)

            bindTaskRow(context, views, 1, snapshot.tasks.getOrNull(0))
            bindTaskRow(context, views, 2, snapshot.tasks.getOrNull(1))
            bindTaskRow(context, views, 3, snapshot.tasks.getOrNull(2))
            bindTaskRow(context, views, 4, snapshot.tasks.getOrNull(3))
            views.setViewVisibility(
                R.id.widget_empty_state,
                if (snapshot.tasks.isEmpty()) View.VISIBLE else View.GONE
            )

            val openIntent = Intent(context, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(
                context,
                9001,
                openIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_root, pendingIntent)
            return views
        }

        private fun bindTaskRow(context: Context, views: RemoteViews, index: Int, task: TaskEntity?) {
            val rowId = context.resources.getIdentifier("widget_row_$index", "id", context.packageName)
            val taskId = context.resources.getIdentifier("widget_task_$index", "id", context.packageName)
            val doneId = context.resources.getIdentifier("widget_done_$index", "id", context.packageName)

            if (task == null) {
                views.setViewVisibility(rowId, View.GONE)
                return
            }

            views.setViewVisibility(rowId, View.VISIBLE)
            views.setTextViewText(taskId, formatTask(task))
            views.setTextColor(taskId, taskTextColor(task.priority))
            views.setInt(rowId, "setBackgroundResource", rowBackground(task.priority))

            val doneIntent = Intent(context, TodoWidgetProvider::class.java).apply {
                action = ACTION_COMPLETE_TASK
                putExtra(EXTRA_TASK_ID, task.taskId)
            }
            val donePendingIntent = PendingIntent.getBroadcast(
                context,
                (9100 + task.taskId).toInt(),
                doneIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(doneId, donePendingIntent)
            views.setTextViewText(doneId, "Done")
        }

        private fun formatTask(task: TaskEntity): String {
            val time = task.timeMinutesOfDay?.let { minutes ->
                val h = minutes / 60
                val m = minutes % 60
                String.format("%02d:%02d", h, m)
            } ?: "Any"
            val badge = when (task.priority) {
                TaskItem.PRIORITY_HIGH -> "!!"
                TaskItem.PRIORITY_LOW -> "--"
                else -> "->"
            }
            return "$badge  $time  ${task.title}"
        }

        private fun rowBackground(priority: Int): Int {
            return when (priority) {
                TaskItem.PRIORITY_HIGH -> R.drawable.widget_task_high
                TaskItem.PRIORITY_LOW -> R.drawable.widget_task_low
                else -> R.drawable.widget_task_medium
            }
        }

        private fun taskTextColor(priority: Int): Int {
            return when (priority) {
                TaskItem.PRIORITY_HIGH -> Color.parseColor("#FFE0EA")
                TaskItem.PRIORITY_LOW -> Color.parseColor("#D5E1F5")
                else -> Color.parseColor("#E8F0FF")
            }
        }

        private fun loadSnapshot(context: Context): WidgetSnapshot = runBlocking {
            val db = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "fitx.db"
            ).fallbackToDestructiveMigration().build()
            try {
                val today = DateUtils.todayEpochDay()
                val pending = db.taskDao().getPendingTaskCount(today)
                val total = db.taskDao().getTaskCountForDate(today)
                val tasks = db.taskDao().getTopPendingTasks(today, 4)
                WidgetSnapshot(
                    pendingCount = pending,
                    totalCount = total,
                    tasks = tasks
                )
            } finally {
                db.close()
            }
        }

        private fun completeTask(context: Context, taskId: Long) = runBlocking {
            val db = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "fitx.db"
            ).fallbackToDestructiveMigration().build()
            try {
                db.taskDao().completeTask(taskId)
            } finally {
                db.close()
            }
        }
    }
}

private data class WidgetSnapshot(
    val pendingCount: Int,
    val totalCount: Int,
    val tasks: List<TaskEntity>
)
