package com.gasguru.auto.ui.mainmenu

data class MainMenuUiState(
    val permissionDenied: Boolean = true,
    val needsOnboarding: Boolean = false,
)