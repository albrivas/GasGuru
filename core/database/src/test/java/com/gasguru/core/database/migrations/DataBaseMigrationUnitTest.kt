package com.gasguru.core.database.migrations

import androidx.sqlite.db.SupportSQLiteDatabase
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class DataBaseMigrationUnitTest {

    @Test
    @DisplayName(
        "GIVEN migration 14 to 15 WHEN migrate is called THEN vehicleType and isPrincipal columns are added"
    )
    fun migration14to15AddsVehicleTypeAndIsPrincipalColumns() {
        val db = mockk<SupportSQLiteDatabase>(relaxed = true)

        MIGRATION_14_15.migrate(db)

        verify {
            db.execSQL("ALTER TABLE `vehicles` ADD COLUMN `vehicleType` TEXT NOT NULL DEFAULT 'CAR'")
        }
        verify {
            db.execSQL("ALTER TABLE `vehicles` ADD COLUMN `isPrincipal` INTEGER NOT NULL DEFAULT 0")
        }
    }
}
