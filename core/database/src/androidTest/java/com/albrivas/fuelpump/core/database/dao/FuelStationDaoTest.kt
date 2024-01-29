/*
 * File: FuelStationDaoTest.kt
 * Project: FuelPump
 * Module: FuelPump.core.database.androidTest
 * Last modified: 1/7/23, 4:31 PM
 *
 * Created by albertorivas on 1/7/23, 4:31 PM
 * Copyright © 2023 Alberto Rivas. All rights reserved.
 *
 */

package com.albrivas.fuelpump.core.database.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import app.cash.turbine.test
import com.albrivas.fuelpump.core.database.FuelPumpDatabase
import com.albrivas.fuelpump.core.database.model.FuelStationEntity
import com.albrivas.fuelpump.core.testing.CoroutinesTestRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test


@ExperimentalCoroutinesApi
class FuelStationDaoTest {

    private lateinit var fuelStationDao: FuelStationDao
    private lateinit var db: FuelPumpDatabase

    @get: Rule
    val dispatcherRule = CoroutinesTestRule()

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context,
            FuelPumpDatabase::class.java
        ).build()
        fuelStationDao = db.fuelStationDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun fuelStationDao_insert_items() = runTest {
        fuelStationDao.insertFuelStation(listFuelStation)

        val result = fuelStationDao.getFuelStations()

        result.test {
            assertEquals(awaitItem(), listFuelStation)
            cancel()
        }
    }

    private val listFuelStation = listOf(
        FuelStationEntity(
            "",
            "",
            "",
            "",
            "",
            "",
            1,
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
        ),
        FuelStationEntity(
            "",
            "",
            "",
            "",
            "",
            "",
            2,
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
        )
    )
}