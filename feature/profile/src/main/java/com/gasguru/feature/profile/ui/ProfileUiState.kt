package com.gasguru.feature.profile.ui

import com.gasguru.core.model.data.UserData

sealed interface ProfileUiState {
    data object Loading : ProfileUiState
    data class Success(val userData: UserData) : ProfileUiState
    data object LoadFailed : ProfileUiState
}
