/*
 * File: OfflineFuelStationRepository.kt
 * Project: FuelPump
 * Module: FuelPump.core.data.main
 * Last modified: 1/7/23, 1:06 PM
 *
 * Created by albertorivas on 1/7/23, 1:06 PM
 * Copyright © 2023 Alberto Rivas. All rights reserved.
 *
 */

package com.albrivas.fuelpump.core.data.repository

import com.albrivas.fuelpump.core.data.mapper.asEntity
import com.albrivas.fuelpump.core.database.dao.FuelStationDao
import com.albrivas.fuelpump.core.database.model.asExternalModel
import com.albrivas.fuelpump.core.model.data.FuelStationModel
import com.albrivas.fuelpump.core.network.datasource.RemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class OfflineFuelStationRepository @Inject constructor(
    private val fuelStationDao: FuelStationDao,
    private val remoteDataSource: RemoteDataSource,
) : FuelStationRepository {

    override val listFuelStation: Flow<List<FuelStationModel>> =
        fuelStationDao.getFuelStations()
            .map { items -> items.map { it.asExternalModel() }.take(20) }


    override suspend fun addAllStations() {
        remoteDataSource.getListFuelStations().fold(ifLeft = {}, ifRight = { data ->
            fuelStationDao.insertFuelStation(data.listPriceFuelStation.map { it.asEntity() })
        })
    }

}