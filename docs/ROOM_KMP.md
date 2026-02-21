# Room KMP — Migración de core/database

Room `2.7.x` soporta KMP oficialmente. Las anotaciones `@Database`, `@Dao` y `@Entity` funcionan en `commonMain`. El driver SQLite es platform-specific.

---

## Estructura de ficheros

```
core/database/
  src/
    commonMain/kotlin/com/gasguru/core/database/
      GasGuruDatabase.kt        ← @Database
      dao/                      ← @Dao interfaces
      entities/                 ← @Entity data classes
      converters/               ← @TypeConverter
      migrations/               ← Migration objects
      di/
        DaoModule.kt            ← val daoModule (koin-core)

    androidMain/kotlin/com/gasguru/core/database/
      di/
        DatabaseModule.kt       ← Room.databaseBuilder + AndroidSQLiteDriver

    iosMain/kotlin/com/gasguru/core/database/
      di/
        DatabaseModule.kt       ← Room.databaseBuilder + NativeSQLiteDriver
```

---

## build.gradle.kts

```kotlin
plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.gasguru.room)   // gestiona ksp para Room codegen
    alias(libs.plugins.gasguru.koin)
}

kotlin {
    androidTarget()
    iosArm64()
    iosX64()
    iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
            implementation(libs.androidx.room.runtime)
            implementation(libs.koin.core)
        }
        androidMain.dependencies {
            implementation(libs.koin.android)
        }
    }
}

android {
    namespace = "com.gasguru.core.database"
}
```

---

## commonMain — esquema (sin cambios respecto a hoy)

```kotlin
// GasGuruDatabase.kt
@Database(entities = [FuelStationEntity::class, ...], version = 13)
abstract class GasGuruDatabase : RoomDatabase() {
    abstract fun fuelStationDao(): FuelStationDao
    // ...
}
```

```kotlin
// di/DaoModule.kt
val daoModule = module {
    single<FuelStationDao> { get<GasGuruDatabase>().fuelStationDao() }
    // ...
}
```

---

## androidMain — driver

```kotlin
// di/DatabaseModule.kt
val databaseModule = module {
    single {
        Room.databaseBuilder<GasGuruDatabase>(
            context = androidContext(),
            name = "fuel-pump-database",
        )
        .setDriver(AndroidSQLiteDriver())
        .addMigrations(MIGRATION_2_3, ..., MIGRATION_12_13)
        .build()
    }
}
```

---

## iosMain — driver

```kotlin
// di/DatabaseModule.kt
val databaseModule = module {
    single {
        val dbPath = NSHomeDirectory() + "/fuel-pump-database"
        Room.databaseBuilder<GasGuruDatabase>(name = dbPath)
            .setDriver(NativeSQLiteDriver())
            .addMigrations(MIGRATION_2_3, ..., MIGRATION_12_13)
            .build()
    }
}
```

---

## Qué cambia en GasGuruApplication

Nada. `databaseModule` y `daoModule` se siguen registrando igual en `startKoin { }`. En iOS, el equivalente sería el entry point de Koin para esa plataforma.

---

## Resumen

| | Android (hoy) | KMP |
|---|---|---|
| `@Entity`, `@Dao`, `@Database` | `androidMain` | `commonMain` ✅ |
| `Room.databaseBuilder` | `androidMain` | `androidMain` / `iosMain` |
| Driver | implícito | `AndroidSQLiteDriver` / `NativeSQLiteDriver` |
| `val daoModule` | `androidMain` | `commonMain` ✅ |
| `val databaseModule` | `androidMain` | platform-specific |