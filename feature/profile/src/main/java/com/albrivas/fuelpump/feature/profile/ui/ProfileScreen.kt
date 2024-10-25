package com.albrivas.fuelpump.feature.profile.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.albrivas.fuelpump.core.model.data.FuelType
import com.albrivas.fuelpump.core.model.data.UserData
import com.albrivas.fuelpump.core.ui.translation
import com.albrivas.fuelpump.core.uikit.components.settings.SettingItem
import com.albrivas.fuelpump.core.uikit.components.settings.SettingItemModel
import com.albrivas.fuelpump.core.uikit.theme.FuelPumpTheme
import com.albrivas.fuelpump.core.uikit.theme.MyApplicationTheme
import com.albrivas.fuelpump.core.uikit.theme.Neutral100
import com.albrivas.fuelpump.feature.profile.R

@Composable
internal fun ProfileScreenRoute(viewModel: ProfileViewModel = hiltViewModel()) {
    val state by viewModel.userData.collectAsStateWithLifecycle()
    return ProfileScreen(uiState = state, saveFuelType = viewModel::saveSelectionFuel)
}

@Composable
internal fun ProfileScreen(uiState: ProfileUiState, saveFuelType: (FuelType) -> Unit = {}) {
    var showDialog by remember { mutableStateOf(false) }
    var fuelSelected by remember { mutableStateOf(FuelType.GASOLINE_95) }

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
            Column(
                modifier = Modifier
                    .background(color = Neutral100)
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(start = 16.dp, end = 16.dp, top = 32.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                fuelSelected = uiState.userData.fuelSelection
                Text(
                    modifier = Modifier,
                    text = stringResource(id = R.string.profile),
                    style = FuelPumpTheme.typography.h5
                )
                SettingItem(
                    model = SettingItemModel(
                        title = stringResource(id = R.string.fuel_selection),
                        selection = stringResource(id = uiState.userData.fuelSelection.translation()),
                        icon = R.drawable.ic_fuel_station,
                        onClick = { showDialog = true },
                    ),
                    modifier = Modifier.testTag("fuel_setting_item")
                )
            }
        }

        is ProfileUiState.LoadFailed -> {
            // Error state
        }
    }
}

@Preview
@Composable
private fun ProfileScreenPreview() {
    MyApplicationTheme {
        ProfileScreen(uiState = ProfileUiState.Success(userData = UserData(fuelSelection = FuelType.GASOLINE_95)))
    }
}

@Preview
@Composable
private fun ProfileScreenLoadingPreview() {
    MyApplicationTheme {
        ProfileScreen(uiState = ProfileUiState.Loading)
    }
}
