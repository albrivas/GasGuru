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
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SaveVehicleUseCaseTest {

    private lateinit var sut: SaveVehicleUseCase
    private lateinit var fakeVehicleRepository: FakeVehicleRepository

    private val existingPrincipalVehicle = Vehicle(
        id = 1L,
        userId = 0L,
        name = "Golf VII",
        fuelType = FuelType.GASOLINE_95,
        tankCapacity = 55,
        vehicleType = VehicleType.CAR,
        isPrincipal = true,
    )

    @BeforeTest
    fun setUp() {
        fakeVehicleRepository = FakeVehicleRepository(initialVehicles = listOf(existingPrincipalVehicle))
        sut = SaveVehicleUseCase(vehicleRepository = fakeVehicleRepository)
    }

    @Test
    fun savesNonPrincipalVehicleWithoutClearingOthers() = runTest {
        val newVehicle = Vehicle(
            id = 0L,
            userId = 0L,
            name = "Moto",
            fuelType = FuelType.GASOLINE_95,
            tankCapacity = 15,
            vehicleType = VehicleType.MOTORCYCLE,
            isPrincipal = false,
        )

        sut(vehicle = newVehicle)

        val vehicles = fakeVehicleRepository.getVehiclesForUser(userId = 0L).first()
        val principal = vehicles.first { it.id == 1L }
        assertTrue(principal.isPrincipal)
    }

    @Test
    fun newPrincipalVehicleClearsExistingPrincipal() = runTest {
        val newPrincipalVehicle = Vehicle(
            id = 0L,
            userId = 0L,
            name = "Moto",
            fuelType = FuelType.GASOLINE_95,
            tankCapacity = 15,
            vehicleType = VehicleType.MOTORCYCLE,
            isPrincipal = true,
        )

        sut(vehicle = newPrincipalVehicle)

        val vehicles = fakeVehicleRepository.getVehiclesForUser(userId = 0L).first()
        val previousPrincipal = vehicles.first { it.id == 1L }
        assertFalse(previousPrincipal.isPrincipal)
    }

    @Test
    fun newPrincipalVehicleIsSavedAsPrincipal() = runTest {
        val newPrincipalVehicle = Vehicle(
            id = 2L,
            userId = 0L,
            name = "Moto",
            fuelType = FuelType.GASOLINE_95,
            tankCapacity = 15,
            vehicleType = VehicleType.MOTORCYCLE,
            isPrincipal = true,
        )

        sut(vehicle = newPrincipalVehicle)

        val vehicles = fakeVehicleRepository.getVehiclesForUser(userId = 0L).first()
        val savedVehicle = vehicles.first { it.id == 2L }
        assertTrue(savedVehicle.isPrincipal)
        assertEquals(1, vehicles.count { it.isPrincipal })
    }
}
