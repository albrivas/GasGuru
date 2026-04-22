package com.gasguru.core.domain.vehicle

import app.cash.turbine.test
import com.gasguru.core.domain.fakes.FakeVehicleRepository
import com.gasguru.core.model.data.FuelType
import com.gasguru.core.model.data.Vehicle
import com.gasguru.core.model.data.VehicleType
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GetVehiclesUseCaseTest {

    private lateinit var sut: GetVehiclesUseCase
    private lateinit var fakeVehicleRepository: FakeVehicleRepository

    @BeforeTest
    fun setUp() {
        fakeVehicleRepository = FakeVehicleRepository()
        sut = GetVehiclesUseCase(vehicleRepository = fakeVehicleRepository)
    }

    @Test
    fun returnsEmptyListWhenNoVehicles() = runTest {
        sut(userId = 0L).test {
            assertTrue(awaitItem().isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun returnsVehiclesForUser() = runTest {
        val vehicle = Vehicle(
            id = 1L,
            userId = 0L,
            name = "Golf VII",
            fuelType = FuelType.GASOLINE_95,
            tankCapacity = 55,
            vehicleType = VehicleType.CAR,
            isPrincipal = true,
        )
        val repository = FakeVehicleRepository(initialVehicles = listOf(vehicle))
        val sutWithVehicle = GetVehiclesUseCase(vehicleRepository = repository)

        sutWithVehicle(userId = 0L).test {
            val result = awaitItem()
            assertEquals(1, result.size)
            assertEquals(vehicle, result.first())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun doesNotReturnVehiclesFromOtherUsers() = runTest {
        val myVehicle = Vehicle(
            id = 1L,
            userId = 0L,
            name = "Golf VII",
            fuelType = FuelType.GASOLINE_95,
            tankCapacity = 55,
            vehicleType = VehicleType.CAR,
            isPrincipal = true,
        )
        val otherUserVehicle = Vehicle(
            id = 2L,
            userId = 99L,
            name = "Fiat 500",
            fuelType = FuelType.DIESEL,
            tankCapacity = 35,
            vehicleType = VehicleType.CAR,
            isPrincipal = true,
        )
        val repository = FakeVehicleRepository(initialVehicles = listOf(myVehicle, otherUserVehicle))
        val sutWithVehicles = GetVehiclesUseCase(vehicleRepository = repository)

        sutWithVehicles(userId = 0L).test {
            val result = awaitItem()
            assertEquals(1, result.size)
            assertEquals(0L, result.first().userId)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
