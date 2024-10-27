package com.albrivas.fuelpump.feature.profile.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.albrivas.fuelpump.core.domain.GetUserDataUseCase
import com.albrivas.fuelpump.core.domain.SaveUserDataUseCase
import com.albrivas.fuelpump.core.model.data.FuelType
import com.albrivas.fuelpump.core.model.data.UserData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    getUserDataUseCase: GetUserDataUseCase,
    private val saveUserDataUseCase: SaveUserDataUseCase,
) : ViewModel() {

    val userData: StateFlow<ProfileUiState> = getUserDataUseCase().map {
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
        saveUserDataUseCase(UserData(fuelSelection = fuelType))
    }
}
