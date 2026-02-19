package com.fitx.app.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.fitx.app.MainActivity
import com.fitx.app.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        const val TRACKING_CHANNEL_ID = "tracking_channel"
        const val REMINDER_CHANNEL_ID = "reminder_channel"
        const val TRACKING_NOTIFICATION_ID = 1001
    }

    fun createChannels() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val trackingChannel = NotificationChannel(
            TRACKING_CHANNEL_ID,
            context.getString(R.string.tracking_channel_name),
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = context.getString(R.string.tracking_channel_description)
        }

        val reminderChannel = NotificationChannel(
            REMINDER_CHANNEL_ID,
            context.getString(R.string.reminder_channel_name),
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = context.getString(R.string.reminder_channel_description)
        }

        manager.createNotificationChannel(trackingChannel)
        manager.createNotificationChannel(reminderChannel)
    }

    fun buildTrackingNotification(contentText: String): Notification {
        val launchIntent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Builder(context, TRACKING_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setContentTitle("Fitx activity in progress")
            .setContentText(contentText)
            .setOngoing(true)
            .setContentIntent(pendingIntent)
            .build()
    }

    fun showReminder(notificationId: Int, title: String, body: String) {
        val launchIntent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(context, REMINDER_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(body)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(context).notify(notificationId, notification)
    }
}

