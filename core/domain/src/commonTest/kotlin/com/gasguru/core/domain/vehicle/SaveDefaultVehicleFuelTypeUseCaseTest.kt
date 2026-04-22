package com.gasguru.core.domain.vehicle

import com.gasguru.core.domain.fakes.FakeVehicleRepository
import com.gasguru.core.model.data.FuelType
import com.gasguru.core.model.data.VehicleType
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class SaveDefaultVehicleFuelTypeUseCaseTest {

    private lateinit var sut: SaveDefaultVehicleFuelTypeUseCase
    private lateinit var fakeVehicleRepository: FakeVehicleRepository

    @BeforeTest
    fun setUp() {
        fakeVehicleRepository = FakeVehicleRepository()
        sut = SaveDefaultVehicleFuelTypeUseCase(vehicleRepository = fakeVehicleRepository)
    }

    @Test
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
    fun saveDefaultVehicleWithGasoline95() = runTest {
        sut(fuelType = FuelType.GASOLINE_95)

        val savedVehicles = fakeVehicleRepository.getVehiclesForUser(userId = 0L).first()
        assertEquals(FuelType.GASOLINE_95, savedVehicles.first().fuelType)
    }
}
