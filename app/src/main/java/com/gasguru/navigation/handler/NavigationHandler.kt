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
import com.gasguru.navigation.manager.NavigationCommand
import com.gasguru.navigation.manager.NavigationDestination
import com.gasguru.navigation.navigationbar.navigateToNavigationBar

/**
 * NavigationHandler is responsible for translating [NavigationCommand] objects
 * into actual navigation actions using NavController extension functions.
 *
 * This class acts as a bridge between the centralized [NavigationManager] and
 * the feature-specific navigation extension functions.
 */
class NavigationHandler(private val navController: NavController) {

    /**
     * Handles a navigation command and executes the appropriate navigation action.
     *
     * @param command The command to execute
     */
    fun handle(command: NavigationCommand) {
        when (command) {
            is NavigationCommand.To -> when (val destination = command.destination) {
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
            }

            is NavigationCommand.Back -> {
                navController.popBackStack()
            }

            is NavigationCommand.BackTo -> {
                navController.popBackStack(
                    route = command.route,
                    inclusive = command.inclusive,
                )
            }

            is NavigationCommand.BackWithData -> {
                navController.setPreviousResult(
                    key = command.key,
                    value = command.value,
                )
                navController.popBackStack()
            }
        }
    }
}
