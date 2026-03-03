package com.gasguru.core.model.data

import kotlinx.datetime.Clock

data class UserData(
    val isOnboardingSuccess: Boolean = false,
    val lastUpdate: Long = Clock.System.now().toEpochMilliseconds(),
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val vehicles: List<Vehicle> = emptyList(),
)
