package com.gasguru.core.model.data

data class UserData(
    val fuelSelection: FuelType = FuelType.GASOLINE_95,
    val lastUpdate: Long = 0L,
)
