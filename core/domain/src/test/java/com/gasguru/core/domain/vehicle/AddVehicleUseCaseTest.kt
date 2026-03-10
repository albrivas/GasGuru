package com.gasguru.core.domain.vehicle

import com.gasguru.core.model.data.FuelType
import com.gasguru.core.model.data.Vehicle
import com.gasguru.core.model.data.VehicleType
import com.gasguru.core.testing.CoroutinesTestExtension
import com.gasguru.core.testing.fakes.data.vehicle.FakeVehicleRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(CoroutinesTestExtension::class)
class AddVehicleUseCaseTest {

    private lateinit var sut: AddVehicleUseCase
    private lateinit var fakeVehicleRepository: FakeVehicleRepository

    @BeforeEach
    fun setUp() {
        fakeVehicleRepository = FakeVehicleRepository()
        sut = AddVehicleUseCase(vehicleRepository = fakeVehicleRepository)
    }

    @Test
    @DisplayName(
        "GIVEN a valid vehicle WHEN invoke is called THEN the vehicle is persisted in the repository"
    )
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
    @DisplayName(
        "GIVEN two vehicles added WHEN invoke is called twice THEN both vehicles are persisted"
    )
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
    @DisplayName(
        "GIVEN a vehicle with null name WHEN invoke is called THEN the vehicle is saved with null name"
    )
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
        assertEquals(null, savedVehicles.first().name)
    }
}
