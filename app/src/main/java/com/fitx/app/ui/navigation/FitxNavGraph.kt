package com.fitx.app.ui.navigation

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.fitx.app.ui.screens.ActivityFinishRoute
import com.fitx.app.ui.screens.ActivityHistoryRoute
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

private data class BottomItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)

@Composable
fun FitxNavGraph(
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val bottomItems = remember {
        listOf(
            BottomItem(Screen.Dashboard.route, "Home", Icons.Default.Home),
            BottomItem(Screen.ActivityStart.route, "Activity", Icons.AutoMirrored.Filled.DirectionsRun),
            BottomItem(Screen.Nutrition.route, "Nutrition", Icons.Default.Restaurant),
            BottomItem(Screen.Planner.route, "Tasks", Icons.Default.TaskAlt),
            BottomItem(Screen.Settings.route, "Settings", Icons.Default.Settings)
        )
    }
    val bottomRoutes = remember(bottomItems) { bottomItems.map { it.route }.toSet() }
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (currentRoute in bottomRoutes) {
                NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                    bottomItems.forEach { item ->
                        val selected = currentRoute == item.route
                        val scale by animateFloatAsState(
                            targetValue = if (selected) 1.1f else 1f,
                            animationSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing),
                            label = "bottom_nav_scale_${item.route}"
                        )
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(Screen.Dashboard.route) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.label,
                                    modifier = Modifier.graphicsLayer {
                                        scaleX = scale
                                        scaleY = scale
                                    }
                                )
                            },
                            label = { Text(item.label) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier.padding(innerPadding),
            enterTransition = {
                androidx.compose.animation.fadeIn(
                    animationSpec = tween(durationMillis = 240, easing = FastOutSlowInEasing)
                ) + androidx.compose.animation.slideInVertically(
                    initialOffsetY = { it / 12 },
                    animationSpec = tween(durationMillis = 260, easing = FastOutSlowInEasing)
                )
            },
            exitTransition = {
                androidx.compose.animation.fadeOut(
                    animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing)
                )
            },
            popEnterTransition = {
                androidx.compose.animation.fadeIn(
                    animationSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing)
                )
            },
            popExitTransition = {
                androidx.compose.animation.fadeOut(
                    animationSpec = tween(durationMillis = 180, easing = FastOutSlowInEasing)
                )
            }
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
            ) { backStackEntryForDetail ->
                val sessionId = backStackEntryForDetail.arguments?.getLong("sessionId") ?: 0L
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
}
