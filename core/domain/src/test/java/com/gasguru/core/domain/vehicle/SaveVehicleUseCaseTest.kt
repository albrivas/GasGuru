package com.gasguru.core.domain.vehicle

import com.gasguru.core.model.data.FuelType
import com.gasguru.core.model.data.Vehicle
import com.gasguru.core.model.data.VehicleType
import com.gasguru.core.testing.CoroutinesTestExtension
import com.gasguru.core.testing.fakes.data.vehicle.FakeVehicleRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(CoroutinesTestExtension::class)
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

    @BeforeEach
    fun setUp() {
        fakeVehicleRepository = FakeVehicleRepository(initialVehicles = listOf(existingPrincipalVehicle))
        sut = SaveVehicleUseCase(vehicleRepository = fakeVehicleRepository)
    }

    @Test
    @DisplayName("GIVEN a non-principal vehicle WHEN invoke THEN saves without clearing others")
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
    @DisplayName(
        "GIVEN existing principal vehicle WHEN saving new vehicle with isPrincipal=true THEN existing vehicle loses principal flag"
    )
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
    @DisplayName(
        "GIVEN existing principal vehicle WHEN saving new vehicle with isPrincipal=true THEN new vehicle is saved with isPrincipal=true"
    )
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
