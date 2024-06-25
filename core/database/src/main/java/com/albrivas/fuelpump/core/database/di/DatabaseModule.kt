package com.albrivas.fuelpump.core.database.di

import android.content.Context
import androidx.room.Room
import com.albrivas.fuelpump.core.database.FuelPumpDatabase
import com.albrivas.fuelpump.core.database.migrations.MIGRATION_2_3
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): FuelPumpDatabase {
        return Room.databaseBuilder(
            appContext,
            FuelPumpDatabase::class.java,
            "fuel-pump-database"
        ).addMigrations(MIGRATION_2_3).build()
    }
}
