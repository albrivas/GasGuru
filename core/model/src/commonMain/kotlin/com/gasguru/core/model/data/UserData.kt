package com.gasguru.core.model.data

data class UserData(
    val userId: Long = 0L,
    val isOnboardingSuccess: Boolean = false,
    val lastUpdate: Long = 0L,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val vehicles: List<Vehicle> = emptyList(),
)

fun UserData.principalVehicle(): Vehicle =
    vehicles.firstOrNull { it.isPrincipal } ?: vehicles.first()
