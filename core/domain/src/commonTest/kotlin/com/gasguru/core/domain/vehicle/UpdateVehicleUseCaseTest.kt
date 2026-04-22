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

class UpdateVehicleUseCaseTest {

    private lateinit var sut: UpdateVehicleUseCase
    private lateinit var fakeVehicleRepository: FakeVehicleRepository

    private val existingVehicle = Vehicle(
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
        fakeVehicleRepository = FakeVehicleRepository(initialVehicles = listOf(existingVehicle))
        sut = UpdateVehicleUseCase(vehicleRepository = fakeVehicleRepository)
    }

    @Test
    fun updateVehicleReplacesAllFields() = runTest {
        val updatedVehicle = existingVehicle.copy(
            name = "Golf VIII",
            fuelType = FuelType.GASOLINE_98,
            tankCapacity = 60,
            vehicleType = VehicleType.CAR,
            isPrincipal = true,
        )

        sut(vehicle = updatedVehicle)

        val savedVehicles = fakeVehicleRepository.getVehiclesForUser(userId = 0L).first()
        assertEquals(1, savedVehicles.size)
        val savedVehicle = savedVehicles.first()
        assertEquals("Golf VIII", savedVehicle.name)
        assertEquals(FuelType.GASOLINE_98, savedVehicle.fuelType)
        assertEquals(60, savedVehicle.tankCapacity)
        assertEquals(true, savedVehicle.isPrincipal)
    }

    @Test
    fun updateVehicleOnlyFuelType() = runTest {
        val updatedVehicle = existingVehicle.copy(fuelType = FuelType.DIESEL)

        sut(vehicle = updatedVehicle)

        val savedVehicles = fakeVehicleRepository.getVehiclesForUser(userId = 0L).first()
        val savedVehicle = savedVehicles.first()
        assertEquals(FuelType.DIESEL, savedVehicle.fuelType)
        assertEquals(existingVehicle.name, savedVehicle.name)
        assertEquals(existingVehicle.tankCapacity, savedVehicle.tankCapacity)
    }

    @Test
    fun updateVehicleNameToNull() = runTest {
        val updatedVehicle = existingVehicle.copy(name = null)

        sut(vehicle = updatedVehicle)

        val savedVehicles = fakeVehicleRepository.getVehiclesForUser(userId = 0L).first()
        assertEquals(null, savedVehicles.first().name)
    }
}
