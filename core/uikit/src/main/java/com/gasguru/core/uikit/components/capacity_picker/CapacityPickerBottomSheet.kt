package com.gasguru.core.uikit.components.capacity_picker

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gasguru.core.uikit.components.GasGuruButton
import com.gasguru.core.uikit.components.number_wheel_picker.NumberWheelPicker
import com.gasguru.core.uikit.components.number_wheel_picker.NumberWheelPickerModel
import com.gasguru.core.uikit.theme.GasGuruTheme

@OptIn(ExperimentalMaterial3Api::class)
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

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = GasGuruTheme.colors.neutralWhite,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = title,
                style = GasGuruTheme.typography.h5,
                color = GasGuruTheme.colors.neutralBlack,
            )
            Spacer(modifier = Modifier.height(height = 4.dp))
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = subtitle,
                style = GasGuruTheme.typography.captionRegular,
                color = GasGuruTheme.colors.neutral600,
            )
            Spacer(modifier = Modifier.height(height = 16.dp))
            NumberWheelPicker(
                model = NumberWheelPickerModel(
                    min = min,
                    max = max,
                    initialValue = initialValue,
                    onValueChanged = { value -> currentValue = value },
                ),
                modifier = Modifier
                    .height(height = 156.dp)
                    .fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(height = 16.dp))
            GasGuruButton(
                onClick = { onConfirm(currentValue) },
                text = confirmButtonText,
            )
        }
    }
}
