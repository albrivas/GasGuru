package com.gasguru.core.database.migration

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import androidx.sqlite.execSQL
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class DatabaseMigrationTest {

    // region Migration 2 → 3

    @Test
    @DisplayName(
        """
        GIVEN fuel-station table at v2 without lastUpdate column
        WHEN migrating to v3
        THEN lastUpdate column is added with default 0
        """
    )
    fun migrate2to3_addsLastUpdateColumnToFuelStation() {
        val connection = BundledSQLiteDriver().open(":memory:")

        connection.execSQL(
            "CREATE TABLE IF NOT EXISTS `fuel-station` " +
                "(`idServiceStation` INTEGER NOT NULL, `brandStation` TEXT NOT NULL, " +
                "PRIMARY KEY(`idServiceStation`))",
        )
        connection.execSQL(
            "INSERT INTO `fuel-station` (idServiceStation, brandStation) VALUES (1, 'Repsol')",
        )

        MIGRATION_2_3.migrate(connection)

        val stmt =
            connection.prepare("SELECT lastUpdate FROM `fuel-station` WHERE idServiceStation = 1")
        assertTrue(stmt.step())
        assertEquals(0L, stmt.getLong(0))
        stmt.close()
        connection.close()
    }

    // endregion

    // region Migration 3 → 4

    @Test
    @DisplayName(
        """
        GIVEN fuel-station table at v3 without isFavorite column
        WHEN migrating to v4
        THEN isFavorite column is added with default 0
        """
    )
    fun migrate3to4_addsIsFavoriteColumnToFuelStation() {
        val connection = BundledSQLiteDriver().open(":memory:")

        connection.execSQL(
            "CREATE TABLE IF NOT EXISTS `fuel-station` " +
                "(`idServiceStation` INTEGER NOT NULL, `lastUpdate` INTEGER NOT NULL DEFAULT 0, " +
                "PRIMARY KEY(`idServiceStation`))",
        )
        connection.execSQL(
            "INSERT INTO `fuel-station` (idServiceStation) VALUES (1)",
        )

        MIGRATION_3_4.migrate(connection)

        val stmt =
            connection.prepare("SELECT isFavorite FROM `fuel-station` WHERE idServiceStation = 1")
        assertTrue(stmt.step())
        assertEquals(0L, stmt.getLong(0))
        stmt.close()
        connection.close()
    }

    // endregion

    // region Migration 4 → 5

    @Test
    @DisplayName(
        """
        GIVEN schema at v4 without favorite_station_cross_ref table
        WHEN migrating to v5
        THEN favorite_station_cross_ref table is created
        """
    )
    fun migrate4to5_createsFavoriteStationCrossRefTable() {
        val connection = BundledSQLiteDriver().open(":memory:")

        MIGRATION_4_5.migrate(connection)

        // If the table does not exist this will throw
        connection.execSQL(
            "INSERT INTO favorite_station_cross_ref (id, idServiceStation) VALUES (1, 100)",
        )
        val stmt = connection.prepare("SELECT COUNT(*) FROM favorite_station_cross_ref")
        assertTrue(stmt.step())
        assertEquals(1L, stmt.getLong(0))
        stmt.close()
        connection.close()
    }

    @Test
    @DisplayName(
        """
        GIVEN schema at v4
        WHEN migrating to v5 twice
        THEN table creation is idempotent due to IF NOT EXISTS
        """
    )
    fun migrate4to5_isIdempotent() {
        val connection = BundledSQLiteDriver().open(":memory:")

        MIGRATION_4_5.migrate(connection)
        MIGRATION_4_5.migrate(connection)

        val stmt = connection.prepare("SELECT COUNT(*) FROM favorite_station_cross_ref")
        assertTrue(stmt.step())
        stmt.close()
        connection.close()
    }

    // endregion

    // region Migration 5 → 6

    @Test
    @DisplayName(
        """
        GIVEN user-data table at v5 without lastUpdate column
        WHEN migrating to v6
        THEN lastUpdate column is added with default 0
        """
    )
    fun migrate5to6_addsLastUpdateToUserData() {
        val connection = BundledSQLiteDriver().open(":memory:")

        connection.execSQL(
            "CREATE TABLE IF NOT EXISTS `user-data` (`id` INTEGER NOT NULL, PRIMARY KEY(`id`))",
        )
        connection.execSQL("INSERT INTO `user-data` (id) VALUES (0)")

        MIGRATION_5_6.migrate(connection)

        val stmt = connection.prepare("SELECT lastUpdate FROM `user-data` WHERE id = 0")
        assertTrue(stmt.step())
        assertEquals(0L, stmt.getLong(0))
        stmt.close()
        connection.close()
    }

    // endregion

    // region Migration 6 → 7

    @Test
    @DisplayName(
        """
        GIVEN schema at v6 without filter table
        WHEN migrating to v7
        THEN filter table is created with correct columns
        """
    )
    fun migrate6to7_createsFilterTable() {
        val connection = BundledSQLiteDriver().open(":memory:")

        MIGRATION_6_7.migrate(connection)

        connection.execSQL(
            "INSERT INTO `filter` (type, selection) VALUES ('BRAND', 'Repsol')",
        )
        val stmt = connection.prepare("SELECT type, selection FROM `filter` WHERE id = 1")
        assertTrue(stmt.step())
        assertEquals("BRAND", stmt.getText(0))
        assertEquals("Repsol", stmt.getText(1))
        stmt.close()
        connection.close()
    }

    // endregion

    // region Migration 7 → 8

    @Test
    @DisplayName(
        """
        GIVEN user-data table at v7 without isOnboardingSuccess
        WHEN migrating to v8
        THEN isOnboardingSuccess column is added with default 0
        """
    )
    fun migrate7to8_addsIsOnboardingSuccessColumn() {
        val connection = BundledSQLiteDriver().open(":memory:")

        connection.execSQL(
            "CREATE TABLE IF NOT EXISTS `user-data` " +
                "(`id` INTEGER NOT NULL, `lastUpdate` INTEGER NOT NULL DEFAULT 0, " +
                "PRIMARY KEY(`id`))",
        )
        connection.execSQL("INSERT INTO `user-data` (id) VALUES (0)")

        MIGRATION_7_8.migrate(connection)

        val stmt = connection.prepare("SELECT isOnboardingSuccess FROM `user-data` WHERE id = 0")
        assertTrue(stmt.step())
        assertEquals(0L, stmt.getLong(0))
        stmt.close()
        connection.close()
    }

    // endregion

    // region Migration 8 → 9

    @Test
    @DisplayName(
        """
        GIVEN user-data table at v8 without themeModeId
        WHEN migrating to v9
        THEN themeModeId column is added with default 3 (SYSTEM)
        """
    )
    fun migrate8to9_addsThemeModeIdColumnWithDefault3() {
        val connection = BundledSQLiteDriver().open(":memory:")

        connection.execSQL(
            "CREATE TABLE IF NOT EXISTS `user-data` " +
                "(`id` INTEGER NOT NULL, `lastUpdate` INTEGER NOT NULL DEFAULT 0, " +
                "`isOnboardingSuccess` INTEGER NOT NULL DEFAULT 0, PRIMARY KEY(`id`))",
        )
        connection.execSQL("INSERT INTO `user-data` (id) VALUES (0)")

        MIGRATION_8_9.migrate(connection)

        val stmt = connection.prepare("SELECT themeModeId FROM `user-data` WHERE id = 0")
        assertTrue(stmt.step())
        assertEquals(3L, stmt.getLong(0))
        stmt.close()
        connection.close()
    }

    // endregion

    // region Migration 9 → 10

    @Test
    @DisplayName(
        """
        GIVEN fuel-station table at v9
        WHEN migrating to v10
        THEN index_location index is created on latitude and longitudeWGS84
        """
    )
    fun migrate9to10_createsLocationIndex() {
        val connection = BundledSQLiteDriver().open(":memory:")

        connection.execSQL(
            "CREATE TABLE IF NOT EXISTS `fuel-station` " +
                "(`idServiceStation` INTEGER NOT NULL, `latitude` REAL NOT NULL DEFAULT 0, " +
                "`longitudeWGS84` REAL NOT NULL DEFAULT 0, PRIMARY KEY(`idServiceStation`))",
        )

        MIGRATION_9_10.migrate(connection)

        // Verify index exists by querying sqlite_master
        val stmt = connection.prepare(
            "SELECT COUNT(*) FROM sqlite_master WHERE type = 'index' AND name = 'index_location'",
        )
        assertTrue(stmt.step())
        assertEquals(1L, stmt.getLong(0))
        stmt.close()
        connection.close()
    }

    // endregion

    // region Migration 10 → 11

    @Test
    @DisplayName(
        """
        GIVEN schema at v10 with favorite_station_cross_ref containing data
        WHEN migrating to v11
        THEN data is migrated to favorite_stations and cross_ref is dropped
        """
    )
    fun migrate10to11_migratesCrossRefToFavoriteStations() {
        val connection = BundledSQLiteDriver().open(":memory:")

        connection.execSQL(
            "CREATE TABLE IF NOT EXISTS `favorite_station_cross_ref` " +
                "(`id` INTEGER NOT NULL, `idServiceStation` INTEGER NOT NULL, " +
                "PRIMARY KEY(`id`, `idServiceStation`))",
        )
        connection.execSQL(
            "INSERT INTO `favorite_station_cross_ref` (id, idServiceStation) VALUES (1, 100)",
        )
        connection.execSQL(
            "INSERT INTO `favorite_station_cross_ref` (id, idServiceStation) VALUES (2, 100)",
        )
        connection.execSQL(
            "INSERT INTO `favorite_station_cross_ref` (id, idServiceStation) VALUES (3, 200)",
        )

        MIGRATION_10_11.migrate(connection)

        // favorite_stations should exist and contain unique idServiceStation values
        val stmt = connection.prepare("SELECT COUNT(*) FROM favorite_stations")
        assertTrue(stmt.step())
        assertEquals(2L, stmt.getLong(0))
        stmt.close()

        // cross_ref table should be gone
        val tableCheck = connection.prepare(
            "SELECT COUNT(*) FROM sqlite_master " +
                "WHERE type = 'table' AND name = 'favorite_station_cross_ref'",
        )
        assertTrue(tableCheck.step())
        assertEquals(0L, tableCheck.getLong(0))
        tableCheck.close()

        connection.close()
    }

    @Test
    @DisplayName(
        """
        GIVEN schema at v10 with empty favorite_station_cross_ref
        WHEN migrating to v11
        THEN favorite_stations is empty and cross_ref is dropped
        """
    )
    fun migrate10to11_withEmptyCrossRef_createsFavoriteStationsEmpty() {
        val connection = BundledSQLiteDriver().open(":memory:")

        connection.execSQL(
            "CREATE TABLE IF NOT EXISTS `favorite_station_cross_ref` " +
                "(`id` INTEGER NOT NULL, `idServiceStation` INTEGER NOT NULL, " +
                "PRIMARY KEY(`id`, `idServiceStation`))",
        )

        MIGRATION_10_11.migrate(connection)

        val stmt = connection.prepare("SELECT COUNT(*) FROM favorite_stations")
        assertTrue(stmt.step())
        assertEquals(0L, stmt.getLong(0))
        stmt.close()
        connection.close()
    }

    // endregion

    // region Migration 11 → 12

    @Test
    @DisplayName(
        """
        GIVEN schema at v11 without price_alerts table
        WHEN migrating to v12
        THEN price_alerts table is created with correct schema
        """
    )
    fun migrate11to12_createsPriceAlertsTable() {
        val connection = BundledSQLiteDriver().open(":memory:")

        MIGRATION_11_12.migrate(connection)

        connection.execSQL(
            "INSERT INTO `price_alerts` (stationId, createdAt, lastNotifiedPrice) " +
                "VALUES (123, 1000, 1.50)",
        )
        val stmt = connection.prepare(
            "SELECT stationId, typeModification, isSynced FROM price_alerts WHERE stationId = 123",
        )
        assertTrue(stmt.step())
        assertEquals(123L, stmt.getLong(0))
        assertEquals("INSERT", stmt.getText(1))
        assertEquals(0L, stmt.getLong(2))
        stmt.close()
        connection.close()
    }

    // endregion

    // region Migration 12 → 13

    @Test
    @DisplayName(
        """
        GIVEN fuel-station table at v12 without priceAdblue column
        WHEN migrating to v13
        THEN priceAdblue column is added with default 0.0
        """
    )
    fun migrate12to13_addsPriceAdblueColumnWithDefaultZero() {
        val connection = BundledSQLiteDriver().open(":memory:")

        connection.execSQL(
            "CREATE TABLE IF NOT EXISTS `fuel-station` " +
                "(`idServiceStation` INTEGER NOT NULL, `brandStation` TEXT NOT NULL, " +
                "PRIMARY KEY(`idServiceStation`))",
        )
        connection.execSQL(
            "INSERT INTO `fuel-station` (idServiceStation, brandStation) VALUES (1, 'Repsol')",
        )

        MIGRATION_12_13.migrate(connection)

        val stmt =
            connection.prepare("SELECT priceAdblue FROM `fuel-station` WHERE idServiceStation = 1")
        assertTrue(stmt.step())
        assertEquals(0.0, stmt.getDouble(0))
        stmt.close()
        connection.close()
    }

    // endregion

    @Test
    @DisplayName(
        """
        GIVEN user-data with fuelSelection in v13
        WHEN migrating to v14
        THEN vehicle is created with correct fuelType and capacity
        """
    )
    fun migrate13to14_createsVehicleFromFuelSelection() {
        val connection = BundledSQLiteDriver().open(":memory:")

        connection.execSQL(
            "CREATE TABLE IF NOT EXISTS `user-data` " +
                "(`id` INTEGER NOT NULL, `fuelSelection` TEXT NOT NULL, " +
                "`lastUpdate` INTEGER NOT NULL DEFAULT 0, " +
                "`isOnboardingSuccess` INTEGER NOT NULL DEFAULT 0, " +
                "`themeModeId` INTEGER NOT NULL DEFAULT 3, PRIMARY KEY(`id`))",
        )
        connection.execSQL(
            "INSERT INTO `user-data` " +
                "(id, lastUpdate, isOnboardingSuccess, themeModeId, fuelSelection) " +
                "VALUES (0, 0, 1, 3, 'GASOLINE_95')",
        )

        MIGRATION_13_14.migrate(connection)

        val stmt =
            connection.prepare("SELECT fuelType, tankCapacity FROM vehicles WHERE userId = 0")
        assertTrue(stmt.step())
        assertEquals("GASOLINE_95", stmt.getText(0))
        assertEquals(40L, stmt.getLong(1))
        stmt.close()

        connection.close()
    }

    @Test
    @DisplayName(
        """
        GIVEN user-data with DIESEL fuelSelection in v13
        WHEN migrating to v14
        THEN vehicle fuelType is DIESEL
        """
    )
    fun migrate13to14_preservesDieselFuelType() {
        val connection = BundledSQLiteDriver().open(":memory:")

        connection.execSQL(
            "CREATE TABLE IF NOT EXISTS `user-data` " +
                "(`id` INTEGER NOT NULL, `fuelSelection` TEXT NOT NULL, " +
                "`lastUpdate` INTEGER NOT NULL DEFAULT 0, " +
                "`isOnboardingSuccess` INTEGER NOT NULL DEFAULT 0, " +
                "`themeModeId` INTEGER NOT NULL DEFAULT 3, PRIMARY KEY(`id`))",
        )
        connection.execSQL(
            "INSERT INTO `user-data` " +
                "(id, lastUpdate, isOnboardingSuccess, themeModeId, fuelSelection) " +
                "VALUES (0, 0, 1, 3, 'DIESEL')",
        )

        MIGRATION_13_14.migrate(connection)

        val stmt = connection.prepare("SELECT fuelType FROM vehicles WHERE userId = 0")
        assertTrue(stmt.step())
        assertEquals("DIESEL", stmt.getText(0))
        stmt.close()

        connection.close()
    }

    @Test
    @DisplayName(
        """
        GIVEN empty user-data table in v13
        WHEN migrating to v14
        THEN no vehicles are created
        """
    )
    fun migrate13to14_withNoUsers_producesNoVehicles() {
        val connection = BundledSQLiteDriver().open(":memory:")

        connection.execSQL(
            "CREATE TABLE IF NOT EXISTS `user-data` " +
                "(`id` INTEGER NOT NULL, `fuelSelection` TEXT NOT NULL, " +
                "`lastUpdate` INTEGER NOT NULL DEFAULT 0, " +
                "`isOnboardingSuccess` INTEGER NOT NULL DEFAULT 0, " +
                "`themeModeId` INTEGER NOT NULL DEFAULT 3, PRIMARY KEY(`id`))",
        )

        MIGRATION_13_14.migrate(connection)

        val stmt = connection.prepare("SELECT COUNT(*) FROM vehicles")
        assertTrue(stmt.step())
        assertEquals(0L, stmt.getLong(0))
        stmt.close()

        connection.close()
    }

    @Test
    @DisplayName(
        """
        GIVEN migrated vehicle in v14
        WHEN updating user-data with UPDATE
        THEN vehicle is not cascade deleted
        """
    )
    fun migrate13to14_vehicleIsNotDeletedWhenUserDataIsUpdated() {
        val connection = BundledSQLiteDriver().open(":memory:")

        connection.execSQL(
            "CREATE TABLE IF NOT EXISTS `user-data` " +
                "(`id` INTEGER NOT NULL, `fuelSelection` TEXT NOT NULL, " +
                "`lastUpdate` INTEGER NOT NULL DEFAULT 0, " +
                "`isOnboardingSuccess` INTEGER NOT NULL DEFAULT 0, " +
                "`themeModeId` INTEGER NOT NULL DEFAULT 3, PRIMARY KEY(`id`))",
        )
        connection.execSQL(
            "INSERT INTO `user-data` " +
                "(id, lastUpdate, isOnboardingSuccess, themeModeId, fuelSelection) " +
                "VALUES (0, 0, 0, 3, 'GASOLINE_95')",
        )

        MIGRATION_13_14.migrate(connection)

        // Simulate the fixed setOnboardingComplete() — uses UPDATE, not REPLACE
        connection.execSQL("UPDATE `user-data` SET isOnboardingSuccess = 1 WHERE id = 0")

        val stmt = connection.prepare("SELECT COUNT(*) FROM vehicles WHERE userId = 0")
        assertTrue(stmt.step())
        assertEquals(1L, stmt.getLong(0))
        stmt.close()

        connection.close()
    }

    @Test
    @DisplayName(
        """
        GIVEN vehicles in v14
        WHEN migrating to v15
        THEN vehicleType defaults to CAR and isPrincipal defaults to false
        """
    )
    fun migrate14to15_addsVehicleTypeAndIsPrincipalWithDefaults() {
        val connection = BundledSQLiteDriver().open(":memory:")

        connection.execSQL(
            "CREATE TABLE IF NOT EXISTS `user-data` " +
                "(`id` INTEGER NOT NULL, `lastUpdate` INTEGER NOT NULL DEFAULT 0, " +
                "`isOnboardingSuccess` INTEGER NOT NULL DEFAULT 0, " +
                "`themeModeId` INTEGER NOT NULL DEFAULT 3, PRIMARY KEY(`id`))",
        )
        connection.execSQL(
            "CREATE TABLE IF NOT EXISTS `vehicles` " +
                "(`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `userId` INTEGER NOT NULL, " +
                "`name` TEXT, `fuelType` TEXT NOT NULL, `tankCapacity` INTEGER NOT NULL, " +
                "FOREIGN KEY(`userId`) REFERENCES `user-data`(`id`) " +
                "ON UPDATE NO ACTION ON DELETE CASCADE )",
        )
        connection.execSQL(
            "CREATE INDEX IF NOT EXISTS `index_vehicles_userId` ON `vehicles` (`userId`)",
        )
        connection.execSQL(
            "INSERT INTO `user-data` (id, lastUpdate, isOnboardingSuccess, themeModeId) " +
                "VALUES (0, 0, 1, 3)",
        )
        connection.execSQL(
            "INSERT INTO `vehicles` (id, userId, name, fuelType, tankCapacity) " +
                "VALUES (1, 0, NULL, 'GASOLINE_95', 40)",
        )

        MIGRATION_14_15.migrate(connection)

        val stmt = connection.prepare("SELECT vehicleType, isPrincipal FROM vehicles WHERE id = 1")
        assertTrue(stmt.step())
        assertEquals("CAR", stmt.getText(0))
        assertEquals(0L, stmt.getLong(1))
        stmt.close()

        connection.close()
    }

    @Test
    @DisplayName(
        """
        GIVEN multiple vehicles per user in v15
        WHEN migrating to v16
        THEN first vehicle per user is marked as principal
        """
    )
    fun migrate15to16_setsFirstVehiclePerUserAsPrincipal() {
        val connection = BundledSQLiteDriver().open(":memory:")

        connection.execSQL(
            "CREATE TABLE IF NOT EXISTS `user-data` " +
                "(`id` INTEGER NOT NULL, `lastUpdate` INTEGER NOT NULL DEFAULT 0, " +
                "`isOnboardingSuccess` INTEGER NOT NULL DEFAULT 0, " +
                "`themeModeId` INTEGER NOT NULL DEFAULT 3, PRIMARY KEY(`id`))",
        )
        connection.execSQL(
            "CREATE TABLE IF NOT EXISTS `vehicles` " +
                "(`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `userId` INTEGER NOT NULL, " +
                "`name` TEXT, `fuelType` TEXT NOT NULL, `tankCapacity` INTEGER NOT NULL, " +
                "`vehicleType` TEXT NOT NULL DEFAULT 'CAR', " +
                "`isPrincipal` INTEGER NOT NULL DEFAULT 0, " +
                "FOREIGN KEY(`userId`) REFERENCES `user-data`(`id`) " +
                "ON UPDATE NO ACTION ON DELETE CASCADE )",
        )
        connection.execSQL(
            "CREATE INDEX IF NOT EXISTS `index_vehicles_userId` ON `vehicles` (`userId`)",
        )
        connection.execSQL(
            "INSERT INTO `user-data` (id, lastUpdate, isOnboardingSuccess, themeModeId) " +
                "VALUES (0, 0, 1, 3)",
        )
        connection.execSQL(
            "INSERT INTO `vehicles` " +
                "(id, userId, name, fuelType, tankCapacity, vehicleType, isPrincipal) " +
                "VALUES (1, 0, NULL, 'GASOLINE_95', 40, 'CAR', 0)",
        )
        connection.execSQL(
            "INSERT INTO `vehicles` " +
                "(id, userId, name, fuelType, tankCapacity, vehicleType, isPrincipal) " +
                "VALUES (2, 0, NULL, 'DIESEL', 50, 'CAR', 0)",
        )

        MIGRATION_15_16.migrate(connection)

        val stmt = connection.prepare("SELECT id, isPrincipal FROM vehicles ORDER BY id")

        assertTrue(stmt.step())
        assertEquals(1L, stmt.getLong(0))
        assertEquals(1L, stmt.getLong(1))

        assertTrue(stmt.step())
        assertEquals(2L, stmt.getLong(0))
        assertEquals(0L, stmt.getLong(1))

        stmt.close()
        connection.close()
    }

    @Test
    @DisplayName(
        """
        GIVEN empty vehicles table in v15
        WHEN migrating to v16
        THEN no error is thrown and table remains empty
        """
    )
    fun migrate15to16_withNoVehicles_doesNotThrow() {
        val connection = BundledSQLiteDriver().open(":memory:")

        connection.execSQL(
            "CREATE TABLE IF NOT EXISTS `vehicles` " +
                "(`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `userId` INTEGER NOT NULL, " +
                "`name` TEXT, `fuelType` TEXT NOT NULL, `tankCapacity` INTEGER NOT NULL, " +
                "`vehicleType` TEXT NOT NULL DEFAULT 'CAR', " +
                "`isPrincipal` INTEGER NOT NULL DEFAULT 0)",
        )

        MIGRATION_15_16.migrate(connection)

        val stmt = connection.prepare("SELECT COUNT(*) FROM vehicles")
        assertTrue(stmt.step())
        assertEquals(0L, stmt.getLong(0))
        stmt.close()
        connection.close()
    }

    @Test
    @DisplayName(
        """
        GIVEN multiple users each with vehicles in v15
        WHEN migrating to v16
        THEN each user's first vehicle is marked as principal independently
        """
    )
    fun migrate15to16_multipleUsers_eachUserFirstVehicleBecomePrincipal() {
        val connection = BundledSQLiteDriver().open(":memory:")

        connection.execSQL(
            "CREATE TABLE IF NOT EXISTS `vehicles` " +
                "(`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `userId` INTEGER NOT NULL, " +
                "`name` TEXT, `fuelType` TEXT NOT NULL, `tankCapacity` INTEGER NOT NULL, " +
                "`vehicleType` TEXT NOT NULL DEFAULT 'CAR', " +
                "`isPrincipal` INTEGER NOT NULL DEFAULT 0)",
        )
        // User 0: vehicles 1 and 2
        connection.execSQL(
            "INSERT INTO `vehicles` " +
                "(id, userId, name, fuelType, tankCapacity, vehicleType, isPrincipal) " +
                "VALUES (1, 0, NULL, 'GASOLINE_95', 40, 'CAR', 0)",
        )
        connection.execSQL(
            "INSERT INTO `vehicles` " +
                "(id, userId, name, fuelType, tankCapacity, vehicleType, isPrincipal) " +
                "VALUES (2, 0, NULL, 'DIESEL', 50, 'CAR', 0)",
        )
        // User 1: vehicles 3 and 4
        connection.execSQL(
            "INSERT INTO `vehicles` " +
                "(id, userId, name, fuelType, tankCapacity, vehicleType, isPrincipal) " +
                "VALUES (3, 1, NULL, 'DIESEL', 60, 'TRUCK', 0)",
        )
        connection.execSQL(
            "INSERT INTO `vehicles` " +
                "(id, userId, name, fuelType, tankCapacity, vehicleType, isPrincipal) " +
                "VALUES (4, 1, NULL, 'GASOLINE_95', 35, 'MOTORCYCLE', 0)",
        )

        MIGRATION_15_16.migrate(connection)

        val stmt = connection.prepare("SELECT id, isPrincipal FROM vehicles ORDER BY id")

        // User 0 - vehicle 1 principal
        assertTrue(stmt.step())
        assertEquals(1L, stmt.getLong(0))
        assertEquals(1L, stmt.getLong(1))

        // User 0 - vehicle 2 not principal
        assertTrue(stmt.step())
        assertEquals(2L, stmt.getLong(0))
        assertEquals(0L, stmt.getLong(1))

        // User 1 - vehicle 3 principal
        assertTrue(stmt.step())
        assertEquals(3L, stmt.getLong(0))
        assertEquals(1L, stmt.getLong(1))

        // User 1 - vehicle 4 not principal
        assertTrue(stmt.step())
        assertEquals(4L, stmt.getLong(0))
        assertEquals(0L, stmt.getLong(1))

        stmt.close()
        connection.close()
    }

    @Test
    @DisplayName(
        """
        GIVEN a single vehicle per user in v15
        WHEN migrating to v16
        THEN that vehicle becomes principal
        """
    )
    fun migrate15to16_singleVehiclePerUser_becomesPrincipal() {
        val connection = BundledSQLiteDriver().open(":memory:")

        connection.execSQL(
            "CREATE TABLE IF NOT EXISTS `vehicles` " +
                "(`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `userId` INTEGER NOT NULL, " +
                "`name` TEXT, `fuelType` TEXT NOT NULL, `tankCapacity` INTEGER NOT NULL, " +
                "`vehicleType` TEXT NOT NULL DEFAULT 'CAR', " +
                "`isPrincipal` INTEGER NOT NULL DEFAULT 0)",
        )
        connection.execSQL(
            "INSERT INTO `vehicles` " +
                "(id, userId, name, fuelType, tankCapacity, vehicleType, isPrincipal) " +
                "VALUES (10, 0, NULL, 'DIESEL', 55, 'CAR', 0)",
        )

        MIGRATION_15_16.migrate(connection)

        val stmt = connection.prepare("SELECT isPrincipal FROM vehicles WHERE id = 10")
        assertTrue(stmt.step())
        assertEquals(1L, stmt.getLong(0))
        stmt.close()
        connection.close()
    }

    // endregion

    // region Migration 13 → 14 additional edge cases

    @Test
    @DisplayName(
        """
        GIVEN multiple users with fuelSelection in v13
        WHEN migrating to v14
        THEN each user gets their own vehicle
        """
    )
    fun migrate13to14_multipleUsers_eachUserGetsVehicle() {
        val connection = BundledSQLiteDriver().open(":memory:")

        connection.execSQL(
            "CREATE TABLE IF NOT EXISTS `user-data` " +
                "(`id` INTEGER NOT NULL, `fuelSelection` TEXT NOT NULL, " +
                "`lastUpdate` INTEGER NOT NULL DEFAULT 0, " +
                "`isOnboardingSuccess` INTEGER NOT NULL DEFAULT 0, " +
                "`themeModeId` INTEGER NOT NULL DEFAULT 3, PRIMARY KEY(`id`))",
        )
        connection.execSQL(
            "INSERT INTO `user-data` " +
                "(id, lastUpdate, isOnboardingSuccess, themeModeId, fuelSelection) " +
                "VALUES (0, 0, 1, 3, 'GASOLINE_95')",
        )
        connection.execSQL(
            "INSERT INTO `user-data` " +
                "(id, lastUpdate, isOnboardingSuccess, themeModeId, fuelSelection) " +
                "VALUES (1, 0, 1, 3, 'DIESEL')",
        )

        MIGRATION_13_14.migrate(connection)

        val stmt = connection.prepare("SELECT COUNT(*) FROM vehicles")
        assertTrue(stmt.step())
        assertEquals(2L, stmt.getLong(0))
        stmt.close()

        val stmtUser0 = connection.prepare("SELECT fuelType FROM vehicles WHERE userId = 0")
        assertTrue(stmtUser0.step())
        assertEquals("GASOLINE_95", stmtUser0.getText(0))
        stmtUser0.close()

        val stmtUser1 = connection.prepare("SELECT fuelType FROM vehicles WHERE userId = 1")
        assertTrue(stmtUser1.step())
        assertEquals("DIESEL", stmtUser1.getText(0))
        stmtUser1.close()

        connection.close()
    }

    @Test
    @DisplayName(
        """
        GIVEN user-data with fuelSelection in v13
        WHEN migrating to v14
        THEN fuelSelection column is removed from user-data
        """
    )
    fun migrate13to14_removesSelectionColumnFromUserData() {
        val connection = BundledSQLiteDriver().open(":memory:")

        connection.execSQL(
            "CREATE TABLE IF NOT EXISTS `user-data` " +
                "(`id` INTEGER NOT NULL, `fuelSelection` TEXT NOT NULL, " +
                "`lastUpdate` INTEGER NOT NULL DEFAULT 0, " +
                "`isOnboardingSuccess` INTEGER NOT NULL DEFAULT 0, " +
                "`themeModeId` INTEGER NOT NULL DEFAULT 3, PRIMARY KEY(`id`))",
        )
        connection.execSQL(
            "INSERT INTO `user-data` " +
                "(id, lastUpdate, isOnboardingSuccess, themeModeId, fuelSelection) " +
                "VALUES (0, 0, 1, 3, 'GASOLINE_95')",
        )

        MIGRATION_13_14.migrate(connection)

        // The fuelSelection column should no longer exist — attempting SELECT should fail
        // or the table should only contain the 4 expected columns
        val columnCountStmt = connection.prepare("PRAGMA table_info(`user-data`)")
        var columnCount = 0
        while (columnCountStmt.step()) {
            columnCount++
        }
        columnCountStmt.close()

        // After migration the table should have 4 columns: id, lastUpdate, isOnboardingSuccess, themeModeId
        assertEquals(4, columnCount)

        connection.close()
    }

    // endregion
}
