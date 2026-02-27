package com.fitx.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fitx.app.domain.model.MealEntry
import com.fitx.app.ui.viewmodel.NutritionViewModel
import kotlin.math.roundToInt
import androidx.compose.foundation.text.KeyboardOptions

@Composable
fun NutritionRoute(
    viewModel: NutritionViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val meals by viewModel.meals.collectAsStateWithLifecycle()
    val customFoods by viewModel.customFoods.collectAsStateWithLifecycle()
    val results by viewModel.results.collectAsStateWithLifecycle()
    val loading by viewModel.loading.collectAsStateWithLifecycle()
    val loadingMoreFoods by viewModel.loadingMoreFoods.collectAsStateWithLifecycle()
    val canLoadMoreFoods by viewModel.canLoadMoreFoods.collectAsStateWithLifecycle()
    val searchMessage by viewModel.searchMessage.collectAsStateWithLifecycle()
    val offlineCount = viewModel.offlineCatalogCount

    var query by remember { mutableStateOf("") }
    var gramsInput by remember { mutableStateOf("100") }
    var selectedMealType by remember { mutableStateOf("Breakfast") }
    var customName by remember { mutableStateOf("") }
    var customCalories by remember { mutableStateOf("0") }
    var customProtein by remember { mutableStateOf("0") }
    var customCarbs by remember { mutableStateOf("0") }
    var customFat by remember { mutableStateOf("0") }
    var servingLabel by remember { mutableStateOf("1 serving") }
    var servingGrams by remember { mutableStateOf("100") }
    val quickQueries = remember {
        listOf("All", "egg", "chicken", "rice", "oats", "banana", "paneer", "tofu", "milk")
    }
    val mealTypes = remember { listOf("Breakfast", "Lunch", "Snack", "Dinner") }

    val totalCalories = meals.sumOf { it.calories }
    val totalProtein = meals.sumOf { it.protein }
    val totalCarbs = meals.sumOf { it.carbs }
    val totalFat = meals.sumOf { it.fat }
    val dailyTarget = 2200.0
    val leftCalories = (dailyTarget - totalCalories).coerceAtLeast(0.0)
    val progress = (totalCalories / dailyTarget).toFloat().coerceIn(0f, 1f)

    LaunchedEffect(Unit) {
        viewModel.loadAllFoods(reset = true)
    }

    FitxScreenScaffold(topBar = { ScreenTopBar("My Diary", onBack) }) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 6.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                brush = Brush.horizontalGradient(
                                    listOf(
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.18f),
                                        MaterialTheme.colorScheme.tertiary.copy(alpha = 0.14f)
                                    )
                                )
                            )
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Mediterranean Diet", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text(
                                "${totalCalories.roundToInt()} eaten  |  ${leftCalories.roundToInt()} left",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                "USDA online catalog (fallback offline: $offlineCount)",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(progress = { progress })
                            Text("${(progress * 100).roundToInt()}%", style = MaterialTheme.typography.labelLarge)
                        }
                    }
                }
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    MacroTile(
                        title = "Protein",
                        value = "${totalProtein.roundToInt()} g",
                        accent = Color(0xFF3B82F6),
                        modifier = Modifier.weight(1f)
                    )
                    MacroTile(
                        title = "Carbs",
                        value = "${totalCarbs.roundToInt()} g",
                        accent = Color(0xFFF97316),
                        modifier = Modifier.weight(1f)
                    )
                    MacroTile(
                        title = "Fat",
                        value = "${totalFat.roundToInt()} g",
                        accent = Color(0xFFEC4899),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(mealTypes) { type ->
                        AssistChip(
                            onClick = { selectedMealType = type },
                            label = { Text(type) }
                        )
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = query,
                        onValueChange = { query = it },
                        label = { Text("Search food") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    Button(
                        onClick = { viewModel.searchFoods(query) },
                        modifier = Modifier.height(56.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Text("Find", modifier = Modifier.padding(start = 4.dp))
                    }
                }
            }
            item {
                OutlinedTextField(
                    value = gramsInput,
                    onValueChange = { gramsInput = it.filter { c -> c.isDigit() || c == '.' } },
                    label = { Text("Grams eaten (g)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(quickQueries) { quick ->
                        AssistChip(
                            onClick = {
                                if (quick == "All") {
                                    query = ""
                                    viewModel.loadAllFoods(reset = true)
                                } else {
                                    query = quick
                                    viewModel.searchFoods(quick)
                                }
                            },
                            label = { Text(quick) }
                        )
                    }
                }
            }

            item {
                Text("Custom Foods", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.92f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = customName,
                            onValueChange = { customName = it },
                            label = { Text("Food name") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = customCalories,
                                onValueChange = { customCalories = it.filter { c -> c.isDigit() || c == '.' } },
                                label = { Text("Kcal /100g") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = customProtein,
                                onValueChange = { customProtein = it.filter { c -> c.isDigit() || c == '.' } },
                                label = { Text("P /100g") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = customCarbs,
                                onValueChange = { customCarbs = it.filter { c -> c.isDigit() || c == '.' } },
                                label = { Text("C /100g") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = customFat,
                                onValueChange = { customFat = it.filter { c -> c.isDigit() || c == '.' } },
                                label = { Text("F /100g") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = servingLabel,
                                onValueChange = { servingLabel = it },
                                label = { Text("Serving label") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = servingGrams,
                                onValueChange = { servingGrams = it.filter { c -> c.isDigit() || c == '.' } },
                                label = { Text("Serving grams") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                        }
                        Button(
                            onClick = {
                                viewModel.saveCustomFood(
                                    name = customName,
                                    caloriesPer100g = customCalories.toDoubleOrNull() ?: 0.0,
                                    proteinPer100g = customProtein.toDoubleOrNull() ?: 0.0,
                                    carbsPer100g = customCarbs.toDoubleOrNull() ?: 0.0,
                                    fatPer100g = customFat.toDoubleOrNull() ?: 0.0,
                                    servingLabel = servingLabel,
                                    servingGrams = servingGrams.toDoubleOrNull()
                                )
                                customName = ""
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Save Custom Food")
                        }
                    }
                }
            }
            if (customFoods.isNotEmpty()) {
                item {
                    Text("Saved Custom Foods", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                }
            }
            items(customFoods, key = { it.id }) { food ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(food.name, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        Text(
                            "Per 100g  |  ${food.caloriesPer100g.roundToInt()} kcal  |  P ${food.proteinPer100g.roundToInt()} C ${food.carbsPer100g.roundToInt()} F ${food.fatPer100g.roundToInt()}",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodySmall
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = {
                                    val grams = gramsInput.toDoubleOrNull() ?: 100.0
                                    viewModel.addCustomFood(food, selectedMealType, grams)
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Add ${gramsInput.ifBlank { "100" }}g")
                            }
                            FilledTonalButton(
                                onClick = {
                                    val grams = gramsInput.toDoubleOrNull() ?: 100.0
                                    viewModel.saveServingPreset(food.id, "${grams.roundToInt()}g", grams)
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Save Serving")
                            }
                        }
                        if (food.servings.isNotEmpty()) {
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(food.servings) { serving ->
                                    AssistChip(
                                        onClick = { viewModel.addCustomFoodServing(food, selectedMealType, serving) },
                                        label = { Text("${serving.label} (${serving.grams.roundToInt()}g)") }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            if (loading) {
                item { LinearProgressIndicator(modifier = Modifier.fillMaxWidth()) }
            }
            if (!searchMessage.isNullOrBlank()) {
                item {
                    Text(
                        text = searchMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            item {
                Text("Search Results", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            if (results.isEmpty() && query.isNotBlank() && !loading) {
                item {
                    EmptyCard("No matching foods. Try: egg, oats, rice, paneer, tofu.")
                }
            }
            items(results, key = { it.fdcId }) { food ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.93f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(food.name, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                            Button(onClick = {
                                val grams = gramsInput.toDoubleOrNull() ?: 100.0
                                viewModel.addFood(food, selectedMealType, grams)
                            }) {
                                Text("Add")
                            }
                        }
                        Text(
                            "Base ${food.baseGrams.roundToInt()}g values",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.labelMedium
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            TinyBadge(Icons.Default.LocalFireDepartment, "${food.calories.roundToInt()} kcal")
                            TinyBadge(Icons.Default.WaterDrop, "P ${"%.1f".format(food.protein)}")
                            TinyBadge(Icons.Default.WaterDrop, "C ${"%.1f".format(food.carbs)}")
                            TinyBadge(Icons.Default.WaterDrop, "F ${"%.1f".format(food.fat)}")
                        }
                    }
                }
            }
            if (query.isBlank()) {
                item {
                    Button(
                        onClick = { viewModel.loadMoreFoods() },
                        enabled = canLoadMoreFoods && !loadingMoreFoods,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (loadingMoreFoods) "Loading..." else "Load More Foods")
                    }
                }
            }

            item {
                Text("Meals Today", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            if (meals.isEmpty()) {
                item { EmptyCard("No meals added yet. Search and tap Add.") }
            }
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    val groups = meals.groupBy { it.mealType }
                    items(groups.keys.toList()) { mealType ->
                        val group = groups[mealType].orEmpty()
                        val kcal = group.sumOf { it.calories }.roundToInt()
                        MealTypeSummaryCard(mealType = mealType, mealCount = group.size, calories = kcal)
                    }
                }
            }
            items(meals, key = { it.mealEntryId }) { meal ->
                MealRow(
                    meal = meal,
                    onDelete = { viewModel.deleteMeal(meal.mealEntryId) }
                )
            }
        }
    }
}

@Composable
private fun MacroTile(
    title: String,
    value: String,
    accent: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            accent.copy(alpha = 0.16f),
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f)
                        )
                    )
                )
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(title, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.labelMedium)
            Text(value, color = MaterialTheme.colorScheme.onSurface, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun TinyBadge(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Surface(
        shape = RoundedCornerShape(50),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.tertiary)
            Text(text, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
private fun MealTypeSummaryCard(mealType: String, mealCount: Int, calories: Int) {
    val accent = when (mealType.lowercase()) {
        "breakfast" -> Color(0xFFF97316)
        "lunch" -> Color(0xFF3B82F6)
        "snack" -> Color(0xFFEC4899)
        "dinner" -> Color(0xFF8B5CF6)
        else -> MaterialTheme.colorScheme.primary
    }
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        listOf(
                            accent.copy(alpha = 0.82f),
                            accent.copy(alpha = 0.55f)
                        )
                    )
                )
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Text(mealType, color = Color.White, fontWeight = FontWeight.Bold)
            Text("$calories kcal", color = Color.White, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text("$mealCount items", color = Color.White.copy(alpha = 0.9f), style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
private fun MealRow(
    meal: MealEntry,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.92f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(meal.foodName, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                Text(
                    "${meal.mealType}  |  ${meal.grams.roundToInt()}g  |  ${"%.1f".format(meal.calories)} kcal  |  P ${"%.1f".format(meal.protein)} C ${"%.1f".format(meal.carbs)} F ${"%.1f".format(meal.fat)}",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete")
            }
        }
    }
}

@Composable
private fun EmptyCard(text: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.86f))
    ) {
        Text(text, modifier = Modifier.padding(12.dp), color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
