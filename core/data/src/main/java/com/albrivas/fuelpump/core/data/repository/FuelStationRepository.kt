/*
 * File: FuelStationRepository.kt
 * Project: FuelPump
 * Module: FuelPump.core.data.main
 * Last modified: 1/5/23, 12:24 AM
 *
 * Created by albertorivas on 1/7/23, 1:06 PM
 * Copyright © 2023 Alberto Rivas. All rights reserved.
 *
 */

package com.albrivas.fuelpump.core.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.albrivas.fuelpump.core.database.dao.FuelStationDao
import com.albrivas.fuelpump.core.database.model.FuelStationEntity
import javax.inject.Inject

interface FuelStationRepository {
    val listFuelStation: Flow<List<String>>

    suspend fun addAllStations(listStations: List<FuelStationEntity>)
}
