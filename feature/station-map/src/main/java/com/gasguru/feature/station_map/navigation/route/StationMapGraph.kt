package com.gasguru.feature.station_map.navigation.route

import kotlinx.serialization.Serializable

@Serializable
sealed class StationMapGraph {
    @Serializable
    data object StationMapGraphRoute : StationMapGraph()

    @Serializable
    data object StationMapRoute : StationMapGraph()
}
