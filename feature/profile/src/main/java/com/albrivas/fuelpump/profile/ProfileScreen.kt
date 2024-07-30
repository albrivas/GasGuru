package com.albrivas.fuelpump.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.albrivas.fuelpump.core.model.data.FuelType
import com.albrivas.fuelpump.core.model.data.UserData
import com.albrivas.fuelpump.core.ui.translation
import com.albrivas.fuelpump.core.uikit.components.settings.SettingItem
import com.albrivas.fuelpump.core.uikit.components.settings.SettingItemModel
import com.albrivas.fuelpump.core.uikit.theme.MyApplicationTheme
import com.albrivas.fuelpump.feature.profile.R

@Composable
internal fun ProfileScreenRoute(viewModel: ProfileViewModel = hiltViewModel()) {
    val state by viewModel.userData.collectAsStateWithLifecycle()
    return ProfileScreen(uiState = state)
}

@Composable
internal fun ProfileScreen(uiState: ProfileUiState) {
    when (uiState) {
        is ProfileUiState.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .statusBarsPadding(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }

        is ProfileUiState.Success -> {
            Column(
                modifier = Modifier
                    .background(color = Color.White)
                    .fillMaxSize()
            ) {
                SettingItem(
                    model = SettingItemModel(
                        title = stringResource(id = R.string.fuel_selection),
                        selection = stringResource(id = uiState.userData.fuelSelection.translation()),
                        onClick = {},
                    )
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
