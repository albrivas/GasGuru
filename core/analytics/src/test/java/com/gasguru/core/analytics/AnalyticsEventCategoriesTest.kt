package com.gasguru.core.analytics

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("AnalyticsEvent.Categories")
class AnalyticsEventCategoriesTest {

    @Nested
    @DisplayName("onboarding")
    inner class OnboardingCategoryTest {

        @Test
        @DisplayName("ONBOARDING_STARTED maps to onboarding")
        fun `ONBOARDING_STARTED maps to onboarding`() =
            assertEquals(AnalyticsEvent.Categories.ONBOARDING, AnalyticsEvent.Categories.fromType(AnalyticsEvent.Types.ONBOARDING_STARTED))

        @Test
        @DisplayName("ONBOARDING_PAGE_VIEWED maps to onboarding")
        fun `ONBOARDING_PAGE_VIEWED maps to onboarding`() =
            assertEquals(AnalyticsEvent.Categories.ONBOARDING, AnalyticsEvent.Categories.fromType(AnalyticsEvent.Types.ONBOARDING_PAGE_VIEWED))

        @Test
        @DisplayName("ONBOARDING_SKIPPED maps to onboarding")
        fun `ONBOARDING_SKIPPED maps to onboarding`() =
            assertEquals(AnalyticsEvent.Categories.ONBOARDING, AnalyticsEvent.Categories.fromType(AnalyticsEvent.Types.ONBOARDING_SKIPPED))

        @Test
        @DisplayName("ONBOARDING_FUEL_SELECTED maps to onboarding")
        fun `ONBOARDING_FUEL_SELECTED maps to onboarding`() =
            assertEquals(AnalyticsEvent.Categories.ONBOARDING, AnalyticsEvent.Categories.fromType(AnalyticsEvent.Types.ONBOARDING_FUEL_SELECTED))

        @Test
        @DisplayName("ONBOARDING_TANK_CAPACITY_SET maps to onboarding")
        fun `ONBOARDING_TANK_CAPACITY_SET maps to onboarding`() =
            assertEquals(AnalyticsEvent.Categories.ONBOARDING, AnalyticsEvent.Categories.fromType(AnalyticsEvent.Types.ONBOARDING_TANK_CAPACITY_SET))

        @Test
        @DisplayName("ONBOARDING_COMPLETED maps to onboarding")
        fun `ONBOARDING_COMPLETED maps to onboarding`() =
            assertEquals(AnalyticsEvent.Categories.ONBOARDING, AnalyticsEvent.Categories.fromType(AnalyticsEvent.Types.ONBOARDING_COMPLETED))
    }

    @Nested
    @DisplayName("vehicle")
    inner class VehicleCategoryTest {

        @Test
        @DisplayName("VEHICLE_CREATED maps to vehicle")
        fun `VEHICLE_CREATED maps to vehicle`() =
            assertEquals(AnalyticsEvent.Categories.VEHICLE, AnalyticsEvent.Categories.fromType(AnalyticsEvent.Types.VEHICLE_CREATED))

        @Test
        @DisplayName("VEHICLE_EDITED maps to vehicle")
        fun `VEHICLE_EDITED maps to vehicle`() =
            assertEquals(AnalyticsEvent.Categories.VEHICLE, AnalyticsEvent.Categories.fromType(AnalyticsEvent.Types.VEHICLE_EDITED))

        @Test
        @DisplayName("VEHICLE_DELETED maps to vehicle")
        fun `VEHICLE_DELETED maps to vehicle`() =
            assertEquals(AnalyticsEvent.Categories.VEHICLE, AnalyticsEvent.Categories.fromType(AnalyticsEvent.Types.VEHICLE_DELETED))
    }

