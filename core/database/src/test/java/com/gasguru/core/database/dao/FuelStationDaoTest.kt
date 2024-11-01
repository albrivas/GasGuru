package com.gasguru.core.database.dao

import android.content.Context
import androidx.room.Room
import app.cash.turbine.test
import com.gasguru.core.database.FuelPumpDatabase
import com.gasguru.core.database.model.FuelStationEntity
import com.gasguru.core.model.data.FuelType
import com.gasguru.core.testing.CoroutinesTestRule
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class FuelStationDaoTest {

    private lateinit var fuelStationDao: FuelStationDao
    private lateinit var db: FuelPumpDatabase

    @get: org.junit.Rule
    val dispatcherRule = CoroutinesTestRule()

    @org.junit.Before
    fun createDb() {
        val context = androidx.test.core.app.ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context,
            FuelPumpDatabase::class.java
        ).build()
        fuelStationDao = db.fuelStationDao()
    }

    @org.junit.After
    fun closeDb() {
        db.close()
    }

    @org.junit.Test
    fun fuelStationDao_insert_items() = kotlinx.coroutines.test.runTest {
        fuelStationDao.insertFuelStation(listFuelStation)

        val result = fuelStationDao.getFuelStations(FuelType.GASOLINE_95.name)

        result.test {
            org.junit.Assert.assertEquals(awaitItem(), listFuelStation)
            cancel()
        }
    }

    private val listFuelStation = listOf(
        FuelStationEntity(
            bioEthanolPercentage = "erroribus",
            esterMethylPercentage = "risus",
            postalCode = "eros",
            direction = "graeco",
            schedule = "quisque",
            idAutonomousCommunity = "utinam",
            idServiceStation = 1,
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
            province = "luptatum",
            referral = "possim",
            brandStation = "eos",
            typeSale = "pulvinar",
            lastUpdate = 1719310537523,
            isFavorite = false,
        ),
        FuelStationEntity(
            bioEthanolPercentage = "potenti",
            esterMethylPercentage = "magnis",
            postalCode = "vidisse",
            direction = "netus",
            schedule = "orci",
            idAutonomousCommunity = "finibus",
            idServiceStation = 2,
            idMunicipality = "legere",
            idProvince = "altera",
            latitude = 32.33,
            locality = "ne",
            longitudeWGS84 = 34.35,
            margin = "luptatum",
            municipality = "sagittis",
            priceBiodiesel = 36.37,
            priceBioEthanol = 38.39,
            priceGasNaturalCompressed = 40.41,
            priceLiquefiedNaturalGas = 42.43,
            priceLiquefiedPetroleumGas = 44.45,
            priceGasoilA = 46.47,
            priceGasoilB = 48.49,
            priceGasoilPremium = 50.51,
            priceGasoline95E10 = 52.53,
            priceGasoline95E5 = 54.55,
            priceGasoline95E5Premium = 56.57,
            priceGasoline98E10 = 58.59,
            priceGasoline98E5 = 60.61,
            priceHydrogen = 62.63,
            province = "cubilia",
            referral = "graece",
            brandStation = "aliquam",
            typeSale = "impetus",
            lastUpdate = 1719310537523,
            isFavorite = false
        )
    )
}
