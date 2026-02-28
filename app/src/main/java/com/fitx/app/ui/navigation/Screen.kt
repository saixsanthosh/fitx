package com.fitx.app.ui.navigation

import android.net.Uri

sealed class Screen(val route: String) {
    data object Dashboard : Screen("dashboard")
    data object Profile : Screen("profile")
    data object ActivityStart : Screen("activity_start")
    data object LiveTracking : Screen("live_tracking")
    data object ActivityFinish : Screen("activity_finish")
    data object ActivityHistory : Screen("activity_history")
    data object Music : Screen("music")
    data object MusicNowPlaying : Screen("music_now_playing")
    data object MusicYouTube : Screen("music_youtube/{playlistId}") {
        fun createRoute(playlistId: String): String = "music_youtube/${Uri.encode(playlistId)}"
    }
    data object SessionDetail : Screen("session_detail/{sessionId}") {
        fun createRoute(sessionId: Long): String = "session_detail/$sessionId"
    }
    data object Weight : Screen("weight")
    data object Workout : Screen("workout")
    data object Habits : Screen("habits")
    data object Planner : Screen("planner")
    data object Nutrition : Screen("nutrition")
    data object WeeklyInsights : Screen("weekly_insights")
    data object HealthCheck : Screen("health_check")
    data object Settings : Screen("settings")
}

