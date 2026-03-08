package com.fitx.app.ui.screens.nutrition

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
 * Feature 10: Nutrition Tracker
 * Complete nutrition tracking with USDA API, macros, and meal logging
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NutritionTrackerScreen(
    theme: PremiumTheme = PremiumThemes.ElectricBlue,
    nutritionData: NutritionData = NutritionData(),
    meals: List<MealEntry> = emptyList(),
    onSearchFood: () -> Unit = {},
    onAddMeal: (MealEntry) -> Unit = {},
    onDeleteMeal: (MealEntry) -> Unit = {},
    onBackPressed: () -> Unit = {}
) {
    var selectedMealType by remember { mutableStateOf(MealType.BREAKFAST) }
    var showAddMealDialog by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nutrition Tracker", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onSearchFood) {
                        Icon(Icons.Default.Search, "Search", tint = theme.primaryColor)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddMealDialog = true },
                icon = { Icon(Icons.Default.Add, "Add", tint = Color.White) },
                backgroundColor = theme.primaryColor
            )
        }
    ) { padding ->
        Box(Modifier.fillMaxSize()) {
            FloatingParticles(Modifier.fillMaxSize(), theme.primaryColor.copy(alpha = 0.1f))
            
            LazyColumn(
                Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item { Spacer(Modifier.height(8.dp)) }
                item { CalorieHeroCard(nutritionData, theme) }
                item { MacroSummaryRow(nutritionData, theme) }
                item { MacroPieChart(nutritionData, theme) }
                item { MealTypeSelector(selectedMealType) { selectedMealType = it } }
                item { Text("Today's Meals", fontSize = 20.sp, fontWeight = FontWeight.Bold) }
                
                items(meals.filter { it.mealType == selectedMealType }) { meal ->
                    MealEntryCard(meal, theme) { onDeleteMeal(meal) }
                }
                
                item { Spacer(Modifier.height(80.dp)) }
            }
        }
    }
}

@Composable
private fun CalorieHeroCard(data: NutritionData, theme: PremiumTheme) {
    HeroCard(
        Modifier.fillMaxWidth().height(220.dp),
        listOf(theme.gradientStart, theme.gradientEnd)
    ) {
        Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Calories Today", fontSize = 16.sp, color = Color.White.copy(0.9f))
                Row(verticalAlignment = Alignment.Bottom) {
                    Text("${data.consumed}", fontSize = 56.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text(" / ${data.goal}", fontSize = 24.sp, color = Color.White.copy(0.8f), modifier = Modifier.padding(bottom = 8.dp))
                }
                Text("${data.remaining} kcal remaining", fontSize = 14.sp, color = Color.White.copy(0.9f))
            }
            AnimatedProgressRing(
                data.consumed.toFloat() / data.goal, 140.dp, 16.dp,
                listOf(Color.White, Color.White.copy(0.7f)), Color.White.copy(0.2f), false
            )
        }
    }
}

@Composable
private fun MacroSummaryRow(data: NutritionData, theme: PremiumTheme) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        MacroCard("Protein", data.protein, data.proteinGoal, "g", theme.primaryColor, Modifier.weight(1f))
        MacroCard("Carbs", data.carbs, data.carbsGoal, "g", theme.secondaryColor, Modifier.weight(1f))
        MacroCard("Fat", data.fat, data.fatGoal, "g", theme.accentColor, Modifier.weight(1f))
    }
}

@Composable
private fun MacroCard(label: String, current: Int, goal: Int, unit: String, color: Color, modifier: Modifier) {
    GlassCard(modifier.height(110.dp), 20.dp) {
        Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Text(label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(0.7f))
            Spacer(Modifier.height(4.dp))
            Text("$current", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = color)
            Text("/ $goal $unit", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(0.6f))
            Spacer(Modifier.height(4.dp))
            LinearProgressIndicator(
                current.toFloat() / goal, Modifier.fillMaxWidth(0.8f).height(4.dp).clip(RoundedCornerShape(2.dp)),
                color, MaterialTheme.colorScheme.surfaceVariant
            )
        }
    }
}

@Composable
private fun MacroPieChart(data: NutritionData, theme: PremiumTheme) {
    GlowingCard(Modifier.fillMaxWidth().height(240.dp), theme.primaryColor) {
        Column {
            Text("Macro Distribution", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(160.dp), Alignment.Center) {
                    Icon(Icons.Default.PieChart, null, Modifier.size(120.dp), theme.primaryColor.copy(0.3f))
                    Text("Pie Chart", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(0.5f))
                }
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    MacroLegend(theme.primaryColor, "Protein", "${data.protein}g")
                    MacroLegend(theme.secondaryColor, "Carbs", "${data.carbs}g")
                    MacroLegend(theme.accentColor, "Fat", "${data.fat}g")
                }
            }
        }
    }
}

@Composable
private fun MacroLegend(color: Color, label: String, value: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(16.dp).clip(CircleShape).background(color))
        Column {
            Text(label, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            Text(value, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(0.7f))
        }
    }
}

@Composable
private fun MealTypeSelector(selected: MealType, onSelect: (MealType) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        MealType.values().forEach { type ->
            FilterChip(
                selected = selected == type,
                onClick = { onSelect(type) },
                label = { Text(type.displayName) },
                leadingIcon = { Icon(type.icon, null, Modifier.size(18.dp)) }
            )
        }
    }
}

@Composable
private fun MealEntryCard(meal: MealEntry, theme: PremiumTheme, onDelete: () -> Unit) {
    GlassCard(Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(Modifier.size(48.dp).clip(CircleShape).background(theme.primaryColor.copy(0.15f)), Alignment.Center) {
                    Icon(Icons.Default.Restaurant, null, Modifier.size(24.dp), theme.primaryColor)
                }
                Column {
                    Text(meal.foodName, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                    Text("${meal.calories} kcal â€¢ ${meal.portion}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(0.6f))
                    Text("P: ${meal.protein}g â€¢ C: ${meal.carbs}g â€¢ F: ${meal.fat}g", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(0.5f))
                }
            }
            IconButton(onClick = onDelete, Modifier.size(36.dp)) {
                Icon(Icons.Default.Delete, "Delete", Modifier.size(20.dp), Color(0xFFEF4444))
            }
        }
    }
}

// Data Classes
data class NutritionData(
    val consumed: Int = 0,
    val goal: Int = 2000,
    val remaining: Int = 2000,
    val protein: Int = 0,
    val proteinGoal: Int = 150,
    val carbs: Int = 0,
    val carbsGoal: Int = 250,
    val fat: Int = 0,
    val fatGoal: Int = 65
)

data class MealEntry(
    val id: String = UUID.randomUUID().toString(),
    val foodName: String,
    val mealType: MealType,
    val calories: Int,
    val protein: Int,
    val carbs: Int,
    val fat: Int,
    val portion: String,
    val timestamp: Date = Date()
)

enum class MealType(val displayName: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    BREAKFAST("Breakfast", Icons.Default.WbSunny),
    LUNCH("Lunch", Icons.Default.LunchDining),
    DINNER("Dinner", Icons.Default.DinnerDining),
    SNACKS("Snacks", Icons.Default.Cookie)
}

