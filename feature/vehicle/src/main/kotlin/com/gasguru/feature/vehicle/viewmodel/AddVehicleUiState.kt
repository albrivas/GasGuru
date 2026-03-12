package com.gasguru.feature.vehicle.viewmodel

import androidx.compose.runtime.Immutable
import com.gasguru.core.model.data.FuelType
import com.gasguru.core.model.data.VehicleType
import com.gasguru.core.ui.models.FuelTypeUiModel
import com.gasguru.core.ui.models.VehicleTypeUiModel

@Immutable
data class AddVehicleUiState(
    val vehicleId: Long? = null,
    val isEditMode: Boolean = false,
    val vehicleTypes: List<VehicleTypeUiModel> = VehicleTypeUiModel.ALL_TYPES,
    val selectedVehicleType: VehicleType? = null,
    val vehicleName: String = "",
    val fuelTypes: List<FuelTypeUiModel> = FuelTypeUiModel.ALL_FUELS,
    val selectedFuelType: FuelType? = null,
    val selectedCapacity: Int? = null,
    val isMainVehicle: Boolean = false,
    val isOriginallyPrincipal: Boolean = false,
    val showCapacityPicker: Boolean = false,
    val pickerValue: Int = PICKER_MIN,
) {
    val isSaveEnabled: Boolean
        get() = selectedFuelType != null && selectedCapacity != null

    companion object {
        const val PICKER_MIN = 1
        const val PICKER_MAX = 999
    }
}