    @Nested
    @DisplayName("map")
    inner class MapCategoryTest {

        @Test
        @DisplayName("MAP_STATIONS_LOADED maps to map")
        fun `MAP_STATIONS_LOADED maps to map`() =
            assertEquals(AnalyticsEvent.Categories.MAP, AnalyticsEvent.Categories.fromType(AnalyticsEvent.Types.MAP_STATIONS_LOADED))

        @Test
        @DisplayName("STATION_SELECTED maps to map")
        fun `STATION_SELECTED maps to map`() =
            assertEquals(AnalyticsEvent.Categories.MAP, AnalyticsEvent.Categories.fromType(AnalyticsEvent.Types.STATION_SELECTED))

        @Test
        @DisplayName("FILTER_BRAND_CHANGED maps to map")
        fun `FILTER_BRAND_CHANGED maps to map`() =
            assertEquals(AnalyticsEvent.Categories.MAP, AnalyticsEvent.Categories.fromType(AnalyticsEvent.Types.FILTER_BRAND_CHANGED))

        @Test
        @DisplayName("FILTER_NEARBY_CHANGED maps to map")
        fun `FILTER_NEARBY_CHANGED maps to map`() =
            assertEquals(AnalyticsEvent.Categories.MAP, AnalyticsEvent.Categories.fromType(AnalyticsEvent.Types.FILTER_NEARBY_CHANGED))

        @Test
        @DisplayName("FILTER_SCHEDULE_CHANGED maps to map")
        fun `FILTER_SCHEDULE_CHANGED maps to map`() =
            assertEquals(AnalyticsEvent.Categories.MAP, AnalyticsEvent.Categories.fromType(AnalyticsEvent.Types.FILTER_SCHEDULE_CHANGED))

        @Test
        @DisplayName("MAP_TAB_CHANGED maps to map")
        fun `MAP_TAB_CHANGED maps to map`() =
            assertEquals(AnalyticsEvent.Categories.MAP, AnalyticsEvent.Categories.fromType(AnalyticsEvent.Types.MAP_TAB_CHANGED))

        @Test
        @DisplayName("ROUTE_STARTED maps to map")
        fun `ROUTE_STARTED maps to map`() =
            assertEquals(AnalyticsEvent.Categories.MAP, AnalyticsEvent.Categories.fromType(AnalyticsEvent.Types.ROUTE_STARTED))

        @Test
        @DisplayName("ROUTE_CANCELLED maps to map")
        fun `ROUTE_CANCELLED maps to map`() =
            assertEquals(AnalyticsEvent.Categories.MAP, AnalyticsEvent.Categories.fromType(AnalyticsEvent.Types.ROUTE_CANCELLED))
    }

    @Nested
    @DisplayName("station_detail")
    inner class StationDetailCategoryTest {

        @Test
        @DisplayName("STATION_FAVORITED maps to station_detail")
        fun `STATION_FAVORITED maps to station_detail`() =
            assertEquals(AnalyticsEvent.Categories.STATION_DETAIL, AnalyticsEvent.Categories.fromType(AnalyticsEvent.Types.STATION_FAVORITED))

        @Test
        @DisplayName("STATION_UNFAVORITED maps to station_detail")
        fun `STATION_UNFAVORITED maps to station_detail`() =
            assertEquals(AnalyticsEvent.Categories.STATION_DETAIL, AnalyticsEvent.Categories.fromType(AnalyticsEvent.Types.STATION_UNFAVORITED))

        @Test
        @DisplayName("STATION_SHARED maps to station_detail")
        fun `STATION_SHARED maps to station_detail`() =
            assertEquals(AnalyticsEvent.Categories.STATION_DETAIL, AnalyticsEvent.Categories.fromType(AnalyticsEvent.Types.STATION_SHARED))

        @Test
        @DisplayName("PRICE_ALERT_ENABLED maps to station_detail")
        fun `PRICE_ALERT_ENABLED maps to station_detail`() =
            assertEquals(AnalyticsEvent.Categories.STATION_DETAIL, AnalyticsEvent.Categories.fromType(AnalyticsEvent.Types.PRICE_ALERT_ENABLED))

        @Test
        @DisplayName("PRICE_ALERT_DISABLED maps to station_detail")
        fun `PRICE_ALERT_DISABLED maps to station_detail`() =
            assertEquals(AnalyticsEvent.Categories.STATION_DETAIL, AnalyticsEvent.Categories.fromType(AnalyticsEvent.Types.PRICE_ALERT_DISABLED))
    }

    @Nested
    @DisplayName("search")
    inner class SearchCategoryTest {

        @Test
        @DisplayName("SEARCH_PLACE_SELECTED maps to search")
        fun `SEARCH_PLACE_SELECTED maps to search`() =
            assertEquals(AnalyticsEvent.Categories.SEARCH, AnalyticsEvent.Categories.fromType(AnalyticsEvent.Types.SEARCH_PLACE_SELECTED))

        @Test
        @DisplayName("SEARCH_HISTORY_CLEARED maps to search")
        fun `SEARCH_HISTORY_CLEARED maps to search`() =
            assertEquals(AnalyticsEvent.Categories.SEARCH, AnalyticsEvent.Categories.fromType(AnalyticsEvent.Types.SEARCH_HISTORY_CLEARED))
    }

