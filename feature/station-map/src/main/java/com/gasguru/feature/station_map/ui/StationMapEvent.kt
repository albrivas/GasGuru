package com.gasguru.feature.station_map.ui

sealed class StationMapEvent {
    data object GetStationByCurrentLocation : StationMapEvent()
    data class GetStationByPlace(val placeId: String) : StationMapEvent()
    data class ShowListStations(val show: Boolean) : StationMapEvent()
    data class UpdateBrandFilter(val selected: List<String>) : StationMapEvent()
    data class UpdateNearbyFilter(val number: String) : StationMapEvent()
    data class UpdateScheduleFilter(val schedule: FilterUiState.OpeningHours) : StationMapEvent()
    data object OnMapCentered : StationMapEvent()
    data class StartRoute(val originId: String?, val destinationId: String?): StationMapEvent()
}
