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

import android.location.Location
import com.albrivas.fuelpump.core.model.data.FuelStation
import kotlinx.coroutines.flow.Flow

interface FuelStationRepository {
//    val listFuelStation: Flow<List<FuelStation>>

    suspend fun addAllStations(/*listStations: List<FuelStationEntity>*/)
    fun getFuelStationByLocation(userLocation: Location): Flow<List<FuelStation>>
}
