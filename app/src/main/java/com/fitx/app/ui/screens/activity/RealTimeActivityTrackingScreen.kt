package com.fitx.app.ui.screens.activity

import android.location.Location
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.fitx.app.ui.components.premium.AnimatedProgressRing
import com.fitx.app.ui.theme.premium.*
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.Marker
import kotlinx.coroutines.delay

/**
 * Feature 4: Real-Time Activity Tracking with Live Map
 * Swiggy/Zomato/Zepto style live location tracking
 * Premium design with animated map and real-time stats
 */

@Composable
fun RealTimeActivityTrackingScreen(
    activityType: TrackingActivityType = TrackingActivityType.WALKING,
    theme: PremiumTheme = PremiumThemes.ElectricBlue,
    onStopActivity: (ActivitySummary) -> Unit = {},
    onBackPressed: () -> Unit = {}
) {
    val context = LocalContext.current
    var isTracking by remember { mutableStateOf(false) }
    var isPaused by remember { mutableStateOf(false) }
    var showMap by remember { mutableStateOf(true) }
    
    // Real-time stats
    var distance by remember { mutableStateOf(0.0f) }
    var duration by remember { mutableStateOf(0) }
    var calories by remember { mutableStateOf(0) }
    var currentSpeed by remember { mutableStateOf(0.0f) }
    var avgSpeed by remember { mutableStateOf(0.0f) }
    var steps by remember { mutableStateOf(0) }
    var elevation by remember { mutableStateOf(0f) }
    
    // Location tracking
    var currentLocation by remember { mutableStateOf<GeoPoint?>(null) }
    var routePoints by remember { mutableStateOf<List<GeoPoint>>(emptyList()) }
    
    // Speed zones
    var speedZone by remember { mutableStateOf(SpeedZone.MODERATE) }
    
    // Milestones
    var lastMilestone by remember { mutableStateOf(0f) }
    var showMilestone by remember { mutableStateOf(false) }
    
    // Simulate live tracking
    LaunchedEffect(isTracking, isPaused) {
        if (isTracking && !isPaused) {
            while (true) {
                delay(1000)
                duration++
                
                // Simulate location update
                val newLat = 37.7749 + (routePoints.size * 0.0001)
                val newLng = -122.4194 + (routePoints.size * 0.0001)
                val newPoint = GeoPoint(newLat, newLng)
                currentLocation = newPoint
                routePoints = routePoints + newPoint
                
                // Update stats
                distance += 0.015f // ~15m per second
                currentSpeed = (10..25).random().toFloat() / 10f // 1-2.5 km/h
                avgSpeed = distance / (duration / 3600f)
                calories = (distance * 60).toInt()
                if (activityType == TrackingActivityType.WALKING) {
                    steps += 2
                }
                elevation += ((-1..1).random() * 0.5f)
                
                // Speed zone detection
                speedZone = when {
                    currentSpeed < 3f -> SpeedZone.SLOW
                    currentSpeed < 6f -> SpeedZone.MODERATE
                    currentSpeed < 10f -> SpeedZone.FAST
                    else -> SpeedZone.SPRINT
                }
                
                // Milestone detection
                val currentKm = distance.toInt()
                if (currentKm > lastMilestone && currentKm % 1 == 0) {
                    lastMilestone = currentKm.toFloat()
                    showMilestone = true
                }
            }
        }
    }
    
    Box(modifier = Modifier.fillMaxSize()) {
        // Real-time Map View
        if (showMap) {
            RealTimeMapView(
                modifier = Modifier.fillMaxSize(),
                currentLocation = currentLocation,
                routePoints = routePoints,
                theme = theme,
                isTracking = isTracking
            )
        }
        
        // Gradient overlay for better readability
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.3f),
                            Color.Transparent,
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.5f)
                        )
                    )
                )
        )
        
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top Bar with Stats
            TopStatsBar(
                activityType = activityType,
                duration = duration,
                distance = distance,
                speedZone = speedZone,
                theme = theme,
                onBackPressed = onBackPressed,
                onToggleMap = { showMap = !showMap }
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Bottom Stats Panel
            BottomStatsPanel(
                distance = distance,
                currentSpeed = currentSpeed,
                avgSpeed = avgSpeed,
                calories = calories,
                steps = steps,
                elevation = elevation,
                activityType = activityType,
                theme = theme,
                isTracking = isTracking,
                isPaused = isPaused
            )
            
            // Control Panel
            ControlPanel(
                isTracking = isTracking,
                isPaused = isPaused,
                theme = theme,
                onStart = { isTracking = true },
                onPause = { isPaused = !isPaused },
                onStop = {
                    isTracking = false
                    isPaused = false
                    onStopActivity(
                        ActivitySummary(
                            distance = distance,
                            duration = duration,
                            calories = calories,
                            avgSpeed = avgSpeed,
                            steps = steps,
                            elevation = elevation,
                            routePoints = routePoints
                        )
                    )
                }
            )
        }
        
        // Live Speed Indicator (Swiggy-style)
        if (isTracking && !isPaused) {
            LiveSpeedIndicator(
                speed = currentSpeed,
                speedZone = speedZone,
                theme = theme,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 120.dp, end = 16.dp)
            )
        }
        
        // Milestone Celebration
        if (showMilestone) {
            MilestoneCelebration(
                distance = lastMilestone,
                theme = theme,
                onDismiss = { showMilestone = false }
            )
        }
        
        // Auto-pause indicator
        if (isPaused && currentSpeed < 0.5f) {
            AutoPauseIndicator(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(16.dp)
            )
        }
    }
}

