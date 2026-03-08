package com.fitx.app.ui.screens.water

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fitx.app.ui.components.premium.AnimatedProgressRing
import com.fitx.app.ui.theme.premium.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Feature 11: Water Tracker
 * Complete water intake tracking with quick add, reminders, and daily goals
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WaterTrackerScreen(
    theme: PremiumTheme = PremiumThemes.ElectricBlue,
    waterData: WaterData = WaterData(),
    intakeHistory: List<WaterIntake> = emptyList(),
    onAddWater: (Int) -> Unit = {},
    onDeleteIntake: (WaterIntake) -> Unit = {},
    onBackPressed: () -> Unit = {}
) {
    var showCelebration by remember { mutableStateOf(false) }
    val progress = waterData.current.toFloat() / waterData.goal
    
    LaunchedEffect(waterData.current, waterData.goal) {
        if (waterData.current >= waterData.goal && waterData.current < waterData.goal + 250) {
            showCelebration = true
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Water Tracker", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBackPressed) { Icon(Icons.Default.ArrowBack, "Back") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Box(Modifier.fillMaxSize()) {
            FloatingParticles(Modifier.fillMaxSize(), Color(0xFF00B4D8).copy(alpha = 0.1f))
            
            LazyColumn(
                Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item { Spacer(Modifier.height(8.dp)) }
                item { WaterProgressCard(waterData, progress, theme) }
                item { QuickAddButtons(onAddWater, theme) }
                item { DailyGoalCard(waterData, theme) }
                item { Text("Today's Intake", fontSize = 20.sp, fontWeight = FontWeight.Bold) }
                
                items(intakeHistory) { intake ->
                    WaterIntakeCard(intake, theme) { onDeleteIntake(intake) }
                }
                
                item { Spacer(Modifier.height(80.dp)) }
            }
            
            if (showCelebration) {
                ParticleExplosion(
                    Modifier.fillMaxSize(), showCelebration,
                    listOf(Color(0xFF00B4D8), Color(0xFF0096C7), Color(0xFF0077B6))
                ) { showCelebration = false }
            }
        }
    }
}

@Composable
private fun WaterProgressCard(data: WaterData, progress: Float, theme: PremiumTheme) {
    HeroCard(
        Modifier.fillMaxWidth().height(240.dp),
        listOf(Color(0xFF00B4D8), Color(0xFF0077B6))
    ) {
        Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Water Intake", fontSize = 16.sp, color = Color.White.copy(0.9f))
                Row(verticalAlignment = Alignment.Bottom) {
                    Text("${data.current}", fontSize = 56.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text(" / ${data.goal} ml", fontSize = 20.sp, color = Color.White.copy(0.8f), modifier = Modifier.padding(bottom = 10.dp))
                }
                Text("${data.remaining} ml remaining", fontSize = 14.sp, color = Color.White.copy(0.9f))
            }
            AnimatedProgressRing(
                progress, 140.dp, 16.dp,
                listOf(Color.White, Color.White.copy(0.7f)), Color.White.copy(0.2f), false
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.WaterDrop, null, Modifier.size(40.dp), Color.White)
                    Text("${(progress * 100).toInt()}%", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}

@Composable
private fun QuickAddButtons(onAdd: (Int) -> Unit, theme: PremiumTheme) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Quick Add", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            QuickAddButton("250 ml", 250, onAdd, theme, Modifier.weight(1f))
            QuickAddButton("500 ml", 500, onAdd, theme, Modifier.weight(1f))
            QuickAddButton("1 L", 1000, onAdd, theme, Modifier.weight(1f))
        }
    }
}

@Composable
private fun QuickAddButton(label: String, amount: Int, onAdd: (Int) -> Unit, theme: PremiumTheme, modifier: Modifier) {
    PremiumButton(
        text = label,
        onClick = { onAdd(amount) },
        modifier = modifier,
        gradient = listOf(Color(0xFF00B4D8), Color(0xFF0077B6)),
        height = 56.dp,
        icon = { Icon(Icons.Default.Add, null, tint = Color.White) }
    )
}

@Composable
private fun DailyGoalCard(data: WaterData, theme: PremiumTheme) {
    GlassCard(Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(56.dp).clip(CircleShape).background(
                    Brush.linearGradient(listOf(Color(0xFF00B4D8), Color(0xFF0077B6)))
                ), Alignment.Center
            ) {
                Icon(Icons.Default.Flag, null, Modifier.size(28.dp), Color.White)
            }
            Column(Modifier.weight(1f)) {
                Text("Daily Goal", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface.copy(0.7f))
                Text("${data.goal} ml", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Text("${data.glassesRemaining} glasses remaining", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(0.6f))
            }
        }
    }
}

@Composable
private fun WaterIntakeCard(intake: WaterIntake, theme: PremiumTheme, onDelete: () -> Unit) {
    GlassCard(Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    Modifier.size(48.dp).clip(CircleShape).background(Color(0xFF00B4D8).copy(0.15f)),
                    Alignment.Center
                ) {
                    Icon(Icons.Default.WaterDrop, null, Modifier.size(24.dp), Color(0xFF00B4D8))
                }
                Column {
                    Text("${intake.amount} ml", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text(formatTime(intake.timestamp), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(0.6f))
                }
            }
            IconButton(onClick = onDelete, Modifier.size(36.dp)) {
                Icon(Icons.Default.Delete, "Delete", Modifier.size(20.dp), Color(0xFFEF4444))
            }
        }
    }
}

// Data Classes
data class WaterData(
    val current: Int = 0,
    val goal: Int = 2000,
    val remaining: Int = 2000,
    val glassesRemaining: Int = 8
)

data class WaterIntake(
    val id: String = UUID.randomUUID().toString(),
    val amount: Int,
    val timestamp: Date = Date()
)

private fun formatTime(date: Date): String {
    return SimpleDateFormat("hh:mm a", Locale.getDefault()).format(date)
}

