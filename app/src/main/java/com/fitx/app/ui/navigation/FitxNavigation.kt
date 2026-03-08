package com.fitx.app.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fitx.app.ui.screens.AuthRoute
import com.fitx.app.ui.theme.FitxTheme
import com.fitx.app.ui.viewmodel.AuthViewModel
import com.fitx.app.ui.viewmodel.SettingsViewModel

@Composable
fun FitxApp(
    settingsViewModel: SettingsViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val settings = settingsViewModel.settings.collectAsStateWithLifecycle().value
    val currentUser = authViewModel.currentUser.collectAsStateWithLifecycle().value
    val systemDarkTheme = isSystemInDarkTheme()
    val effectiveDarkTheme = if (settings.useSystemTheme) systemDarkTheme else settings.darkTheme

    LaunchedEffect(currentUser?.uid, settings.guestModeEnabled) {
        if (currentUser != null && settings.guestModeEnabled) {
            settingsViewModel.setGuestMode(false)
        }
    }

    FitxTheme(
        darkTheme = effectiveDarkTheme,
        dynamicColor = false
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            if (currentUser != null || settings.guestModeEnabled) {
                FitxNavGraph()
            } else {
                AuthRoute(
                    authViewModel,
                    onContinueOffline = {
                        authViewModel.clearError()
                        settingsViewModel.setGuestMode(true)
                    }
                )
            }
        }
    }
}
