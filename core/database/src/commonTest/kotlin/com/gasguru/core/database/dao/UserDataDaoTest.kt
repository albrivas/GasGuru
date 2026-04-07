package com.gasguru.core.database.dao

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.gasguru.core.database.GasGuruDatabase
import com.gasguru.core.database.model.UserDataEntity
import com.gasguru.core.model.data.ThemeMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class UserDataDaoTest {

    private lateinit var userDataDao: UserDataDao
    private lateinit var db: GasGuruDatabase

    @BeforeTest
    fun createDb() {
        db = Room.databaseBuilder<GasGuruDatabase>(name = ":memory:")
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
        userDataDao = db.userDataDao()
    }

    @AfterTest
    fun closeDb() = db.close()

    @Test
    fun getUserDataTest() = runTest {
        val userData = UserDataEntity(id = 1, lastUpdate = 0, isOnboardingSuccess = true)
        userDataDao.insertUserData(userData)
        val retrievedUserData = userDataDao.getUserData().first()
        assertEquals(userData, retrievedUserData)
    }

    @Test
    fun insertUserDataTest() = runTest {
        val userData = UserDataEntity(id = 1, lastUpdate = 0, isOnboardingSuccess = true)
        userDataDao.insertUserData(userData)
        val retrievedUserData = userDataDao.getUserData().first()
        assertEquals(userData, retrievedUserData)
    }

    @Test
    fun getUserData_withEmptyDatabase_returnsNull() = runTest {
        val result = userDataDao.getUserData().first()

        assertNull(result)
    }

    @Test
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
    fun insertUserData_defaultThemeModeIsSystem() = runTest {
        val userData = UserDataEntity(id = 0L, lastUpdate = 0L, isOnboardingSuccess = false)
        userDataDao.insertUserData(userData)

        val result = userDataDao.getUserData().first()
        assertEquals(ThemeMode.SYSTEM.id, result?.themeModeId)
    }
}
