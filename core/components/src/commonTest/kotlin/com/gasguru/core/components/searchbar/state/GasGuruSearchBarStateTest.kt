package com.gasguru.core.components.searchbar.state

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("GasGuruSearchBarState")
class GasGuruSearchBarStateTest {

    private lateinit var sut: GasGuruSearchBarState

    @BeforeEach
    fun setUp() {
        sut = GasGuruSearchBarState()
    }

    @Test
    @DisplayName(
        """
        GIVEN a new state instance
        WHEN initialized
        THEN active is false
        """,
    )
    fun initialActiveIsFalse() {
        assertFalse(sut.active)
    }

    @Test
    @DisplayName(
        """
        GIVEN state is active
        WHEN deactivate is called
        THEN active becomes false
        """,
    )
    fun deactivateSetsActiveFalse() {
        sut.onExpandedChange(newActive = true)

        sut.deactivate()

        assertFalse(sut.active)
    }

    @Test
    @DisplayName(
        """
        GIVEN state is already inactive
        WHEN deactivate is called
        THEN active remains false
        """,
    )
    fun deactivateWhenAlreadyInactiveRemainsInactive() {
        sut.deactivate()

        assertFalse(sut.active)
    }

    @Test
    @DisplayName(
        """
        GIVEN state is active
        WHEN deactivateWithFocusClear is called
        THEN active becomes false and callback is invoked
        """,
    )
    fun deactivateWithFocusClearSetsInactiveAndCallsCallback() {
        sut.onExpandedChange(newActive = true)
        var callbackInvoked = false

        sut.deactivateWithFocusClear { callbackInvoked = true }

        assertFalse(sut.active)
        assertTrue(callbackInvoked)
    }

    @Test
    @DisplayName(
        """
        GIVEN state is inactive
        WHEN deactivateWithFocusClear is called
        THEN callback is still invoked
        """,
    )
    fun deactivateWithFocusClearAlwaysCallsCallback() {
        var callbackInvoked = false

        sut.deactivateWithFocusClear { callbackInvoked = true }

        assertTrue(callbackInvoked)
    }

    @Test
    @DisplayName(
        """
        GIVEN state is inactive
        WHEN onFocusReceived is called
        THEN active becomes true
        """,
    )
    fun onFocusReceivedActivatesWhenInactive() {
        sut.onFocusReceived()

        assertTrue(sut.active)
    }

    @Test
    @DisplayName(
        """
        GIVEN state is already active
        WHEN onFocusReceived is called
        THEN active stays true
        """,
    )
    fun onFocusReceivedKeepsActiveWhenAlreadyActive() {
        sut.onExpandedChange(newActive = true)

        sut.onFocusReceived()

        assertTrue(sut.active)
    }

    @Test
    @DisplayName(
        """
        GIVEN any state
        WHEN onExpandedChange is called with true
        THEN active becomes true
        """,
    )
    fun onExpandedChangeTrueSetsActive() {
        sut.onExpandedChange(newActive = true)

        assertTrue(sut.active)
    }

    @Test
    @DisplayName(
        """
        GIVEN active state
        WHEN onExpandedChange is called with false
        THEN active becomes false
        """,
    )
    fun onExpandedChangeFalseSetsInactive() {
        sut.onExpandedChange(newActive = true)

        sut.onExpandedChange(newActive = false)

        assertFalse(sut.active)
    }
}
