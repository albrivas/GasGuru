package com.gasguru.core.database.di

import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.gasguru.core.database.GasGuruDatabase
import com.gasguru.core.database.migrations.MIGRATION_10_11
import com.gasguru.core.database.migrations.MIGRATION_11_12
import com.gasguru.core.database.migrations.MIGRATION_12_13
import com.gasguru.core.database.migrations.MIGRATION_13_14
import com.gasguru.core.database.migrations.MIGRATION_2_3
import com.gasguru.core.database.migrations.MIGRATION_3_4
import com.gasguru.core.database.migrations.MIGRATION_4_5
import com.gasguru.core.database.migrations.MIGRATION_5_6
import com.gasguru.core.database.migrations.MIGRATION_6_7
import com.gasguru.core.database.migrations.MIGRATION_7_8
import com.gasguru.core.database.migrations.MIGRATION_8_9
import com.gasguru.core.database.migrations.MIGRATION_9_10
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            GasGuruDatabase::class.java,
            "fuel-pump-database",
        ).addMigrations(
            MIGRATION_2_3,
            MIGRATION_3_4,
            MIGRATION_4_5,
            MIGRATION_5_6,
            MIGRATION_6_7,
            MIGRATION_7_8,
            MIGRATION_8_9,
            MIGRATION_9_10,
            MIGRATION_10_11,
            MIGRATION_11_12,
            MIGRATION_12_13,
            MIGRATION_13_14,
        ).addCallback(
            object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    db.execSQL(
                        "INSERT OR IGNORE INTO `user-data` (id, lastUpdate, isOnboardingSuccess, themeModeId) VALUES (0, 0, 0, 3)"
                    )
                }
            },
        ).build()
    }
}
