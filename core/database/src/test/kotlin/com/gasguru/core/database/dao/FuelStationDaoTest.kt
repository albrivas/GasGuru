package com.gasguru.core.database.dao

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import app.cash.turbine.test
import com.gasguru.core.database.GasGuruDatabase
import com.gasguru.core.database.model.FuelStationEntity
import com.gasguru.core.model.data.FuelType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class FuelStationDaoTest {

    private lateinit var fuelStationDao: FuelStationDao
    private lateinit var db: GasGuruDatabase

    @BeforeEach
    fun createDb() {
        db = Room.inMemoryDatabaseBuilder<GasGuruDatabase>()
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
        fuelStationDao = db.fuelStationDao()
    }

    @AfterEach
    fun closeDb() = db.close()

    @Test
    @DisplayName(
        """
        GIVEN fuel stations exist in database
        WHEN querying without brand filter
        THEN returns all stations
        """
    )
    fun getAllStations() = runTest {
        val listStations = listOf(
            testFuelStationEntity(brand = "Repsol", isFavorite = false, idServiceStation = 1),
            testFuelStationEntity(brand = "Cepsa", isFavorite = false, idServiceStation = 2),
        )
        fuelStationDao.insertFuelStation(listStations)

        val result = fuelStationDao.getFuelStations(
            fuelType = FuelType.GASOLINE_95.name,
            brands = emptyList(),
        )

        result.test {
            Assertions.assertEquals(awaitItem(), listStations)
            cancel()
        }
    }

    @Test
    @DisplayName(
        """
        GIVEN stations with different brands
        WHEN querying with brand filter
        THEN returns only matching stations
        """
    )
    fun getStationBrandFilter() = runTest {
        val listStations = listOf(
            testFuelStationEntity(brand = "Repsol", isFavorite = false, idServiceStation = 1),
            testFuelStationEntity(brand = "Cepsa", isFavorite = false, idServiceStation = 2),
        )
        fuelStationDao.insertFuelStation(listStations)

        val result = fuelStationDao.getFuelStations(
            fuelType = FuelType.GASOLINE_95.name,
            brands = listOf("Repsol"),
        ).first()

        Assertions.assertEquals(result, listOf(listStations.first()))
    }

    @Test
    @DisplayName(
        """
        GIVEN a station exists in database
        WHEN getting fuel station by id
        THEN returns the matching station
        """
    )
    fun getFavorites() = runTest {
        val listStations = listOf(
            testFuelStationEntity(brand = "Repsol", isFavorite = false, idServiceStation = 1),
            testFuelStationEntity(brand = "Cepsa", isFavorite = true, idServiceStation = 2),
        )
        fuelStationDao.insertFuelStation(listStations)

        val result = fuelStationDao.getFuelStationById(2).first()

        Assertions.assertEquals(result, listStations.last())
    }

    @Test
    @DisplayName(
        """GIVEN empty database
        WHEN querying fuel stations without brand filter
        THEN returns empty list"""
    )
    fun getFuelStations_withEmptyDatabase_returnsEmptyList() = runTest {
        val result = fuelStationDao.getFuelStations(
            fuelType = FuelType.GASOLINE_95.name,
            brands = emptyList(),
        )

        result.test {
            assertTrue(awaitItem().isEmpty())
            cancel()
        }
    }

    @Test
    @DisplayName(
        """GIVEN stations with diesel price
        WHEN querying with DIESEL fuel type
        THEN returns only stations with diesel price > 0"""
    )
    fun getFuelStations_withDieselFuelType_returnsOnlyDieselStations() = runTest {
        val dieselStation = testFuelStationEntity(
            brand = "Repsol",
            isFavorite = false,
            idServiceStation = 1,
        ).copy(priceGasoline95E5 = 0.0, priceGasoilA = 1.45)
        val gasolineOnlyStation = testFuelStationEntity(
            brand = "Cepsa",
            isFavorite = false,
            idServiceStation = 2,
        ).copy(priceGasoline95E5 = 1.52, priceGasoilA = 0.0)
        fuelStationDao.insertFuelStation(listOf(dieselStation, gasolineOnlyStation))

        val result = fuelStationDao.getFuelStations(
            fuelType = FuelType.DIESEL.name,
            brands = emptyList(),
        ).first()

        assertEquals(1, result.size)
        assertEquals(1, result.first().idServiceStation)
    }

    @Test
    @DisplayName(
        """GIVEN stations with adblue price
        WHEN querying with ADBLUE fuel type
        THEN returns only stations with adblue price > 0"""
    )
    fun getFuelStations_withAdBlueFuelType_returnsOnlyAdBlueStations() = runTest {
        val adblueStation = testFuelStationEntity(
            brand = "Shell",
            isFavorite = false,
            idServiceStation = 10,
        ).copy(priceAdblue = 0.99, priceGasoline95E5 = 0.0, priceGasoilA = 0.0)
        fuelStationDao.insertFuelStation(listOf(adblueStation))

        val result = fuelStationDao.getFuelStations(
            fuelType = FuelType.ADBLUE.name,
            brands = emptyList(),
        ).first()

        assertEquals(1, result.size)
        assertEquals(10, result.first().idServiceStation)
    }

    @Test
    @DisplayName(
        """GIVEN stations with different brands with mixed case
        WHEN querying with brand filter in uppercase
        THEN returns stations regardless of original case (NOCASE)"""
    )
    fun getFuelStations_brandFilterCaseInsensitive_returnsMatchingStations() = runTest {
        val station = testFuelStationEntity(
            brand = "repsol",
            isFavorite = false,
            idServiceStation = 99,
        )
        fuelStationDao.insertFuelStation(listOf(station))

        val result = fuelStationDao.getFuelStations(
            fuelType = FuelType.GASOLINE_95.name,
            brands = listOf("REPSOL"),
        ).first()

        assertEquals(1, result.size)
    }

    @Test
    @DisplayName(
        """GIVEN stations with brand filter applied
        WHEN brand list does not match any station
        THEN returns empty list"""
    )
    fun getFuelStations_brandFilterNoMatch_returnsEmptyList() = runTest {
        val station = testFuelStationEntity(
            brand = "Repsol",
            isFavorite = false,
            idServiceStation = 1,
        )
        fuelStationDao.insertFuelStation(listOf(station))

        val result = fuelStationDao.getFuelStations(
            fuelType = FuelType.GASOLINE_95.name,
            brands = listOf("Galp"),
        ).first()

        assertTrue(result.isEmpty())
    }

    @Test
    @DisplayName(
        """GIVEN stations inside a geographic bounding box
        WHEN querying getFuelStationsInBounds
        THEN returns only stations within bounds with matching fuel type"""
    )
    fun getFuelStationsInBounds_withMatchingStations_returnsThem() = runTest {
        val insideBounds = testFuelStationEntity(
            brand = "Repsol",
            isFavorite = false,
            idServiceStation = 1,
        ).copy(latitude = 40.4168, longitudeWGS84 = -3.7038)
        val outsideBounds = testFuelStationEntity(
            brand = "Cepsa",
            isFavorite = false,
            idServiceStation = 2,
        ).copy(latitude = 52.5200, longitudeWGS84 = 13.4050)
        fuelStationDao.insertFuelStation(listOf(insideBounds, outsideBounds))

        val result = fuelStationDao.getFuelStationsInBounds(
            minLat = 40.0,
            maxLat = 41.0,
            minLng = -4.0,
            maxLng = -3.0,
            fuelType = FuelType.GASOLINE_95.name,
        )

        assertEquals(1, result.size)
        assertEquals(1, result.first().idServiceStation)
    }

    @Test
    @DisplayName(
        """GIVEN empty database
        WHEN querying getFuelStationsInBounds
        THEN returns empty list"""
    )
    fun getFuelStationsInBounds_withEmptyDatabase_returnsEmptyList() = runTest {
        val result = fuelStationDao.getFuelStationsInBounds(
            minLat = 40.0,
            maxLat = 41.0,
            minLng = -4.0,
            maxLng = -3.0,
            fuelType = FuelType.GASOLINE_95.name,
        )

        assertTrue(result.isEmpty())
    }

    @Test
    @DisplayName(
        """GIVEN stations inside bounds but with fuel type price = 0
        WHEN querying getFuelStationsInBounds with that fuel type
        THEN returns empty list"""
    )
    fun getFuelStationsInBounds_fuelTypePriceZero_notIncluded() = runTest {
        val station = testFuelStationEntity(
            brand = "Repsol",
            isFavorite = false,
            idServiceStation = 1,
        ).copy(
            latitude = 40.4168,
            longitudeWGS84 = -3.7038,
            priceGasoline95E5 = 0.0,
            priceGasoilA = 1.45,
        )
        fuelStationDao.insertFuelStation(listOf(station))

        val result = fuelStationDao.getFuelStationsInBounds(
            minLat = 40.0,
            maxLat = 41.0,
            minLng = -4.0,
            maxLng = -3.0,
            fuelType = FuelType.GASOLINE_95.name,
        )

        assertTrue(result.isEmpty())
    }

    @Test
    @DisplayName(
        """GIVEN a station in db
        WHEN inserting a station with the same id (REPLACE conflict)
        THEN the new record replaces the old one"""
    )
    fun insertFuelStation_duplicateId_replacesExistingRecord() = runTest {
        val original = testFuelStationEntity(brand = "Repsol", isFavorite = false, idServiceStation = 1)
        val updated = original.copy(brandStation = "Cepsa")
        fuelStationDao.insertFuelStation(listOf(original))
        fuelStationDao.insertFuelStation(listOf(updated))

        val result = fuelStationDao.getFuelStationById(id = 1).first()

        assertEquals("Cepsa", result.brandStation)
    }
}

