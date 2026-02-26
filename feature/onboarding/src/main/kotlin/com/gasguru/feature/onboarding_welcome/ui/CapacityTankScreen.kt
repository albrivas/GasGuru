package com.gasguru.feature.onboarding_welcome.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gasguru.core.uikit.components.GasGuruButton
import com.gasguru.core.uikit.components.number_wheel_picker.NumberWheelPicker
import com.gasguru.core.uikit.components.number_wheel_picker.NumberWheelPickerModel
import com.gasguru.core.uikit.theme.GasGuruTheme
import com.gasguru.core.uikit.theme.MyApplicationTheme
import com.gasguru.core.uikit.theme.ThemePreviews
import com.gasguru.feature.onboarding.R
import com.gasguru.feature.onboarding_welcome.viewmodel.CapacityTankUiState
import com.gasguru.feature.onboarding_welcome.viewmodel.CapacityTankViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun CapacityTankRoute(
    viewModel: CapacityTankViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    CapacityTankScreen(
        uiState = uiState,
        onEvent = viewModel::handleEvent,
    )
}

@Composable
internal fun CapacityTankScreen(
    uiState: CapacityTankUiState,
    onEvent: (CapacityTankEvent) -> Unit,
) {
    if (uiState.showPicker) {
        CapacityPickerBottomSheet(
            initialValue = uiState.pickerValue,
            onDismiss = { onEvent(CapacityTankEvent.ClosePicker) },
            onConfirm = { value -> onEvent(CapacityTankEvent.ConfirmPickerValue(value = value)) },
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = GasGuruTheme.colors.neutral100),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, top = 60.dp, end = 24.dp, bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(id = R.string.capacity_tank_title),
                style = GasGuruTheme.typography.h4,
                color = GasGuruTheme.colors.neutralBlack,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(id = R.string.capacity_tank_subtitle),
                style = GasGuruTheme.typography.smallRegular,
                color = GasGuruTheme.colors.textSubtle,
                textAlign = TextAlign.Center,
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            CapacityDisplayBox(
                selectedCapacity = uiState.selectedCapacity,
                onClick = { onEvent(CapacityTankEvent.OpenPicker) },
            )
            Spacer(modifier = Modifier.height(32.dp))
            CommonCapacityValues(
                values = uiState.commonValues,
                selectedValue = uiState.selectedCapacity,
                onValueSelected = { value ->
                    onEvent(CapacityTankEvent.SelectCommonValue(value = value))
                },
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .systemBarsPadding()
                .padding(start = 24.dp, end = 24.dp, bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            GasGuruButton(
                onClick = { onEvent(CapacityTankEvent.Continue) },
                enabled = uiState.isContinueEnabled,
                text = stringResource(id = R.string.onboarding_continue),
                modifier = Modifier.testTag("button_capacity_continue"),
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(id = R.string.capacity_tank_hint),
                style = GasGuruTheme.typography.captionRegular,
                color = GasGuruTheme.colors.neutral600,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun CapacityDisplayBox(
    selectedCapacity: Int?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isSelected = selectedCapacity != null
    val backgroundColor = if (isSelected) {
        GasGuruTheme.colors.primary100.copy(alpha = 0.4f)
    } else {
        GasGuruTheme.colors.neutral200
    }
    val contentColor = if (isSelected) {
        GasGuruTheme.colors.primary500
    } else {
        GasGuruTheme.colors.neutral500
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(140.dp)
            .clip(shape = RoundedCornerShape(size = 20.dp))
            .background(color = backgroundColor)
            .then(
                if (isSelected) {
                    Modifier.border(
                        width = 2.dp,
                        color = GasGuruTheme.colors.primary500,
                        shape = RoundedCornerShape(size = 20.dp),
                    )
                } else {
                    Modifier
                },
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = selectedCapacity?.toString() ?: "---",
                    fontSize = 56.sp,
                    fontWeight = FontWeight.Bold,
                    color = contentColor,
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "L",
                    style = GasGuruTheme.typography.h3,
                    color = contentColor,
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            if (isSelected) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(space = 4.dp),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Check,
                        contentDescription = null,
                        tint = GasGuruTheme.colors.primary500,
                        modifier = Modifier.size(14.dp),
                    )
                    Text(
                        text = stringResource(id = R.string.capacity_tank_saved),
                        style = GasGuruTheme.typography.captionBold,
                        color = GasGuruTheme.colors.primary500,
                    )
                }
            } else {
                Text(
                    text = stringResource(id = R.string.capacity_tank_display_hint),
                    style = GasGuruTheme.typography.captionRegular,
                    color = GasGuruTheme.colors.neutral600,
                )
            }
        }
    }
}

@Composable
private fun CommonCapacityValues(
    values: List<Int>,
    selectedValue: Int?,
    onValueSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(id = R.string.capacity_tank_common_values),
            style = GasGuruTheme.typography.smallBold,
            color = GasGuruTheme.colors.textSubtle,
        )
        Spacer(modifier = Modifier.height(12.dp))
        values.chunked(size = 3).forEach { rowValues ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(space = 10.dp),
            ) {
                rowValues.forEach { value ->
                    CapacityValueChip(
                        value = value,
                        isSelected = selectedValue == value,
                        onClick = { onValueSelected(value) },
                        modifier = Modifier.weight(weight = 1f),
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
private fun CapacityValueChip(
    value: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val backgroundColor = if (isSelected) {
        GasGuruTheme.colors.primary100.copy(alpha = 0.4f)
    } else {
        GasGuruTheme.colors.neutral200
    }
    val textColor = if (isSelected) {
        GasGuruTheme.colors.primary500
    } else {
        GasGuruTheme.colors.neutralBlack
    }

    Box(
        modifier = modifier
            .height(52.dp)
            .clip(shape = RoundedCornerShape(size = 14.dp))
            .background(color = backgroundColor)
            .then(
                if (isSelected) {
                    Modifier.border(
                        width = 2.dp,
                        color = GasGuruTheme.colors.primary500,
                        shape = RoundedCornerShape(size = 14.dp),
                    )
                } else {
                    Modifier
                },
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "$value L",
            style = GasGuruTheme.typography.baseBold,
            color = textColor,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CapacityPickerBottomSheet(
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
                text = stringResource(id = R.string.capacity_picker_title),
                style = GasGuruTheme.typography.h5,
                color = GasGuruTheme.colors.neutralBlack,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.capacity_picker_range),
                style = GasGuruTheme.typography.captionRegular,
                color = GasGuruTheme.colors.neutral600,
            )
            Spacer(modifier = Modifier.height(16.dp))
            NumberWheelPicker(
                model = NumberWheelPickerModel(
                    min = CapacityTankUiState.PICKER_MIN,
                    max = CapacityTankUiState.PICKER_MAX,
                    initialValue = initialValue,
                    onValueChanged = { value -> currentValue = value },
                ),
                modifier = Modifier
                    .height(156.dp)
                    .fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(16.dp))
            GasGuruButton(
                onClick = { onConfirm(currentValue) },
                text = stringResource(id = R.string.capacity_picker_confirm),
            )
        }
    }
}

@Composable
@ThemePreviews
private fun PreviewCapacityTankEmpty() {
    MyApplicationTheme {
        CapacityTankScreen(
            uiState = CapacityTankUiState(),
            onEvent = {},
        )
    }
}

@Composable
@ThemePreviews
private fun PreviewCapacityTankSelected() {
    MyApplicationTheme {
        CapacityTankScreen(
            uiState = CapacityTankUiState(selectedCapacity = 45),
            onEvent = {},
        )
    }
}
