package com.albrivas.fuelpump.navigation.navigationbar.route

import com.albrivas.fuelpump.feature.fuel_list_station.navigation.route.StationListGraph
import com.albrivas.fuelpump.feature.station_map.navigation.route.StationMapGraph
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

    companion object {
        fun fromRoute(route: String?): String? {
            return when {
                route?.contains("${StationMapGraph.StationMapRoute::class.simpleName}") == true -> Map().route
                route?.contains("${StationListGraph.StationListRoute::class.simpleName}") == true -> Favorite().route
                route?.contains("ProfileRoute") == true -> Profile().route
                else -> null
            }
        }
    }
}
