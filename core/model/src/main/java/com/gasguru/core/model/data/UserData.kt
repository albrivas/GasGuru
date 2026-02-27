package com.gasguru.core.model.data

data class UserData(
    val isOnboardingSuccess: Boolean = false,
    val lastUpdate: Long = System.currentTimeMillis(),
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val vehicles: List<Vehicle> = emptyList(),
)