package com.albrivas.fuelpump.core.model.data

data class UserWithFavoriteStations(
    val user: UserData,
    val favoriteStations: List<FuelStation>
)
