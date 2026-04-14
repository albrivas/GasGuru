# Lessons Learned

## L001 — KMP: `api()` en `commonMain` no garantiza runtime en consumidores Android

**Fecha**: 2026-03-20
**Contexto**: Migración de `core:common` a KMP. `isStationOpen()` usa `kotlinx.datetime.Clock.System`.

**Error**: Declarar `api(libs.kotlinx.datetime)` en `commonMain.dependencies` de un módulo KMP NO garantiza que la librería esté en el classpath de runtime de los módulos Android consumidores. Resultado: `NoClassDefFoundError: Failed resolution of: Lkotlinx/datetime/Clock$System;` al navegar a la pantalla de detalle.

**Síntoma confuso**: La pantalla "parecía que iba a abrir pero no abría" — el outer `.catch` capturaba el error y emitía `DetailStationUiState.Error`, cuyo bloque en la UI estaba vacío.

**Causa raíz**: KMP `commonMain.dependencies { api(dep) }` garantiza visibilidad en compilación pero no siempre propaga la dependencia al runtime de módulos Android no-KMP que consumen el módulo.

**Fix aplicado**: Añadir un bloque `dependencies {}` estándar de Android (fuera del bloque `kotlin {}`) en el propio módulo KMP `core:common` con `implementation(libs.kotlinx.datetime)`. Las declaraciones dentro de `kotlin { sourceSets { commonMain/androidMain } }` NO alimentan el `debugRuntimeClasspath` de Android — solo lo hace el bloque `dependencies {}` a nivel Android.

**Verificación**: `./gradlew :core:common:dependencies --configuration debugRuntimeClasspath | grep datetime` debe mostrar `kotlinx-datetime-jvm`.

**Regla**: En módulos KMP con `gasguru.kmp.library`, las dependencias de `commonMain.dependencies { api/implementation(...) }` y `androidMain.dependencies { ... }` NO aparecen en el `debugRuntimeClasspath` Android. Para que una librería esté disponible en runtime Android, declararla en el bloque `dependencies {}` estándar (fuera de `kotlin {}`) del módulo KMP que la usa.

---

## L003 — Supabase (mock flavor) fuerza versión mayor de kotlinx-datetime

**Fecha**: 2026-03-22
**Contexto**: `NoClassDefFoundError: Clock$System` persistía incluso después de declarar `kotlinx-datetime` correctamente en `core:common`.

**Causa raíz**: El flavor `mock` incluye `supabase-kt:3.2.6`, que requiere `kotlinx-datetime:0.7.1`. Gradle resuelve la versión más alta, subiendo de `0.6.2` a `0.7.1`. En `0.7.1`, `kotlinx.datetime.Clock` fue deprecado y `Clock$System` cambió — la clase no existía de la misma forma en el JAR resuelto, causando el `NoClassDefFoundError` en runtime.

**Cómo detectarlo**: `./gradlew :app:dependencies --configuration mockDebugRuntimeClasspath | grep datetime` mostraba `0.6.2 -> 0.7.1`. El culpable estaba unos niveles más arriba en el árbol junto a `co.touchlab:kermit`.

**Fix**: Actualizar `kotlinxDatetime` en `libs.versions.toml` a `0.7.1` para que sea la versión declarada explícitamente. En `0.7.1`, `Clock` viene de `kotlin.time.Clock` (stdlib) en lugar de `kotlinx.datetime.Clock` — actualizar el import en `CommonUtils.kt`.

**Regla**: Cuando haya un `NoClassDefFoundError` persistente de una dependencia KMP, comprobar si algún flavor (especialmente `mock`) arrastra una versión mayor de esa librería con `./gradlew :app:dependencies --configuration <flavor>DebugRuntimeClasspath | grep <lib>`. El conflicto de versión puede cambiar la API o estructura de clases en runtime.

---

## L002 — Hipótesis especulativas sin evidencia: no proponer sin confirmar

**Fecha**: 2026-03-20
**Contexto**: Diagnóstico del bug de la pantalla de detalle.

**Error**: Se propusieron múltiples hipótesis incorrectas (horarios "24:00", proguard, SavedStateHandle, FuelStationBrandsType.first()...) sin confirmar el error real primero. El usuario tuvo que proporcionar el mensaje de error concreto.

**Regla**: Ante una pantalla que "no abre sin dar error", pedir siempre el logcat o mensaje de error real ANTES de proponer hipótesis. El stacktrace ahorra toda la especulación.

---

