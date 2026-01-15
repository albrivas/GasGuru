package com.gasguru.navigation.handler

import androidx.navigation.NavController
import androidx.navigation.NavOptions
import com.gasguru.feature.detail_station.navigation.navigateToDetailStation
import com.gasguru.feature.detail_station.navigation.navigateToDetailStationAsDialog
import com.gasguru.feature.onboarding_welcome.navigation.OnboardingRoutes
import com.gasguru.feature.onboarding_welcome.navigation.navigateToOnboardingFuelPreferencesRoute
import com.gasguru.feature.onboarding_welcome.navigation.navigateToOnboardingWelcomeRoute
import com.gasguru.feature.route_planner.navigation.navigateToRoutePlannerScreen
import com.gasguru.feature.search.navigation.navigateToSearch
import com.gasguru.navigation.extensions.setPreviousResult
import com.gasguru.navigation.manager.NavigationDestination
import com.gasguru.navigation.navigationbar.navigateToNavigationBar

/**
 * NavigationHandler is responsible for translating [NavigationDestination] objects
 * into actual navigation actions using NavController extension functions.
 *
 * This class acts as a bridge between the centralized [NavigationManager] and
 * the feature-specific navigation extension functions.
 */
class NavigationHandler(private val navController: NavController) {

    /**
     * Handles a navigation destination and executes the appropriate navigation action.
     *
     * @param destination The destination to navigate to
     */
    fun handle(destination: NavigationDestination) {
        when (destination) {
            is NavigationDestination.DetailStation -> {
                if (destination.presentAsDialog) {
                    navController.navigateToDetailStationAsDialog(
                        idServiceStation = destination.idServiceStation,
                    )
                } else {
                    navController.navigateToDetailStation(
                        idServiceStation = destination.idServiceStation,
                    )
                }
            }

            is NavigationDestination.OnboardingWelcome -> {
                navController.navigateToOnboardingWelcomeRoute()
            }

            is NavigationDestination.OnboardingFuelPreferences -> {
                navController.navigateToOnboardingFuelPreferencesRoute()
            }

            is NavigationDestination.Home -> {
                val navOptions = NavOptions.Builder()
                    .setPopUpTo(
                        route = OnboardingRoutes.OnboardingWelcomeRoute,
                        inclusive = true,
                    )
                    .build()
                navController.navigateToNavigationBar(navOptions = navOptions)
            }

            is NavigationDestination.Search -> {
                navController.navigateToSearch()
            }

            is NavigationDestination.RoutePlanner -> {
                navController.navigateToRoutePlannerScreen()
            }

            is NavigationDestination.Back -> {
                navController.popBackStack()
            }

            is NavigationDestination.BackTo -> {
                navController.popBackStack(
                    route = destination.route,
                    inclusive = destination.inclusive,
                )
            }

            is NavigationDestination.BackWithData -> {
                navController.setPreviousResult(
                    key = destination.key,
                    value = destination.value,
                )
                navController.popBackStack()
            }
        }
    }
}