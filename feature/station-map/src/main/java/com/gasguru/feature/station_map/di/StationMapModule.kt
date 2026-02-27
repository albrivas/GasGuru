package com.gasguru.feature.station_map.di

import com.gasguru.core.common.KoinQualifiers
import com.gasguru.feature.station_map.ui.StationMapViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val stationMapModule = module {
    viewModel {
        StationMapViewModel(
            fuelStationByLocation = get(),
            getUserDataUseCase = get(),
            getLocationPlaceUseCase = get(),
            getCurrentLocationUseCase = get(),
            getFiltersUseCase = get(),
            saveFilterUseCase = get(),
            getRouteUseCase = get(),
            getFuelStationsInRouteUseCase = get(),
            defaultDispatcher = get(named(KoinQualifiers.DEFAULT_DISPATCHER)),
        )
    }
}
