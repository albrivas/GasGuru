package com.gasguru.feature.onboarding_welcome.ui

sealed interface CapacityTankEvent {
    data class SelectCommonValue(val value: Int) : CapacityTankEvent
    data object OpenPicker : CapacityTankEvent
    data object ClosePicker : CapacityTankEvent
    data class ConfirmPickerValue(val value: Int) : CapacityTankEvent
    data object Continue : CapacityTankEvent
}
