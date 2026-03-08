package com.fitx.app.ui.screens.premium

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fitx.app.ui.theme.premium.*

/**
 * Theme Settings Screen
 * Allows users to customize app theme with live preview
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeSettingsScreen(
    currentTheme: PremiumTheme,
    onThemeSelected: (PremiumTheme) -> Unit,
    isDarkMode: Boolean,
    onDarkModeToggle: (Boolean) -> Unit,
    onBackPressed: () -> Unit
) {
    var selectedTheme by remember { mutableStateOf(currentTheme) }
    var showPreview by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Theme Settings",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Dark Mode Toggle
            DarkModeSection(
                isDarkMode = isDarkMode,
                onToggle = onDarkModeToggle,
                theme = selectedTheme
            )
            
            // Theme Preview
            ThemePreviewCard(theme = selectedTheme)
            
            // Theme Selection
            Text(
                text = "Choose Your Color",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(PremiumThemes.AllThemes) { theme ->
                    ThemeCard(
                        theme = theme,
                        isSelected = theme.id == selectedTheme.id,
                        onClick = {
                            selectedTheme = theme
                            onThemeSelected(theme)
                        }
                    )
                }
            }
            
            // Apply Button
            PremiumButton(
                text = "Apply Theme",
                onClick = {
                    onThemeSelected(selectedTheme)
                    showPreview = true
                },
                modifier = Modifier.fillMaxWidth(),
                gradient = listOf(selectedTheme.gradientStart, selectedTheme.gradientEnd)
            )
        }
        
        // Success animation
        if (showPreview) {
            Box(modifier = Modifier.fillMaxSize()) {
                ParticleExplosion(
                    modifier = Modifier.fillMaxSize(),
                    trigger = showPreview,
                    colors = listOf(
                        selectedTheme.primaryColor,
                        selectedTheme.secondaryColor,
                        selectedTheme.accentColor
                    ),
                    onComplete = { showPreview = false }
                )
            }
        }
    }
}

@Composable
private fun DarkModeSection(
    isDarkMode: Boolean,
    onToggle: (Boolean) -> Unit,
    theme: PremiumTheme
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 20.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(theme.primaryColor, theme.secondaryColor)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                
                Column {
                    Text(
                        text = "Dark Mode",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (isDarkMode) "Enabled" else "Disabled",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }
            
            Switch(
                checked = isDarkMode,
                onCheckedChange = onToggle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = theme.primaryColor,
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = Color.Gray
                )
            )
        }
    }
}

@Composable
private fun ThemePreviewCard(theme: PremiumTheme) {
    HeroCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        backgroundGradient = listOf(theme.gradientStart, theme.gradientEnd),
        cornerRadius = 24.dp
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = theme.name,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ColorDot(color = theme.primaryColor)
                ColorDot(color = theme.secondaryColor)
                ColorDot(color = theme.accentColor)
            }
        }
    }
}

@Composable
private fun ColorDot(color: Color) {
    Box(
        modifier = Modifier
            .size(24.dp)
            .clip(CircleShape)
            .background(color)
            .border(2.dp, Color.White.copy(alpha = 0.5f), CircleShape)
    )
}

@Composable
private fun ThemeCard(
    theme: PremiumTheme,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "theme_scale"
    )
    
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .scale(scale)
    ) {
        GlowingCard(
            modifier = Modifier.fillMaxSize(),
            glowColor = if (isSelected) theme.primaryColor else Color.Transparent
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
                        text = theme.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    AnimatedVisibility(
                        visible = isSelected,
                        enter = scaleIn() + fadeIn()
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Selected",
                            tint = theme.primaryColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                
                // Color gradient preview
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(theme.gradientStart, theme.gradientEnd)
                            )
                        )
                        .clickable(onClick = onClick)
                )
            }
        }
    }
}
