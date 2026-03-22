package com.gasguru.feature.profile.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gasguru.core.common.getAppVersion
import com.gasguru.core.ui.models.ThemeModeUi
import com.gasguru.core.uikit.components.divider.DividerLength
import com.gasguru.core.uikit.components.divider.DividerThickness
import com.gasguru.core.uikit.components.divider.GasGuruDivider
import com.gasguru.core.uikit.components.divider.GasGuruDividerModel
import com.gasguru.core.uikit.components.filter_sheet.FilterSheet
import com.gasguru.core.uikit.components.filter_sheet.FilterSheetModel
import com.gasguru.core.uikit.components.filter_sheet.FilterSheetType
import com.gasguru.core.uikit.components.loading.GasGuruLoading
import com.gasguru.core.uikit.components.loading.GasGuruLoadingModel
import com.gasguru.core.uikit.components.settings.SettingItem
import com.gasguru.core.uikit.components.settings.SettingItemModel
import com.gasguru.core.uikit.components.swipe.SwipeItem
import com.gasguru.core.uikit.components.swipe.SwipeItemModel
import com.gasguru.core.uikit.components.vehicle_item.VehicleItemCard
import com.gasguru.core.uikit.theme.GasGuruTheme
import com.gasguru.core.uikit.theme.MyApplicationTheme
import com.gasguru.core.uikit.theme.ThemePreviews
import com.gasguru.feature.profile.R
import org.koin.androidx.compose.koinViewModel
import com.gasguru.core.uikit.R as RUikit

@Composable
internal fun ProfileScreenRoute(viewModel: ProfileViewModel = koinViewModel()) {
    val state by viewModel.userData.collectAsStateWithLifecycle()
    return ProfileScreen(uiState = state, event = viewModel::handleEvents)
}

@Composable
internal fun ProfileScreen(uiState: ProfileUiState, event: (ProfileEvents) -> Unit) {
    var activeSheet by remember { mutableStateOf<ProfileSheet>(ProfileSheet.None) }

    when (uiState) {
        is ProfileUiState.Loading -> {
            GasGuruLoading(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .testTag("loading"),
                model = GasGuruLoadingModel(color = GasGuruTheme.colors.primary800),
            )
        }

        is ProfileUiState.Success -> {
            SuccessContent(
                content = uiState.content,
                onSheetRequest = { sheet -> activeSheet = sheet },
                onAddVehicle = { event(ProfileEvents.AddVehicle) },
                onEditVehicle = { vehicleId -> event(ProfileEvents.EditVehicle(vehicleId = vehicleId)) },
                onDeleteVehicle = { vehicleId -> event(ProfileEvents.DeleteVehicle(vehicleId = vehicleId)) },
            )
        }

        is ProfileUiState.LoadFailed -> {
            // Error state
        }
    }

    if (uiState is ProfileUiState.Success) {
        ProfileSheetHandler(
            activeSheet = activeSheet,
            onDismiss = { activeSheet = ProfileSheet.None },
            onEvent = event,
            content = uiState.content,
        )
    }
}

@Composable
private fun ProfileSheetHandler(
    activeSheet: ProfileSheet,
    onDismiss: () -> Unit,
    onEvent: (ProfileEvents) -> Unit,
    content: ProfileContentUi,
) {
    when (activeSheet) {
        ProfileSheet.None -> Unit

        ProfileSheet.Theme -> {
            ThemeModeSheet(
                selectedTheme = content.themeUi,
                allThemesUi = content.allThemesUi,
                onDismiss = onDismiss,
                onThemeSelected = { theme ->
                    onEvent(ProfileEvents.Theme(theme = theme))
                    onDismiss()
                },
            )
        }
    }
}

