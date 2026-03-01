package com.gasguru.feature.onboarding_welcome.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gasguru.core.domain.vehicle.SaveDefaultVehicleCapacityUseCase
import com.gasguru.feature.onboarding_welcome.ui.CapacityTankEvent
import com.gasguru.navigation.manager.NavigationDestination
import com.gasguru.navigation.manager.NavigationManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CapacityTankViewModel(
    private val navigationManager: NavigationManager,
    private val saveDefaultVehicleCapacityUseCase: SaveDefaultVehicleCapacityUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CapacityTankUiState())
    val uiState: StateFlow<CapacityTankUiState> = _uiState.asStateFlow()

    fun handleEvent(event: CapacityTankEvent) {
        when (event) {
            is CapacityTankEvent.SelectCommonValue -> onSelectCommonValue(value = event.value)
            is CapacityTankEvent.OpenPicker -> onOpenPicker()
            is CapacityTankEvent.ClosePicker -> onClosePicker()
            is CapacityTankEvent.ConfirmPickerValue -> onConfirmPickerValue(value = event.value)
            is CapacityTankEvent.Continue -> onContinue()
        }
    }

    private fun onSelectCommonValue(value: Int) {
        _uiState.update { it.copy(selectedCapacity = value) }
    }

    private fun onOpenPicker() {
        _uiState.update { state ->
            state.copy(
                showPicker = true,
                pickerValue = state.selectedCapacity ?: CapacityTankUiState.PICKER_MIN,
            )
        }
    }

    private fun onClosePicker() {
        _uiState.update { it.copy(showPicker = false) }
    }

    private fun onConfirmPickerValue(value: Int) {
        _uiState.update { it.copy(selectedCapacity = value, showPicker = false) }
    }

    private fun onContinue() {
        val capacity = _uiState.value.selectedCapacity ?: return
        viewModelScope.launch {
            saveDefaultVehicleCapacityUseCase(tankCapacity = capacity)
            navigationManager.navigateTo(destination = NavigationDestination.Home)
        }
    }
}
