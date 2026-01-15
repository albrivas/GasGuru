package com.gasguru.navigation

import androidx.compose.runtime.staticCompositionLocalOf
import com.gasguru.navigation.manager.NavigationManager

/**
 * CompositionLocal for providing [NavigationManager] to Composables.
 *
 * This allows Composables to access NavigationManager without needing to pass it
 * through every function parameter.
 *
 * Usage in a Composable:
 * ```
 * @Composable
 * fun MyScreen() {
 *     val navigationManager = LocalNavigationManager.current
 *     Button(onClick = { navigationManager.navigateBack() }) {
 *         Text("Go Back")
 *     }
 * }
 * ```
 */
val LocalNavigationManager = staticCompositionLocalOf<NavigationManager> {
    error("NavigationManager not provided. Make sure you wrap your NavHost in CompositionLocalProvider.")
}
