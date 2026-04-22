package com.gasguru.core.domain.alerts

import com.gasguru.core.domain.fakes.FakePriceAlertRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RemovePriceAlertUseCaseTest {

    private lateinit var sut: RemovePriceAlertUseCase
    private lateinit var fakePriceAlertRepository: FakePriceAlertRepository

    @BeforeTest
    fun setUp() {
        fakePriceAlertRepository = FakePriceAlertRepository()
        sut = RemovePriceAlertUseCase(priceAlertRepository = fakePriceAlertRepository)
    }

    @Test
    fun removesAlertByStationId() = runTest {
        sut(stationId = 42)

        assertEquals(1, fakePriceAlertRepository.removedAlerts.size)
        assertEquals(42, fakePriceAlertRepository.removedAlerts.first())
    }

    @Test
    fun removingMultipleAlertsTracksAll() = runTest {
        sut(stationId = 1)
        sut(stationId = 2)

        assertEquals(2, fakePriceAlertRepository.removedAlerts.size)
    }

    @Test
    fun addedAlertsListRemainsEmpty() = runTest {
        sut(stationId = 5)

        assertTrue(fakePriceAlertRepository.addedAlerts.isEmpty())
    }
}
