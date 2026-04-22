package com.gasguru.core.domain.alerts

import com.gasguru.core.domain.fakes.FakePriceAlertRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AddPriceAlertUseCaseTest {

    private lateinit var sut: AddPriceAlertUseCase
    private lateinit var fakePriceAlertRepository: FakePriceAlertRepository

    @BeforeTest
    fun setUp() {
        fakePriceAlertRepository = FakePriceAlertRepository()
        sut = AddPriceAlertUseCase(priceAlertRepository = fakePriceAlertRepository)
    }

    @Test
    fun addsAlertWithCorrectStationIdAndPrice() = runTest {
        sut(stationId = 42, lastNotifiedPrice = 1.85)

        assertEquals(1, fakePriceAlertRepository.addedAlerts.size)
        assertEquals(42, fakePriceAlertRepository.addedAlerts.first().first)
        assertEquals(1.85, fakePriceAlertRepository.addedAlerts.first().second)
    }

    @Test
    fun addingMultipleAlertsTracksAll() = runTest {
        sut(stationId = 1, lastNotifiedPrice = 1.50)
        sut(stationId = 2, lastNotifiedPrice = 1.75)

        assertEquals(2, fakePriceAlertRepository.addedAlerts.size)
    }

    @Test
    fun removedAlertsListRemainsEmpty() = runTest {
        sut(stationId = 10, lastNotifiedPrice = 1.60)

        assertTrue(fakePriceAlertRepository.removedAlerts.isEmpty())
    }
}
