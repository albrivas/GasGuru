package com.albrivas.fuelpump.core.data.repository

import android.location.Location
import com.albrivas.fuelpump.core.model.data.FuelStation
import kotlinx.coroutines.flow.Flow

interface FuelStationRepository {
//    val listFuelStation: Flow<List<FuelStation>>

    suspend fun addAllStations(/*listStations: List<FuelStationEntity>*/)
    fun getFuelStationByLocation(userLocation: Location, maxStations: Int): Flow<List<FuelStation>>
}
