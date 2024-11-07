package com.gasguru.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gasguru.core.common.IoDispatcher
import com.gasguru.core.domain.GetFuelStationUseCase
import com.gasguru.core.domain.GetUserDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val fuelStation: GetFuelStationUseCase,
    private val userData: GetUserDataUseCase,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : ViewModel() {

    fun updateFuelStations() = viewModelScope.launch(ioDispatcher) {
        userData().catch {
            getFuelStations() // Is first installation
        }.collect { user ->
            if (!isTimestampWithin30Minutes(user.lastUpdate)) {
                getFuelStations()
            }
        }
    }

    private fun getFuelStations() = viewModelScope.launch(ioDispatcher) {
        fuelStation.getFuelInAllStations()
    }

    val uiState: StateFlow<Result<SplashUiState>> = userData()
        .map { Result.success(SplashUiState.Success) }
        .catch { emit(Result.failure(it)) }
        .stateIn(
            scope = viewModelScope,
            initialValue = Result.success(SplashUiState.Loading),
            started = SharingStarted.WhileSubscribed(5_000),
        )

    private fun isTimestampWithin30Minutes(timestamp: Long): Boolean {
        val thirtyMinutesAgo = System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(30)
        return timestamp > thirtyMinutesAgo
    }
}
