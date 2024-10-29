package com.gasguru.core.database.model

import androidx.room.Entity

@Entity(
    tableName = "favorite_station_cross_ref",
    primaryKeys = ["id", "idServiceStation"],
)
data class FavoriteStationCrossRef(
    val id: Long,
    val idServiceStation: Int
)
