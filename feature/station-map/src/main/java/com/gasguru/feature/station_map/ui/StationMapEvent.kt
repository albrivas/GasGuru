package com.gasguru.feature.station_map.ui

import com.gasguru.core.model.data.SearchPlace
import com.google.android.gms.maps.model.LatLng

sealed class StationMapEvent {
    data object GetStationByCurrentLocation : StationMapEvent()
    data object ClearRecentSearches : StationMapEvent()
    data class InsertRecentSearch(val searchQuery: SearchPlace) : StationMapEvent()
    data class CenterMapStation(val latLng: LatLng) : StationMapEvent()
    data class GetStationByPlace(val placeId: String) : StationMapEvent()
    data object CenterMapInCurrentLocation : StationMapEvent()
    data object ResetMapCenter : StationMapEvent()
    data class UpdateSearchQuery(val query: String) : StationMapEvent()
    data class ShowListStations(val show: Boolean) : StationMapEvent()
}
