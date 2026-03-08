package com.fitx.app.ui.screens.activity

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fitx.app.ui.components.premium.AnimatedProgressRing
import com.fitx.app.ui.theme.premium.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Feature 5: Activity History - UNBEATABLE DESIGN
 * Complete activity history with stunning visuals, filters, and detailed views
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityHistoryScreen(
    theme: PremiumTheme = PremiumThemes.ElectricBlue,
    activities: List<ActivityRecord> = emptyList(),
    onActivityClick: (ActivityRecord) -> Unit = {},
    onDeleteActivity: (ActivityRecord) -> Unit = {},
    onShareActivity: (ActivityRecord) -> Unit = {},
    onBackPressed: () -> Unit = {}
) {
    var selectedFilter by remember { mutableStateOf(ActivityFilter.ALL) }
    var selectedSort by remember { mutableStateOf(SortOption.DATE_DESC) }
    var showFilterSheet by remember { mutableStateOf(false) }
    
    val filteredActivities = remember(activities, selectedFilter, selectedSort) {
        activities
            .filter { activity ->
                when (selectedFilter) {
                    ActivityFilter.ALL -> true
                    ActivityFilter.WALKING -> activity.type == ActivityType.WALKING
                    ActivityFilter.RUNNING -> activity.type == ActivityType.RUNNING
                    ActivityFilter.CYCLING -> activity.type == ActivityType.CYCLING
                    ActivityFilter.WORKOUT -> activity.type == ActivityType.WORKOUT
                }
            }
            .sortedWith(
                when (selectedSort) {
                    SortOption.DATE_DESC -> compareByDescending { it.date }
                    SortOption.DATE_ASC -> compareBy { it.date }
                    SortOption.DISTANCE_DESC -> compareByDescending { it.distance }
                    SortOption.DURATION_DESC -> compareByDescending { it.duration }
                    SortOption.CALORIES_DESC -> compareByDescending { it.calories }
                }
            )
    }
    
    val totalStats = remember(activities) {
        calculateTotalStats(activities)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Activity History", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showFilterSheet = true }) {
                        Icon(Icons.Default.FilterList, "Filter", tint = theme.primaryColor)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Box(Modifier.fillMaxSize()) {
            FloatingParticles(Modifier.fillMaxSize(), theme.primaryColor.copy(alpha = 0.1f))
            
            LazyColumn(
                Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item { Spacer(Modifier.height(8.dp)) }
                
                // Total Stats Hero Card
                item {
                    TotalStatsHeroCard(totalStats, theme)
                }
                
                // Stats Summary Row
                item {
                    StatsBreakdownRow(totalStats, theme)
                }
                
                // Filter Chips
                item {
                    FilterChipsRow(selectedFilter) { selectedFilter = it }
                }
                
                // Activities Header
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "${filteredActivities.size} Activities",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        TextButton(onClick = { showFilterSheet = true }) {
                            Icon(Icons.Default.Sort, null, Modifier.size(18.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Sort")
                        }
                    }
                }
                
                // Activity List
                if (filteredActivities.isEmpty()) {
                    item {
                        EmptyActivityState(theme)
                    }
                } else {
                    items(filteredActivities) { activity ->
                        ActivityHistoryCard(
                            activity, theme,
                            onClick = { onActivityClick(activity) },
                            onShare = { onShareActivity(activity) },
                            onDelete = { onDeleteActivity(activity) }
                        )
                    }
                }
                
                item { Spacer(Modifier.height(80.dp)) }
            }
        }
    }
    
    // Filter Bottom Sheet
    if (showFilterSheet) {
        ModalBottomSheet(
            onDismissRequest = { showFilterSheet = false }
        ) {
            FilterBottomSheet(
                selectedSort = selectedSort,
                onSortSelected = { selectedSort = it; showFilterSheet = false },
                theme = theme
            )
        }
    }
}

