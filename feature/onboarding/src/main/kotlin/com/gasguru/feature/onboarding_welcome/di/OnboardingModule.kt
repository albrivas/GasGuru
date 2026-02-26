package com.gasguru.feature.onboarding_welcome.di

import com.gasguru.feature.onboarding_welcome.viewmodel.CapacityTankViewModel
import com.gasguru.feature.onboarding_welcome.viewmodel.NewOnboardingViewModel
import com.gasguru.feature.onboarding_welcome.viewmodel.OnboardingViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val onboardingModule = module {
    viewModel {
        OnboardingViewModel(
            saveFuelSelectionUseCase = get(),
        )
    }
    viewModel {
        NewOnboardingViewModel(
            navigationManager = get(),
        )
    }
    viewModel {
        CapacityTankViewModel(
            navigationManager = get(),
        )
    }
}
