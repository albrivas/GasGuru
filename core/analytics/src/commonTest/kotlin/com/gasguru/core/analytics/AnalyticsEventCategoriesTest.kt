package com.gasguru.core.analytics

import kotlin.test.Test
import kotlin.test.assertEquals

class AnalyticsEventCategoriesTest {

    // region Onboarding

    @Test
    fun onboardingStartedMapsToOnboarding() = assertEquals(
        AnalyticsEvent.Categories.ONBOARDING,
        AnalyticsEvent.Categories.fromType(AnalyticsEvent.Types.ONBOARDING_STARTED),
    )

    @Test
    fun onboardingPageViewedMapsToOnboarding() = assertEquals(
        AnalyticsEvent.Categories.ONBOARDING,
        AnalyticsEvent.Categories.fromType(AnalyticsEvent.Types.ONBOARDING_PAGE_VIEWED),
    )

    @Test
    fun onboardingSkippedMapsToOnboarding() = assertEquals(
        AnalyticsEvent.Categories.ONBOARDING,
        AnalyticsEvent.Categories.fromType(AnalyticsEvent.Types.ONBOARDING_SKIPPED),
    )

    @Test
    fun onboardingCompletedMapsToOnboarding() = assertEquals(
        AnalyticsEvent.Categories.ONBOARDING,
        AnalyticsEvent.Categories.fromType(AnalyticsEvent.Types.ONBOARDING_COMPLETED),
    )

    // endregion

    // region Vehicle

    @Test
    fun vehicleCreatedMapsToVehicle() = assertEquals(
        AnalyticsEvent.Categories.VEHICLE,
        AnalyticsEvent.Categories.fromType(AnalyticsEvent.Types.VEHICLE_CREATED),
    )

    @Test
    fun vehicleEditedMapsToVehicle() = assertEquals(
        AnalyticsEvent.Categories.VEHICLE,
        AnalyticsEvent.Categories.fromType(AnalyticsEvent.Types.VEHICLE_EDITED),
    )

    @Test
    fun vehicleDeletedMapsToVehicle() = assertEquals(
        AnalyticsEvent.Categories.VEHICLE,
        AnalyticsEvent.Categories.fromType(AnalyticsEvent.Types.VEHICLE_DELETED),
    )

    // endregion

    // region Session

    @Test
    fun appOpenedMapsToSession() = assertEquals(
        AnalyticsEvent.Categories.SESSION,
        AnalyticsEvent.Categories.fromType(AnalyticsEvent.Types.APP_OPENED),
    )

    // endregion

    // region Map

    @Test
    fun stationSelectedMapsToMap() = assertEquals(
        AnalyticsEvent.Categories.MAP,
        AnalyticsEvent.Categories.fromType(AnalyticsEvent.Types.STATION_SELECTED),
    )

    @Test
    fun filterBrandChangedMapsToMap() = assertEquals(
        AnalyticsEvent.Categories.MAP,
        AnalyticsEvent.Categories.fromType(AnalyticsEvent.Types.FILTER_BRAND_CHANGED),
    )

    @Test
    fun filterNearbyChangedMapsToMap() = assertEquals(
        AnalyticsEvent.Categories.MAP,
        AnalyticsEvent.Categories.fromType(AnalyticsEvent.Types.FILTER_NEARBY_CHANGED),
    )

    @Test
    fun filterScheduleChangedMapsToMap() = assertEquals(
        AnalyticsEvent.Categories.MAP,
        AnalyticsEvent.Categories.fromType(AnalyticsEvent.Types.FILTER_SCHEDULE_CHANGED),
    )

    @Test
    fun mapTabChangedMapsToMap() = assertEquals(
        AnalyticsEvent.Categories.MAP,
        AnalyticsEvent.Categories.fromType(AnalyticsEvent.Types.MAP_TAB_CHANGED),
    )

    @Test
    fun routeStartedMapsToMap() = assertEquals(
        AnalyticsEvent.Categories.MAP,
        AnalyticsEvent.Categories.fromType(AnalyticsEvent.Types.ROUTE_STARTED),
    )

    @Test
    fun routeCancelledMapsToMap() = assertEquals(
        AnalyticsEvent.Categories.MAP,
        AnalyticsEvent.Categories.fromType(AnalyticsEvent.Types.ROUTE_CANCELLED),
    )

    // endregion

    // region Station Detail

    @Test
    fun stationDetailViewedMapsToStationDetail() = assertEquals(
        AnalyticsEvent.Categories.STATION_DETAIL,
        AnalyticsEvent.Categories.fromType(AnalyticsEvent.Types.STATION_DETAIL_VIEWED),
    )

    @Test
    fun stationFavoritedMapsToStationDetail() = assertEquals(
        AnalyticsEvent.Categories.STATION_DETAIL,
        AnalyticsEvent.Categories.fromType(AnalyticsEvent.Types.STATION_FAVORITED),
    )

    @Test
    fun stationUnfavoritedMapsToStationDetail() = assertEquals(
        AnalyticsEvent.Categories.STATION_DETAIL,
        AnalyticsEvent.Categories.fromType(AnalyticsEvent.Types.STATION_UNFAVORITED),
    )

    @Test
    fun stationSharedMapsToStationDetail() = assertEquals(
        AnalyticsEvent.Categories.STATION_DETAIL,
        AnalyticsEvent.Categories.fromType(AnalyticsEvent.Types.STATION_SHARED),
    )

