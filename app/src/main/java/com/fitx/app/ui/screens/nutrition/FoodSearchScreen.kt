package com.fitx.app.ui.screens.nutrition

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fitx.app.ui.theme.premium.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Feature 10: Food Search with USDA API - UNBEATABLE DESIGN
 * Advanced food search with stunning UI, favorites, and quick add
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodSearchScreen(
    theme: PremiumTheme = PremiumThemes.ElectricBlue,
    searchResults: List<FoodItem> = emptyList(),
    recentSearches: List<String> = emptyList(),
    favoriteFoods: List<FoodItem> = emptyList(),
    isLoading: Boolean = false,
    onSearch: (String) -> Unit = {},
    onFoodClick: (FoodItem) -> Unit = {},
    onAddToFavorites: (FoodItem) -> Unit = {},
    onQuickAdd: (FoodItem) -> Unit = {},
    onBackPressed: () -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    var showFavorites by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    
    // Debounced search
    LaunchedEffect(searchQuery) {
        if (searchQuery.length >= 3) {
            delay(500)
            onSearch(searchQuery)
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Food Search", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showFavorites = !showFavorites }) {
                        Icon(
                            if (showFavorites) Icons.Default.Search else Icons.Default.Favorite,
                            if (showFavorites) "Search" else "Favorites",
                            tint = theme.primaryColor
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Box(Modifier.fillMaxSize()) {
            FloatingParticles(Modifier.fillMaxSize(), theme.primaryColor.copy(alpha = 0.1f))
            
            Column(Modifier.fillMaxSize().padding(padding)) {
                // Search Bar
                if (!showFavorites) {
                    SearchBar(
                        query = searchQuery,
                        onQueryChange = { searchQuery = it },
                        onClear = { searchQuery = "" },
                        theme = theme,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                
                LazyColumn(
                    Modifier.fillMaxSize().padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (showFavorites) {
                        // Favorites Section
                        item {
                            Text("Favorite Foods", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        }
                        
                        if (favoriteFoods.isEmpty()) {
                            item {
                                EmptyFavoritesState(theme)
                            }
                        } else {
                            items(favoriteFoods) { food ->
                                FoodItemCard(
                                    food, theme, true,
                                    onClick = { onFoodClick(food) },
                                    onFavorite = { onAddToFavorites(food) },
                                    onQuickAdd = { onQuickAdd(food) }
                                )
                            }
                        }
                    } else {
                        // Search Results
                        if (searchQuery.isEmpty()) {
                            item {
                                QuickAccessSection(recentSearches, favoriteFoods, theme) {
                                    searchQuery = it
                                }
                            }
                        } else if (isLoading) {
                            item {
                                LoadingState(theme)
                            }
                        } else if (searchResults.isEmpty() && searchQuery.length >= 3) {
                            item {
                                NoResultsState(theme)
                            }
                        } else {
                            item {
                                Text("${searchResults.size} Results", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            }
                            
                            items(searchResults) { food ->
                                FoodItemCard(
                                    food, theme, favoriteFoods.contains(food),
                                    onClick = { onFoodClick(food) },
                                    onFavorite = { onAddToFavorites(food) },
                                    onQuickAdd = { onQuickAdd(food) }
                                )
                            }
                        }
                    }
                    
                    item { Spacer(Modifier.height(80.dp)) }
                }
            }
        }
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClear: () -> Unit,
    theme: PremiumTheme,
    modifier: Modifier = Modifier
) {
    GlowingCard(modifier.fillMaxWidth(), theme.primaryColor.copy(alpha = 0.3f)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Search, null, Modifier.size(24.dp), theme.primaryColor)
            
            TextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text("Search foods...") },
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = theme.primaryColor
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search)
            )
            
            if (query.isNotEmpty()) {
                IconButton(onClick = onClear) {
                    Icon(Icons.Default.Close, "Clear", Modifier.size(20.dp))
                }
            }
        }
    }
}

@Composable
private fun QuickAccessSection(
    recentSearches: List<String>,
    favoriteFoods: List<FoodItem>,
    theme: PremiumTheme,
    onSearchClick: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Recent Searches
        if (recentSearches.isNotEmpty()) {
            Text("Recent Searches", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            
            recentSearches.take(5).forEach { search ->
                GlassCard(Modifier.fillMaxWidth().clickable { onSearchClick(search) }) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.History, null, Modifier.size(20.dp), theme.primaryColor)
                        Text(search, fontSize = 16.sp)
                    }
                }
            }
        }
        
        // Quick Add Favorites
        if (favoriteFoods.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            Text("Quick Add Favorites", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text("Tap to add to today's meals", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface.copy(0.7f))
        }
    }
}

