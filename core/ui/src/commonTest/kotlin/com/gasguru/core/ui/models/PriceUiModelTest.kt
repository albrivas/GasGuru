package com.gasguru.core.ui.models

import com.gasguru.core.model.data.FuelType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PriceUiModelTest {

    @Test
    fun givenPriceAboveZero_whenCheckingHasPrice_thenReturnsTrue() {
        val model = PriceUiModel(rawPrice = 1.234, fuelType = FuelType.GASOLINE_95)
        assertTrue(model.hasPrice)
    }

    @Test
    fun givenZeroPrice_whenCheckingHasPrice_thenReturnsFalse() {
        val model = PriceUiModel(rawPrice = 0.0, fuelType = FuelType.GASOLINE_95)
        assertFalse(model.hasPrice)
    }

    @Test
    fun givenPrice1234_whenFormattingPrice_thenReturns1234Format() {
        val model = PriceUiModel(rawPrice = 1.234, fuelType = FuelType.GASOLINE_95)
        assertEquals("1.234 €/l", model.formattedPrice)
    }

    @Test
    fun givenPrice05_whenFormattingPrice_thenReturns0500Format() {
        val model = PriceUiModel(rawPrice = 0.5, fuelType = FuelType.GASOLINE_95)
        assertEquals("0.500 €/l", model.formattedPrice)
    }

    @Test
    fun givenPrice1999_whenFormattingPrice_thenReturns1999Format() {
        val model = PriceUiModel(rawPrice = 1.999, fuelType = FuelType.GASOLINE_95)
        assertEquals("1.999 €/l", model.formattedPrice)
    }

    @Test
    fun givenPrice20_whenFormattingPrice_thenReturns2000Format() {
        val model = PriceUiModel(rawPrice = 2.0, fuelType = FuelType.GASOLINE_95)
        assertEquals("2.000 €/l", model.formattedPrice)
    }

    @Test
    fun givenPrice14555_whenFormattingPrice_thenRoundsTo3Decimals() {
        val model = PriceUiModel(rawPrice = 1.4555, fuelType = FuelType.GASOLINE_95)
        assertEquals("1.456 €/l", model.formattedPrice)
    }

    @Test
    fun givenZeroPrice_whenFormattingPrice_thenReturns0000WithoutUnit() {
        val model = PriceUiModel(rawPrice = 0.0, fuelType = FuelType.GASOLINE_95)
        assertEquals("0.000", model.formattedPrice)
    }
}
