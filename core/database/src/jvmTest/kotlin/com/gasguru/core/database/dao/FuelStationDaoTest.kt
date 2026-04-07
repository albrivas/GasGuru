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
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName(
    """
    GIVEN a FuelStationDao
    WHEN performing fuel station operations
    THEN the results are correct
    """
)
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
        GIVEN two stations are inserted
        WHEN getting all stations
        THEN returns all inserted stations
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
            assertEquals(awaitItem(), listStations)
            cancel()
        }
    }

    @Test
    @DisplayName(
        """
        GIVEN two stations of different brands
        WHEN getting stations with a brand filter
        THEN returns only stations matching the brand
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

        assertEquals(result, listOf(listStations.first()))
    }

    @Test
    @DisplayName(
        """
        GIVEN two stations where one is favorite
        WHEN getting station by id
        THEN returns the correct station
        """
    )
    fun getFavorites() = runTest {
        val listStations = listOf(
            testFuelStationEntity(brand = "Repsol", isFavorite = false, idServiceStation = 1),
            testFuelStationEntity(brand = "Cepsa", isFavorite = true, idServiceStation = 2),
        )
        fuelStationDao.insertFuelStation(listStations)

        val result = fuelStationDao.getFuelStationById(2).first()

        assertEquals(result, listStations.last())
    }

    @Test
    @DisplayName(
        """
        GIVEN empty database
        WHEN getting all stations
        THEN returns empty list
        """
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
        """
        GIVEN a diesel station and a gasoline-only station
        WHEN getting stations for diesel fuel type
        THEN returns only the diesel station
        """
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
        """
        GIVEN a station with adblue price
        WHEN getting stations for adblue fuel type
        THEN returns only that station
        """
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
        """
        GIVEN a station with lowercase brand
        WHEN filtering with uppercase brand
        THEN returns the matching station (case insensitive)
        """
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
        """
        GIVEN a Repsol station is inserted
        WHEN filtering by Galp brand
        THEN returns empty list
        """
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
        """
        GIVEN one station inside bounds and one outside
        WHEN getting stations in bounds
        THEN returns only the station inside bounds
        """
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
        """
        GIVEN empty database
        WHEN getting stations in bounds
        THEN returns empty list
        """
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
        """
        GIVEN a station with zero gasoline price within bounds
        WHEN getting stations in bounds for gasoline fuel type
        THEN station is not included
        """
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
        """
        GIVEN a station is inserted then another with same id is inserted
        WHEN getting station by id
        THEN the second station replaces the first
        """
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
