package com.gasguru.feature.vehicle.ui

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gasguru.core.model.data.FuelType
import com.gasguru.core.model.data.VehicleType
import com.gasguru.core.ui.models.FuelTypeUiModel
import com.gasguru.core.ui.models.VehicleTypeUiModel
import com.gasguru.core.uikit.components.GasGuruButton
import com.gasguru.core.uikit.components.capacity_picker.CapacityPickerBottomSheet
import com.gasguru.core.uikit.components.drag_handle.DragHandle
import com.gasguru.core.uikit.components.icon.UiKitIcons
import com.gasguru.core.uikit.components.selectedItem.SelectedItem
import com.gasguru.core.uikit.components.selectedItem.SelectedItemModel
import com.gasguru.core.uikit.components.vehicle_type.VehicleTypeCard
import com.gasguru.core.uikit.components.vehicle_type.VehicleTypeCardModel
import com.gasguru.core.uikit.theme.GasGuruTheme
import com.gasguru.core.uikit.theme.MyApplicationTheme
import com.gasguru.core.uikit.theme.ThemePreviews
import com.gasguru.core.uikit.utils.TestTags
import com.gasguru.core.uikit.utils.maestroTestTag
import com.gasguru.feature.vehicle.generated.resources.Res
import com.gasguru.feature.vehicle.generated.resources.add_vehicle_back_content_description
import com.gasguru.feature.vehicle.generated.resources.add_vehicle_capacity_picker_confirm
import com.gasguru.feature.vehicle.generated.resources.add_vehicle_capacity_picker_range
import com.gasguru.feature.vehicle.generated.resources.add_vehicle_capacity_picker_title
import com.gasguru.feature.vehicle.generated.resources.add_vehicle_capacity_placeholder
import com.gasguru.feature.vehicle.generated.resources.add_vehicle_capacity_section
import com.gasguru.feature.vehicle.generated.resources.add_vehicle_capacity_unit
import com.gasguru.feature.vehicle.generated.resources.add_vehicle_fuel_picker_close_content_description
import com.gasguru.feature.vehicle.generated.resources.add_vehicle_fuel_section
import com.gasguru.feature.vehicle.generated.resources.add_vehicle_main_vehicle_subtitle
import com.gasguru.feature.vehicle.generated.resources.add_vehicle_main_vehicle_title
import com.gasguru.feature.vehicle.generated.resources.add_vehicle_name_placeholder
import com.gasguru.feature.vehicle.generated.resources.add_vehicle_name_section
import com.gasguru.feature.vehicle.generated.resources.add_vehicle_save_button
import com.gasguru.feature.vehicle.generated.resources.add_vehicle_title
import com.gasguru.feature.vehicle.generated.resources.add_vehicle_type_section
import com.gasguru.feature.vehicle.generated.resources.edit_vehicle_title
import com.gasguru.feature.vehicle.viewmodel.AddVehicleUiState
import com.gasguru.feature.vehicle.viewmodel.AddVehicleViewModel
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

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
    // Visibilidad de sheets: UI element state, no Screen UI state.
    // No tiene lógica de negocio ni debe sobrevivir a rotación, así que vive local
    // en el composable en vez de en AddVehicleUiState. Ver docs/MVI_EFFECTS.md.
    var showCapacityPicker by remember { mutableStateOf(value = false) }
    var showFuelPicker by remember { mutableStateOf(value = false) }

    if (showCapacityPicker) {
        CapacityPickerBottomSheet(
            title = stringResource(Res.string.add_vehicle_capacity_picker_title),
            subtitle = stringResource(Res.string.add_vehicle_capacity_picker_range),
            confirmButtonText = stringResource(Res.string.add_vehicle_capacity_picker_confirm),
            min = AddVehicleUiState.PICKER_MIN,
            max = AddVehicleUiState.PICKER_MAX,
            initialValue = uiState.selectedCapacity ?: AddVehicleUiState.PICKER_MIN,
            onDismiss = { showCapacityPicker = false },
            onConfirm = { value ->
                onEvent(AddVehicleEvent.ConfirmCapacityValue(value = value))
                showCapacityPicker = false
            },
        )
    }

    if (showFuelPicker) {
        FuelTypePickerBottomSheet(
            fuelTypes = uiState.fuelTypes,
            selectedFuelType = uiState.selectedFuelType,
            onSelect = { fuelType -> onEvent(AddVehicleEvent.SelectFuelType(fuelType = fuelType)) },
            onDismiss = { showFuelPicker = false },
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = GasGuruTheme.colors.neutral100),
    ) {
        AddVehicleTopBar(onBack = { onEvent(AddVehicleEvent.Back) }, isEditMode = uiState.isEditMode)

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
                FuelSection(
                    selectedFuel = uiState.fuelTypes.firstOrNull { it.type == uiState.selectedFuelType },
                    onClick = { showFuelPicker = true },
                )
            }

            item {
                CapacitySection(
                    selectedCapacity = uiState.selectedCapacity,
                    onClick = { showCapacityPicker = true },
                )
            }

            item {
                MainVehicleSection(
                    isMainVehicle = uiState.isMainVehicle,
                    isToggleEnabled = !uiState.isOriginallyPrincipal,
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
                .systemBarsPadding()
                .padding(horizontal = 24.dp, vertical = 32.dp),
        ) {
            GasGuruButton(
                onClick = { onEvent(AddVehicleEvent.SaveVehicle) },
                enabled = uiState.isSaveEnabled,
                text = stringResource(Res.string.add_vehicle_save_button),
            )
        }
    }
}

