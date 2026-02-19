package com.fitx.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fitx.app.domain.model.WeightEntry
import com.fitx.app.ui.components.MetricCard
import com.fitx.app.ui.components.WeightLineChart
import com.fitx.app.ui.viewmodel.WeightViewModel
import com.fitx.app.util.DateUtils

@Composable
fun WeightRoute(
    viewModel: WeightViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val entries by viewModel.entries.collectAsStateWithLifecycle()
    val weeklyAverage by viewModel.weeklyAverage.collectAsStateWithLifecycle()
    val input by viewModel.weightInput.collectAsStateWithLifecycle()
    val editingId by viewModel.editingEntryId.collectAsStateWithLifecycle()

    FitxScreenScaffold(topBar = { ScreenTopBar("Daily Weight Tracker", onBack) }) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                OutlinedTextField(
                    value = input,
                    onValueChange = viewModel::updateWeightInput,
                    label = { Text("Weight (kg)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                Button(onClick = viewModel::saveToday) {
                    Text(if (editingId == null) "Save Today" else "Update Entry")
                }
            }
            item {
                MetricCard(
                    title = "Weekly Average",
                    value = weeklyAverage?.let { "%.2f kg".format(it) } ?: "--"
                )
            }
            item {
                WeightLineChart(values = entries.map { it.weightKg }.reversed())
            }
            item {
                Text("History", style = MaterialTheme.typography.titleMedium)
            }
            items(entries, key = { it.entryId }) { entry ->
                WeightRow(
                    entry = entry,
                    onEdit = { viewModel.loadForEdit(entry) },
                    onDelete = { viewModel.deleteEntry(entry.entryId) }
                )
            }
        }
    }
}

@Composable
private fun WeightRow(
    entry: WeightEntry,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(DateUtils.formatEpochDay(entry.dateEpochDay))
                Text("${entry.weightKg} kg", fontWeight = FontWeight.Bold)
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
