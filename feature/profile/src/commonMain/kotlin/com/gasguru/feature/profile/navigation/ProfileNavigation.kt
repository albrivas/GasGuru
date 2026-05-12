package com.gasguru.feature.profile.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.gasguru.feature.profile.ui.ProfileScreenRoute

fun NavGraphBuilder.profileScreen() {
    composable<ProfileRoute>(
        enterTransition = { null },
        exitTransition = { null },
        popEnterTransition = { null },
        popExitTransition = { null }
    ) {
        ProfileScreenRoute()
    }
}
