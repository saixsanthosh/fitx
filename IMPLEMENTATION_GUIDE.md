# 🚀 Fitx Premium Implementation Guide

This guide will help you integrate all premium features into your existing Fitx application.

---

## 📋 Table of Contents
1. [Setup & Dependencies](#setup--dependencies)
2. [Theme Engine Integration](#theme-engine-integration)
3. [Premium Components Usage](#premium-components-usage)
4. [Screen Integration](#screen-integration)
5. [Animation System](#animation-system)
6. [Data Layer](#data-layer)
7. [Testing](#testing)

---

## 1. Setup & Dependencies

### Step 1: Sync Gradle
The dependencies have already been added to `app/build.gradle.kts`. Sync your project:

```bash
./gradlew build
```

### Step 2: Verify Dependencies
Ensure these libraries are properly imported:
- Lottie Compose
- Accompanist (SystemUI, Permissions, Pager)
- MPAndroidChart
- Compose Charts
- Shimmer
- Cloudy (Blur effects)

---

## 2. Theme Engine Integration

### Step 1: Create Theme Manager

Create `ThemeManager.kt` in your data layer:

```kotlin
package com.fitx.app.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreference
import androidx.datastore.preferences.core.booleanPreference
import androidx.datastore.preferences.preferencesDataStore
import com.fitx.app.ui.theme.premium.PremiumTheme
import com.fitx.app.ui.theme.premium.PremiumThemes
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "theme_preferences")

class ThemeManager(private val context: Context) {
    
    private val THEME_KEY = stringPreference("selected_theme")
    private val DARK_MODE_KEY = booleanPreference("dark_mode")
    
    val selectedTheme: Flow<PremiumTheme> = context.dataStore.data.map { prefs ->
        val themeId = prefs[THEME_KEY] ?: PremiumThemes.ElectricBlue.id
        PremiumThemes.AllThemes.find { it.id == themeId } ?: PremiumThemes.ElectricBlue
    }
    
    val isDarkMode: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[DARK_MODE_KEY] ?: true
    }
    
    suspend fun setTheme(theme: PremiumTheme) {
        context.dataStore.edit { prefs ->
            prefs[THEME_KEY] = theme.id
        }
    }
    
    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[DARK_MODE_KEY] = enabled
        }
    }
}
```

### Step 2: Update Main Theme File

Update your existing `Theme.kt`:

```kotlin
package com.fitx.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import com.fitx.app.data.preferences.ThemeManager
import com.fitx.app.ui.theme.premium.PremiumTheme
import com.fitx.app.ui.theme.premium.PremiumThemes
import com.fitx.app.ui.theme.premium.animatedColorScheme

@Composable
fun FitxTheme(
    themeManager: ThemeManager? = null,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val manager = themeManager ?: ThemeManager(context)
    
    val selectedTheme by manager.selectedTheme.collectAsState(
        initial = PremiumThemes.ElectricBlue
    )
    val isDarkMode by manager.isDarkMode.collectAsState(initial = true)
    
    val colorScheme = animatedColorScheme(
        theme = selectedTheme,
        isDark = isDarkMode
    )
    
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
```

---

## 3. Premium Components Usage

### Using GlassCard

```kotlin
import com.fitx.app.ui.theme.premium.GlassCard

@Composable
fun MyScreen() {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 24.dp,
        glassOpacity = 0.15f
    ) {
        Text("Content goes here")
    }
}
```

### Using GlowingCard

```kotlin
import com.fitx.app.ui.theme.premium.GlowingCard

@Composable
fun MyFeatureCard() {
    GlowingCard(
        modifier = Modifier.fillMaxWidth(),
        glowColor = MaterialTheme.colorScheme.primary
    ) {
        Column {
            Text("Feature Title")
            Text("Feature Description")
        }
    }
}
```

### Using PremiumButton

```kotlin
import com.fitx.app.ui.theme.premium.PremiumButton

@Composable
fun MyActionButton() {
    PremiumButton(
        text = "Start Workout",
        onClick = { /* Handle click */ },
        modifier = Modifier.fillMaxWidth(),
        gradient = listOf(
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.secondary
        ),
        icon = {
            Icon(Icons.Default.PlayArrow, contentDescription = null)
        }
    )
}
```

### Using AnimatedProgressRing

```kotlin
import com.fitx.app.ui.components.premium.AnimatedProgressRing

@Composable
fun MyProgressIndicator() {
    AnimatedProgressRing(
        progress = 0.75f, // 75%
        size = 200.dp,
        strokeWidth = 20.dp,
        gradientColors = listOf(
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.secondary
        )
    )
}
```

---

## 4. Screen Integration

### Step 1: Update MainActivity

```kotlin
package com.fitx.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.fitx.app.data.preferences.ThemeManager
import com.fitx.app.ui.navigation.AppNavigation
import com.fitx.app.ui.theme.FitxTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    private lateinit var themeManager: ThemeManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        themeManager = ThemeManager(this)
        
        setContent {
            FitxTheme(themeManager = themeManager) {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    AppNavigation(themeManager = themeManager)
                }
            }
        }
    }
}
```

### Step 2: Update Navigation

Add premium screens to your navigation:

```kotlin
package com.fitx.app.ui.navigation

import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.fitx.app.data.preferences.ThemeManager
import com.fitx.app.ui.screens.premium.*
import kotlinx.coroutines.launch

@Composable
fun AppNavigation(themeManager: ThemeManager) {
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()
    
    val currentTheme by themeManager.selectedTheme.collectAsState(
        initial = PremiumThemes.ElectricBlue
    )
    val isDarkMode by themeManager.isDarkMode.collectAsState(initial = true)
    
    NavHost(
        navController = navController,
        startDestination = "dashboard"
    ) {
        composable("dashboard") {
            PremiumDashboardScreen(
                currentTheme = currentTheme
            )
        }
        
        composable("theme_settings") {
            ThemeSettingsScreen(
                currentTheme = currentTheme,
                onThemeSelected = { theme ->
                    scope.launch {
                        themeManager.setTheme(theme)
                    }
                },
                isDarkMode = isDarkMode,
                onDarkModeToggle = { enabled ->
                    scope.launch {
                        themeManager.setDarkMode(enabled)
                    }
                },
                onBackPressed = { navController.popBackStack() }
            )
        }
        
        composable("live_activity") {
            LiveActivityScreen(
                activityType = ActivityType.WALKING,
                theme = currentTheme,
                onStopActivity = { navController.popBackStack() }
            )
        }
        
        // Add your existing screens here
    }
}
```

---

## 5. Animation System

### Using Pulse Animation

```kotlin
import com.fitx.app.ui.theme.premium.rememberPulseAnimation
import com.fitx.app.ui.theme.premium.pulseEffect

@Composable
fun PulsingElement() {
    val scale = rememberPulseAnimation(
        minScale = 0.95f,
        maxScale = 1.05f,
        durationMillis = 1000
    )
    
    Box(
        modifier = Modifier
            .size(100.dp)
            .pulseEffect(scale)
    ) {
        // Your content
    }
}
```

### Using Floating Animation

```kotlin
import com.fitx.app.ui.theme.premium.rememberFloatingAnimation
import com.fitx.app.ui.theme.premium.floatingEffect

@Composable
fun FloatingCard() {
    val offsetY = rememberFloatingAnimation(
        distance = 8.dp,
        durationMillis = 2000
    )
    
    Card(
        modifier = Modifier.floatingEffect(offsetY)
    ) {
        // Your content
    }
}
```

### Using Particle Effects

```kotlin
import com.fitx.app.ui.theme.premium.ParticleExplosion

@Composable
fun CelebrationScreen() {
    var showCelebration by remember { mutableStateOf(false) }
    
    Box(modifier = Modifier.fillMaxSize()) {
        // Your content
        
        Button(onClick = { showCelebration = true }) {
            Text("Celebrate!")
        }
        
        if (showCelebration) {
            ParticleExplosion(
                modifier = Modifier.fillMaxSize(),
                trigger = showCelebration,
                onComplete = { showCelebration = false }
            )
        }
    }
}
```

---

## 6. Data Layer

### Step 1: Create Theme Repository

```kotlin
package com.fitx.app.data.repository

import com.fitx.app.data.preferences.ThemeManager
import com.fitx.app.ui.theme.premium.PremiumTheme
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ThemeRepository @Inject constructor(
    private val themeManager: ThemeManager
) {
    val currentTheme: Flow<PremiumTheme> = themeManager.selectedTheme
    val isDarkMode: Flow<Boolean> = themeManager.isDarkMode
    
    suspend fun updateTheme(theme: PremiumTheme) {
        themeManager.setTheme(theme)
    }
    
    suspend fun updateDarkMode(enabled: Boolean) {
        themeManager.setDarkMode(enabled)
    }
}
```

### Step 2: Create ViewModel

```kotlin
package com.fitx.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fitx.app.data.repository.ThemeRepository
import com.fitx.app.ui.theme.premium.PremiumTheme
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ThemeViewModel @Inject constructor(
    private val themeRepository: ThemeRepository
) : ViewModel() {
    
    val currentTheme = themeRepository.currentTheme.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PremiumThemes.ElectricBlue
    )
    
    val isDarkMode = themeRepository.isDarkMode.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = true
    )
    
    fun setTheme(theme: PremiumTheme) {
        viewModelScope.launch {
            themeRepository.updateTheme(theme)
        }
    }
    
    fun setDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            themeRepository.updateDarkMode(enabled)
        }
    }
}
```

---

## 7. Testing

### Unit Test Example

```kotlin
package com.fitx.app.ui.theme.premium

import org.junit.Test
import org.junit.Assert.*

class ThemeEngineTest {
    
    @Test
    fun `test all themes are unique`() {
        val themeIds = PremiumThemes.AllThemes.map { it.id }
        assertEquals(themeIds.size, themeIds.distinct().size)
    }
    
    @Test
    fun `test theme colors are valid`() {
        PremiumThemes.AllThemes.forEach { theme ->
            assertNotNull(theme.primaryColor)
            assertNotNull(theme.secondaryColor)
            assertNotNull(theme.accentColor)
        }
    }
}
```

### UI Test Example

```kotlin
package com.fitx.app.ui.screens.premium

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.fitx.app.ui.theme.FitxTheme
import org.junit.Rule
import org.junit.Test

class PremiumDashboardTest {
    
    @get:Rule
    val composeTestRule = createComposeRule()
    
    @Test
    fun dashboardDisplaysGreeting() {
        composeTestRule.setContent {
            FitxTheme {
                PremiumDashboardScreen()
            }
        }
        
        composeTestRule.onNodeWithText("Good Morning", substring = true)
            .assertExists()
    }
}
```

---

## 🎯 Quick Start Checklist

- [ ] Sync Gradle dependencies
- [ ] Create ThemeManager class
- [ ] Update MainActivity with theme support
- [ ] Add premium screens to navigation
- [ ] Test theme switching
- [ ] Implement particle effects
- [ ] Add premium buttons to existing screens
- [ ] Replace standard cards with glass/glowing cards
- [ ] Add progress rings to stats
- [ ] Test animations on device
- [ ] Optimize performance
- [ ] Add haptic feedback
- [ ] Test on different screen sizes
- [ ] Verify dark/light mode
- [ ] Add accessibility labels

---

## 🚀 Performance Tips

1. **Lazy Loading**: Use LazyColumn/LazyRow for lists
2. **Remember**: Use `remember` for expensive calculations
3. **Keys**: Provide stable keys for animated lists
4. **Composition**: Minimize recomposition scope
5. **Images**: Use Coil with proper caching
6. **Animations**: Limit concurrent animations
7. **Particles**: Reduce particle count on low-end devices

---

## 🎨 Customization Guide

### Creating Custom Themes

```kotlin
val MyCustomTheme = PremiumTheme(
    id = "my_custom",
    name = "My Custom Theme",
    primaryColor = Color(0xFFYOURCOLOR),
    secondaryColor = Color(0xFFYOURCOLOR),
    accentColor = Color(0xFFYOURCOLOR),
    gradientStart = Color(0xFFYOURCOLOR),
    gradientEnd = Color(0xFFYOURCOLOR),
    surfaceGlow = Color(0xFFYOURCOLOR).copy(alpha = 0.15f)
)

// Add to AllThemes list
object PremiumThemes {
    val AllThemes = listOf(
        ElectricBlue,
        // ... existing themes
        MyCustomTheme
    )
}
```

### Custom Animations

```kotlin
@Composable
fun rememberCustomAnimation(): Float {
    val infiniteTransition = rememberInfiniteTransition(label = "custom")
    val value by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "custom_value"
    )
    return value
}
```

---

## 📱 Device Testing

Test on:
- [ ] Small phones (< 5.5")
- [ ] Medium phones (5.5" - 6.5")
- [ ] Large phones (> 6.5")
- [ ] Tablets
- [ ] Foldables
- [ ] Different Android versions (API 21-35)
- [ ] Different screen densities

---

## 🐛 Troubleshooting

### Issue: Animations are laggy
**Solution**: Reduce particle count, use hardware acceleration

### Issue: Theme not persisting
**Solution**: Check DataStore implementation, verify coroutine scope

### Issue: Colors not animating
**Solution**: Ensure `animatedColorScheme` is used in theme

### Issue: Gradle sync fails
**Solution**: Check JitPack repository is added, verify internet connection

---

## 📚 Additional Resources

- [Jetpack Compose Documentation](https://developer.android.com/jetpack/compose)
- [Material 3 Guidelines](https://m3.material.io/)
- [Lottie Documentation](https://airbnb.io/lottie/)
- [Accompanist Library](https://google.github.io/accompanist/)

---

## ✅ Next Steps

1. Implement remaining 55 features from PREMIUM_FEATURES.md
2. Add Lottie animation files for achievements
3. Integrate Firebase for cloud sync
4. Add Google Maps for route visualization
5. Implement USDA API for nutrition
6. Add WorkManager for reminders
7. Create widgets
8. Add social sharing
9. Implement data export
10. Add comprehensive testing

---

**Happy Coding! 🚀**
