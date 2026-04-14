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

@DisplayName(
    """
    GIVEN a RecentSearchQueryDao
    WHEN performing recent search query operations
    THEN the results are correct
    """
)
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
        GIVEN three queries inserted
        WHEN getting recent queries with limit 2
        THEN returns the two most recent in descending order
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
        GIVEN one query inserted
        WHEN getting recent queries
        THEN returns that query
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
        GIVEN a query inserted then replaced with the same id
        WHEN getting recent queries
        THEN only the second query exists
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
        GIVEN a query is inserted
        WHEN clearing all recent queries
        THEN returns empty list
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
        """
        GIVEN two queries inserted
        WHEN getting with limit larger than count
        THEN returns all queries
        """
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
        """
        GIVEN empty database
        WHEN getting recent queries
        THEN returns empty list
        """
    )
    fun getRecentSearchQueryEntities_withEmptyDatabase_returnsEmptyList() = runTest {
        val result = recentSearchQueryDao.getRecentSearchQueryEntities(limit = 10).first()

        assertTrue(result.isEmpty())
    }

    @Test
    @DisplayName(
        """
        GIVEN two queries, then all are cleared, then a new query is inserted
        WHEN getting recent queries
        THEN only the new query is present
        """
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
