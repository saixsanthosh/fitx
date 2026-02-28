package com.fitx.app.ui.screens

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.layout.ContentScale
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
import com.fitx.app.util.DateUtils
import com.fitx.app.util.FoodImageResolver
import kotlin.math.roundToInt
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import com.google.zxing.client.android.Intents
import com.google.zxing.integration.android.IntentIntegrator
import coil.compose.SubcomposeAsyncImage

@Composable
fun NutritionRoute(
    viewModel: NutritionViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle()
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
    var expandedMealSections by remember { mutableStateOf(mealTypes.toSet()) }
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
    val proteinGoal = (dailyTarget * 0.30 / 4.0)
    val carbsGoal = (dailyTarget * 0.40 / 4.0)
    val fatGoal = (dailyTarget * 0.30 / 9.0)
    val groupedMeals = remember(meals, mealTypes) {
        mealTypes.associateWith { type ->
            meals.filter { it.mealType.equals(type, ignoreCase = true) }
        }
    }

    FitxScreenScaffold(topBar = { ScreenTopBar("My Diary", onBack) }) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                NutritionCommandHeader(
                    dateLabel = DateUtils.formatEpochDay(selectedDate),
                    totalCalories = totalCalories,
                    leftCalories = leftCalories,
                    progress = progress,
                    offlineCount = offlineCount
                )
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    MacroProgressTile(
                        title = "Protein",
                        value = totalProtein,
                        goal = proteinGoal,
                        modifier = Modifier.weight(1f)
                    )
                    MacroProgressTile(
                        title = "Carbs",
                        value = totalCarbs,
                        goal = carbsGoal,
                        modifier = Modifier.weight(1f)
                    )
                    MacroProgressTile(
                        title = "Fat",
                        value = totalFat,
                        goal = fatGoal,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    mealTypes.take(2).forEach { type ->
                        MealSlotCard(
                            title = type,
                            selected = selectedMealType == type,
                            mealCount = groupedMeals[type].orEmpty().size,
                            calories = groupedMeals[type].orEmpty().sumOf { it.calories }.roundToInt(),
                            onClick = { selectedMealType = type },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    mealTypes.drop(2).forEach { type ->
                        MealSlotCard(
                            title = type,
                            selected = selectedMealType == type,
                            mealCount = groupedMeals[type].orEmpty().size,
                            calories = groupedMeals[type].orEmpty().sumOf { it.calories }.roundToInt(),
                            onClick = { selectedMealType = type },
                            modifier = Modifier.weight(1f)
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
                    LazyRow(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
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
                    FilledTonalButton(
                        onClick = { viewModel.copyYesterdayMeals() },
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Copy")
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
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            FoodThumb(
                                foodName = food.name,
                                modifier = Modifier.size(48.dp)
                            )
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
            items(mealTypes, key = { it }) { mealType ->
                val mealGroup = groupedMeals[mealType].orEmpty()
                ExpandableMealSection(
                    mealType = mealType,
                    meals = mealGroup,
                    expanded = expandedMealSections.contains(mealType),
                    onToggleExpanded = {
                        expandedMealSections = if (expandedMealSections.contains(mealType)) {
                            expandedMealSections - mealType
                        } else {
                            expandedMealSections + mealType
                        }
                    },
                    onSelectMealType = { selectedMealType = mealType },
                    onDelete = { entryId -> viewModel.deleteMeal(entryId) }
                )
            }
        }
    }
}

@Composable
private fun NutritionCommandHeader(
    dateLabel: String,
    totalCalories: Double,
    leftCalories: Double,
    progress: Float,
    offlineCount: Int
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 360),
        label = "nutrition_progress"
    )
    val animatedPercent by animateIntAsState(
        targetValue = (animatedProgress * 100f).roundToInt(),
        animationSpec = tween(durationMillis = 360),
        label = "nutrition_percent"
    )
    val animatedCalories by animateIntAsState(
        targetValue = totalCalories.roundToInt(),
        animationSpec = tween(durationMillis = 360),
        label = "nutrition_calories"
    )
    val animatedLeft by animateIntAsState(
        targetValue = leftCalories.roundToInt(),
        animationSpec = tween(durationMillis = 360),
        label = "nutrition_left"
    )
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.93f)),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.24f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text("Nutrition Command", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(dateLabel, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.labelLarge)
                }
                Text(
                    "$animatedPercent%",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "$animatedCalories eaten",
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    "$animatedLeft left",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.72f)
            )
            Text(
                "USDA online + offline backup ($offlineCount)",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun MacroProgressTile(
    title: String,
    value: Double,
    goal: Double,
    modifier: Modifier = Modifier
) {
    val ratio = if (goal <= 0.0) 0f else (value / goal).toFloat().coerceIn(0f, 1f)
    val animatedRatio by animateFloatAsState(
        targetValue = ratio,
        animationSpec = tween(durationMillis = 320),
        label = "macro_ratio_$title"
    )
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
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(title, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.labelMedium)
            Text(
                "${value.roundToInt()}g / ${goal.roundToInt()}g",
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            LinearProgressIndicator(
                progress = { animatedRatio },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.68f)
            )
        }
    }
}

