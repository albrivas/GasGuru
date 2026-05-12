package com.gasguru.core.ui.mapper

import com.gasguru.core.model.data.FuelType
import com.gasguru.core.ui.models.FuelTypeUiModel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class FuelTypeUiMapperTest {

    @Test
    fun givenGasoline95_whenMappingToUiModel_thenReturnsMatchingEntry() {
        val result = FuelType.GASOLINE_95.toUiModel()
        assertEquals(FuelType.GASOLINE_95, result.type)
        assertNotNull(result.iconRes)
        assertNotNull(result.translationRes)
        assertNotNull(result.noPriceRes)
    }

    @Test
    fun givenDiesel_whenMappingToUiModel_thenReturnsMatchingEntry() {
        val result = FuelType.DIESEL.toUiModel()
        assertEquals(FuelType.DIESEL, result.type)
        assertNotNull(result.iconRes)
    }

    @Test
    fun givenAllFuelsCompanion_whenCheckingCount_thenMatchesExpected() {
        assertEquals(9, FuelTypeUiModel.ALL_FUELS.size)
    }

    @Test
    fun givenEachFuelType_whenMappingToUiModel_thenFindsEntry() {
        val supportedTypes = FuelTypeUiModel.ALL_FUELS.map { it.type }.toSet()
        supportedTypes.forEach { fuelType ->
            val result = fuelType.toUiModel()
            assertEquals(fuelType, result.type)
        }
    }

    @Test
    fun givenAllFuels_whenCheckingUniqueness_thenNoDuplicateTypes() {
        val types = FuelTypeUiModel.ALL_FUELS.map { it.type }
        assertEquals(types.size, types.toSet().size, "Duplicate FuelType entries in ALL_FUELS")
    }
}
