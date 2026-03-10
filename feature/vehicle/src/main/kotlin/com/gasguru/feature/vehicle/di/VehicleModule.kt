package com.gasguru.feature.vehicle.di

import com.gasguru.core.domain.user.GetUserDataUseCase
import com.gasguru.core.domain.vehicle.AddVehicleUseCase
import com.gasguru.feature.vehicle.viewmodel.AddVehicleViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val vehicleModule = module {
    viewModel {
        AddVehicleViewModel(
            navigationManager = get(),
            addVehicleUseCase = get(),
            getUserDataUseCase = get(),
        )
    }
}
