package com.gasguru.core.components.searchbar.state

import com.gasguru.core.model.data.SearchPlace

sealed interface GasGuruSearchBarEvent {
    data class UpdateSearchQuery(val query: String) : GasGuruSearchBarEvent
    data object ClearRecentSearches : GasGuruSearchBarEvent
    data class InsertRecentSearch(val searchQuery: SearchPlace) : GasGuruSearchBarEvent
}
