package com.gasguru.core.domain.fuelstation

import com.gasguru.core.domain.fakes.FakeFuelStationRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetFuelStationUseCaseTest {

    private lateinit var sut: GetFuelStationUseCase
    private lateinit var fakeFuelStationRepository: FakeFuelStationRepository

    @BeforeTest
    fun setUp() {
        fakeFuelStationRepository = FakeFuelStationRepository()
        sut = GetFuelStationUseCase(repository = fakeFuelStationRepository)
    }

    @Test
    fun getFuelInAllStationsDelegatesToRepository() = runTest {
        sut.getFuelInAllStations()

        assertEquals(1, fakeFuelStationRepository.addAllStationsCalls)
    }

    @Test
    fun getFuelInAllStationsCanBeCalledMultipleTimes() = runTest {
        sut.getFuelInAllStations()
        sut.getFuelInAllStations()

        assertEquals(2, fakeFuelStationRepository.addAllStationsCalls)
    }
}
