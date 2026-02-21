package com.gasguru.core.components.searchbar.di

import com.gasguru.core.components.searchbar.GasGuruSearchBarViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val searchBarModule = module {
    viewModel {
        GasGuruSearchBarViewModel(
            savedStateHandle = get(),
            getPlacesUseCase = get(),
            clearRecentSearchQueriesUseCase = get(),
            insertRecentSearchQueryUseCase = get(),
            getRecentSearchQueryUseCase = get(),
        )
    }
}
