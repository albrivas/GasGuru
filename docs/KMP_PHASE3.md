# KMP Phase 3: `:core:database` — Room KMP Migration

## Objetivo

Migrar `:core:database` de Android-only a KMP para que entities, DAOs, type converters y migraciones vivan en `commonMain`, compartibles con iOS. Solo la instanciación de la base de datos es plataforma-específica.

**Rama**: `feature/kmp-phase-3`
**Módulo**: `:core:database`
**Plugins resultantes**: `gasguru.kmp.library` + `gasguru.kmp.room` + `kotlin.serialization`

---

## Decisiones de diseño

### 1. Upgrade de Room a 2.8.4

Se sube Room de `2.7.1` a `2.8.4` (última versión estable KMP, noviembre 2025) aprovechando la migración. No hay breaking changes entre `2.7.x` y `2.8.x`.

### 2. `@ConstructedBy` para Room KMP

Room KMP requiere anotación especial en la clase base de la BD para que el KSP genere la implementación concreta por plataforma:

```kotlin
// commonMain
@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object GasGuruDatabaseConstructor : RoomDatabaseConstructor<GasGuruDatabase>

@ConstructedBy(GasGuruDatabaseConstructor::class)
abstract class GasGuruDatabase : RoomDatabase() { ... }
```

El `@Suppress("NO_ACTUAL_FOR_EXPECT")` es necesario porque Room KSP genera los `actual` automáticamente (no los escribimos nosotros). Sin el suppress, el compilador da error de `actual` faltante.

### 3. Migración de `SupportSQLiteDatabase` → `SQLiteConnection`

Room KMP 2.7+ cambió la API de migraciones. El parámetro `SupportSQLiteDatabase` (Android-only) se reemplaza por `SQLiteConnection` (KMP, de `androidx.sqlite`):

```kotlin
// Antes (Android-only)
override fun migrate(db: SupportSQLiteDatabase) {
    db.execSQL("ALTER TABLE ...")
}

// Después (KMP)
override fun migrate(connection: SQLiteConnection) {
    connection.execSQL("ALTER TABLE ...")
}
```

La función `execSQL(sql)` es una extension function de `androidx.sqlite` sobre `SQLiteConnection`. Todas las 14 migraciones se actualizaron mecánicamente. El SQL queda idéntico.

El `RoomDatabase.Callback` también cambió:
```kotlin
// Antes
override fun onCreate(db: SupportSQLiteDatabase)
// Después
override fun onCreate(connection: SQLiteConnection)
```

### 4. Moshi → kotlinx-serialization en `ListConverters`

`Moshi` es JVM-only. Se reemplaza por `kotlinx-serialization-json` (KMP-compatible):

```kotlin
// Antes (Moshi)
private val adapter = moshi.adapter<List<String>>(listType)
fun fromList(list: List<String>): String = adapter.toJson(list)
fun toList(data: String): List<String> = adapter.fromJson(data) ?: emptyList()

// Después (kotlinx-serialization)
fun fromList(list: List<String>): String = Json.encodeToString(list)
fun toList(data: String): List<String> = try { Json.decodeFromString(data) } catch (_: Exception) { emptyList() }
```

**Compatibilidad de datos**: Ambas librerías producen JSON idéntico para `List<String>`: `["a","b","c"]`. No hay migración de datos.

### 5. `System.currentTimeMillis()` → `kotlin.time.Clock`

`PriceAlertEntity.createdAt` tenía `System.currentTimeMillis()` como valor por defecto (JVM-only). Se reemplaza por:

```kotlin
import kotlin.time.Clock
val createdAt: Long = Clock.System.now().toEpochMilliseconds()
```

**Importante**: usar `kotlin.time.Clock` (stdlib de Kotlin 2.x), NO `kotlinx.datetime.Clock` (deprecated en 0.7.1 donde `Clock$System` cambió de estructura). Ver lección L003 en `tasks/lessons.md`.

### 6. DI split: commonMain / androidMain / iosMain

| Archivo | Source set | Motivo |
|---------|-----------|--------|
| `DaoModule.kt` | commonMain | Koin puro, sin código de plataforma |
| `DatabaseModule.kt` | androidMain | Usa `androidContext()` de Koin Android |
| `DatabaseModule.kt` | iosMain | Usa `NSFileManager` para obtener el path del documento |

El `databaseBuilder` para Android usa la nueva API tipada:
```kotlin
Room.databaseBuilder<GasGuruDatabase>(
    context = androidContext(),
    name = "fuel-pump-database",
)
```

### 7. Tests

| Test | Source set | Motivo |
|------|-----------|--------|
| `ListConvertersTest` | commonTest | Lógica pura kotlinx-serialization |
| `UserDataConvertersTest` | commonTest | Lógica pura Kotlin, sin Android |
| DAO tests (7 ficheros) | **jvmTest** | `BundledSQLiteDriver` + `inMemoryDatabaseBuilder<T>()` sin contexto — solo disponibles en target JVM |
| `DatabaseMigrationTest` | **jvmTest** | `BundledSQLiteDriver` + `SQLiteConnection` directo, sin dispositivo |

