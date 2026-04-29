package com.gasguru.core.ui.mapper

import com.gasguru.core.model.data.FuelStationBrandsType
import com.gasguru.core.model.data.previewFuelStationDomain
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class FuelStationUiMapperTest {

    @Test
    fun givenFuelStation_whenMappingToUiModel_thenBrandIconIsSet() {
        val station = previewFuelStationDomain().copy(
            brandStationBrandsType = FuelStationBrandsType.REPSOL,
        )
        val result = station.toUiModel()
        assertNotNull(result.brandIcon)
    }

    @Test
    fun givenFuelStationWithDistance_whenMappingToUiModel_thenFormattedDistanceIsSet() {
        val station = previewFuelStationDomain().copy(
            distance = 1500f,
        )
        val result = station.toUiModel()
        assertEquals("2 Km", result.formattedDistance)
    }

    @Test
    fun givenFuelStationWithLowercaseName_whenMappingToUiModel_thenFormattedNameIsSet() {
        val station = previewFuelStationDomain().copy(
            brandStationName = "gasolinera repsol",
        )
        val result = station.toUiModel()
        assertEquals("Gasolinera repsol", result.formattedName)
    }

    @Test
    fun givenUnknownBrand_whenMappingToUiModel_thenUnknownIconIsUsed() {
        val station = previewFuelStationDomain().copy(
            brandStationBrandsType = FuelStationBrandsType.UNKNOWN,
        )
        val result = station.toUiModel()
        assertNotNull(result.brandIcon)
    }
}
