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
import kotlin.test.assertTrue

class DeleteVehicleUseCaseTest {

    private lateinit var sut: DeleteVehicleUseCase
    private lateinit var fakeVehicleRepository: FakeVehicleRepository

    private val existingVehicle = Vehicle(
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
        fakeVehicleRepository = FakeVehicleRepository(initialVehicles = listOf(existingVehicle))
        sut = DeleteVehicleUseCase(vehicleRepository = fakeVehicleRepository)
    }

    @Test
    fun deletesVehicleByVehicleId() = runTest {
        sut(vehicleId = 1L)

        val remaining = fakeVehicleRepository.getVehiclesForUser(userId = 0L).first()
        assertTrue(remaining.isEmpty())
    }

    @Test
    fun tracksDeletedVehicleId() = runTest {
        sut(vehicleId = 1L)

        assertEquals(listOf(1L), fakeVehicleRepository.deletedVehicleIds)
    }

    @Test
    fun deletingNonExistentVehicleDoesNotThrow() = runTest {
        sut(vehicleId = 99L)

        val remaining = fakeVehicleRepository.getVehiclesForUser(userId = 0L).first()
        assertEquals(1, remaining.size)
    }
}
