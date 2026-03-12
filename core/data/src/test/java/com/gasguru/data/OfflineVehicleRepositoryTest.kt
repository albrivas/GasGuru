package com.gasguru.data

import app.cash.turbine.test
import com.gasguru.core.data.repository.vehicle.OfflineVehicleRepository
import com.gasguru.core.database.model.VehicleEntity
import com.gasguru.core.database.model.asExternalModel
import com.gasguru.core.model.data.FuelType
import com.gasguru.core.model.data.VehicleType
import com.gasguru.core.testing.CoroutinesTestExtension
import com.gasguru.core.testing.fakes.data.database.FakeVehicleDao
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(CoroutinesTestExtension::class)
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

    @BeforeEach
    fun setUp() {
        fakeVehicleDao = FakeVehicleDao()
        sut = OfflineVehicleRepository(vehicleDao = fakeVehicleDao)
    }

    @Test
    @DisplayName(
        """
        GIVEN vehicles exist for a user
        WHEN getVehiclesForUser is called
        THEN returns mapped vehicles for that user only
        """
    )
    fun getVehiclesForUserReturnsMappedVehicles() = runTest {
        fakeVehicleDao.upsertVehicle(vehicle = vehicleEntityA)
        fakeVehicleDao.upsertVehicle(vehicle = vehicleEntityB)
        fakeVehicleDao.upsertVehicle(
            vehicle = vehicleEntityA.copy(id = 3L, userId = 99L),
        )

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
    @DisplayName(
        """
        GIVEN no vehicles exist
        WHEN upsertVehicle is called
        THEN vehicle is persisted and returned by getVehiclesForUser
        """
    )
    fun upsertVehiclePersistsNewVehicle() = runTest {
        sut.upsertVehicle(vehicle = vehicleEntityA.asExternalModel())

        sut.getVehiclesForUser(userId = 10L).test {
            val vehicles = awaitItem()
            assertEquals(1, vehicles.size)
            assertEquals("Golf VII", vehicles.first().name)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    @DisplayName(
        """
        GIVEN a vehicle exists
        WHEN upsertVehicle is called with the same id
        THEN vehicle is updated not duplicated
        """
    )
    fun upsertVehicleUpdatesExistingVehicle() = runTest {
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
    @DisplayName(
        """
        GIVEN a vehicle exists
        WHEN getVehicleById is called with its id
        THEN returns the mapped vehicle
        """
    )
    fun getVehicleByIdReturnsMappedVehicle() = runTest {
        fakeVehicleDao.upsertVehicle(vehicle = vehicleEntityA)

        val result = sut.getVehicleById(vehicleId = 1L)

        assertEquals(1L, result?.id)
        assertEquals("Golf VII", result?.name)
        assertEquals(FuelType.GASOLINE_95, result?.fuelType)
    }

    @Test
    @DisplayName(
        """
        GIVEN no vehicle exists with given id
        WHEN getVehicleById is called
        THEN returns null
        """
    )
    fun getVehicleByIdReturnsNullWhenNotFound() = runTest {
        val result = sut.getVehicleById(vehicleId = 999L)
        assertNull(result)
    }

    @Test
    @DisplayName(
        """
        GIVEN vehicles exist for a user
        WHEN clearPrincipalVehiclesForUser is called
        THEN all vehicles for that user have isPrincipal set to false
        """
    )
    fun clearPrincipalVehiclesForUserClearsAllPrincipals() = runTest {
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
    @DisplayName(
        """
        GIVEN vehicles exist for multiple users
        WHEN clearPrincipalVehiclesForUser is called for one user
        THEN only that user's vehicles are affected
        """
    )
    fun clearPrincipalVehiclesDoesNotAffectOtherUsers() = runTest {
        fakeVehicleDao.upsertVehicle(vehicle = vehicleEntityA) // userId = 10, isPrincipal = true
        fakeVehicleDao.upsertVehicle(
            vehicle = vehicleEntityA.copy(id = 3L, userId = 99L, isPrincipal = true),
        )

        sut.clearPrincipalVehiclesForUser(userId = 10L)

        val otherUserVehicle = sut.getVehicleById(vehicleId = 3L)
        assertEquals(true, otherUserVehicle?.isPrincipal)
    }
}
