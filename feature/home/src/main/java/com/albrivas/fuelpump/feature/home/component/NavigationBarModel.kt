package com.albrivas.fuelpump.feature.home.component

import androidx.navigation.NavBackStackEntry
import com.albrivas.fuelpump.feature.home.navigation.route.HomeTopLevelRoutes

data class NavigationBarModel(
    val destinations: List<HomeTopLevelRoutes>,
    val currentDestination: NavBackStackEntry?,
    val onNavigateToDestination: (HomeTopLevelRoutes) -> Unit
)
