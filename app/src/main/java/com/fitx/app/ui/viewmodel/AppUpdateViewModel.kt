package com.fitx.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fitx.app.BuildConfig
import com.fitx.app.domain.model.AppUpdateInfo
import com.fitx.app.domain.repository.AppUpdateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AppUpdateUiState(
    val checking: Boolean = false,
    val availableUpdate: AppUpdateInfo? = null
)

@HiltViewModel
class AppUpdateViewModel @Inject constructor(
    private val appUpdateRepository: AppUpdateRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(AppUpdateUiState())
    val uiState: StateFlow<AppUpdateUiState> = _uiState

    private var checkedThisLaunch = false

    fun checkOnce() {
        if (checkedThisLaunch) return
        checkedThisLaunch = true
        checkNow()
    }

    fun checkNow() {
        viewModelScope.launch {
            _uiState.update { it.copy(checking = true) }
            val update = appUpdateRepository.getAvailableUpdate(BuildConfig.VERSION_NAME)
            _uiState.update {
                it.copy(
                    checking = false,
                    availableUpdate = update
                )
            }
        }
    }

    fun dismissCurrentPrompt() {
        val version = _uiState.value.availableUpdate?.version ?: return
        viewModelScope.launch {
            appUpdateRepository.markPrompted(version)
        }
        _uiState.update { it.copy(availableUpdate = null) }
    }
}
