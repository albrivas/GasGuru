package com.gasguru.feature.vehicle.ui

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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gasguru.core.model.data.VehicleType
import com.gasguru.core.ui.models.FuelTypeUiModel
import com.gasguru.core.ui.models.VehicleTypeUiModel
import com.gasguru.core.uikit.components.GasGuruButton
import com.gasguru.core.uikit.components.capacity_picker.CapacityPickerBottomSheet
import com.gasguru.core.uikit.components.selectedItem.SelectedItem
import com.gasguru.core.uikit.components.selectedItem.SelectedItemModel
import com.gasguru.core.uikit.components.vehicle_type.VehicleTypeCard
import com.gasguru.core.uikit.components.vehicle_type.VehicleTypeCardModel
import com.gasguru.core.uikit.theme.GasGuruTheme
import com.gasguru.core.uikit.theme.MyApplicationTheme
import com.gasguru.core.uikit.theme.ThemePreviews
import com.gasguru.feature.vehicle.R
import com.gasguru.feature.vehicle.viewmodel.AddVehicleUiState
import com.gasguru.feature.vehicle.viewmodel.AddVehicleViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun AddVehicleRoute(
    viewModel: AddVehicleViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    AddVehicleScreen(
        uiState = uiState,
        onEvent = viewModel::handleEvent,
    )
}

@Composable
internal fun AddVehicleScreen(
    uiState: AddVehicleUiState,
    onEvent: (AddVehicleEvent) -> Unit,
) {
    if (uiState.showCapacityPicker) {
        CapacityPickerBottomSheet(
            title = stringResource(id = R.string.add_vehicle_capacity_picker_title),
            subtitle = stringResource(id = R.string.add_vehicle_capacity_picker_range),
            confirmButtonText = stringResource(id = R.string.add_vehicle_capacity_picker_confirm),
            min = AddVehicleUiState.PICKER_MIN,
            max = AddVehicleUiState.PICKER_MAX,
            initialValue = uiState.pickerValue,
            onDismiss = { onEvent(AddVehicleEvent.CloseCapacityPicker) },
            onConfirm = { value -> onEvent(AddVehicleEvent.ConfirmCapacityValue(value = value)) },
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = GasGuruTheme.colors.neutral100),
    ) {
        AddVehicleTopBar(onBack = { onEvent(AddVehicleEvent.Back) })

        LazyColumn(
            modifier = Modifier
                .weight(weight = 1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(space = 0.dp),
        ) {
            item {
                VehicleTypeSection(
                    vehicleTypes = uiState.vehicleTypes,
                    selectedVehicleType = uiState.selectedVehicleType?.let { selectedType ->
                        uiState.vehicleTypes.firstOrNull { it.type == selectedType }
                    },
                    onSelect = { vehicleTypeUiModel ->
                        onEvent(AddVehicleEvent.SelectVehicleType(vehicleType = vehicleTypeUiModel.type))
                    },
                )
            }

            item {
                VehicleNameSection(
                    vehicleName = uiState.vehicleName,
                    onNameChanged = { name -> onEvent(AddVehicleEvent.UpdateVehicleName(name = name)) },
                )
            }

            item {
                FuelSectionHeader()
            }

            item {
                Column(
                    modifier = Modifier
                        .height(height = 300.dp)
                        .fillMaxWidth()
                        .verticalScroll(state = rememberScrollState()),
                ) {
                    uiState.fuelTypes.forEachIndexed { index, fuelTypeUiModel ->
                        SelectedItem(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp)
                                .then(
                                    if (index < uiState.fuelTypes.lastIndex) {
                                        Modifier.padding(bottom = 10.dp)
                                    } else {
                                        Modifier
                                    },
                                ),
                            model = SelectedItemModel(
                                title = fuelTypeUiModel.translationRes,
                                isSelected = fuelTypeUiModel.type == uiState.selectedFuelType,
                                image = fuelTypeUiModel.iconRes,
                                onItemSelected = {
                                    onEvent(AddVehicleEvent.SelectFuelType(fuelType = fuelTypeUiModel.type))
                                },
                            ),
                        )
                    }
                }
            }

            item {
                CapacitySection(
                    selectedCapacity = uiState.selectedCapacity,
                    onClick = { onEvent(AddVehicleEvent.OpenCapacityPicker) },
                )
            }

            item {
                MainVehicleSection(
                    isMainVehicle = uiState.isMainVehicle,
                    onToggle = { onEvent(AddVehicleEvent.ToggleMainVehicle) },
                )
            }

            item {
                Spacer(modifier = Modifier.height(height = 24.dp))
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = GasGuruTheme.colors.neutral100)
                .padding(horizontal = 24.dp, vertical = 32.dp),
        ) {
            GasGuruButton(
                onClick = { onEvent(AddVehicleEvent.SaveVehicle) },
                enabled = uiState.isSaveEnabled,
                text = stringResource(id = R.string.add_vehicle_save_button),
            )
        }
    }
}

