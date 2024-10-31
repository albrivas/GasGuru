package com.gasguru.feature.profile.ui

import android.content.Context
import android.os.Build
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gasguru.core.model.data.FuelType
import com.gasguru.core.model.data.UserData
import com.gasguru.core.ui.getIcon
import com.gasguru.core.ui.toFuelType
import com.gasguru.core.ui.translation
import com.gasguru.core.uikit.components.settings.SettingItem
import com.gasguru.core.uikit.fuel_list.FuelListSelection
import com.gasguru.core.uikit.fuel_list.FuelListSelectionModel
import com.gasguru.core.uikit.theme.FuelPumpTheme
import com.gasguru.core.uikit.theme.MyApplicationTheme
import com.gasguru.core.uikit.theme.Neutral100
import com.gasguru.core.uikit.theme.TextMain
import com.gasguru.core.uikit.theme.TextSubtle
import com.gasguru.feature.profile.R
import kotlinx.coroutines.launch
import com.gasguru.core.ui.R as RUi

@Composable
internal fun ProfileScreenRoute(viewModel: ProfileViewModel = hiltViewModel()) {
    val state by viewModel.userData.collectAsStateWithLifecycle()
    return ProfileScreen(uiState = state, event = viewModel::handleEvents)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ProfileScreen(uiState: ProfileUiState, event: (ProfileEvents) -> Unit) {
    var showSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var selectedFuel by remember { mutableStateOf<Int?>(null) }

    when (uiState) {
        is ProfileUiState.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .statusBarsPadding()
                    .testTag("loading"),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }

        is ProfileUiState.Success -> {
            selectedFuel = uiState.userData.fuelSelection.translation()
            SuccessContent(
                userData = uiState.userData,
                showSheet = {
                    showSheet = true
                }
            )
        }

        is ProfileUiState.LoadFailed -> {
            // Error state
        }
    }

    if (showSheet) {
        ModalBottomSheet(
            modifier = Modifier
                .navigationBarsPadding()
                .testTag("bottom_sheet_fuel"),
            onDismissRequest = {
                showSheet = false
            },
            dragHandle = {
                Surface(
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = Color.LightGray,
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
            sheetState = sheetState,
            containerColor = Neutral100,
            contentColor = Neutral100,
            windowInsets =  WindowInsets.navigationBars
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = stringResource(id = RUi.string.select_fuel_preference),
                    style = FuelPumpTheme.typography.baseBold,
                    color = TextMain
                )
                val list = FuelType.entries.map { Pair(it.getIcon(), it.translation()) }
                FuelListSelection(
                    model = FuelListSelectionModel(
                        list = list,
                        selected = selectedFuel,
                        onItemSelected = { fuel ->
                            selectedFuel = fuel
                            event(ProfileEvents.Fuel(fuel.toFuelType()))
                            scope.launch { sheetState.hide() }.invokeOnCompletion {
                                if (!sheetState.isVisible) {
                                    showSheet = false
                                }
                            }
                        }
                    )
                )
            }
        }
    }
}

@Composable
fun SuccessContent(userData: UserData, showSheet: () -> Unit) {
    Column(
        modifier = Modifier
            .background(color = Neutral100)
            .fillMaxSize()
            .statusBarsPadding()
            .padding(start = 16.dp, end = 16.dp, top = 32.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            modifier = Modifier,
            text = stringResource(id = R.string.profile),
            style = FuelPumpTheme.typography.h5
        )
        SettingItem(
            model = com.gasguru.core.uikit.components.settings.SettingItemModel(
                title = stringResource(id = R.string.fuel_selection),
                selection = stringResource(id = userData.fuelSelection.translation()),
                icon = R.drawable.ic_fuel_station,
                onClick = { showSheet() },
            ),
            modifier = Modifier.testTag("fuel_setting_item")
        )
        Spacer(modifier = Modifier.weight(1f))
        VersionAppInfo(modifier = Modifier.padding(bottom = 12.dp))
    }
}

@Composable
fun VersionAppInfo(modifier: Modifier = Modifier) {
    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        Text(
            modifier = Modifier,
            text = stringResource(
                id = R.string.version,
                getVersionInfo(context = LocalContext.current)
            ),
            style = FuelPumpTheme.typography.captionRegular,
            color = TextSubtle
        )
    }
}

@Suppress("DEPRECATION")
private fun getVersionInfo(context: Context): String {
    val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
    val versionCode =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            packageInfo.longVersionCode
        } else {
            packageInfo.versionCode
        }
    return "${packageInfo.versionName} ($versionCode)"
}

@Preview
@Composable
private fun ProfileScreenPreview() {
    MyApplicationTheme {
        ProfileScreen(
            uiState = ProfileUiState.Success(
                userData = UserData(
                    fuelSelection = FuelType.GASOLINE_95
                )
            ),
            event = { }
        )
    }
}

@Preview
@Composable
private fun ProfileScreenLoadingPreview() {
    MyApplicationTheme {
        ProfileScreen(uiState = ProfileUiState.Loading, event = {})
    }
}
