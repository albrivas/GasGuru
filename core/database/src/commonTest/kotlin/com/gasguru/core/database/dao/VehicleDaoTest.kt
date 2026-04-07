package com.gasguru.core.database.dao

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.gasguru.core.database.GasGuruDatabase
import com.gasguru.core.database.model.UserDataEntity
import com.gasguru.core.database.model.VehicleEntity
import com.gasguru.core.model.data.FuelType
import com.gasguru.core.model.data.VehicleType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class VehicleDaoTest {

    private lateinit var vehicleDao: VehicleDao
    private lateinit var userDataDao: UserDataDao
    private lateinit var db: GasGuruDatabase

    @BeforeTest
    fun createDb() {
        db = Room.databaseBuilder<GasGuruDatabase>(name = ":memory:")
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
        vehicleDao = db.vehicleDao()
        userDataDao = db.userDataDao()
    }

    @AfterTest
    fun closeDb() = db.close()

    private suspend fun insertUser(): Long {
        userDataDao.insertUserData(
            UserDataEntity(
                id = 0L,
                lastUpdate = 0L,
                isOnboardingSuccess = false,
            )
        )
        return 0L
    }

    @Test
    fun upsertVehicle_insertsAndReturnsId() = runTest {
        val userId = insertUser()
        val vehicle = VehicleEntity(
            userId = userId,
            name = null,
            fuelType = FuelType.GASOLINE_95,
            tankCapacity = 50,
            vehicleType = VehicleType.CAR,
            isPrincipal = true,
        )

        val id = vehicleDao.upsertVehicle(vehicle = vehicle)

        val fetched = vehicleDao.getVehicleById(vehicleId = id)
        assertNotNull(fetched)
        assertEquals(50, fetched?.tankCapacity)
        assertNull(fetched?.name)
    }

    @Test
    fun updateTankCapacity_updatesCorrectly() = runTest {
        val userId = insertUser()
        val id = vehicleDao.upsertVehicle(
            vehicle = VehicleEntity(
                userId = userId,
                name = null,
                fuelType = FuelType.GASOLINE_95,
                tankCapacity = 40,
                vehicleType = VehicleType.CAR,
                isPrincipal = true,
            ),
        )

        vehicleDao.updateTankCapacity(vehicleId = id, tankCapacity = 70)

        val updated = vehicleDao.getVehicleById(vehicleId = id)
        assertEquals(70, updated?.tankCapacity)
    }

    @Test
    fun updateFuelType_updatesCorrectly() = runTest {
        val userId = insertUser()
        val id = vehicleDao.upsertVehicle(
            vehicle = VehicleEntity(
                userId = userId,
                name = null,
                fuelType = FuelType.GASOLINE_95,
                tankCapacity = 50,
                vehicleType = VehicleType.CAR,
                isPrincipal = true,
            ),
        )

        vehicleDao.updateFuelType(vehicleId = id, fuelType = FuelType.DIESEL)

        val updated = vehicleDao.getVehicleById(vehicleId = id)
        assertEquals(FuelType.DIESEL, updated?.fuelType)
    }

    @Test
    fun getVehiclesByUser_returnsVehiclesForUser() = runTest {
        val userId = insertUser()
        vehicleDao.upsertVehicle(
            vehicle = VehicleEntity(
                userId = userId,
                name = null,
                fuelType = FuelType.DIESEL,
                tankCapacity = 60,
                vehicleType = VehicleType.CAR,
                isPrincipal = true,
            ),
        )

        val vehicles = vehicleDao.getVehiclesByUser(userId = userId).first()

        assertEquals(1, vehicles.size)
        assertEquals(FuelType.DIESEL, vehicles[0].fuelType)
    }

    @Test
    fun getVehiclesByUser_withNoVehicles_returnsEmptyList() = runTest {
        val userId = insertUser()

        val vehicles = vehicleDao.getVehiclesByUser(userId = userId).first()

        assertTrue(vehicles.isEmpty())
    }

    @Test
    fun getVehicleById_nonExistentId_returnsNull() = runTest {
        val result = vehicleDao.getVehicleById(vehicleId = 9999L)

        assertNull(result)
    }

    @Test
    fun deleteVehicle_removesVehicleFromDatabase() = runTest {
        val userId = insertUser()
        val vehicleId = vehicleDao.upsertVehicle(
            vehicle = VehicleEntity(
                userId = userId,
                name = "My Car",
                fuelType = FuelType.GASOLINE_95,
                tankCapacity = 45,
                vehicleType = VehicleType.CAR,
                isPrincipal = true,
            ),
        )

        vehicleDao.deleteVehicle(vehicleId = vehicleId)

        val result = vehicleDao.getVehicleById(vehicleId = vehicleId)
        assertNull(result)
    }

    @Test
    fun deleteVehicle_nonExistentId_doesNothing() = runTest {
        val userId = insertUser()
        val vehicleId = vehicleDao.upsertVehicle(
            vehicle = VehicleEntity(
                userId = userId,
                name = null,
                fuelType = FuelType.DIESEL,
                tankCapacity = 50,
                vehicleType = VehicleType.CAR,
                isPrincipal = true,
            ),
        )

        vehicleDao.deleteVehicle(vehicleId = 9999L)

        val remaining = vehicleDao.getVehicleById(vehicleId = vehicleId)
        assertNotNull(remaining)
    }

    @Test
    fun clearPrincipalVehiclesForUser_setsAllIsPrincipalToFalse() = runTest {
        val userId = insertUser()
        val id1 = vehicleDao.upsertVehicle(
            vehicle = VehicleEntity(
                userId = userId,
                name = null,
                fuelType = FuelType.GASOLINE_95,
                tankCapacity = 50,
                vehicleType = VehicleType.CAR,
                isPrincipal = true,
            ),
        )
        val id2 = vehicleDao.upsertVehicle(
            vehicle = VehicleEntity(
                userId = userId,
                name = null,
                fuelType = FuelType.DIESEL,
                tankCapacity = 60,
                vehicleType = VehicleType.VAN,
                isPrincipal = true,
            ),
        )

        vehicleDao.clearPrincipalVehiclesForUser(userId = userId)

        val vehicle1 = vehicleDao.getVehicleById(vehicleId = id1)
        val vehicle2 = vehicleDao.getVehicleById(vehicleId = id2)
        assertEquals(false, vehicle1?.isPrincipal)
        assertEquals(false, vehicle2?.isPrincipal)
    }

    @Test
    fun upsertVehicle_withExistingId_replacesExistingVehicle() = runTest {
        val userId = insertUser()
        val vehicleId = vehicleDao.upsertVehicle(
            vehicle = VehicleEntity(
                userId = userId,
                name = "Original",
                fuelType = FuelType.GASOLINE_95,
                tankCapacity = 40,
                vehicleType = VehicleType.CAR,
                isPrincipal = false,
            ),
        )

        vehicleDao.upsertVehicle(
            vehicle = VehicleEntity(
                id = vehicleId,
                userId = userId,
                name = "Updated",
                fuelType = FuelType.DIESEL,
                tankCapacity = 60,
                vehicleType = VehicleType.TRUCK,
                isPrincipal = true,
            ),
        )

        val vehicles = vehicleDao.getVehiclesByUser(userId = userId).first()
        assertEquals(1, vehicles.size)
        assertEquals("Updated", vehicles.first().name)
        assertEquals(FuelType.DIESEL, vehicles.first().fuelType)
        assertEquals(60, vehicles.first().tankCapacity)
    }

    @Test
    fun clearPrincipalVehiclesForUser_withNoVehicles_doesNotThrow() = runTest {
        val userId = insertUser()

        vehicleDao.clearPrincipalVehiclesForUser(userId = userId)

        val vehicles = vehicleDao.getVehiclesByUser(userId = userId).first()
        assertTrue(vehicles.isEmpty())
    }

    @Test
    fun clearPrincipalVehiclesForUser_doesNotAffectOtherUsers() = runTest {
        // Insert first user
        userDataDao.insertUserData(
            UserDataEntity(id = 0L, lastUpdate = 0L, isOnboardingSuccess = false)
        )
        // Insert second user
        userDataDao.insertUserData(
            UserDataEntity(id = 1L, lastUpdate = 0L, isOnboardingSuccess = false)
        )

        val vehicleIdUser0 = vehicleDao.upsertVehicle(
            vehicle = VehicleEntity(
                userId = 0L,
                name = null,
                fuelType = FuelType.GASOLINE_95,
                tankCapacity = 50,
                vehicleType = VehicleType.CAR,
                isPrincipal = true,
            ),
        )
        val vehicleIdUser1 = vehicleDao.upsertVehicle(
            vehicle = VehicleEntity(
                userId = 1L,
                name = null,
                fuelType = FuelType.DIESEL,
                tankCapacity = 60,
                vehicleType = VehicleType.CAR,
                isPrincipal = true,
            ),
        )

        vehicleDao.clearPrincipalVehiclesForUser(userId = 0L)

        val vehicleForUser0 = vehicleDao.getVehicleById(vehicleId = vehicleIdUser0)
        val vehicleForUser1 = vehicleDao.getVehicleById(vehicleId = vehicleIdUser1)
        assertEquals(false, vehicleForUser0?.isPrincipal)
        assertEquals(true, vehicleForUser1?.isPrincipal)
    }

    @Test
    fun upsertVehicle_withName_persistsNameCorrectly() = runTest {
        val userId = insertUser()
        val vehicleId = vehicleDao.upsertVehicle(
            vehicle = VehicleEntity(
                userId = userId,
                name = "Family SUV",
                fuelType = FuelType.GASOLINE_95,
                tankCapacity = 70,
                vehicleType = VehicleType.CAR,
                isPrincipal = true,
            ),
        )

        val fetched = vehicleDao.getVehicleById(vehicleId = vehicleId)

        assertEquals("Family SUV", fetched?.name)
    }

    @Test
    fun updateTankCapacity_doesNotAffectOtherVehicles() = runTest {
        val userId = insertUser()
        val vehicleId1 = vehicleDao.upsertVehicle(
            vehicle = VehicleEntity(
                userId = userId,
                name = null,
                fuelType = FuelType.GASOLINE_95,
                tankCapacity = 40,
                vehicleType = VehicleType.CAR,
                isPrincipal = true,
            ),
        )
        val vehicleId2 = vehicleDao.upsertVehicle(
            vehicle = VehicleEntity(
                userId = userId,
                name = null,
                fuelType = FuelType.DIESEL,
                tankCapacity = 60,
                vehicleType = VehicleType.TRUCK,
                isPrincipal = false,
            ),
        )

        vehicleDao.updateTankCapacity(vehicleId = vehicleId1, tankCapacity = 90)

        val vehicle1 = vehicleDao.getVehicleById(vehicleId = vehicleId1)
        val vehicle2 = vehicleDao.getVehicleById(vehicleId = vehicleId2)
        assertEquals(90, vehicle1?.tankCapacity)
        assertEquals(60, vehicle2?.tankCapacity)
    }

    @Test
    fun updateTankCapacity_nonExistentVehicle_doesNotThrow() = runTest {
        vehicleDao.updateTankCapacity(vehicleId = 9999L, tankCapacity = 80)
    }

    @Test
    fun updateFuelType_nonExistentVehicle_doesNotThrow() = runTest {
        vehicleDao.updateFuelType(vehicleId = 9999L, fuelType = FuelType.DIESEL)
    }
}
