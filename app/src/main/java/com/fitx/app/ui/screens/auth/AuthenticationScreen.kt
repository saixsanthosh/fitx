package com.fitx.app.ui.screens.auth

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fitx.app.ui.theme.premium.*

/**
 * Feature 1: Authentication Screen
 * Premium authentication with Google Sign-In and Guest Mode
 */

@Composable
fun AuthenticationScreen(
    theme: PremiumTheme = PremiumThemes.ElectricBlue,
    onGoogleSignIn: () -> Unit,
    onGuestMode: () -> Unit,
    isLoading: Boolean = false
) {
    var showWelcome by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(300)
        showWelcome = true
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        theme.gradientStart.copy(alpha = 0.1f),
                        MaterialTheme.colorScheme.background,
                        theme.gradientEnd.copy(alpha = 0.1f)
                    )
                )
            )
    ) {
        // Floating particles background
        FloatingParticles(
            modifier = Modifier.fillMaxSize(),
            color = theme.primaryColor.copy(alpha = 0.15f),
            particleCount = 25
        )
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(60.dp))
            
            // Logo and Welcome Section
            AnimatedVisibility(
                visible = showWelcome,
                enter = fadeIn() + slideInVertically(initialOffsetY = { -it / 2 })
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // Animated Logo
                    AnimatedLogo(theme = theme)
                    
                    // Welcome Text
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Welcome to Fitx",
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Your premium fitness companion",
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            
            // Features Preview
            AnimatedVisibility(
                visible = showWelcome,
                enter = fadeIn(animationSpec = tween(800, delayMillis = 400))
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    FeatureHighlight(
                        icon = Icons.Default.DirectionsRun,
                        text = "Track your activities",
                        color = theme.primaryColor
                    )
                    FeatureHighlight(
                        icon = Icons.Default.Restaurant,
                        text = "Monitor nutrition",
                        color = theme.secondaryColor
                    )
                    FeatureHighlight(
                        icon = Icons.Default.TrendingUp,
                        text = "Achieve your goals",
                        color = theme.accentColor
                    )
                }
            }
            
            // Authentication Buttons
            AnimatedVisibility(
                visible = showWelcome,
                enter = fadeIn(animationSpec = tween(800, delayMillis = 600)) +
                        slideInVertically(
                            initialOffsetY = { it / 2 },
                            animationSpec = tween(800, delayMillis = 600)
                        )
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Google Sign-In Button
                    GoogleSignInButton(
                        onClick = onGoogleSignIn,
                        isLoading = isLoading,
                        theme = theme
                    )
                    
                    // Guest Mode Button
                    OutlinedButton(
                        onClick = onGuestMode,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(28.dp),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            width = 2.dp,
                            brush = Brush.horizontalGradient(
                                colors = listOf(theme.primaryColor, theme.secondaryColor)
                            )
                        ),
                        enabled = !isLoading
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = theme.primaryColor
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Continue as Guest",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    
                    // Privacy Text
                    Text(
                        text = "By continuing, you agree to our Terms & Privacy Policy",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun AnimatedLogo(theme: PremiumTheme) {
    val rotation = rememberRotationAnimation(durationMillis = 4000)
    val scale = rememberPulseAnimation(minScale = 0.95f, maxScale = 1.05f, durationMillis = 2000)
    
    Box(
        modifier = Modifier
            .size(140.dp)
            .scale(scale),
        contentAlignment = Alignment.Center
    ) {
        // Outer rotating ring
        Box(
            modifier = Modifier
                .size(140.dp)
                .rotateEffect(rotation)
                .clip(CircleShape)
                .background(
                    brush = Brush.sweepGradient(
                        colors = listOf(
                            theme.primaryColor,
                            theme.secondaryColor,
                            theme.accentColor,
                            theme.primaryColor
                        )
                    )
                )
        )
        
        // Inner circle
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.FitnessCenter,
                contentDescription = "Fitx Logo",
                modifier = Modifier.size(64.dp),
                tint = theme.primaryColor
            )
        }
    }
}

@Composable
private fun GoogleSignInButton(
    onClick: () -> Unit,
    isLoading: Boolean,
    theme: PremiumTheme
) {
    PremiumButton(
        text = if (isLoading) "Signing in..." else "Sign in with Google",
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        enabled = !isLoading,
        gradient = listOf(theme.gradientStart, theme.gradientEnd),
        height = 56.dp,
        icon = {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Login,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        }
    )
}

@Composable
private fun FeatureHighlight(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    color: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
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
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

class AuthViewModel @javax.inject.Inject constructor(
    private val authRepository: com.fitx.app.domain.repository.AuthRepository
) : androidx.lifecycle.ViewModel() {
    
    private val _authState = androidx.compose.runtime.mutableStateOf<AuthState>(AuthState.Idle)
    val authState: androidx.compose.runtime.State<AuthState> = _authState
    
    fun signInWithGoogle(idToken: String) {
        _authState.value = AuthState.Success
    }
    
    fun continueAsGuest() {
        _authState.value = AuthState.Success
    }
}

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    data class Error(val message: String) : AuthState()
}
