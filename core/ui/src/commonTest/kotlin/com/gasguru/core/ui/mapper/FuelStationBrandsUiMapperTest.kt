package com.gasguru.core.ui.mapper

import com.gasguru.core.model.data.FuelStationBrandsType
import com.gasguru.core.ui.models.FuelStationBrandsUiModel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class FuelStationBrandsUiMapperTest {

    @Test
    fun givenRepsolBrand_whenMappingToUiModel_thenReturnsCorrectIcon() {
        val result = FuelStationBrandsType.REPSOL.toUiModel()
        assertEquals(FuelStationBrandsType.REPSOL, result.type)
        assertNotNull(result.iconRes)
    }

    @Test
    fun givenUnknownBrand_whenMappingToUiModel_thenReturnsUnknownIcon() {
        val result = FuelStationBrandsType.UNKNOWN.toUiModel()
        assertEquals(FuelStationBrandsType.UNKNOWN, result.type)
        assertNotNull(result.iconRes)
    }

    @Test
    fun givenAllBrandsCompanion_whenCheckingExhaustiveness_thenAllBrandsTypesAreCovered() {
        val allTypes = FuelStationBrandsType.entries
        val allUiModels = FuelStationBrandsUiModel.ALL_BRANDS
        val coveredTypes = allUiModels.map { it.type }.toSet()
        val missingTypes = allTypes.filter { it !in coveredTypes }
        assertEquals(
            emptyList(),
            missingTypes,
            "Missing brand types in ALL_BRANDS: $missingTypes"
        )
    }

    @Test
    fun givenEachBrandType_whenMappingToUiModel_thenFindsEntry() {
        FuelStationBrandsType.entries.forEach { brandType ->
            val result = brandType.toUiModel()
            assertEquals(brandType, result.type)
        }
    }
}