## L004 — Room KMP: `inMemoryDatabaseBuilder<T>()` sin contexto solo existe en JVM target

**Fecha**: 2026-04-07
**Contexto**: Migración de tests de DAO a KMP. Los tests usaban `Room.inMemoryDatabaseBuilder<GasGuruDatabase>()` sin contexto.

**Error**: Al poner los tests en `commonTest`, la compilación Android fallaba con `No value passed for parameter 'context'`. La API sin contexto de `inMemoryDatabaseBuilder` solo existe en el target JVM (Room KMP 2.8.x), no en Android. La variante Android sigue requiriendo `Context`.

**Causa raíz**: Room KMP tiene dos sobrecargas: `inMemoryDatabaseBuilder(context, klass)` para Android e `inMemoryDatabaseBuilder<T>()` sin argumentos para JVM/desktop. `commonTest` compila para todos los targets, por lo que el compilador Android no encuentra la sobrecarga sin contexto.

**Fix**: Mover todos los tests de DAO y migración a `jvmTest` (source set exclusivo del target JVM). Añadir `jvm()` al módulo KMP, declarar `jvmTest.dependencies` en `build.gradle.kts` y `kspJvm` para Room.

**Regla**: Los tests de Room KMP que usan `BundledSQLiteDriver` e `inMemoryDatabaseBuilder<T>()` sin contexto deben vivir en `jvmTest`, nunca en `commonTest` ni `androidUnitTest`.

---

## L005 — BundledSQLiteDriver falla en androidUnitTest con UnsatisfiedLinkError

**Fecha**: 2026-04-07
**Contexto**: Tests de DAO intentaban ejecutarse como Android unit tests (JVM host).

**Error**: `UnsatisfiedLinkError: no sqliteJni in java.library.path`. El artefacto `jvmAndroid` de `sqlite-bundled` intenta cargar una librería JNI compilada para ARM Android, que no puede cargarse en la JVM del host (macOS/Linux).

**Causa raíz**: `androidUnitTest` usa el artefacto `jvmAndroid` de `sqlite-bundled`, que contiene un `.so` de Android (ARM). El target `jvm` usa el artefacto nativo del host.

**Fix**: Mover tests a `jvmTest` (target JVM puro). En ese source set, `sqlite-bundled` resuelve la variante nativa del host en lugar de la de Android.

**Regla**: `BundledSQLiteDriver` en tests solo funciona en `jvmTest`. En `androidUnitTest` (test JVM que simula Android) falla con `UnsatisfiedLinkError` porque el JNI que intenta cargar es para ARM.

---

## L006 — `OnConflictStrategy.REPLACE` solo actúa sobre conflictos de clave primaria

**Fecha**: 2026-04-07
**Contexto**: `FilterEntity` tenía `@PrimaryKey(autoGenerate = true) val id: Long` y el DAO usaba `@Insert(onConflict = OnConflictStrategy.REPLACE)`.

**Error**: Insertar dos `FilterEntity` con el mismo `type` producía dos filas en lugar de reemplazar la existente. El test `insertFilter_duplicateType_replacesExistingFilter` fallaba con `expected: <1> but was: <2>`.

**Causa raíz**: `REPLACE` en Room/SQLite solo reemplaza cuando hay un conflicto en la clave primaria o en una restricción `UNIQUE`. Como `id` era autoincremental, cada insert generaba un `id` distinto — nunca había conflicto.

**Fix**: Hacer `type: FilterType` la `@PrimaryKey` (sin autoincremento). Añadir `MIGRATION_16_17` que recrea la tabla `filter` con `type` como clave primaria y migra los datos existentes.

**Regla**: Antes de confiar en `OnConflictStrategy.REPLACE` para "upsert por campo de negocio", verificar que ese campo ES la clave primaria o tiene restricción `UNIQUE`. Si no, `REPLACE` no tiene efecto.

---

## L007 — `COLLATE NOCASE` en SQLite debe ir después de la columna, antes de `IN`

**Fecha**: 2026-04-07
**Contexto**: Query de filtro de marca en `FuelStationDao` con `AND brandStation IN (:brands) COLLATE NOCASE`.

**Error**: El filtro case-insensitive devolvía 0 resultados cuando la marca de la BD era en minúsculas y el filtro en mayúsculas. El test `getFuelStations_brandFilterCaseInsensitive_returnsMatchingStations` fallaba con `expected: <1> but was: <0>`.

