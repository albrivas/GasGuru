package com.gasguru.feature.station_map.ui

import android.location.Location
import com.gasguru.core.model.data.Filter
import com.gasguru.core.model.data.FilterType
import com.gasguru.core.model.data.FuelStation
import com.gasguru.core.model.data.FuelType
import com.gasguru.core.model.data.LatLng
import com.gasguru.core.model.data.PriceCategory
import com.gasguru.core.model.data.Route
import com.gasguru.core.model.data.UserData
import com.google.android.gms.maps.model.LatLng as GoogleLatLng
import io.mockk.every
import io.mockk.mockk

object StationMapViewModelTestFixtures {

    fun createTestLocation(
        latitude: Double = 40.0,
        longitude: Double = -4.0,
    ): Location = mockk<Location>(relaxed = true) {
        every { this@mockk.latitude } returns latitude
        every { this@mockk.longitude } returns longitude
    }

    fun createTestFuelStation(
        id: Int = 1,
        latitude: Double = 40.0,
        longitude: Double = -4.0,
        distance: Float = 100f,
        priceGasoilA: Double = 1.50,
        priceGasoline95E5: Double = 1.60,
        priceCategory: PriceCategory = PriceCategory.CHEAP,
    ): FuelStation = mockk<FuelStation>(relaxed = true) {
        every { idServiceStation } returns id
        every { location } returns createTestLocation(latitude = latitude, longitude = longitude)
        every { this@mockk.distance } returns distance
        every { this@mockk.priceGasoilA } returns priceGasoilA
        every { this@mockk.priceGasoline95E5 } returns priceGasoline95E5
        every { this@mockk.priceCategory } returns priceCategory
        every { brandStationName } returns "Test Station $id"
    }

    fun createTestRoute(
        points: List<LatLng> = listOf(
            LatLng(latitude = 40.0, longitude = -4.0),
            LatLng(latitude = 40.1, longitude = -4.1),
            LatLng(latitude = 40.2, longitude = -4.2),
        ),
        distanceText: String = "10 km",
        durationText: String = "15 min",
    ): Route = Route(
        route = points,
        distanceText = distanceText,
        durationText = durationText,
    )

    fun createTestUserData(
        fuelSelection: FuelType = FuelType.DIESEL,
    ): UserData = UserData(
        fuelSelection = fuelSelection,
    )

    fun createTestFilter(
        type: FilterType,
        selection: List<String>,
    ): Filter = Filter(
        type = type,
        selection = selection,
    )

    fun createTestGoogleLatLng(
        latitude: Double = 40.0,
        longitude: Double = -4.0,
    ): GoogleLatLng = GoogleLatLng(latitude, longitude)

    fun createTestFilters(
        brands: List<String> = emptyList(),
        nearby: Int = 10,
        schedule: String = "NONE",
    ): List<Filter> = listOfNotNull(
        if (brands.isNotEmpty()) createTestFilter(type = FilterType.BRAND, selection = brands) else null,
        createTestFilter(type = FilterType.NEARBY, selection = listOf(nearby.toString())),
        createTestFilter(type = FilterType.SCHEDULE, selection = listOf(schedule)),
    )
}
