package com.gasguru.core.domain.vehicle

import com.gasguru.core.model.data.FuelType
import com.gasguru.core.model.data.Vehicle
import com.gasguru.core.model.data.VehicleType
import com.gasguru.core.testing.CoroutinesTestExtension
import com.gasguru.core.testing.fakes.data.vehicle.FakeVehicleRepository
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(CoroutinesTestExtension::class)
class GetVehicleByIdUseCaseTest {

    private lateinit var sut: GetVehicleByIdUseCase
    private lateinit var fakeVehicleRepository: FakeVehicleRepository

    private val testVehicle = Vehicle(
        id = 1L,
        userId = 0L,
        name = "Golf VII",
        fuelType = FuelType.GASOLINE_95,
        tankCapacity = 55,
        vehicleType = VehicleType.CAR,
        isPrincipal = false,
    )

    @BeforeEach
    fun setUp() {
        fakeVehicleRepository = FakeVehicleRepository(initialVehicles = listOf(testVehicle))
        sut = GetVehicleByIdUseCase(vehicleRepository = fakeVehicleRepository)
    }

    @Test
    @DisplayName("GIVEN a vehicle exists WHEN invoke with its id THEN returns the vehicle")
    fun returnsVehicleWhenFound() = runTest {
        val result = sut(vehicleId = 1L)
        assertEquals(testVehicle, result)
    }

    @Test
    @DisplayName("GIVEN no vehicle with that id WHEN invoke THEN returns null")
    fun returnsNullWhenNotFound() = runTest {
        val result = sut(vehicleId = 99L)
        assertNull(result)
    }
}
