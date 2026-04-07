package com.gasguru.core.database.dao

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.gasguru.core.database.GasGuruDatabase
import com.gasguru.core.database.model.RecentSearchQueryEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class RecentSearchQueryDaoTest {

    private lateinit var recentSearchQueryDao: RecentSearchQueryDao
    private lateinit var db: GasGuruDatabase

    @BeforeTest
    fun createDb() {
        db = Room.databaseBuilder<GasGuruDatabase>(name = ":memory:")
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
        recentSearchQueryDao = db.recentDao()
    }

    @AfterTest
    fun closeDb() = db.close()

    @Test
    fun getRecentSearchQueryEntitiesTest() = runTest {
        val query1 = RecentSearchQueryEntity(id = "1", name = "Query 1")
        val query2 = RecentSearchQueryEntity(id = "2", name = "Query 2")
        val query3 = RecentSearchQueryEntity(id = "3", name = "Query 3")

        recentSearchQueryDao.insertOrReplaceRecentSearchQuery(query1)
        recentSearchQueryDao.insertOrReplaceRecentSearchQuery(query2)
        recentSearchQueryDao.insertOrReplaceRecentSearchQuery(query3)

        val result = recentSearchQueryDao.getRecentSearchQueryEntities(limit = 2).first()

        assertEquals(2, result.size)
        assertEquals(query3, result[0])
        assertEquals(query2, result[1])
    }

    @Test
    fun insertRecentSearchQueryTest() = runTest {
        val query1 = RecentSearchQueryEntity(id = "1", name = "Query 1")
        recentSearchQueryDao.insertOrReplaceRecentSearchQuery(query1)

        val result = recentSearchQueryDao.getRecentSearchQueryEntities(limit = 5).first()
        assertEquals(1, result.size)
        assertEquals(query1, result[0])
    }

    @Test
    fun replaceRecentSearchQueryTest() = runTest {
        val query1 = RecentSearchQueryEntity(id = "1", name = "Query 1")
        recentSearchQueryDao.insertOrReplaceRecentSearchQuery(query1)

        val query2 = RecentSearchQueryEntity(id = "1", name = "Query 2")
        recentSearchQueryDao.insertOrReplaceRecentSearchQuery(query2)

        val result = recentSearchQueryDao.getRecentSearchQueryEntities(limit = 5).first()
        assertEquals(1, result.size)
        assertEquals(query2, result[0])
        assertNotEquals(query1, result[0])
    }

    @Test
    fun clearRecentSearchQueriesTest() = runTest {
        val query1 = RecentSearchQueryEntity(id = "1", name = "Query 1")
        recentSearchQueryDao.insertOrReplaceRecentSearchQuery(query1)

        recentSearchQueryDao.clearRecentSearchQueries()

        val result = recentSearchQueryDao.getRecentSearchQueryEntities(limit = 5).first()
        assertTrue(result.isEmpty())
    }

    @Test
    fun getRecentSearchQueryEntities_limitLargerThanCount_returnsAll() = runTest {
        val query1 = RecentSearchQueryEntity(id = "1", name = "Query 1")
        val query2 = RecentSearchQueryEntity(id = "2", name = "Query 2")
        recentSearchQueryDao.insertOrReplaceRecentSearchQuery(query1)
        recentSearchQueryDao.insertOrReplaceRecentSearchQuery(query2)

        val result = recentSearchQueryDao.getRecentSearchQueryEntities(limit = 100).first()

        assertEquals(2, result.size)
    }

    @Test
    fun getRecentSearchQueryEntities_withEmptyDatabase_returnsEmptyList() = runTest {
        val result = recentSearchQueryDao.getRecentSearchQueryEntities(limit = 10).first()

        assertTrue(result.isEmpty())
    }

    @Test
    fun clearAndReInsert_onlyNewQueryIsPresent() = runTest {
        recentSearchQueryDao.insertOrReplaceRecentSearchQuery(
            RecentSearchQueryEntity(id = "1", name = "Old")
        )
        recentSearchQueryDao.insertOrReplaceRecentSearchQuery(
            RecentSearchQueryEntity(id = "2", name = "Older")
        )

        recentSearchQueryDao.clearRecentSearchQueries()

        recentSearchQueryDao.insertOrReplaceRecentSearchQuery(
            RecentSearchQueryEntity(id = "3", name = "New")
        )

        val result = recentSearchQueryDao.getRecentSearchQueryEntities(limit = 10).first()
        assertEquals(1, result.size)
        assertEquals("New", result.first().name)
    }
}
