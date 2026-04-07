# Room KMP — Conceptos clave y cambios respecto a Room Android

Room `2.7+` soporta KMP oficialmente. Las anotaciones `@Database`, `@Dao` y `@Entity` funcionan en `commonMain` y son compartibles con iOS. Este documento explica los cambios conceptuales más importantes y por qué existen.

---

## 1. `@ConstructedBy` — el cambio más confuso

### Antes (Room Android)

Room generaba una clase `GasGuruDatabase_Impl` y la encontraba por reflexión. Bastaba con:

```kotlin
@Database(entities = [...], version = 16)
abstract class GasGuruDatabase : RoomDatabase()
```

Room sabía cómo instanciarla internamente (vía reflexión JVM).

### Ahora (Room KMP)

KMP prohíbe reflexión (iOS no la tiene). Room necesita saber en tiempo de compilación cómo construir la BD. Para eso se usa `@ConstructedBy`:

```kotlin
// commonMain — tú escribes esto
@Suppress("KotlinNoActualForExpect")
expect object GasGuruDatabaseConstructor : RoomDatabaseConstructor<GasGuruDatabase> {
    override fun initialize(): GasGuruDatabase
}

@ConstructedBy(GasGuruDatabaseConstructor::class)
abstract class GasGuruDatabase : RoomDatabase()
```

```kotlin
// androidMain — Room KSP lo genera automáticamente en build/generated/ksp/
actual object GasGuruDatabaseConstructor : RoomDatabaseConstructor<GasGuruDatabase> {
    override fun initialize(): GasGuruDatabase = GasGuruDatabase_Impl()
}
```

**Clave:** el `actual` nunca lo escribes tú. Room KSP lo genera durante la compilación. El `@Suppress("KotlinNoActualForExpect")` suprime el warning del compilador de Kotlin que avisa de que el `actual` no existe en `src/` — porque existe en `build/generated/ksp/`.

**Flujo en runtime:**
```
Room.databaseBuilder<GasGuruDatabase>(...)
  → lee @ConstructedBy(GasGuruDatabaseConstructor::class)
  → llama GasGuruDatabaseConstructor.initialize()
  → devuelve GasGuruDatabase_Impl()   ← también generado por KSP
```

---

## 2. API de migraciones: `SupportSQLiteDatabase` → `SQLiteConnection`

### Antes (Room Android)

```kotlin
object MIGRATION_13_14 : Migration(13, 14) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `vehicles` (...)")
    }
}
```

`SupportSQLiteDatabase` es una clase Android-only del paquete `androidx.sqlite.db`.

### Ahora (Room KMP)

```kotlin
object MIGRATION_13_14 : Migration(13, 14) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL("CREATE TABLE IF NOT EXISTS `vehicles` (...)")
    }
}
```

`SQLiteConnection` es del paquete `androidx.sqlite` (KMP). `execSQL` es una extension function de `androidx.sqlite`. El SQL en sí no cambia, solo el parámetro.

Lo mismo aplica a `RoomDatabase.Callback`:

```kotlin
// Antes
override fun onCreate(db: SupportSQLiteDatabase)

// Ahora
override fun onCreate(connection: SQLiteConnection)
```

---

## 3. Drivers SQLite — cuándo usar cada uno

En Room Android, el driver SQLite era implícito (usaba la implementación del SO). En Room KMP, hay que elegirlo explícitamente:

| Driver | Dónde usar | SQLite |
|--------|-----------|--------|
| `BundledSQLiteDriver` | **`jvmTest`** (tests KMP) | Compilado desde fuentes, versión fija y actualizada |
| `AndroidSQLiteDriver` | `androidMain` producción | El del sistema operativo Android |
| `NativeSQLiteDriver` | `iosMain` producción | El del sistema operativo iOS |

**Por qué `BundledSQLiteDriver` en tests:** permite ejecutar tests de Room en JVM (sin dispositivo, sin emulador) porque el driver incluye SQLite compilado como binario nativo para cada plataforma de escritorio.

**Importante:** `BundledSQLiteDriver` solo funciona correctamente en el target `jvm` (source set `jvmTest`). En `androidUnitTest` falla con `UnsatisfiedLinkError` porque el artefacto `jvmAndroid` intenta cargar una librería JNI de ARM Android.

```kotlin
// androidMain — producción
Room.databaseBuilder<GasGuruDatabase>(
    context = androidContext(),
    name = "fuel-pump-database",
)

// jvmTest — tests sin dispositivo
Room.inMemoryDatabaseBuilder<GasGuruDatabase>()
    .setDriver(BundledSQLiteDriver())
    .setQueryCoroutineContext(Dispatchers.IO)
    .build()
```

