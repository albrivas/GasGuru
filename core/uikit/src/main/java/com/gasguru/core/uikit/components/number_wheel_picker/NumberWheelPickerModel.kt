package com.gasguru.core.uikit.components.number_wheel_picker

data class NumberWheelPickerModel(
    val min: Int,
    val max: Int,
    val initialValue: Int,
    val onValueChanged: (Int) -> Unit,
) {
    init {
        require(min < max) { "min ($min) must be less than max ($max)" }
        require(initialValue in min..max) { "initialValue ($initialValue) must be between min ($min) and max ($max)" }
    }
}
