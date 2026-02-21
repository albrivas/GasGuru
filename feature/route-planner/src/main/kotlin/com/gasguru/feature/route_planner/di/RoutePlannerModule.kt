package com.gasguru.feature.route_planner.di

import com.gasguru.feature.route_planner.ui.RoutePlannerViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val routePlannerModule = module {
    viewModel {
        RoutePlannerViewModel(
            clearRecentSearchQueriesUseCase = get(),
            getRecentSearchQueryUseCase = get(),
        )
    }
}
