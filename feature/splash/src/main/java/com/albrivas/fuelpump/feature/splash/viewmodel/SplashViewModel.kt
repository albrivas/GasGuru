package com.albrivas.fuelpump.feature.splash.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.albrivas.fuelpump.core.domain.GetFuelStationUseCase
import com.albrivas.fuelpump.core.domain.GetUserDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val fuelStation: GetFuelStationUseCase,
    private val userData: GetUserDataUseCase
) : ViewModel() {

    var state by mutableStateOf(false)
        private set

    init {
        getFuelStations()
        isOnboardingCompleted()
    }

    private fun getFuelStations() {
        viewModelScope.launch {
            fuelStation.temporalFillBBDD()
        }
    }

    private fun isOnboardingCompleted() {
        viewModelScope.launch {
            userData().catch { state = false }
                .collect { state = true}
        }
    }
}