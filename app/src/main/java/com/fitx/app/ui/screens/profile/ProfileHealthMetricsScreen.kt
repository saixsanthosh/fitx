package com.fitx.app.ui.screens.profile

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fitx.app.ui.components.premium.AnimatedProgressRing
import com.fitx.app.ui.theme.premium.*
import kotlin.math.pow

/**
 * Feature 3: Profile & Health Metrics
 * Complete health profile with BMI, BMR, TDEE calculations
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileHealthMetricsScreen(
    theme: PremiumTheme = PremiumThemes.ElectricBlue,
    onBackPressed: () -> Unit = {},
    onSaveProfile: (HealthProfile) -> Unit = {}
) {
    var height by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf(Gender.MALE) }
    var activityLevel by remember { mutableStateOf(ActivityLevel.MODERATE) }
    var goalWeight by remember { mutableStateOf("") }
    var showResults by remember { mutableStateOf(false) }
    
    val healthMetrics = remember(height, weight, age, gender, activityLevel, goalWeight) {
        calculateHealthMetrics(
            height = height.toFloatOrNull() ?: 0f,
            weight = weight.toFloatOrNull() ?: 0f,
            age = age.toIntOrNull() ?: 0,
            gender = gender,
            activityLevel = activityLevel,
            goalWeight = goalWeight.toFloatOrNull() ?: 0f
        )
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Health Profile",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            FloatingParticles(
                modifier = Modifier.fillMaxSize(),
                color = theme.primaryColor.copy(alpha = 0.1f)
            )
            
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item { Spacer(modifier = Modifier.height(8.dp)) }
                
                // Profile Header
                item {
                    ProfileHeader(theme = theme)
                }
                
                // Basic Information Section
                item {
                    SectionTitle(text = "Basic Information", icon = Icons.Default.Person)
                }
                
                item {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            // Height Input
                            MetricInputField(
                                value = height,
                                onValueChange = { height = it },
                                label = "Height",
                                unit = "cm",
                                icon = Icons.Default.Height,
                                color = theme.primaryColor
                            )
                            
                            // Weight Input
                            MetricInputField(
                                value = weight,
                                onValueChange = { weight = it },
                                label = "Current Weight",
                                unit = "kg",
                                icon = Icons.Default.MonitorWeight,
                                color = theme.secondaryColor
                            )
                            
                            // Age Input
                            MetricInputField(
                                value = age,
                                onValueChange = { age = it },
                                label = "Age",
                                unit = "years",
                                icon = Icons.Default.Cake,
                                color = theme.accentColor
                            )
                        }
                    }
                }
                
                // Gender Selection
                item {
                    SectionTitle(text = "Gender", icon = Icons.Default.Wc)
                }
                
                item {
                    GenderSelector(
                        selectedGender = gender,
                        onGenderSelected = { gender = it },
                        theme = theme
                    )
                }
                
                // Activity Level
                item {
                    SectionTitle(text = "Activity Level", icon = Icons.Default.DirectionsRun)
                }
                
                item {
                    ActivityLevelSelector(
                        selectedLevel = activityLevel,
                        onLevelSelected = { activityLevel = it },
                        theme = theme
                    )
                }
                
                // Goal Weight
                item {
                    SectionTitle(text = "Goal Weight", icon = Icons.Default.Flag)
                }
                
                item {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        MetricInputField(
                            value = goalWeight,
                            onValueChange = { goalWeight = it },
                            label = "Target Weight",
                            unit = "kg",
                            icon = Icons.Default.TrendingDown,
                            color = theme.primaryColor
                        )
                    }
                }
                
                // Calculate Button
                item {
                    PremiumButton(
                        text = "Calculate Metrics",
                        onClick = {
                            showResults = true
                            onSaveProfile(
                                HealthProfile(
                                    height = height.toFloatOrNull() ?: 0f,
                                    weight = weight.toFloatOrNull() ?: 0f,
                                    age = age.toIntOrNull() ?: 0,
                                    gender = gender,
                                    activityLevel = activityLevel,
                                    goalWeight = goalWeight.toFloatOrNull() ?: 0f
                                )
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        gradient = listOf(theme.gradientStart, theme.gradientEnd),
                        enabled = height.isNotEmpty() && weight.isNotEmpty() && age.isNotEmpty()
                    )
                }
                
                // Results Section
                if (showResults && healthMetrics.bmi > 0) {
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        SectionTitle(text = "Your Health Metrics", icon = Icons.Default.Analytics)
                    }
                    
                    item {
                        HealthMetricsResults(
                            metrics = healthMetrics,
                            theme = theme
                        )
                    }
                }
                
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }
}

@Composable
private fun ProfileHeader(theme: PremiumTheme) {
    HeroCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp),
        backgroundGradient = listOf(theme.gradientStart, theme.gradientEnd)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Complete Your Profile",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Get personalized health insights",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
            
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.HealthAndSafety,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(48.dp)
                )
            }
        }
    }
}

@Composable
private fun SectionTitle(text: String, icon: ImageVector) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = text,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
private fun MetricInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    unit: String,
    icon: ImageVector,
    color: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
        }
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                suffix = { Text(unit) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = color,
                    cursorColor = color
                )
            )
        }
    }
}

@Composable
private fun GenderSelector(
    selectedGender: Gender,
    onGenderSelected: (Gender) -> Unit,
    theme: PremiumTheme
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Gender.values().forEach { gender ->
            GenderCard(
                gender = gender,
                isSelected = selectedGender == gender,
                onClick = { onGenderSelected(gender) },
                theme = theme,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun GenderCard(
    gender: Gender,
    isSelected: Boolean,
    onClick: () -> Unit,
    theme: PremiumTheme,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "gender_scale"
    )
    
    GlowingCard(
        modifier = modifier
            .height(100.dp)
            .scale(scale),
        glowColor = if (isSelected) theme.primaryColor else Color.Transparent
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(20.dp))
                .background(
                    if (isSelected) {
                        Brush.linearGradient(
                            colors = listOf(
                                theme.primaryColor.copy(alpha = 0.2f),
                                theme.secondaryColor.copy(alpha = 0.2f)
                            )
                        )
                    } else {
                        Brush.linearGradient(colors = listOf(Color.Transparent, Color.Transparent))
                    }
                )
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = if (gender == Gender.MALE) Icons.Default.Male else Icons.Default.Female,
                contentDescription = null,
                tint = if (isSelected) theme.primaryColor else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = gender.displayName,
                fontSize = 16.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun ActivityLevelSelector(
    selectedLevel: ActivityLevel,
    onLevelSelected: (ActivityLevel) -> Unit,
    theme: PremiumTheme
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        ActivityLevel.values().forEach { level ->
            ActivityLevelCard(
                level = level,
                isSelected = selectedLevel == level,
                onClick = { onLevelSelected(level) },
                theme = theme
            )
        }
    }
}

@Composable
private fun ActivityLevelCard(
    level: ActivityLevel,
    isSelected: Boolean,
    onClick: () -> Unit,
    theme: PremiumTheme
) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
                if (isSelected) {
                    Brush.horizontalGradient(
                        colors = listOf(
                            theme.primaryColor.copy(alpha = 0.15f),
                            theme.secondaryColor.copy(alpha = 0.15f)
                        )
                    )
                } else {
                    Brush.linearGradient(colors = listOf(Color.Transparent, Color.Transparent))
                }
            )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            if (isSelected) theme.primaryColor.copy(alpha = 0.2f)
                            else MaterialTheme.colorScheme.surfaceVariant
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = level.icon,
                        contentDescription = null,
                        tint = if (isSelected) theme.primaryColor else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(24.dp)
                    )
                }
                
                Column {
                    Text(
                        text = level.displayName,
                        fontSize = 16.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = level.description,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }
            
            RadioButton(
                selected = isSelected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(
                    selectedColor = theme.primaryColor
                )
            )
        }
    }
}

@Composable
private fun HealthMetricsResults(
    metrics: HealthMetrics,
    theme: PremiumTheme
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // BMI Card with Progress Ring
        GlowingCard(
            modifier = Modifier.fillMaxWidth(),
            glowColor = theme.primaryColor
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Body Mass Index",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = String.format("%.1f", metrics.bmi),
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = theme.primaryColor
                    )
                    Text(
                        text = metrics.bmiCategory,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
                
                AnimatedProgressRing(
                    progress = (metrics.bmi / 40f).coerceIn(0f, 1f),
                    size = 100.dp,
                    strokeWidth = 10.dp,
                    gradientColors = listOf(theme.primaryColor, theme.secondaryColor),
                    showPercentage = false
                )
            }
        }
        
        // BMR and TDEE Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MetricResultCard(
                title = "BMR",
                value = "${metrics.bmr.toInt()}",
                unit = "kcal/day",
                description = "Basal Metabolic Rate",
                color = theme.secondaryColor,
                modifier = Modifier.weight(1f)
            )
            
            MetricResultCard(
                title = "TDEE",
                value = "${metrics.tdee.toInt()}",
                unit = "kcal/day",
                description = "Total Daily Energy",
                color = theme.accentColor,
                modifier = Modifier.weight(1f)
            )
        }
        
        // Daily Calorie Target
        GradientCard(
            modifier = Modifier.fillMaxWidth(),
            gradientColors = listOf(theme.gradientStart, theme.gradientEnd)
        ) {
            Column {
                Text(
                    text = "Daily Calorie Target",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White.copy(alpha = 0.9f)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${metrics.dailyCalorieTarget.toInt()} kcal",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "To reach your goal weight",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }
        
        // Goal Projection
        if (metrics.weeksToGoal > 0) {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = null,
                        tint = theme.primaryColor,
                        modifier = Modifier.size(32.dp)
                    )
                    Column {
                        Text(
                            text = "Goal Timeline",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                        Text(
                            text = "${metrics.weeksToGoal} weeks",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Estimated time to reach goal",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricResultCard(
    title: String,
    value: String,
    unit: String,
    description: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    GlassCard(
        modifier = modifier.height(140.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            
            Column {
                Text(
                    text = value,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
                Text(
                    text = unit,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
            
            Text(
                text = description,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        }
    }
}

// Data Classes and Enums
enum class Gender(val displayName: String) {
    MALE("Male"),
    FEMALE("Female")
}

enum class ActivityLevel(
    val displayName: String,
    val description: String,
    val multiplier: Float,
    val icon: ImageVector
) {
    SEDENTARY("Sedentary", "Little or no exercise", 1.2f, Icons.Default.Chair),
    LIGHT("Light", "Exercise 1-3 days/week", 1.375f, Icons.Default.DirectionsWalk),
    MODERATE("Moderate", "Exercise 3-5 days/week", 1.55f, Icons.Default.DirectionsRun),
    ACTIVE("Active", "Exercise 6-7 days/week", 1.725f, Icons.Default.FitnessCenter),
    VERY_ACTIVE("Very Active", "Hard exercise daily", 1.9f, Icons.Default.SportsGymnastics)
}

data class HealthProfile(
    val height: Float,
    val weight: Float,
    val age: Int,
    val gender: Gender,
    val activityLevel: ActivityLevel,
    val goalWeight: Float
)

data class HealthMetrics(
    val bmi: Float,
    val bmiCategory: String,
    val bmr: Float,
    val tdee: Float,
    val dailyCalorieTarget: Float,
    val weeksToGoal: Int
)

private fun calculateHealthMetrics(
    height: Float,
    weight: Float,
    age: Int,
    gender: Gender,
    activityLevel: ActivityLevel,
    goalWeight: Float
): HealthMetrics {
    if (height <= 0 || weight <= 0 || age <= 0) {
        return HealthMetrics(0f, "", 0f, 0f, 0f, 0)
    }
    
    // BMI Calculation
    val heightInMeters = height / 100f
    val bmi = weight / (heightInMeters.pow(2))
    val bmiCategory = when {
        bmi < 18.5 -> "Underweight"
        bmi < 25 -> "Normal"
        bmi < 30 -> "Overweight"
        else -> "Obese"
    }
    
    // BMR Calculation (Mifflin-St Jeor)
    val bmr = if (gender == Gender.MALE) {
        (10 * weight) + (6.25 * height) - (5 * age) + 5
    } else {
        (10 * weight) + (6.25 * height) - (5 * age) - 161
    }.toFloat()
    
    // TDEE Calculation
    val tdee = bmr * activityLevel.multiplier
    
    // Daily Calorie Target (500 kcal deficit for weight loss, surplus for gain)
    val weightDifference = goalWeight - weight
    val dailyCalorieTarget = if (weightDifference < 0) {
        tdee - 500 // Weight loss
    } else if (weightDifference > 0) {
        tdee + 300 // Weight gain
    } else {
        tdee // Maintenance
    }.toFloat()
    
    // Weeks to Goal (0.5 kg per week is healthy)
    val weeksToGoal = if (goalWeight > 0 && weightDifference != 0f) {
        (kotlin.math.abs(weightDifference) / 0.5f).toInt()
    } else {
        0
    }
    
    return HealthMetrics(
        bmi = bmi,
        bmiCategory = bmiCategory,
        bmr = bmr,
        tdee = tdee,
        dailyCalorieTarget = dailyCalorieTarget,
        weeksToGoal = weeksToGoal
    )
}
