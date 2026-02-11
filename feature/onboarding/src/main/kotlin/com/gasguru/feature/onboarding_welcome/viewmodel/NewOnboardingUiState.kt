package com.gasguru.feature.onboarding_welcome.viewmodel

import androidx.compose.runtime.Immutable
import com.gasguru.feature.onboarding_welcome.ui.OnboardingPageUiModel

@Immutable
data class NewOnboardingUiState(
    val currentPage: Int = 0,
) {
    val totalPages: Int get() = OnboardingPageUiModel.entries.size
    val isFirstPage: Boolean get() = currentPage == 0
    val isLastPage: Boolean get() = currentPage == totalPages - 1
    val showSkipButton: Boolean get() = !isLastPage
}
