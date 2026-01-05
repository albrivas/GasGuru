# Guía: Añadir un nuevo tipo de combustible

Esta guía explica cómo añadir un nuevo tipo de combustible a la aplicación GasGuru siguiendo todos los pasos necesarios.

## Ejemplo: Añadir AdBlue

Usaremos AdBlue como ejemplo, que viene de la API con el campo `Precio Adblue`.

---

## Paso 1: Añadir campo en NetworkPriceFuelStation

**Archivo:** `core/network/src/main/java/com/gasguru/core/network/model/NetworkPriceFuelStation.kt`

Añadir el campo que mapea desde la API (respetando el nombre exacto del campo JSON):

```kotlin
@Json(name = "Precio Hidrogeno")
val priceHydrogen: String,
@Json(name = "Precio Adblue")  // ← NUEVO
val priceAdblue: String,        // ← NUEVO
@Json(name = "Provincia")
val province: String,
```

---

## Paso 2: Añadir campo en FuelStationEntity

**Archivo:** `core/database/src/main/java/com/gasguru/core/database/model/FuelStationEntity.kt`

### 2.1 Añadir propiedad en la entidad

```kotlin
val priceGasoline98E5: Double,
val priceHydrogen: Double,
@ColumnInfo(defaultValue = "0.0")  // ← IMPORTANTE: valor por defecto
val priceAdblue: Double,           // ← NUEVO
val province: String,
```

> **⚠️ Importante:** Usar `@ColumnInfo(defaultValue = "0.0")` para campos añadidos vía migración.

### 2.2 Actualizar función asExternalModel()

```kotlin
fun FuelStationEntity.asExternalModel() = FuelStation(
    // ... otros campos
    priceHydrogen = priceHydrogen,
    priceAdblue = priceAdblue,  // ← NUEVO
    province = province,
    // ... resto
)
```

---

## Paso 3: Añadir campo en FuelStation (Domain)

**Archivo:** `core/model/src/main/java/com/gasguru/core/model/data/FuelStation.kt`

### 3.1 Añadir propiedad

```kotlin
val priceGasoline98E5: Double,
val priceHydrogen: Double,
val priceAdblue: Double,  // ← NUEVO
val province: String,
```

### 3.2 Actualizar función previewFuelStationDomain()

```kotlin
fun previewFuelStationDomain(idServiceStation: Int = 0) = FuelStation(
    // ... otros campos
    priceHydrogen = 0.0,
    priceAdblue = 0.999,  // ← NUEVO (valor de ejemplo)
    province = "",
    // ... resto
)
```

---

## Paso 4: Actualizar FuelStationMapper

**Archivo:** `core/data/src/main/java/com/gasguru/core/data/mapper/FuelStationMapper.kt`

```kotlin
fun NetworkPriceFuelStation.asEntity() = FuelStationEntity(
    // ... otros campos
    priceHydrogen = priceHydrogen.toSafeDouble(),
    priceAdblue = priceAdblue.toSafeDouble(),  // ← NUEVO
    province = province,
    // ... resto
)
```

---

## Paso 5: Añadir al enum FuelType

**Archivo:** `core/model/src/main/java/com/gasguru/core/model/data/FuelType.kt`

```kotlin
enum class FuelType(
    val extractPrice: (FuelStation) -> Double,
) {
    GASOLINE_95({ it.priceGasoline95E5 }),
    // ... otros tipos
    GASOIL_B({ it.priceGasoilB }),
    ADBLUE({ it.priceAdblue }),  // ← NUEVO
}
```

---

## Paso 6: Añadir UI en FuelTypeUiModel

**Archivo:** `core/ui/src/main/java/com/gasguru/core/ui/models/FuelTypeUiModel.kt`

```kotlin
val ALL_FUELS = listOf(
    // ... otros combustibles
    FuelTypeUiModel(
        type = FuelType.GASOIL_B,
        translationRes = R.string.gasoil_b,
        iconRes = RUikit.drawable.ic_gasoleo_b,
        noPriceRes = R.string.sin_gasoleo_b
    ),
    FuelTypeUiModel(                            // ← NUEVO
        type = FuelType.ADBLUE,
        translationRes = R.string.adblue,
        iconRes = RUikit.drawable.ic_adblue,    // Crear o reutilizar icono
        noPriceRes = R.string.sin_adblue
    ),
)
```

---

## Paso 7: Añadir strings de traducción

### 7.1 Inglés

**Archivo:** `core/ui/src/main/res/values/strings.xml`

