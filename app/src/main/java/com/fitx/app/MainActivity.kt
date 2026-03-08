package com.fitx.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import com.fitx.app.ui.navigation.FitxApp
import com.fitx.app.ui.theme.FitxTheme

/**
 * MAIN ACTIVITY - REVOLUTIONARY FITNESS APP
 * Entry point for the world's most advanced fitness application
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Enable edge-to-edge display
        enableEdgeToEdge()
        
        // Allow content to draw behind system bars
        WindowCompat.setDecorFitsSystemWindows(window, false)
        
        setContent {
            FitxTheme(
                darkTheme = true, // Default to dark theme
                dynamicColor = false // Use our custom themes
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Launch the revolutionary Fitx app
                    FitxApp()
                }
            }
        }
    }
}
