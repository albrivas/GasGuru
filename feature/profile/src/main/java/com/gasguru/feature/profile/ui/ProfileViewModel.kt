package com.gasguru.feature.profile.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gasguru.core.domain.user.GetUserDataUseCase
import com.gasguru.core.domain.fuelstation.SaveFuelSelectionUseCase
import com.gasguru.core.model.data.FuelType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    getUserData: GetUserDataUseCase,
    private val saveFuelSelectionUseCase: SaveFuelSelectionUseCase,
) : ViewModel() {

    val userData: StateFlow<ProfileUiState> = getUserData().map {
        ProfileUiState.Success(it)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ProfileUiState.Loading
    )

    fun handleEvents(event: ProfileEvents) {
        when (event) {
            is ProfileEvents.Fuel -> saveSelectionFuel(event.fuel)
        }
    }

    private fun saveSelectionFuel(fuelType: FuelType) = viewModelScope.launch {
        saveFuelSelectionUseCase(fuelType = fuelType)
    }
}
