package com.gasguru.core.analytics

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class AnalyticsEventCategoriesTest {

    @Nested
    @DisplayName("onboarding")
    inner class OnboardingCategoryTest {

        @Test
        @DisplayName("ONBOARDING_STARTED maps to onboarding")
        fun `ONBOARDING_STARTED maps to onboarding`() =
            assertEquals(
                AnalyticsEvent.Categories.ONBOARDING,
                AnalyticsEvent.Categories.fromType(AnalyticsEvent.Types.ONBOARDING_STARTED)
            )

        @Test
        @DisplayName("ONBOARDING_PAGE_VIEWED maps to onboarding")
        fun `ONBOARDING_PAGE_VIEWED maps to onboarding`() =
            assertEquals(
                AnalyticsEvent.Categories.ONBOARDING,
                AnalyticsEvent.Categories.fromType(AnalyticsEvent.Types.ONBOARDING_PAGE_VIEWED)
            )

        @Test
        @DisplayName("ONBOARDING_SKIPPED maps to onboarding")
        fun `ONBOARDING_SKIPPED maps to onboarding`() =
            assertEquals(
                AnalyticsEvent.Categories.ONBOARDING,
                AnalyticsEvent.Categories.fromType(AnalyticsEvent.Types.ONBOARDING_SKIPPED)
            )

        @Test
        @DisplayName("ONBOARDING_COMPLETED maps to onboarding")
        fun `ONBOARDING_COMPLETED maps to onboarding`() =
            assertEquals(
                AnalyticsEvent.Categories.ONBOARDING,
                AnalyticsEvent.Categories.fromType(AnalyticsEvent.Types.ONBOARDING_COMPLETED)
            )
    }

    @Nested
    @DisplayName("vehicle")
    inner class VehicleCategoryTest {

        @Test
        @DisplayName("VEHICLE_CREATED maps to vehicle")
        fun `VEHICLE_CREATED maps to vehicle`() =
            assertEquals(
                AnalyticsEvent.Categories.VEHICLE,
                AnalyticsEvent.Categories.fromType(AnalyticsEvent.Types.VEHICLE_CREATED)
            )

        @Test
        @DisplayName("VEHICLE_EDITED maps to vehicle")
        fun `VEHICLE_EDITED maps to vehicle`() =
            assertEquals(
                AnalyticsEvent.Categories.VEHICLE,
                AnalyticsEvent.Categories.fromType(AnalyticsEvent.Types.VEHICLE_EDITED)
            )

        @Test
        @DisplayName("VEHICLE_DELETED maps to vehicle")
        fun `VEHICLE_DELETED maps to vehicle`() =
            assertEquals(
                AnalyticsEvent.Categories.VEHICLE,
                AnalyticsEvent.Categories.fromType(AnalyticsEvent.Types.VEHICLE_DELETED)
            )
    }

    @Nested
    @DisplayName("session")
    inner class SessionCategoryTest {

        @Test
        @DisplayName("APP_OPENED maps to session")
        fun `APP_OPENED maps to session`() =
            assertEquals(
                AnalyticsEvent.Categories.SESSION,
                AnalyticsEvent.Categories.fromType(AnalyticsEvent.Types.APP_OPENED)
            )
    }

    @Nested
    @DisplayName("map")
    inner class MapCategoryTest {

        @Test
        @DisplayName("STATION_SELECTED maps to map")
        fun `STATION_SELECTED maps to map`() =
            assertEquals(
                AnalyticsEvent.Categories.MAP,
                AnalyticsEvent.Categories.fromType(AnalyticsEvent.Types.STATION_SELECTED)
            )

        @Test
        @DisplayName("FILTER_BRAND_CHANGED maps to map")
        fun `FILTER_BRAND_CHANGED maps to map`() =
            assertEquals(
                AnalyticsEvent.Categories.MAP,
                AnalyticsEvent.Categories.fromType(AnalyticsEvent.Types.FILTER_BRAND_CHANGED)
            )

        @Test
        @DisplayName("FILTER_NEARBY_CHANGED maps to map")
        fun `FILTER_NEARBY_CHANGED maps to map`() =
            assertEquals(
                AnalyticsEvent.Categories.MAP,
                AnalyticsEvent.Categories.fromType(AnalyticsEvent.Types.FILTER_NEARBY_CHANGED)
            )

        @Test
        @DisplayName("FILTER_SCHEDULE_CHANGED maps to map")
        fun `FILTER_SCHEDULE_CHANGED maps to map`() =
            assertEquals(
                AnalyticsEvent.Categories.MAP,
                AnalyticsEvent.Categories.fromType(AnalyticsEvent.Types.FILTER_SCHEDULE_CHANGED)
            )

        @Test
        @DisplayName("MAP_TAB_CHANGED maps to map")
        fun `MAP_TAB_CHANGED maps to map`() =
            assertEquals(
                AnalyticsEvent.Categories.MAP,
                AnalyticsEvent.Categories.fromType(AnalyticsEvent.Types.MAP_TAB_CHANGED)
            )

        @Test
        @DisplayName("ROUTE_STARTED maps to map")
        fun `ROUTE_STARTED maps to map`() =
            assertEquals(
                AnalyticsEvent.Categories.MAP,
                AnalyticsEvent.Categories.fromType(AnalyticsEvent.Types.ROUTE_STARTED)
            )

        @Test
        @DisplayName("ROUTE_CANCELLED maps to map")
        fun `ROUTE_CANCELLED maps to map`() =
            assertEquals(
                AnalyticsEvent.Categories.MAP,
                AnalyticsEvent.Categories.fromType(AnalyticsEvent.Types.ROUTE_CANCELLED)
            )
    }

    @Nested
    @DisplayName("station_detail")
    inner class StationDetailCategoryTest {

        @Test
        @DisplayName("STATION_DETAIL_VIEWED maps to station_detail")
        fun `STATION_DETAIL_VIEWED maps to station_detail`() =
            assertEquals(
                AnalyticsEvent.Categories.STATION_DETAIL,
                AnalyticsEvent.Categories.fromType(AnalyticsEvent.Types.STATION_DETAIL_VIEWED)
            )

        @Test
        @DisplayName("STATION_FAVORITED maps to station_detail")
        fun `STATION_FAVORITED maps to station_detail`() =
            assertEquals(
                AnalyticsEvent.Categories.STATION_DETAIL,
                AnalyticsEvent.Categories.fromType(AnalyticsEvent.Types.STATION_FAVORITED)
            )

        @Test
        @DisplayName("STATION_UNFAVORITED maps to station_detail")
        fun `STATION_UNFAVORITED maps to station_detail`() =
            assertEquals(
                AnalyticsEvent.Categories.STATION_DETAIL,
                AnalyticsEvent.Categories.fromType(AnalyticsEvent.Types.STATION_UNFAVORITED)
            )

        @Test
        @DisplayName("STATION_SHARED maps to station_detail")
        fun `STATION_SHARED maps to station_detail`() =
            assertEquals(
                AnalyticsEvent.Categories.STATION_DETAIL,
                AnalyticsEvent.Categories.fromType(AnalyticsEvent.Types.STATION_SHARED)
            )

        @Test
        @DisplayName("PRICE_ALERT_ENABLED maps to station_detail")
        fun `PRICE_ALERT_ENABLED maps to station_detail`() =
            assertEquals(
                AnalyticsEvent.Categories.STATION_DETAIL,
                AnalyticsEvent.Categories.fromType(AnalyticsEvent.Types.PRICE_ALERT_ENABLED)
            )

        @Test
        @DisplayName("PRICE_ALERT_DISABLED maps to station_detail")
        fun `PRICE_ALERT_DISABLED maps to station_detail`() =
            assertEquals(
                AnalyticsEvent.Categories.STATION_DETAIL,
                AnalyticsEvent.Categories.fromType(AnalyticsEvent.Types.PRICE_ALERT_DISABLED)
            )

        @Test
        @DisplayName("PRICE_ALERT_TRIGGERED maps to station_detail")
        fun `PRICE_ALERT_TRIGGERED maps to station_detail`() =
            assertEquals(
                AnalyticsEvent.Categories.STATION_DETAIL,
                AnalyticsEvent.Categories.fromType(AnalyticsEvent.Types.PRICE_ALERT_TRIGGERED)
            )
    }

    @Nested
    @DisplayName("route_planner")
    inner class RoutePlannerCategoryTest {

        @Test
        @DisplayName("ROUTE_PLANNER_DESTINATION_SET maps to route_planner")
        fun `ROUTE_PLANNER_DESTINATION_SET maps to route_planner`() =
            assertEquals(
                AnalyticsEvent.Categories.ROUTE_PLANNER,
                AnalyticsEvent.Categories.fromType(AnalyticsEvent.Types.ROUTE_PLANNER_DESTINATION_SET)
            )

        @Test
        @DisplayName("ROUTE_PLANNER_DESTINATIONS_SWAPPED maps to route_planner")
        fun `ROUTE_PLANNER_DESTINATIONS_SWAPPED maps to route_planner`() =
            assertEquals(
                AnalyticsEvent.Categories.ROUTE_PLANNER,
                AnalyticsEvent.Categories.fromType(AnalyticsEvent.Types.ROUTE_PLANNER_DESTINATIONS_SWAPPED)
            )

        @Test
        @DisplayName("RECENT_SEARCH_USED maps to route_planner")
        fun `RECENT_SEARCH_USED maps to route_planner`() =
            assertEquals(
                AnalyticsEvent.Categories.ROUTE_PLANNER,
                AnalyticsEvent.Categories.fromType(AnalyticsEvent.Types.RECENT_SEARCH_USED)
            )
    }

    @Nested
    @DisplayName("sync")
    inner class SyncCategoryTest {

        @Test
        @DisplayName("ALERTS_SYNC_FAILED maps to sync")
        fun `ALERTS_SYNC_FAILED maps to sync`() =
            assertEquals(
                AnalyticsEvent.Categories.SYNC,
                AnalyticsEvent.Categories.fromType(AnalyticsEvent.Types.ALERTS_SYNC_FAILED)
            )

        @Test
        @DisplayName("STATION_SYNC_WORKER_RETRIED maps to sync")
        fun `STATION_SYNC_WORKER_RETRIED maps to sync`() =
            assertEquals(
                AnalyticsEvent.Categories.SYNC,
                AnalyticsEvent.Categories.fromType(AnalyticsEvent.Types.STATION_SYNC_WORKER_RETRIED)
            )
    }

    @Nested
    @DisplayName("api")
    inner class ApiCategoryTest {

        @Test
        @DisplayName("API_STATIONS_FETCH_FAILED maps to api")
        fun `API_STATIONS_FETCH_FAILED maps to api`() =
            assertEquals(
                AnalyticsEvent.Categories.API,
                AnalyticsEvent.Categories.fromType(AnalyticsEvent.Types.API_STATIONS_FETCH_FAILED)
            )
    }

    @Nested
    @DisplayName("push")
    inner class PushCategoryTest {

        @Test
        @DisplayName("PUSH_NOTIFICATION_TAPPED maps to push")
        fun `PUSH_NOTIFICATION_TAPPED maps to push`() =
            assertEquals(
                AnalyticsEvent.Categories.PUSH,
                AnalyticsEvent.Categories.fromType(AnalyticsEvent.Types.PUSH_NOTIFICATION_TAPPED)
            )
    }

    @Nested
    @DisplayName("widget")
    inner class WidgetCategoryTest {

        @Test
        @DisplayName("WIDGET_STATION_TAPPED maps to widget")
        fun `WIDGET_STATION_TAPPED maps to widget`() =
            assertEquals(
                AnalyticsEvent.Categories.WIDGET,
                AnalyticsEvent.Categories.fromType(AnalyticsEvent.Types.WIDGET_STATION_TAPPED)
            )

        @Test
        @DisplayName("WIDGET_ADDED_TO_HOME maps to widget")
        fun `WIDGET_ADDED_TO_HOME maps to widget`() =
            assertEquals(
                AnalyticsEvent.Categories.WIDGET,
                AnalyticsEvent.Categories.fromType(AnalyticsEvent.Types.WIDGET_ADDED_TO_HOME)
            )
    }

    @Nested
    @DisplayName("auto")
    inner class AutoCategoryTest {

        @Test
        @DisplayName("AUTO_SESSION_STARTED maps to auto")
        fun `AUTO_SESSION_STARTED maps to auto`() =
            assertEquals(
                AnalyticsEvent.Categories.AUTO,
                AnalyticsEvent.Categories.fromType(AnalyticsEvent.Types.AUTO_SESSION_STARTED)
            )

        @Test
        @DisplayName("AUTO_NEARBY_STATIONS_OPENED maps to auto")
        fun `AUTO_NEARBY_STATIONS_OPENED maps to auto`() =
            assertEquals(
                AnalyticsEvent.Categories.AUTO,
                AnalyticsEvent.Categories.fromType(AnalyticsEvent.Types.AUTO_NEARBY_STATIONS_OPENED)
            )

        @Test
        @DisplayName("AUTO_FAVORITE_STATIONS_OPENED maps to auto")
        fun `AUTO_FAVORITE_STATIONS_OPENED maps to auto`() =
            assertEquals(
                AnalyticsEvent.Categories.AUTO,
                AnalyticsEvent.Categories.fromType(AnalyticsEvent.Types.AUTO_FAVORITE_STATIONS_OPENED)
            )

        @Test
        @DisplayName("AUTO_STATION_NAVIGATION_STARTED maps to auto")
        fun `AUTO_STATION_NAVIGATION_STARTED maps to auto`() =
            assertEquals(
                AnalyticsEvent.Categories.AUTO,
                AnalyticsEvent.Categories.fromType(AnalyticsEvent.Types.AUTO_STATION_NAVIGATION_STARTED)
            )
    }

    @Nested
    @DisplayName("unknown")
    inner class UnknownCategoryTest {

        @Test
        @DisplayName("unregistered type maps to unknown")
        fun `unregistered type maps to unknown`() =
            assertEquals(
                AnalyticsEvent.Categories.UNKNOWN,
                AnalyticsEvent.Categories.fromType(type = "some_future_event")
            )
    }

    @Nested
    @DisplayName("category computed property")
    inner class CategoryPropertyTest {

        @Test
        @DisplayName("AnalyticsEvent.category returns correct category for the event type")
        fun `AnalyticsEvent category returns correct category for the event type`() {
            val event = AnalyticsEvent(type = AnalyticsEvent.Types.WIDGET_STATION_TAPPED)
            assertEquals(AnalyticsEvent.Categories.WIDGET, event.category)
        }
    }
}
