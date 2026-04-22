package com.gasguru.core.domain.vehicle

import com.gasguru.core.domain.fakes.FakeUserDataRepository
import com.gasguru.core.domain.fakes.FakeVehicleRepository
import com.gasguru.core.model.data.FuelType
import com.gasguru.core.model.data.UserData
import com.gasguru.core.model.data.Vehicle
import com.gasguru.core.model.data.VehicleType
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SaveDefaultVehicleCapacityUseCaseTest {

    private lateinit var sut: SaveDefaultVehicleCapacityUseCase
    private lateinit var fakeVehicleRepository: FakeVehicleRepository
    private lateinit var fakeUserDataRepository: FakeUserDataRepository

    private val principalVehicle = Vehicle(
        id = 5L,
        userId = 0L,
        name = "Golf VII",
        fuelType = FuelType.GASOLINE_95,
        tankCapacity = 55,
        vehicleType = VehicleType.CAR,
        isPrincipal = true,
    )

    @BeforeTest
    fun setUp() {
        fakeVehicleRepository = FakeVehicleRepository(initialVehicles = listOf(principalVehicle))
        fakeUserDataRepository = FakeUserDataRepository(
            initialUserData = UserData(vehicles = listOf(principalVehicle)),
        )
        sut = SaveDefaultVehicleCapacityUseCase(
            vehicleRepository = fakeVehicleRepository,
            userDataRepository = fakeUserDataRepository,
        )
    }

    @Test
    fun updatesTankCapacityOnPrincipalVehicle() = runTest {
        sut(tankCapacity = 70)

        assertEquals(1, fakeVehicleRepository.updatedTankCapacities.size)
        val (vehicleId, tankCapacity) = fakeVehicleRepository.updatedTankCapacities.first()
        assertEquals(principalVehicle.id, vehicleId)
        assertEquals(70, tankCapacity)
    }

    @Test
    fun marksOnboardingAsComplete() = runTest {
        sut(tankCapacity = 70)

        assertEquals(1, fakeUserDataRepository.onboardingCompleteCalls)
    }

    @Test
    fun completesOnboardingAfterUpdatingCapacity() = runTest {
        sut(tankCapacity = 60)

        assertTrue(fakeVehicleRepository.updatedTankCapacities.isNotEmpty())
        assertTrue(fakeUserDataRepository.onboardingCompleteCalls > 0)
    }
}
