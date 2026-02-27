package com.fitx.app.ui.screens

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fitx.app.domain.model.FoodItem
import com.fitx.app.domain.model.MealEntry
import com.fitx.app.ui.viewmodel.NutritionViewModel
import kotlin.math.roundToInt
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import com.google.zxing.client.android.Intents
import com.google.zxing.integration.android.IntentIntegrator

@Composable
fun NutritionRoute(
    viewModel: NutritionViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val meals by viewModel.meals.collectAsStateWithLifecycle()
    val customFoods by viewModel.customFoods.collectAsStateWithLifecycle()
    val favoriteFoods by viewModel.favoriteFoods.collectAsStateWithLifecycle()
    val recentFoods by viewModel.recentFoods.collectAsStateWithLifecycle()
    val results by viewModel.results.collectAsStateWithLifecycle()
    val loading by viewModel.loading.collectAsStateWithLifecycle()
    val loadingMoreFoods by viewModel.loadingMoreFoods.collectAsStateWithLifecycle()
    val canLoadMoreFoods by viewModel.canLoadMoreFoods.collectAsStateWithLifecycle()
    val searchMessage by viewModel.searchMessage.collectAsStateWithLifecycle()
    val actionMessage by viewModel.actionMessage.collectAsStateWithLifecycle()
    val offlineCount = viewModel.offlineCatalogCount
    val keyboardController = LocalSoftwareKeyboardController.current
    val context = LocalContext.current
    val activity = remember(context) { context.findActivity() }
    val favoriteIdentities = remember(favoriteFoods) {
        favoriteFoods.map { "${it.fdcId}_${it.name.trim().lowercase()}" }.toSet()
    }

    var query by remember { mutableStateOf("") }
    var barcodeInput by remember { mutableStateOf("") }
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
    val barcodeScanLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val scanned = result.data?.getStringExtra(Intents.Scan.RESULT).orEmpty().trim()
        if (scanned.isNotBlank()) {
            barcodeInput = scanned
            viewModel.findFoodByBarcode(scanned)
        }
    }

    val totalCalories = meals.sumOf { it.calories }
    val totalProtein = meals.sumOf { it.protein }
    val totalCarbs = meals.sumOf { it.carbs }
    val totalFat = meals.sumOf { it.fat }
    val dailyTarget = 2200.0
    val leftCalories = (dailyTarget - totalCalories).coerceAtLeast(0.0)
    val progress = (totalCalories / dailyTarget).toFloat().coerceIn(0f, 1f)

    FitxScreenScaffold(topBar = { ScreenTopBar("My Diary", onBack) }) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.92f)),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.24f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
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
                            CircularProgressIndicator(
                                progress = { progress },
                                color = MaterialTheme.colorScheme.primary,
                                trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                            )
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
                        modifier = Modifier.weight(1f)
                    )
                    MacroTile(
                        title = "Carbs",
                        value = "${totalCarbs.roundToInt()} g",
                        modifier = Modifier.weight(1f)
                    )
                    MacroTile(
                        title = "Fat",
                        value = "${totalFat.roundToInt()} g",
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(mealTypes) { type ->
                        FilterChip(
                            selected = selectedMealType == type,
                            onClick = { selectedMealType = type },
                            label = { Text(type) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.22f),
                                selectedLabelColor = MaterialTheme.colorScheme.primary,
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.82f)
                            )
                        )
                    }
                }
            }
            item {
                FilledTonalButton(
                    onClick = { viewModel.copyYesterdayMeals() },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Copy Yesterday Meals")
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
                        onValueChange = {
                            query = it
                            viewModel.onSearchQueryChanged(it)
                        },
                        label = { Text("Search food") },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Search
                        ),
                        keyboardActions = KeyboardActions(
                            onSearch = {
                                viewModel.searchFoodsNow(query)
                                keyboardController?.hide()
                            }
                        ),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    Button(
                        onClick = {
                            viewModel.searchFoodsNow(query)
                            keyboardController?.hide()
                        },
                        modifier = Modifier.height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Text("Find", modifier = Modifier.padding(start = 4.dp))
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
                        value = barcodeInput,
                        onValueChange = { barcodeInput = it.filter { c -> c.isDigit() } },
                        label = { Text("Barcode (EAN/UPC)") },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Search
                        ),
                        keyboardActions = KeyboardActions(
                            onSearch = {
                                viewModel.findFoodByBarcode(barcodeInput)
                                keyboardController?.hide()
                            }
                        ),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    Button(
                        onClick = {
                            viewModel.findFoodByBarcode(barcodeInput)
                            keyboardController?.hide()
                        },
                        modifier = Modifier.height(56.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Find")
                    }
                    Button(
                        onClick = {
                            val hostActivity = activity ?: return@Button
                            val intent = IntentIntegrator(hostActivity)
                                .setDesiredBarcodeFormats(IntentIntegrator.PRODUCT_CODE_TYPES)
                                .setPrompt("Scan food barcode")
                                .setBeepEnabled(false)
                                .setOrientationLocked(false)
                                .createScanIntent()
                            barcodeScanLauncher.launch(intent)
                        },
                        enabled = activity != null,
                        modifier = Modifier.height(56.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null)
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
                                    viewModel.onSearchQueryChanged("")
                                    viewModel.loadAllFoods(reset = true)
                                } else {
                                    query = quick
                                    viewModel.onSearchQueryChanged(quick)
                                    viewModel.searchFoodsNow(quick)
                                }
                                keyboardController?.hide()
                            },
                            label = { Text(quick) },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.82f),
                                labelColor = MaterialTheme.colorScheme.onSurface
                            )
                        )
                    }
                }
            }

            if (favoriteFoods.isNotEmpty()) {
                item {
                    Text("Favorites", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                }
                item {
                    QuickFoodChips(
                        foods = favoriteFoods.take(12),
                        onAdd = { food ->
                            val grams = gramsInput.toDoubleOrNull() ?: 100.0
                            viewModel.addFood(food, selectedMealType, grams)
                        }
                    )
                }
            }

            if (recentFoods.isNotEmpty()) {
                item {
                    Text("Recent", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                }
                item {
                    QuickFoodChips(
                        foods = recentFoods.take(12),
                        onAdd = { food ->
                            val grams = gramsInput.toDoubleOrNull() ?: 100.0
                            viewModel.addFood(food, selectedMealType, grams)
                        }
                    )
                }
            }

            item {
                Text("Custom Foods", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.92f)),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.22f))
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
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.18f))
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
            if (!actionMessage.isNullOrBlank()) {
                item {
                    Text(
                        text = actionMessage!!,
                        color = MaterialTheme.colorScheme.primary,
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
                val isFavorite = favoriteIdentities.contains("${food.fdcId}_${food.name.trim().lowercase()}")
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.93f)),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
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
                            IconButton(onClick = { viewModel.toggleFavoriteFood(food) }) {
                                Icon(
                                    imageVector = if (isFavorite) Icons.Filled.Star else Icons.Outlined.StarBorder,
                                    contentDescription = if (isFavorite) "Remove favorite" else "Mark favorite",
                                    tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
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
                        if (!food.barcode.isNullOrBlank()) {
                            Text(
                                "Barcode ${food.barcode}",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
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
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f)),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(title, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.labelMedium)
            Text(value, color = MaterialTheme.colorScheme.onSurface, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        }
    }
}

private tailrec fun Context.findActivity(): Activity? {
    return when (this) {
        is Activity -> this
        is ContextWrapper -> baseContext.findActivity()
        else -> null
    }
}

@Composable
private fun QuickFoodChips(
    foods: List<FoodItem>,
    onAdd: (FoodItem) -> Unit
) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(foods, key = { "${it.fdcId}_${it.name}" }) { food ->
            AssistChip(
                onClick = { onAdd(food) },
                label = {
                    Text(food.name.take(30))
                },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.84f),
                    labelColor = MaterialTheme.colorScheme.onSurface
                )
            )
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
            Icon(icon, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.primary)
            Text(text, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
private fun MealTypeSummaryCard(mealType: String, mealCount: Int, calories: Int) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.18f))
    ) {
        Column(
            modifier = Modifier
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Text(mealType, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
            Text(
                "$calories kcal",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                "$mealCount items",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelLarge
            )
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
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.92f)),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
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
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.86f)),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.16f))
    ) {
        Text(text, modifier = Modifier.padding(12.dp), color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
