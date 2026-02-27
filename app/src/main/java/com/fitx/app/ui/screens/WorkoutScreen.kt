package com.fitx.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fitx.app.domain.model.ExerciseLog
import com.fitx.app.ui.viewmodel.WorkoutViewModel
import kotlin.math.roundToInt

@Composable
fun WorkoutRoute(
    viewModel: WorkoutViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val templates by viewModel.templates.collectAsStateWithLifecycle()
    val logs by viewModel.logs.collectAsStateWithLifecycle()
    val personalRecords by viewModel.personalRecords.collectAsStateWithLifecycle()

    var templateName by remember { mutableStateOf("") }
    var templateDesc by remember { mutableStateOf("") }
    var exerciseName by remember { mutableStateOf("") }
    var sets by remember { mutableStateOf("3") }
    var reps by remember { mutableStateOf("10") }
    var weight by remember { mutableStateOf("0") }
    var notes by remember { mutableStateOf("") }

    val totalSets = logs.sumOf { it.sets }
    val totalReps = logs.sumOf { it.reps * it.sets }
    val totalVolume = logs.sumOf { it.weightKg * it.reps * it.sets }

    FitxScreenScaffold(topBar = { ScreenTopBar("Workout Console", onBack) }) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        MaterialTheme.colorScheme.secondary.copy(alpha = 0.22f),
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.16f)
                                    )
                                )
                            )
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text("My Workouts", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text("Log sets, reps and load. Track your training volume in one place.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            WorkoutStat("Sets", totalSets.toString(), Modifier.weight(1f))
                            WorkoutStat("Reps", totalReps.toString(), Modifier.weight(1f))
                            WorkoutStat("Volume", "${totalVolume.roundToInt()} kg", Modifier.weight(1f))
                        }
                    }
                }
            }

            item { SectionTitle("Personal Records") }
            if (personalRecords.isEmpty()) {
                item { EmptyWorkoutCard("No records yet. Keep logging to unlock PRs.") }
            }
            items(personalRecords, key = { it.exerciseName }) { record ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(record.exerciseName, fontWeight = FontWeight.Bold)
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Max ${"%.1f".format(record.maxWeightKg)} kg", color = MaterialTheme.colorScheme.primary)
                            Text("Volume ${record.maxVolume.roundToInt()}", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }

            item { SectionTitle("Templates") }
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = templateName,
                            onValueChange = { templateName = it },
                            label = { Text("Template name") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = templateDesc,
                            onValueChange = { templateDesc = it },
                            label = { Text("Description") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Button(onClick = {
                            viewModel.addTemplate(templateName, templateDesc)
                            templateName = ""
                            templateDesc = ""
                        }) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Text("Add Template", modifier = Modifier.padding(start = 6.dp))
                        }
                    }
                }
            }
            if (templates.isEmpty()) {
                item { EmptyWorkoutCard("No templates yet. Create one above.") }
            }
            items(templates, key = { it.templateId }) { template ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.97f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.FitnessCenter, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Column {
                            Text(template.name, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                            Text(template.description, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            item { SectionTitle("Quick Log") }
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.92f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = exerciseName,
                            onValueChange = { exerciseName = it },
                            label = { Text("Exercise name") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = sets,
                                onValueChange = { sets = it },
                                label = { Text("Sets") },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )
                            OutlinedTextField(
                                value = reps,
                                onValueChange = { reps = it },
                                label = { Text("Reps") },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )
                            OutlinedTextField(
                                value = weight,
                                onValueChange = { weight = it },
                                label = { Text("Kg") },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                            )
                        }
                        OutlinedTextField(
                            value = notes,
                            onValueChange = { notes = it },
                            label = { Text("Notes") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Button(onClick = {
                            viewModel.addLog(
                                name = exerciseName,
                                sets = sets.toIntOrNull() ?: 3,
                                reps = reps.toIntOrNull() ?: 10,
                                weightKg = weight.toDoubleOrNull() ?: 0.0,
                                notes = notes
                            )
                            exerciseName = ""
                            notes = ""
                        }) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null)
                            Text("Save Log", modifier = Modifier.padding(start = 6.dp))
                        }
                    }
                }
            }

            item { SectionTitle("Today History") }
            if (logs.isEmpty()) {
                item { EmptyWorkoutCard("No logs yet for today. Add your first set.") }
            }
            items(logs, key = { it.logId }) { log ->
                WorkoutLogRow(log = log)
            }
        }
    }
}

@Composable
private fun WorkoutStat(title: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.86f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(title, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.labelMedium)
            Text(value, color = MaterialTheme.colorScheme.onSurface, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(text, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
}

@Composable
private fun WorkoutLogRow(log: ExerciseLog) {
    val intensityColor = when {
        log.weightKg >= 60 -> Color(0xFFEF4444)
        log.weightKg >= 30 -> Color(0xFFF59E0B)
        else -> Color(0xFF10B981)
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(log.exerciseName, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Text(
                    "${log.sets} sets  •  ${log.reps} reps  •  ${"%.1f".format(log.weightKg)} kg",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (log.notes.isNotBlank()) {
                    Text(log.notes, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
                }
            }
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Icon(Icons.Default.Schedule, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary)
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(Icons.Default.Speed, contentDescription = null, tint = intensityColor)
                    Text(
                        "${(log.sets * log.reps * log.weightKg).roundToInt()}",
                        color = intensityColor,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyWorkoutCard(text: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.88f))
    ) {
        Text(text, modifier = Modifier.padding(12.dp), color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
