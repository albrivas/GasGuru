package com.gasguru.feature.onboarding_welcome.viewmodel

import androidx.compose.runtime.Immutable

@Immutable
data class CapacityTankUiState(
    val selectedCapacity: Int? = null,
    val showPicker: Boolean = false,
    val pickerValue: Int = PICKER_MIN,
) {
    val isContinueEnabled: Boolean get() = selectedCapacity != null
    val commonValues: List<Int> get() = listOf(40, 45, 50, 55, 60, 70)

    companion object {
        const val PICKER_MIN = 40
        const val PICKER_MAX = 999
    }
}