@Composable
private fun AddVehicleTopBar(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 4.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                contentDescription = stringResource(id = R.string.add_vehicle_back_content_description),
                tint = GasGuruTheme.colors.neutralBlack,
            )
        }
        Text(
            text = stringResource(id = R.string.add_vehicle_title),
            style = GasGuruTheme.typography.h5,
            color = GasGuruTheme.colors.neutralBlack,
        )
    }
}

@Composable
private fun VehicleTypeSection(
    vehicleTypes: List<VehicleTypeUiModel>,
    selectedVehicleType: VehicleTypeUiModel?,
    onSelect: (VehicleTypeUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(top = 16.dp, bottom = 20.dp),
    ) {
        Text(
            text = stringResource(id = R.string.add_vehicle_type_section),
            style = GasGuruTheme.typography.smallBold,
            color = GasGuruTheme.colors.textMain,
        )
        Spacer(modifier = Modifier.height(height = 12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(space = 8.dp),
        ) {
            vehicleTypes.forEach { vehicleTypeUiModel ->
                VehicleTypeCard(
                    modifier = Modifier.weight(weight = 1f),
                    model = VehicleTypeCardModel(
                        iconRes = vehicleTypeUiModel.iconRes,
                        nameRes = vehicleTypeUiModel.nameRes,
                        isSelected = vehicleTypeUiModel == selectedVehicleType,
                        onClick = { onSelect(vehicleTypeUiModel) },
                    ),
                )
            }
        }
    }
}

@Composable
private fun VehicleNameSection(
    vehicleName: String,
    onNameChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(bottom = 20.dp),
    ) {
        Text(
            text = stringResource(id = R.string.add_vehicle_name_section),
            style = GasGuruTheme.typography.smallBold,
            color = GasGuruTheme.colors.textMain,
        )
        Spacer(modifier = Modifier.height(height = 12.dp))
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape = RoundedCornerShape(size = 14.dp)),
            value = vehicleName,
            onValueChange = onNameChanged,
            placeholder = {
                Text(
                    text = stringResource(id = R.string.add_vehicle_name_placeholder),
                    style = GasGuruTheme.typography.baseRegular,
                    color = GasGuruTheme.colors.neutral500,
                )
            },
            textStyle = GasGuruTheme.typography.baseRegular,
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                imeAction = ImeAction.Done,
            ),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = GasGuruTheme.colors.neutral200,
                unfocusedContainerColor = GasGuruTheme.colors.neutral200,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = GasGuruTheme.colors.primary500,
                focusedTextColor = GasGuruTheme.colors.neutralBlack,
                unfocusedTextColor = GasGuruTheme.colors.neutralBlack,
            ),
        )
    }
}

@Composable
private fun FuelSectionHeader(
    modifier: Modifier = Modifier,
) {
    Text(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(bottom = 12.dp),
        text = stringResource(id = R.string.add_vehicle_fuel_section),
        style = GasGuruTheme.typography.smallBold,
        color = GasGuruTheme.colors.textMain,
    )
}