> No hay tests en `androidInstrumentedTest` ni en `androidUnitTest`. Todos los tests de Room corren en JVM puro via `jvmTest`.

**Por qué `jvmTest` y no `commonTest` ni `androidUnitTest`:**
- `inMemoryDatabaseBuilder<T>()` sin `Context` no existe en el target Android — falla en compilación con `No value passed for parameter 'context'`.
- `BundledSQLiteDriver` en `androidUnitTest` falla en runtime con `UnsatisfiedLinkError` porque intenta cargar un `.so` ARM Android desde JVM de host.
- El target `jvm` (source set `jvmTest`) resuelve ambos problemas: la API sin contexto existe y `BundledSQLiteDriver` carga la versión nativa del host.

**Patrón DAO tests** (`BundledSQLiteDriver`):
```kotlin
db = Room.inMemoryDatabaseBuilder<GasGuruDatabase>()
    .setDriver(BundledSQLiteDriver())
    .setQueryCoroutineContext(Dispatchers.IO)
    .build()
```

**Patrón migration tests** (sin `MigrationTestHelper`):
```kotlin
val connection = BundledSQLiteDriver().open(":memory:")
// Crear schema de la versión anterior manualmente
connection.execSQL("CREATE TABLE IF NOT EXISTS `user-data` (...)")
// Llamar migration directamente
MIGRATION_13_14.migrate(connection)
// Verificar con queries SQL directas
val stmt = connection.prepare("SELECT fuelType FROM vehicles WHERE userId = 0")
assertTrue(stmt.step())
stmt.close()
connection.close()
```

---

## Estructura final de source sets

```
core/database/src/
├── commonMain/kotlin/com/gasguru/core/database/
│   ├── GasGuruDatabase.kt          (@ConstructedBy + expect constructor)
│   ├── converters/
│   │   ├── FilterTypeConverter.kt
│   │   ├── ListConverters.kt       (kotlinx-serialization)
│   │   └── UserDataConverters.kt
│   ├── dao/                        (7 DAOs, sin cambios en lógica)
│   ├── di/DaoModule.kt
│   ├── migrations/DataBaseMigration.kt  (SQLiteConnection API)
│   └── model/                      (8 entidades; PriceAlertEntity usa kotlin.time.Clock)
├── androidMain/kotlin/com/gasguru/core/database/
│   └── di/DatabaseModule.kt        (Room.databaseBuilder con androidContext())
├── iosMain/kotlin/com/gasguru/core/database/
│   └── di/DatabaseModule.kt        (Room.databaseBuilder con NSFileManager path)
├── commonTest/kotlin/com/gasguru/core/database/
│   ├── converters/ListConvertersTest.kt
│   └── converters/UserDataConvertersTest.kt
└── jvmTest/kotlin/com/gasguru/core/database/
    ├── dao/                        (7 DAO tests — BundledSQLiteDriver + inMemoryDatabaseBuilder sin Context)
    └── migration/DatabaseMigrationTest.kt  (BundledSQLiteDriver + SQLiteConnection directo)
```

---

## Dependencias añadidas/eliminadas

### Añadidas
- `kotlinx-serialization-json` en `commonMain` — reemplaza Moshi
- `kotlin.serialization` plugin — necesario para `kotlinx-serialization-json`
- Room `2.8.4` (upgrade de `2.7.1`)

### Eliminadas
- `moshi.kotlin` — reemplazado por kotlinx-serialization
- `gasguru.android.library` plugin — reemplazado por `gasguru.kmp.library`
- `gasguru.room` plugin — reemplazado por `gasguru.kmp.room`
- `gasguru.proguard` plugin — ofuscación a nivel de app, no de módulo KMP
- `junit5` plugin — `gasguru.kmp.library` ya configura `useJUnitPlatform()`
- `mockk` de compilación principal — movido a `androidUnitTest.dependencies`

---

## Criterio de "done"

- [x] Plugin cambiado a `gasguru.kmp.library` + `gasguru.kmp.room`
- [x] Todos los archivos en source sets correctos (sin `src/main/java/`)
- [x] `ListConverters` sin Moshi
- [x] 14 migraciones con `SQLiteConnection` API
- [x] `PriceAlertEntity` sin `System.currentTimeMillis()`
- [x] `@ConstructedBy` en `GasGuruDatabase`
- [x] DI split: androidMain + iosMain + commonMain
- [x] `./gradlew :core:database:assembleDebug` ✅
- [x] `./gradlew :core:database:compileKotlinIosSimulatorArm64` ✅
- [x] `./gradlew :core:data:assembleDebug` ✅ (downstream)
- [x] Tests comunes en commonTest
- [x] DAO tests en `jvmTest` con `BundledSQLiteDriver` (sin dispositivo)
- [x] `DatabaseMigrationTest` en `jvmTest` con `BundledSQLiteDriver` (sin dispositivo)
- [x] `./gradlew :core:database:jvmTest` ✅ (sin dispositivo)
