package com.gasguru.core.domain.vehicle

import com.gasguru.core.domain.fakes.FakeVehicleRepository
import com.gasguru.core.model.data.FuelType
import com.gasguru.core.model.data.Vehicle
import com.gasguru.core.model.data.VehicleType
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class GetVehicleByIdUseCaseTest {

    private lateinit var sut: GetVehicleByIdUseCase
    private lateinit var fakeVehicleRepository: FakeVehicleRepository

    private val testVehicle = Vehicle(
        id = 1L,
        userId = 0L,
        name = "Golf VII",
        fuelType = FuelType.GASOLINE_95,
        tankCapacity = 55,
        vehicleType = VehicleType.CAR,
        isPrincipal = false,
    )

    @BeforeTest
    fun setUp() {
        fakeVehicleRepository = FakeVehicleRepository(initialVehicles = listOf(testVehicle))
        sut = GetVehicleByIdUseCase(vehicleRepository = fakeVehicleRepository)
    }

    @Test
    fun returnsVehicleWhenFound() = runTest {
        val result = sut(vehicleId = 1L)
        assertEquals(testVehicle, result)
    }

    @Test
    fun returnsNullWhenNotFound() = runTest {
        val result = sut(vehicleId = 99L)
        assertNull(result)
    }
}
