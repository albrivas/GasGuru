package com.gasguru.feature.station_map.ui

import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals

class SheetContentMaxHeightTest {

    private val unitDensity = Density(density = 1f)

    @Test
    fun `GIVEN overlay not measured yet WHEN calculating max height THEN height is unspecified`() {
        val maxHeight = calculateSheetContentMaxHeight(
            mapContainerTopPx = 0f,
            mapContainerHeightPx = 800,
            topOverlayBottomPx = 0f,
            dragHandleHeightPx = 20,
            density = unitDensity,
        )

        assertEquals(expected = Dp.Unspecified, actual = maxHeight)
    }

    @Test
    fun `GIVEN map container not measured yet WHEN calculating max height THEN height is unspecified`() {
        val maxHeight = calculateSheetContentMaxHeight(
            mapContainerTopPx = 0f,
            mapContainerHeightPx = 0,
            topOverlayBottomPx = 120f,
            dragHandleHeightPx = 20,
            density = unitDensity,
        )

        assertEquals(expected = Dp.Unspecified, actual = maxHeight)
    }

    @Test
    fun `GIVEN measured overlay and drag handle WHEN calculating max height THEN sheet ends below the overlay with the gap`() {
        val maxHeight = calculateSheetContentMaxHeight(
            mapContainerTopPx = 0f,
            mapContainerHeightPx = 800,
            topOverlayBottomPx = 120f,
            dragHandleHeightPx = 20,
            density = unitDensity,
        )

        assertEquals(expected = 652.dp, actual = maxHeight)
    }

    @Test
    fun `GIVEN map container offset in root WHEN calculating max height THEN overlay bottom is relative to the container`() {
        val maxHeight = calculateSheetContentMaxHeight(
            mapContainerTopPx = 50f,
            mapContainerHeightPx = 800,
            topOverlayBottomPx = 170f,
            dragHandleHeightPx = 20,
            density = unitDensity,
        )

        assertEquals(expected = 652.dp, actual = maxHeight)
    }

    @Test
    fun `GIVEN density greater than one WHEN calculating max height THEN pixels are converted to dp`() {
        val maxHeight = calculateSheetContentMaxHeight(
            mapContainerTopPx = 0f,
            mapContainerHeightPx = 1600,
            topOverlayBottomPx = 240f,
            dragHandleHeightPx = 40,
            density = Density(density = 2f),
        )

        assertEquals(expected = 652.dp, actual = maxHeight)
    }

    @Test
    fun `GIVEN overlay taller than available space WHEN calculating max height THEN height is never negative`() {
        val maxHeight = calculateSheetContentMaxHeight(
            mapContainerTopPx = 0f,
            mapContainerHeightPx = 100,
            topOverlayBottomPx = 120f,
            dragHandleHeightPx = 20,
            density = unitDensity,
        )

        assertEquals(expected = 0.dp, actual = maxHeight)
    }
}
