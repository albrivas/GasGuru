package com.gasguru.core.model.data

import kotlin.test.Test
import kotlin.test.assertEquals

class FuelStationTest {

    private fun buildStation(
        distance: Float = 0f,
        direction: String = "",
        brandStationName: String = "",
    ) = FuelStation(
        bioEthanolPercentage = "",
        esterMethylPercentage = "",
        postalCode = "",
        direction = direction,
        schedule = "",
        idAutonomousCommunity = "",
        idServiceStation = 0,
        idMunicipality = "",
        idProvince = "",
        location = LatLng(latitude = 0.0, longitude = 0.0),
        locality = "",
        margin = "",
        municipality = "",
        priceGasoilA = 0.0,
        priceGasoilB = 0.0,
        priceGasoilPremium = 0.0,
        priceGasoline95E10 = 0.0,
        priceGasoline95E5 = 0.0,
        priceGasoline95E5Premium = 0.0,
        priceGasoline98E10 = 0.0,
        priceGasoline98E5 = 0.0,
        priceHydrogen = 0.0,
        priceAdblue = 0.0,
        province = "",
        referral = "",
        brandStationName = brandStationName,
        brandStationBrandsType = FuelStationBrandsType.UNKNOWN,
        typeSale = "",
        distance = distance,
    )

    // --- formatDistance ---

    @Test
    fun `GIVEN distance above 1000m WHEN formatDistance THEN returns kilometers with 2 decimals`() {
        val station = buildStation(distance = 1500f)
        assertEquals(expected = "1.50 Km", actual = station.formatDistance())
    }

    @Test
    fun `GIVEN distance exactly 1000m WHEN formatDistance THEN returns 1 00 Km`() {
        val station = buildStation(distance = 1000f)
        assertEquals(expected = "1.00 Km", actual = station.formatDistance())
    }

    @Test
    fun `GIVEN distance as whole number below 1000m WHEN formatDistance THEN returns meters without decimals`() {
        val station = buildStation(distance = 250f)
        assertEquals(expected = "250 m", actual = station.formatDistance())
    }

    @Test
    fun `GIVEN distance with decimals below 1000m WHEN formatDistance THEN returns meters with 2 decimals`() {
        val station = buildStation(distance = 123.45f)
        assertEquals(expected = "123.45 m", actual = station.formatDistance())
    }

    @Test
    fun `GIVEN distance of zero WHEN formatDistance THEN returns 0 m`() {
        val station = buildStation(distance = 0f)
        assertEquals(expected = "0 m", actual = station.formatDistance())
    }

    // --- formatDirection ---

    @Test
    fun `GIVEN uppercase direction WHEN formatDirection THEN first char is uppercase rest is lowercase`() {
        val station = buildStation(direction = "C/RIOS ROSAS - MADRID")
        assertEquals(expected = "C/rios rosas - madrid", actual = station.formatDirection())
    }

    @Test
    fun `GIVEN already lowercase direction WHEN formatDirection THEN first char becomes uppercase`() {
        val station = buildStation(direction = "avenida de la paz")
        assertEquals(expected = "Avenida de la paz", actual = station.formatDirection())
    }

    @Test
    fun `GIVEN empty direction WHEN formatDirection THEN returns empty string`() {
        val station = buildStation(direction = "")
        assertEquals(expected = "", actual = station.formatDirection())
    }

    // --- formatName ---

    @Test
    fun `GIVEN uppercase brand name WHEN formatName THEN result is title cased`() {
        val station = buildStation(brandStationName = "REPSOL")
        assertEquals(expected = "Repsol", actual = station.formatName())
    }

    @Test
    fun `GIVEN mixed case brand name WHEN formatName THEN first char uppercase rest lowercase`() {
        val station = buildStation(brandStationName = "BP STATION")
        assertEquals(expected = "Bp station", actual = station.formatName())
    }

    @Test
    fun `GIVEN empty brand name WHEN formatName THEN returns empty string`() {
        val station = buildStation(brandStationName = "")
        assertEquals(expected = "", actual = station.formatName())
    }
}
