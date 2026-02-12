package com.gasguru.feature.onboarding_welcome.ui

sealed interface NewOnboardingEvent {
    data object NextPage : NewOnboardingEvent
    data object PreviousPage : NewOnboardingEvent
    data class PageChanged(val page: Int) : NewOnboardingEvent
    data object Skip : NewOnboardingEvent
}