```xml
<string name="gasoil_b">Gasoil B</string>
<string name="adblue">AdBlue</string>  <!-- NUEVO -->
<string name="select_fuel_preference">Elige tu tipo de combustible</string>

<!-- ... más abajo -->

<string name="sin_gasoleo_b">Without Gasoil B</string>
<string name="sin_adblue">Without AdBlue</string>  <!-- NUEVO -->
<string name="theme_mode_light">Light</string>
```

### 7.2 Español

**Archivo:** `core/ui/src/main/res/values-es-rES/strings.xml`

```xml
<string name="gasoil_b">Gasóleo B</string>
<string name="adblue">AdBlue</string>  <!-- NUEVO -->
<string name="select_fuel_preference">Elige tu tipo de combustible</string>

<!-- ... más abajo -->

<string name="sin_gasoleo_b">Sin Gasóleo B</string>
<string name="sin_adblue">Sin AdBlue</string>  <!-- NUEVO -->
<string name="theme_mode_light">Claro</string>
```

---

## Paso 8: Crear migración de base de datos

### 8.1 Añadir constante de versión

**Archivo:** `core/database/src/main/java/com/gasguru/core/database/migrations/DataBaseMigration.kt`

```kotlin
const val DB_VERSION_11 = 11
const val DB_VERSION_12 = 12
const val DB_VERSION_13 = 13  // ← NUEVO
```

### 8.2 Crear objeto de migración

```kotlin
internal val MIGRATION_11_12 = object : Migration(DB_VERSION_11, DB_VERSION_12) {
    // ... migración anterior
}

internal val MIGRATION_12_13 = object : Migration(DB_VERSION_12, DB_VERSION_13) {  // ← NUEVO
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE 'fuel-station' ADD COLUMN 'priceAdblue' REAL NOT NULL DEFAULT 0.0")
    }
}
```

> **📝 Nota:** El tipo `REAL` en SQLite corresponde a `Double` en Kotlin.

---

## Paso 9: Registrar migración y actualizar versión

### 9.1 Importar la migración

**Archivo:** `core/database/src/main/java/com/gasguru/core/database/di/DatabaseModule.kt`

```kotlin
import com.gasguru.core.database.migrations.MIGRATION_10_11
import com.gasguru.core.database.migrations.MIGRATION_11_12
import com.gasguru.core.database.migrations.MIGRATION_12_13  // ← NUEVO
import com.gasguru.core.database.migrations.MIGRATION_2_3
```

### 9.2 Añadir al builder

```kotlin
fun provideAppDatabase(@ApplicationContext appContext: Context): GasGuruDatabase {
    return Room.databaseBuilder(
        appContext,
        GasGuruDatabase::class.java,
        "fuel-pump-database"
    ).addMigrations(
        MIGRATION_2_3,
        // ... otras migraciones
        MIGRATION_11_12,
        MIGRATION_12_13,  // ← NUEVO
    ).build()
}
```

### 9.3 Actualizar versión de la base de datos

**Archivo:** `core/database/src/main/java/com/gasguru/core/database/GasGuruDatabase.kt`

```kotlin
@Database(
    entities = [
        FuelStationEntity::class,
        UserDataEntity::class,
        RecentSearchQueryEntity::class,
        FavoriteStationEntity::class,
        FilterEntity::class,
        PriceAlertEntity::class,
    ],
    version = 13,  // ← CAMBIAR de 12 a 13
    exportSchema = true,
)
```

---

## Paso 10: ⚠️ CRÍTICO - Actualizar queries en FuelStationDao

**Archivo:** `core/database/src/main/java/com/gasguru/core/database/dao/FuelStationDao.kt`

Este es el paso más importante y fácil de olvidar. Hay **3 queries** que deben actualizarse:

### 10.1 getFuelStationsWithoutBrandFilter

```kotlin
@Query(
    "SELECT * FROM `fuel-station` WHERE " +
        "(" +
        "(:fuelType = 'GASOLINE_95' AND priceGasoline95E5 > 0) OR " +
        // ... otros tipos
        "(:fuelType = 'GASOIL_B' AND priceGasoilB > 0) OR " +
        "(:fuelType = 'ADBLUE' AND priceAdblue > 0)" +  // ← NUEVO
        ")"
)
```

### 10.2 getFuelStationsWithBrandFilter

```kotlin
@Query(
    "SELECT * FROM `fuel-station` WHERE " +
        "(" +
        "(:fuelType = 'GASOLINE_95' AND priceGasoline95E5 > 0) OR " +
        // ... otros tipos
        "(:fuelType = 'GASOIL_B' AND priceGasoilB > 0) OR " +
        "(:fuelType = 'ADBLUE' AND priceAdblue > 0)" +  // ← NUEVO
        ")" +
        "AND brandStation IN (:brands) COLLATE NOCASE"
)
```

### 10.3 getFuelStationsInBounds

