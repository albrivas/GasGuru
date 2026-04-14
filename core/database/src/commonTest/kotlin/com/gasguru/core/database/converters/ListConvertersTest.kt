package com.gasguru.core.database.converters

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ListConvertersTest {

    private val converter = ListConverters()

    @Test
    fun givenNonEmptyList_whenConvertingToStringAndBack_thenOriginalListIsRecovered() {
        val originalList = listOf("Madrid", "Barcelona", "Sevilla")

        val encoded = converter.fromList(originalList)
        val decoded = converter.toList(encoded)

        assertEquals(
            expected = originalList,
            actual = decoded,
        )
    }

    @Test
    fun givenEmptyList_whenConvertingToStringAndBack_thenEmptyListIsReturned() {
        val emptyList = emptyList<String>()

        val encoded = converter.fromList(emptyList)
        val decoded = converter.toList(encoded)

        assertTrue(decoded.isEmpty())
    }

    @Test
    fun givenSingleElementList_whenConvertingToStringAndBack_thenOriginalListIsRecovered() {
        val singleItemList = listOf("Repsol")

        val encoded = converter.fromList(singleItemList)
        val decoded = converter.toList(encoded)

        assertEquals(
            expected = singleItemList,
            actual = decoded,
        )
    }

    @Test
    fun givenMalformedJsonString_whenConvertingToList_thenEmptyListIsReturned() {
        val malformedJson = "not a valid json"

        val decoded = converter.toList(malformedJson)

        assertTrue(decoded.isEmpty())
    }

    @Test
    fun givenJsonProducedByMoshi_whenConvertingToList_thenListIsCorrectlyParsed() {
        // Moshi produces: ["a","b","c"] — same format as kotlinx-serialization
        val moshiStyleJson = """["Madrid","Barcelona","Sevilla"]"""

        val decoded = converter.toList(moshiStyleJson)

        assertEquals(
            expected = listOf("Madrid", "Barcelona", "Sevilla"),
            actual = decoded,
        )
    }

    @Test
    fun givenList_whenEncodedToJson_thenFormatMatchesMoshiOutput() {
        val list = listOf("a", "b", "c")

        val encoded = converter.fromList(list)

        // Verify the JSON format is identical to what Moshi would produce
        assertEquals(
            expected = """["a","b","c"]""",
            actual = encoded,
        )
    }
}
