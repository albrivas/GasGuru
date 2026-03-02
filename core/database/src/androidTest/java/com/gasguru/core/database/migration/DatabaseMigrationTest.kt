package com.gasguru.core.database.migration

import android.content.Context
import androidx.room.testing.MigrationTestHelper
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import com.gasguru.core.database.GasGuruDatabase
import com.gasguru.core.database.migrations.MIGRATION_13_14
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class DatabaseMigrationTest {

    private val helper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        GasGuruDatabase::class.java,
    )

    @AfterEach
    fun tearDown() {
        ApplicationProvider.getApplicationContext<Context>().deleteDatabase(TEST_DB)
    }

    @Test
    @DisplayName("GIVEN user-data with fuelSelection in v13 WHEN migrating to v14 THEN vehicle is created with correct fuelType and capacity")
    fun migrate13to14_createsVehicleFromFuelSelection() {
        helper.createDatabase(TEST_DB, 13).apply {
            execSQL(
                "INSERT INTO `user-data` (id, lastUpdate, isOnboardingSuccess, themeModeId, fuelSelection) VALUES (0, 0, 1, 3, 'GASOLINE_95')"
            )
            close()
        }

        val db = helper.runMigrationsAndValidate(TEST_DB, 14, true, MIGRATION_13_14)

        val cursor = db.query("SELECT * FROM vehicles WHERE userId = 0")
        assertEquals(1, cursor.count)
        cursor.moveToFirst()
        assertEquals("GASOLINE_95", cursor.getString(cursor.getColumnIndexOrThrow("fuelType")))
        assertEquals(40, cursor.getInt(cursor.getColumnIndexOrThrow("tankCapacity")))
        cursor.close()
        db.close()
    }

    @Test
    @DisplayName("GIVEN user-data with DIESEL fuelSelection in v13 WHEN migrating to v14 THEN vehicle fuelType is DIESEL")
    fun migrate13to14_preservesDieselFuelType() {
        helper.createDatabase(TEST_DB, 13).apply {
            execSQL(
                "INSERT INTO `user-data` (id, lastUpdate, isOnboardingSuccess, themeModeId, fuelSelection) VALUES (0, 0, 1, 3, 'DIESEL')"
            )
            close()
        }

        val db = helper.runMigrationsAndValidate(TEST_DB, 14, true, MIGRATION_13_14)

        val cursor = db.query("SELECT * FROM vehicles WHERE userId = 0")
        cursor.moveToFirst()
        assertEquals("DIESEL", cursor.getString(cursor.getColumnIndexOrThrow("fuelType")))
        cursor.close()
        db.close()
    }

    @Test
    @DisplayName("GIVEN empty user-data table in v13 WHEN migrating to v14 THEN no vehicles are created")
    fun migrate13to14_withNoUsers_producesNoVehicles() {
        helper.createDatabase(TEST_DB, 13).close()

        val db = helper.runMigrationsAndValidate(TEST_DB, 14, true, MIGRATION_13_14)

        val cursor = db.query("SELECT * FROM vehicles")
        assertEquals(0, cursor.count)
        cursor.close()
        db.close()
    }

    @Test
    @DisplayName("GIVEN migrated vehicle in v14 WHEN updating user-data with UPDATE THEN vehicle is not cascade deleted")
    fun migrate13to14_vehicleIsNotDeletedWhenUserDataIsUpdated() {
        helper.createDatabase(TEST_DB, 13).apply {
            execSQL(
                "INSERT INTO `user-data` (id, lastUpdate, isOnboardingSuccess, themeModeId, fuelSelection) VALUES (0, 0, 0, 3, 'GASOLINE_95')"
            )
            close()
        }

        val db = helper.runMigrationsAndValidate(TEST_DB, 14, true, MIGRATION_13_14)

        // Simulate the fixed setOnboardingComplete() — uses UPDATE, not REPLACE
        db.execSQL("UPDATE `user-data` SET isOnboardingSuccess = 1 WHERE id = 0")

        val cursor = db.query("SELECT * FROM vehicles WHERE userId = 0")
        assertEquals(1, cursor.count)
        cursor.close()
        db.close()
    }

    companion object {
        private const val TEST_DB = "migration-test"
    }
}