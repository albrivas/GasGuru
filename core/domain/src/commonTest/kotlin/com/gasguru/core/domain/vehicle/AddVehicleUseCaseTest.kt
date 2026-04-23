package com.gasguru.core.domain.vehicle

import com.gasguru.core.domain.fakes.FakeVehicleRepository
import com.gasguru.core.model.data.FuelType
import com.gasguru.core.model.data.Vehicle
import com.gasguru.core.model.data.VehicleType
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class AddVehicleUseCaseTest {

    private lateinit var sut: AddVehicleUseCase
    private lateinit var fakeVehicleRepository: FakeVehicleRepository

    @BeforeTest
    fun setUp() {
        fakeVehicleRepository = FakeVehicleRepository()
        sut = AddVehicleUseCase(vehicleRepository = fakeVehicleRepository)
    }

    @Test
    fun addVehiclePersistsVehicle() = runTest {
        val newVehicle = Vehicle(
            userId = 0L,
            name = "Golf VII",
            fuelType = FuelType.GASOLINE_95,
            tankCapacity = 55,
            vehicleType = VehicleType.CAR,
            isPrincipal = true,
        )

        sut(vehicle = newVehicle)

        val savedVehicles = fakeVehicleRepository.getVehiclesForUser(userId = 0L).first()
        assertEquals(1, savedVehicles.size)
        assertEquals(newVehicle, savedVehicles.first())
    }

    @Test
    fun addMultipleVehiclesPersistsAll() = runTest {
        val firstVehicle = Vehicle(
            id = 1L,
            userId = 0L,
            name = "Golf VII",
            fuelType = FuelType.GASOLINE_95,
            tankCapacity = 55,
            vehicleType = VehicleType.CAR,
            isPrincipal = true,
        )
        val secondVehicle = Vehicle(
            id = 2L,
            userId = 0L,
            name = "Honda CB500",
            fuelType = FuelType.GASOLINE_98,
            tankCapacity = 17,
            vehicleType = VehicleType.MOTORCYCLE,
            isPrincipal = false,
        )

        sut(vehicle = firstVehicle)
        sut(vehicle = secondVehicle)

        val savedVehicles = fakeVehicleRepository.getVehiclesForUser(userId = 0L).first()
        assertEquals(2, savedVehicles.size)
    }

    @Test
    fun addVehicleWithNullNamePersistsCorrectly() = runTest {
        val newVehicle = Vehicle(
            userId = 0L,
            name = null,
            fuelType = FuelType.DIESEL,
            tankCapacity = 40,
            vehicleType = VehicleType.VAN,
            isPrincipal = false,
        )

        sut(vehicle = newVehicle)

        val savedVehicles = fakeVehicleRepository.getVehiclesForUser(userId = 0L).first()
        assertNull(savedVehicles.first().name)
    }
}
