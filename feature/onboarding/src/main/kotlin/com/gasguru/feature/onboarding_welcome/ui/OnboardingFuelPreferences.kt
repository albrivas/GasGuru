package com.gasguru.feature.onboarding_welcome.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gasguru.core.model.data.FuelType
import com.gasguru.core.ui.models.FuelTypeUiModel
import com.gasguru.core.ui.toFuelType
import com.gasguru.core.uikit.components.GasGuruButton
import com.gasguru.core.uikit.components.fuel_list.FuelListSelection
import com.gasguru.core.uikit.components.fuel_list.FuelListSelectionModel
import com.gasguru.core.uikit.theme.GasGuruTheme
import com.gasguru.core.uikit.theme.MyApplicationTheme
import com.gasguru.core.uikit.theme.ThemePreviews
import com.gasguru.feature.onboarding.R
import com.gasguru.feature.onboarding_welcome.viewmodel.OnboardingUiState
import com.gasguru.feature.onboarding_welcome.viewmodel.OnboardingViewModel
import com.gasguru.navigation.LocalNavigationManager
import com.gasguru.navigation.manager.NavigationDestination

@Composable
internal fun OnboardingFuelPreferencesRoute(
    viewModel: OnboardingViewModel = hiltViewModel(),
) {
    val navigationManager = LocalNavigationManager.current

    OnboardingFuelPreferences(
        navigateToHome = {
            navigationManager.navigateTo(NavigationDestination.Home)
        },
        saveSelection = viewModel::saveSelectedFuel,
        uiState = viewModel.state,
    )
}

@Composable
internal fun OnboardingFuelPreferences(
    navigateToHome: () -> Unit,
    saveSelection: (FuelType) -> Unit = {},
    uiState: OnboardingUiState,
) {
    var selectedFuel by remember { mutableStateOf<Int?>(null) }

    when (uiState) {
        is OnboardingUiState.ListFuelPreferences -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(GasGuruTheme.colors.neutral100)
                    .padding(start = 16.dp, top = 24.dp, end = 16.dp, bottom = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(id = R.string.welcome_title_fuel_preferences),
                    style = GasGuruTheme.typography.h2,
                    color = GasGuruTheme.colors.neutralBlack,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(0.dp, 30.dp, 0.dp, 0.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                val list = uiState.list.map {
                    val fuelUiModel = FuelTypeUiModel.fromFuelType(it)
                    Pair(fuelUiModel.iconRes, fuelUiModel.translationRes)
                }
                FuelListSelection(
                    modifier = Modifier.weight(1f),
                    model = FuelListSelectionModel(
                        list = list,
                        selected = null,
                        onItemSelected = { fuel ->
                            selectedFuel = fuel
                        }
                    )
                )
                GasGuruButton(
                    onClick = {
                        selectedFuel?.let { saveSelection(it.toFuelType()) }
                        navigateToHome()
                    },
                    enabled = selectedFuel != null,
                    text = stringResource(id = R.string.welcome_button),
                    modifier = Modifier
                        .systemBarsPadding()
                        .testTag("button_next_onboarding")
                )
            }
        }
    }
}

@Composable
@ThemePreviews
private fun PreviewOnboardingFuelPreferences() {
    MyApplicationTheme {
        OnboardingFuelPreferences(
            navigateToHome = {},
            saveSelection = {},
            uiState = OnboardingUiState.ListFuelPreferences(FuelType.entries)
        )
    }
}
