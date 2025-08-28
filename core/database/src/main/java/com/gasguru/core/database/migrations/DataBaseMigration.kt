package com.gasguru.core.database.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

const val DB_VERSION_2 = 2
const val DB_VERSION_3 = 3
const val DB_VERSION_4 = 4
const val DB_VERSION_5 = 5
const val DB_VERSION_6 = 6
const val DB_VERSION_7 = 7
const val DB_VERSION_8 = 8
const val DB_VERSION_9 = 9
const val DB_VERSION_10 = 10
const val DB_VERSION_11 = 11

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

internal val MIGRATION_5_6 = object : Migration(DB_VERSION_5, DB_VERSION_6) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE 'user-data' ADD COLUMN 'lastUpdate' INTEGER NOT NULL DEFAULT 0")
    }
}

internal val MIGRATION_6_7 = object : Migration(DB_VERSION_6, DB_VERSION_7) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS 'filter' (
                'id' INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                'type' TEXT NOT NULL,
                'selection' TEXT NOT NULL
            )
            """.trimIndent()
        )
    }
}

internal val MIGRATION_7_8 = object : Migration(DB_VERSION_7, DB_VERSION_8) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE 'user-data' ADD COLUMN 'isOnboardingSuccess' INTEGER NOT NULL DEFAULT 0")
    }
}

internal val MIGRATION_8_9 = object : Migration(DB_VERSION_8, DB_VERSION_9) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE 'user-data' ADD COLUMN 'themeModeId' INTEGER NOT NULL DEFAULT 3")
    }
}

internal val MIGRATION_9_10 = object : Migration(DB_VERSION_9, DB_VERSION_10) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_location` ON `fuel-station` (`latitude`, `longitudeWGS84`)")
    }
}

internal val MIGRATION_10_11 = object : Migration(DB_VERSION_10, DB_VERSION_11) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // 1. Create new simplified table for favorite stations
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `favorite_stations` (
                `idServiceStation` INTEGER NOT NULL, 
                PRIMARY KEY(`idServiceStation`)
            )
            """.trimIndent()
        )

        // 2. Migrate existing data from cross-reference table (if any)
        db.execSQL(
            """
            INSERT OR IGNORE INTO `favorite_stations` (`idServiceStation`)
            SELECT DISTINCT `idServiceStation` 
            FROM `favorite_station_cross_ref`
            """.trimIndent()
        )

        // 3. Drop obsolete cross-reference table
        db.execSQL("DROP TABLE IF EXISTS `favorite_station_cross_ref`")
    }
}
