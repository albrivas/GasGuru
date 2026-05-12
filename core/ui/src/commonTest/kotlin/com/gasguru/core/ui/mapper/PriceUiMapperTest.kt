package com.gasguru.core.ui.mapper

import com.gasguru.core.model.data.FuelType
import com.gasguru.core.model.data.previewFuelStationDomain
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PriceUiMapperTest {

    private val stationWithPrice = previewFuelStationDomain().copy(
        priceGasoline95E5 = 1.539,
        priceGasoilA = 1.459,
    )

    @Test
    fun givenGasoline95WithPrice_whenMappingToPriceUiModel_thenHasPriceIsTrue() {
        val result = FuelType.GASOLINE_95.toPriceUiModel(fuelStation = stationWithPrice)
        assertTrue(result.hasPrice)
        assertEquals(FuelType.GASOLINE_95, result.fuelType)
    }

    @Test
    fun givenGasoline95WithPrice_whenMappingToPriceUiModel_thenRawPriceIsCorrect() {
        val result = FuelType.GASOLINE_95.toPriceUiModel(fuelStation = stationWithPrice)
        assertEquals(1.539, result.rawPrice)
    }

    @Test
    fun givenFuelTypeWithZeroPrice_whenMappingToPriceUiModel_thenHasPriceIsFalse() {
        val stationNoPrice = previewFuelStationDomain().copy(priceGasoline98E5 = 0.0)
        val result = FuelType.GASOLINE_98.toPriceUiModel(fuelStation = stationNoPrice)
        assertFalse(result.hasPrice)
    }

    @Test
    fun givenDieselWithPrice_whenMappingToPriceUiModel_thenFormattedPriceIsCorrect() {
        val result = FuelType.DIESEL.toPriceUiModel(fuelStation = stationWithPrice)
        assertTrue(result.hasPrice)
        assertEquals("1.459 €/l", result.formattedPrice)
    }
}
