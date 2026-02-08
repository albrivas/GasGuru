package com.gasguru.feature.onboarding_welcome.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gasguru.core.model.data.FuelType
import com.gasguru.core.ui.mapper.toFuelItem
import com.gasguru.core.ui.mapper.toUiModel
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
                    .background(GasGuruTheme.colors.neutral100),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 24.dp, top = 60.dp, end = 24.dp, bottom = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = stringResource(id = R.string.welcome_title_fuel_preferences),
                        style = GasGuruTheme.typography.h4,
                        color = GasGuruTheme.colors.neutralBlack,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = stringResource(id = R.string.welcome_subtitle_fuel_preferences),
                        style = GasGuruTheme.typography.smallRegular,
                        color = GasGuruTheme.colors.textSubtle,
                        textAlign = TextAlign.Center,
                    )
                }
                val list = uiState.list.map { it.toUiModel().toFuelItem() }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 20.dp),
                ) {
                    FuelListSelection(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 8.dp),
                        model = FuelListSelectionModel(
                            list = list,
                            selected = null,
                            onItemSelected = { fuel ->
                                selectedFuel = fuel
                            },
                        ),
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(16.dp)
                            .align(Alignment.BottomCenter)
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        GasGuruTheme.colors.neutral100.copy(alpha = 0f),
                                        GasGuruTheme.colors.neutral100,
                                    ),
                                ),
                            ),
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
                        onClick = {
                            selectedFuel?.let { saveSelection(it.toFuelType()) }
                            navigateToHome()
                        },
                        enabled = selectedFuel != null,
                        text = stringResource(id = R.string.onboarding_continue),
                        modifier = Modifier.testTag("button_next_onboarding"),
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(id = R.string.welcome_hint_fuel_preferences),
                        style = GasGuruTheme.typography.captionRegular,
                        color = GasGuruTheme.colors.neutral600,
                        textAlign = TextAlign.Center,
                    )
                }
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
            uiState = OnboardingUiState.ListFuelPreferences(FuelType.entries),
        )
    }
}