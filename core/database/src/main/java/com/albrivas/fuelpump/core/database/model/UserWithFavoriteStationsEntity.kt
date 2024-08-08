package com.albrivas.fuelpump.core.database.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.albrivas.fuelpump.core.model.data.UserWithFavoriteStations

data class UserWithFavoriteStationsEntity(
    @Embedded val userData: UserDataEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "idServiceStation",
        associateBy = Junction(FavoriteStationCrossRef::class)
    )
    val favoriteStations: List<FuelStationEntity>
)

fun UserWithFavoriteStationsEntity.asExternalModel() = UserWithFavoriteStations(
    user = userData.asExternalModel(),
    favoriteStations = favoriteStations.map { it.asExternalModel() }
)
