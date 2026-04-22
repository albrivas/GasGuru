package com.gasguru.core.domain.fuelstation

import com.gasguru.core.domain.fakes.FakeFuelStationRepository
import com.gasguru.core.model.data.LatLng
import com.gasguru.core.model.data.previewFuelStationDomain
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetFuelStationsInRouteUseCaseTest {

    private lateinit var sut: GetFuelStationsInRouteUseCase
    private lateinit var fakeFuelStationRepository: FakeFuelStationRepository

    private val origin = LatLng(latitude = 40.4, longitude = -3.7)
    private val routePoints = listOf(
        LatLng(latitude = 40.5, longitude = -3.6),
        LatLng(latitude = 40.6, longitude = -3.5),
    )

    @BeforeTest
    fun setUp() {
        fakeFuelStationRepository = FakeFuelStationRepository()
        sut = GetFuelStationsInRouteUseCase(repository = fakeFuelStationRepository)
    }

    @Test
    fun returnsStationsAlongRoute() = runTest {
        val stations = listOf(
            previewFuelStationDomain(idServiceStation = 1),
            previewFuelStationDomain(idServiceStation = 2),
        )
        fakeFuelStationRepository.setRouteStations(stations)

        val result = sut(origin = origin, routePoints = routePoints)

        assertEquals(2, result.size)
    }

    @Test
    fun returnsEmptyListWhenNoStationsOnRoute() = runTest {
        val result = sut(origin = origin, routePoints = routePoints)

        assertEquals(0, result.size)
    }

    @Test
    fun delegatesCorrectParamsToRepository() = runTest {
        sut(origin = origin, routePoints = routePoints)

        val request = fakeFuelStationRepository.routeRequests.first()
        assertEquals(origin, request.first)
        assertEquals(routePoints, request.second)
    }
}
