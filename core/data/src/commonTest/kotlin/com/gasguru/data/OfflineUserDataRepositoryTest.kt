package com.gasguru.data

import app.cash.turbine.test
import com.gasguru.core.data.repository.user.OfflineUserDataRepository
import com.gasguru.core.database.model.FuelStationEntity
import com.gasguru.core.database.model.UserDataEntity
import com.gasguru.core.database.model.VehicleEntity
import com.gasguru.core.model.data.FuelType
import com.gasguru.core.model.data.PriceCategory
import com.gasguru.core.model.data.ThemeMode
import com.gasguru.core.model.data.VehicleType
import com.gasguru.data.fakes.FakeFavoriteStationDao
import com.gasguru.data.fakes.FakeUserDataDao
import com.gasguru.data.fakes.FakeVehicleDao
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest

class OfflineUserDataRepositoryTest {

    private lateinit var fakeUserDataDao: FakeUserDataDao
    private lateinit var fakeFavoriteStationDao: FakeFavoriteStationDao
    private lateinit var fakeVehicleDao: FakeVehicleDao
    private lateinit var sut: OfflineUserDataRepository

    private val principalVehicle = VehicleEntity(
        id = 1L,
        userId = 0L,
        name = "Golf VII",
        fuelType = FuelType.GASOLINE_95,
        tankCapacity = 55,
        vehicleType = VehicleType.CAR,
        isPrincipal = true,
    )

    @BeforeTest
    fun setUp() {
        fakeUserDataDao = FakeUserDataDao()
        fakeFavoriteStationDao = FakeFavoriteStationDao()
        fakeVehicleDao = FakeVehicleDao(initialVehicles = listOf(principalVehicle))
        sut = OfflineUserDataRepository(
            userDataDao = fakeUserDataDao,
            favoriteStationDao = fakeFavoriteStationDao,
            vehicleDao = fakeVehicleDao,
        )
    }

    // region userData