@Composable
private fun RealTimeMapView(
    modifier: Modifier = Modifier,
    currentLocation: GeoPoint?,
    routePoints: List<GeoPoint>,
    theme: PremiumTheme,
    isTracking: Boolean
) {
    val context = LocalContext.current
    
    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            Configuration.getInstance().load(ctx, ctx.getSharedPreferences("osmdroid", 0))
            
            MapView(ctx).apply {
                setTileSource(TileSourceFactory.MAPNIK)
                setMultiTouchControls(true)
                controller.setZoom(17.0)
                
                // Set initial position
                currentLocation?.let {
                    controller.setCenter(it)
                }
            }
        },
        update = { mapView ->
            mapView.overlays.clear()
            
            // Draw route polyline
            if (routePoints.size > 1) {
                val polyline = Polyline().apply {
                    setPoints(routePoints)
                    outlinePaint.color = android.graphics.Color.parseColor("#3A86FF")
                    outlinePaint.strokeWidth = 12f
                    outlinePaint.strokeCap = android.graphics.Paint.Cap.ROUND
                }
                mapView.overlays.add(polyline)
            }
            
            // Add current location marker with pulse animation
            currentLocation?.let { location ->
                val marker = Marker(mapView).apply {
                    position = location
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                    icon = createPulsingMarkerDrawable(context, theme, isTracking)
                    title = "You are here"
                }
                mapView.overlays.add(marker)
                
                // Auto-center on current location
                if (isTracking) {
                    mapView.controller.animateTo(location)
                }
            }
            
            mapView.invalidate()
        }
    )
}

@Composable
private fun TopStatsBar(
    activityType: TrackingActivityType,
    duration: Int,
    distance: Float,
    speedZone: SpeedZone,
    theme: PremiumTheme,
    onBackPressed: () -> Unit,
    onToggleMap: () -> Unit
) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        cornerRadius = 24.dp,
        glassOpacity = 0.25f
    ) {
        Column {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackPressed) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = activityType.displayName,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        PulsingDot(color = theme.accentColor)
                        Text(
                            text = "Live Tracking",
                            fontSize = 12.sp,
                            color = theme.accentColor
                        )
                    }
                }
                
                IconButton(onClick = onToggleMap) {
                    Icon(
                        imageVector = Icons.Default.Layers,
                        contentDescription = "Toggle Map",
                        tint = Color.White
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Main Stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                CompactStatCard(
                    value = formatDuration(duration),
                    label = "Duration",
                    icon = Icons.Default.Timer
                )
                CompactStatCard(
                    value = String.format("%.2f", distance),
                    label = "Distance (km)",
                    icon = Icons.Default.Route
                )
                CompactStatCard(
                    value = speedZone.displayName,
                    label = "Pace",
                    icon = Icons.Default.Speed,
                    color = speedZone.color
                )
            }
        }
    }
}

