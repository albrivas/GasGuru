package com.gasguru.core.analytics

import androidx.compose.runtime.staticCompositionLocalOf

/**
 * CompositionLocal for providing [AnalyticsHelper] to Composables.
 *
 * Usage in a Composable:
 * ```
 * @Composable
 * fun MyScreen() {
 *     val analyticsHelper = LocalAnalyticsHelper.current
 *     Button(onClick = { analyticsHelper.logEvent(AnalyticsEvent(type = AnalyticsEvent.Types.STATION_SELECTED)) }) {
 *         Text("Select")
 *     }
 * }
 * ```
 *
 * Provide at the root of the composition in MainActivity:
 * ```
 * CompositionLocalProvider(LocalAnalyticsHelper provides analyticsHelper) { ... }
 * ```
 */
val LocalAnalyticsHelper = staticCompositionLocalOf<AnalyticsHelper> {
    NoOpAnalyticsHelper()
}
