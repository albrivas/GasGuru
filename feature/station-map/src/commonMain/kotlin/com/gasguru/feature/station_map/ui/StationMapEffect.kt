package com.gasguru.feature.station_map.ui

sealed class StationMapEffect {
    data object ShowRouteError : StationMapEffect()
}