    @Nested
    @DisplayName("route_planner")
    inner class RoutePlannerCategoryTest {

        @Test
        @DisplayName("ROUTE_PLANNER_DESTINATION_SET maps to route_planner")
        fun `ROUTE_PLANNER_DESTINATION_SET maps to route_planner`() =
            assertEquals(AnalyticsEvent.Categories.ROUTE_PLANNER, AnalyticsEvent.Categories.fromType(AnalyticsEvent.Types.ROUTE_PLANNER_DESTINATION_SET))

        @Test
        @DisplayName("ROUTE_PLANNER_DESTINATIONS_SWAPPED maps to route_planner")
        fun `ROUTE_PLANNER_DESTINATIONS_SWAPPED maps to route_planner`() =
            assertEquals(AnalyticsEvent.Categories.ROUTE_PLANNER, AnalyticsEvent.Categories.fromType(AnalyticsEvent.Types.ROUTE_PLANNER_DESTINATIONS_SWAPPED))

        @Test
        @DisplayName("RECENT_SEARCH_USED maps to route_planner")
        fun `RECENT_SEARCH_USED maps to route_planner`() =
            assertEquals(AnalyticsEvent.Categories.ROUTE_PLANNER, AnalyticsEvent.Categories.fromType(AnalyticsEvent.Types.RECENT_SEARCH_USED))
    }

    @Nested
    @DisplayName("profile")
    inner class ProfileCategoryTest {

        @Test
        @DisplayName("THEME_CHANGED maps to profile")
        fun `THEME_CHANGED maps to profile`() =
            assertEquals(AnalyticsEvent.Categories.PROFILE, AnalyticsEvent.Categories.fromType(AnalyticsEvent.Types.THEME_CHANGED))
    }

    @Nested
    @DisplayName("favorites")
    inner class FavoritesCategoryTest {

        @Test
        @DisplayName("FAVORITES_TAB_CHANGED maps to favorites")
        fun `FAVORITES_TAB_CHANGED maps to favorites`() =
            assertEquals(AnalyticsEvent.Categories.FAVORITES, AnalyticsEvent.Categories.fromType(AnalyticsEvent.Types.FAVORITES_TAB_CHANGED))

        @Test
        @DisplayName("STATION_UNFAVORITED_FROM_LIST maps to favorites")
        fun `STATION_UNFAVORITED_FROM_LIST maps to favorites`() =
            assertEquals(AnalyticsEvent.Categories.FAVORITES, AnalyticsEvent.Categories.fromType(AnalyticsEvent.Types.STATION_UNFAVORITED_FROM_LIST))
    }

    @Nested
    @DisplayName("network")
    inner class NetworkCategoryTest {

        @Test
        @DisplayName("WENT_OFFLINE maps to network")
        fun `WENT_OFFLINE maps to network`() =
            assertEquals(AnalyticsEvent.Categories.NETWORK, AnalyticsEvent.Categories.fromType(AnalyticsEvent.Types.WENT_OFFLINE))

        @Test
        @DisplayName("CAME_ONLINE maps to network")
        fun `CAME_ONLINE maps to network`() =
            assertEquals(AnalyticsEvent.Categories.NETWORK, AnalyticsEvent.Categories.fromType(AnalyticsEvent.Types.CAME_ONLINE))
    }

    @Nested
    @DisplayName("sync")
    inner class SyncCategoryTest {

        @Test
        @DisplayName("ALERTS_SYNC_COMPLETED maps to sync")
        fun `ALERTS_SYNC_COMPLETED maps to sync`() =
            assertEquals(AnalyticsEvent.Categories.SYNC, AnalyticsEvent.Categories.fromType(AnalyticsEvent.Types.ALERTS_SYNC_COMPLETED))

        @Test
        @DisplayName("ALERTS_SYNC_FAILED maps to sync")
        fun `ALERTS_SYNC_FAILED maps to sync`() =
            assertEquals(AnalyticsEvent.Categories.SYNC, AnalyticsEvent.Categories.fromType(AnalyticsEvent.Types.ALERTS_SYNC_FAILED))

        @Test
        @DisplayName("STATION_SYNC_WORKER_STARTED maps to sync")
        fun `STATION_SYNC_WORKER_STARTED maps to sync`() =
            assertEquals(AnalyticsEvent.Categories.SYNC, AnalyticsEvent.Categories.fromType(AnalyticsEvent.Types.STATION_SYNC_WORKER_STARTED))

        @Test
        @DisplayName("STATION_SYNC_WORKER_COMPLETED maps to sync")
        fun `STATION_SYNC_WORKER_COMPLETED maps to sync`() =
            assertEquals(AnalyticsEvent.Categories.SYNC, AnalyticsEvent.Categories.fromType(AnalyticsEvent.Types.STATION_SYNC_WORKER_COMPLETED))

        @Test
        @DisplayName("STATION_SYNC_WORKER_RETRIED maps to sync")
        fun `STATION_SYNC_WORKER_RETRIED maps to sync`() =
            assertEquals(AnalyticsEvent.Categories.SYNC, AnalyticsEvent.Categories.fromType(AnalyticsEvent.Types.STATION_SYNC_WORKER_RETRIED))
    }

