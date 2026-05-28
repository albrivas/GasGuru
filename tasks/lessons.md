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

---

## L010 — KMP: `dataModule` renombrado tras migración rompe tests del `app`

**Fecha**: 2026-04-21
**Contexto**: Migración de `core:data` a KMP (Phase 4c). El módulo Koin `dataModule` fue dividido en `commonDataModule` + `androidDataModule`.

**Error**: `KoinModulesTest.kt` (en `:app`) seguía importando `com.gasguru.core.data.di.dataModule`, que ya no existía. El build de CI fallaba con:
```
e: Unresolved reference 'dataModule'
```
en `app/src/test/kotlin/com/gasguru/KoinModulesTest.kt:9` y `:61`.

**Causa raíz**: Al migrar un módulo a KMP y dividir su DI, solo se actualizó `GasGuruApplication.kt` (producción) pero no `KoinModulesTest.kt` (tests del `:app`).

**Fix**: Reemplazar `dataModule` por `commonDataModule` + `androidDataModule` en el test, igual que en la `Application`.

**Regla**: Al renombrar o dividir un módulo Koin durante una migración KMP, buscar siempre todas las referencias con `grep -r "dataModule"` (o el nombre afectado) en el proyecto completo — incluyendo `src/test/` — antes de dar la tarea por terminada. No basta con actualizar el punto de entrada de producción.

---

## L011 — Koin-Detekt: el ID del rule set en detekt.yml debe coincidir con `ruleSetId` del provider

**Fecha**: 2026-04-22
**Contexto**: Integración de `detekt-koin4-rules` en el proyecto.

**Error**: Usé nombres de categoría inventados (`KoinServiceLocator`, `KoinModuleDSL`, etc.) como claves de primer nivel en `detekt.yml`. Detekt los rechazó con "Property X is misspelled or does not exist".

**Causa raíz**: Detekt usa el valor de `RuleSetProvider.ruleSetId` como clave en el YAML. No hay un bloque por categoría; todas las reglas van anidadas bajo esa única clave.

**Fix**: Leer el `KoinRuleSetProvider.kt` del repo para obtener el `ruleSetId` real. En este caso: `koin-rules:` como clave raíz con todas las reglas dentro.

**Regla**: Antes de configurar un plugin de Detekt externo en `detekt.yml`, consultar el `RuleSetProvider` del repo para obtener el `ruleSetId` exacto.

---

## L012 — Worktrees: los edits deben ir a la ruta del worktree, no al repo principal

**Fecha**: 2026-04-22
**Contexto**: Tras crear un worktree con `EnterWorktree`, usé rutas absolutas al repo principal.

**Error**: Los cambios se aplicaron en el repo principal (rama equivocada) y el worktree quedó limpio. Hubo que revertir el repo principal y replicar los cambios en el worktree.

**Regla**: Tras `EnterWorktree`, todas las rutas de ficheros deben apuntar al directorio del worktree. Verificar con `git status` en el worktree antes de empezar.

---

## L013 — `org.gradle.java.home` es necesario cuando el terminal tiene un JAVA_HOME diferente al JDK de Gradle

**Fecha**: 2026-04-22
**Contexto**: Librería compilada con Java 21 en proyecto cuyo terminal tiene JAVA_HOME apuntando a Java 19.

**Error**: Cambiar `jvmTarget` a 21 NO cambia el JDK con el que corre el daemon de Gradle. El daemon usa `JAVA_HOME` del sistema y fallaba al cargar la librería.

**Fix**: Añadir `org.gradle.java.home=/ruta/al/jdk21` en `gradle.properties` para fijar el JDK del daemon a nivel de proyecto.

**Regla**: `jvmTarget` controla el bytecode generado. `org.gradle.java.home` controla qué JVM ejecuta el daemon. Son configuraciones distintas.

---

## L014 — Workers necesitan `KoinComponent`; suprimir `NoInjectDelegate` y `NoKoinComponentInterface`

**Fecha**: 2026-04-22
**Contexto**: Reglas de Koin-Detekt sobre `StationSyncWorker`.

**Motivo**: `CoroutineWorker` es instanciado por WorkManager, que no soporta constructor injection sin un `WorkerFactory` personalizado. `KoinComponent` + `by inject()` es el patrón correcto para Workers en Koin.

**Regla**: En clases que extiendan `CoroutineWorker`/`ListenableWorker`, suprimir `NoInjectDelegate` y `NoKoinComponentInterface` con `@Suppress`.

---

## L016 — Tras migración KMP: buscar TODOS los usos de `R.drawable`/`R.string` del módulo migrado antes de commitear

**Fecha**: 2026-04-27
**Contexto**: Migración de `core:uikit` a Compose Multiplatform. Los modelos cambian de `Int` (R.drawable/R.string) a `DrawableResource` y `String`.

