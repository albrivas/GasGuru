package com.gasguru.feature.profile.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gasguru.core.common.CommonUtils.getAppVersion
import com.gasguru.core.model.data.FuelType
import com.gasguru.core.ui.models.FuelTypeUiModel
import com.gasguru.core.ui.models.ThemeModeUi
import com.gasguru.core.ui.toFuelType
import com.gasguru.core.uikit.components.filter_sheet.FilterSheet
import com.gasguru.core.uikit.components.filter_sheet.FilterSheetModel
import com.gasguru.core.uikit.components.filter_sheet.FilterSheetType
import com.gasguru.core.uikit.components.fuel_list.FuelListSelection
import com.gasguru.core.uikit.components.fuel_list.FuelListSelectionModel
import com.gasguru.core.uikit.components.settings.SettingItem
import com.gasguru.core.uikit.components.settings.SettingItemModel
import com.gasguru.core.uikit.theme.GasGuruTheme
import com.gasguru.core.uikit.theme.MyApplicationTheme
import com.gasguru.core.uikit.theme.ThemePreviews
import com.gasguru.feature.profile.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import com.gasguru.core.ui.R as RUi
import com.gasguru.core.uikit.R as RUikit

@Composable
internal fun ProfileScreenRoute(viewModel: ProfileViewModel = hiltViewModel()) {
    val state by viewModel.userData.collectAsStateWithLifecycle()
    return ProfileScreen(uiState = state, event = viewModel::handleEvents)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ProfileScreen(uiState: ProfileUiState, event: (ProfileEvents) -> Unit) {
    var activeSheet by remember { mutableStateOf<ProfileSheet>(ProfileSheet.None) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    when (uiState) {
        is ProfileUiState.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(GasGuruTheme.colors.neutralWhite)
                    .statusBarsPadding()
                    .testTag("loading"),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }

        is ProfileUiState.Success -> {
            SuccessContent(
                content = uiState.content,
                onSheetRequest = { sheet -> activeSheet = sheet }
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
            sheetState = sheetState,
            scope = scope
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileSheetHandler(
    activeSheet: ProfileSheet,
    onDismiss: () -> Unit,
    onEvent: (ProfileEvents) -> Unit,
    content: ProfileContentUi,
    sheetState: SheetState,
    scope: CoroutineScope
) {
    when (activeSheet) {
        ProfileSheet.None -> { Unit }
        ProfileSheet.Theme -> {
            ThemeModeSheet(
                selectedTheme = content.themeUi,
                allThemesUi = content.allThemesUi,
                onDismiss = onDismiss,
                onThemeSelected = { theme ->
                    onEvent(ProfileEvents.Theme(theme))
                    onDismiss()
                }
            )
        }
        ProfileSheet.Fuel -> {
            FuelSelectionSheet(
                selectedFuel = content.fuelTranslation,
                onDismiss = onDismiss,
                onFuelSelected = { fuel ->
                    onEvent(ProfileEvents.Fuel(fuel.toFuelType()))
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        if (!sheetState.isVisible) {
                            onDismiss()
                        }
                    }
                },
                sheetState = sheetState
            )
        }
    }
}

@Composable
fun SuccessContent(content: ProfileContentUi, onSheetRequest: (ProfileSheet) -> Unit) {
    Column(
        modifier = Modifier
            .background(color = GasGuruTheme.colors.neutral100)
            .fillMaxSize()
            .statusBarsPadding()
            .padding(start = 16.dp, end = 16.dp, top = 32.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            modifier = Modifier,
            text = stringResource(id = R.string.profile),
            style = GasGuruTheme.typography.h5,
            color = GasGuruTheme.colors.textMain
        )
        SettingItem(
            model = SettingItemModel(
                title = stringResource(id = R.string.fuel_selection),
                selection = stringResource(id = content.fuelTranslation),
                icon = RUikit.drawable.ic_fuel_station,
                onClick = { onSheetRequest(ProfileSheet.Fuel) },
            ),
            modifier = Modifier.testTag("fuel_setting_item")
        )
        SettingItem(
            model = SettingItemModel(
                title = stringResource(id = R.string.theme_mode),
                selection = stringResource(id = content.themeUi.titleRes),
                icon = content.themeUi.iconRes,
                onClick = { onSheetRequest(ProfileSheet.Theme) },
            ),
            modifier = Modifier.testTag("theme_setting_item")
        )
        Spacer(modifier = Modifier.weight(1f))
        VersionAppInfo(modifier = Modifier.padding(bottom = 12.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FuelSelectionSheet(
    selectedFuel: Int,
    onDismiss: () -> Unit,
    onFuelSelected: (Int) -> Unit,
    sheetState: SheetState
) {
    ModalBottomSheet(
        modifier = Modifier
            .testTag("bottom_sheet_fuel"),
        onDismissRequest = onDismiss,
        dragHandle = {
            Surface(
                modifier = Modifier.padding(vertical = 8.dp),
                color = GasGuruTheme.colors.neutral700,
                shape = MaterialTheme.shapes.extraLarge
            ) {
                Box(
                    modifier = Modifier.size(
                        width = 32.dp,
                        height = 4.0.dp
                    )
                )
            }
        },
        shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
        sheetState = sheetState,
        containerColor = GasGuruTheme.colors.neutral100,
        contentColor = GasGuruTheme.colors.neutral100,
        contentWindowInsets = { WindowInsets.navigationBars }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(id = RUi.string.select_fuel_preference),
                style = GasGuruTheme.typography.baseBold,
                color = GasGuruTheme.colors.textMain
            )
            val list = FuelType.entries.map {
                val fuelUiModel = FuelTypeUiModel.fromFuelType(it)
                Pair(fuelUiModel.iconRes, fuelUiModel.translationRes)
            }
            FuelListSelection(
                model = FuelListSelectionModel(
                    list = list,
                    selected = selectedFuel,
                    onItemSelected = onFuelSelected
                )
            )
        }
    }
}

@Composable
fun ThemeModeSheet(
    selectedTheme: ThemeModeUi,
    allThemesUi: List<ThemeModeUi>,
    onDismiss: () -> Unit,
    onThemeSelected: (ThemeModeUi) -> Unit
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
                getAppVersion()
            ),
            style = GasGuruTheme.typography.captionRegular,
            color = GasGuruTheme.colors.textSubtle
        )
    }
}

@Composable
@ThemePreviews
private fun ProfileScreenPreview(
    @PreviewParameter(ProfileContentUiPreviewParameterProvider::class) content: ProfileContentUi
) {
    MyApplicationTheme {
        ProfileScreen(
            uiState = ProfileUiState.Success(content = content),
            event = { }
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
