package com.gasguru.feature.vehicle.ui

import com.gasguru.core.model.data.FuelType
import com.gasguru.core.model.data.VehicleType

sealed class AddVehicleEvent {
    data class SelectVehicleType(val vehicleType: VehicleType) : AddVehicleEvent()
    data class UpdateVehicleName(val name: String) : AddVehicleEvent()
    data class SelectFuelType(val fuelType: FuelType) : AddVehicleEvent()
    data object OpenCapacityPicker : AddVehicleEvent()
    data object CloseCapacityPicker : AddVehicleEvent()
    data class ConfirmCapacityValue(val value: Int) : AddVehicleEvent()
    data object ToggleMainVehicle : AddVehicleEvent()
    data object SaveVehicle : AddVehicleEvent()
    data object Back : AddVehicleEvent()
}
