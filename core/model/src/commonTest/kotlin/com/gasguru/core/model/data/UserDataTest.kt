package com.gasguru.core.model.data

import io.kotest.matchers.shouldBe
import kotlin.test.Test

class UserDataTest {

    private fun buildVehicle(
        id: Long,
        isPrincipal: Boolean,
    ) = Vehicle(
        id = id,
        userId = 0L,
        name = null,
        fuelType = FuelType.GASOLINE_95,
        tankCapacity = 40,
        vehicleType = VehicleType.CAR,
        isPrincipal = isPrincipal,
    )

    // --- principalVehicleOrNull ---

    @Test
    fun `GIVEN no vehicles WHEN principalVehicleOrNull THEN returns null`() {
        UserData(vehicles = emptyList()).principalVehicleOrNull() shouldBe null
    }

    @Test
    fun `GIVEN a single non principal vehicle WHEN principalVehicleOrNull THEN returns that vehicle as fallback`() {
        val onlyVehicle = buildVehicle(id = 1L, isPrincipal = false)

        UserData(vehicles = listOf(onlyVehicle)).principalVehicleOrNull() shouldBe onlyVehicle
    }

    @Test
    fun `GIVEN the principal vehicle is not the first in the list WHEN principalVehicleOrNull THEN returns the vehicle marked as principal`() {
        val firstVehicle = buildVehicle(id = 1L, isPrincipal = false)
        val principalVehicle = buildVehicle(id = 2L, isPrincipal = true)

        UserData(vehicles = listOf(firstVehicle, principalVehicle)).principalVehicleOrNull() shouldBe principalVehicle
    }

    @Test
    fun `GIVEN multiple vehicles marked as principal WHEN principalVehicleOrNull THEN returns the first one marked as principal`() {
        val firstPrincipalVehicle = buildVehicle(id = 1L, isPrincipal = true)
        val secondPrincipalVehicle = buildVehicle(id = 2L, isPrincipal = true)

        UserData(
            vehicles = listOf(firstPrincipalVehicle, secondPrincipalVehicle),
        ).principalVehicleOrNull() shouldBe firstPrincipalVehicle
    }
}
