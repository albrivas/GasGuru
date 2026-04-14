package com.gasguru.core.database.dao

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import app.cash.turbine.test
import com.gasguru.core.database.GasGuruDatabase
import com.gasguru.core.database.model.FuelStationEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName(
    """
    GIVEN a FavoriteStationDao
    WHEN performing favorite station operations
    THEN the results are correct
    """
)
class FavoriteStationDaoTest {

    private lateinit var favoriteStationDao: FavoriteStationDao
    private lateinit var fuelStationDao: FuelStationDao
    private lateinit var db: GasGuruDatabase

    @BeforeEach
    fun createDb() {
        db = Room.inMemoryDatabaseBuilder<GasGuruDatabase>()
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
        favoriteStationDao = db.favoriteStationDao()
        fuelStationDao = db.fuelStationDao()
    }

    @AfterEach
    fun closeDb() = db.close()

    // region getFavoriteStationIds

    @Test
    @DisplayName(
        """
        GIVEN empty database
        WHEN getting favorite station ids
        THEN returns empty list
        """
    )
    fun getFavoriteStationIds_withEmptyDatabase_returnsEmptyList() = runTest {
        val result = favoriteStationDao.getFavoriteStationIds().first()

        assertTrue(result.isEmpty())
    }

    @Test
    @DisplayName(
        """
        GIVEN a favorite station was added
        WHEN getting favorite station ids
        THEN returns that station id
        """
    )
    fun getFavoriteStationIds_afterAddingFavorite_returnsStationId() = runTest {
        favoriteStationDao.addFavoriteStation(stationId = 42)

        val result = favoriteStationDao.getFavoriteStationIds().first()

        assertEquals(1, result.size)
        assertEquals(42, result.first())
    }

    @Test
    @DisplayName(
        """
        GIVEN multiple favorite stations were added
        WHEN getting favorite station ids
        THEN returns all ids
        """
    )
    fun getFavoriteStationIds_afterAddingMultipleFavorites_returnsAllIds() = runTest {
        favoriteStationDao.addFavoriteStation(stationId = 1)
        favoriteStationDao.addFavoriteStation(stationId = 2)
        favoriteStationDao.addFavoriteStation(stationId = 3)

        val result = favoriteStationDao.getFavoriteStationIds().first()

        assertEquals(3, result.size)
        assertTrue(result.containsAll(listOf(1, 2, 3)))
    }

    @Test
    @DisplayName(
        """
        GIVEN a favorite station was added and removed
        WHEN getting favorite station ids
        THEN returns empty list
        """
    )
    fun getFavoriteStationIds_afterAddingAndRemoving_returnsEmptyList() = runTest {
        favoriteStationDao.addFavoriteStation(stationId = 10)
        favoriteStationDao.removeFavoriteStation(stationId = 10)

        val result = favoriteStationDao.getFavoriteStationIds().first()

        assertTrue(result.isEmpty())
    }

    // endregion

    // region addFavoriteStation

    @Test
    @DisplayName(
        """
        GIVEN a favorite station is inserted twice with same id
        WHEN getting favorite station ids
        THEN duplicate is ignored and only one entry exists
        """
    )
    fun addFavoriteStation_insertingDuplicateId_isIgnored() = runTest {
        favoriteStationDao.addFavoriteStation(stationId = 7)
        favoriteStationDao.addFavoriteStation(stationId = 7)

        val result = favoriteStationDao.getFavoriteStationIds().first()

        assertEquals(1, result.size)
        assertEquals(7, result.first())
    }

    // endregion

    // region removeFavoriteStation

    @Test
    @DisplayName(
        """
        GIVEN a non-existent station id
        WHEN removing it from favorites
        THEN no error is thrown and favorites remain empty
        """
    )
    fun removeFavoriteStation_nonExistentId_doesNotThrow() = runTest {
        favoriteStationDao.removeFavoriteStation(stationId = 999)

        val result = favoriteStationDao.getFavoriteStationIds().first()

        assertTrue(result.isEmpty())
    }

    @Test
    @DisplayName(
        """
        GIVEN two favorite stations
        WHEN removing one
        THEN only the other remains
        """
    )
    fun removeFavoriteStation_removesOnlyTargetStation() = runTest {
        favoriteStationDao.addFavoriteStation(stationId = 1)
        favoriteStationDao.addFavoriteStation(stationId = 2)

        favoriteStationDao.removeFavoriteStation(stationId = 1)

        val result = favoriteStationDao.getFavoriteStationIds().first()
        assertEquals(1, result.size)
        assertEquals(2, result.first())
    }

