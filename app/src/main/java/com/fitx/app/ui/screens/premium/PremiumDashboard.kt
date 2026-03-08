package com.fitx.app.ui.screens.premium

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fitx.app.ui.theme.premium.*

/**
 * Premium Dashboard Screen
 * World-class fitness dashboard with stunning animations
 */

@Composable
fun PremiumDashboardScreen(
    modifier: Modifier = Modifier,
    currentTheme: PremiumTheme = PremiumThemes.ElectricBlue
) {
    var showCelebration by remember { mutableStateOf(false) }
    
    Box(modifier = modifier.fillMaxSize()) {
        // Animated background particles
        FloatingParticles(
            modifier = Modifier.fillMaxSize(),
            color = currentTheme.primaryColor.copy(alpha = 0.1f)
        )
        
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                GreetingSection()
            }
            
            item {
                HeroStatCard(
                    mainValue = "5.4",
                    mainUnit = "km",
                    label = "Today's Distance",
                    gradient = listOf(currentTheme.gradientStart, currentTheme.gradientEnd),
                    icon = Icons.Default.DirectionsRun
                )
            }
            
            item {
                QuickStatsRow(theme = currentTheme)
            }
            
            item {
                Text(
                    text = "Today's Activity",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            
            items(getDashboardCards()) { card ->
                AnimatedDashboardCard(
                    card = card,
                    theme = currentTheme
                )
            }
            
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
        
        // Floating Action Button
        FloatingActionButton(
            onClick = { showCelebration = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp),
            icon = {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add",
                    tint = Color.White
                )
            },
            backgroundColor = currentTheme.primaryColor
        )
        
        // Celebration effect
        if (showCelebration) {
            ParticleExplosion(
                modifier = Modifier.fillMaxSize(),
                trigger = showCelebration,
                colors = listOf(
                    currentTheme.primaryColor,
                    currentTheme.secondaryColor,
                    currentTheme.accentColor
                ),
                onComplete = { showCelebration = false }
            )
        }
    }
}

@Composable
private fun GreetingSection() {
    val currentHour = remember { java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY) }
    val greeting = when (currentHour) {
        in 0..11 -> "Good Morning"
        in 12..16 -> "Good Afternoon"
        else -> "Good Evening"
    }
    
    Column {
        Text(
            text = greeting,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Ready to crush your goals?",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun HeroStatCard(
    mainValue: String,
    mainUnit: String,
    label: String,
    gradient: List<Color>,
    icon: ImageVector
) {
    var isVisible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        isVisible = true
    }
    
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 })
    ) {
        HeroCard(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            backgroundGradient = gradient
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = label,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                    
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
                
                Row(
                    verticalAlignment = Alignment.Bottom,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    AnimatedCounter(
                        value = mainValue,
                        fontSize = 72.sp,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = mainUnit,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun QuickStatsRow(theme: PremiumTheme) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        QuickStatCard(
            value = "2,847",
            label = "Steps",
            icon = Icons.Default.DirectionsWalk,
            color = theme.primaryColor,
            modifier = Modifier.weight(1f)
        )
        QuickStatCard(
            value = "420",
            label = "Calories",
            icon = Icons.Default.LocalFireDepartment,
            color = theme.secondaryColor,
            modifier = Modifier.weight(1f)
        )
        QuickStatCard(
            value = "32",
            label = "Minutes",
            icon = Icons.Default.Timer,
            color = theme.accentColor,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun QuickStatCard(
    value: String,
    label: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    GlassCard(
        modifier = modifier.height(100.dp),
        cornerRadius = 20.dp
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = label,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun AnimatedDashboardCard(
    card: DashboardCardData,
    theme: PremiumTheme
) {
    var isVisible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(card.animationDelay)
        isVisible = true
    }
    
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn() + slideInHorizontally(initialOffsetX = { it / 2 })
    ) {
        GlowingCard(
            modifier = Modifier.fillMaxWidth(),
            glowColor = card.color
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = card.title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = card.subtitle,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
                
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(card.color, card.color.copy(alpha = 0.7f))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = card.icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun AnimatedCounter(
    value: String,
    fontSize: androidx.compose.ui.unit.TextUnit,
    color: Color
) {
    Text(
        text = value,
        fontSize = fontSize,
        fontWeight = FontWeight.Bold,
        color = color
    )
}

private data class DashboardCardData(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val color: Color,
    val animationDelay: Long
)

private fun getDashboardCards() = listOf(
    DashboardCardData(
        title = "Workout",
        subtitle = "Start your training session",
        icon = Icons.Default.FitnessCenter,
        color = Color(0xFF3A86FF),
        animationDelay = 100
    ),
    DashboardCardData(
        title = "Nutrition",
        subtitle = "Track your meals",
        icon = Icons.Default.Restaurant,
        color = Color(0xFF06FFA5),
        animationDelay = 200
    ),
    DashboardCardData(
        title = "Habits",
        subtitle = "Build consistency",
        icon = Icons.Default.CheckCircle,
        color = Color(0xFF8338EC),
        animationDelay = 300
    ),
    DashboardCardData(
        title = "Progress",
        subtitle = "View your stats",
        icon = Icons.Default.TrendingUp,
        color = Color(0xFFFF006E),
        animationDelay = 400
    )
)
