package com.fitx.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.fitx.app.ui.screens.ActivityHistoryRoute
import com.fitx.app.ui.screens.ActivityFinishRoute
import com.fitx.app.ui.screens.ActivityStartRoute
import com.fitx.app.ui.screens.DashboardRoute
import com.fitx.app.ui.screens.HabitsRoute
import com.fitx.app.ui.screens.LiveTrackingRoute
import com.fitx.app.ui.screens.NutritionRoute
import com.fitx.app.ui.screens.PlannerRoute
import com.fitx.app.ui.screens.ProfileRoute
import com.fitx.app.ui.screens.SessionDetailRoute
import com.fitx.app.ui.screens.SettingsRoute
import com.fitx.app.ui.screens.WeightRoute
import com.fitx.app.ui.screens.WorkoutRoute
import com.fitx.app.ui.viewmodel.ActivityViewModel

@Composable
fun FitxNavGraph(
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Screen.Dashboard.route,
        modifier = modifier
    ) {
        composable(Screen.Dashboard.route) {
            DashboardRoute(
                onOpenProfile = { navController.navigate(Screen.Profile.route) },
                onOpenActivity = { navController.navigate(Screen.ActivityStart.route) },
                onOpenWeight = { navController.navigate(Screen.Weight.route) },
                onOpenWorkout = { navController.navigate(Screen.Workout.route) },
                onOpenHabits = { navController.navigate(Screen.Habits.route) },
                onOpenPlanner = { navController.navigate(Screen.Planner.route) },
                onOpenNutrition = { navController.navigate(Screen.Nutrition.route) },
                onOpenSettings = { navController.navigate(Screen.Settings.route) }
            )
        }
        composable(Screen.Profile.route) {
            ProfileRoute(onBack = { navController.popBackStack() })
        }
        composable(Screen.ActivityStart.route) {
            ActivityStartRoute(
                onOpenLive = { navController.navigate(Screen.LiveTracking.route) },
                onOpenHistory = { navController.navigate(Screen.ActivityHistory.route) },
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.LiveTracking.route) {
            LiveTrackingRoute(
                onStop = { navController.navigate(Screen.ActivityFinish.route) },
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.ActivityFinish.route) {
            ActivityFinishRoute(
                onOpenHistory = { navController.navigate(Screen.ActivityHistory.route) },
                onBackToStart = {
                    navController.navigate(Screen.ActivityStart.route) {
                        popUpTo(Screen.ActivityStart.route) { inclusive = false }
                    }
                }
            )
        }
        composable(Screen.ActivityHistory.route) {
            ActivityHistoryRoute(
                onSessionClick = { sessionId ->
                    navController.navigate(Screen.SessionDetail.createRoute(sessionId))
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            route = Screen.SessionDetail.route,
            arguments = listOf(navArgument("sessionId") { type = NavType.LongType })
        ) { backStackEntry ->
            val sessionId = backStackEntry.arguments?.getLong("sessionId") ?: 0L
            val viewModel: ActivityViewModel = hiltViewModel()
            LaunchedEffect(sessionId) {
                viewModel.loadSession(sessionId)
            }
            SessionDetailRoute(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Weight.route) {
            WeightRoute(onBack = { navController.popBackStack() })
        }
        composable(Screen.Workout.route) {
            WorkoutRoute(onBack = { navController.popBackStack() })
        }
        composable(Screen.Habits.route) {
            HabitsRoute(onBack = { navController.popBackStack() })
        }
        composable(Screen.Planner.route) {
            PlannerRoute(onBack = { navController.popBackStack() })
        }
        composable(Screen.Nutrition.route) {
            NutritionRoute(onBack = { navController.popBackStack() })
        }
        composable(Screen.Settings.route) {
            SettingsRoute(onBack = { navController.popBackStack() })
        }
    }
}
