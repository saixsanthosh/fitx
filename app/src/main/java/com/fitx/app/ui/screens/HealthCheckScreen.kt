package com.fitx.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fitx.app.domain.model.HealthCheckItem
import com.fitx.app.domain.model.HealthCheckStatus
import com.fitx.app.ui.viewmodel.HealthCheckViewModel

@Composable
fun HealthCheckRoute(
    viewModel: HealthCheckViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val checks by viewModel.checks.collectAsStateWithLifecycle()

    FitxScreenScaffold(topBar = { ScreenTopBar("App Health Check", onBack) }) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Button(
                    onClick = { viewModel.runHealthCheck() },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Run Health Check")
                }
            }
            items(checks, key = { it.title }) { item ->
                HealthCheckCard(item)
            }
        }
    }
}

@Composable
private fun HealthCheckCard(item: HealthCheckItem) {
    val color = when (item.status) {
        HealthCheckStatus.OK -> MaterialTheme.colorScheme.primary
        HealthCheckStatus.WARN -> MaterialTheme.colorScheme.tertiary
        HealthCheckStatus.ERROR -> MaterialTheme.colorScheme.error
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.92f)),
        border = BorderStroke(1.dp, color.copy(alpha = 0.45f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(item.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Text(item.status.name, color = color, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            Text(item.detail, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
        }
    }
}