    @Test
    fun priceAlertEnabledMapsToStationDetail() = assertEquals(
        AnalyticsEvent.Categories.STATION_DETAIL,
        AnalyticsEvent.Categories.fromType(AnalyticsEvent.Types.PRICE_ALERT_ENABLED),
    )

    @Test
    fun priceAlertDisabledMapsToStationDetail() = assertEquals(
        AnalyticsEvent.Categories.STATION_DETAIL,
        AnalyticsEvent.Categories.fromType(AnalyticsEvent.Types.PRICE_ALERT_DISABLED),
    )

    @Test
    fun priceAlertTriggeredMapsToStationDetail() = assertEquals(
        AnalyticsEvent.Categories.STATION_DETAIL,
        AnalyticsEvent.Categories.fromType(AnalyticsEvent.Types.PRICE_ALERT_TRIGGERED),
    )

    // endregion

    // region Route Planner

    @Test
    fun routePlannerDestinationSetMapsToRoutePlanner() = assertEquals(
        AnalyticsEvent.Categories.ROUTE_PLANNER,
        AnalyticsEvent.Categories.fromType(AnalyticsEvent.Types.ROUTE_PLANNER_DESTINATION_SET),
    )

    @Test
    fun routePlannerDestinationsSwappedMapsToRoutePlanner() = assertEquals(
        AnalyticsEvent.Categories.ROUTE_PLANNER,
        AnalyticsEvent.Categories.fromType(AnalyticsEvent.Types.ROUTE_PLANNER_DESTINATIONS_SWAPPED),
    )

    @Test
    fun recentSearchUsedMapsToRoutePlanner() = assertEquals(
        AnalyticsEvent.Categories.ROUTE_PLANNER,
        AnalyticsEvent.Categories.fromType(AnalyticsEvent.Types.RECENT_SEARCH_USED),
    )

    // endregion

    // region Sync

    @Test
    fun alertsSyncFailedMapsToSync() = assertEquals(
        AnalyticsEvent.Categories.SYNC,
        AnalyticsEvent.Categories.fromType(AnalyticsEvent.Types.ALERTS_SYNC_FAILED),
    )

    @Test
    fun stationSyncWorkerRetriedMapsToSync() = assertEquals(
        AnalyticsEvent.Categories.SYNC,
        AnalyticsEvent.Categories.fromType(AnalyticsEvent.Types.STATION_SYNC_WORKER_RETRIED),
    )

    // endregion

    // region API

    @Test
    fun apiStationsFetchFailedMapsToApi() = assertEquals(
        AnalyticsEvent.Categories.API,
        AnalyticsEvent.Categories.fromType(AnalyticsEvent.Types.API_STATIONS_FETCH_FAILED),
    )

    // endregion

    // region Push

    @Test
    fun pushNotificationTappedMapsToPush() = assertEquals(
        AnalyticsEvent.Categories.PUSH,
        AnalyticsEvent.Categories.fromType(AnalyticsEvent.Types.PUSH_NOTIFICATION_TAPPED),
    )

    // endregion

    // region Widget

    @Test
    fun widgetStationTappedMapsToWidget() = assertEquals(
        AnalyticsEvent.Categories.WIDGET,
        AnalyticsEvent.Categories.fromType(AnalyticsEvent.Types.WIDGET_STATION_TAPPED),
    )

    @Test
    fun widgetAddedToHomeMapsToWidget() = assertEquals(
        AnalyticsEvent.Categories.WIDGET,
        AnalyticsEvent.Categories.fromType(AnalyticsEvent.Types.WIDGET_ADDED_TO_HOME),
    )

    // endregion

    // region Auto

    @Test
    fun autoSessionStartedMapsToAuto() = assertEquals(
        AnalyticsEvent.Categories.AUTO,
        AnalyticsEvent.Categories.fromType(AnalyticsEvent.Types.AUTO_SESSION_STARTED),
    )

    @Test
    fun autoNearbyStationsOpenedMapsToAuto() = assertEquals(
        AnalyticsEvent.Categories.AUTO,
        AnalyticsEvent.Categories.fromType(AnalyticsEvent.Types.AUTO_NEARBY_STATIONS_OPENED),
    )

    @Test
    fun autoFavoriteStationsOpenedMapsToAuto() = assertEquals(
        AnalyticsEvent.Categories.AUTO,
        AnalyticsEvent.Categories.fromType(AnalyticsEvent.Types.AUTO_FAVORITE_STATIONS_OPENED),
    )

    @Test
    fun autoStationNavigationStartedMapsToAuto() = assertEquals(
        AnalyticsEvent.Categories.AUTO,
        AnalyticsEvent.Categories.fromType(AnalyticsEvent.Types.AUTO_STATION_NAVIGATION_STARTED),
    )

    // endregion

    // region Unknown / Category property

    @Test
    fun unregisteredTypeMapsToUnknown() = assertEquals(
        AnalyticsEvent.Categories.UNKNOWN,
        AnalyticsEvent.Categories.fromType(type = "some_future_event"),
    )

    @Test
    fun analyticsEventCategoryPropertyReturnsCorrectCategory() {
        val event = AnalyticsEvent(type = AnalyticsEvent.Types.WIDGET_STATION_TAPPED)
        assertEquals(AnalyticsEvent.Categories.WIDGET, event.category)
    }

    // endregion
}