@Composable
fun SuccessContent(
    content: ProfileContentUi,
    onSheetRequest: (ProfileSheet) -> Unit,
    onAddVehicle: () -> Unit,
    onEditVehicle: (Long) -> Unit,
    onDeleteVehicle: (Long) -> Unit,
) {
    Column(
        modifier = Modifier
            .background(color = GasGuruTheme.colors.neutral100)
            .fillMaxWidth()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(start = 16.dp, end = 16.dp, top = 32.dp, bottom = 12.dp)
            .pointerInput(Unit) { detectTapGestures { } }, // For overlay map
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            modifier = Modifier,
            text = stringResource(id = R.string.profile),
            style = GasGuruTheme.typography.h5,
            color = GasGuruTheme.colors.textMain,
        )
        Text(
            text = stringResource(id = R.string.my_vehicles),
            style = GasGuruTheme.typography.captionBold,
            color = GasGuruTheme.colors.textSubtle,
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(color = GasGuruTheme.colors.neutralWhite)
                .border(
                    width = 1.dp,
                    color = GasGuruTheme.colors.neutral300,
                    shape = RoundedCornerShape(14.dp)
                )
                .animateContentSize(),
        ) {
            content.vehicles.forEachIndexed { index, vehicle ->
                if (index > 0) {
                    GasGuruDivider(
                        model = GasGuruDividerModel(
                            color = GasGuruTheme.colors.neutral300,
                            length = DividerLength.INSET,
                            thickness = DividerThickness.THICK,
                        )
                    )
                }
                if (content.vehicles.size > 1) {
                    SwipeItem(
                        model = SwipeItemModel(
                            iconAnimated = com.gasguru.core.ui.R.raw.trash_animated,
                            backgroundColor = GasGuruTheme.colors.red500,
                            onClick = { onDeleteVehicle(vehicle.id) },
                        ),
                    ) {
                        VehicleItemCard(model = vehicle, onClick = { onEditVehicle(vehicle.id) })
                    }
                } else {
                    VehicleItemCard(model = vehicle, onClick = { onEditVehicle(vehicle.id) })
                }
            }
            GasGuruDivider(
                model = GasGuruDividerModel(
                    color = GasGuruTheme.colors.neutral300,
                    length = DividerLength.FULL,
                    thickness = DividerThickness.THICK,
                )
            )
            AddVehicleButton(onClick = onAddVehicle)
        }
        Text(
            text = stringResource(id = R.string.settings),
            style = GasGuruTheme.typography.captionBold,
            color = GasGuruTheme.colors.textSubtle,
        )
        SettingItem(
            model = SettingItemModel(
                title = stringResource(id = R.string.theme_mode),
                selection = stringResource(id = content.themeUi.titleRes),
                icon = content.themeUi.iconRes,
                onClick = { onSheetRequest(ProfileSheet.Theme) },
            ),
            modifier = Modifier.testTag("theme_setting_item"),
        )
        VersionAppInfo()
    }
}

@Composable
fun ThemeModeSheet(
    selectedTheme: ThemeModeUi,
    allThemesUi: List<ThemeModeUi>,
    onDismiss: () -> Unit,
    onThemeSelected: (ThemeModeUi) -> Unit,
) {
    val themeOptions = allThemesUi.map { stringResource(it.titleRes) }
    val selectedOption = stringResource(selectedTheme.titleRes)

    FilterSheet(
        model = FilterSheetModel(
            title = stringResource(R.string.theme_mode),
            buttonText = "Save",
            isMultiOption = false,
            isMustSelection = true,
            options = themeOptions,
            optionsSelected = listOf(selectedOption),
            onDismiss = onDismiss,
            onSaveButton = { selectedOptions ->
                val selectedIndex = themeOptions.indexOf(selectedOptions.first())
                onThemeSelected(allThemesUi[selectedIndex])
            },
            type = FilterSheetType.NORMAL,
        )
    )
}

@Composable
fun VersionAppInfo(modifier: Modifier = Modifier) {
    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        Text(
            modifier = Modifier,
            text = stringResource(
                id = R.string.version,
                getAppVersion(),
            ),
            style = GasGuruTheme.typography.captionRegular,
            color = GasGuruTheme.colors.textSubtle,
        )
    }
}

@Composable
private fun AddVehicleButton(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = null,
            tint = GasGuruTheme.colors.neutral800,
            modifier = Modifier.size(16.dp),
        )
        Spacer(modifier = Modifier.padding(4.dp))
        Text(
            text = stringResource(id = RUikit.string.vehicle_add),
            style = GasGuruTheme.typography.smallRegular,
            color = GasGuruTheme.colors.textSubtle,
        )
    }
}

@Composable
@ThemePreviews
private fun ProfileScreenPreview(
    @PreviewParameter(ProfileContentUiPreviewParameterProvider::class) content: ProfileContentUi,
) {
    MyApplicationTheme {
        ProfileScreen(
            uiState = ProfileUiState.Success(content = content),
            event = { },
        )
    }
}

@Composable
@ThemePreviews
private fun ProfileScreenLoadingPreview() {
    MyApplicationTheme {
        ProfileScreen(uiState = ProfileUiState.Loading, event = {})
    }
}
