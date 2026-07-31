package com.gasguru.core.uikit.components.capacity_picker

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gasguru.core.uikit.components.bottom_sheet.GasGuruBottomSheet
import com.gasguru.core.uikit.components.bottom_sheet.GasGuruBottomSheetAction
import com.gasguru.core.uikit.components.bottom_sheet.GasGuruBottomSheetModel
import com.gasguru.core.uikit.components.number_wheel_picker.NumberWheelPicker
import com.gasguru.core.uikit.components.number_wheel_picker.NumberWheelPickerModel

@Composable
fun CapacityPickerBottomSheet(
    title: String,
    subtitle: String,
    confirmButtonText: String,
    min: Int,
    max: Int,
    initialValue: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit,
) {
    var currentValue by remember { mutableIntStateOf(value = initialValue) }

    GasGuruBottomSheet(
        model = GasGuruBottomSheetModel(
            title = title,
            subtitle = subtitle,
            onDismiss = onDismiss,
            actionButton = GasGuruBottomSheetAction(
                text = confirmButtonText,
                onClick = { onConfirm(currentValue) },
            ),
            skipPartiallyExpanded = true,
        ),
    ) {
        NumberWheelPicker(
            model = NumberWheelPickerModel(
                min = min,
                max = max,
                initialValue = initialValue,
                onValueChanged = { value -> currentValue = value },
            ),
            modifier = Modifier
                .height(height = 156.dp)
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
        )
    }
}
