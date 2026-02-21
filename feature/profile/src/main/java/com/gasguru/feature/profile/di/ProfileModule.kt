package com.gasguru.feature.profile.di

import com.gasguru.feature.profile.ui.ProfileViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val profileModule = module {
    viewModel {
        ProfileViewModel(
            getUserData = get(),
            saveFuelSelectionUseCase = get(),
            saveThemeModeUseCase = get(),
        )
    }
}
