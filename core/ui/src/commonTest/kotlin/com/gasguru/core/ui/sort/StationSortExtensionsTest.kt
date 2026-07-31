package com.gasguru.core.ui.sort

import com.gasguru.core.model.data.FuelType
import com.gasguru.core.model.data.previewFuelStationDomain
import com.gasguru.core.ui.mapper.toUiModel
import com.gasguru.core.ui.models.FuelStationUiModel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class StationSortExtensionsTest {

    private val cheapAndFarStation = previewFuelStationDomain(idServiceStation = 1).copy(
        priceGasoline95E5 = 1.20,
        distance = 1000f,
    )
    private val expensiveAndNearStation = previewFuelStationDomain(idServiceStation = 2).copy(
        priceGasoline95E5 = 1.50,
        distance = 500f,
    )
    private val stations = listOf(expensiveAndNearStation, cheapAndFarStation).map { it.toUiModel() }

    @Test
    fun givenStationsUnordered_whenSortedByCriteriaPrice_thenCheapestStationComesFirst() {
        val result = stations.sortedByCriteria(criteria = StationSortCriteria.PRICE, fuelType = FuelType.GASOLINE_95)

        assertEquals(listOf(1, 2), result.map { it.fuelStation.idServiceStation })
    }

    @Test
    fun givenStationsUnordered_whenSortedByCriteriaDistance_thenNearestStationComesFirst() {
        val result = stations.sortedByCriteria(criteria = StationSortCriteria.DISTANCE, fuelType = FuelType.GASOLINE_95)

        assertEquals(listOf(2, 1), result.map { it.fuelStation.idServiceStation })
    }

    @Test
    fun givenStationsUnordered_whenSortedByCriteriaNone_thenOriginalOrderIsPreserved() {
        val result = stations.sortedByCriteria(criteria = StationSortCriteria.NONE, fuelType = FuelType.GASOLINE_95)

        assertEquals(stations.map { it.fuelStation.idServiceStation }, result.map { it.fuelStation.idServiceStation })
    }

    @Test
    fun givenEmptyList_whenSortedByCriteria_thenResultIsEmpty() {
        val result = emptyList<FuelStationUiModel>()
            .sortedByCriteria(criteria = StationSortCriteria.PRICE, fuelType = FuelType.GASOLINE_95)

        assertTrue(result.isEmpty())
    }

    @Test
    fun givenFuelStationDomainList_whenSortedByCriteriaPrice_thenCheapestStationComesFirst() {
        val result = listOf(expensiveAndNearStation, cheapAndFarStation)
            .sortedByCriteria(criteria = StationSortCriteria.PRICE, fuelType = FuelType.GASOLINE_95)

        assertEquals(listOf(1, 2), result.map { it.idServiceStation })
    }

    @Test
    fun givenFuelStationDomainList_whenSortedByCriteriaDistance_thenNearestStationComesFirst() {
        val result = listOf(expensiveAndNearStation, cheapAndFarStation)
            .sortedByCriteria(criteria = StationSortCriteria.DISTANCE, fuelType = FuelType.GASOLINE_95)

        assertEquals(listOf(2, 1), result.map { it.idServiceStation })
    }
}
