package com.gasguru.data

import app.cash.turbine.test
import com.gasguru.core.data.repository.stations.OfflineFuelStationRepository
import com.gasguru.core.database.model.FuelStationEntity
import com.gasguru.core.model.data.FuelType
import com.gasguru.core.model.data.LatLng
import com.gasguru.core.model.data.MapBounds
import com.gasguru.core.model.data.OpeningHours
import com.gasguru.core.model.data.PriceCategory
import com.gasguru.core.model.data.UserData
import com.gasguru.core.model.data.Vehicle
import com.gasguru.core.model.data.VehicleType
import com.gasguru.core.testing.fakes.data.database.FakeFavoriteStationDao
import com.gasguru.core.testing.fakes.data.database.FakeFuelStationDao
import com.gasguru.core.testing.fakes.data.database.FakePriceAlertDao
import com.gasguru.core.testing.fakes.data.network.FakeRemoteDataSource
import com.gasguru.core.testing.fakes.data.user.FakeUserDataRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class OfflineFuelStationRepositoryTest {

    private val userLocation = LatLng(latitude = 40.0, longitude = -3.0)

    private fun buildRepository(
        stations: List<FuelStationEntity>,
        dispatcher: TestDispatcher,
    ): OfflineFuelStationRepository = OfflineFuelStationRepository(
        fuelStationDao = FakeFuelStationDao(initialStations = stations),
        remoteDataSource = FakeRemoteDataSource(),
        defaultDispatcher = dispatcher,
        ioDispatcher = dispatcher,
        offlineUserDataRepository = FakeUserDataRepository(
            initialUserData = UserData(
                vehicles = listOf(
                    Vehicle(
                        name = "Car",
                        fuelType = FuelType.GASOLINE_95,
                        tankCapacity = 50,
                        vehicleType = VehicleType.CAR,
                        isPrincipal = true,
                    ),
                ),
            ),
        ),
        favoriteStationDao = FakeFavoriteStationDao(),
        priceAlertDao = FakePriceAlertDao(),
    )

    @Test
    fun getFuelStationByLocation_withOpen24hAndNearerClosedStations_closedStationsDoNotConsumeQuota() =
        runTest {
            val dispatcher = UnconfinedTestDispatcher(testScheduler)
            val nearClosedStations = (1..5).map { index ->
                stationEntity(
                    id = index,
                    latitude = 40.0 + index * 0.001,
                    schedule = "L-V: 07:00-22:00",
                )
            }
            val farOpen24hStations = (6..10).map { index ->
                stationEntity(
                    id = index,
                    latitude = 40.0 + index * 0.001,
                    schedule = "L-D: 24H",
                )
            }
            val sut = buildRepository(
                stations = nearClosedStations + farOpen24hStations,
                dispatcher = dispatcher,
            )

            sut.getFuelStationByLocation(
                userLocation = userLocation,
                maxStations = 3,
                brands = emptyList(),
                schedule = OpeningHours.OPEN_24H,
            ).test {
                val stations = awaitItem()
                assertEquals(expected = listOf(6, 7, 8), actual = stations.map { it.idServiceStation })
                assertTrue(stations.all { it.schedule == "L-D: 24H" })
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun getFuelStationByLocation_withOpen24hAndFewerMatchesThanMax_returnsAllMatches() = runTest {
        val dispatcher = UnconfinedTestDispatcher(testScheduler)
        val stations = listOf(
            stationEntity(id = 1, latitude = 40.001, schedule = "L-V: 07:00-22:00"),
            stationEntity(id = 2, latitude = 40.002, schedule = "L-D: 24H"),
        )
        val sut = buildRepository(stations = stations, dispatcher = dispatcher)

        sut.getFuelStationByLocation(
            userLocation = userLocation,
            maxStations = 10,
            brands = emptyList(),
            schedule = OpeningHours.OPEN_24H,
        ).test {
            assertEquals(expected = listOf(2), actual = awaitItem().map { it.idServiceStation })
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun getFuelStationByLocation_withNoScheduleFilter_returnsNearestStationsUpToMax() = runTest {
        val dispatcher = UnconfinedTestDispatcher(testScheduler)
        val stations = (1..6).map { index ->
            stationEntity(id = index, latitude = 40.0 + index * 0.001, schedule = "L-V: 07:00-22:00")
        }
        val sut = buildRepository(stations = stations.reversed(), dispatcher = dispatcher)

        sut.getFuelStationByLocation(
            userLocation = userLocation,
            maxStations = 4,
            brands = emptyList(),
            schedule = OpeningHours.NONE,
        ).test {
            assertEquals(expected = listOf(1, 2, 3, 4), actual = awaitItem().map { it.idServiceStation })
            cancelAndIgnoreRemainingEvents()
        }
    }

    private val areaBounds = MapBounds(
        southwest = LatLng(latitude = 40.0, longitude = -3.5),
        northeast = LatLng(latitude = 40.1, longitude = -2.5),
    )

    @Test
    fun getFuelStationsInArea_returnsOnlyStationsInsideBounds() = runTest {
        val dispatcher = UnconfinedTestDispatcher(testScheduler)
        val stations = listOf(
            stationEntity(id = 1, latitude = 40.05, schedule = ""),
            stationEntity(id = 2, latitude = 40.5, schedule = ""),
            stationEntity(id = 3, latitude = 39.9, schedule = ""),
            stationEntity(id = 4, latitude = 40.05, schedule = "", longitude = -1.0),
        )
        val sut = buildRepository(stations = stations, dispatcher = dispatcher)

        sut.getFuelStationsInArea(
            bounds = areaBounds,
            referenceLocation = userLocation,
            maxStations = 10,
            brands = emptyList(),
            schedule = OpeningHours.NONE,
        ).test {
            val result = awaitItem()
            assertEquals(expected = listOf(1), actual = result.stations.map { it.idServiceStation })
            assertFalse(result.isTruncated)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun getFuelStationsInArea_sortsByDistanceToBoundsCenterAndComputesDistanceFromReference() = runTest {
        val dispatcher = UnconfinedTestDispatcher(testScheduler)
        // Bounds center latitude is 40.05; station 1 is the farthest from it but the closest to the reference
        val stations = listOf(
            stationEntity(id = 1, latitude = 40.0, schedule = ""),
            stationEntity(id = 2, latitude = 40.05, schedule = ""),
            stationEntity(id = 3, latitude = 40.08, schedule = ""),
        )
        val sut = buildRepository(stations = stations, dispatcher = dispatcher)

        sut.getFuelStationsInArea(
            bounds = areaBounds,
            referenceLocation = userLocation,
            maxStations = 10,
            brands = emptyList(),
            schedule = OpeningHours.NONE,
        ).test {
            val result = awaitItem()
            assertEquals(expected = listOf(2, 3, 1), actual = result.stations.map { it.idServiceStation })
            val distanceById = result.stations.associate { it.idServiceStation to it.distance }
            assertTrue(distanceById.getValue(1) < distanceById.getValue(2))
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun getFuelStationsInArea_withMoreStationsThanMax_keepsClosestToCenterAndFlagsTruncation() = runTest {
        val dispatcher = UnconfinedTestDispatcher(testScheduler)
        val stations = (1..5).map { index ->
            stationEntity(id = index, latitude = 40.05 + index * 0.001, schedule = "")
        }
        val sut = buildRepository(stations = stations.reversed(), dispatcher = dispatcher)

        sut.getFuelStationsInArea(
            bounds = areaBounds,
            referenceLocation = userLocation,
            maxStations = 3,
            brands = emptyList(),
            schedule = OpeningHours.NONE,
        ).test {
            val result = awaitItem()
            assertEquals(expected = listOf(1, 2, 3), actual = result.stations.map { it.idServiceStation })
            assertTrue(result.isTruncated)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun getFuelStationsInArea_withExactlyMaxStations_isNotTruncated() = runTest {
        val dispatcher = UnconfinedTestDispatcher(testScheduler)
        val stations = (1..3).map { index ->
            stationEntity(id = index, latitude = 40.05 + index * 0.001, schedule = "")
        }
        val sut = buildRepository(stations = stations, dispatcher = dispatcher)

        sut.getFuelStationsInArea(
            bounds = areaBounds,
            referenceLocation = userLocation,
            maxStations = 3,
            brands = emptyList(),
            schedule = OpeningHours.NONE,
        ).test {
            val result = awaitItem()
            assertEquals(expected = 3, actual = result.stations.size)
            assertFalse(result.isTruncated)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun getFuelStationsInArea_withOpen24h_closedStationsDoNotConsumeQuotaNorCountAsTruncation() = runTest {
        val dispatcher = UnconfinedTestDispatcher(testScheduler)
        val closedStations = (1..5).map { index ->
            stationEntity(id = index, latitude = 40.05 + index * 0.001, schedule = "L-V: 07:00-22:00")
        }
        val open24hStations = (6..7).map { index ->
            stationEntity(id = index, latitude = 40.05 + index * 0.001, schedule = "L-D: 24H")
        }
        val sut = buildRepository(stations = closedStations + open24hStations, dispatcher = dispatcher)

        sut.getFuelStationsInArea(
            bounds = areaBounds,
            referenceLocation = userLocation,
            maxStations = 2,
            brands = emptyList(),
            schedule = OpeningHours.OPEN_24H,
        ).test {
            val result = awaitItem()
            assertEquals(expected = listOf(6, 7), actual = result.stations.map { it.idServiceStation })
            assertFalse(result.isTruncated)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun getFuelStationsInArea_withBrandFilter_returnsOnlyMatchingBrand() = runTest {
        val dispatcher = UnconfinedTestDispatcher(testScheduler)
        val stations = listOf(
            stationEntity(id = 1, latitude = 40.05, schedule = "", brand = "REPSOL"),
            stationEntity(id = 2, latitude = 40.06, schedule = "", brand = "CEPSA"),
        )
        val sut = buildRepository(stations = stations, dispatcher = dispatcher)

        sut.getFuelStationsInArea(
            bounds = areaBounds,
            referenceLocation = userLocation,
            maxStations = 10,
            brands = listOf("cepsa"),
            schedule = OpeningHours.NONE,
        ).test {
            assertEquals(expected = listOf(2), actual = awaitItem().stations.map { it.idServiceStation })
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun getFuelStationsInArea_assignsPriceCategoriesOverReturnedStations() = runTest {
        val dispatcher = UnconfinedTestDispatcher(testScheduler)
        val stations = listOf(
            stationEntity(id = 1, latitude = 40.05, schedule = ""),
            stationEntity(id = 9, latitude = 40.06, schedule = ""),
            stationEntity(id = 300, latitude = 45.0, schedule = ""),
        )
        val sut = buildRepository(stations = stations, dispatcher = dispatcher)

        sut.getFuelStationsInArea(
            bounds = areaBounds,
            referenceLocation = userLocation,
            maxStations = 10,
            brands = emptyList(),
            schedule = OpeningHours.NONE,
        ).test {
            val categories = awaitItem().stations.associate { it.idServiceStation to it.priceCategory }
            assertEquals(expected = PriceCategory.CHEAP, actual = categories.getValue(1))
            assertEquals(expected = PriceCategory.EXPENSIVE, actual = categories.getValue(9))
            cancelAndIgnoreRemainingEvents()
        }
    }

    private fun stationEntity(
        id: Int,
        latitude: Double,
        schedule: String,
        longitude: Double = -3.0,
        brand: String = "REPSOL",
    ) = FuelStationEntity(
        bioEthanolPercentage = "",
        esterMethylPercentage = "",
        postalCode = "",
        direction = "",
        schedule = schedule,
        idAutonomousCommunity = "",
        idServiceStation = id,
        idMunicipality = "",
        idProvince = "",
        latitude = latitude,
        locality = "",
        longitudeWGS84 = longitude,
        margin = "",
        municipality = "",
        priceBiodiesel = 0.0,
        priceBioEthanol = 0.0,
        priceGasNaturalCompressed = 0.0,
        priceLiquefiedNaturalGas = 0.0,
        priceLiquefiedPetroleumGas = 0.0,
        priceGasoilA = 0.0,
        priceGasoilB = 0.0,
        priceGasoilPremium = 0.0,
        priceGasoline95E10 = 0.0,
        priceGasoline95E5 = 1.5 + id * 0.01,
        priceGasoline95E5Premium = 0.0,
        priceGasoline98E10 = 0.0,
        priceGasoline98E5 = 0.0,
        priceHydrogen = 0.0,
        priceAdblue = 0.0,
        province = "",
        referral = "",
        brandStation = brand,
        typeSale = "",
        lastUpdate = 0L,
        isFavorite = false,
    )
}