    // endregion

    // region isFavorite

    @Test
    @DisplayName(
        """
        GIVEN a station is in favorites
        WHEN checking if it is favorite
        THEN returns true
        """
    )
    fun isFavorite_whenStationIsInFavorites_returnsTrue() = runTest {
        favoriteStationDao.addFavoriteStation(stationId = 55)

        val result = favoriteStationDao.isFavorite(stationId = 55)

        assertTrue(result)
    }

    @Test
    @DisplayName(
        """
        GIVEN a station is not in favorites
        WHEN checking if it is favorite
        THEN returns false
        """
    )
    fun isFavorite_whenStationIsNotInFavorites_returnsFalse() = runTest {
        val result = favoriteStationDao.isFavorite(stationId = 55)

        assertFalse(result)
    }

    @Test
    @DisplayName(
        """
        GIVEN a station was added then removed from favorites
        WHEN checking if it is favorite
        THEN returns false
        """
    )
    fun isFavorite_afterRemovingStation_returnsFalse() = runTest {
        favoriteStationDao.addFavoriteStation(stationId = 30)
        favoriteStationDao.removeFavoriteStation(stationId = 30)

        val result = favoriteStationDao.isFavorite(stationId = 30)

        assertFalse(result)
    }

    // endregion

    // region getFavoriteStations

    @Test
    @DisplayName(
        """
        GIVEN empty database
        WHEN getting favorite stations
        THEN returns empty list
        """
    )
    fun getFavoriteStations_withEmptyDatabase_returnsEmptyList() = runTest {
        val result = favoriteStationDao.getFavoriteStations().first()

        assertTrue(result.isEmpty())
    }

    @Test
    @DisplayName(
        """
        GIVEN one station is inserted and marked as favorite
        WHEN getting favorite stations
        THEN returns that station
        """
    )
    fun getFavoriteStations_withOneFavoriteStation_returnsThatStation() = runTest {
        val station = testFavoriteStationEntity(idServiceStation = 100)
        fuelStationDao.insertFuelStation(listOf(station))
        favoriteStationDao.addFavoriteStation(stationId = 100)

        val result = favoriteStationDao.getFavoriteStations().first()

        assertEquals(1, result.size)
        assertEquals(100, result.first().idServiceStation)
    }

    @Test
    @DisplayName(
        """
        GIVEN two stations where only one is favorited
        WHEN getting favorite stations
        THEN returns only the favorited one
        """
    )
    fun getFavoriteStations_withOnlyOneFavoriteAmongMultiple_returnsOnlyFavorited() = runTest {
        val stationFavorite = testFavoriteStationEntity(idServiceStation = 1)
        val stationNotFavorite = testFavoriteStationEntity(idServiceStation = 2)
        fuelStationDao.insertFuelStation(listOf(stationFavorite, stationNotFavorite))
        favoriteStationDao.addFavoriteStation(stationId = 1)

        val result = favoriteStationDao.getFavoriteStations().first()

        assertEquals(1, result.size)
        assertEquals(1, result.first().idServiceStation)
    }

    @Test
    @DisplayName(
        """
        GIVEN a favorite id that has no matching station
        WHEN getting favorite stations
        THEN returns empty list
        """
    )
    fun getFavoriteStations_withOrphanFavoriteId_returnsEmptyList() = runTest {
        favoriteStationDao.addFavoriteStation(stationId = 9999)

        val result = favoriteStationDao.getFavoriteStations().first()

        assertTrue(result.isEmpty())
    }

    @Test
    @DisplayName(
        """
        GIVEN multiple favorited stations
        WHEN getting favorite stations
        THEN returns all of them
        """
    )
    fun getFavoriteStations_withMultipleFavorites_returnsAll() = runTest {
        val stations = listOf(
            testFavoriteStationEntity(idServiceStation = 1),
            testFavoriteStationEntity(idServiceStation = 2),
            testFavoriteStationEntity(idServiceStation = 3),
        )
        fuelStationDao.insertFuelStation(stations)
        favoriteStationDao.addFavoriteStation(stationId = 1)
        favoriteStationDao.addFavoriteStation(stationId = 2)
        favoriteStationDao.addFavoriteStation(stationId = 3)

        val result = favoriteStationDao.getFavoriteStations().first()

        assertEquals(3, result.size)
    }

