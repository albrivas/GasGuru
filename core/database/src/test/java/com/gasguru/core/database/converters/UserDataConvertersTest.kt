package com.gasguru.core.database.converters

import com.gasguru.core.model.data.FuelType
import com.gasguru.core.model.data.ThemeMode
import com.gasguru.core.model.data.VehicleType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class UserDataConvertersTest {

    private val sut = UserDataConverters()

    @Test
    @DisplayName(
        "GIVEN a VehicleType WHEN fromVehicleType is called THEN returns the enum name as string"
    )
    fun fromVehicleTypeReturnsName() {
        assertEquals("CAR", sut.fromVehicleType(vehicleType = VehicleType.CAR))
        assertEquals("MOTORCYCLE", sut.fromVehicleType(vehicleType = VehicleType.MOTORCYCLE))
        assertEquals("VAN", sut.fromVehicleType(vehicleType = VehicleType.VAN))
        assertEquals("TRUCK", sut.fromVehicleType(vehicleType = VehicleType.TRUCK))
    }

    @Test
    @DisplayName(
        "GIVEN a vehicle type name string WHEN toVehicleType is called THEN returns the correct VehicleType"
    )
    fun toVehicleTypeReturnsCorrectEnum() {
        assertEquals(VehicleType.CAR, sut.toVehicleType(value = "CAR"))
        assertEquals(VehicleType.MOTORCYCLE, sut.toVehicleType(value = "MOTORCYCLE"))
        assertEquals(VehicleType.VAN, sut.toVehicleType(value = "VAN"))
        assertEquals(VehicleType.TRUCK, sut.toVehicleType(value = "TRUCK"))
    }

    @Test
    @DisplayName(
        "GIVEN a FuelType WHEN fromFuelType is called THEN returns the enum name as string"
    )
    fun fromFuelTypeReturnsName() {
        assertEquals("GASOLINE_95", sut.fromFuelType(fuelType = FuelType.GASOLINE_95))
        assertEquals("DIESEL", sut.fromFuelType(fuelType = FuelType.DIESEL))
    }

    @Test
    @DisplayName(
        "GIVEN a fuel type name string WHEN toFuelType is called THEN returns the correct FuelType"
    )
    fun toFuelTypeReturnsCorrectEnum() {
        assertEquals(FuelType.GASOLINE_95, sut.toFuelType(value = "GASOLINE_95"))
        assertEquals(FuelType.DIESEL, sut.toFuelType(value = "DIESEL"))
    }

    @Test
    @DisplayName(
        "GIVEN a ThemeMode WHEN fromThemeMode is called THEN returns the enum name as string"
    )
    fun fromThemeModeReturnsName() {
        assertEquals("SYSTEM", sut.fromThemeMode(themeMode = ThemeMode.SYSTEM))
        assertEquals("DARK", sut.fromThemeMode(themeMode = ThemeMode.DARK))
        assertEquals("LIGHT", sut.fromThemeMode(themeMode = ThemeMode.LIGHT))
    }

    @Test
    @DisplayName(
        "GIVEN a theme mode name string WHEN toThemeMode is called THEN returns the correct ThemeMode"
    )
    fun toThemeModeReturnsCorrectEnum() {
        assertEquals(ThemeMode.SYSTEM, sut.toThemeMode(value = "SYSTEM"))
        assertEquals(ThemeMode.DARK, sut.toThemeMode(value = "DARK"))
        assertEquals(ThemeMode.LIGHT, sut.toThemeMode(value = "LIGHT"))
    }
}