@Composable
private fun CapacitySection(
    selectedCapacity: Int?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isSelected = selectedCapacity != null
    val shape = RoundedCornerShape(size = 14.dp)
    val borderModifier = if (isSelected) {
        Modifier.border(
            width = 2.dp,
            color = GasGuruTheme.colors.primary500,
            shape = shape,
        )
    } else {
        Modifier
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(top = 20.dp, bottom = 4.dp),
    ) {
        Text(
            text = stringResource(id = R.string.add_vehicle_capacity_section),
            style = GasGuruTheme.typography.smallBold,
            color = GasGuruTheme.colors.textMain,
        )
        Spacer(modifier = Modifier.height(height = 12.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape = shape)
                .background(
                    color = if (isSelected) {
                        GasGuruTheme.colors.primary100.copy(alpha = 0.4f)
                    } else {
                        GasGuruTheme.colors.neutral200
                    },
                )
                .then(borderModifier)
                .clickable { onClick() }
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                modifier = Modifier.weight(weight = 1f),
                text = selectedCapacity?.toString()
                    ?: stringResource(id = R.string.add_vehicle_capacity_placeholder),
                style = GasGuruTheme.typography.baseRegular,
                color = if (isSelected) {
                    GasGuruTheme.colors.neutralBlack
                } else {
                    GasGuruTheme.colors.neutral500
                },
            )
            Text(
                text = stringResource(id = R.string.add_vehicle_capacity_unit),
                style = GasGuruTheme.typography.baseRegular,
                color = GasGuruTheme.colors.neutral600,
                modifier = Modifier.padding(end = 8.dp),
            )
            Icon(
                painter = painterResource(id = com.gasguru.core.uikit.R.drawable.ic_chevron_right),
                contentDescription = null,
                tint = GasGuruTheme.colors.neutral600,
                modifier = Modifier.size(size = 20.dp),
            )
        }
    }
}

@Composable
private fun MainVehicleSection(
    isMainVehicle: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(top = 16.dp, bottom = 8.dp)
            .clip(shape = RoundedCornerShape(size = 12.dp))
            .border(
                width = 1.dp,
                color = GasGuruTheme.colors.neutral300,
                shape = RoundedCornerShape(size = 12.dp)
            )
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(weight = 1f)) {
            Text(
                text = stringResource(id = R.string.add_vehicle_main_vehicle_title),
                style = GasGuruTheme.typography.smallBold,
                color = GasGuruTheme.colors.neutralBlack,
            )
            Spacer(modifier = Modifier.height(height = 4.dp))
            Text(
                text = stringResource(id = R.string.add_vehicle_main_vehicle_subtitle),
                style = GasGuruTheme.typography.captionRegular,
                color = GasGuruTheme.colors.textSubtle,
            )
        }
        Spacer(modifier = Modifier.size(size = 12.dp))
        Switch(
            checked = isMainVehicle,
            onCheckedChange = { onToggle() },
            colors = SwitchDefaults.colors(
                checkedThumbColor = GasGuruTheme.colors.neutralWhite,
                checkedTrackColor = GasGuruTheme.colors.primary500,
                uncheckedThumbColor = GasGuruTheme.colors.neutralWhite,
                uncheckedTrackColor = GasGuruTheme.colors.neutral300,
                uncheckedBorderColor = Color.Transparent,
            ),
        )
    }
}

@Composable
@ThemePreviews
private fun PreviewAddVehicleScreen() {
    MyApplicationTheme {
        AddVehicleScreen(
            uiState = AddVehicleUiState(),
            onEvent = {},
        )
    }
}

@Composable
@ThemePreviews
private fun PreviewAddVehicleScreenFilled() {
    MyApplicationTheme {
        AddVehicleScreen(
            uiState = AddVehicleUiState(
                selectedVehicleType = VehicleType.MOTORCYCLE,
                selectedFuelType = FuelTypeUiModel.ALL_FUELS.first().type,
                selectedCapacity = 55,
                isMainVehicle = true,
            ),
            onEvent = {},
        )
    }
}
