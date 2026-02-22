package com.gasguru.core.data.repository.stations

import com.gasguru.core.common.CommonUtils.isStationOpen
import com.gasguru.core.common.distanceTo
import com.gasguru.core.data.mapper.asEntity
import com.gasguru.core.data.mapper.calculateFuelPrices
import com.gasguru.core.data.mapper.getPriceCategory
import com.gasguru.core.data.repository.user.UserDataRepository
import com.gasguru.core.database.dao.FavoriteStationDao
import com.gasguru.core.database.dao.FuelStationDao
import com.gasguru.core.database.dao.PriceAlertDao
import com.gasguru.core.database.model.FuelStationEntity
import com.gasguru.core.database.model.asExternalModel
import com.gasguru.core.database.model.toLatLng
import com.gasguru.core.model.data.FuelStation
import com.gasguru.core.model.data.LatLng
import com.gasguru.core.model.data.OpeningHours
import com.gasguru.core.network.datasource.RemoteDataSource
import com.gasguru.core.network.model.NetworkPriceFuelStation
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.cos

class OfflineFuelStationRepository(
    private val fuelStationDao: FuelStationDao,
    private val remoteDataSource: RemoteDataSource,
    private val defaultDispatcher: CoroutineDispatcher,
    private val ioDispatcher: CoroutineDispatcher,
    private val offlineUserDataRepository: UserDataRepository,
    private val favoriteStationDao: FavoriteStationDao,
    private val priceAlertDao: PriceAlertDao,
) : FuelStationRepository {

    private val defaultScope = CoroutineScope(defaultDispatcher + SupervisorJob())

    override suspend fun addAllStations() {
        defaultScope.launch {
            remoteDataSource.getListFuelStations().fold(ifLeft = {}, ifRight = { data ->
                fuelStationDao.insertFuelStation(
                    data.listPriceFuelStation.map(NetworkPriceFuelStation::asEntity)
                )
                offlineUserDataRepository.updateLastUpdate()
            })

            defaultScope.cancel()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getFuelStationByLocation(
        userLocation: LatLng,
        maxStations: Int,
        brands: List<String>,
        schedule: OpeningHours,
    ): Flow<List<FuelStation>> =
        offlineUserDataRepository.userData.flatMapLatest { user ->
            fuelStationDao.getFuelStations(
                fuelType = user.fuelSelection.name,
                brands = brands.map { it.uppercase() }
            ).map { items ->
                val externalModel = items
                    .sortedBy {
                        LatLng(
                            latitude = it.latitude,
                            longitude = it.longitudeWGS84,
                        ).distanceTo(userLocation)
                    }
                    .take(maxStations)
                    .map(FuelStationEntity::asExternalModel)
                    .filter {
                        when (schedule) {
                            OpeningHours.OPEN_NOW -> it.isStationOpen()
                            OpeningHours.OPEN_24H -> it.schedule.trim()
                                .uppercase(Locale.ROOT) == "L-D: 24H"

                            OpeningHours.NONE -> true
                        }
                    }

                val (minPrice, maxPrice) = externalModel.calculateFuelPrices(fuelType = user.fuelSelection)

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
        }.flowOn(defaultDispatcher)

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getFuelStationById(id: Int, userLocation: LatLng): Flow<FuelStation> =
        combine(
            fuelStationDao.getFuelStationById(id),
            favoriteStationDao.getFavoriteStationIds(),
            priceAlertDao.getAllPriceAlerts(),
        ) { station, favoriteIds, alertEntities ->
            val isFavorite = favoriteIds.contains(station.idServiceStation)
            val hasPriceAlert = alertEntities.any { it.stationId == station.idServiceStation }
            val stationModel = station.asExternalModel()

            stationModel.copy(
                isFavorite = isFavorite,
                hasPriceAlert = hasPriceAlert,
                distance = stationModel.location.distanceTo(userLocation)
            )
        }.flowOn(ioDispatcher)

    override suspend fun getFuelStationInRoute(
        origin: LatLng,
        points: List<LatLng>,
    ): List<FuelStation> {
        if (points.isEmpty()) return emptyList()

        val userData = offlineUserDataRepository.userData.first()
        val radiusKm = 0.4
        val radiusMeters = radiusKm * 1000
        val fuelTypeName = userData.fuelSelection.name
        val allStations = mutableSetOf<FuelStationEntity>()

        val reducedPoints = points.filterIndexed { index, _ -> index % 2 == 0 }
        reducedPoints.forEach { point ->
            val bounds = calculateBoundingBox(point, radiusKm)

            val stationsInBounds = fuelStationDao.getFuelStationsInBounds(
                minLat = bounds.minLat,
                maxLat = bounds.maxLat,
                minLng = bounds.minLng,
                maxLng = bounds.maxLng,
                fuelType = fuelTypeName
            )

            stationsInBounds.forEach { station ->
                val distance = station.toLatLng().distanceTo(point)
                if (distance <= radiusMeters) {
                    allStations.add(station)
                }
            }
        }

        val externalModel = allStations.map(FuelStationEntity::asExternalModel)
        val (minPrice, maxPrice) = externalModel.calculateFuelPrices(fuelType = userData.fuelSelection)
        return externalModel.map { fuelStation ->
            val priceCategory = fuelStation.getPriceCategory(
                fuelType = userData.fuelSelection,
                minPrice = minPrice,
                maxPrice = maxPrice
            )
            fuelStation.copy(
                priceCategory = priceCategory,
                distance = fuelStation.location.distanceTo(origin)
            )
        }
    }

    private fun calculateBoundingBox(center: LatLng, radiusKm: Double): BoundingBox {
        val latDiff = radiusKm / 110.574
        val lngDiff = radiusKm / (111.320 * cos(Math.toRadians(center.latitude)))

        return BoundingBox(
            minLat = center.latitude - latDiff,
            maxLat = center.latitude + latDiff,
            minLng = center.longitude - lngDiff,
            maxLng = center.longitude + lngDiff
        )
    }

    private data class BoundingBox(
        val minLat: Double,
        val maxLat: Double,
        val minLng: Double,
        val maxLng: Double,
    )
}
