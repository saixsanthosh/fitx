package com.fitx.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import com.fitx.app.ui.navigation.FitxApp
import dagger.hilt.android.AndroidEntryPoint

/**
 * MAIN ACTIVITY - REVOLUTIONARY FITNESS APP
 * Entry point for the world's most advanced fitness application
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Enable edge-to-edge display
        enableEdgeToEdge()
        
        // Allow content to draw behind system bars
        WindowCompat.setDecorFitsSystemWindows(window, false)
        
        setContent {
            FitxApp()
        }
    }
}
