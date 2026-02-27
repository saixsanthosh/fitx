package com.fitx.app.ui.screens

import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fitx.app.domain.model.TaskItem
import com.fitx.app.ui.viewmodel.PlannerViewModel
import com.fitx.app.util.DateUtils
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun PlannerRoute(
    viewModel: PlannerViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val date by viewModel.selectedDate.collectAsStateWithLifecycle()
    val tasks by viewModel.tasks.collectAsStateWithLifecycle()
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val view = LocalView.current
    val pendingCount = tasks.count { !it.isCompleted }
    val completedCount = tasks.size - pendingCount
    val highPriorityCount = tasks.count {
        !it.isCompleted && it.priority == TaskItem.PRIORITY_HIGH
    }

    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var repeatDaily by remember { mutableStateOf(false) }
    var reminderEnabled by remember { mutableStateOf(false) }
    var priority by remember { mutableStateOf(TaskItem.PRIORITY_MEDIUM) }
    var timeText by remember { mutableStateOf("") }
    var editingTaskId by remember { mutableStateOf<Long?>(null) }
    var editingCompleted by remember { mutableStateOf(false) }

    val selectedDate = LocalDate.ofEpochDay(date)
    var visibleMonth by remember(date) { mutableStateOf(YearMonth.from(selectedDate)) }

    fun resetForm() {
        title = ""
        desc = ""
        repeatDaily = false
        reminderEnabled = false
        priority = TaskItem.PRIORITY_MEDIUM
        timeText = ""
        editingTaskId = null
        editingCompleted = false
    }

    FitxScreenScaffold(topBar = { ScreenTopBar("Daily Variable List", onBack) }) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "Type 2: Daily Variable List",
                            fontWeight = FontWeight.Bold
                        )
                        MonthPicker(
                            month = visibleMonth,
                            selectedDate = selectedDate,
                            onMonthChange = { visibleMonth = it },
                            onDateSelected = { picked -> viewModel.setDate(picked.toEpochDay()) }
                        )
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(onClick = { viewModel.moveDate(-1) }) { Text("Prev") }
                    Text(DateUtils.formatEpochDay(date), fontWeight = FontWeight.Bold)
                    Button(onClick = { viewModel.moveDate(1) }) { Text("Next") }
                }
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AssistChip(
                        onClick = {},
                        label = { Text("Pending $pendingCount") }
                    )
                    AssistChip(
                        onClick = {},
                        label = { Text("Done $completedCount") }
                    )
                    AssistChip(
                        onClick = {},
                        label = { Text("High $highPriorityCount") }
                    )
                }
            }

            item {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Task title") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                OutlinedTextField(
                    value = timeText,
                    onValueChange = { timeText = it },
                    label = { Text("Time (HH:mm, optional)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                Text("Priority", fontWeight = FontWeight.SemiBold)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = priority == TaskItem.PRIORITY_LOW,
                        onClick = { priority = TaskItem.PRIORITY_LOW },
                        label = { Text("Low") }
                    )
                    FilterChip(
                        selected = priority == TaskItem.PRIORITY_MEDIUM,
                        onClick = { priority = TaskItem.PRIORITY_MEDIUM },
                        label = { Text("Medium") }
                    )
                    FilterChip(
                        selected = priority == TaskItem.PRIORITY_HIGH,
                        onClick = { priority = TaskItem.PRIORITY_HIGH },
                        label = { Text("High") }
                    )
                }
            }
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = repeatDaily, onCheckedChange = { repeatDaily = it })
                    Text("Repeat daily")
                }
            }
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = reminderEnabled, onCheckedChange = { reminderEnabled = it })
                    Text("Reminder")
                }
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = {
                        val minutes = parseTimeMinutesOrNull(timeText)
                        if (editingTaskId == null) {
                            viewModel.addTask(
                                title = title,
                                description = desc,
                                repeatDaily = repeatDaily,
                                timeMinutesOfDay = minutes,
                                priority = priority,
                                reminderEnabled = reminderEnabled
                            )
                        } else {
                            viewModel.updateTask(
                                TaskItem(
                                    taskId = editingTaskId!!,
                                    title = title,
                                    description = desc,
                                    dateEpochDay = date,
                                    isCompleted = editingCompleted,
                                    repeatDaily = repeatDaily,
                                    timeMinutesOfDay = minutes,
                                    priority = priority,
                                    reminderEnabled = reminderEnabled
                                )
                            )
                        }
                        resetForm()
                    }) {
                        Text(if (editingTaskId == null) "Add Task" else "Update Task")
                    }
                    if (editingTaskId != null) {
                        Button(onClick = { resetForm() }) {
                            Text("Cancel")
                        }
                    }
                }
            }

            items(tasks, key = { it.taskId }) { task ->
                PlannerTaskRow(
                    task = task,
                    onToggle = { checked ->
                        if (checked && settings.hapticsEnabled) {
                            view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                        }
                        viewModel.toggleTask(task, checked)
                    },
                    onEdit = {
                        editingTaskId = task.taskId
                        editingCompleted = task.isCompleted
                        title = task.title
                        desc = task.description
                        repeatDaily = task.repeatDaily
                        reminderEnabled = task.reminderEnabled
                        priority = task.priority
                        timeText = formatMinutes(task.timeMinutesOfDay)
                    },
                    onDelete = { viewModel.deleteTask(task.taskId) }
                )
            }
        }
    }
}

