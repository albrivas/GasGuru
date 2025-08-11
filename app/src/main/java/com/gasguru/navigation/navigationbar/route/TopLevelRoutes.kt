package com.gasguru.navigation.navigationbar.route

import kotlinx.serialization.Serializable

@Serializable
sealed class TopLevelRoutes {
    @Serializable
    data object Map : TopLevelRoutes()

    @Serializable
    data object Favorite : TopLevelRoutes()

    @Serializable
    data object Profile: TopLevelRoutes()
}
