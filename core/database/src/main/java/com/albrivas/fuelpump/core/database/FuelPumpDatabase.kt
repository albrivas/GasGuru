package com.albrivas.fuelpump.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.albrivas.fuelpump.core.database.converters.UserDataConverters
import com.albrivas.fuelpump.core.database.dao.FuelStationDao
import com.albrivas.fuelpump.core.database.dao.RecentSearchQueryDao
import com.albrivas.fuelpump.core.database.dao.UserDataDao
import com.albrivas.fuelpump.core.database.model.FuelStationEntity
import com.albrivas.fuelpump.core.database.model.RecentSearchQueryEntity
import com.albrivas.fuelpump.core.database.model.UserDataEntity

@Database(
    entities = [
        FuelStationEntity::class,
        UserDataEntity::class,
        RecentSearchQueryEntity::class
    ],
    version = 2,
)
@TypeConverters(UserDataConverters::class)
abstract class FuelPumpDatabase : RoomDatabase() {
    abstract fun fuelStationDao(): FuelStationDao
    abstract fun userDataDao(): UserDataDao
    abstract fun recentDao(): RecentSearchQueryDao
}
