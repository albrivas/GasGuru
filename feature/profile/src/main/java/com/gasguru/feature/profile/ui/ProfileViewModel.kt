package com.gasguru.feature.profile.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gasguru.core.domain.GetUserDataUseCase
import com.gasguru.core.domain.SaveUserDataUseCase
import com.gasguru.core.model.data.FuelType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getUserDataUseCase: GetUserDataUseCase,
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
        val user = getUserDataUseCase().first()
        saveUserDataUseCase(user.copy(fuelSelection = fuelType))
    }
}
