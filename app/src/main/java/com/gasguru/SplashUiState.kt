package com.gasguru

sealed interface SplashUiState {
    data object Loading : SplashUiState
    data object Error : SplashUiState
    data class Success(val isOnboardingSuccess: Boolean) : SplashUiState
}