    @Test
    @DisplayName(
        """
        GIVEN two favorited stations
        WHEN removing one from favorites
        THEN does not contain the removed station
        """
    )
    fun getFavoriteStations_afterRemovingFavorite_doesNotContainRemovedStation() = runTest {
        val stationA = testFavoriteStationEntity(idServiceStation = 1)
        val stationB = testFavoriteStationEntity(idServiceStation = 2)
        fuelStationDao.insertFuelStation(listOf(stationA, stationB))
        favoriteStationDao.addFavoriteStation(stationId = 1)
        favoriteStationDao.addFavoriteStation(stationId = 2)

        favoriteStationDao.removeFavoriteStation(stationId = 1)

        val result = favoriteStationDao.getFavoriteStations().first()
        assertEquals(1, result.size)
        assertEquals(2, result.first().idServiceStation)
    }

    // endregion

    // region Flow emissions

    @Test
    @DisplayName(
        """
        GIVEN a flow of favorite station ids
        WHEN adding and removing stations
        THEN flow emits updates correctly
        """
    )
    fun getFavoriteStationIds_emitsUpdatesOnAddAndRemove() = runTest {
        favoriteStationDao.getFavoriteStationIds().test {
            assertEquals(emptyList<Int>(), awaitItem())

            favoriteStationDao.addFavoriteStation(stationId = 5)
            assertEquals(listOf(5), awaitItem())

            favoriteStationDao.addFavoriteStation(stationId = 10)
            val afterSecondAdd = awaitItem()
            assertTrue(afterSecondAdd.containsAll(listOf(5, 10)))

            favoriteStationDao.removeFavoriteStation(stationId = 5)
            assertEquals(listOf(10), awaitItem())

            cancel()
        }
    }

    @Test
    @DisplayName(
        """
        GIVEN a flow of favorite stations
        WHEN adding and removing favorites
        THEN flow emits updates correctly
        """
    )
    fun getFavoriteStations_emitsUpdatesWhenFavoritesChange() = runTest {
        val station = testFavoriteStationEntity(idServiceStation = 42)
        fuelStationDao.insertFuelStation(listOf(station))

        favoriteStationDao.getFavoriteStations().test {
            assertTrue(awaitItem().isEmpty())

            favoriteStationDao.addFavoriteStation(stationId = 42)
            val afterAdd = awaitItem()
            assertEquals(1, afterAdd.size)
            assertEquals(42, afterAdd.first().idServiceStation)

            favoriteStationDao.removeFavoriteStation(stationId = 42)
            assertTrue(awaitItem().isEmpty())

            cancel()
        }
    }

    // endregion
}

private fun testFavoriteStationEntity(idServiceStation: Int): FuelStationEntity =
    FuelStationEntity(
        bioEthanolPercentage = "",
        esterMethylPercentage = "",
        postalCode = "28001",
        direction = "Calle Falsa 123",
        schedule = "L-D: 00:00-24:00",
        idAutonomousCommunity = "13",
        idServiceStation = idServiceStation,
        idMunicipality = "79",
        idProvince = "28",
        latitude = 40.4168,
        locality = "Madrid",
        longitudeWGS84 = -3.7038,
        margin = "D",
        municipality = "Madrid",
        priceBiodiesel = 0.0,
        priceBioEthanol = 0.0,
        priceGasNaturalCompressed = 0.0,
        priceLiquefiedNaturalGas = 0.0,
        priceLiquefiedPetroleumGas = 0.0,
        priceGasoilA = 1.45,
        priceGasoilB = 0.0,
        priceGasoilPremium = 1.55,
        priceGasoline95E10 = 0.0,
        priceGasoline95E5 = 1.52,
        priceGasoline95E5Premium = 0.0,
        priceGasoline98E10 = 0.0,
        priceGasoline98E5 = 1.65,
        priceHydrogen = 0.0,
        priceAdblue = 0.0,
        province = "Madrid",
        referral = "",
        brandStation = "Repsol",
        typeSale = "P",
        lastUpdate = 1719310537523L,
        isFavorite = false,
    )
