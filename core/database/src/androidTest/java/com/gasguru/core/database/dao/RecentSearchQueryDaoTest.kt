package com.gasguru.core.database.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.gasguru.core.database.GasGuruDatabase
import com.gasguru.core.database.model.RecentSearchQueryEntity
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
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context,
            GasGuruDatabase::class.java
        ).allowMainThreadQueries().build()
        recentSearchQueryDao = db.recentDao()
    }

    @AfterEach
    fun closeDb() = db.close()

    @Test
    @DisplayName("Retrieves recent search queries with limit")
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
    @DisplayName("Inserts recent search query")
    fun insertRecentSearchQueryTest() = runTest {
        val query1 = RecentSearchQueryEntity(id = "1", name = "Query 1")
        recentSearchQueryDao.insertOrReplaceRecentSearchQuery(query1)

        val result = recentSearchQueryDao.getRecentSearchQueryEntities(limit = 5).first()
        assertEquals(1, result.size)
        assertEquals(query1, result[0])
    }

    @Test
    @DisplayName("Replaces recent search query")
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
    @DisplayName("Clears recent search queries")
    fun clearRecentSearchQueriesTest() = runTest {
        val query1 = RecentSearchQueryEntity(id = "1", name = "Query 1")
        recentSearchQueryDao.insertOrReplaceRecentSearchQuery(query1)

        recentSearchQueryDao.clearRecentSearchQueries()

        val result = recentSearchQueryDao.getRecentSearchQueryEntities(limit = 5).first()
        assertTrue(result.isEmpty())
    }
}