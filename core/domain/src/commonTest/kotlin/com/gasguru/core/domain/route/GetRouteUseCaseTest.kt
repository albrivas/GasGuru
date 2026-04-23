package com.gasguru.core.domain.route

import app.cash.turbine.test
import com.gasguru.core.domain.fakes.FakeRoutesRepository
import com.gasguru.core.model.data.LatLng
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class GetRouteUseCaseTest {

    private lateinit var sut: GetRouteUseCase
    private lateinit var fakeRoutesRepository: FakeRoutesRepository

    private val origin = LatLng(latitude = 40.4, longitude = -3.7)
    private val destination = LatLng(latitude = 41.0, longitude = -4.0)

    @BeforeTest
    fun setUp() {
        fakeRoutesRepository = FakeRoutesRepository()
        sut = GetRouteUseCase(routesRepository = fakeRoutesRepository)
    }

    @Test
    fun emitsNullWhenNoRouteAvailable() = runTest {
        sut(origin = origin, destination = destination).test {
            assertNull(awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun delegatesCorrectOriginAndDestination() = runTest {
        sut(origin = origin, destination = destination).test {
            awaitItem()
            cancelAndIgnoreRemainingEvents()
        }

        assertEquals(origin, fakeRoutesRepository.lastOrigin)
        assertEquals(destination, fakeRoutesRepository.lastDestination)
    }
}