**Causa raíz**: En SQLite, la sintaxis correcta para aplicar una collation a un `IN` es `columna COLLATE NOCASE IN (...)`. La sintaxis `columna IN (...) COLLATE NOCASE` es inválida o no tiene el efecto esperado — el `COLLATE` queda fuera de la expresión de comparación.

**Fix**: Cambiar `AND brandStation IN (:brands) COLLATE NOCASE` por `AND brandStation COLLATE NOCASE IN (:brands)`.

**Regla**: En SQLite, `COLLATE collation-name` debe ir inmediatamente después de la expresión de la columna, no al final de la cláusula. Forma correcta: `expr COLLATE NOCASE IN (...)` o `expr COLLATE NOCASE = ?`.

---

## L008 — KMP: MockK y JUnit5 son JVM-only; en commonTest se usan fakes y kotlin.test

**Fecha**: 2026-04-09
**Contexto**: Migración de tests de módulos Android a KMP (Phase 4).

**Regla**: Al migrar un módulo a KMP, los tests que van a `commonTest` deben abandonar MockK y JUnit5:
- **MockK** → No soporta Kotlin/Native. En `commonTest` se usan **fakes escritos a mano** que implementan las interfaces.
- **JUnit5** (`@Test`, `@BeforeEach`, `@DisplayName` de `org.junit.jupiter`) → No existe en KMP. En `commonTest` se usa **`kotlin.test`** (`kotlin.test.Test`, `kotlin.test.BeforeTest`, etc.).
- **`runTest`** de `kotlinx-coroutines-test` → SÍ es KMP-compatible, se puede usar en `commonTest`.
- **`ktor-client-mock` (`MockEngine`)** → SÍ es KMP-compatible, se puede usar en `commonTest`.
- **`classLoader!!.getResourceAsStream(...)`** → JVM-only. En `commonTest` se inlinea el JSON directamente en el fichero de stub.

**Qué va dónde**:
- `commonTest` → `kotlin.test` + fakes manuales + `runTest` + `MockEngine` (Ktor). Tests de lógica compartida.
- `jvmTest` → `kotlin.test` + JUnit5 + MockK + `classLoader`. Tests JVM-específicos (Room DAO, etc.).
- `androidUnitTest` → JUnit5 + MockK. Tests con APIs Android o que dependen de `core:testing` (que es Android-only).

**Implicación de diseño**: Migrar tests a `commonTest` obliga a diseñar la testabilidad con interfaces + fakes, lo que mejora la arquitectura general.

**Regla práctica**: Al migrar un módulo a KMP, revisar cada test y decidir si puede ir a `commonTest` (reemplazando MockK por fake + JUnit5 por kotlin.test) o si debe quedarse en `jvmTest`/`androidUnitTest` por dependencias de plataforma.

---

## L009 — KMP commonTest: carga de resources de test sin `classLoader`

**Fecha**: 2026-04-09
**Contexto**: Tests de `core:supabase` usan `classLoader!!.getResourceAsStream(...)` para cargar JSON fixtures. Al migrar a `commonTest`, `classLoader` no existe en Kotlin/Native.

**Opciones disponibles**:

**Opción A — JSON inlineado (simple, recomendado para fixtures pequeños)**:
```kotlin
object StubsSupabaseResponse {
    val fuelStationListSuccess = """[{"id":1,"province":"Albacete",...}]""".trimIndent()
}
```
Pro: Sin dependencias de plataforma. Contra: Difícil de mantener si el JSON es grande.

**Opción B — `expect/actual` para lectura de resources (patrón KMP formal)**:
```kotlin
// commonTest/kotlin/utils/TestResources.kt
expect fun readTestResource(path: String): String

// jvmTest/kotlin/utils/TestResources.kt
actual fun readTestResource(path: String): String =
    Thread.currentThread().contextClassLoader!!
        .getResourceAsStream(path)!!.bufferedReader().readText()

// iosTest/kotlin/utils/TestResources.kt
actual fun readTestResource(path: String): String {
    val resourcePath = NSBundle.mainBundle.pathForResource(
        path.substringBeforeLast("."), path.substringAfterLast(".")
    )!!
    return NSString.stringWithContentsOfFile(resourcePath, NSUTF8StringEncoding, null) as String
}
```
Los ficheros JSON se colocan en `src/commonTest/resources/` y cada plataforma los lee con su API nativa.

**Regla**: Para fixtures pequeños (< 200 líneas), inline con `trimIndent()`. Para fixtures grandes o reutilizados entre módulos, usar el patrón `expect/actual readTestResource(path)`.
