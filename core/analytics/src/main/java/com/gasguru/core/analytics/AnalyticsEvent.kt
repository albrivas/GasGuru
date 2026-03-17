package com.gasguru.core.analytics

data class AnalyticsEvent(
    val type: String,
    val extras: List<Param> = emptyList(),
) {
    data class Param(val key: String, val value: String)

    object Types {
        // Onboarding
        const val ONBOARDING_STARTED = "onboarding_started"
        const val ONBOARDING_PAGE_VIEWED = "onboarding_page_viewed"
        const val ONBOARDING_SKIPPED = "onboarding_skipped"
        const val ONBOARDING_FUEL_SELECTED = "onboarding_fuel_selected"
        const val ONBOARDING_TANK_CAPACITY_SET = "onboarding_tank_capacity_set"
        const val ONBOARDING_COMPLETED = "onboarding_completed"

        // Vehicles
        const val VEHICLE_CREATED = "vehicle_created"
        const val VEHICLE_EDITED = "vehicle_edited"
        const val VEHICLE_DELETED = "vehicle_deleted"

        // Station map
        const val MAP_STATIONS_LOADED = "map_stations_loaded"
        const val STATION_SELECTED = "station_selected"
        const val FILTER_BRAND_CHANGED = "filter_brand_changed"
        const val FILTER_NEARBY_CHANGED = "filter_nearby_changed"
        const val FILTER_SCHEDULE_CHANGED = "filter_schedule_changed"
        const val MAP_TAB_CHANGED = "map_tab_changed"
        const val ROUTE_STARTED = "route_started"
        const val ROUTE_CANCELLED = "route_cancelled"

        // Station detail
        const val STATION_FAVORITED = "station_favorited"
        const val STATION_UNFAVORITED = "station_unfavorited"
        const val PRICE_ALERT_ENABLED = "price_alert_enabled"
        const val PRICE_ALERT_DISABLED = "price_alert_disabled"

        // Search
        const val SEARCH_PLACE_SELECTED = "search_place_selected"
        const val SEARCH_HISTORY_CLEARED = "search_history_cleared"

        // Route planner
        const val ROUTE_PLANNER_DESTINATION_SET = "route_planner_destination_set"
        const val ROUTE_PLANNER_DESTINATIONS_SWAPPED = "route_planner_destinations_swapped"
        const val RECENT_SEARCH_USED = "recent_search_used"

        // Profile
        const val THEME_CHANGED = "theme_changed"

        // Favorites
        const val FAVORITES_TAB_CHANGED = "favorites_tab_changed"
        const val STATION_UNFAVORITED_FROM_LIST = "station_unfavorited_from_list"

        // Network
        const val WENT_OFFLINE = "went_offline"
        const val CAME_ONLINE = "came_online"

        // Alerts sync
        const val ALERTS_SYNC_COMPLETED = "alerts_sync_completed"
        const val ALERTS_SYNC_FAILED = "alerts_sync_failed"

        // Worker
        const val STATION_SYNC_WORKER_STARTED = "station_sync_worker_started"
        const val STATION_SYNC_WORKER_COMPLETED = "station_sync_worker_completed"
        const val STATION_SYNC_WORKER_RETRIED = "station_sync_worker_retried"

        // API
        const val API_STATIONS_FETCH_STARTED = "api_stations_fetch_started"
        const val API_STATIONS_FETCH_COMPLETED = "api_stations_fetch_completed"
        const val API_STATIONS_FETCH_FAILED = "api_stations_fetch_failed"
    }

    object ParamKeys {
        const val PAGE_NUMBER = "page_number"
        const val FUEL_TYPE = "fuel_type"
        const val CAPACITY_LITRES = "capacity_litres"
        const val VEHICLE_TYPE = "vehicle_type"
        const val IS_PRINCIPAL = "is_principal"
        const val WAS_PRINCIPAL = "was_principal"
        const val VEHICLES_REMAINING = "vehicles_remaining"
        const val STATION_COUNT = "station_count"
        const val STATION_ID = "station_id"
        const val BRAND_COUNT = "brand_count"
        const val NEARBY_KM = "nearby_km"
        const val SCHEDULE = "schedule"
        const val TAB = "tab"
        const val IS_CURRENT_LOCATION = "is_current_location"
        const val THEME_MODE = "theme_mode"
        const val PENDING_SYNC_COUNT = "pending_sync_count"
        const val SYNCED_COUNT = "synced_count"
    }
}
