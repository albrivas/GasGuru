package com.gasguru.feature.profile.di

import com.gasguru.feature.profile.ui.ProfileViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

@Suppress("DeprecatedKoinApi")
fun profileModule() = module {
    viewModel {
        ProfileViewModel(
            getUserData = get(),
            saveThemeModeUseCase = get(),
            deleteVehicleUseCase = get(),
            getVehicleByIdUseCase = get(),
            saveVehicleUseCase = get(),
            navigationManager = get(),
            analyticsHelper = get(),
        )
    }
}
