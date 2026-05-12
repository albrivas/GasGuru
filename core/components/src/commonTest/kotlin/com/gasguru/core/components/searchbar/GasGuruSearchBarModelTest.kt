package com.gasguru.core.components.searchbar

import com.gasguru.core.model.data.SearchPlace
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("GasGuruSearchBarModel")
class GasGuruSearchBarModelTest {

    @Test
    @DisplayName(
        """
        GIVEN default model
        WHEN created with no arguments
        THEN alwaysActive is false
        """,
    )
    fun defaultModelAlwaysActiveIsFalse() {
        val model = GasGuruSearchBarModel()

        assertFalse(model.alwaysActive)
    }

    @Test
    @DisplayName(
        """
        GIVEN model with alwaysActive true
        WHEN created
        THEN alwaysActive is true
        """,
    )
    fun modelWithAlwaysActiveTrueReflectsValue() {
        val model = GasGuruSearchBarModel(alwaysActive = true)

        assertTrue(model.alwaysActive)
    }

    @Test
    @DisplayName(
        """
        GIVEN model with onActiveChange callback
        WHEN callback is invoked
        THEN it receives the expected value
        """,
    )
    fun onActiveChangeCallbackReceivesValue() {
        var received: Boolean? = null
        val model = GasGuruSearchBarModel(
            onActiveChange = { isActive -> received = isActive },
        )

        model.onActiveChange(true)

        assertEquals(true, received)
    }

    @Test
    @DisplayName(
        """
        GIVEN model with onPlaceSelected callback
        WHEN callback is invoked with a place
        THEN it receives the expected place
        """,
    )
    fun onPlaceSelectedCallbackReceivesPlace() {
        val expectedPlace = SearchPlace(name = "Madrid", id = "1")
        var receivedPlace: SearchPlace? = null
        val model = GasGuruSearchBarModel(
            onPlaceSelected = { place -> receivedPlace = place },
        )

        model.onPlaceSelected(expectedPlace)

        assertEquals(expectedPlace, receivedPlace)
    }

    @Test
    @DisplayName(
        """
        GIVEN model with onRecentSearchClicked callback
        WHEN callback is invoked with a place
        THEN it receives the expected place
        """,
    )
    fun onRecentSearchClickedCallbackReceivesPlace() {
        val expectedPlace = SearchPlace(name = "Barcelona", id = "2")
        var receivedPlace: SearchPlace? = null
        val model = GasGuruSearchBarModel(
            onRecentSearchClicked = { place -> receivedPlace = place },
        )

        model.onRecentSearchClicked(expectedPlace)

        assertEquals(expectedPlace, receivedPlace)
    }

    @Test
    @DisplayName(
        """
        GIVEN model with onBackPressed callback
        WHEN callback is invoked
        THEN it is called exactly once
        """,
    )
    fun onBackPressedCallbackIsInvoked() {
        var invocationCount = 0
        val model = GasGuruSearchBarModel(
            onBackPressed = { invocationCount++ },
        )

        model.onBackPressed()

        assertEquals(1, invocationCount)
    }

    @Test
    @DisplayName(
        """
        GIVEN model with onHeight callback
        WHEN callback is invoked with a height value
        THEN it receives the expected height
        """,
    )
    fun onHeightCallbackReceivesHeight() {
        val expectedHeight = 120
        var receivedHeight: Int? = null
        val model = GasGuruSearchBarModel(
            onHeight = { height -> receivedHeight = height },
        )

        model.onHeight(expectedHeight)

        assertEquals(expectedHeight, receivedHeight)
    }

    @Test
    @DisplayName(
        """
        GIVEN two models with same values
        WHEN compared
        THEN they are equal
        """,
    )
    fun modelsWithSameValuesAreEqual() {
        val modelA = GasGuruSearchBarModel(alwaysActive = true)
        val modelB = GasGuruSearchBarModel(alwaysActive = true)

        assertEquals(modelA.alwaysActive, modelB.alwaysActive)
    }
}
