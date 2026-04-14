package com.gasguru.core.database.migrations

import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL

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
const val DB_VERSION_12 = 12
const val DB_VERSION_13 = 13

internal val MIGRATION_2_3 = object : Migration(DB_VERSION_2, DB_VERSION_3) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL("ALTER TABLE 'fuel-station' ADD COLUMN 'lastUpdate' INTEGER NOT NULL DEFAULT 0")
    }
}

internal val MIGRATION_3_4 = object : Migration(DB_VERSION_3, DB_VERSION_4) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL("ALTER TABLE 'fuel-station' ADD COLUMN 'isFavorite' INTEGER NOT NULL DEFAULT 0")
    }
}

internal val MIGRATION_4_5 = object : Migration(DB_VERSION_4, DB_VERSION_5) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL(
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
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL("ALTER TABLE 'user-data' ADD COLUMN 'lastUpdate' INTEGER NOT NULL DEFAULT 0")
    }
}

internal val MIGRATION_6_7 = object : Migration(DB_VERSION_6, DB_VERSION_7) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL(
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
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL("ALTER TABLE 'user-data' ADD COLUMN 'isOnboardingSuccess' INTEGER NOT NULL DEFAULT 0")
    }
}

internal val MIGRATION_8_9 = object : Migration(DB_VERSION_8, DB_VERSION_9) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL("ALTER TABLE 'user-data' ADD COLUMN 'themeModeId' INTEGER NOT NULL DEFAULT 3")
    }
}

internal val MIGRATION_9_10 = object : Migration(DB_VERSION_9, DB_VERSION_10) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_location` ON `fuel-station` (`latitude`, `longitudeWGS84`)")
    }
}

internal val MIGRATION_10_11 = object : Migration(DB_VERSION_10, DB_VERSION_11) {
    override fun migrate(connection: SQLiteConnection) {
        // 1. Create new simplified table for favorite stations
        connection.execSQL(
            """
                CREATE TABLE IF NOT EXISTS `favorite_stations` (
                    `idServiceStation` INTEGER NOT NULL,
                    PRIMARY KEY(`idServiceStation`)
                )
            """.trimIndent()
        )

        // 2. Migrate existing data from cross-reference table (if any)
        connection.execSQL(
            """
                INSERT OR IGNORE INTO `favorite_stations` (`idServiceStation`)
                SELECT DISTINCT `idServiceStation`
                FROM `favorite_station_cross_ref`
            """.trimIndent()
        )

        // 3. Drop obsolete cross-reference table
        connection.execSQL("DROP TABLE IF EXISTS `favorite_station_cross_ref`")
    }
}

internal val MIGRATION_11_12 = object : Migration(DB_VERSION_11, DB_VERSION_12) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL(
            """
                CREATE TABLE IF NOT EXISTS `price_alerts` (
                    `stationId` INTEGER NOT NULL,
                    `createdAt` INTEGER NOT NULL,
                    `lastNotifiedPrice` REAL NOT NULL,
                    `typeModification` TEXT NOT NULL DEFAULT 'INSERT',
                    `isSynced` INTEGER NOT NULL DEFAULT 0,
                    PRIMARY KEY(`stationId`)
                )
            """.trimIndent()
        )
    }
}

internal val MIGRATION_12_13 = object : Migration(DB_VERSION_12, DB_VERSION_13) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL("ALTER TABLE 'fuel-station' ADD COLUMN 'priceAdblue' REAL NOT NULL DEFAULT 0.0")
    }
}

const val DB_VERSION_14 = 14
const val DB_VERSION_15 = 15
const val DB_VERSION_16 = 16
const val DB_VERSION_17 = 17

internal val MIGRATION_13_14 = object : Migration(DB_VERSION_13, DB_VERSION_14) {
    override fun migrate(connection: SQLiteConnection) {
        // 1. Create vehicles table
        connection.execSQL(
            """
                CREATE TABLE IF NOT EXISTS `vehicles` (
                    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    `userId` INTEGER NOT NULL,
                    `name` TEXT,
                    `fuelType` TEXT NOT NULL,
                    `tankCapacity` INTEGER NOT NULL DEFAULT 40,
                    FOREIGN KEY(`userId`) REFERENCES `user-data`(`id`) ON DELETE CASCADE
                )
            """.trimIndent()
        )
        connection.execSQL(
            "CREATE INDEX IF NOT EXISTS `index_vehicles_userId` ON `vehicles` (`userId`)"
        )

        // 2. Copy fuelSelection from user-data into the new vehicles table
        connection.execSQL(
            """
                INSERT INTO `vehicles` (`userId`, `name`, `fuelType`, `tankCapacity`)
                SELECT `id`, NULL, `fuelSelection`, 40
                FROM `user-data`
            """.trimIndent()
        )

        // 3. Recreate user-data without fuelSelection
        connection.execSQL(
            """
                CREATE TABLE `user-data_new` (
                    `id` INTEGER PRIMARY KEY NOT NULL,
                    `lastUpdate` INTEGER NOT NULL DEFAULT 0,
                    `isOnboardingSuccess` INTEGER NOT NULL DEFAULT 0,
                    `themeModeId` INTEGER NOT NULL DEFAULT 3
                )
            """.trimIndent()
        )
        connection.execSQL(
            """
                INSERT INTO `user-data_new` (`id`, `lastUpdate`, `isOnboardingSuccess`, `themeModeId`)
                SELECT `id`, `lastUpdate`, `isOnboardingSuccess`, `themeModeId`
                FROM `user-data`
            """.trimIndent()
        )
        connection.execSQL("DROP TABLE `user-data`")
        connection.execSQL("ALTER TABLE `user-data_new` RENAME TO `user-data`")
    }
}

internal val MIGRATION_14_15 = object : Migration(DB_VERSION_14, DB_VERSION_15) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL("ALTER TABLE `vehicles` ADD COLUMN `vehicleType` TEXT NOT NULL DEFAULT 'CAR'")
        connection.execSQL("ALTER TABLE `vehicles` ADD COLUMN `isPrincipal` INTEGER NOT NULL DEFAULT 0")
    }
}

internal val MIGRATION_15_16 = object : Migration(DB_VERSION_15, DB_VERSION_16) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL(
            "UPDATE vehicles SET isPrincipal = 1 WHERE id IN (SELECT MIN(id) FROM vehicles GROUP BY userId)",
        )
    }
}

internal val MIGRATION_16_17 = object : Migration(DB_VERSION_16, DB_VERSION_17) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL(
            """
                CREATE TABLE IF NOT EXISTS `filter_new` (
                    `type` TEXT NOT NULL,
                    `selection` TEXT NOT NULL,
                    PRIMARY KEY(`type`)
                )
            """.trimIndent()
        )
        connection.execSQL(
            """
                INSERT OR IGNORE INTO `filter_new` (`type`, `selection`)
                SELECT `type`, `selection` FROM `filter`
            """.trimIndent()
        )
        connection.execSQL("DROP TABLE `filter`")
        connection.execSQL("ALTER TABLE `filter_new` RENAME TO `filter`")
    }
}
