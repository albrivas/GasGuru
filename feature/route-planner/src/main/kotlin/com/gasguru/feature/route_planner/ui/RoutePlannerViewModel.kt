package com.gasguru.feature.route_planner.ui

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class RoutePlannerViewModel : ViewModel() {

    private val _state = MutableStateFlow(RoutePlannerUiState.Loading)
    val state = _state.asStateFlow()
}

