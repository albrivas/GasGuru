package com.gasguru.feature.onboarding_welcome.viewmodel

import androidx.lifecycle.ViewModel
import com.gasguru.core.analytics.AnalyticsEvent
import com.gasguru.core.analytics.AnalyticsHelper
import com.gasguru.feature.onboarding_welcome.ui.NewOnboardingEvent
import com.gasguru.feature.onboarding_welcome.ui.OnboardingPageUiModel
import com.gasguru.navigation.manager.NavigationDestination
import com.gasguru.navigation.manager.NavigationManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class NewOnboardingViewModel(
    private val navigationManager: NavigationManager,
    private val analyticsHelper: AnalyticsHelper,
) : ViewModel() {

    private val _uiState = MutableStateFlow(NewOnboardingUiState())
    val uiState: StateFlow<NewOnboardingUiState> = _uiState.asStateFlow()

    init {
        analyticsHelper.logEvent(event = AnalyticsEvent(type = AnalyticsEvent.Types.ONBOARDING_STARTED))
    }

    fun handleEvent(event: NewOnboardingEvent) {
        when (event) {
            is NewOnboardingEvent.NextPage -> onNextPage()
            is NewOnboardingEvent.PreviousPage -> onPreviousPage()
            is NewOnboardingEvent.PageChanged -> onPageChanged(page = event.page)
            is NewOnboardingEvent.Skip -> onSkip()
        }
    }

    private fun onNextPage() {
        val state = _uiState.value
        if (state.isLastPage) {
            navigateToFuelPreferences()
        } else {
            _uiState.update { it.copy(currentPage = state.currentPage + 1) }
        }
    }

    private fun onPreviousPage() {
        _uiState.update { state ->
            if (!state.isFirstPage) {
                state.copy(currentPage = state.currentPage - 1)
            } else {
                state
            }
        }
    }

    private fun onPageChanged(page: Int) {
        if (page in 0 until OnboardingPageUiModel.entries.size) {
            _uiState.update { it.copy(currentPage = page) }
            analyticsHelper.logEvent(
                event = AnalyticsEvent(
                    type = AnalyticsEvent.Types.ONBOARDING_PAGE_VIEWED,
                    extras = listOf(
                        AnalyticsEvent.Param(key = AnalyticsEvent.ParamKeys.PAGE_NUMBER, value = page.toString()),
                    ),
                ),
            )
        }
    }

    private fun onSkip() {
        analyticsHelper.logEvent(event = AnalyticsEvent(type = AnalyticsEvent.Types.ONBOARDING_SKIPPED))
        navigateToFuelPreferences()
    }

    private fun navigateToFuelPreferences() {
        navigationManager.navigateTo(destination = NavigationDestination.OnboardingFuelPreferences)
    }
}
