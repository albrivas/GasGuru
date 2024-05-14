package com.albrivas.fuelpump.feature.splash.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.albrivas.fuelpump.core.domain.GetFuelStationUseCase
import com.albrivas.fuelpump.core.domain.GetUserDataUseCase
import com.albrivas.fuelpump.feature.splash.state.SplashUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val fuelStation: GetFuelStationUseCase,
    private val userData: GetUserDataUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(SplashUiState())
    val state = _state.asStateFlow()

    init {
        isOnboardingCompleted()
        getFuelStations()
    }

    private fun getFuelStations() {
        viewModelScope.launch {
            fuelStation.temporalFillBBDD()
        }
    }

    private fun isOnboardingCompleted() {
        viewModelScope.launch {
            userData().catch { _state.update { it.copy(isOnboardingComplete = false) } }
                .collect {  _state.update { it.copy(isOnboardingComplete = true) } }
        }
    }
}