**Nota:** `Room.inMemoryDatabaseBuilder<T>()` sin `Context` es la nueva API KMP, pero **solo existe en el target JVM**. En `commonTest` (que compila también para Android), el compilador Android no la encuentra y falla. Usar siempre desde `jvmTest`.

---

## 4. APIs de tiempo: `System.currentTimeMillis()` → `kotlin.time.Clock`

`System.currentTimeMillis()` es JVM-only. Para valores por defecto en entidades se usa:

```kotlin
// Antes
val createdAt: Long = System.currentTimeMillis()

// Ahora
import kotlin.time.Clock
val createdAt: Long = Clock.System.now().toEpochMilliseconds()
```

Usar `kotlin.time.Clock` (stdlib de Kotlin 2.x), **no** `kotlinx.datetime.Clock` (tiene breaking changes en versiones recientes de la librería).

---

## 6. DI — cómo se divide Koin

| Archivo | Source set | Qué hace |
|---------|-----------|----------|
| `DaoModule.kt` | `commonMain` | Expone cada DAO desde `GasGuruDatabase`; código Koin puro |
| `DatabaseModule.kt` | `androidMain` | Crea la BD con `androidContext()` y las migraciones |

```kotlin
// commonMain/di/DaoModule.kt
val daoModule = module {
    single<FuelStationDao> { get<GasGuruDatabase>().fuelStationDao() }
    single<VehicleDao> { get<GasGuruDatabase>().vehicleDao() }
    // ...
}

// androidMain/di/DatabaseModule.kt
val databaseModule = module {
    single {
        Room.databaseBuilder<GasGuruDatabase>(
            context = androidContext(),
            name = "fuel-pump-database",
        ).addMigrations(...).addCallback(...).build()
    }
}
```

En `GasGuruApplication` se registran ambos igual que antes:
```kotlin
startKoin {
    modules(databaseModule, daoModule, ...)
}
```

---

## 7. Tests — sin dispositivo con BundledSQLiteDriver

Los tests de Room viven en **`jvmTest`** (source set del target JVM puro). No en `commonTest` ni en `androidUnitTest`.

**Por qué `jvmTest` y no `commonTest`:**
- `Room.inMemoryDatabaseBuilder<T>()` sin `Context` solo existe en el target JVM. En `commonTest`, el compilador Android no encuentra esta sobrecarga y falla con `No value passed for parameter 'context'`.

**Por qué `jvmTest` y no `androidUnitTest`:**
- `BundledSQLiteDriver` en el artefacto `jvmAndroid` intenta cargar un `.so` compilado para ARM Android, lo que causa `UnsatisfiedLinkError: no sqliteJni in java.library.path` en JVM de host (macOS/Linux). El artefacto del target `jvm` carga la versión nativa del host correctamente.

El patrón para **DAO tests** en `jvmTest`:

```kotlin
@BeforeEach
fun createDb() {
    db = Room.inMemoryDatabaseBuilder<GasGuruDatabase>()
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}
```

El patrón para **migration tests** sin `MigrationTestHelper` (que es Android-only):

```kotlin
fun testMigration() {
    val connection = BundledSQLiteDriver().open(":memory:")

    // Crear schema de la versión anterior manualmente
    connection.execSQL("CREATE TABLE IF NOT EXISTS `user-data` (...)")
    connection.execSQL("INSERT INTO `user-data` VALUES (0, 0, 1, 3, 'GASOLINE_95')")

    // Ejecutar la migración directamente
    MIGRATION_13_14.migrate(connection)

    // Verificar el resultado con queries directas
    val stmt = connection.prepare("SELECT fuelType FROM vehicles WHERE userId = 0")
    assertTrue(stmt.step())
    assertEquals("GASOLINE_95", stmt.getText(0))
    stmt.close()
    connection.close()
}
```

**Ventaja sobre `MigrationTestHelper`:** corre en JVM sin emulador, más rápido y usable en CI sin dispositivo.

---

## Resumen de cambios

| Concepto | Room Android | Room KMP |
|----------|-------------|----------|
| `@Entity`, `@Dao`, `@Database` | `androidMain` | `commonMain` |
| Instanciación de la BD | Reflexión automática | `@ConstructedBy` + KSP genera `actual` |
| API de migraciones | `SupportSQLiteDatabase` | `SQLiteConnection` |
| Driver SQLite | Implícito (SO Android) | `AndroidSQLiteDriver` / `BundledSQLiteDriver` |
| `System.currentTimeMillis()` | JVM-only | `kotlin.time.Clock` |
| Tests de DAO/migraciones | `androidInstrumentedTest` (necesita dispositivo) | `jvmTest` con `BundledSQLiteDriver` (JVM puro) |
