package com.gasguru.navigation.navigationbar.route

import com.gasguru.feature.favorite_list_station.navigation.route.StationListGraph
import com.gasguru.feature.station_map.navigation.route.StationMapGraph
import kotlinx.serialization.Serializable

@Serializable
sealed class TopLevelRoutes {
    @Serializable
    data class Map(
        val route: String = "map_route"
    ) : TopLevelRoutes()

    @Serializable
    data class Favorite(
        val route: String = "favorite_route"
    ) : TopLevelRoutes()

    @Serializable
    data class Profile(
        val route: String = "profile_route"
    ) : TopLevelRoutes()

}
