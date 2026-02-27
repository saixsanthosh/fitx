package com.fitx.app.ui.screens

import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fitx.app.ui.components.AnimatedFitxLogo
import com.fitx.app.ui.components.FitxBrandBackground
import com.fitx.app.ui.viewmodel.AuthViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

@Composable
fun AuthRoute(
    viewModel: AuthViewModel = hiltViewModel(),
    onContinueOffline: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = androidx.compose.ui.platform.LocalContext.current

    val webClientId = remember(context) { readWebClientId(context) }
    val signInClient = remember(context, webClientId) {
        if (webClientId.isNullOrBlank()) {
            null
        } else {
            val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(webClientId)
                .build()
            GoogleSignIn.getClient(context, options)
        }
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val data = result.data
        if (data == null) {
            viewModel.setError("Sign-in canceled.")
            return@rememberLauncherForActivityResult
        }
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            val account = task.getResult(ApiException::class.java)
            val token = account.idToken
            if (token.isNullOrBlank()) {
                viewModel.setError("Unable to fetch Google token. Check Firebase setup.")
            } else {
                viewModel.signInWithGoogleToken(token)
            }
        } catch (exception: ApiException) {
            viewModel.setError("Google sign-in failed (${exception.statusCode}).")
        }
    }

    FitxBrandBackground {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                shape = RoundedCornerShape(28.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.86f)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    AnimatedFitxLogo(showWordmark = false, logoSize = 128.dp)
                    Text(
                        text = "Welcome to Fitx",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Sign in with Google to sync your profile and continue.",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )

                    if (uiState.loading) {
                        CircularProgressIndicator()
                    }

                    Button(
                        onClick = {
                            viewModel.clearError()
                            if (signInClient == null) {
                                viewModel.setError("Firebase config missing. Add google-services.json.")
                            } else {
                                launcher.launch(signInClient.signInIntent)
                            }
                        },
                        enabled = !uiState.loading,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Continue with Google")
                    }

                    OutlinedButton(
                        onClick = {
                            viewModel.clearError()
                            onContinueOffline()
                        },
                        enabled = !uiState.loading,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Continue Offline")
                    }

                    if (webClientId.isNullOrBlank()) {
                        Text(
                            text = "Missing default_web_client_id. Check Firebase setup.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                    }

                    uiState.errorMessage?.let { message ->
                        Text(
                            text = message,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

private fun readWebClientId(context: Context): String? {
    val id = context.resources.getIdentifier(
        "default_web_client_id",
        "string",
        context.packageName
    )
    if (id == 0) return null
    return context.getString(id).takeIf { it.isNotBlank() }
}
