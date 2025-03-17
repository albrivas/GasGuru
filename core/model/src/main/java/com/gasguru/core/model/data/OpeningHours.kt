package com.gasguru.core.model.data

/**
 * Domain enum. Represents the opening hours of the fuel stations
 */
enum class OpeningHours {
    /**
     * Default option. Filter both cases OPEN_NOW and OPEN_24H
     */
    NONE,
    OPEN_NOW,
    OPEN_24H
}
