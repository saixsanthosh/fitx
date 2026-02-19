package com.fitx.app.service

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

object TrackingStore {
    private val _state = MutableStateFlow(TrackingState())
    val state: StateFlow<TrackingState> = _state

    fun update(update: (TrackingState) -> TrackingState) {
        _state.update(update)
    }

    fun set(state: TrackingState) {
        _state.value = state
    }

    fun reset() {
        _state.value = TrackingState()
    }
}

