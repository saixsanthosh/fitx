package com.fitx.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fitx.app.ui.viewmodel.HabitViewModel

@Composable
fun HabitsRoute(
    viewModel: HabitViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val habits by viewModel.habits.collectAsStateWithLifecycle()
    var name by remember { mutableStateOf("") }
    var target by remember { mutableStateOf("1") }

    FitxScreenScaffold(topBar = { ScreenTopBar("Fixed Daily List", onBack) }) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Type 1: Fixed Daily List", fontWeight = FontWeight.Bold)
                        Text("Same checklist every day. Great for water, gym, reading, sleep streaks.")
                    }
                }
            }
            item {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Habit name") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                OutlinedTextField(
                    value = target,
                    onValueChange = { target = it },
                    label = { Text("Target per day") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                Button(onClick = {
                    viewModel.addHabit(name, target.toIntOrNull() ?: 1)
                    name = ""
                    target = "1"
                }) {
                    Text("Add Habit")
                }
            }
            item {
                Text("Habit Streaks", style = MaterialTheme.typography.titleMedium)
            }
            items(habits, key = { it.habit.habitId }) { item ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(item.habit.name, fontWeight = FontWeight.Bold)
                            Text("Today: ${item.todayCount}/${item.habit.targetPerDay}")
                            Text("Streak: ${item.streakDays} days")
                        }
                        Button(onClick = { viewModel.incrementHabit(item.habit.habitId) }) {
                            Text("+1")
                        }
                    }
                }
            }
        }
    }
}
