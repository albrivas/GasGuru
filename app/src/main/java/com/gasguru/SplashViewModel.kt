package com.gasguru

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gasguru.core.domain.fuelstation.GetFuelStationUseCase
import com.gasguru.core.domain.user.GetUserDataUseCase
import com.gasguru.core.model.data.ThemeMode
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class SplashViewModel(
    private val fuelStation: GetFuelStationUseCase,
    private val userData: GetUserDataUseCase,
    private val ioDispatcher: CoroutineDispatcher,
) : ViewModel() {

    init {
        getFuelStations()
    }

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
        .take(1)
        .map { Result.success(SplashUiState.Success(it.isOnboardingSuccess)) }
        .catch { emit(Result.failure(it)) }
        .stateIn(
            scope = viewModelScope,
            initialValue = Result.success(SplashUiState.Loading),
            started = SharingStarted.WhileSubscribed(5_000),
        )

    val themeMode: StateFlow<ThemeMode> = userData()
        .map { it.themeMode }
        .stateIn(
            scope = viewModelScope,
            initialValue = ThemeMode.SYSTEM,
            started = SharingStarted.WhileSubscribed(5_000),
        )

    private fun isTimestampWithin30Minutes(timestamp: Long): Boolean {
        val thirtyMinutesAgo = System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(30)
        return timestamp > thirtyMinutesAgo
    }
}
