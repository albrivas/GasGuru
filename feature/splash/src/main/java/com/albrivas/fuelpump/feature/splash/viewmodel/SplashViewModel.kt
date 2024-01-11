package com.albrivas.fuelpump.feature.splash.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.albrivas.fuelpump.core.domain.GetFuelStationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val fuelStation: GetFuelStationUseCase,
) : ViewModel() {

    init {
        viewModelScope.launch {
            fuelStation.temporalFillBBDD()
        }
    }
}