package com.fitx.app.ui.screens.weight

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fitx.app.ui.components.premium.AnimatedProgressRing
import com.fitx.app.ui.theme.premium.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Feature 6: Weight Tracker
 * Complete weight tracking with charts, trends, and goal progress
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeightTrackerScreen(
    theme: PremiumTheme = PremiumThemes.ElectricBlue,
    weightEntries: List<WeightEntry> = emptyList(),
    goalWeight: Float = 70f,
    onAddWeight: (Float, Date) -> Unit = { _, _ -> },
    onEditWeight: (WeightEntry) -> Unit = {},
    onDeleteWeight: (WeightEntry) -> Unit = {},
    onBackPressed: () -> Unit = {}
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf<WeightEntry?>(null) }
    var showDeleteDialog by remember { mutableStateOf<WeightEntry?>(null) }
    var showCelebration by remember { mutableStateOf(false) }
    
    val currentWeight = weightEntries.firstOrNull()?.weight ?: 0f
    val startWeight = weightEntries.lastOrNull()?.weight ?: currentWeight
    val weightChange = currentWeight - startWeight
    val progressToGoal = if (startWeight > goalWeight) {
        ((startWeight - currentWeight) / (startWeight - goalWeight)).coerceIn(0f, 1f)
    } else {
        ((currentWeight - startWeight) / (goalWeight - startWeight)).coerceIn(0f, 1f)
    }
    
    val weeklyAverage = remember(weightEntries) {
        calculateWeeklyAverage(weightEntries)
    }
    
    // Check for goal achievement
    LaunchedEffect(currentWeight, goalWeight) {
        if (currentWeight > 0 && kotlin.math.abs(currentWeight - goalWeight) < 0.5f) {
            showCelebration = true
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Weight Tracker",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Weight",
                            tint = theme.primaryColor
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add",
                        tint = Color.White
                    )
                },
                backgroundColor = theme.primaryColor
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            FloatingParticles(
                modifier = Modifier.fillMaxSize(),
                color = theme.primaryColor.copy(alpha = 0.1f)
            )
            
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item { Spacer(modifier = Modifier.height(8.dp)) }
                
                // Current Weight Hero Card
                item {
                    CurrentWeightCard(
                        currentWeight = currentWeight,
                        goalWeight = goalWeight,
                        progress = progressToGoal,
                        theme = theme
                    )
                }
                
                // Weight Change Summary
                item {
                    WeightChangeSummaryRow(
                        weightChange = weightChange,
                        weeklyAverage = weeklyAverage,
                        totalEntries = weightEntries.size,
                        theme = theme
                    )
                }
                
                // Weight Trend Chart
                if (weightEntries.isNotEmpty()) {
                    item {
                        WeightTrendChart(
                            entries = weightEntries,
                            goalWeight = goalWeight,
                            theme = theme
                        )
                    }
                }
                
                // Weekly Average Card
                if (weeklyAverage > 0) {
                    item {
                        WeeklyAverageCard(
                            average = weeklyAverage,
                            theme = theme
                        )
                    }
                }
                
                // Weight History Header
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Weight History",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "${weightEntries.size} entries",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                        )
                    }
                }
                
                // Weight Entries List
                if (weightEntries.isEmpty()) {
                    item {
                        EmptyStateCard(theme = theme)
                    }
                } else {
                    items(weightEntries) { entry ->
                        WeightEntryCard(
                            entry = entry,
                            previousEntry = weightEntries.getOrNull(weightEntries.indexOf(entry) + 1),
                            theme = theme,
                            onEdit = { showEditDialog = entry },
                            onDelete = { showDeleteDialog = entry }
                        )
                    }
                }
                
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
            
            // Celebration Effect
            if (showCelebration) {
                ParticleExplosion(
                    modifier = Modifier.fillMaxSize(),
                    trigger = showCelebration,
                    colors = listOf(
                        theme.primaryColor,
                        theme.secondaryColor,
                        theme.accentColor
                    ),
                    onComplete = { showCelebration = false }
                )
            }
        }
    }
    
    // Add Weight Dialog
    if (showAddDialog) {
        AddWeightDialog(
            theme = theme,
            onDismiss = { showAddDialog = false },
            onConfirm = { weight, date ->
                onAddWeight(weight, date)
                showAddDialog = false
            }
        )
    }
    
    // Edit Weight Dialog
    showEditDialog?.let { entry ->
        EditWeightDialog(
            entry = entry,
            theme = theme,
            onDismiss = { showEditDialog = null },
            onConfirm = { updatedEntry ->
                onEditWeight(updatedEntry)
                showEditDialog = null
            }
        )
    }
    
    // Delete Confirmation Dialog
    showDeleteDialog?.let { entry ->
        DeleteConfirmationDialog(
            entry = entry,
            theme = theme,
            onDismiss = { showDeleteDialog = null },
            onConfirm = {
                onDeleteWeight(entry)
                showDeleteDialog = null
            }
        )
    }
}