    @Nested
    @DisplayName("api")
    inner class ApiCategoryTest {

        @Test
        @DisplayName("API_STATIONS_FETCH_STARTED maps to api")
        fun `API_STATIONS_FETCH_STARTED maps to api`() =
            assertEquals(AnalyticsEvent.Categories.API, AnalyticsEvent.Categories.fromType(AnalyticsEvent.Types.API_STATIONS_FETCH_STARTED))

        @Test
        @DisplayName("API_STATIONS_FETCH_COMPLETED maps to api")
        fun `API_STATIONS_FETCH_COMPLETED maps to api`() =
            assertEquals(AnalyticsEvent.Categories.API, AnalyticsEvent.Categories.fromType(AnalyticsEvent.Types.API_STATIONS_FETCH_COMPLETED))

        @Test
        @DisplayName("API_STATIONS_FETCH_FAILED maps to api")
        fun `API_STATIONS_FETCH_FAILED maps to api`() =
            assertEquals(AnalyticsEvent.Categories.API, AnalyticsEvent.Categories.fromType(AnalyticsEvent.Types.API_STATIONS_FETCH_FAILED))
    }

    @Nested
    @DisplayName("push")
    inner class PushCategoryTest {

        @Test
        @DisplayName("PUSH_NOTIFICATION_TAPPED maps to push")
        fun `PUSH_NOTIFICATION_TAPPED maps to push`() =
            assertEquals(AnalyticsEvent.Categories.PUSH, AnalyticsEvent.Categories.fromType(AnalyticsEvent.Types.PUSH_NOTIFICATION_TAPPED))
    }

    @Nested
    @DisplayName("widget")
    inner class WidgetCategoryTest {

        @Test
        @DisplayName("WIDGET_STATION_TAPPED maps to widget")
        fun `WIDGET_STATION_TAPPED maps to widget`() =
            assertEquals(AnalyticsEvent.Categories.WIDGET, AnalyticsEvent.Categories.fromType(AnalyticsEvent.Types.WIDGET_STATION_TAPPED))
    }

    @Nested
    @DisplayName("auto")
    inner class AutoCategoryTest {

        @Test
        @DisplayName("AUTO_SESSION_STARTED maps to auto")
        fun `AUTO_SESSION_STARTED maps to auto`() =
            assertEquals(AnalyticsEvent.Categories.AUTO, AnalyticsEvent.Categories.fromType(AnalyticsEvent.Types.AUTO_SESSION_STARTED))

        @Test
        @DisplayName("AUTO_NEARBY_STATIONS_OPENED maps to auto")
        fun `AUTO_NEARBY_STATIONS_OPENED maps to auto`() =
            assertEquals(AnalyticsEvent.Categories.AUTO, AnalyticsEvent.Categories.fromType(AnalyticsEvent.Types.AUTO_NEARBY_STATIONS_OPENED))

        @Test
        @DisplayName("AUTO_FAVORITE_STATIONS_OPENED maps to auto")
        fun `AUTO_FAVORITE_STATIONS_OPENED maps to auto`() =
            assertEquals(AnalyticsEvent.Categories.AUTO, AnalyticsEvent.Categories.fromType(AnalyticsEvent.Types.AUTO_FAVORITE_STATIONS_OPENED))

        @Test
        @DisplayName("AUTO_STATION_NAVIGATION_STARTED maps to auto")
        fun `AUTO_STATION_NAVIGATION_STARTED maps to auto`() =
            assertEquals(AnalyticsEvent.Categories.AUTO, AnalyticsEvent.Categories.fromType(AnalyticsEvent.Types.AUTO_STATION_NAVIGATION_STARTED))
    }

    @Nested
    @DisplayName("unknown")
    inner class UnknownCategoryTest {

        @Test
        @DisplayName("unregistered type maps to unknown")
        fun `unregistered type maps to unknown`() =
            assertEquals(AnalyticsEvent.Categories.UNKNOWN, AnalyticsEvent.Categories.fromType(type = "some_future_event"))
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
