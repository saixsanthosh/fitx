package com.fitx.app.ui.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import androidx.room.Room
import com.fitx.app.MainActivity
import com.fitx.app.R
import com.fitx.app.data.local.AppDatabase
import com.fitx.app.util.DateUtils
import kotlinx.coroutines.runBlocking

class TodoWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        appWidgetIds.forEach { appWidgetId ->
            appWidgetManager.updateAppWidget(
                appWidgetId,
                createRemoteViews(context)
            )
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == ACTION_REFRESH_WIDGET) {
            refreshAll(context)
        }
    }

    companion object {
        const val ACTION_REFRESH_WIDGET = "com.fitx.app.action.REFRESH_TODO_WIDGET"

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
            val (pendingCount, topTaskText) = loadSnapshot(context)
            views.setTextViewText(R.id.widget_pending_count, "$pendingCount pending")
            views.setTextViewText(R.id.widget_top_task, topTaskText)

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

        private fun loadSnapshot(context: Context): Pair<Int, String> = runBlocking {
            val db = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "fitx.db"
            ).fallbackToDestructiveMigration().build()
            try {
                val today = DateUtils.todayEpochDay()
                val pending = db.taskDao().getPendingTaskCount(today)
                val top = db.taskDao().getTopPendingTask(today)
                val topText = top?.let { task ->
                    val time = task.timeMinutesOfDay?.let { minutes ->
                        val h = minutes / 60
                        val m = minutes % 60
                        String.format("%02d:%02d", h, m)
                    } ?: "--:--"
                    "$time  ${task.title}"
                } ?: "No pending tasks"
                pending to topText
            } finally {
                db.close()
            }
        }
    }
}
