package com.gasguru.auto.ui.mainmenu

data class MainMenuUiState(
    val permissionDenied: Boolean = true,
    val locationDisabled: Boolean = false,
    val needsOnboarding: Boolean = false,
)
