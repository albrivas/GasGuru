package com.gasguru.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gasguru.core.domain.GetFuelStationUseCase
import com.gasguru.core.domain.GetUserDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val fuelStation: GetFuelStationUseCase,
    userData: GetUserDataUseCase,
) : ViewModel() {

    init {
        getFuelStations()
    }

    private fun getFuelStations() = viewModelScope.launch { fuelStation.getFuelInAllStations() }

    val uiState: StateFlow<Result<SplashUiState>> = userData()
        .map { Result.success(SplashUiState.Success) }
        .catch { emit(Result.failure(it)) }
        .stateIn(
            scope = viewModelScope,
            initialValue = Result.success(SplashUiState.Loading),
            started = SharingStarted.WhileSubscribed(5_000),
        )
}
