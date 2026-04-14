package com.gasguru.core.analytics

data class AnalyticsEvent(
    val type: String,
    val extras: List<Param> = emptyList(),
) {
    data class Param(val key: String, val value: String)

    val category: String
        get() = Categories.fromType(type = type)

    object Types {
        // Session
        const val APP_OPENED = "app_opened"

        // Onboarding
        const val ONBOARDING_STARTED = "onboarding_started"
        const val ONBOARDING_PAGE_VIEWED = "onboarding_page_viewed"
        const val ONBOARDING_SKIPPED = "onboarding_skipped"
        const val ONBOARDING_COMPLETED = "onboarding_completed"

        // Vehicles
        const val VEHICLE_CREATED = "vehicle_created"
        const val VEHICLE_EDITED = "vehicle_edited"
        const val VEHICLE_DELETED = "vehicle_deleted"

        // Station map
        const val STATION_SELECTED = "station_selected"
        const val FILTER_BRAND_CHANGED = "filter_brand_changed"
        const val FILTER_NEARBY_CHANGED = "filter_nearby_changed"
        const val FILTER_SCHEDULE_CHANGED = "filter_schedule_changed"
        const val MAP_TAB_CHANGED = "map_tab_changed"
        const val ROUTE_STARTED = "route_started"
        const val ROUTE_CANCELLED = "route_cancelled"

        // Station detail
        const val STATION_DETAIL_VIEWED = "station_detail_viewed"
        const val STATION_FAVORITED = "station_favorited"
        const val STATION_UNFAVORITED = "station_unfavorited"
        const val STATION_SHARED = "station_shared"
        const val PRICE_ALERT_ENABLED = "price_alert_enabled"
        const val PRICE_ALERT_DISABLED = "price_alert_disabled"

        // Search
        const val SEARCH_PERFORMED = "search_performed"
        const val SEARCH_PLACE_SELECTED = "search_place_selected"

        // Route planner
        const val ROUTE_PLANNER_DESTINATION_SET = "route_planner_destination_set"
        const val ROUTE_PLANNER_DESTINATIONS_SWAPPED = "route_planner_destinations_swapped"
        const val RECENT_SEARCH_USED = "recent_search_used"

        // Alerts sync (errors only)
        const val ALERTS_SYNC_FAILED = "alerts_sync_failed"

        // Worker (errors only)
        const val STATION_SYNC_WORKER_RETRIED = "station_sync_worker_retried"

        // API (errors only)
        const val API_STATIONS_FETCH_FAILED = "api_stations_fetch_failed"

        // Push notifications
        const val PUSH_NOTIFICATION_TAPPED = "push_notification_tapped"

        // Widget
        const val WIDGET_STATION_TAPPED = "widget_station_tapped"
        const val WIDGET_ADDED_TO_HOME = "widget_added_to_home"

        // Android Auto
        const val AUTO_SESSION_STARTED = "auto_session_started"
        const val AUTO_NEARBY_STATIONS_OPENED = "auto_nearby_stations_opened"
        const val AUTO_FAVORITE_STATIONS_OPENED = "auto_favorite_stations_opened"
        const val AUTO_STATION_NAVIGATION_STARTED = "auto_station_navigation_started"

        // Price alerts
        const val PRICE_ALERT_TRIGGERED = "price_alert_triggered"
    }

    object Categories {
        const val SESSION = "session"
        const val ONBOARDING = "onboarding"
        const val VEHICLE = "vehicle"
        const val MAP = "map"
        const val STATION_DETAIL = "station_detail"
        const val ROUTE_PLANNER = "route_planner"
        const val SYNC = "sync"
        const val API = "api"
        const val PUSH = "push"
        const val WIDGET = "widget"
        const val AUTO = "auto"
        const val UNKNOWN = "unknown"

        fun fromType(type: String): String = when (type) {
            Types.APP_OPENED,
            -> SESSION

            Types.ONBOARDING_STARTED,
            Types.ONBOARDING_PAGE_VIEWED,
            Types.ONBOARDING_SKIPPED,
            Types.ONBOARDING_COMPLETED,
            -> ONBOARDING

            Types.VEHICLE_CREATED,
            Types.VEHICLE_EDITED,
            Types.VEHICLE_DELETED,
            -> VEHICLE

            Types.STATION_SELECTED,
            Types.FILTER_BRAND_CHANGED,
            Types.FILTER_NEARBY_CHANGED,
            Types.FILTER_SCHEDULE_CHANGED,
            Types.MAP_TAB_CHANGED,
            Types.ROUTE_STARTED,
            Types.ROUTE_CANCELLED,
            -> MAP

            Types.STATION_DETAIL_VIEWED,
            Types.STATION_FAVORITED,
            Types.STATION_UNFAVORITED,
            Types.STATION_SHARED,
            Types.PRICE_ALERT_ENABLED,
            Types.PRICE_ALERT_DISABLED,
            -> STATION_DETAIL

            Types.ROUTE_PLANNER_DESTINATION_SET,
            Types.ROUTE_PLANNER_DESTINATIONS_SWAPPED,
            Types.RECENT_SEARCH_USED,
            -> ROUTE_PLANNER

            Types.ALERTS_SYNC_FAILED,
            Types.STATION_SYNC_WORKER_RETRIED,
            -> SYNC

            Types.API_STATIONS_FETCH_FAILED,
            -> API

            Types.PUSH_NOTIFICATION_TAPPED,
            -> PUSH

            Types.WIDGET_STATION_TAPPED,
            Types.WIDGET_ADDED_TO_HOME,
            -> WIDGET

            Types.AUTO_SESSION_STARTED,
            Types.AUTO_NEARBY_STATIONS_OPENED,
            Types.AUTO_FAVORITE_STATIONS_OPENED,
            Types.AUTO_STATION_NAVIGATION_STARTED,
            -> AUTO

            Types.PRICE_ALERT_TRIGGERED,
            -> STATION_DETAIL

            else -> UNKNOWN
        }
    }

    object ParamKeys {
        const val CATEGORY = "category"
        const val PAGE_NUMBER = "page_number"
        const val FUEL_TYPE = "fuel_type"
        const val CAPACITY_LITRES = "capacity_litres"
        const val VEHICLE_TYPE = "vehicle_type"
        const val IS_PRINCIPAL = "is_principal"
        const val WAS_PRINCIPAL = "was_principal"
        const val VEHICLES_REMAINING = "vehicles_remaining"
        const val STATION_BRAND = "station_brand"
        const val SELECTION_SOURCE = "selection_source"
        const val IS_FAVORITE = "is_favorite"
        const val HAS_PRICE_ALERT = "has_price_alert"
        const val SOURCE = "source"
        const val BRAND_COUNT = "brand_count"
        const val BRAND_NAMES = "brand_names"
        const val NEARBY_KM = "nearby_km"
        const val SCHEDULE = "schedule"
        const val TAB = "tab"
        const val IS_CURRENT_LOCATION = "is_current_location"
        const val ERROR_MESSAGE = "error_message"
        const val ERROR_TYPE = "error_type"
        const val NOTIFICATION_TYPE = "notification_type"
    }
}
