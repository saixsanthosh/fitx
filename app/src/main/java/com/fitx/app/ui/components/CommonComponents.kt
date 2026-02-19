package com.fitx.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun MetricCard(
    title: String,
    value: String,
    subtitle: String? = null,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = colors.surfaceVariant.copy(alpha = 0.96f)
        ),
        shape = RoundedCornerShape(22.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title.uppercase(),
                style = MaterialTheme.typography.labelLarge,
                color = colors.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                color = colors.onSurface
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun WeightLineChart(
    values: List<Double>,
    modifier: Modifier = Modifier
) {
    val points = if (values.isNotEmpty()) values else listOf(0.0)
    val min = points.minOrNull() ?: 0.0
    val max = points.maxOrNull() ?: 1.0
    val range = (max - min).takeIf { it > 0.0 } ?: 1.0
    val colors = MaterialTheme.colorScheme

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    listOf(
                        colors.surfaceVariant.copy(alpha = 0.94f),
                        colors.surface.copy(alpha = 0.98f)
                    )
                ),
                shape = RoundedCornerShape(22.dp)
            )
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Trend", style = MaterialTheme.typography.labelMedium)
            Text(text = "${"%.1f".format(points.last())} kg", style = MaterialTheme.typography.labelMedium)
        }
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .padding(top = 8.dp)
        ) {
            if (points.size < 2) return@Canvas
            val path = Path()
            points.forEachIndexed { index, value ->
                val x = (index / (points.lastIndex.toFloat())) * size.width
                val yNorm = ((value - min) / range).toFloat()
                val y = size.height - (yNorm * size.height)
                if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
            }
            drawPath(
                path = path,
                color = colors.primary,
                style = Stroke(width = 6f)
            )
            drawLine(
                color = colors.outline.copy(alpha = 0.5f),
                start = Offset(0f, size.height),
                end = Offset(size.width, size.height),
                strokeWidth = 2f
            )
        }
    }
}