@Composable
private fun CompactStatCard(
    value: String,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color = Color.White
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = label,
            fontSize = 10.sp,
            color = Color.White.copy(alpha = 0.8f)
        )
    }
}

@Composable
private fun BottomStatsPanel(
    distance: Float,
    currentSpeed: Float,
    avgSpeed: Float,
    calories: Int,
    steps: Int,
    elevation: Float,
    activityType: TrackingActivityType,
    theme: PremiumTheme,
    isTracking: Boolean,
    isPaused: Boolean
) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        cornerRadius = 32.dp,
        glassOpacity = 0.25f
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            // Distance Ring (Hero Stat)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AnimatedProgressRing(
                    progress = (distance / 10f).coerceIn(0f, 1f),
                    size = 120.dp,
                    strokeWidth = 12.dp,
                    gradientColors = listOf(theme.primaryColor, theme.secondaryColor),
                    showPercentage = false
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = String.format("%.2f", distance),
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "km",
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
                
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    LiveStatRow(
                        icon = Icons.Default.Speed,
                        label = "Current",
                        value = String.format("%.1f km/h", currentSpeed),
                        color = theme.accentColor
                    )
                    LiveStatRow(
                        icon = Icons.Default.TrendingUp,
                        label = "Average",
                        value = String.format("%.1f km/h", avgSpeed),
                        color = theme.primaryColor
                    )
                    LiveStatRow(
                        icon = Icons.Default.LocalFireDepartment,
                        label = "Calories",
                        value = "$calories kcal",
                        color = Color(0xFFFF6B6B)
                    )
                }
            }
            
            Divider(color = Color.White.copy(alpha = 0.2f))
            
            // Additional Stats Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                if (activityType == TrackingActivityType.WALKING || activityType == TrackingActivityType.RUNNING) {
                    MiniStatCard(
                        icon = Icons.Default.DirectionsWalk,
                        value = steps.toString(),
                        label = "Steps",
                        color = theme.secondaryColor
                    )
                }
                MiniStatCard(
                    icon = Icons.Default.Terrain,
                    value = String.format("%.0f m", elevation),
                    label = "Elevation",
                    color = theme.accentColor
                )
                MiniStatCard(
                    icon = Icons.Default.MyLocation,
                    value = if (isTracking && !isPaused) "Active" else "Paused",
                    label = "Status",
                    color = if (isTracking && !isPaused) Color(0xFF06FFA5) else Color(0xFFFFA500)
                )
            }
        }
    }
}

