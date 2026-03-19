package com.gasguru.core.common

import com.gasguru.core.model.data.FuelStation
import com.gasguru.core.model.data.FuelStationBrandsType
import com.gasguru.core.model.data.LatLng
import com.gasguru.core.common.CommonUtils.isStationOpen
import io.kotest.matchers.shouldBe
import kotlinx.datetime.LocalDateTime
import kotlin.test.Test

class CommonUtilsTest {

    private fun buildStation(schedule: String) = FuelStation(
        bioEthanolPercentage = "",
        esterMethylPercentage = "",
        postalCode = "",
        direction = "",
        schedule = schedule,
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
        brandStationName = "",
        brandStationBrandsType = FuelStationBrandsType.UNKNOWN,
        typeSale = "",
        distance = 0f,
    )

    // Monday 10:00
    private val mondayMorning = LocalDateTime(year = 2024, monthNumber = 1, dayOfMonth = 1, hour = 10, minute = 0)

    // Saturday 10:00
    private val saturdayMorning = LocalDateTime(year = 2024, monthNumber = 1, dayOfMonth = 6, hour = 10, minute = 0)

    // Sunday 10:00
    private val sundayMorning = LocalDateTime(year = 2024, monthNumber = 1, dayOfMonth = 7, hour = 10, minute = 0)

    // Monday 22:00
    private val mondayNight = LocalDateTime(year = 2024, monthNumber = 1, dayOfMonth = 1, hour = 22, minute = 0)

    // --- 24h ---

    @Test
    fun `GIVEN schedule L-D 24H WHEN isStationOpen THEN always returns true`() {
        buildStation(schedule = "L-D: 24H").isStationOpen(now = mondayMorning) shouldBe true
    }

    @Test
    fun `GIVEN schedule L-D 24H lowercase WHEN isStationOpen THEN returns true`() {
        buildStation(schedule = "l-d: 24h").isStationOpen(now = mondayMorning) shouldBe true
    }

    // --- L-V schedule ---

    @Test
    fun `GIVEN L-V schedule WHEN weekday inside hours THEN returns true`() {
        buildStation(schedule = "L-V: 08:00-20:00").isStationOpen(now = mondayMorning) shouldBe true
    }

    @Test
    fun `GIVEN L-V schedule WHEN Saturday THEN returns false`() {
        buildStation(schedule = "L-V: 08:00-20:00").isStationOpen(now = saturdayMorning) shouldBe false
    }

    @Test
    fun `GIVEN L-V schedule WHEN Sunday THEN returns false`() {
        buildStation(schedule = "L-V: 08:00-20:00").isStationOpen(now = sundayMorning) shouldBe false
    }

    @Test
    fun `GIVEN L-V schedule WHEN weekday outside hours THEN returns false`() {
        buildStation(schedule = "L-V: 08:00-20:00").isStationOpen(now = mondayNight) shouldBe false
    }

    // --- L-S schedule ---

    @Test
    fun `GIVEN L-S schedule WHEN Saturday THEN returns true`() {
        buildStation(schedule = "L-S: 08:00-20:00").isStationOpen(now = saturdayMorning) shouldBe true
    }

    @Test
    fun `GIVEN L-S schedule WHEN Sunday THEN returns false`() {
        buildStation(schedule = "L-S: 08:00-20:00").isStationOpen(now = sundayMorning) shouldBe false
    }

    // --- L-D schedule (explicit range, not 24H) ---

    @Test
    fun `GIVEN L-D schedule WHEN Sunday inside hours THEN returns true`() {
        buildStation(schedule = "L-D: 08:00-22:00").isStationOpen(now = sundayMorning) shouldBe true
    }

    // --- Multiple parts separated by semicolon ---

    @Test
    fun `GIVEN multi-part schedule WHEN time matches second part THEN returns true`() {
        val schedule = "L-V: 08:00-14:00;L-V: 16:00-20:00"
        val afternoonMonday = LocalDateTime(year = 2024, monthNumber = 1, dayOfMonth = 1, hour = 17, minute = 0)
        buildStation(schedule = schedule).isStationOpen(now = afternoonMonday) shouldBe true
    }

    @Test
    fun `GIVEN multi-part schedule WHEN time is between parts THEN returns false`() {
        val schedule = "L-V: 08:00-14:00;L-V: 16:00-20:00"
        val midday = LocalDateTime(year = 2024, monthNumber = 1, dayOfMonth = 1, hour = 15, minute = 0)
        buildStation(schedule = schedule).isStationOpen(now = midday) shouldBe false
    }

    // --- Empty / malformed schedule ---

    @Test
    fun `GIVEN empty schedule WHEN isStationOpen THEN returns false`() {
        buildStation(schedule = "").isStationOpen(now = mondayMorning) shouldBe false
    }

    @Test
    fun `GIVEN malformed schedule WHEN isStationOpen THEN returns false`() {
        buildStation(schedule = "INVALID").isStationOpen(now = mondayMorning) shouldBe false
    }
}
