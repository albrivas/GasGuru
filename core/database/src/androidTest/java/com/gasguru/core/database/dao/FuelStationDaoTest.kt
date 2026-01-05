package com.gasguru.core.database.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import app.cash.turbine.test
import com.gasguru.core.database.GasGuruDatabase
import com.gasguru.core.database.model.FuelStationEntity
import com.gasguru.core.model.data.FuelType
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class FuelStationDaoTest {

    private lateinit var fuelStationDao: FuelStationDao
    private lateinit var db: GasGuruDatabase

    @BeforeEach
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context,
            GasGuruDatabase::class.java
        ).build()
        fuelStationDao = db.fuelStationDao()
    }

    @AfterEach
    fun closeDb() = db.close()

    @Test
    @DisplayName("Retrieves all fuel stations when no filters are applied")
    fun getAllStations() = runTest {
        val listStations = listOf(
            testFuelStationEntity(
                brand = "Repsol", isFavorite = false, idServiceStation = 1
            ),
            testFuelStationEntity(
                brand = "Cepsa", isFavorite = false, idServiceStation = 2
            )
        )
        fuelStationDao.insertFuelStation(listStations)

        val result = fuelStationDao.getFuelStations(
            fuelType = FuelType.GASOLINE_95.name,
            brands = emptyList()
        )

        result.test {
            Assertions.assertEquals(awaitItem(), listStations)
            cancel()
        }
    }

    @Test
    @DisplayName("Retrieves a specific fuel station by its ID")
    fun getStationBrandFilter() = runTest {
        val listStations = listOf(
            testFuelStationEntity(
                brand = "Repsol", isFavorite = false, idServiceStation = 1
            ),
            testFuelStationEntity(
                brand = "Cepsa", isFavorite = false, idServiceStation = 2
            )
        )
        fuelStationDao.insertFuelStation(listStations)

        val result = fuelStationDao.getFuelStations(
            fuelType = FuelType.GASOLINE_95.name,
            brands = listOf("Repsol")
        ).first()

        Assertions.assertEquals(result, listOf(listStations.first()))
    }

    @Test
    @DisplayName("Get fuel station by id")
    fun getFavorites() = runTest {
        val listStations = listOf(
            testFuelStationEntity(
                brand = "Repsol", isFavorite = false, idServiceStation = 1
            ),
            testFuelStationEntity(
                brand = "Cepsa", isFavorite = true, idServiceStation = 2
            )
        )
        fuelStationDao.insertFuelStation(listStations)

        val result = fuelStationDao.getFuelStationById(2).first()

        Assertions.assertEquals(result, listStations.last())
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
