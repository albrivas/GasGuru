package com.gasguru.core.ui.mapper

import com.gasguru.core.model.data.FuelType
import com.gasguru.core.model.data.Vehicle
import com.gasguru.core.model.data.VehicleType
import com.gasguru.core.ui.models.VehicleTypeUiModel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class VehicleUiMapperTest {

    @Test
    fun givenVehicleWithName_whenMappingToVehicleItemCardModel_thenFieldsAreMapped() {
        val vehicle = Vehicle(
            id = 1L,
            userId = 0L,
            name = "Golf VIII",
            fuelType = FuelType.GASOLINE_95,
            tankCapacity = 55,
            vehicleType = VehicleType.CAR,
            isPrincipal = true,
        )

        val result = vehicle.toVehicleItemCardModel(isSelected = true)

        assertEquals(vehicle.id, result.id)
        assertEquals(vehicle.name, result.name)
        assertEquals(vehicle.tankCapacity, result.tankCapacityLitres)
        assertEquals(true, result.isSelected)
    }

    @Test
    fun givenVehicleWithoutName_whenMappingToVehicleItemCardModel_thenNameIsNull() {
        val vehicle = Vehicle(
            id = 2L,
            userId = 0L,
            name = null,
            fuelType = FuelType.DIESEL,
            tankCapacity = 40,
            vehicleType = VehicleType.MOTORCYCLE,
            isPrincipal = false,
        )

        val result = vehicle.toVehicleItemCardModel(isSelected = false)

        assertNull(result.name)
        assertEquals(false, result.isSelected)
    }

    @Test
    fun givenEachVehicleType_whenMappingToVehicleItemCardModel_thenIconIsResolved() {
        VehicleType.entries.forEach { vehicleType ->
            val vehicle = Vehicle(
                id = 3L,
                userId = 0L,
                name = null,
                fuelType = FuelType.GASOLINE_95,
                tankCapacity = 40,
                vehicleType = vehicleType,
                isPrincipal = false,
            )

            val result = vehicle.toVehicleItemCardModel(isSelected = false)

            assertEquals(
                VehicleTypeUiModel.ALL_TYPES.first { it.type == vehicleType }.iconRes,
                result.vehicleTypeIconRes,
            )
        }
    }
}
