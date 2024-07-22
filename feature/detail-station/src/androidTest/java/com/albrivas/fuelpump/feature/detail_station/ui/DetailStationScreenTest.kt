package com.albrivas.fuelpump.feature.detail_station.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.onNodeWithTag
import com.albrivas.fuelpump.core.model.data.previewFuelStationDomain
import com.albrivas.fuelpump.core.testing.BaseTest
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class DetailStationScreenTest : BaseTest() {

    @Test
    @DisplayName("Button go to station is enable")
    fun buttonGoToStationEnable() = extension.use {
       setContent {
            DetailStationScreen(uiState = DetailStationUiState.Success(station = previewFuelStationDomain()))
        }

        onNodeWithTag("button_go_station").assertIsEnabled()
    }

    @Test
    @DisplayName("Show all the information about station")
    fun displayStationInformation() = extension.use {
        setContent {
            DetailStationScreen(uiState = DetailStationUiState.Success(station = previewFuelStationDomain()))
        }

        onNodeWithTag("address").assertIsDisplayed()
        onNodeWithTag("country").assertIsDisplayed()
        onNodeWithTag("distance").assertIsDisplayed()
        onNodeWithTag("schedule").assertIsDisplayed()
        onNodeWithTag("calendar").assertIsDisplayed()
    }

    @Test
    @DisplayName("Show loading when the information station is loading")
    fun showLoading() {
        extension.use {
            setContent {
                DetailStationScreen(uiState = DetailStationUiState.Loading)
            }

            onNodeWithTag("loading").assertIsDisplayed()
        }
    }
}