**Error**: Se corrigieron los tests de `core:uikit` pero no se buscaron los tests de otros módulos (`feature:profile`) que también instancian esos modelos con los tipos antiguos. Resultado: varios ciclos de CI fallidos corrigiendo un archivo a la vez.

**Regla**: Tras cualquier migración KMP que cambie tipos de modelos, ejecutar `grep -rn "R\.drawable\|R\.string" --include="*.kt"` en todo el repo para encontrar TODOS los usos afectados y corregirlos en un único commit.

---

## L017 — `runBlocking { getString(StringResource) }` en ViewModel causa deadlock en tests de coroutines

**Fecha**: 2026-04-29
**Contexto**: Migración Phase 6A — `VehicleUiMapper` usaba `runBlocking { getString(res) }` para resolver `StringResource` en el ViewModel.

**Error**: `ProfileViewModelTest` fallaba con `TurbineTimeoutCancellationException` — el flujo `userData` nunca emitía `Success`. El dispatcher de tests se bloqueaba porque `runBlocking` lanzaba una coroutine que intentaba usar el mismo dispatcher.

**Causa raíz**: `org.jetbrains.compose.resources.getString` necesita inicialización de recursos que no existe en unit tests JVM. `runBlocking` en un test coroutines bloquea el dispatcher, produciendo un deadlock silencioso.

**Fix**: No resolver `StringResource` en el ViewModel. Pasar `StringResource` directamente al modelo (`VehicleItemCardModel.fuelTypeTranslationRes: StringResource`) y resolverlo en el componente Composable con `stringResource()`. Esto elimina `context: Context` del ViewModel completamente.

**Regla**: Los ViewModels no deben resolver `StringResource`. La resolución de strings es responsabilidad del composable. Si un modelo de UI necesita texto localizado, guardar `StringResource` y dejar que el composable lo resuelva.

---

## L015 — `MissingKoinStopInTest` genera falso positivo en clases `Application`

**Fecha**: 2026-04-22
**Contexto**: Regla `MissingKoinStopInTest` aplicada sobre `GasGuruApplication`.

**Error**: La regla detecta `startKoin {}` en `Application` y la trata como clase de test.

**Fix**: Suprimir `MissingKoinStopInTest` en la clase `Application` con `@Suppress`.

## L018 — `compose.materialIconsExtended` no está disponible transitivamente en módulos CMP consumers

**Fecha**: 2026-05-03
**Contexto**: Phase 6C — migración de `:core:components` a CMP.

**Error**: `:core:uikit` declara `implementation(compose.materialIconsExtended)` en commonMain. Al migrar `:core:components` y declararlo como `implementation(projects.core.uikit)`, los `Icons.*` no resuelven porque `implementation` no expone dependencias transitivas en KMP.

**Fix**: Añadir `compose.materialIconsExtended` al `KmpComposeLibraryConventionPlugin` en `commonMain.dependencies`. Así todos los módulos CMP que usen el plugin heredan los iconos automáticamente.

**Regla**: Si un módulo KMP A hace `implementation(compose.X)` y el módulo KMP B necesita los mismos tipos, B debe declarar la dependencia explícitamente (o el convention plugin debe incluirla si es de uso general).

---

## L019 — androidTest de módulo CMP: R.string ya no existe tras mover strings a composeResources

**Fecha**: 2026-05-05
**Contexto**: Migración de `:feature:profile` a CMP.

**Error**: `ProfileScreenTest.kt` importaba `com.gasguru.feature.profile.R` y usaba `R.string.version`. Al mover todos los strings a `composeResources/values/strings.xml`, la clase `R` del módulo ya no contiene esas entradas — el build de los instrumented tests fallaba con `Unresolved reference: version` en `R.string`.

**Causa raíz**: Las strings en `composeResources/values/strings.xml` generan accessors bajo `com.gasguru.<módulo>.generated.resources.Res.string.*`, no bajo `R.string`. El `R` de Android solo contiene recursos de `src/main/res/`.

**Fix**: En los `androidTest`, reemplazar `getStringResource(id = R.string.xxx, ...)` por `getCmpString(Res.string.xxx)` (o con args: `getCmpString(Res.string.xxx, arg1, arg2)`). Añadir overload `getCmpString(resource: StringResource, vararg formatArgs: Any)` a `BaseTest` si no existe.

**Regla**: Al migrar un módulo a CMP y mover strings a `composeResources`, actualizar inmediatamente todos los `androidTest` que referencien `R.string.<nombre>` del módulo migrado. Buscar con `grep -rn "R\.string\." <módulo>/src/androidTest/`.

---

