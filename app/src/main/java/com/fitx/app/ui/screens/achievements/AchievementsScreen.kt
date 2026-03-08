package com.fitx.app.ui.screens.achievements

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fitx.app.ui.components.premium.AnimatedProgressRing
import com.fitx.app.ui.theme.premium.*

/**
 * Feature 15: Achievement Badges - UNBEATABLE DESIGN
 * Stunning achievement system with Lottie animations and celebrations
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AchievementsScreen(
    theme: PremiumTheme = PremiumThemes.ElectricBlue,
    achievements: List<Achievement> = emptyList(),
    onAchievementClick: (Achievement) -> Unit = {},
    onBackPressed: () -> Unit = {}
) {
    var showCelebration by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf(AchievementCategory.ALL) }
    
    val filteredAchievements = remember(achievements, selectedCategory) {
        if (selectedCategory == AchievementCategory.ALL) achievements
        else achievements.filter { it.category == selectedCategory }
    }
    
    val unlockedCount = achievements.count { it.isUnlocked }
    val progress = if (achievements.isNotEmpty()) unlockedCount.toFloat() / achievements.size else 0f
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Achievements", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Box(Modifier.fillMaxSize()) {
            FloatingParticles(Modifier.fillMaxSize(), theme.primaryColor.copy(alpha = 0.1f))
            
            Column(Modifier.fillMaxSize().padding(padding)) {
                // Progress Header
                AchievementProgressHeader(unlockedCount, achievements.size, progress, theme)
                
                // Category Filter
                CategoryFilterRow(selectedCategory) { selectedCategory = it }
                
                // Achievements Grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    Modifier.fillMaxSize().padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    items(filteredAchievements) { achievement ->
                        AchievementBadgeCard(
                            achievement, theme,
                            onClick = {
                                onAchievementClick(achievement)
                                if (achievement.isUnlocked) showCelebration = true
                            }
                        )
                    }
                }
            }
            
            if (showCelebration) {
                ConfettiEffect(Modifier.fillMaxSize(), showCelebration)
                LaunchedEffect(Unit) {
                    kotlinx.coroutines.delay(3000)
                    showCelebration = false
                }
            }
        }
    }
}

@Composable
private fun AchievementProgressHeader(
    unlocked: Int,
    total: Int,
    progress: Float,
    theme: PremiumTheme
) {
    HeroCard(
        Modifier.fillMaxWidth().height(180.dp).padding(16.dp),
        listOf(theme.gradientStart, theme.gradientEnd)
    ) {
        Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Achievements", fontSize = 16.sp, color = Color.White.copy(0.9f))
                Text("$unlocked / $total", fontSize = 48.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text("Unlocked", fontSize = 14.sp, color = Color.White.copy(0.8f))
            }
            AnimatedProgressRing(
                progress, 120.dp, 14.dp,
                listOf(Color.White, Color.White.copy(0.7f)), Color.White.copy(0.2f), false
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.EmojiEvents, null, Modifier.size(32.dp), Color.White)
                    Text("${(progress * 100).toInt()}%", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}

@Composable
private fun CategoryFilterRow(
    selected: AchievementCategory,
    onSelect: (AchievementCategory) -> Unit
) {
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        Arrangement.spacedBy(8.dp)
    ) {
        AchievementCategory.values().forEach { category ->
            FilterChip(
                selected = selected == category,
                onClick = { onSelect(category) },
                label = { Text(category.displayName) },
                leadingIcon = if (selected == category) {
                    { Icon(Icons.Default.Check, null, Modifier.size(18.dp)) }
                } else null
            )
        }
    }
}

@Composable
private fun AchievementBadgeCard(
    achievement: Achievement,
    theme: PremiumTheme,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        if (achievement.isUnlocked) 1f else 0.95f,
        spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "badge_scale"
    )
    
    val rotation = if (achievement.isUnlocked) {
        rememberRotationAnimation(durationMillis = 8000)
    } else 0f
    
    GlowingCard(
        Modifier.aspectRatio(1f).scale(scale).clickable(onClick = onClick),
        if (achievement.isUnlocked) theme.primaryColor else Color.Transparent
    ) {
        Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            // Badge Icon
            Box(
                Modifier.size(80.dp).clip(CircleShape).background(
                    if (achievement.isUnlocked) {
                        Brush.radialGradient(
                            listOf(achievement.color, achievement.color.copy(0.7f))
                        )
                    } else {
                        Brush.radialGradient(
                            listOf(Color.Gray.copy(0.3f), Color.Gray.copy(0.1f))
                        )
                    }
                ).then(if (achievement.isUnlocked) Modifier.rotateEffect(rotation) else Modifier),
                Alignment.Center
            ) {
                Icon(
                    achievement.icon,
                    null,
                    Modifier.size(40.dp),
                    if (achievement.isUnlocked) Color.White else Color.Gray.copy(0.5f)
                )
            }
            
            Spacer(Modifier.height(12.dp))
            
            // Badge Name
            Text(
                achievement.name,
                fontSize = 14.sp,
                fontWeight = if (achievement.isUnlocked) FontWeight.Bold else FontWeight.Normal,
                textAlign = TextAlign.Center,
                color = if (achievement.isUnlocked) {
                    MaterialTheme.colorScheme.onSurface
                } else {
                    MaterialTheme.colorScheme.onSurface.copy(0.5f)
                }
            )
            
            // Progress or Date
            if (achievement.isUnlocked) {
                Text(
                    achievement.unlockedDate,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(0.6f)
                )
            } else {
                Spacer(Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = achievement.progress,
                    Modifier.fillMaxWidth(0.8f).height(4.dp).clip(RoundedCornerShape(2.dp)),
                    color = theme.primaryColor,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
                Text(
                    "${(achievement.progress * 100).toInt()}%",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(0.5f)
                )
            }
        }
    }
}

// Data Classes
data class Achievement(
    val id: String,
    val name: String,
    val description: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val color: Color,
    val category: AchievementCategory,
    val isUnlocked: Boolean,
    val progress: Float,
    val unlockedDate: String,
    val requirement: String
)

enum class AchievementCategory(val displayName: String) {
    ALL("All"),
    WORKOUT("Workout"),
    DISTANCE("Distance"),
    STREAK("Streak"),
    WEIGHT("Weight"),
    NUTRITION("Nutrition")
}

// Sample Achievements
fun getSampleAchievements() = listOf(
    Achievement("1", "First Workout", "Complete your first workout", Icons.Default.FitnessCenter, Color(0xFF3A86FF), AchievementCategory.WORKOUT, true, 1f, "Jan 15, 2024", "1 workout"),
    Achievement("2", "10 Workouts", "Complete 10 workouts", Icons.Default.EmojiEvents, Color(0xFFFF006E), AchievementCategory.WORKOUT, true, 1f, "Feb 20, 2024", "10 workouts"),
    Achievement("3", "50 Workouts", "Complete 50 workouts", Icons.Default.MilitaryTech, Color(0xFFFBBF24), AchievementCategory.WORKOUT, false, 0.6f, "", "50 workouts"),
    Achievement("4", "10km Runner", "Run 10 kilometers", Icons.Default.DirectionsRun, Color(0xFF06FFA5), AchievementCategory.DISTANCE, true, 1f, "Mar 5, 2024", "10km"),
    Achievement("5", "100km Walker", "Walk 100 kilometers", Icons.Default.DirectionsWalk, Color(0xFF8338EC), AchievementCategory.DISTANCE, false, 0.45f, "", "100km"),
    Achievement("6", "7 Day Streak", "Maintain 7 day streak", Icons.Default.Whatshot, Color(0xFFFF6B6B), AchievementCategory.STREAK, true, 1f, "Apr 1, 2024", "7 days"),
    Achievement("7", "30 Day Streak", "Maintain 30 day streak", Icons.Default.LocalFireDepartment, Color(0xFFFF4500), AchievementCategory.STREAK, false, 0.7f, "", "30 days"),
    Achievement("8", "Goal Weight", "Reach your goal weight", Icons.Default.Flag, Color(0xFF10B981), AchievementCategory.WEIGHT, false, 0.8f, "", "Goal achieved")
)

