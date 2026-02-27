package com.gasguru.feature.favorite_list_station.di

import com.gasguru.feature.favorite_list_station.ui.FavoriteListStationViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val favoriteListStationModule = module {
    viewModel {
        FavoriteListStationViewModel(
            getUserDataUseCase = get(),
            getFavoriteStationsUseCase = get(),
            getLastKnownLocationUseCase = get(),
            removeFavoriteStationUseCase = get(),
        )
    }
}