private fun testFuelStationEntity(brand: String, isFavorite: Boolean, idServiceStation: Int) =
    FuelStationEntity(
        bioEthanolPercentage = "erroribus",
        esterMethylPercentage = "risus",
        postalCode = "eros",
        direction = "graeco",
        schedule = "quisque",
        idAutonomousCommunity = "utinam",
        idServiceStation = idServiceStation,
        idMunicipality = "ceteros",
        idProvince = "vulputate",
        latitude = 96.97,
        locality = "turpis",
        longitudeWGS84 = 98.99,
        margin = "aperiri",
        municipality = "dicat",
        priceBiodiesel = 100.101,
        priceBioEthanol = 102.103,
        priceGasNaturalCompressed = 104.105,
        priceLiquefiedNaturalGas = 106.107,
        priceLiquefiedPetroleumGas = 108.109,
        priceGasoilA = 110.111,
        priceGasoilB = 112.113,
        priceGasoilPremium = 114.115,
        priceGasoline95E10 = 116.117,
        priceGasoline95E5 = 118.119,
        priceGasoline95E5Premium = 120.121,
        priceGasoline98E10 = 122.123,
        priceGasoline98E5 = 124.125,
        priceHydrogen = 126.127,
        priceAdblue = 128.129,
        province = "luptatum",
        referral = "possim",
        brandStation = brand,
        typeSale = "pulvinar",
        lastUpdate = 1719310537523,
        isFavorite = isFavorite,
    )
