package com.fitx.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fitx.app.ui.navigation.FitxNavGraph
import com.fitx.app.ui.components.FitxSplash
import com.fitx.app.ui.screens.AuthRoute
import com.fitx.app.ui.theme.FitxTheme
import com.fitx.app.ui.viewmodel.AuthViewModel
import com.fitx.app.ui.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: MainViewModel = hiltViewModel()
            val authViewModel: AuthViewModel = hiltViewModel()
            val settings by viewModel.settings.collectAsStateWithLifecycle()
            val authUser by authViewModel.currentUser.collectAsStateWithLifecycle()
            var showSplash by rememberSaveable { mutableStateOf(true) }
            FitxTheme(darkTheme = settings.darkTheme) {
                if (showSplash) {
                    FitxSplash(onFinished = { showSplash = false })
                } else if (authUser == null) {
                    AuthRoute(viewModel = authViewModel)
                } else {
                    FitxNavGraph()
                }
            }
        }
    }
}
