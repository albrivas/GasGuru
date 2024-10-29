package com.gasguru.core.model.data

data class UserWithFavoriteStations(
    val user: UserData,
    val favoriteStations: List<FuelStation>
)
