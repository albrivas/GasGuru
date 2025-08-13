package com.gasguru.core.model.data

data class UserData(
    val isOnboardingSuccess: Boolean = false,
    val fuelSelection: FuelType = FuelType.GASOLINE_95,
    val lastUpdate: Long = System.currentTimeMillis(),
    val themeMode: ThemeMode = ThemeMode.SYSTEM
)
