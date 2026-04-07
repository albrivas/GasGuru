package com.gasguru.core.database.dao

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.gasguru.core.database.GasGuruDatabase
import com.gasguru.core.database.model.RecentSearchQueryEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class RecentSearchQueryDaoTest {

    private lateinit var recentSearchQueryDao: RecentSearchQueryDao
    private lateinit var db: GasGuruDatabase

    @BeforeEach
    fun createDb() {
        db = Room.inMemoryDatabaseBuilder<GasGuruDatabase>()
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
        recentSearchQueryDao = db.recentDao()
    }

    @AfterEach
    fun closeDb() = db.close()

    @Test
    @DisplayName(
        """
        GIVEN multiple queries in database
        WHEN querying with a limit
        THEN returns only up to limit queries ordered by recency
        """
    )
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
    @DisplayName(
        """
        GIVEN a valid recent search query
        WHEN inserting it
        THEN it is persisted and retrievable
        """
    )
    fun insertRecentSearchQueryTest() = runTest {
        val query1 = RecentSearchQueryEntity(id = "1", name = "Query 1")
        recentSearchQueryDao.insertOrReplaceRecentSearchQuery(query1)

        val result = recentSearchQueryDao.getRecentSearchQueryEntities(limit = 5).first()
        assertEquals(1, result.size)
        assertEquals(query1, result[0])
    }

    @Test
    @DisplayName(
        """
        GIVEN a query with the same id already exists
        WHEN inserting again with same id
        THEN the existing record is replaced
        """
    )
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
    @DisplayName(
        """
        GIVEN queries exist in database
        WHEN clearing all queries
        THEN database is empty
        """
    )
    fun clearRecentSearchQueriesTest() = runTest {
        val query1 = RecentSearchQueryEntity(id = "1", name = "Query 1")
        recentSearchQueryDao.insertOrReplaceRecentSearchQuery(query1)

        recentSearchQueryDao.clearRecentSearchQueries()

        val result = recentSearchQueryDao.getRecentSearchQueryEntities(limit = 5).first()
        assertTrue(result.isEmpty())
    }

    @Test
    @DisplayName(
        """GIVEN queries in db
        WHEN limit is larger than total count
        THEN all queries are returned"""
    )
    fun getRecentSearchQueryEntities_limitLargerThanCount_returnsAll() = runTest {
        val query1 = RecentSearchQueryEntity(id = "1", name = "Query 1")
        val query2 = RecentSearchQueryEntity(id = "2", name = "Query 2")
        recentSearchQueryDao.insertOrReplaceRecentSearchQuery(query1)
        recentSearchQueryDao.insertOrReplaceRecentSearchQuery(query2)

        val result = recentSearchQueryDao.getRecentSearchQueryEntities(limit = 100).first()

        assertEquals(2, result.size)
    }

    @Test
    @DisplayName(
        """GIVEN an empty database
        WHEN querying with any limit
        THEN returns empty list"""
    )
    fun getRecentSearchQueryEntities_withEmptyDatabase_returnsEmptyList() = runTest {
        val result = recentSearchQueryDao.getRecentSearchQueryEntities(limit = 10).first()

        assertTrue(result.isEmpty())
    }

    @Test
    @DisplayName(
        """GIVEN multiple queries
        WHEN clearing and re-inserting one
        THEN only the new query is present"""
    )
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
