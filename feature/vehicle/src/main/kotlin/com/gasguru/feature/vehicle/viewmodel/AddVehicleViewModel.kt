package com.gasguru.feature.vehicle.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gasguru.core.domain.user.GetUserDataUseCase
import com.gasguru.core.domain.vehicle.AddVehicleUseCase
import com.gasguru.core.model.data.FuelType
import com.gasguru.core.model.data.Vehicle
import com.gasguru.core.model.data.VehicleType
import com.gasguru.feature.vehicle.ui.AddVehicleEvent
import com.gasguru.navigation.manager.NavigationManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddVehicleViewModel(
    private val navigationManager: NavigationManager,
    private val addVehicleUseCase: AddVehicleUseCase,
    private val getUserDataUseCase: GetUserDataUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddVehicleUiState())
    val uiState: StateFlow<AddVehicleUiState> = _uiState.asStateFlow()

    fun handleEvent(event: AddVehicleEvent) {
        when (event) {
            is AddVehicleEvent.SelectVehicleType -> onSelectVehicleType(vehicleType = event.vehicleType)
            is AddVehicleEvent.UpdateVehicleName -> onUpdateVehicleName(name = event.name)
            is AddVehicleEvent.SelectFuelType -> onSelectFuelType(fuelType = event.fuelType)
            is AddVehicleEvent.OpenCapacityPicker -> onOpenCapacityPicker()
            is AddVehicleEvent.CloseCapacityPicker -> onCloseCapacityPicker()
            is AddVehicleEvent.ConfirmCapacityValue -> onConfirmCapacityValue(value = event.value)
            is AddVehicleEvent.ToggleMainVehicle -> onToggleMainVehicle()
            is AddVehicleEvent.SaveVehicle -> onSaveVehicle()
            is AddVehicleEvent.Back -> navigationManager.navigateBack()
        }
    }

    private fun onSelectVehicleType(vehicleType: VehicleType) {
        _uiState.update { it.copy(selectedVehicleType = vehicleType) }
    }

    private fun onUpdateVehicleName(name: String) {
        _uiState.update { it.copy(vehicleName = name) }
    }

    private fun onSelectFuelType(fuelType: FuelType) {
        _uiState.update { it.copy(selectedFuelType = fuelType) }
    }

    private fun onOpenCapacityPicker() {
        _uiState.update { currentState ->
            currentState.copy(
                showCapacityPicker = true,
                pickerValue = currentState.selectedCapacity ?: AddVehicleUiState.PICKER_MIN,
            )
        }
    }

    private fun onCloseCapacityPicker() {
        _uiState.update { it.copy(showCapacityPicker = false) }
    }

    private fun onConfirmCapacityValue(value: Int) {
        _uiState.update { it.copy(selectedCapacity = value, showCapacityPicker = false) }
    }

    private fun onToggleMainVehicle() {
        _uiState.update { it.copy(isMainVehicle = !it.isMainVehicle) }
    }

    private fun onSaveVehicle() {
        val currentState = _uiState.value
        val selectedFuelType = currentState.selectedFuelType ?: return
        val tankCapacity = currentState.selectedCapacity ?: return

        viewModelScope.launch {
            val currentUserId = getUserDataUseCase().first().userId

            val newVehicle = Vehicle(
                userId = currentUserId,
                name = currentState.vehicleName.takeIf { it.isNotBlank() },
                fuelType = selectedFuelType,
                tankCapacity = tankCapacity,
                vehicleType = currentState.selectedVehicleType ?: VehicleType.CAR,
                isPrincipal = currentState.isMainVehicle,
            )

            addVehicleUseCase(vehicle = newVehicle)
            navigationManager.navigateBack()
        }
    }
}
