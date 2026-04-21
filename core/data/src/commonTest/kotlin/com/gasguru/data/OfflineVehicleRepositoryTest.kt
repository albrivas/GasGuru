package com.gasguru.data

import app.cash.turbine.test
import com.gasguru.core.data.repository.vehicle.OfflineVehicleRepository
import com.gasguru.core.database.model.VehicleEntity
import com.gasguru.core.database.model.asExternalModel
import com.gasguru.core.model.data.FuelType
import com.gasguru.core.model.data.VehicleType
import com.gasguru.data.fakes.FakeVehicleDao
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest

class OfflineVehicleRepositoryTest {

    private lateinit var fakeVehicleDao: FakeVehicleDao
    private lateinit var sut: OfflineVehicleRepository

    private val vehicleEntityA = VehicleEntity(
        id = 1L,
        userId = 10L,
        name = "Golf VII",
        fuelType = FuelType.GASOLINE_95,
        tankCapacity = 55,
        vehicleType = VehicleType.CAR,
        isPrincipal = true,
    )
    private val vehicleEntityB = VehicleEntity(
        id = 2L,
        userId = 10L,
        name = "Honda CB500",
        fuelType = FuelType.GASOLINE_95,
        tankCapacity = 18,
        vehicleType = VehicleType.MOTORCYCLE,
        isPrincipal = false,
    )

    @BeforeTest
    fun setUp() {
        fakeVehicleDao = FakeVehicleDao()
        sut = OfflineVehicleRepository(vehicleDao = fakeVehicleDao)
    }

    @Test
    fun getVehiclesForUser_withExistingVehicles_returnsMappedVehiclesForThatUserOnly() = runTest {
        fakeVehicleDao.upsertVehicle(vehicle = vehicleEntityA)
        fakeVehicleDao.upsertVehicle(vehicle = vehicleEntityB)
        fakeVehicleDao.upsertVehicle(vehicle = vehicleEntityA.copy(id = 3L, userId = 99L))

        sut.getVehiclesForUser(userId = 10L).test {
            val vehicles = awaitItem()
            assertEquals(2, vehicles.size)
            assertEquals(1L, vehicles[0].id)
            assertEquals("Golf VII", vehicles[0].name)
            assertEquals(2L, vehicles[1].id)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun upsertVehicle_withNoExistingVehicles_persistsAndReturnsByGetVehiclesForUser() = runTest {
        sut.upsertVehicle(vehicle = vehicleEntityA.asExternalModel())

        sut.getVehiclesForUser(userId = 10L).test {
            val vehicles = awaitItem()
            assertEquals(1, vehicles.size)
            assertEquals("Golf VII", vehicles.first().name)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun upsertVehicle_withSameId_updatesExistingVehicleNotDuplicates() = runTest {
        fakeVehicleDao.upsertVehicle(vehicle = vehicleEntityA)
        sut.upsertVehicle(vehicle = vehicleEntityA.copy(name = "Golf VIII").asExternalModel())

        sut.getVehiclesForUser(userId = 10L).test {
            val vehicles = awaitItem()
            assertEquals(1, vehicles.size)
            assertEquals("Golf VIII", vehicles.first().name)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun getVehicleById_withExistingId_returnsMappedVehicle() = runTest {
        fakeVehicleDao.upsertVehicle(vehicle = vehicleEntityA)

        val result = sut.getVehicleById(vehicleId = 1L)

        assertEquals(1L, result?.id)
        assertEquals("Golf VII", result?.name)
        assertEquals(FuelType.GASOLINE_95, result?.fuelType)
    }

    @Test
    fun getVehicleById_withNonExistingId_returnsNull() = runTest {
        val result = sut.getVehicleById(vehicleId = 999L)
        assertNull(result)
    }

    @Test
    fun clearPrincipalVehiclesForUser_withExistingVehicles_clearsAllPrincipals() = runTest {
        fakeVehicleDao.upsertVehicle(vehicle = vehicleEntityA)
        fakeVehicleDao.upsertVehicle(vehicle = vehicleEntityB.copy(isPrincipal = true))

        sut.clearPrincipalVehiclesForUser(userId = 10L)

        sut.getVehiclesForUser(userId = 10L).test {
            val vehicles = awaitItem()
            assertEquals(0, vehicles.count { it.isPrincipal })
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun clearPrincipalVehiclesForUser_withMultipleUsers_doesNotAffectOtherUsers() = runTest {
        fakeVehicleDao.upsertVehicle(vehicle = vehicleEntityA)
        fakeVehicleDao.upsertVehicle(vehicle = vehicleEntityA.copy(id = 3L, userId = 99L, isPrincipal = true))

        sut.clearPrincipalVehiclesForUser(userId = 10L)

        val otherUserVehicle = sut.getVehicleById(vehicleId = 3L)
        assertTrue(otherUserVehicle?.isPrincipal == true)
    }
}
