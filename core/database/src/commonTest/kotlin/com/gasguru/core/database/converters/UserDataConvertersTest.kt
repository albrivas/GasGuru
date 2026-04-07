package com.gasguru.core.database.converters

import com.gasguru.core.model.data.FuelType
import com.gasguru.core.model.data.ThemeMode
import com.gasguru.core.model.data.VehicleType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

class UserDataConvertersTest {

    private val sut = UserDataConverters()

    @Test
    fun givenVehicleType_whenFromVehicleTypeIsCalled_thenReturnsEnumNameAsString() {
        assertEquals("CAR", sut.fromVehicleType(vehicleType = VehicleType.CAR))
        assertEquals("MOTORCYCLE", sut.fromVehicleType(vehicleType = VehicleType.MOTORCYCLE))
        assertEquals("VAN", sut.fromVehicleType(vehicleType = VehicleType.VAN))
        assertEquals("TRUCK", sut.fromVehicleType(vehicleType = VehicleType.TRUCK))
    }

    @Test
    fun givenVehicleTypeName_whenToVehicleTypeIsCalled_thenReturnsCorrectEnum() {
        assertEquals(VehicleType.CAR, sut.toVehicleType(value = "CAR"))
        assertEquals(VehicleType.MOTORCYCLE, sut.toVehicleType(value = "MOTORCYCLE"))
        assertEquals(VehicleType.VAN, sut.toVehicleType(value = "VAN"))
        assertEquals(VehicleType.TRUCK, sut.toVehicleType(value = "TRUCK"))
    }

    @Test
    fun givenFuelType_whenFromFuelTypeIsCalled_thenReturnsEnumNameAsString() {
        assertEquals("GASOLINE_95", sut.fromFuelType(fuelType = FuelType.GASOLINE_95))
        assertEquals("DIESEL", sut.fromFuelType(fuelType = FuelType.DIESEL))
    }

    @Test
    fun givenFuelTypeName_whenToFuelTypeIsCalled_thenReturnsCorrectEnum() {
        assertEquals(FuelType.GASOLINE_95, sut.toFuelType(value = "GASOLINE_95"))
        assertEquals(FuelType.DIESEL, sut.toFuelType(value = "DIESEL"))
    }

    @Test
    fun givenThemeMode_whenFromThemeModeIsCalled_thenReturnsEnumNameAsString() {
        assertEquals("SYSTEM", sut.fromThemeMode(themeMode = ThemeMode.SYSTEM))
        assertEquals("DARK", sut.fromThemeMode(themeMode = ThemeMode.DARK))
        assertEquals("LIGHT", sut.fromThemeMode(themeMode = ThemeMode.LIGHT))
    }

    @Test
    fun givenThemeModeName_whenToThemeModeIsCalled_thenReturnsCorrectEnum() {
        assertEquals(ThemeMode.SYSTEM, sut.toThemeMode(value = "SYSTEM"))
        assertEquals(ThemeMode.DARK, sut.toThemeMode(value = "DARK"))
        assertEquals(ThemeMode.LIGHT, sut.toThemeMode(value = "LIGHT"))
    }

    @Test
    fun givenAllVehicleTypeValues_whenRoundTrip_thenIdempotent() {
        VehicleType.entries.forEach { vehicleType ->
            val encoded = sut.fromVehicleType(vehicleType = vehicleType)
            val decoded = sut.toVehicleType(value = encoded)
            assertEquals(vehicleType, decoded)
        }
    }

    @Test
    fun givenAllFuelTypeValues_whenRoundTrip_thenIdempotent() {
        FuelType.entries.forEach { fuelType ->
            val encoded = sut.fromFuelType(fuelType = fuelType)
            val decoded = sut.toFuelType(value = encoded)
            assertEquals(fuelType, decoded)
        }
    }

    @Test
    fun givenAllThemeModeValues_whenRoundTrip_thenIdempotent() {
        ThemeMode.entries.forEach { themeMode ->
            val encoded = sut.fromThemeMode(themeMode = themeMode)
            val decoded = sut.toThemeMode(value = encoded)
            assertEquals(themeMode, decoded)
        }
    }

    @Test
    fun givenInvalidVehicleTypeName_whenToVehicleTypeIsCalled_thenThrowsException() {
        assertFails {
            sut.toVehicleType(value = "INVALID_VEHICLE")
        }
    }

    @Test
    fun givenEmptyString_whenToVehicleTypeIsCalled_thenThrowsException() {
        assertFails {
            sut.toVehicleType(value = "")
        }
    }

    @Test
    fun givenLowercaseVehicleTypeName_whenToVehicleTypeIsCalled_thenThrowsException() {
        assertFails {
            sut.toVehicleType(value = "car")
        }
    }

    @Test
    fun givenInvalidFuelTypeName_whenToFuelTypeIsCalled_thenThrowsException() {
        assertFails {
            sut.toFuelType(value = "UNKNOWN_FUEL")
        }
    }

    @Test
    fun givenEmptyString_whenToFuelTypeIsCalled_thenThrowsException() {
        assertFails {
            sut.toFuelType(value = "")
        }
    }

    @Test
    fun givenInvalidThemeModeName_whenToThemeModeIsCalled_thenThrowsException() {
        assertFails {
            sut.toThemeMode(value = "INVALID_THEME")
        }
    }

    @Test
    fun givenEmptyString_whenToThemeModeIsCalled_thenThrowsException() {
        assertFails {
            sut.toThemeMode(value = "")
        }
    }

    @Test
    fun givenAllFuelTypeValues_whenFromFuelTypeIsCalled_thenReturnsEnumName() {
        FuelType.entries.forEach { fuelType ->
            assertEquals(fuelType.name, sut.fromFuelType(fuelType = fuelType))
        }
    }

    @Test
    fun givenAllThemeModeValues_whenFromThemeModeIsCalled_thenReturnsEnumName() {
        ThemeMode.entries.forEach { themeMode ->
            assertEquals(themeMode.name, sut.fromThemeMode(themeMode = themeMode))
        }
    }
}
