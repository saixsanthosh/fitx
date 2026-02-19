package com.fitx.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fitx.app.domain.model.AuthUser
import com.fitx.app.domain.repository.AuthRepository
import com.fitx.app.service.sync.CloudSyncScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthUiState(
    val loading: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val cloudSyncScheduler: CloudSyncScheduler
) : ViewModel() {

    val currentUser: StateFlow<AuthUser?> = authRepository.observeCurrentUser().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null
    )

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState

    fun signInWithGoogleToken(idToken: String) {
        if (idToken.isBlank()) {
            _uiState.value = AuthUiState(errorMessage = "Invalid Google token received.")
            return
        }
        viewModelScope.launch {
            _uiState.value = AuthUiState(loading = true)
            val result = authRepository.signInWithGoogleIdToken(idToken)
            _uiState.value = if (result.isSuccess) {
                cloudSyncScheduler.syncNow()
                AuthUiState()
            } else {
                AuthUiState(
                    errorMessage = result.exceptionOrNull()?.message ?: "Google sign-in failed."
                )
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
            _uiState.value = AuthUiState()
        }
    }

    fun setError(message: String) {
        _uiState.update { it.copy(loading = false, errorMessage = message) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
