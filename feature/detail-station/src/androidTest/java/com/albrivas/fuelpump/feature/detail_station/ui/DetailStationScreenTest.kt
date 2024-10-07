package com.albrivas.fuelpump.feature.detail_station.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.albrivas.fuelpump.core.model.data.previewFuelStationDomain
import com.albrivas.fuelpump.core.testing.BaseTest
import com.albrivas.fuelpump.core.ui.IconTintKey
import com.albrivas.fuelpump.core.uikit.theme.YellowFavorite
import org.junit.jupiter.api.Disabled
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

    @Disabled("Disabled until the new design")
    @Test
    @DisplayName("Show all the information about station")
    fun displayStationInformation() = extension.use {
        setContent {
            DetailStationScreen(uiState = DetailStationUiState.Success(station = previewFuelStationDomain()))
        }

        onNodeWithTag("address").assertIsDisplayed()
        onNodeWithTag("calendar").assertIsDisplayed()
        onNodeWithTag("status-station").assertIsDisplayed()
        onNodeWithTag("name-station").assertIsDisplayed()
        onNodeWithTag("distance").assertIsDisplayed()
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

    @Test
    @DisplayName("Bookmark a fuel station")
    fun markFavoriteStation() {
        val initialStation = previewFuelStationDomain()
        var station by mutableStateOf(initialStation)

        extension.use {
            setContent {
                DetailStationScreen(
                    uiState =DetailStationUiState.Success(station = station),
                    onFavoriteClick = { isFavorite ->
                        station = station.copy(isFavorite = isFavorite)
                    }
                )
            }

            onNodeWithTag("icon_favorite", useUnmergedTree = true)
                .assert(hasIconTint(Color.LightGray))

            onNodeWithTag("button_favorite").performClick()
            waitForIdle()

            onNodeWithTag("icon_favorite", useUnmergedTree = true)
                .assert(hasIconTint(YellowFavorite))
        }
    }


    private fun hasIconTint(expectedColor: Color) =
        SemanticsMatcher.expectValue(IconTintKey, expectedColor)
}