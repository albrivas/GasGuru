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

@DisplayName(
    """
    GIVEN a UserDataDao
    WHEN performing user data operations
    THEN the results are correct
    """
)
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
        GIVEN user data is inserted
        WHEN getting user data
        THEN returns the inserted entity
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
        GIVEN user data is inserted
        WHEN inserting and then retrieving
        THEN returns the same entity
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
        """
        GIVEN empty database
        WHEN getting user data
        THEN returns null
        """
    )
    fun getUserData_withEmptyDatabase_returnsNull() = runTest {
        val result = userDataDao.getUserData().first()

        assertNull(result)
    }

    @Test
    @DisplayName(
        """
        GIVEN user data is inserted
        WHEN updating it with new values
        THEN persists the updated values
        """
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
        """
        GIVEN user data is inserted with id 0
        WHEN inserting another entity with the same id
        THEN the duplicate is ignored and original persists
        """
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
        """
        GIVEN user data is inserted without explicit theme mode
        WHEN getting user data
        THEN theme mode defaults to SYSTEM
        """
    )
    fun insertUserData_defaultThemeModeIsSystem() = runTest {
        val userData = UserDataEntity(id = 0L, lastUpdate = 0L, isOnboardingSuccess = false)
        userDataDao.insertUserData(userData)

        val result = userDataDao.getUserData().first()
        assertEquals(ThemeMode.SYSTEM.id, result?.themeModeId)
    }
}