@Composable
private fun AddVehicleTopBar(
    onBack: () -> Unit,
    isEditMode: Boolean,
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
                contentDescription = stringResource(Res.string.add_vehicle_back_content_description),
                tint = GasGuruTheme.colors.neutralBlack,
            )
        }
        Text(
            text = stringResource(if (isEditMode) Res.string.edit_vehicle_title else Res.string.add_vehicle_title),
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
            text = stringResource(Res.string.add_vehicle_type_section),
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
                        nameRes = stringResource(vehicleTypeUiModel.nameRes),
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
            text = stringResource(Res.string.add_vehicle_name_section),
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
                    text = stringResource(Res.string.add_vehicle_name_placeholder),
                    style = GasGuruTheme.typography.baseRegular,
                    color = GasGuruTheme.colors.neutral600,
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
        text = stringResource(Res.string.add_vehicle_fuel_section),
        style = GasGuruTheme.typography.smallBold,
        color = GasGuruTheme.colors.textMain,
    )
}

@Composable
private fun FuelSection(
    selectedFuel: FuelTypeUiModel?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val hasValue = selectedFuel != null
    val shape = RoundedCornerShape(size = 14.dp)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(bottom = 20.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape = shape)
                .background(color = GasGuruTheme.colors.neutral200)
                .clickable { onClick() }
                .maestroTestTag(TestTags.Vehicle.FUEL_SELECTOR)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (selectedFuel != null) {
                Image(
                    modifier = Modifier
                        .size(size = 44.dp)
                        .clip(shape = RoundedCornerShape(size = 12.dp)),
                    painter = painterResource(selectedFuel.iconRes),
                    contentScale = ContentScale.Crop,
                    contentDescription = null,
                )
                Spacer(modifier = Modifier.size(size = 14.dp))
            }
            Text(
                modifier = Modifier.weight(weight = 1f),
                text = selectedFuel?.let { stringResource(it.translationRes) }
                    ?: stringResource(Res.string.add_vehicle_fuel_section),
                style = GasGuruTheme.typography.baseRegular,
                color = if (hasValue) {
                    GasGuruTheme.colors.neutralBlack
                } else {
                    GasGuruTheme.colors.neutral600
                },
            )
            Icon(
                painter = painterResource(UiKitIcons.ChevronRight),
                contentDescription = null,
                tint = GasGuruTheme.colors.neutral600,
                modifier = Modifier.size(size = 20.dp),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FuelTypePickerBottomSheet(
    fuelTypes: List<FuelTypeUiModel>,
    selectedFuelType: FuelType?,
    onSelect: (FuelType) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val sheetState = rememberModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()

    fun animatedDismiss() {
        coroutineScope.launch { sheetState.hide() }.invokeOnCompletion {
            if (!sheetState.isVisible) {
                onDismiss()
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = GasGuruTheme.colors.neutral100,
        contentColor = GasGuruTheme.colors.neutral100,
        shape = MaterialTheme.shapes.large,
        dragHandle = { DragHandle() },
        modifier = modifier.statusBarsPadding(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(Res.string.add_vehicle_fuel_section),
                style = GasGuruTheme.typography.baseBold,
                color = GasGuruTheme.colors.textMain,
                modifier = Modifier.weight(weight = 1f),
            )
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = stringResource(Res.string.add_vehicle_fuel_picker_close_content_description),
                tint = GasGuruTheme.colors.neutralBlack,
                modifier = Modifier
                    .size(size = 32.dp)
                    .maestroTestTag(TestTags.Vehicle.FUEL_PICKER_CLOSE)
                    .clickable { animatedDismiss() },
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(state = rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
        ) {
            fuelTypes.forEachIndexed { index, fuelTypeUiModel ->
                SelectedItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(
                            if (index < fuelTypes.lastIndex) {
                                Modifier.padding(bottom = 10.dp)
                            } else {
                                Modifier
                            },
                        ),
                    model = SelectedItemModel(
                        title = stringResource(fuelTypeUiModel.translationRes),
                        isSelected = fuelTypeUiModel.type == selectedFuelType,
                        image = fuelTypeUiModel.iconRes,
                        onItemSelected = {
                            onSelect(fuelTypeUiModel.type)
                            animatedDismiss()
                        },
                    ),
                )
            }
        }
    }
}

@Composable
private fun CapacitySection(
    selectedCapacity: Int?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val hasValue = selectedCapacity != null
    val shape = RoundedCornerShape(size = 14.dp)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(top = 20.dp, bottom = 4.dp),
    ) {
        Text(
            text = stringResource(Res.string.add_vehicle_capacity_section),
            style = GasGuruTheme.typography.smallBold,
            color = GasGuruTheme.colors.textMain,
        )
        Spacer(modifier = Modifier.height(height = 12.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape = shape)
                .background(color = GasGuruTheme.colors.neutral200)
                .clickable { onClick() }
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                modifier = Modifier.weight(weight = 1f),
                text = selectedCapacity?.toString()
                    ?: stringResource(Res.string.add_vehicle_capacity_placeholder),
                style = GasGuruTheme.typography.baseRegular,
                color = if (hasValue) {
                    GasGuruTheme.colors.neutralBlack
                } else {
                    GasGuruTheme.colors.neutral600
                },
            )
            Text(
                text = stringResource(Res.string.add_vehicle_capacity_unit),
                style = GasGuruTheme.typography.baseRegular,
                color = GasGuruTheme.colors.neutral600,
                modifier = Modifier.padding(end = 8.dp),
            )
            Icon(
                painter = painterResource(UiKitIcons.ChevronRight),
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
    isToggleEnabled: Boolean,
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
                shape = RoundedCornerShape(size = 12.dp),
            )
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(weight = 1f)) {
            Text(
                text = stringResource(Res.string.add_vehicle_main_vehicle_title),
                style = GasGuruTheme.typography.smallBold,
                color = GasGuruTheme.colors.neutralBlack,
            )
            Spacer(modifier = Modifier.height(height = 4.dp))
            Text(
                text = stringResource(Res.string.add_vehicle_main_vehicle_subtitle),
                style = GasGuruTheme.typography.captionRegular,
                color = GasGuruTheme.colors.textSubtle,
            )
        }
        Spacer(modifier = Modifier.size(size = 12.dp))
        Switch(
            checked = isMainVehicle,
            onCheckedChange = { onToggle() },
            enabled = isToggleEnabled,
            colors = SwitchDefaults.colors(
                checkedThumbColor = GasGuruTheme.colors.neutralWhite,
                checkedTrackColor = GasGuruTheme.colors.primary500,
                uncheckedThumbColor = GasGuruTheme.colors.neutralWhite,
                uncheckedTrackColor = GasGuruTheme.colors.neutral600,
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
