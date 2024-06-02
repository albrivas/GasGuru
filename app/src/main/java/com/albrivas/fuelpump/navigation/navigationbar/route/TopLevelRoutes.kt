package com.albrivas.fuelpump.navigation.navigationbar.route

import com.albrivas.fuelpump.core.uikit.R
import kotlinx.serialization.Serializable

@Serializable
sealed class TopLevelRoutes {
    @Serializable
    data class Map(
        val icon: Int = R.drawable.ic_map,
        val route: String = "map_route"
    ) : TopLevelRoutes()

    @Serializable
    data class List(
        val icon: Int = R.drawable.ic_list,
        val route: String = "list_route"
    ) : TopLevelRoutes()

    @Serializable
    data class Profile(
        val icon: Int = R.drawable.ic_profile,
        val route: String = "profile_route"
    ) : TopLevelRoutes()
}