## L020 — KMP 2.1+: `Clock.System` viene de `kotlin.time`, no de `kotlinx.datetime`

**Fecha**: 2026-05-07
**Contexto**: Migración de `DateUtils.kt` en `:feature:detail-station` a commonMain.

**Error**: `import kotlinx.datetime.Clock` + `Clock.System.now()` causaba `Unresolved reference 'System'` en `compileDebugKotlinAndroid` con Kotlin 2.3.0. El error no era de dependencia faltante — `libs.kotlinx.datetime` estaba declarado correctamente en `commonMain.dependencies`.

**Causa raíz**: En Kotlin 2.1+, `kotlin.time.Clock` fue añadido a la stdlib estándar. En `kotlinx-datetime 0.7.1`, `kotlinx.datetime.Clock` fue remodelado y `Clock.System` pasó a vivir en `kotlin.time.Clock`. El resto del proyecto (L003) ya usa `kotlin.time.Clock` — el import de `kotlinx.datetime.Clock` es obsoleto con este stack.

**Verificación**: `grep -rn "import kotlin.time.Clock" <proyecto>` muestra que `PriceAlertEntity.kt`, `CommonUtils.kt` y `FakeUserDataRepository.kt` ya usaban el import correcto.

**Fix**: Cambiar `import kotlinx.datetime.Clock` → `import kotlin.time.Clock`.

**Regla**: En proyectos con Kotlin 2.1+ y kotlinx-datetime 0.7.1+, usar siempre `import kotlin.time.Clock` para acceder a `Clock.System.now()`. `kotlinx.datetime.Clock` ya no expone `System` como sub-objeto.

---

## L021 — Room KMP en iOS: requiere BundledSQLiteDriver y fila inicial en user-data

**Fecha**: 2026-05-26
**Contexto**: Phase 8D — inicialización Koin en iOS.

**Error 1**: `InstanceCreationException: Could not create instance for SplashViewModel` — causa raíz: `Cannot create a RoomDatabase without providing a SQLiteDriver via setDriver()`.

**Error 2**: Splash bloqueado para siempre en la primera instalación iOS — causa raíz: la tabla `user-data` estaba vacía, el flow `userData()` nunca emitía.

**Fix**: En `DatabaseModule.kt` (iosMain), añadir `.setDriver(BundledSQLiteDriver())` y un `addCallback` con `onCreate` que inserta la fila inicial. Añadir `implementation(libs.androidx.sqlite.bundled)` a `iosMain.dependencies` del módulo `:core:database`.

**Regla**: En iOS, Room no tiene driver de sistema como en Android — hay que proveer `BundledSQLiteDriver` explícitamente. Si la base de datos necesita una fila inicial para funcionar, crearla en `onCreate` del callback; en iOS no hay mecanismo de pre-población automático.

---

## L022 — JUnit5 en commonTest rompe la compilación para targets iOS

**Fecha**: 2026-05-26
**Contexto**: Phase 8D — migración de tests de features a KMP commonTest.

**Error**: `Could not resolve org.junit.jupiter:junit-jupiter-api for :<feature>:iosSimulatorArm64Test` al tener `implementation(libs.junit5.api)` en `commonTest.dependencies`.

**Causa raíz**: JUnit5 es JVM-only. `commonTest` compila para todos los targets activos, incluido iOS. Gradle intenta resolver artefactos JVM puros para Kotlin/Native y falla.

**Fix**: Mover `junit5.api`, `junit5.extensions` de `commonTest.dependencies` → `androidUnitTest.dependencies` en cada feature module. Los tests comunes deben usar solo `kotlin("test")` en `commonTest`.

**Regla**: En módulos KMP con target iOS, ningún artefacto JVM-only puede estar en `commonTest`. JUnit5, Espresso, y similares van exclusivamente en `androidUnitTest` o `androidInstrumentedTest`.

---

## L023 — @BeforeTest/@AfterTest en commonMain requieren kotlin-test-annotations-common + bridge JVM

**Fecha**: 2026-05-26
**Contexto**: Phase 8D — clase base `CoroutineTest` en `commonMain` de `:core:testing`.

**Error**: `Unresolved reference 'BeforeTest'` / `Unresolved reference 'AfterTest'` al compilar `commonMain` para Android, aunque `api(kotlin("test"))` estaba declarado.

**Causa raíz**: `kotlin("test")` en `commonMain` no inyecta automáticamente `kotlin-test-annotations-common` cuando el módulo es una librería. Sin `kotlin-test-junit5` en `androidMain`, el compilador Android no encuentra los `actual` de esas anotaciones.

**Fix**:
```kotlin
commonMain.dependencies {
    api(kotlin("test-annotations-common"))  // expect declarations
}
androidMain.dependencies {
    api(kotlin("test-junit5"))              // actual JVM via JUnit5
}
```
iOS no necesita nada — el stdlib de Kotlin/Native incluye los actuals de `kotlin.test` automáticamente.

