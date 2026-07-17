package com.gasguru.core.uikit.utils

object TestTags {
    object Onboarding {
        const val CONTINUE_BUTTON = "onboarding_continue_button"
        const val FUEL_NEXT = "button_next_onboarding"
        const val CAPACITY_CONTINUE = "button_capacity_continue"
        fun fuelChip(index: Int) = "onboarding_fuel_chip_$index"
        fun capacityChip(litres: Int) = "onboarding_capacity_chip_$litres"
    }

    object Home {
        const val SHOW_LIST_BUTTON = "home_show_list_button"
        const val CREATE_ROUTE_FAB = "home_create_route_fab"
        fun stationItem(index: Int) = "home_station_item_$index"
    }

    object RoutePlanner {
        const val ORIGIN_FIELD = "route_origin_field"
        const val DESTINATION_FIELD = "route_destination_field"
        const val START_ROUTE_BUTTON = "route_start_button"
    }

    object DetailStation {
        const val BACK_TO_MAP = "detail_back_to_map"
    }

    object Vehicle {
        const val FUEL_SELECTOR = "vehicle_fuel_selector"
        const val FUEL_PICKER_CLOSE = "vehicle_fuel_picker_close"
    }
}