@Composable
private fun TotalStatsHeroCard(stats: TotalActivityStats, theme: PremiumTheme) {
    HeroCard(
        Modifier.fillMaxWidth().height(200.dp),
        listOf(theme.gradientStart, theme.gradientEnd)
    ) {
        Column(Modifier.fillMaxSize(), Arrangement.SpaceBetween) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Total Activities", fontSize = 16.sp, color = Color.White.copy(0.9f))
                    Text("${stats.totalActivities}", fontSize = 48.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
                Box(
                    Modifier.size(64.dp).clip(CircleShape).background(Color.White.copy(0.2f)),
                    Alignment.Center
                ) {
                    Icon(Icons.Default.DirectionsRun, null, Modifier.size(36.dp), Color.White)
                }
            }
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                MiniStat("${String.format("%.1f", stats.totalDistance)} km", Icons.Default.Route)
                MiniStat("${stats.totalDuration} min", Icons.Default.Timer)
                MiniStat("${stats.totalCalories} kcal", Icons.Default.LocalFireDepartment)
            }
        }
    }
}

@Composable
private fun MiniStat(text: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Row(
        Modifier.clip(RoundedCornerShape(20.dp)).background(Color.White.copy(0.2f))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        Arrangement.spacedBy(6.dp), Alignment.CenterVertically
    ) {
        Icon(icon, null, Modifier.size(16.dp), Color.White)
        Text(text, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color.White)
    }
}

@Composable
private fun StatsBreakdownRow(stats: TotalActivityStats, theme: PremiumTheme) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        StatBreakdownCard("Walking", stats.walkingCount, theme.primaryColor, Modifier.weight(1f))
        StatBreakdownCard("Running", stats.runningCount, theme.secondaryColor, Modifier.weight(1f))
        StatBreakdownCard("Cycling", stats.cyclingCount, theme.accentColor, Modifier.weight(1f))
    }
}

@Composable
private fun StatBreakdownCard(label: String, count: Int, color: Color, modifier: Modifier) {
    GlassCard(modifier.height(90.dp), 20.dp) {
        Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Text(count.toString(), fontSize = 28.sp, fontWeight = FontWeight.Bold, color = color)
            Text(label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(0.7f))
        }
    }
}

@Composable
private fun FilterChipsRow(selected: ActivityFilter, onSelect: (ActivityFilter) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        ActivityFilter.values().forEach { filter ->
            FilterChip(
                selected = selected == filter,
                onClick = { onSelect(filter) },
                label = { Text(filter.displayName) },
                leadingIcon = if (selected == filter) {
                    { Icon(Icons.Default.Check, null, Modifier.size(18.dp)) }
                } else null
            )
        }
    }
}

@Composable
private fun ActivityHistoryCard(
    activity: ActivityRecord,
    theme: PremiumTheme,
    onClick: () -> Unit,
    onShare: () -> Unit,
    onDelete: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    
    GlowingCard(
        Modifier.fillMaxWidth().clickable(onClick = onClick),
        theme.primaryColor.copy(alpha = 0.3f)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            // Header
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier.size(56.dp).clip(CircleShape).background(
                            Brush.linearGradient(
                                listOf(activity.type.color, activity.type.color.copy(0.7f))
                            )
                        ), Alignment.Center
                    ) {
                        Icon(activity.type.icon, null, Modifier.size(28.dp), Color.White)
                    }
                    Column {
                        Text(activity.type.displayName, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text(formatDate(activity.date), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(0.6f))
                    }
                }
                
                Box {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.MoreVert, "Menu")
                    }
                    DropdownMenu(showMenu, { showMenu = false }) {
                        DropdownMenuItem(
                            text = { Text("Share") },
                            onClick = { onShare(); showMenu = false },
                            leadingIcon = { Icon(Icons.Default.Share, null) }
                        )
                        DropdownMenuItem(
                            text = { Text("Delete") },
                            onClick = { onDelete(); showMenu = false },
                            leadingIcon = { Icon(Icons.Default.Delete, null, tint = Color(0xFFEF4444)) }
                        )
                    }
                }
            }
            
            // Stats Grid
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                ActivityStat(Icons.Default.Route, "${String.format("%.2f", activity.distance)} km", Modifier.weight(1f))
                ActivityStat(Icons.Default.Timer, "${activity.duration} min", Modifier.weight(1f))
                ActivityStat(Icons.Default.LocalFireDepartment, "${activity.calories} kcal", Modifier.weight(1f))
            }
            
            // Additional Stats
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                ActivityStat(Icons.Default.Speed, "${String.format("%.1f", activity.avgSpeed)} km/h", Modifier.weight(1f))
                ActivityStat(Icons.Default.DirectionsWalk, "${activity.steps} steps", Modifier.weight(1f))
                ActivityStat(Icons.Default.Favorite, "${activity.avgHeartRate} bpm", Modifier.weight(1f))
            }
            
            // Progress Bar
            LinearProgressIndicator(
                progress = (activity.distance / 10f).coerceIn(0f, 1f),
                Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                color = activity.type.color,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }
    }
}