@Composable
private fun CurrentWeightCard(
    currentWeight: Float,
    goalWeight: Float,
    progress: Float,
    theme: PremiumTheme
) {
    HeroCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp),
        backgroundGradient = listOf(theme.gradientStart, theme.gradientEnd)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Current Weight",
                    fontSize = 16.sp,
                    color = Color.White.copy(alpha = 0.9f)
                )
                
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = String.format("%.1f", currentWeight),
                        fontSize = 56.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "kg",
                        fontSize = 24.sp,
                        color = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Flag,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.9f),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Goal: ${String.format("%.1f", goalWeight)} kg",
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                    
                    val remaining = kotlin.math.abs(currentWeight - goalWeight)
                    Text(
                        text = "${String.format("%.1f", remaining)} kg to go",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }
            
            AnimatedProgressRing(
                progress = progress,
                size = 140.dp,
                strokeWidth = 16.dp,
                gradientColors = listOf(Color.White, Color.White.copy(alpha = 0.7f)),
                backgroundColor = Color.White.copy(alpha = 0.2f),
                showPercentage = false
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${(progress * 100).toInt()}%",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Progress",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }
        }
    }
}

@Composable
private fun WeightChangeSummaryRow(
    weightChange: Float,
    weeklyAverage: Float,
    totalEntries: Int,
    theme: PremiumTheme
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        WeightStatCard(
            value = String.format("%.1f", kotlin.math.abs(weightChange)),
            unit = "kg",
            label = if (weightChange < 0) "Lost" else "Gained",
            icon = if (weightChange < 0) Icons.Default.TrendingDown else Icons.Default.TrendingUp,
            color = if (weightChange < 0) theme.primaryColor else theme.secondaryColor,
            modifier = Modifier.weight(1f)
        )
        
        WeightStatCard(
            value = String.format("%.1f", weeklyAverage),
            unit = "kg",
            label = "Weekly Avg",
            icon = Icons.Default.CalendarMonth,
            color = theme.accentColor,
            modifier = Modifier.weight(1f)
        )
        
        WeightStatCard(
            value = totalEntries.toString(),
            unit = "",
            label = "Entries",
            icon = Icons.Default.DataUsage,
            color = theme.secondaryColor,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun WeightStatCard(
    value: String,
    unit: String,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    GlassCard(
        modifier = modifier.height(100.dp),
        cornerRadius = 20.dp
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = value,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (unit.isNotEmpty()) {
                    Text(
                        text = " $unit",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }
            Text(
                text = label,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun WeightTrendChart(
    entries: List<WeightEntry>,
    goalWeight: Float,
    theme: PremiumTheme
) {
    GlowingCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp),
        glowColor = theme.primaryColor
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Weight Trend",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ChartLegendItem(
                        color = theme.primaryColor,
                        label = "Weight"
                    )
                    ChartLegendItem(
                        color = theme.accentColor,
                        label = "Goal"
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Simple line chart visualization
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                WeightLineChart(
                    entries = entries.takeLast(30),
                    goalWeight = goalWeight,
                    theme = theme
                )
            }
        }
    }
}

@Composable
private fun WeightLineChart(
    entries: List<WeightEntry>,
    goalWeight: Float,
    theme: PremiumTheme
) {
    // Simplified chart - in production use MPAndroidChart or Compose Charts
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.ShowChart,
            contentDescription = null,
            tint = theme.primaryColor,
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Chart visualization",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        Text(
            text = "Use MPAndroidChart for production",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )
    }
}

