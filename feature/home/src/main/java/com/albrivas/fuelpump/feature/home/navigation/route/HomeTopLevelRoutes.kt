package com.albrivas.fuelpump.feature.home.navigation.route

import com.albrivas.fuelpump.feature.home.R
import kotlinx.serialization.Serializable

@Serializable
sealed class HomeTopLevelRoutes  {
    @Serializable
    data class Map(
        val icon: Int = R.drawable.ic_map,
        val route: String = "map_route"
    ) : HomeTopLevelRoutes()

    @Serializable
    data class List(
        val icon: Int = R.drawable.ic_list,
        val route: String = "list_route"
    ) : HomeTopLevelRoutes()

    @Serializable
    data class Profile(
        val icon: Int = R.drawable.ic_profile,
        val route: String = "profile_route"
    ) : HomeTopLevelRoutes()
}