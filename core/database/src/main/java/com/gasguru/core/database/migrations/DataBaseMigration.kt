package com.gasguru.core.database.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

const val DB_VERSION_2 = 2
const val DB_VERSION_3 = 3
const val DB_VERSION_4 = 4
const val DB_VERSION_5 = 5

internal val MIGRATION_2_3 = object : Migration(DB_VERSION_2, DB_VERSION_3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE 'fuel-station' ADD COLUMN 'lastUpdate' INTEGER NOT NULL DEFAULT 0")
    }
}

internal val MIGRATION_3_4 = object : Migration(DB_VERSION_3, DB_VERSION_4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE 'fuel-station' ADD COLUMN 'isFavorite' INTEGER NOT NULL DEFAULT 0")
    }
}

internal val MIGRATION_4_5 = object : Migration(DB_VERSION_4, DB_VERSION_5) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `favorite_station_cross_ref` (
                `id` INTEGER NOT NULL,
                `idServiceStation` INTEGER NOT NULL,
                PRIMARY KEY(`id`, `idServiceStation`)
            )
            """.trimIndent()
        )
    }
}