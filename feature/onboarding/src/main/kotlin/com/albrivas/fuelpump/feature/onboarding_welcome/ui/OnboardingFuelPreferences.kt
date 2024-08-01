package com.albrivas.fuelpump.feature.onboarding_welcome.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.hilt.navigation.compose.hiltViewModel
import com.albrivas.fuelpump.core.model.data.FuelType
import com.albrivas.fuelpump.core.ui.toFuelType
import com.albrivas.fuelpump.core.ui.translation
import com.albrivas.fuelpump.core.uikit.components.selectedItem.BasicSelectedItem
import com.albrivas.fuelpump.core.uikit.components.selectedItem.BasicSelectedItemModel
import com.albrivas.fuelpump.core.uikit.components.FuelPumpButton
import com.albrivas.fuelpump.core.uikit.theme.GrayLight
import com.albrivas.fuelpump.core.uikit.theme.MyApplicationTheme
import com.albrivas.fuelpump.feature.onboarding.R
import com.albrivas.fuelpump.feature.onboarding_welcome.viewmodel.OnboardingUiState
import com.albrivas.fuelpump.feature.onboarding_welcome.viewmodel.OnboardingViewModel

@Composable
internal fun OnboardingFuelPreferencesRoute(
    navigateToHome: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel(),
) {
    OnboardingFuelPreferences(
        navigateToHome = navigateToHome,
        saveSelection = viewModel::saveSelectedFuel,
        uiState = viewModel.state
    )
}

@Composable
internal fun OnboardingFuelPreferences(
    navigateToHome: () -> Unit,
    saveSelection: (FuelType) -> Unit = {},
    uiState: OnboardingUiState,
) {
    var selectedFuel by remember { mutableStateOf<Int?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(start = 16.dp, top = 24.dp, end = 16.dp, bottom = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(id = R.string.welcome_title_fuel_preferences),
            style = MaterialTheme.typography.titleLarge,
            lineHeight = 1.3.em,
            textAlign = TextAlign.Start,
            modifier = Modifier.padding(0.dp, 30.dp, 0.dp, 0.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(id = R.string.welcome_subtitle_fuel_preferences),
            color = GrayLight,
            style = MaterialTheme.typography.displayMedium,
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(34.dp))
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            when (uiState) {
                is OnboardingUiState.ListFuelPreferences -> {
                    itemsIndexed(uiState.list.sorted()) { index, fuelName ->
                        val titleTranslation = fuelName.translation()
                        BasicSelectedItem(
                            modifier = Modifier.testTag("list_item_$index"),
                            model = BasicSelectedItemModel(
                                title = titleTranslation,
                                isSelected = titleTranslation == selectedFuel,
                                onItemSelected = { selectedFuel = titleTranslation }
                            ),
                        )
                    }
                }
            }
        }
        FuelPumpButton(
            onClick = {
                selectedFuel?.let { saveSelection(it.toFuelType()) }
                navigateToHome()
            },
            enabled = selectedFuel != null,
            text = stringResource(id = R.string.welcome_button),
            modifier = Modifier
                .padding(bottom = 17.dp, top = 36.dp)
                .systemBarsPadding()
                .testTag("button_next_onboarding")
        )
    }
}

@Composable
@Preview(name = "Onboarding - Fuel preferences preview")
private fun PreviewOnboardingFuelPreferences() {
    MyApplicationTheme {
        OnboardingFuelPreferences(
            navigateToHome = {},
            saveSelection = {},
            uiState = OnboardingUiState.ListFuelPreferences(
                listOf(
                    FuelType.DIESEL_PLUS,
                    FuelType.DIESEL,
                    FuelType.GASOLINE_98,
                    FuelType.GASOLINE_95
                )
            )
        )
    }
}
