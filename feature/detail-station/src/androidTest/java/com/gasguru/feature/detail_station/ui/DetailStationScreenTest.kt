package com.gasguru.feature.detail_station.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.gasguru.core.model.data.previewFuelStationDomain
import com.gasguru.core.testing.BaseTest
import com.gasguru.core.ui.IconTintKey
import com.gasguru.core.uikit.theme.AccentRed
import com.gasguru.core.uikit.theme.GasGuruTheme
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test


class DetailStationScreenTest : BaseTest() {

    @Test
    @DisplayName("Bookmark a fuel station")
    fun markFavoriteStation() {
        val initialStation = previewFuelStationDomain()
        var station by mutableStateOf(initialStation)
        var black = Color.Unspecified

        extension.use {
            setContent {
                black = GasGuruTheme.colors.neutralBlack
                DetailStationScreen(
                    uiState = DetailStationUiState.Success(station = station, address = null),
                    onFavoriteClick = { isFavorite ->
                        station = station.copy(isFavorite = isFavorite)
                    },
                    lastUpdate = 0
                )
            }


            onNodeWithTag("icon_favorite", useUnmergedTree = true)
                .assert(hasIconTint(black))

            onNodeWithTag("button_favorite").performClick()
            waitForIdle()

            onNodeWithTag("icon_favorite", useUnmergedTree = true)
                .assert(hasIconTint(AccentRed))
        }
    }

    private fun hasIconTint(expectedColor: Color) =
        SemanticsMatcher.expectValue(IconTintKey, expectedColor)
}