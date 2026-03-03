package com.gasguru.core.model.data

data class Vehicle(
    val id: Long = 0,
    val userId: Long = 0,
    val name: String?,
    val fuelType: FuelType,
    val tankCapacity: Int,
)
