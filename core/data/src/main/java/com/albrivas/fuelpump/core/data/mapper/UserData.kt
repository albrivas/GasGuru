package com.albrivas.fuelpump.core.data.mapper

import com.albrivas.fuelpump.core.database.model.UserDataEntity
import com.albrivas.fuelpump.core.model.data.UserData

fun UserData.asEntity() = UserDataEntity(
    fuelSelection = fuelSelection,
)