**Regla**: Para exportar una clase base de test con `@BeforeTest`/`@AfterTest` desde `commonMain` de un módulo librería KMP: `kotlin-test-annotations-common` en `commonMain` + `kotlin-test-junit5` en `androidMain`. No añadir nada en `iosMain`.

---

## L024 — Tests KMP: usar backtick names con GIVEN/WHEN/THEN en lugar de @DisplayName

**Fecha**: 2026-05-26
**Contexto**: Migración de tests de JUnit5 a kotlin.test en commonTest.

**Problema**: Al migrar de JUnit5 a `kotlin.test`, se eliminaron los `@DisplayName` pero los nombres de función quedaron cortos. En `kotlin.test` el nombre de función es el nombre del test en los reports.

**Fix**: Kotlin permite backtick names con espacios:
```kotlin
@Test
fun `GIVEN two vehicles WHEN userData emits THEN principal is first`() = runTest { ... }
```

**Regla**: Al migrar tests de JUnit5 a `kotlin.test`, renombrar todas las funciones `@Test` al formato `` `GIVEN ... WHEN ... THEN ...` `` con backticks. `@BeforeTest fun setUp()` no se renombra. No añadir `@DisplayName` (es JUnit5).

---

## L026 — `instrumentedTestVariant.sourceSetTree` solo en módulos con compose.uiTest

**Fecha**: 2026-05-28
**Contexto**: CI de Android tests fallaba con D8 en `:feature:detail-station:dexBuilderDebugAndroidTest`.

**Error**:
```
Space characters in SimpleName '...DetailStationViewModelTest$GIVEN a station WHEN...$1'
are not allowed prior to DEX version 040
```

**Causa raíz**: `instrumentedTestVariant.sourceSetTree.set(KotlinSourceSetTree.test)` conecta `commonTest` al build de instrumented tests. Los backtick names (`` `GIVEN ... WHEN ... THEN ...` ``) generan class names con espacios. D8 los rechaza al dexar para Android.

Esta línea estaba en 8 feature modules sin tests de UI Compose, copiada por inercia desde `core:components`.

**Fix**: Eliminar `instrumentedTestVariant.sourceSetTree.set(...)` de todos los módulos que no tienen tests con `compose.uiTest` / `runComposeUiTest { }`. Solo `core:components` la necesita.

**Regla**: `instrumentedTestVariant.sourceSetTree.set(KotlinSourceSetTree.test)` SOLO en módulos con tests de UI Compose reales (`compose.uiTest` en commonTest). En módulos con solo ViewModel tests en commonTest, no añadirla — los backtick names generan class names con espacios que D8 rechaza al dexar.

---

## L025 — KMP + instrumentedTestVariant: conflicto kotlin-test-junit vs kotlin-test-junit5

**Fecha**: 2026-05-27
**Contexto**: CI (Sonar) fallaba al resolver `debugAndroidTestCompileClasspath` en módulos KMP que usan `instrumentedTestVariant.sourceSetTree.set(KotlinSourceSetTree.test)`.

**Error**:
```
Cannot select module with conflict on capability 'org.jetbrains.kotlin:kotlin-test-framework-impl:2.x'
  also provided by [org.jetbrains.kotlin:kotlin-test-junit5:2.x]
```

**Causa raíz**: Al conectar `commonTest` a instrumented tests Android, `kotlin("test")` en `commonTest` se resuelve a `kotlin-test-junit` (JUnit4) porque `androidx.compose.ui.test.junit4` ya está en el classpath. Al mismo tiempo, `core:testing` expone `kotlin-test-junit5` en `androidMain` via `api()`. Ambos proveen la capability `kotlin-test-framework-impl` → conflicto.

**Fix**: Añadir resolución de capability en `KmpLibraryConventionPlugin` para que prefiera siempre `kotlin-test-junit5`:
```kotlin
configurations.all {
    resolutionStrategy.capabilitiesResolution.withCapability(
        "org.jetbrains.kotlin:kotlin-test-framework-impl",
    ) {
        val junit5Candidate = candidates.firstOrNull { c -> c.id.toString().contains("junit5") }
        if (junit5Candidate != null) select(junit5Candidate) else selectHighestVersion()
    }
}
```

**Regla**: En proyectos KMP que usan `instrumentedTestVariant.sourceSetTree.set(KotlinSourceSetTree.test)` y mezclan JUnit4 (via Compose UI test) con JUnit5 (via core:testing), añadir resolución de capability `kotlin-test-framework-impl` en el convention plugin base para evitar el conflicto en todos los módulos afectados.
