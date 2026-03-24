package com.gasguru.core.database.migrations

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import androidx.sqlite.execSQL
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class DatabaseMigrationTest {

    @Test
    @DisplayName("GIVEN user-data with fuelSelection in v13 WHEN migrating to v14 THEN vehicle is created with correct fuelType and capacity")
    fun migrate13to14_createsVehicleFromFuelSelection() {
        val connection = BundledSQLiteDriver().open(":memory:")

        connection.execSQL(
            "CREATE TABLE IF NOT EXISTS `user-data` (`id` INTEGER NOT NULL, `fuelSelection` TEXT NOT NULL, `lastUpdate` INTEGER NOT NULL DEFAULT 0, `isOnboardingSuccess` INTEGER NOT NULL DEFAULT 0, `themeModeId` INTEGER NOT NULL DEFAULT 3, PRIMARY KEY(`id`))"
        )
        connection.execSQL(
            "INSERT INTO `user-data` (id, lastUpdate, isOnboardingSuccess, themeModeId, fuelSelection) VALUES (0, 0, 1, 3, 'GASOLINE_95')"
        )

        MIGRATION_13_14.migrate(connection)

        val stmt = connection.prepare("SELECT fuelType, tankCapacity FROM vehicles WHERE userId = 0")
        assertTrue(stmt.step())
        assertEquals("GASOLINE_95", stmt.getText(0))
        assertEquals(40L, stmt.getLong(1))
        stmt.close()

        connection.close()
    }

    @Test
    @DisplayName("GIVEN user-data with DIESEL fuelSelection in v13 WHEN migrating to v14 THEN vehicle fuelType is DIESEL")
    fun migrate13to14_preservesDieselFuelType() {
        val connection = BundledSQLiteDriver().open(":memory:")

        connection.execSQL(
            "CREATE TABLE IF NOT EXISTS `user-data` (`id` INTEGER NOT NULL, `fuelSelection` TEXT NOT NULL, `lastUpdate` INTEGER NOT NULL DEFAULT 0, `isOnboardingSuccess` INTEGER NOT NULL DEFAULT 0, `themeModeId` INTEGER NOT NULL DEFAULT 3, PRIMARY KEY(`id`))"
        )
        connection.execSQL(
            "INSERT INTO `user-data` (id, lastUpdate, isOnboardingSuccess, themeModeId, fuelSelection) VALUES (0, 0, 1, 3, 'DIESEL')"
        )

        MIGRATION_13_14.migrate(connection)

        val stmt = connection.prepare("SELECT fuelType FROM vehicles WHERE userId = 0")
        assertTrue(stmt.step())
        assertEquals("DIESEL", stmt.getText(0))
        stmt.close()

        connection.close()
    }

    @Test
    @DisplayName("GIVEN empty user-data table in v13 WHEN migrating to v14 THEN no vehicles are created")
    fun migrate13to14_withNoUsers_producesNoVehicles() {
        val connection = BundledSQLiteDriver().open(":memory:")

        connection.execSQL(
            "CREATE TABLE IF NOT EXISTS `user-data` (`id` INTEGER NOT NULL, `fuelSelection` TEXT NOT NULL, `lastUpdate` INTEGER NOT NULL DEFAULT 0, `isOnboardingSuccess` INTEGER NOT NULL DEFAULT 0, `themeModeId` INTEGER NOT NULL DEFAULT 3, PRIMARY KEY(`id`))"
        )

        MIGRATION_13_14.migrate(connection)

        val stmt = connection.prepare("SELECT COUNT(*) FROM vehicles")
        assertTrue(stmt.step())
        assertEquals(0L, stmt.getLong(0))
        stmt.close()

        connection.close()
    }

    @Test
    @DisplayName("GIVEN migrated vehicle in v14 WHEN updating user-data with UPDATE THEN vehicle is not cascade deleted")
    fun migrate13to14_vehicleIsNotDeletedWhenUserDataIsUpdated() {
        val connection = BundledSQLiteDriver().open(":memory:")

        connection.execSQL(
            "CREATE TABLE IF NOT EXISTS `user-data` (`id` INTEGER NOT NULL, `fuelSelection` TEXT NOT NULL, `lastUpdate` INTEGER NOT NULL DEFAULT 0, `isOnboardingSuccess` INTEGER NOT NULL DEFAULT 0, `themeModeId` INTEGER NOT NULL DEFAULT 3, PRIMARY KEY(`id`))"
        )
        connection.execSQL(
            "INSERT INTO `user-data` (id, lastUpdate, isOnboardingSuccess, themeModeId, fuelSelection) VALUES (0, 0, 0, 3, 'GASOLINE_95')"
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
    @DisplayName("GIVEN vehicles in v14 WHEN migrating to v15 THEN vehicleType defaults to CAR and isPrincipal defaults to false")
    fun migrate14to15_addsVehicleTypeAndIsPrincipalWithDefaults() {
        val connection = BundledSQLiteDriver().open(":memory:")

        connection.execSQL(
            "CREATE TABLE IF NOT EXISTS `user-data` (`id` INTEGER NOT NULL, `lastUpdate` INTEGER NOT NULL DEFAULT 0, `isOnboardingSuccess` INTEGER NOT NULL DEFAULT 0, `themeModeId` INTEGER NOT NULL DEFAULT 3, PRIMARY KEY(`id`))"
        )
        connection.execSQL(
            "CREATE TABLE IF NOT EXISTS `vehicles` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `userId` INTEGER NOT NULL, `name` TEXT, `fuelType` TEXT NOT NULL, `tankCapacity` INTEGER NOT NULL, FOREIGN KEY(`userId`) REFERENCES `user-data`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )"
        )
        connection.execSQL(
            "CREATE INDEX IF NOT EXISTS `index_vehicles_userId` ON `vehicles` (`userId`)"
        )
        connection.execSQL(
            "INSERT INTO `user-data` (id, lastUpdate, isOnboardingSuccess, themeModeId) VALUES (0, 0, 1, 3)"
        )
        connection.execSQL(
            "INSERT INTO `vehicles` (id, userId, name, fuelType, tankCapacity) VALUES (1, 0, NULL, 'GASOLINE_95', 40)"
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
    @DisplayName("GIVEN multiple vehicles per user in v15 WHEN migrating to v16 THEN first vehicle per user is marked as principal")
    fun migrate15to16_setsFirstVehiclePerUserAsPrincipal() {
        val connection = BundledSQLiteDriver().open(":memory:")

        connection.execSQL(
            "CREATE TABLE IF NOT EXISTS `user-data` (`id` INTEGER NOT NULL, `lastUpdate` INTEGER NOT NULL DEFAULT 0, `isOnboardingSuccess` INTEGER NOT NULL DEFAULT 0, `themeModeId` INTEGER NOT NULL DEFAULT 3, PRIMARY KEY(`id`))"
        )
        connection.execSQL(
            "CREATE TABLE IF NOT EXISTS `vehicles` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `userId` INTEGER NOT NULL, `name` TEXT, `fuelType` TEXT NOT NULL, `tankCapacity` INTEGER NOT NULL, `vehicleType` TEXT NOT NULL DEFAULT 'CAR', `isPrincipal` INTEGER NOT NULL DEFAULT 0, FOREIGN KEY(`userId`) REFERENCES `user-data`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )"
        )
        connection.execSQL(
            "CREATE INDEX IF NOT EXISTS `index_vehicles_userId` ON `vehicles` (`userId`)"
        )
        connection.execSQL(
            "INSERT INTO `user-data` (id, lastUpdate, isOnboardingSuccess, themeModeId) VALUES (0, 0, 1, 3)"
        )
        connection.execSQL(
            "INSERT INTO `vehicles` (id, userId, name, fuelType, tankCapacity, vehicleType, isPrincipal) VALUES (1, 0, NULL, 'GASOLINE_95', 40, 'CAR', 0)"
        )
        connection.execSQL(
            "INSERT INTO `vehicles` (id, userId, name, fuelType, tankCapacity, vehicleType, isPrincipal) VALUES (2, 0, NULL, 'DIESEL', 50, 'CAR', 0)"
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
}
