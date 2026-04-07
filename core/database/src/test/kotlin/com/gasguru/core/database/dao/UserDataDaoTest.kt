package com.gasguru.core.database.dao

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.gasguru.core.database.GasGuruDatabase
import com.gasguru.core.database.model.UserDataEntity
import com.gasguru.core.model.data.ThemeMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class UserDataDaoTest {

    private lateinit var userDataDao: UserDataDao
    private lateinit var db: GasGuruDatabase

    @BeforeEach
    fun createDb() {
        db = Room.inMemoryDatabaseBuilder<GasGuruDatabase>()
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
        userDataDao = db.userDataDao()
    }

    @AfterEach
    fun closeDb() = db.close()

    @Test
    @DisplayName(
        """
        GIVEN user data exists in database
        WHEN querying getUserData
        THEN returns the stored user data
        """
    )
    fun getUserDataTest() = runTest {
        val userData = UserDataEntity(id = 1, lastUpdate = 0, isOnboardingSuccess = true)
        userDataDao.insertUserData(userData)
        val retrievedUserData = userDataDao.getUserData().first()
        assertEquals(userData, retrievedUserData)
    }

    @Test
    @DisplayName(
        """
        GIVEN valid user data
        WHEN inserting into database
        THEN data is persisted and retrievable
        """
    )
    fun insertUserDataTest() = runTest {
        val userData = UserDataEntity(id = 1, lastUpdate = 0, isOnboardingSuccess = true)
        userDataDao.insertUserData(userData)
        val retrievedUserData = userDataDao.getUserData().first()
        assertEquals(userData, retrievedUserData)
    }

    @Test
    @DisplayName(
        """GIVEN empty database
        WHEN querying getUserData
        THEN returns null"""
    )
    fun getUserData_withEmptyDatabase_returnsNull() = runTest {
        val result = userDataDao.getUserData().first()

        assertNull(result)
    }

    @Test
    @DisplayName(
        """GIVEN existing user data
        WHEN updating it
        THEN the updated values are persisted"""
    )
    fun updateUserData_persistsChanges() = runTest {
        val original = UserDataEntity(
            id = 0L,
            lastUpdate = 1000L,
            isOnboardingSuccess = false,
            themeModeId = ThemeMode.SYSTEM.id,
        )
        userDataDao.insertUserData(original)

        val updated = original.copy(
            isOnboardingSuccess = true,
            themeModeId = ThemeMode.DARK.id,
        )
        userDataDao.updateUserData(updated)

        val result = userDataDao.getUserData().first()
        assertEquals(true, result?.isOnboardingSuccess)
        assertEquals(ThemeMode.DARK.id, result?.themeModeId)
    }

    @Test
    @DisplayName(
        """GIVEN an existing user data record
        WHEN inserting a second record with the same id (IGNORE conflict)
        THEN only the first record is preserved"""
    )
    fun insertUserData_duplicateId_isIgnored() = runTest {
        val first = UserDataEntity(id = 0L, lastUpdate = 0L, isOnboardingSuccess = false)
        val duplicate = UserDataEntity(id = 0L, lastUpdate = 999L, isOnboardingSuccess = true)

        userDataDao.insertUserData(first)
        userDataDao.insertUserData(duplicate)

        val result = userDataDao.getUserData().first()
        assertEquals(0L, result?.lastUpdate)
        assertEquals(false, result?.isOnboardingSuccess)
    }

    @Test
    @DisplayName(
        """GIVEN user data with default theme mode
        WHEN retrieving it
        THEN themeModeId matches SYSTEM"""
    )
    fun insertUserData_defaultThemeModeIsSystem() = runTest {
        val userData = UserDataEntity(id = 0L, lastUpdate = 0L, isOnboardingSuccess = false)
        userDataDao.insertUserData(userData)

        val result = userDataDao.getUserData().first()
        assertEquals(ThemeMode.SYSTEM.id, result?.themeModeId)
    }
}
