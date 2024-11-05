package com.gasguru.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.gasguru.core.database.converters.ListConverters
import com.gasguru.core.database.converters.UserDataConverters
import com.gasguru.core.database.dao.FuelStationDao
import com.gasguru.core.database.dao.RecentSearchQueryDao
import com.gasguru.core.database.dao.UserDataDao
import com.gasguru.core.database.model.FavoriteStationCrossRef
import com.gasguru.core.database.model.FuelStationEntity
import com.gasguru.core.database.model.RecentSearchQueryEntity
import com.gasguru.core.database.model.UserDataEntity

@Database(
    entities = [
        FuelStationEntity::class,
        UserDataEntity::class,
        RecentSearchQueryEntity::class,
        FavoriteStationCrossRef::class
    ],
    version = 5,
    exportSchema = true
)
@TypeConverters(UserDataConverters::class, ListConverters::class)
abstract class GasGuruDatabase : RoomDatabase() {
    abstract fun fuelStationDao(): FuelStationDao
    abstract fun userDataDao(): UserDataDao
    abstract fun recentDao(): RecentSearchQueryDao
}