@Composable
private fun MealSlotCard(
    title: String,
    selected: Boolean,
    mealCount: Int,
    calories: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val containerColor by animateColorAsState(
        targetValue = if (selected) {
            MaterialTheme.colorScheme.primary.copy(alpha = 0.16f)
        } else {
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f)
        },
        animationSpec = tween(durationMillis = 220),
        label = "meal_slot_bg_$title"
    )
    val borderColor by animateColorAsState(
        targetValue = if (selected) {
            MaterialTheme.colorScheme.primary.copy(alpha = 0.45f)
        } else {
            MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
        },
        animationSpec = tween(durationMillis = 220),
        label = "meal_slot_border_$title"
    )
    Card(
        modifier = modifier.animateContentSize(animationSpec = tween(220)),
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = BorderStroke(1.dp, borderColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(title, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Text("$calories kcal", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelLarge)
            Text("$mealCount items", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.labelMedium)
        }
    }
}

@Composable
private fun ExpandableMealSection(
    mealType: String,
    meals: List<MealEntry>,
    expanded: Boolean,
    onToggleExpanded: () -> Unit,
    onSelectMealType: () -> Unit,
    onDelete: (Long) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(animationSpec = tween(durationMillis = 220)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.92f)),
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
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(mealType, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    Text(
                        "${meals.sumOf { it.calories }.roundToInt()} kcal  |  ${meals.size} items",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
                FilledTonalButton(
                    onClick = onSelectMealType,
                    shape = RoundedCornerShape(12.dp)
                ) { Text("Select") }
                IconButton(onClick = onToggleExpanded) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = if (expanded) "Collapse" else "Expand"
                    )
                }
            }
            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn(animationSpec = tween(durationMillis = 180)) +
                    expandVertically(animationSpec = tween(durationMillis = 220)),
                exit = fadeOut(animationSpec = tween(durationMillis = 140)) +
                    shrinkVertically(animationSpec = tween(durationMillis = 180))
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (meals.isEmpty()) {
                        Text(
                            "No entries in $mealType yet.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        meals.forEach { meal ->
                            MealRowCompact(meal = meal, onDelete = { onDelete(meal.mealEntryId) })
                        }
                    }
                }
            }
        }
    }
}
@Composable
private fun MealRowCompact(
    meal: MealEntry,
    onDelete: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FoodThumb(
                foodName = meal.foodName,
                modifier = Modifier.size(44.dp)
            )
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(meal.foodName, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                Text(
                    "${meal.grams.roundToInt()}g  |  ${meal.calories.roundToInt()} kcal  |  P ${meal.protein.roundToInt()} C ${meal.carbs.roundToInt()} F ${meal.fat.roundToInt()}",
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
private fun FoodThumb(
    foodName: String,
    modifier: Modifier = Modifier
) {
    val imageUrl = remember(foodName) { FoodImageResolver.imageUrlFor(foodName) }
    val shape = RoundedCornerShape(12.dp)
    if (imageUrl != null) {
        SubcomposeAsyncImage(
            model = imageUrl,
            contentDescription = foodName,
            modifier = modifier
                .clip(shape)
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)),
            contentScale = ContentScale.Crop,
            loading = {
                FoodThumbFallback(foodName = foodName, shape = shape)
            },
            error = {
                FoodThumbFallback(foodName = foodName, shape = shape)
            }
        )
    } else {
        FoodThumbFallback(foodName = foodName, modifier = modifier, shape = shape)
    }
}

@Composable
private fun FoodThumbFallback(
    foodName: String,
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(12.dp)
) {
    Surface(
        modifier = modifier,
        shape = shape,
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = foodName.firstOrNull()?.uppercase() ?: "F",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
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

