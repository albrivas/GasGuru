package com.gasguru.core.data.repository

import android.location.Location
import com.gasguru.core.common.CommonUtils.isStationOpen
import com.gasguru.core.common.IoDispatcher
import com.gasguru.core.data.mapper.asEntity
import com.gasguru.core.database.dao.FuelStationDao
import com.gasguru.core.database.model.asExternalModel
import com.gasguru.core.database.model.getLocation
import com.gasguru.core.model.data.FuelStation
import com.gasguru.core.model.data.FuelType
import com.gasguru.core.model.data.OpeningHours
import com.gasguru.core.model.data.PriceCategory
import com.gasguru.core.network.datasource.RemoteDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.Locale
import javax.inject.Inject

const val PRICE_RANGE = 3

class OfflineFuelStationRepository @Inject constructor(
    private val fuelStationDao: FuelStationDao,
    private val remoteDataSource: RemoteDataSource,
    @IoDispatcher private val dispatcherIo: CoroutineDispatcher,
    private val offlineUserDataRepository: OfflineUserDataRepository,
) : FuelStationRepository {

    private val ioScope = CoroutineScope(dispatcherIo + SupervisorJob())

    override suspend fun addAllStations() = withContext(ioScope.coroutineContext) {
        remoteDataSource.getListFuelStations().fold(ifLeft = {}, ifRight = { data ->
            fuelStationDao.insertFuelStation(data.listPriceFuelStation.map { it.asEntity() })
            offlineUserDataRepository.updateLastUpdate()
        })
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getFuelStationByLocation(
        userLocation: Location,
        maxStations: Int,
        brands: List<String>,
        schedule: OpeningHours,
    ): Flow<List<FuelStation>> =
        offlineUserDataRepository.userData.flatMapLatest { user ->
            fuelStationDao.getFuelStations(
                fuelType = user.fuelSelection.name,
                brands = brands.map { it.uppercase() }
            ).map { items ->
                val externalModel = items.map { it.asExternalModel() }
                    .sortedBy { it.location.distanceTo(userLocation) }
                    .filter {
                        when (schedule) {
                            OpeningHours.OPEN_NOW -> it.isStationOpen()
                            OpeningHours.OPEN_24H -> it.schedule.trim()
                                .uppercase(Locale.ROOT) == "L-D: 24H"
                            OpeningHours.NONE -> true
                        }
                    }
                    .take(maxStations)

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

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getFuelStationById(id: Int, userLocation: Location): Flow<FuelStation> =
        fuelStationDao.getFuelStationById(id)
            .flatMapLatest { station ->
                offlineUserDataRepository.getUserWithFavoriteStations(userLocation = userLocation)
                    .map { userWithFavorites ->
                        val isFavorite =
                            userWithFavorites.favoriteStations.any { it.idServiceStation == station.idServiceStation }
                        station.asExternalModel().copy(
                            isFavorite = isFavorite,
                            distance = station.getLocation().distanceTo(userLocation)
                        )
                    }
            }
            .flowOn(Dispatchers.IO)

    private fun List<FuelStation>.calculateFuelPrices(fuelType: FuelType): Pair<Double, Double> {
        val prices = when (fuelType) {
            FuelType.GASOLINE_95 -> map { it.priceGasoline95E5 }
            FuelType.GASOLINE_98 -> map { it.priceGasoline98E5 }
            FuelType.DIESEL -> map { it.priceGasoilA }
            FuelType.DIESEL_PLUS -> map { it.priceGasoilPremium }
            FuelType.GASOLINE_95_PREMIUM -> map { it.priceGasoline95E5Premium }
            FuelType.GASOLINE_95_E10 -> map { it.priceGasoline95E10 }
            FuelType.GASOLINE_98_PREMIUM -> map { it.priceGasoline98E10 }
            FuelType.GASOIL_B -> map { it.priceGasoilB }
        }

        return Pair(prices.minOrNull() ?: 0.0, prices.maxOrNull() ?: 0.0)
    }

    private fun FuelStation.getPriceCategory(
        fuelType: FuelType,
        minPrice: Double,
        maxPrice: Double,
    ): PriceCategory {
        val currentPrice = when (fuelType) {
            FuelType.GASOLINE_95 -> priceGasoline95E5
            FuelType.GASOLINE_98 -> priceGasoline98E5
            FuelType.DIESEL -> priceGasoilA
            FuelType.DIESEL_PLUS -> priceGasoilPremium
            FuelType.GASOLINE_95_PREMIUM -> priceGasoline95E5Premium
            FuelType.GASOLINE_95_E10 -> priceGasoline95E10
            FuelType.GASOLINE_98_PREMIUM -> priceGasoline98E10
            FuelType.GASOIL_B -> priceGasoilB
        }

        val priceRange = maxPrice - minPrice
        val step = priceRange / PRICE_RANGE

        return when {
            currentPrice < minPrice + step -> PriceCategory.CHEAP
            currentPrice < minPrice + 2 * step -> PriceCategory.NORMAL
            else -> PriceCategory.EXPENSIVE
        }
    }
}
