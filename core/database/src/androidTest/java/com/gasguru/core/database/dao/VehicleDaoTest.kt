package com.gasguru.core.database.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.gasguru.core.database.GasGuruDatabase
import com.gasguru.core.database.model.UserDataEntity
import com.gasguru.core.database.model.VehicleEntity
import com.gasguru.core.model.data.FuelType
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class VehicleDaoTest {

    private lateinit var vehicleDao: VehicleDao
    private lateinit var userDataDao: UserDataDao
    private lateinit var db: GasGuruDatabase

    @BeforeEach
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context,
            GasGuruDatabase::class.java,
        ).build()
        vehicleDao = db.vehicleDao()
        userDataDao = db.userDataDao()
    }

    @AfterEach
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
    @DisplayName("GIVEN a new vehicle WHEN upserting THEN it is persisted and a valid id is returned")
    fun upsertVehicle_insertsAndReturnsId() = runTest {
        val userId = insertUser()
        val vehicle = VehicleEntity(
            userId = userId,
            name = null,
            fuelType = FuelType.GASOLINE_95,
            tankCapacity = 50,
        )

        val id = vehicleDao.upsertVehicle(vehicle = vehicle)

        val fetched = vehicleDao.getVehicleById(vehicleId = id)
        assertNotNull(fetched)
        assertEquals(50, fetched?.tankCapacity)
        assertNull(fetched?.name)
    }

    @Test
    @DisplayName("GIVEN an existing vehicle WHEN updating tank capacity THEN the new value is persisted")
    fun updateTankCapacity_updatesCorrectly() = runTest {
        val userId = insertUser()
        val id = vehicleDao.upsertVehicle(
            vehicle = VehicleEntity(
                userId = userId,
                name = null,
                fuelType = FuelType.GASOLINE_95,
                tankCapacity = 40,
            ),
        )

        vehicleDao.updateTankCapacity(vehicleId = id, tankCapacity = 70)

        val updated = vehicleDao.getVehicleById(vehicleId = id)
        assertEquals(70, updated?.tankCapacity)
    }

    @Test
    @DisplayName("GIVEN an existing vehicle WHEN updating fuel type THEN the new fuel type is persisted")
    fun updateFuelType_updatesCorrectly() = runTest {
        val userId = insertUser()
        val id = vehicleDao.upsertVehicle(
            vehicle = VehicleEntity(
                userId = userId,
                name = null,
                fuelType = FuelType.GASOLINE_95,
                tankCapacity = 50,
            ),
        )

        vehicleDao.updateFuelType(vehicleId = id, fuelType = FuelType.DIESEL)

        val updated = vehicleDao.getVehicleById(vehicleId = id)
        assertEquals(FuelType.DIESEL, updated?.fuelType)
    }

    @Test
    @DisplayName("GIVEN a user with one vehicle WHEN querying vehicles by user THEN only that vehicle is returned")
    fun getVehiclesByUser_returnsVehiclesForUser() = runTest {
        val userId = insertUser()
        vehicleDao.upsertVehicle(
            vehicle = VehicleEntity(
                userId = userId,
                name = null,
                fuelType = FuelType.DIESEL,
                tankCapacity = 60,
            ),
        )

        val vehicles = vehicleDao.getVehiclesByUser(userId = userId).first()

        assertEquals(1, vehicles.size)
        assertEquals(FuelType.DIESEL, vehicles[0].fuelType)
    }
}