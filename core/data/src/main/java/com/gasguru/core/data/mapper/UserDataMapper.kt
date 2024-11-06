package com.gasguru.core.data.mapper

import com.gasguru.core.database.model.UserDataEntity
import com.gasguru.core.model.data.UserData

fun UserData.asEntity() = UserDataEntity(
    fuelSelection = fuelSelection,
    lastUpdate = lastUpdate
)