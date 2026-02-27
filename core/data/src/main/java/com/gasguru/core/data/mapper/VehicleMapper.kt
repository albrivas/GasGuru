package com.gasguru.core.data.mapper

import com.gasguru.core.database.model.VehicleEntity
import com.gasguru.core.model.data.Vehicle

fun Vehicle.asEntity() = VehicleEntity(
    id = id,
    userId = userId,
    name = name,
    fuelType = fuelType,
    tankCapacity = tankCapacity,
)