```kotlin
@Query(
    "SELECT * FROM `fuel-station` WHERE " +
        "latitude BETWEEN :minLat AND :maxLat AND " +
        "longitudeWGS84 BETWEEN :minLng AND :maxLng AND " +
        "(" +
        "(:fuelType = 'GASOLINE_95' AND priceGasoline95E5 > 0) OR " +
        // ... otros tipos
        "(:fuelType = 'GASOIL_B' AND priceGasoilB > 0) OR " +
        "(:fuelType = 'ADBLUE' AND priceAdblue > 0)" +  // ← NUEVO
        ")"
)
```

> **⚠️ IMPORTANTE:** Si olvidas este paso, el combustible aparecerá en el selector pero no cargará ninguna gasolinera en el mapa.

---

## Paso 11: Actualizar tests

### 11.1 FuelStationDaoTest

**Archivo:** `core/database/src/androidTest/java/com/gasguru/core/database/dao/FuelStationDaoTest.kt`

Actualizar la función helper `testFuelStationEntity` para incluir el nuevo campo:

```kotlin
private fun testFuelStationEntity(brand: String, isFavorite: Boolean, idServiceStation: Int) =
    FuelStationEntity(
        // ... otros campos
        priceGasoline98E5 = 124.125,
        priceHydrogen = 126.127,
        priceAdblue = 128.129,  // ← NUEVO
        province = "luptatum",
        // ... resto
    )
```

> **⚠️ Importante:** Si no actualizas los tests, obtendrás errores en el CI/CD al pasar los test.

---

## Checklist completo

- [ ] NetworkPriceFuelStation: campo con @Json
- [ ] FuelStationEntity: propiedad + @ColumnInfo(defaultValue)
- [ ] FuelStationEntity: actualizar asExternalModel()
- [ ] FuelStation (domain): propiedad
- [ ] FuelStation: actualizar previewFuelStationDomain()
- [ ] FuelStationMapper: mapeo en asEntity()
- [ ] FuelType enum: nueva entrada
- [ ] FuelTypeUiModel: entrada en ALL_FUELS
- [ ] Strings (EN): nombre + sin_nombre
- [ ] Strings (ES): nombre + sin_nombre
- [ ] DataBaseMigration: constante DB_VERSION_X
- [ ] DataBaseMigration: objeto MIGRATION_X_Y
- [ ] DatabaseModule: importar migración
- [ ] DatabaseModule: añadir a addMigrations()
- [ ] GasGuruDatabase: incrementar version
- [ ] FuelStationDao: 3 queries SQL actualizadas
- [ ] FuelStationDaoTest: actualizar testFuelStationEntity helper

---

## Archivos involucrados

### Core - Network
- `core/network/src/main/java/com/gasguru/core/network/model/NetworkPriceFuelStation.kt`

### Core - Database
- `core/database/src/main/java/com/gasguru/core/database/model/FuelStationEntity.kt`
- `core/database/src/main/java/com/gasguru/core/database/dao/FuelStationDao.kt` ⚠️
- `core/database/src/main/java/com/gasguru/core/database/GasGuruDatabase.kt`
- `core/database/src/main/java/com/gasguru/core/database/migrations/DataBaseMigration.kt`
- `core/database/src/main/java/com/gasguru/core/database/di/DatabaseModule.kt`

### Core - Model
- `core/model/src/main/java/com/gasguru/core/model/data/FuelStation.kt`
- `core/model/src/main/java/com/gasguru/core/model/data/FuelType.kt`

### Core - Data
- `core/data/src/main/java/com/gasguru/core/data/mapper/FuelStationMapper.kt`

### Core - UI
- `core/ui/src/main/java/com/gasguru/core/ui/models/FuelTypeUiModel.kt`
- `core/ui/src/main/res/values/strings.xml`
- `core/ui/src/main/res/values-es-rES/strings.xml`

### Tests
- `core/database/src/androidTest/java/com/gasguru/core/database/dao/FuelStationDaoTest.kt`

---

## Troubleshooting

### El combustible no aparece en la lista
- Verificar que se añadió al enum FuelType
- Verificar que se añadió a FuelTypeUiModel.ALL_FUELS

### El combustible aparece pero no carga gasolineras
- **Causa más común:** Olvidaste actualizar las queries en FuelStationDao
- Verificar las 3 queries mencionadas en el Paso 10

### Error de migración en la app
- Verificar que la versión de DB se incrementó
- Verificar que la migración está registrada en DatabaseModule
- Verificar que el SQL de la migración es correcto

### Crash al cambiar de combustible
- Verificar que todos los mappers están actualizados
- Verificar que la función extractPrice en FuelType es correcta