@Composable
private fun LiveStatRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    color: Color
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(20.dp)
        )
        Column {
            Text(
                text = label,
                fontSize = 10.sp,
                color = Color.White.copy(alpha = 0.7f)
            )
            Text(
                text = value,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
private fun MiniStatCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = label,
            fontSize = 10.sp,
            color = Color.White.copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun ControlPanel(
    isTracking: Boolean,
    isPaused: Boolean,
    theme: PremiumTheme,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onStop: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (!isTracking) {
            // Start Button
            PremiumButton(
                text = "Start Activity",
                onClick = onStart,
                modifier = Modifier.weight(1f),
                gradient = listOf(theme.gradientStart, theme.gradientEnd),
                height = 64.dp,
                icon = {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
            )
        } else {
            // Pause/Resume Button
            GradientIconButton(
                onClick = onPause,
                modifier = Modifier.size(64.dp),
                gradient = if (isPaused) {
                    listOf(Color(0xFF06FFA5), Color(0xFF00D9A5))
                } else {
                    listOf(Color(0xFFFFA500), Color(0xFFFF8C00))
                },
                icon = {
                    Icon(
                        imageVector = if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            )
            
            // Stop Button
            PremiumButton(
                text = "Finish Activity",
                onClick = onStop,
                modifier = Modifier.weight(1f),
                gradient = listOf(Color(0xFFEF4444), Color(0xFFDC2626)),
                height = 64.dp,
                icon = {
                    Icon(
                        imageVector = Icons.Default.Stop,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
            )
        }
    }
}

@Composable
private fun LiveSpeedIndicator(
    speed: Float,
    speedZone: SpeedZone,
    theme: PremiumTheme,
    modifier: Modifier = Modifier
) {
    val scale = rememberPulseAnimation(minScale = 0.95f, maxScale = 1.05f, durationMillis = 1000)
    
    Box(
        modifier = modifier
            .size(80.dp)
            .scale(scale)
            .shadow(8.dp, CircleShape)
            .clip(CircleShape)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        speedZone.color,
                        speedZone.color.copy(alpha = 0.8f)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = String.format("%.1f", speed),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "km/h",
                fontSize = 10.sp,
                color = Color.White.copy(alpha = 0.9f)
            )
        }
    }
}

@Composable
private fun MilestoneCelebration(
    distance: Float,
    theme: PremiumTheme,
    onDismiss: () -> Unit
) {
    LaunchedEffect(Unit) {
        delay(3000)
        onDismiss()
    }
    
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        ParticleExplosion(
            modifier = Modifier.fillMaxSize(),
            trigger = true,
            particleCount = 100,
            colors = listOf(
                theme.primaryColor,
                theme.secondaryColor,
                theme.accentColor,
                Color(0xFFFFD700)
            )
        )
        
        GlowingCard(
            modifier = Modifier.padding(32.dp),
            glowColor = theme.primaryColor
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = null,
                    tint = Color(0xFFFFD700),
                    modifier = Modifier.size(64.dp)
                )
                Text(
                    text = "Milestone Reached!",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${distance.toInt()} km completed",
                    fontSize = 18.sp,
                    color = theme.primaryColor
                )
                Text(
                    text = "Keep going! 🔥",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
private fun AutoPauseIndicator(modifier: Modifier = Modifier) {
    GlassCard(
        modifier = modifier,
        cornerRadius = 20.dp
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.PauseCircle,
                contentDescription = null,
                tint = Color(0xFFFFA500),
                modifier = Modifier.size(32.dp)
            )
            Column {
                Text(
                    text = "Auto-Paused",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Start moving to resume",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
private fun PulsingDot(color: Color) {
    val scale = rememberPulseAnimation(minScale = 0.8f, maxScale = 1.2f, durationMillis = 1000)
    
    Box(
        modifier = Modifier
            .size(8.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(color)
    )
}

// Helper function to create pulsing marker drawable
private fun createPulsingMarkerDrawable(
    context: android.content.Context,
    theme: PremiumTheme,
    isTracking: Boolean
): android.graphics.drawable.Drawable {
    // Create a custom drawable for the marker
    return android.graphics.drawable.GradientDrawable().apply {
        shape = android.graphics.drawable.GradientDrawable.OVAL
        setColor(android.graphics.Color.parseColor("#3A86FF"))
        setSize(40, 40)
    }
}

private fun formatDuration(seconds: Int): String {
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    val secs = seconds % 60
    return if (hours > 0) {
        String.format("%d:%02d:%02d", hours, minutes, secs)
    } else {
        String.format("%02d:%02d", minutes, secs)
    }
}

// Data Classes
data class ActivitySummary(
    val distance: Float,
    val duration: Int,
    val calories: Int,
    val avgSpeed: Float,
    val steps: Int,
    val elevation: Float,
    val routePoints: List<GeoPoint>
)

enum class TrackingActivityType(
    val displayName: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    WALKING("Walking", Icons.Default.DirectionsWalk),
    RUNNING("Running", Icons.Default.DirectionsRun),
    CYCLING("Cycling", Icons.Default.DirectionsBike),
    HIKING("Hiking", Icons.Default.Hiking)
}

enum class SpeedZone(
    val displayName: String,
    val color: Color
) {
    SLOW("Slow", Color(0xFF06B6D4)),
    MODERATE("Moderate", Color(0xFF10B981)),
    FAST("Fast", Color(0xFFFFA500)),
    SPRINT("Sprint", Color(0xFFEF4444))
}