@Composable
private fun FoodItemCard(
    food: FoodItem,
    theme: PremiumTheme,
    isFavorite: Boolean,
    onClick: () -> Unit,
    onFavorite: () -> Unit,
    onQuickAdd: () -> Unit
) {
    GlowingCard(
        Modifier.fillMaxWidth().clickable(onClick = onClick),
        if (isFavorite) theme.accentColor.copy(0.2f) else Color.Transparent
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            // Header
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        Modifier.size(56.dp).clip(CircleShape).background(
                            Brush.linearGradient(listOf(theme.primaryColor, theme.secondaryColor))
                        ), Alignment.Center
                    ) {
                        Icon(Icons.Default.Restaurant, null, Modifier.size(28.dp), Color.White)
                    }
                    Column(Modifier.weight(1f)) {
                        Text(food.name, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text(food.brand, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(0.6f))
                    }
                }
                
                IconButton(onClick = onFavorite) {
                    Icon(
                        if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        "Favorite",
                        Modifier.size(24.dp),
                        if (isFavorite) theme.accentColor else MaterialTheme.colorScheme.onSurface.copy(0.5f)
                    )
                }
            }
            
            // Nutrition Info
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                NutritionBadge("${food.calories} kcal", theme.primaryColor, Modifier.weight(1f))
                NutritionBadge("P: ${food.protein}g", theme.secondaryColor, Modifier.weight(1f))
                NutritionBadge("C: ${food.carbs}g", theme.accentColor, Modifier.weight(1f))
                NutritionBadge("F: ${food.fat}g", Color(0xFFFBBF24), Modifier.weight(1f))
            }
            
            // Serving Size
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Serving: ${food.servingSize}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(0.6f))
                
                PremiumButton(
                    text = "Quick Add",
                    onClick = onQuickAdd,
                    modifier = Modifier.height(36.dp),
                    gradient = listOf(theme.primaryColor, theme.secondaryColor),
                    icon = { Icon(Icons.Default.Add, null, Modifier.size(16.dp), Color.White) }
                )
            }
        }
    }
}

@Composable
private fun NutritionBadge(text: String, color: Color, modifier: Modifier) {
    Box(
        modifier.clip(RoundedCornerShape(8.dp)).background(color.copy(0.15f)).padding(vertical = 6.dp),
        Alignment.Center
    ) {
        Text(text, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = color)
    }
}

@Composable
private fun LoadingState(theme: PremiumTheme) {
    Box(Modifier.fillMaxWidth().height(200.dp), Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
            CircularProgressIndicator(color = theme.primaryColor)
            Text("Searching foods...", fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface.copy(0.7f))
        }
    }
}

@Composable
private fun NoResultsState(theme: PremiumTheme) {
    GlassCard(Modifier.fillMaxWidth().height(200.dp)) {
        Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Icon(Icons.Default.SearchOff, null, Modifier.size(64.dp), theme.primaryColor.copy(0.5f))
            Spacer(Modifier.height(16.dp))
            Text("No results found", fontSize = 18.sp, fontWeight = FontWeight.Medium)
            Text("Try a different search term", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface.copy(0.7f))
        }
    }
}

@Composable
private fun EmptyFavoritesState(theme: PremiumTheme) {
    GlassCard(Modifier.fillMaxWidth().height(200.dp)) {
        Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Icon(Icons.Default.FavoriteBorder, null, Modifier.size(64.dp), theme.primaryColor.copy(0.5f))
            Spacer(Modifier.height(16.dp))
            Text("No favorite foods yet", fontSize = 18.sp, fontWeight = FontWeight.Medium)
            Text("Add foods to favorites for quick access", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface.copy(0.7f))
        }
    }
}

// Data Classes
data class FoodItem(
    val id: String,
    val name: String,
    val brand: String,
    val calories: Int,
    val protein: Int,
    val carbs: Int,
    val fat: Int,
    val servingSize: String,
    val servingUnit: String = "g"
)