@Composable
private fun MonthPicker(
    month: YearMonth,
    selectedDate: LocalDate,
    onMonthChange: (YearMonth) -> Unit,
    onDateSelected: (LocalDate) -> Unit
) {
    val firstDay = month.atDay(1)
    val startOffset = (firstDay.dayOfWeek.value % 7)
    val days = month.lengthOfMonth()

    val cells = buildList<LocalDate?> {
        repeat(startOffset) { add(null) }
        for (day in 1..days) add(month.atDay(day))
        while (size % 7 != 0) add(null)
    }

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = { onMonthChange(month.minusMonths(1)) }) { Text("<") }
            Text("${month.month.name.lowercase().replaceFirstChar { it.uppercase() }} ${month.year}")
            Button(onClick = { onMonthChange(month.plusMonths(1)) }) { Text(">") }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat").forEach { dayLabel ->
                Text(dayLabel, modifier = Modifier.weight(1f), fontWeight = FontWeight.SemiBold)
            }
        }

        cells.chunked(7).forEach { week ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                week.forEach { day ->
                    if (day == null) {
                        Text("", modifier = Modifier.weight(1f))
                    } else {
                        val isSelected = day == selectedDate
                        FilterChip(
                            selected = isSelected,
                            onClick = { onDateSelected(day) },
                            label = { Text(day.dayOfMonth.toString()) },
                            modifier = Modifier
                                .weight(1f)
                                .padding(1.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PlannerTaskRow(
    task: TaskItem,
    onToggle: (Boolean) -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val rowAlpha = animateFloatAsState(
        targetValue = if (task.isCompleted) 0.55f else 1f,
        animationSpec = tween(durationMillis = 240),
        label = "planner_task_alpha"
    )
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(rowAlpha.value),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Checkbox(checked = task.isCompleted, onCheckedChange = onToggle)
                Column {
                    Text(task.title, fontWeight = FontWeight.Bold)
                    val time = formatMinutes(task.timeMinutesOfDay)
                    val priorityLabel = when (task.priority) {
                        TaskItem.PRIORITY_HIGH -> "High"
                        TaskItem.PRIORITY_LOW -> "Low"
                        else -> "Medium"
                    }
                    Text("$time  $priorityLabel${if (task.reminderEnabled) "  Reminder" else ""}")
                    if (task.description.isNotBlank()) {
                        Text(task.description)
                    }
                }
            }
            Row {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit")
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                }
            }
        }
    }
}

private fun parseTimeMinutesOrNull(value: String): Int? {
    val text = value.trim()
    if (text.isBlank()) return null
    val parts = text.split(":")
    if (parts.size != 2) return null
    val hour = parts[0].toIntOrNull() ?: return null
    val minute = parts[1].toIntOrNull() ?: return null
    if (hour !in 0..23 || minute !in 0..59) return null
    return hour * 60 + minute
}

private fun formatMinutes(minutes: Int?): String {
    if (minutes == null) return "--:--"
    val h = minutes / 60
    val m = minutes % 60
    return String.format("%02d:%02d", h, m)
}