    @Test
    fun userData_withNoExistingData_returnsDefaultUserData() = runTest {
        sut.userData.test {
            val userData = awaitItem()
            assertEquals(false, userData.isOnboardingSuccess)
            assertEquals(ThemeMode.SYSTEM, userData.themeMode)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun userData_withExistingData_returnsMappedValues() = runTest {
        fakeUserDataDao = FakeUserDataDao(
            initialData = UserDataEntity(
                id = 0,
                lastUpdate = 1000L,
                isOnboardingSuccess = true,
                themeModeId = ThemeMode.DARK.id,
            ),
        )
        sut = OfflineUserDataRepository(
            userDataDao = fakeUserDataDao,
            favoriteStationDao = fakeFavoriteStationDao,
            vehicleDao = fakeVehicleDao,
        )

        sut.userData.test {
            val userData = awaitItem()
            assertEquals(true, userData.isOnboardingSuccess)
            assertEquals(ThemeMode.DARK, userData.themeMode)
            assertEquals(1000L, userData.lastUpdate)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun userData_combinesUserDataWithVehicles() = runTest {
        fakeUserDataDao = FakeUserDataDao(
            initialData = UserDataEntity(
                id = 0,
                lastUpdate = 0L,
                isOnboardingSuccess = false,
            ),
        )
        sut = OfflineUserDataRepository(
            userDataDao = fakeUserDataDao,
            favoriteStationDao = fakeFavoriteStationDao,
            vehicleDao = fakeVehicleDao,
        )

        sut.userData.test {
            val userData = awaitItem()
            assertEquals(1, userData.vehicles.size)
            assertEquals("Golf VII", userData.vehicles.first().name)
            cancelAndIgnoreRemainingEvents()
        }
    }

    // endregion

    // region setOnboardingComplete

    @Test
    fun setOnboardingComplete_updatesIsOnboardingSuccessToTrue() = runTest {
        sut.setOnboardingComplete()

        sut.userData.test {
            val userData = awaitItem()
            assertEquals(true, userData.isOnboardingSuccess)
            cancelAndIgnoreRemainingEvents()
        }
    }

    // endregion

    // region updateThemeMode

    @Test
    fun updateThemeMode_updatesThemeModeCorrectly() = runTest {
        sut.updateThemeMode(themeMode = ThemeMode.DARK)

        sut.userData.test {
            val userData = awaitItem()
            assertEquals(ThemeMode.DARK, userData.themeMode)
            cancelAndIgnoreRemainingEvents()
        }
    }

    // endregion

    // region addFavoriteStation / removeFavoriteStation

    @Test
    fun addFavoriteStation_delegatesToFavoriteStationDao() = runTest {
        fakeFavoriteStationDao.setStations(
            listOf(buildFuelStationEntity(idServiceStation = 1)),
        )

        sut.addFavoriteStation(stationId = 1)

        assertTrue(fakeFavoriteStationDao.isFavorite(stationId = 1))
    }

    @Test
    fun removeFavoriteStation_removesFromFavoriteStationDao() = runTest {
        fakeFavoriteStationDao.setStations(
            listOf(
                buildFuelStationEntity(idServiceStation = 1),
                buildFuelStationEntity(idServiceStation = 2),
            ),
        )

        sut.removeFavoriteStation(stationId = 1)

        assertEquals(false, fakeFavoriteStationDao.isFavorite(stationId = 1))
        assertEquals(true, fakeFavoriteStationDao.isFavorite(stationId = 2))
    }

    // endregion

    // region getFavoriteStationsWithoutDistance

    @Test
    fun getFavoriteStationsWithoutDistance_withSingleStation_returnsPriceCategoryNone() = runTest {
        fakeFavoriteStationDao.setStations(
            listOf(buildFuelStationEntity(idServiceStation = 1, priceGasoline95E5 = 1.5)),
        )

        sut.getFavoriteStationsWithoutDistance().test {
            val result = awaitItem()
            assertEquals(1, result.favoriteStations.size)
            assertEquals(PriceCategory.NONE, result.favoriteStations.first().priceCategory)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun getFavoriteStationsWithoutDistance_withMultipleStations_calculatesPriceCategories() = runTest {
        fakeFavoriteStationDao.setStations(
            listOf(
                buildFuelStationEntity(idServiceStation = 1, priceGasoline95E5 = 1.4),
                buildFuelStationEntity(idServiceStation = 2, priceGasoline95E5 = 1.7),
                buildFuelStationEntity(idServiceStation = 3, priceGasoline95E5 = 2.0),
            ),
        )

        sut.getFavoriteStationsWithoutDistance().test {
            val result = awaitItem()
            assertEquals(3, result.favoriteStations.size)
            assertEquals(PriceCategory.CHEAP, result.favoriteStations[0].priceCategory)
            assertEquals(PriceCategory.EXPENSIVE, result.favoriteStations[2].priceCategory)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun getFavoriteStationsWithoutDistance_withNoStations_returnsEmptyList() = runTest {
        sut.getFavoriteStationsWithoutDistance().test {
            val result = awaitItem()
            assertEquals(0, result.favoriteStations.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    // endregion
}

private fun buildFuelStationEntity(
    idServiceStation: Int = 0,
    priceGasoline95E5: Double = 1.5,
) = FuelStationEntity(
    bioEthanolPercentage = "",
    esterMethylPercentage = "",
    postalCode = "",
    direction = "",
    schedule = "",
    idAutonomousCommunity = "",
    idServiceStation = idServiceStation,
    idMunicipality = "",
    idProvince = "",
    latitude = 40.0,
    locality = "",
    longitudeWGS84 = -3.0,
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
    priceGasoline95E5 = priceGasoline95E5,
    priceGasoline95E5Premium = 0.0,
    priceGasoline98E10 = 0.0,
    priceGasoline98E5 = 0.0,
    priceHydrogen = 0.0,
    priceAdblue = 0.0,
    province = "",
    referral = "",
    brandStation = "REPSOL",
    typeSale = "",
    lastUpdate = 0L,
    isFavorite = true,
)
