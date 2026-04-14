package com.gasguru.core.model.data

import io.kotest.matchers.shouldBe
import kotlin.test.Test

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
    fun `GIVEN distance above 1000m WHEN formatDistance THEN returns kilometers rounded up without decimals`() {
        buildStation(distance = 1500f).formatDistance() shouldBe "2 Km"
    }

    @Test
    fun `GIVEN distance exactly 1000m WHEN formatDistance THEN returns 1 Km`() {
        buildStation(distance = 1000f).formatDistance() shouldBe "1 Km"
    }

    @Test
    fun `GIVEN distance with fractional km WHEN formatDistance THEN rounds up to next km`() {
        buildStation(distance = 1234f).formatDistance() shouldBe "2 Km"
    }

    @Test
    fun `GIVEN whole number distance below 1000m WHEN formatDistance THEN returns meters without decimals`() {
        buildStation(distance = 250f).formatDistance() shouldBe "250 m"
    }

    @Test
    fun `GIVEN decimal distance below 1000m WHEN formatDistance THEN returns meters rounded up`() {
        buildStation(distance = 123.45f).formatDistance() shouldBe "124 m"
    }

    @Test
    fun `GIVEN distance of zero WHEN formatDistance THEN returns 0 m`() {
        buildStation(distance = 0f).formatDistance() shouldBe "0 m"
    }

    // --- formatDirection ---

    @Test
    fun `GIVEN uppercase direction WHEN formatDirection THEN first char uppercase rest lowercase`() {
        buildStation(direction = "C/RIOS ROSAS - MADRID").formatDirection() shouldBe "C/rios rosas - madrid"
    }

    @Test
    fun `GIVEN lowercase direction WHEN formatDirection THEN first char becomes uppercase`() {
        buildStation(direction = "avenida de la paz").formatDirection() shouldBe "Avenida de la paz"
    }

    @Test
    fun `GIVEN empty direction WHEN formatDirection THEN returns empty string`() {
        buildStation(direction = "").formatDirection() shouldBe ""
    }

    // --- formatName ---

    @Test
    fun `GIVEN uppercase brand name WHEN formatName THEN result is title cased`() {
        buildStation(brandStationName = "REPSOL").formatName() shouldBe "Repsol"
    }

    @Test
    fun `GIVEN mixed case brand name WHEN formatName THEN first char uppercase rest lowercase`() {
        buildStation(brandStationName = "BP STATION").formatName() shouldBe "Bp station"
    }

    @Test
    fun `GIVEN empty brand name WHEN formatName THEN returns empty string`() {
        buildStation(brandStationName = "").formatName() shouldBe ""
    }
}
