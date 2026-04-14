package com.gasguru.core.database.converters

import com.gasguru.core.model.data.FilterType
import kotlin.test.Test
import kotlin.test.assertEquals

class FilterTypeConverterTest {

    private val converter = FilterTypeConverter()

    @Test
    fun givenFilterTypeBrand_whenConvertingFromFilterType_thenReturnsCorrectName() {
        val result = converter.fromFilterType(FilterType.BRAND)

        assertEquals(expected = "BRAND", actual = result)
    }

    @Test
    fun givenFilterTypeNearby_whenConvertingFromFilterType_thenReturnsCorrectName() {
        val result = converter.fromFilterType(FilterType.NEARBY)

        assertEquals(expected = "NEARBY", actual = result)
    }

    @Test
    fun givenFilterTypeSchedule_whenConvertingFromFilterType_thenReturnsCorrectName() {
        val result = converter.fromFilterType(FilterType.SCHEDULE)

        assertEquals(expected = "SCHEDULE", actual = result)
    }

    @Test
    fun givenBrandString_whenConvertingToFilterType_thenReturnsBrandEnum() {
        val result = converter.toFilterType("BRAND")

        assertEquals(expected = FilterType.BRAND, actual = result)
    }

    @Test
    fun givenNearbyString_whenConvertingToFilterType_thenReturnsNearbyEnum() {
        val result = converter.toFilterType("NEARBY")

        assertEquals(expected = FilterType.NEARBY, actual = result)
    }

    @Test
    fun givenScheduleString_whenConvertingToFilterType_thenReturnsScheduleEnum() {
        val result = converter.toFilterType("SCHEDULE")

        assertEquals(expected = FilterType.SCHEDULE, actual = result)
    }

    @Test
    fun givenFilterTypeBrand_whenRoundtrip_thenOriginalValueIsRecovered() {
        val original = FilterType.BRAND

        val encoded = converter.fromFilterType(original)
        val decoded = converter.toFilterType(encoded)

        assertEquals(expected = original, actual = decoded)
    }

    @Test
    fun givenFilterTypeNearby_whenRoundtrip_thenOriginalValueIsRecovered() {
        val original = FilterType.NEARBY

        val encoded = converter.fromFilterType(original)
        val decoded = converter.toFilterType(encoded)

        assertEquals(expected = original, actual = decoded)
    }

    @Test
    fun givenFilterTypeSchedule_whenRoundtrip_thenOriginalValueIsRecovered() {
        val original = FilterType.SCHEDULE

        val encoded = converter.fromFilterType(original)
        val decoded = converter.toFilterType(encoded)

        assertEquals(expected = original, actual = decoded)
    }

    @Test
    fun givenAllFilterTypeValues_whenConvertingFromFilterType_thenNameMatchesEnumName() {
        FilterType.entries.forEach { filterType ->
            val result = converter.fromFilterType(filterType)
            assertEquals(expected = filterType.name, actual = result)
        }
    }
}