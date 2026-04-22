package com.gasguru.core.domain.vehicle

import com.gasguru.core.domain.fakes.FakeVehicleRepository
import com.gasguru.core.model.data.FuelType
import com.gasguru.core.model.data.Vehicle
import com.gasguru.core.model.data.VehicleType
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class UpdateVehicleTankCapacityUseCaseTest {

    private lateinit var sut: UpdateVehicleTankCapacityUseCase
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
        sut = UpdateVehicleTankCapacityUseCase(vehicleRepository = fakeVehicleRepository)
    }

    @Test
    fun updatesTankCapacityForVehicle() = runTest {
        sut(vehicleId = 1L, tankCapacity = 70)

        assertEquals(1, fakeVehicleRepository.updatedTankCapacities.size)
        val (vehicleId, tankCapacity) = fakeVehicleRepository.updatedTankCapacities.first()
        assertEquals(1L, vehicleId)
        assertEquals(70, tankCapacity)
    }

    @Test
    fun updatingMultipleTimesTracksAll() = runTest {
        sut(vehicleId = 1L, tankCapacity = 60)
        sut(vehicleId = 1L, tankCapacity = 70)

        assertEquals(2, fakeVehicleRepository.updatedTankCapacities.size)
        assertEquals(70, fakeVehicleRepository.updatedTankCapacities.last().second)
    }
}
