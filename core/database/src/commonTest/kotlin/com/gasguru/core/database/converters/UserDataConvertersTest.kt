package com.gasguru.core.database.converters

import com.gasguru.core.model.data.FuelType
import com.gasguru.core.model.data.ThemeMode
import com.gasguru.core.model.data.VehicleType
import kotlin.test.Test
import kotlin.test.assertEquals

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
}
