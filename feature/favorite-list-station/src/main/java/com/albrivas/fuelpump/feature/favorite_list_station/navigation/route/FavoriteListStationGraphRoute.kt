package com.albrivas.fuelpump.feature.favorite_list_station.navigation.route

import kotlinx.serialization.Serializable

@Serializable
sealed class StationListGraph {
    @Serializable
    data object StationListGraphRoute : StationListGraph()

    @Serializable
    data object StationListRoute : StationListGraph()
}
