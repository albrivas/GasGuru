package com.gasguru.core.database.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.gasguru.core.database.GasGuruDatabase
import com.gasguru.core.database.model.UserDataEntity
import com.gasguru.core.model.data.FuelType
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class UserDataDaoTest {

    private lateinit var userDataDao: UserDataDao
    private lateinit var db: GasGuruDatabase

    @BeforeEach
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context,
            GasGuruDatabase::class.java
        ).build()
        userDataDao = db.userDataDao()
    }

    @AfterEach
    fun closeDb() = db.close()

    @Test
    @DisplayName("Retrieves user ID")
    fun getUserIdTest() = runTest {
        val userData = UserDataEntity(id = 1, fuelSelection = FuelType.GASOLINE_95, lastUpdate = 0)
        userDataDao.insertUserData(userData)
        val userId = userDataDao.getUserId()
        assertEquals(1, userId)
    }

    @Test
    @DisplayName("Retrieves user data")
    fun getUserDataTest() = runTest {
        val userData = UserDataEntity(id = 1, fuelSelection = FuelType.GASOLINE_95, lastUpdate = 0)
        userDataDao.insertUserData(userData)
        val retrievedUserData = userDataDao.getUserData().first()
        assertEquals(userData, retrievedUserData)
    }

    @Test
    @DisplayName("Inserts user data")
    fun insertUserDataTest() = runTest {
        val userData = UserDataEntity(id = 1, fuelSelection = FuelType.GASOLINE_95, lastUpdate = 0)
        userDataDao.insertUserData(userData)
        val retrievedUserData = userDataDao.getUserData().first()
        assertEquals(userData, retrievedUserData)
    }

    @Test
    @DisplayName("Updates fuel selection")
    fun updateFuelSelectionTest() = runTest {
        val userData = UserDataEntity(id = 1, fuelSelection = FuelType.GASOLINE_95, lastUpdate = 0)
        userDataDao.insertUserData(userData)
        userDataDao.updateFuelSelection(FuelType.DIESEL.name)
        val updatedUserData = userDataDao.getUserData().first()
        assertEquals(FuelType.DIESEL, updatedUserData.fuelSelection)
    }

    @Test
    @DisplayName("Updates last update timestamp")
    fun updateLastUpdateTest() = runTest {
        val userData = UserDataEntity(id = 1, fuelSelection = FuelType.GASOLINE_95, lastUpdate = 0)
        userDataDao.insertUserData(userData)
        val newLastUpdate = System.currentTimeMillis()
        userDataDao.updateLastUpdate(newLastUpdate)
        val updatedUserData = userDataDao.getUserData().first()
        assertEquals(newLastUpdate, updatedUserData.lastUpdate)
    }
}