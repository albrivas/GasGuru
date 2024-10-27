package com.albrivas.fuelpump.feature.profile.ui

import com.albrivas.fuelpump.core.model.data.UserData

sealed interface ProfileUiState {
    data object Loading : ProfileUiState
    data class Success(val userData: UserData) : ProfileUiState
    data object LoadFailed : ProfileUiState
}
