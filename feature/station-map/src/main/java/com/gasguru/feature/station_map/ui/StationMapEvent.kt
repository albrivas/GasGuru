package com.gasguru.feature.station_map.ui

import com.gasguru.core.model.data.SearchPlace

sealed class StationMapEvent {
    data object GetStationByCurrentLocation : StationMapEvent()
    data object ClearRecentSearches : StationMapEvent()
    data class InsertRecentSearch(val searchQuery: SearchPlace) : StationMapEvent()
    data class GetStationByPlace(val placeId: String) : StationMapEvent()
    data object ResetMapCenter : StationMapEvent()
    data class UpdateSearchQuery(val query: String) : StationMapEvent()
    data class ShowListStations(val show: Boolean) : StationMapEvent()
    data class UpdateBrandFilter(val selected: List<String>) : StationMapEvent()
    data class UpdateNearbyFilter(val number: String) : StationMapEvent()
    data class UpdateScheduleFilter(val schedule: OpeningHours) : StationMapEvent()
}
