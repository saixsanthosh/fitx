package com.fitx.app

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fitx.app.ui.navigation.FitxNavGraph
import com.fitx.app.ui.components.FitxSplash
import com.fitx.app.ui.screens.AuthRoute
import com.fitx.app.ui.theme.FitxTheme
import com.fitx.app.ui.viewmodel.AppUpdateViewModel
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
            val appUpdateViewModel: AppUpdateViewModel = hiltViewModel()
            val settings by viewModel.settings.collectAsStateWithLifecycle()
            val authUser by authViewModel.currentUser.collectAsStateWithLifecycle()
            val updateState by appUpdateViewModel.uiState.collectAsStateWithLifecycle()
            val context = LocalContext.current
            val updateInfo = updateState.availableUpdate
            var showSplash by rememberSaveable { mutableStateOf(true) }
            val onDownloadUpdate = remember(appUpdateViewModel, context) {
                { url: String ->
                    openExternalUrl(context, url)
                    appUpdateViewModel.dismissCurrentPrompt()
                }
            }

            LaunchedEffect(showSplash, authUser?.uid) {
                if (!showSplash && authUser != null) {
                    appUpdateViewModel.checkOnce()
                }
            }

            FitxTheme(darkTheme = settings.darkTheme) {
                Crossfade(
                    targetState = settings.darkTheme,
                    animationSpec = tween(durationMillis = 220),
                    label = "theme_crossfade"
                ) {
                    if (showSplash) {
                        FitxSplash(onFinished = { showSplash = false })
                    } else if (authUser == null) {
                        AuthRoute(viewModel = authViewModel)
                    } else {
                        FitxNavGraph()
                    }
                }

                if (updateInfo != null) {
                    AlertDialog(
                        onDismissRequest = { appUpdateViewModel.dismissCurrentPrompt() },
                        title = { Text("Update available: v${updateInfo.version}") },
                        text = { Text(updateInfo.message) },
                        confirmButton = {
                            TextButton(onClick = { onDownloadUpdate(updateInfo.downloadUrl) }) {
                                Text("Download")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { appUpdateViewModel.dismissCurrentPrompt() }) {
                                Text("Later")
                            }
                        }
                    )
                }
            }
        }
    }
}

private fun openExternalUrl(context: Context, url: String) {
    runCatching {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }
}
