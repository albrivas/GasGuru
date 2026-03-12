package com.gasguru.core.domain.vehicle

import com.gasguru.core.model.data.FuelType
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
class SaveDefaultVehicleFuelTypeUseCaseTest {

    private lateinit var sut: SaveDefaultVehicleFuelTypeUseCase
    private lateinit var fakeVehicleRepository: FakeVehicleRepository

    @BeforeEach
    fun setUp() {
        fakeVehicleRepository = FakeVehicleRepository()
        sut = SaveDefaultVehicleFuelTypeUseCase(vehicleRepository = fakeVehicleRepository)
    }

    @Test
    @DisplayName(
        "GIVEN a fuel type WHEN invoke is called THEN a default vehicle is saved with that fuel type"
    )
    fun saveDefaultVehiclePersistsWithGivenFuelType() = runTest {
        sut(fuelType = FuelType.DIESEL)

        val savedVehicles = fakeVehicleRepository.getVehiclesForUser(userId = 0L).first()
        assertEquals(1, savedVehicles.size)
        val savedVehicle = savedVehicles.first()
        assertEquals(FuelType.DIESEL, savedVehicle.fuelType)
        assertEquals(40, savedVehicle.tankCapacity)
        assertEquals(VehicleType.CAR, savedVehicle.vehicleType)
        assertEquals(true, savedVehicle.isPrincipal)
        assertEquals(null, savedVehicle.name)
    }

    @Test
    @DisplayName(
        "GIVEN GASOLINE_95 fuel type WHEN invoke is called THEN vehicle is saved with GASOLINE_95"
    )
    fun saveDefaultVehicleWithGasoline95() = runTest {
        sut(fuelType = FuelType.GASOLINE_95)

        val savedVehicles = fakeVehicleRepository.getVehiclesForUser(userId = 0L).first()
        assertEquals(FuelType.GASOLINE_95, savedVehicles.first().fuelType)
    }
}
