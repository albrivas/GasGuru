package com.gasguru.core.domain.vehicle

import com.gasguru.core.domain.fakes.FakeVehicleRepository
import com.gasguru.core.model.data.FuelType
import com.gasguru.core.model.data.Vehicle
import com.gasguru.core.model.data.VehicleType
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class UpdateVehicleFuelTypeUseCaseTest {

    private lateinit var sut: UpdateVehicleFuelTypeUseCase
    private lateinit var fakeVehicleRepository: FakeVehicleRepository

    @BeforeTest
    fun setUp() {
        fakeVehicleRepository = FakeVehicleRepository(
            initialVehicles = listOf(
                Vehicle(
                    id = 1L,
                    userId = 0L,
                    name = "Golf VII",
                    fuelType = FuelType.GASOLINE_95,
                    tankCapacity = 55,
                    vehicleType = VehicleType.CAR,
                    isPrincipal = true,
                ),
            ),
        )
        sut = UpdateVehicleFuelTypeUseCase(vehicleRepository = fakeVehicleRepository)
    }

    @Test
    fun updatesFuelTypeForVehicle() = runTest {
        sut(vehicleId = 1L, fuelType = FuelType.DIESEL)

        assertEquals(1, fakeVehicleRepository.updatedFuelTypes.size)
        val (vehicleId, fuelType) = fakeVehicleRepository.updatedFuelTypes.first()
        assertEquals(1L, vehicleId)
        assertEquals(FuelType.DIESEL, fuelType)
    }

    @Test
    fun updatingMultipleTimesTracksAll() = runTest {
        sut(vehicleId = 1L, fuelType = FuelType.DIESEL)
        sut(vehicleId = 1L, fuelType = FuelType.GASOLINE_98)

        assertEquals(2, fakeVehicleRepository.updatedFuelTypes.size)
        assertEquals(FuelType.GASOLINE_98, fakeVehicleRepository.updatedFuelTypes.last().second)
    }
}