@Composable
private fun ChartLegendItem(
    color: Color,
    label: String
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun WeeklyAverageCard(
    average: Float,
    theme: PremiumTheme
) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(theme.primaryColor, theme.secondaryColor)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Weekly Average",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = String.format("%.1f", average),
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = " kg",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun WeightEntryCard(
    entry: WeightEntry,
    previousEntry: WeightEntry?,
    theme: PremiumTheme,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val change = previousEntry?.let { entry.weight - it.weight }
    
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(theme.primaryColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.MonitorWeight,
                        contentDescription = null,
                        tint = theme.primaryColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
                
                Column {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = String.format("%.1f", entry.weight),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = " kg",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            modifier = Modifier.padding(bottom = 2.dp)
                        )
                    }
                    
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = formatDate(entry.date),
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        
                        change?.let {
                            if (it != 0f) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = if (it < 0) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                                        contentDescription = null,
                                        tint = if (it < 0) theme.primaryColor else theme.secondaryColor,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Text(
                                        text = String.format("%.1f kg", kotlin.math.abs(it)),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = if (it < 0) theme.primaryColor else theme.secondaryColor
                                    )
                                }
                            }
                        }
                    }
                }
            }
            
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                IconButton(
                    onClick = onEdit,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = theme.primaryColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
                
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color(0xFFEF4444),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyStateCard(theme: PremiumTheme) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.MonitorWeight,
                contentDescription = null,
                tint = theme.primaryColor.copy(alpha = 0.5f),
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No weight entries yet",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Tap + to add your first entry",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun AddWeightDialog(
    theme: PremiumTheme,
    onDismiss: () -> Unit,
    onConfirm: (Float, Date) -> Unit
) {
    var weight by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf(Date()) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Add Weight Entry",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = weight,
                    onValueChange = { weight = it },
                    label = { Text("Weight (kg)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = theme.primaryColor,
                        cursorColor = theme.primaryColor
                    )
                )
                
                Text(
                    text = "Date: ${formatDate(selectedDate)}",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        },
        confirmButton = {
            PremiumButton(
                text = "Add",
                onClick = {
                    weight.toFloatOrNull()?.let {
                        onConfirm(it, selectedDate)
                    }
                },
                gradient = listOf(theme.primaryColor, theme.secondaryColor),
                enabled = weight.toFloatOrNull() != null
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun EditWeightDialog(
    entry: WeightEntry,
    theme: PremiumTheme,
    onDismiss: () -> Unit,
    onConfirm: (WeightEntry) -> Unit
) {
    var weight by remember { mutableStateOf(entry.weight.toString()) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Edit Weight Entry",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            OutlinedTextField(
                value = weight,
                onValueChange = { weight = it },
                label = { Text("Weight (kg)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = theme.primaryColor,
                    cursorColor = theme.primaryColor
                )
            )
        },
        confirmButton = {
            PremiumButton(
                text = "Save",
                onClick = {
                    weight.toFloatOrNull()?.let {
                        onConfirm(entry.copy(weight = it))
                    }
                },
                gradient = listOf(theme.primaryColor, theme.secondaryColor),
                enabled = weight.toFloatOrNull() != null
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun DeleteConfirmationDialog(
    entry: WeightEntry,
    theme: PremiumTheme,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Delete Entry",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text("Are you sure you want to delete this weight entry?")
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFEF4444)
                )
            ) {
                Text("Delete")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

// Data Classes
data class WeightEntry(
    val id: String = UUID.randomUUID().toString(),
    val weight: Float,
    val date: Date,
    val note: String = ""
)

// Helper Functions
private fun calculateWeeklyAverage(entries: List<WeightEntry>): Float {
    if (entries.isEmpty()) return 0f
    
    val oneWeekAgo = Calendar.getInstance().apply {
        add(Calendar.DAY_OF_YEAR, -7)
    }.time
    
    val recentEntries = entries.filter { it.date.after(oneWeekAgo) }
    return if (recentEntries.isNotEmpty()) {
        recentEntries.map { it.weight }.average().toFloat()
    } else {
        0f
    }
}

private fun formatDate(date: Date): String {
    return SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(date)
}
