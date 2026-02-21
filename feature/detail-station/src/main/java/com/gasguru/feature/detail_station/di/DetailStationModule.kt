package com.gasguru.feature.detail_station.di

import com.gasguru.feature.detail_station.ui.DetailStationViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val detailStationModule = module {
    viewModel {
        DetailStationViewModel(
            savedStateHandle = get(),
            getFuelStationByIdUseCase = get(),
            getLastKnownLocationUseCase = get(),
            userDataUseCase = get(),
            saveFavoriteStationUseCase = get(),
            removeFavoriteStationUseCase = get(),
            getAddressFromLocationUseCase = get(),
            getStaticMapUrlUseCase = get(),
            addPriceAlertUseCase = get(),
            removePriceAlertUseCase = get(),
        )
    }
}
