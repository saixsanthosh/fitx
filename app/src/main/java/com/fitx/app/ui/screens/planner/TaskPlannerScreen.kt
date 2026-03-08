package com.fitx.app.ui.screens.planner

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fitx.app.ui.components.premium.AnimatedProgressRing
import com.fitx.app.ui.theme.premium.*
import java.util.*

/**
 * Feature 9: Date-Based Task Planner
 * Complete task management with priorities, calendar, and progress tracking
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskPlannerScreen(
    theme: PremiumTheme = PremiumThemes.ElectricBlue,
    tasks: List<Task> = emptyList(),
    onToggleTask: (Task) -> Unit = {},
    onAddTask: () -> Unit = {},
    onDeleteTask: (Task) -> Unit = {},
    onBackPressed: () -> Unit = {}
) {
    val completedTasks = tasks.count { it.isCompleted }
    val progress = if (tasks.isNotEmpty()) completedTasks.toFloat() / tasks.size else 0f
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Task Planner", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBackPressed) { Icon(Icons.Default.ArrowBack, "Back") } },
                actions = {
                    IconButton(onClick = onAddTask) {
                        Icon(Icons.Default.Add, "Add", tint = theme.primaryColor)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddTask,
                icon = { Icon(Icons.Default.Add, "Add", tint = Color.White) },
                backgroundColor = theme.primaryColor
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
                item { TaskProgressCard(completedTasks, tasks.size, progress, theme) }
                item { PriorityFilterRow(theme) }
                item { Text("Today's Tasks", fontSize = 20.sp, fontWeight = FontWeight.Bold) }
                
                items(tasks.sortedBy { it.priority.ordinal }) { task ->
                    TaskCard(task, theme, onToggleTask, onDeleteTask)
                }
                
                item { Spacer(Modifier.height(80.dp)) }
            }
        }
    }
}

@Composable
private fun TaskProgressCard(completed: Int, total: Int, progress: Float, theme: PremiumTheme) {
    HeroCard(Modifier.fillMaxWidth().height(180.dp), listOf(theme.gradientStart, theme.gradientEnd)) {
        Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Tasks Progress", fontSize = 16.sp, color = Color.White.copy(0.9f))
                Text("$completed / $total", fontSize = 48.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text("Tasks Completed", fontSize = 14.sp, color = Color.White.copy(0.8f))
            }
            AnimatedProgressRing(
                progress, 120.dp, 14.dp,
                listOf(Color.White, Color.White.copy(0.7f)), Color.White.copy(0.2f), false
            ) {
                Text("${(progress * 100).toInt()}%", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}

@Composable
private fun PriorityFilterRow(theme: PremiumTheme) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        TaskPriority.values().forEach { priority ->
            FilterChip(
                selected = false,
                onClick = { },
                label = { Text(priority.displayName) },
                leadingIcon = {
                    Box(Modifier.size(8.dp).clip(CircleShape).background(priority.color))
                }
            )
        }
    }
}

@Composable
private fun TaskCard(
    task: Task,
    theme: PremiumTheme,
    onToggle: (Task) -> Unit,
    onDelete: (Task) -> Unit
) {
    GlassCard(
        Modifier.fillMaxWidth().clickable { onToggle(task) }
            .then(if (task.isCompleted) Modifier.background(theme.primaryColor.copy(0.05f)) else Modifier)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = task.isCompleted,
                    onCheckedChange = { onToggle(task) },
                    colors = CheckboxDefaults.colors(checkedColor = theme.primaryColor)
                )
                Column {
                    Text(
                        task.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null,
                        color = if (task.isCompleted) MaterialTheme.colorScheme.onSurface.copy(0.5f) else MaterialTheme.colorScheme.onSurface
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(8.dp).clip(CircleShape).background(task.priority.color))
                        Text(task.priority.displayName, fontSize = 11.sp, color = task.priority.color)
                        if (task.isRepeating) {
                            Icon(Icons.Default.Repeat, null, Modifier.size(12.dp), MaterialTheme.colorScheme.onSurface.copy(0.5f))
                        }
                    }
                }
            }
            IconButton(onClick = { onDelete(task) }, Modifier.size(36.dp)) {
                Icon(Icons.Default.Delete, "Delete", Modifier.size(20.dp), Color(0xFFEF4444))
            }
        }
    }
}

// Data Classes
data class Task(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String = "",
    val priority: TaskPriority = TaskPriority.MEDIUM,
    val isCompleted: Boolean = false,
    val isRepeating: Boolean = false,
    val dueDate: Date = Date(),
    val category: String = "General"
)

enum class TaskPriority(val displayName: String, val color: Color) {
    HIGH("High", Color(0xFFEF4444)),
    MEDIUM("Medium", Color(0xFFFBBF24)),
    LOW("Low", Color(0xFF10B981))
}