@Composable
private fun ActivityStat(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String, modifier: Modifier) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, Modifier.size(16.dp), MaterialTheme.colorScheme.onSurface.copy(0.7f))
        Text(text, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(0.7f))
    }
}

@Composable
private fun EmptyActivityState(theme: PremiumTheme) {
    GlassCard(Modifier.fillMaxWidth().height(200.dp)) {
        Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Icon(Icons.Default.DirectionsRun, null, Modifier.size(64.dp), theme.primaryColor.copy(0.5f))
            Spacer(Modifier.height(16.dp))
            Text("No activities yet", fontSize = 18.sp, fontWeight = FontWeight.Medium)
            Text("Start tracking to see your history", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface.copy(0.7f))
        }
    }
}

@Composable
private fun FilterBottomSheet(
    selectedSort: SortOption,
    onSortSelected: (SortOption) -> Unit,
    theme: PremiumTheme
) {
    Column(Modifier.fillMaxWidth().padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Sort By", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        
        SortOption.values().forEach { option ->
            Row(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
                    .clickable { onSortSelected(option) }
                    .background(if (selectedSort == option) theme.primaryColor.copy(0.1f) else Color.Transparent)
                    .padding(16.dp),
                Arrangement.SpaceBetween, Alignment.CenterVertically
            ) {
                Text(option.displayName, fontSize = 16.sp, fontWeight = if (selectedSort == option) FontWeight.Bold else FontWeight.Normal)
                if (selectedSort == option) {
                    Icon(Icons.Default.Check, null, Modifier.size(24.dp), theme.primaryColor)
                }
            }
        }
        
        Spacer(Modifier.height(16.dp))
    }
}

// Data Classes
data class ActivityRecord(
    val id: String = UUID.randomUUID().toString(),
    val type: ActivityType,
    val date: Date,
    val distance: Float,
    val duration: Int,
    val calories: Int,
    val avgSpeed: Float,
    val steps: Int,
    val avgHeartRate: Int,
    val route: List<LatLng> = emptyList()
)

data class LatLng(val latitude: Double, val longitude: Double)

enum class ActivityType(
    val displayName: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val color: Color
) {
    WALKING("Walking", Icons.Default.DirectionsWalk, Color(0xFF3A86FF)),
    RUNNING("Running", Icons.Default.DirectionsRun, Color(0xFFFF006E)),
    CYCLING("Cycling", Icons.Default.DirectionsBike, Color(0xFF06FFA5)),
    WORKOUT("Workout", Icons.Default.FitnessCenter, Color(0xFF8338EC))
}

enum class ActivityFilter(val displayName: String) {
    ALL("All"),
    WALKING("Walking"),
    RUNNING("Running"),
    CYCLING("Cycling"),
    WORKOUT("Workout")
}

enum class SortOption(val displayName: String) {
    DATE_DESC("Newest First"),
    DATE_ASC("Oldest First"),
    DISTANCE_DESC("Longest Distance"),
    DURATION_DESC("Longest Duration"),
    CALORIES_DESC("Most Calories")
}

data class TotalActivityStats(
    val totalActivities: Int,
    val totalDistance: Float,
    val totalDuration: Int,
    val totalCalories: Int,
    val walkingCount: Int,
    val runningCount: Int,
    val cyclingCount: Int
)

private fun calculateTotalStats(activities: List<ActivityRecord>): TotalActivityStats {
    return TotalActivityStats(
        totalActivities = activities.size,
        totalDistance = activities.sumOf { it.distance.toDouble() }.toFloat(),
        totalDuration = activities.sumOf { it.duration },
        totalCalories = activities.sumOf { it.calories },
        walkingCount = activities.count { it.type == ActivityType.WALKING },
        runningCount = activities.count { it.type == ActivityType.RUNNING },
        cyclingCount = activities.count { it.type == ActivityType.CYCLING }
    )
}

private fun formatDate(date: Date): String {
    return SimpleDateFormat("MMM dd, yyyy â€¢ hh:mm a", Locale.getDefault()).format(date)
}

