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

import android.location.Location
import com.albrivas.fuelpump.core.common.IoDispatcher
import com.albrivas.fuelpump.core.data.mapper.asEntity
import com.albrivas.fuelpump.core.database.dao.FuelStationDao
import com.albrivas.fuelpump.core.database.dao.UserDataDao
import com.albrivas.fuelpump.core.database.model.asExternalModel
import com.albrivas.fuelpump.core.model.data.FuelStation
import com.albrivas.fuelpump.core.model.data.FuelType
import com.albrivas.fuelpump.core.model.data.PriceCategory
import com.albrivas.fuelpump.core.network.datasource.RemoteDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class OfflineFuelStationRepository @Inject constructor(
    private val fuelStationDao: FuelStationDao,
    private val remoteDataSource: RemoteDataSource,
    private val userDataDao: UserDataDao,
    @IoDispatcher private val dispatcherIo: CoroutineDispatcher
) : FuelStationRepository {

    override suspend fun addAllStations() = withContext(dispatcherIo) {
        remoteDataSource.getListFuelStations().fold(ifLeft = {}, ifRight = { data ->
            fuelStationDao.insertFuelStation(data.listPriceFuelStation.map { it.asEntity() })
        })
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getFuelStationByLocation(
        userLocation: Location
    ): Flow<List<FuelStation>> =
        userDataDao.getUserData().flatMapLatest { user ->
            fuelStationDao.getFuelStations(user.fuelSelection.name).map { items ->
                val externalModel = items.map { it.asExternalModel() }
                    .sortedBy { it.location.distanceTo(userLocation) }
                    .take(12)

                val (minPrice, maxPrice) = externalModel.calculateFuelPrices(user.fuelSelection)

                externalModel.map { fuelStation ->
                    val priceCategory = fuelStation.getPriceCategory(
                        user.fuelSelection,
                        minPrice,
                        maxPrice
                    )
                    fuelStation.copy(
                        priceCategory = priceCategory,
                        distance = fuelStation.location.distanceTo(userLocation)
                    )
                }
            }
        }.flowOn(dispatcherIo)

    private fun List<FuelStation>.calculateFuelPrices(fuelType: FuelType): Pair<Double, Double> {
        val prices = when (fuelType) {
            FuelType.GASOLINE_95 -> map { it.priceGasoline95_E5 }
            FuelType.GASOLINE_98 -> map { it.priceGasoline98_E5 }
            FuelType.DIESEL -> map { it.priceGasoilA }
            FuelType.DIESEL_PLUS -> map { it.priceGasoilPremium }
            FuelType.ELECTRIC -> listOf(0.0)
        }

        return Pair(prices.minOrNull() ?: 0.0, prices.maxOrNull() ?: 0.0)
    }

    private fun FuelStation.getPriceCategory(
        fuelType: FuelType,
        minPrice: Double,
        maxPrice: Double
    ): PriceCategory {
        val currentPrice = when (fuelType) {
            FuelType.GASOLINE_95 -> priceGasoline95_E5
            FuelType.GASOLINE_98 -> priceGasoline98_E5
            FuelType.DIESEL -> priceGasoilA
            FuelType.DIESEL_PLUS -> priceGasoilPremium
            FuelType.ELECTRIC -> 0.0
        }

        val priceRange = maxPrice - minPrice
        val step = priceRange / 3

        return when {
            currentPrice < minPrice + step -> PriceCategory.CHEAP
            currentPrice < minPrice + 2 * step -> PriceCategory.NORMAL
            else -> PriceCategory.EXPENSIVE
        }
    }
}