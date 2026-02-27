package com.fitx.app.ui.components

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.fitx.app.domain.model.ActivityPoint
import androidx.compose.ui.viewinterop.AndroidView
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline

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
            containerColor = colors.surfaceVariant.copy(alpha = 0.92f)
        ),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, colors.outline.copy(alpha = 0.22f))
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
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
            .border(1.dp, colors.outline.copy(alpha = 0.22f), RoundedCornerShape(16.dp))
            .background(colors.surfaceVariant.copy(alpha = 0.92f), shape = RoundedCornerShape(16.dp))
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

@Composable
fun RouteMapPreview(
    points: List<ActivityPoint>,
    modifier: Modifier = Modifier,
    height: Dp = 180.dp
) {
    val colors = MaterialTheme.colorScheme
    val context = LocalContext.current
    val geoPoints = remember(points) { points.map { GeoPoint(it.latitude, it.longitude) } }
    val mapView = remember(context) { createRouteMapView(context) }

    DisposableEffect(mapView) {
        mapView.onResume()
        onDispose {
            mapView.onPause()
            mapView.onDetach()
        }
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surfaceVariant.copy(alpha = 0.92f)),
        border = BorderStroke(1.dp, colors.outline.copy(alpha = 0.35f))
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("Route Map", style = MaterialTheme.typography.titleSmall, color = colors.onSurface)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(height)
                    .background(colors.surfaceVariant.copy(alpha = 0.55f), RoundedCornerShape(14.dp))
            ) {
                AndroidView(
                    modifier = Modifier.fillMaxWidth(),
                    factory = { mapView },
                    update = { view ->
                        updateRouteMap(view, geoPoints, colors.primary)
                    }
                )
            }
            if (geoPoints.isEmpty()) {
                Text(
                    "Start a session to draw your route.",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.onSurfaceVariant
                )
            }
        }
    }
}

private fun createRouteMapView(context: Context): MapView {
    Configuration.getInstance().userAgentValue = context.packageName
    return MapView(context).apply {
        setTileSource(TileSourceFactory.MAPNIK)
        setMultiTouchControls(true)
        minZoomLevel = 3.0
        maxZoomLevel = 20.0
        controller.setZoom(4.0)
        controller.setCenter(GeoPoint(20.5937, 78.9629))
    }
}

private fun updateRouteMap(mapView: MapView, points: List<GeoPoint>, lineColor: Color) {
    mapView.overlays.clear()

    if (points.isEmpty()) {
        mapView.controller.setZoom(4.0)
        mapView.controller.setCenter(GeoPoint(20.5937, 78.9629))
        mapView.invalidate()
        return
    }

    val routeLine = Polyline().apply {
        outlinePaint.color = lineColor.toArgb()
        outlinePaint.strokeWidth = 10f
        setPoints(points)
    }
    mapView.overlays.add(routeLine)

    val startMarker = Marker(mapView).apply {
        position = points.first()
        title = "Start"
        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
    }
    mapView.overlays.add(startMarker)

    if (points.size > 1) {
        val finishMarker = Marker(mapView).apply {
            position = points.last()
            title = "Finish"
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        }
        mapView.overlays.add(finishMarker)
    }

    if (points.size == 1) {
        mapView.controller.setZoom(17.0)
        mapView.controller.setCenter(points.first())
    } else {
        val latitudes = points.map { it.latitude }
        val longitudes = points.map { it.longitude }
        val bounds = BoundingBox(
            latitudes.maxOrNull() ?: points.first().latitude,
            longitudes.maxOrNull() ?: points.first().longitude,
            latitudes.minOrNull() ?: points.first().latitude,
            longitudes.minOrNull() ?: points.first().longitude
        )
        mapView.post { mapView.zoomToBoundingBox(bounds, true, 72) }
    }

    mapView.invalidate()
}
