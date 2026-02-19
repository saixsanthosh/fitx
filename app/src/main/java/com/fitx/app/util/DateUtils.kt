package com.fitx.app.util

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object DateUtils {
    private val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy")

    fun todayEpochDay(): Long = LocalDate.now().toEpochDay()

    fun epochDayFromMillis(millis: Long): Long {
        return Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate().toEpochDay()
    }

    fun formatEpochDay(epochDay: Long): String {
        return LocalDate.ofEpochDay(epochDay).format(dateFormatter)
    }

    fun formatDuration(seconds: Long): String {
        val hrs = seconds / 3600
        val mins = (seconds % 3600) / 60
        val secs = seconds % 60
        return if (hrs > 0) {
            String.format("%02d:%02d:%02d", hrs, mins, secs)
        } else {
            String.format("%02d:%02d", mins, secs)
        }
    }
}

