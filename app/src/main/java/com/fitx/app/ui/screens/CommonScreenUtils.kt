package com.fitx.app.ui.screens

import android.Manifest
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fitx.app.domain.model.TaskItem

@Composable
fun FitxScreenScaffold(
    topBar: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    val colors = MaterialTheme.colorScheme
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        colors.background,
                        colors.surfaceVariant.copy(alpha = 0.92f),
                        colors.background.copy(alpha = 0.98f)
                    )
                )
            )
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = topBar,
            content = content
        )
    }
}

@Composable
fun FlowButtonRow(labels: List<String>, actions: List<() -> Unit>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        labels.forEachIndexed { index, label ->
            Button(
                onClick = actions[index],
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f)
                )
            ) {
                Text(label)
            }
        }
    }
}

@Composable
fun TaskRow(
    task: TaskItem,
    onToggle: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.92f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = task.isCompleted, onCheckedChange = onToggle)
                Column {
                    Text(
                        task.title,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (task.description.isNotBlank()) {
                        Text(
                            task.description,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete")
            }
        }
    }
}

@Composable
fun <T> EnumSelector(
    title: String,
    values: List<T>,
    selected: T,
    onSelected: (T) -> Unit
) {
    Column {
        Text(title, style = MaterialTheme.typography.titleSmall)
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            values.forEach { value ->
                FilterChip(
                    selected = selected == value,
                    onClick = { onSelected(value) },
                    label = { Text(value.toString()) }
                )
            }
        }
    }
}

fun requiredTrackingPermissions(): Array<String> {
    val permissions = mutableListOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        permissions += Manifest.permission.ACTIVITY_RECOGNITION
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        permissions += Manifest.permission.POST_NOTIFICATIONS
    }
    return permissions.toTypedArray()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenTopBar(title: String, onBack: () -> Unit) {
    TopAppBar(
        title = { Text(title) },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
            titleContentColor = MaterialTheme.colorScheme.onBackground
        ),
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
        }
    )
}
