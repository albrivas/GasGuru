# Plan de Migración GasGuru: KMP/CMP

## Contexto

GasGuru es una app Android nativa (Kotlin + Jetpack Compose) con arquitectura Clean Architecture modular (22 módulos). El objetivo es migrar a Kotlin Multiplatform (KMP) y Compose Multiplatform (CMP) para soportar **Android + Android Auto + iOS** (V1), dejando la puerta abierta a **Apple CarPlay + Web** (V2).

El proyecto ya tiene un módulo KMP (`:core:network`) que sirve como referencia, un convention plugin `gasguru.kmp.library` funcional con targets Android + iOS (x64/arm64/simulatorArm64), y un stack técnico donde la mayoría de librerías ya tienen soporte KMP oficial.

---

## Análisis del Ecosistema KMP (estado actual)

| Librería | Soporte KMP | Versión KMP | Decisión | Impacto |
|----------|-------------|-------------|----------|---------|
| **Room** | Oficial (Google) | 2.7.0+ | **Quedarse con Room KMP** — 13 migraciones existentes, soporte oficial estable | Migrar type converters de Moshi a kotlinx-serialization |
| **Koin** | Oficial | 4.1.x | **Quedarse** — ya en uso, soporte KMP completo | Separar módulos DI en commonMain/androidMain/iosMain |
| **Ktor** | Nativo KMP | 3.4.0 | **Quedarse** — ya migrado en `:core:network` | Ninguno adicional |
| **ViewModel** | Oficial (Google) | 2.8.0+ (actual: 2.9.4) | **Quedarse con Jetpack ViewModel** — ya KMP, `viewModelScope` multiplataforma | Mover ViewModels a commonMain |
| **Navigation** | JetBrains contribuye a AndroidX Nav | CMP 1.8.0+ | **Mantener NavigationManager propio** — ya es Kotlin puro (SharedFlow), desacoplado de Jetpack Nav | Mover interfaces a commonMain, hosts platform-specific |
| **Compose MP** | Oficial (JetBrains) | 1.10.0 | **Adoptar CMP** — iOS estable desde 1.8.0, Web beta | Requiere nuevo convention plugin |
| **Supabase-kt** | Oficial comunidad | 3.x (Ktor 3) | **Quedarse** — ya KMP-ready | Cambiar engine de ktor-client-android a okhttp/darwin |
| **Arrow** | Oficial | 2.0+ | **Quedarse** — funciona en commonMain sin cambios | Ninguno |
| **OneSignal** | **NO tiene SDK KMP** | N/A | **expect/actual sobre interfaz existente** — SDK nativo en cada plataforma | iOS: integrar OneSignal iOS SDK via Swift/cinterop |
| **Firebase** | **NO oficial** — GitLive SDK comunidad | firebase-kotlin-sdk | **GitLive para Crashlytics KMP** o expect/actual | Medio plazo, no bloquea V1 |
| **Google Maps** | **NO KMP** — maps-compose es Android-only | N/A | **Platform-specific**: Google Maps (Android) + MapKit (iOS) via expect/actual Composables | Mayor esfuerzo en Phase 7 |
| **Places SDK** | **NO KMP** | N/A | **expect/actual**: Google Places (Android) + MKLocalSearch (iOS) | Implementar en Phase 4 |
| **Lottie** | **NO CMP** | N/A | **Reemplazar con compottie** (KMP Lottie) o Compose animations | Phase 6 |
| **ConstraintLayout Compose** | **NO KMP** | N/A | **Refactorizar a layouts estándar** (Column/Row/Box) | Phase 6 |
| **kotlinx-datetime** | Nativo KMP | 0.6.x | **Añadir** — reemplaza java.time en commonMain | Phase 1-2 |

---

## Checklist General de Progreso

### Phase 0: Build Infrastructure
- [ ] Crear rama `feature/kmp-phase0-build-infra` desde `develop`
- [ ] Traer de `feature/migrate-to-ktor`: `KmpLibraryConventionPlugin`, registro en build-logic, deps Ktor en `libs.versions.toml`
- [ ] Crear `KmpComposeLibraryConventionPlugin` → registrar como `gasguru.kmp.library.compose`
- [ ] Crear `KmpRoomConventionPlugin` → registrar como `gasguru.kmp.room`
- [ ] Actualizar `KoinConventionPlugin` para detectar KMP
- [ ] Añadir versiones CMP y kotlinx-datetime a `libs.versions.toml`
- [ ] `./gradlew :build-logic:convention:build` pasa
- [ ] PR → develop y merge

### Phase 1: `:core:model`
- [ ] Crear rama `feature/kmp-phase1-core-model` desde `develop`
- [ ] Cambiar plugin a `gasguru.kmp.library`
- [ ] Mover archivos a `src/commonMain/kotlin/`
- [ ] Reemplazar `java.util.Locale` en `FuelStation.kt`
- [ ] Reemplazar `System.currentTimeMillis()` en `UserData.kt`
- [ ] Añadir tests en `commonTest`
- [ ] `./gradlew :core:model:build` compila Android + iOS
- [ ] Todos los módulos downstream compilan
- [ ] PR → develop y merge

### Phase 2: `:core:common`
- [ ] Crear rama `feature/kmp-phase2-core-common` desde `develop`
- [ ] Cambiar plugin a `gasguru.kmp.library`
- [ ] `GeoUtils.kt` → commonMain (reemplazar `Math.toRadians`)
- [ ] `KoinQualifiers.kt` → commonMain
- [ ] `CoroutineModule.kt` → commonMain con expect/actual IO dispatcher
- [ ] `CommonUtils.kt` → split: schedule parsing a commonMain, `getAppVersion()` expect/actual
- [ ] `LocationUtils.kt` → androidMain
- [ ] Tests en commonTest para `distanceTo()` e `isStationOpen()`
- [ ] `./gradlew :core:common:build` compila Android + iOS
- [ ] Todos los módulos downstream compilan
- [ ] PR → develop y merge

### Phase 3: `:core:database`
- [ ] Crear rama `feature/kmp-phase3-core-database` desde `develop`
- [ ] Cambiar plugin a `gasguru.kmp.room`
- [ ] Migrar `ListConverters` de Moshi a kotlinx-serialization
- [ ] Mover entities, DAOs, migrations, type converters a commonMain
- [ ] Añadir `@ConstructedBy` a `GasGuruDatabase`
- [ ] DI split: DatabaseModule androidMain/iosMain, DaoModule commonMain
- [ ] Test de compatibilidad JSON (Moshi vs kotlinx-serialization)
- [ ] DAO tests Android pasan
- [ ] `./gradlew :core:database:build` compila Android + iOS
- [ ] PR → develop y merge

### Phase 4: Lógica de Negocio
- [ ] Crear rama `feature/kmp-phase4-business-logic` desde `develop`
- [ ] 4A: `:core:supabase` → KMP (engine split okhttp/darwin)
- [ ] 4B: `:core:notifications` → KMP (interfaz commonMain, impl androidMain, no-op iosMain)
- [ ] 4C: `:core:data` → KMP (repos commonMain, platform impls androidMain/iosMain)
- [ ] 4D: `:core:domain` → KMP (todo a commonMain)
- [ ] Implementaciones iOS stub (Location, Network, Geocoder, Places)
- [ ] Tests migrados a commonTest donde aplique
- [ ] Los 4 módulos compilan Android + iOS
- [ ] PR → develop y merge

### Phase 5: Infraestructura
- [ ] Crear rama `feature/kmp-phase5-infrastructure` desde `develop`
- [ ] 5A: `:navigation` → KMP (interfaces commonMain, NavHost androidMain)
- [ ] 5B: `:core:testing` → KMP (fakes commonMain, BaseTest androidMain)
- [ ] 5C: `:mocknetwork` → KMP (Ktor MockEngine commonMain)
- [ ] Todas las fakes compilan para todas las plataformas
- [ ] PR → develop y merge

### Phase 6: UI Compartida (CMP)
- [ ] Crear rama `feature/kmp-phase6-compose-mp` desde `develop`
- [ ] 6A: `:core:ui` → CMP (mappers commonMain, InAppReview platform-specific)
- [ ] 6B: `:core:uikit` → CMP (theme, componentes; reemplazar Lottie y ConstraintLayout)
- [ ] 6C: `:core:components` → CMP (SearchBar + ViewModel commonMain)
- [ ] Componentes renderizan en Android e iOS
- [ ] PR → develop y merge

### Phase 7: Features + App iOS
- [ ] Crear rama `feature/kmp-phase7-features-ios` desde `develop`
- [ ] `:feature:onboarding` → CMP
- [ ] `:feature:profile` → CMP
- [ ] `:feature:favorite-list-station` → CMP
- [ ] `:feature:search` → CMP
- [ ] `:feature:detail-station` → CMP
- [ ] `:feature:route-planner` → CMP (con mapa expect/actual)
- [ ] `:feature:station-map` → CMP (con mapa expect/actual)
- [ ] Crear módulo `:iosApp` con target iOS
- [ ] App iOS compila e instala en simulador
- [ ] ViewModel tests migrados a commonTest
- [ ] PR → develop y merge

---

## Grafo de Dependencias y Orden de Migración

```
Phase 0: build-logic (convention plugins)
    │   Rama: feature/kmp-phase0-build-infra (desde develop)
    │   Incluye: traer KmpLibraryConventionPlugin + deps de feature/migrate-to-ktor
    │
Phase 1: :core:model (sin deps)
    │   Rama: feature/kmp-phase1-core-model
    │
Phase 2: :core:common (← model)
    │   Rama: feature/kmp-phase2-core-common
    │
Phase 3: :core:database (← model, network)
    │   Rama: feature/kmp-phase3-core-database
    │
Phase 4: :core:supabase, :core:notifications, :core:data, :core:domain
    │   Rama: feature/kmp-phase4-business-logic
    │
Phase 5: :navigation, :core:testing, :mocknetwork
    │   Rama: feature/kmp-phase5-infrastructure
    │
Phase 6: :core:ui, :core:uikit, :core:components (CMP)
    │   Rama: feature/kmp-phase6-compose-mp
    │
Phase 7: Features (CMP) + iOS app target
        Rama: feature/kmp-phase7-features-ios
```

---

## Tabla de Fases

| Fase | Rama | Módulos | Plataformas al terminar |
|------|------|---------|-------------------------|
| 0 | feature/kmp-phase0-build-infra | build-logic | Android (sin cambios) |
| 1 | feature/kmp-phase1-core-model | :core:model | Android (sin cambios visibles) |
| 2 | feature/kmp-phase2-core-common | :core:common | Android (sin cambios visibles) |
| 3 | feature/kmp-phase3-core-database | :core:database | Android (sin cambios visibles) |
| 4 | feature/kmp-phase4-business-logic | :core:supabase, :core:notifications, :core:data, :core:domain | Android + lógica iOS compilable |
| 5 | feature/kmp-phase5-infrastructure | :navigation, :core:testing, :mocknetwork | Android + tests KMP |
| 6 | feature/kmp-phase6-compose-mp | :core:ui, :core:uikit, :core:components | Android + componentes iOS |
| 7 | feature/kmp-phase7-features-ios | Features + iOS app | **Android + Android Auto + iOS** |

---

## Phase 0: Build Infrastructure

**Objetivo**: Preparar el sistema de build para soportar múltiples módulos KMP y CMP.

**Módulos**: `build-logic/convention`

### Pasos técnicos

1. **Añadir dependencias a `build-logic/convention/build.gradle.kts`**:
   - JetBrains Compose Multiplatform gradle plugin (para el futuro `KmpComposeLibraryConventionPlugin`)

2. **Crear `KmpComposeLibraryConventionPlugin`**:
   - Aplica `gasguru.kmp.library` + `org.jetbrains.compose` + `org.jetbrains.kotlin.plugin.compose`
   - Configura `commonMain` con compose dependencies (runtime, foundation, material3)
   - Registrar como `gasguru.kmp.library.compose`

3. **Crear `KmpRoomConventionPlugin`**:
   - Aplica `gasguru.kmp.library` + `androidx.room` + `com.google.devtools.ksp`
   - Configura Room KMP: `kspCommonMainMetadata` target para codegen
   - Añade `room-runtime` a `commonMain.dependencies`
   - Registrar como `gasguru.kmp.room`

4. **Actualizar `KoinConventionPlugin`**:
   - Detectar si el proyecto aplica `kotlin.multiplatform`
   - Si KMP: añadir `koin-core` a `commonMain`, `koin-android` a `androidMain`
   - Si no KMP: mantener comportamiento actual

5. **Añadir a `libs.versions.toml`**:
   ```toml
   compose-multiplatform = "1.10.0"
   kotlinx-datetime = "0.6.2"

   # Libraries
   compose-multiplatform-gradlePlugin = { module = "org.jetbrains.compose:compose-gradle-plugin", version.ref = "compose-multiplatform" }
   kotlinx-datetime = { module = "org.jetbrains.kotlinx:kotlinx-datetime", version.ref = "kotlinx-datetime" }

   # Plugins
   gasguru-kmp-compose-library = { id = "gasguru.kmp.library.compose" }
   gasguru-kmp-room = { id = "gasguru.kmp.room" }
   ```

### Tests
- `./gradlew :build-logic:convention:build` compila sin errores
- `:core:network` sigue compilando (sanity check)

### Conceptos KMP a aprender
- Gradle source sets KMP (commonMain, androidMain, iosMain)
- Cómo funcionan los convention plugins con KMP
- Diferencia entre `kotlin.multiplatform` y `kotlin.android` plugins

### Criterio de "done"
- Los 3 nuevos convention plugins compilan y se registran
- `./gradlew tasks` muestra las nuevas tareas
- `:core:network` sigue compilando sin cambios

---

## Phase 1: `:core:model` — Modelos de Dominio

**Objetivo**: Migrar el módulo base del que dependen todos los demás. Riesgo mínimo porque son data classes puras.

**Módulo**: `:core:model`

### Análisis de dependencias JVM

| Archivo | Dependencia JVM | Reemplazo KMP |
|---------|-----------------|---------------|
| `FuelStation.kt` | `java.util.Locale` en `String.format(Locale.ROOT, ...)` | Usar `"%.2f".format(...)` (Kotlin stdlib, disponible en common desde Kotlin 1.9.20) |
| `UserData.kt` | `System.currentTimeMillis()` | `Clock.System.now().toEpochMilliseconds()` (kotlinx-datetime) |

### Pasos técnicos

1. **Cambiar plugin**: `gasguru.android.library` → `gasguru.kmp.library` en `core/model/build.gradle.kts`
2. **Mover archivos**: `src/main/java/com/gasguru/core/model/` → `src/commonMain/kotlin/com/gasguru/core/model/`
3. **En `FuelStation.kt`**:
   - Eliminar `import java.util.Locale`
   - `String.format(Locale.ROOT, "%.2f Km", kilometers)` → `"%.2f Km".format(kilometers)` (Kotlin common)
4. **En `UserData.kt`**:
   - Añadir `import kotlinx.datetime.Clock`
   - `System.currentTimeMillis()` → `Clock.System.now().toEpochMilliseconds()`
5. **Actualizar `build.gradle.kts`**:
   ```kotlin
   plugins {
       alias(libs.plugins.gasguru.kmp.library)
   }
   kotlin {
       sourceSets {
           commonMain.dependencies {
               implementation(libs.kotlinx.datetime)
           }
       }
   }
   ```
6. **Eliminar** el plugin `gasguru.proguard` (no aplica a KMP library sin Android-specific code)

### Tests
- No hay tests existentes en `:core:model`
- Añadir `src/commonTest/kotlin/` con tests básicos para `FuelStation.formatDistance()` y `FuelStation.formatDirection()` usando `kotlin.test`
- Verificar que TODOS los módulos downstream siguen compilando

### Conceptos KMP a aprender
- Estructura de source sets: `commonMain/kotlin/` vs `src/main/java/`
- `kotlin.test` como framework de testing multiplataforma
- kotlinx-datetime como reemplazo de java.time

### Criterio de "done"
- `./gradlew :core:model:build` compila para Android + iOS
- `:core:network`, `:core:database`, `:core:common` siguen compilando
- Tests en `commonTest` pasan

### Archivos a modificar
- `core/model/build.gradle.kts`
- `core/model/src/commonMain/kotlin/com/gasguru/core/model/data/FuelStation.kt` (mover + editar)
- `core/model/src/commonMain/kotlin/com/gasguru/core/model/data/UserData.kt` (mover + editar)
- Resto de archivos en model: mover sin cambios

---

## Phase 2: `:core:common` — Utilidades Compartidas

**Objetivo**: Separar código multiplataforma (GeoUtils, KoinQualifiers, CoroutineModule) del código Android-only (LocationUtils, BuildConfig).

**Módulo**: `:core:common`

### Análisis archivo por archivo

| Archivo | Destino | Cambios necesarios |
|---------|---------|-------------------|
| `KoinQualifiers.kt` | commonMain | Ninguno — `object` con `const val` strings |
| `CoroutineModule.kt` | commonMain + androidMain | `Dispatchers.IO` no existe en iOS → expect/actual |
| `GeoUtils.kt` | commonMain | `Math.toRadians(x)` → `x * PI / 180.0` |
| `CommonUtils.kt` | commonMain + androidMain | Schedule parsing a commonMain (reemplazar java.time), `getAppVersion()` a androidMain con expect/actual |
| `LocationUtils.kt` | androidMain | 100% Android (Google Maps Compose types, Context, permissions) |

### Pasos técnicos

1. **Cambiar plugin**: `gasguru.android.library` → `gasguru.kmp.library` + `gasguru.koin`
2. **Crear estructura de source sets**
3. **`GeoUtils.kt` → commonMain**:
   - `Math.toRadians(x)` → `x * kotlin.math.PI / 180.0` (4 ocurrencias)
4. **`CommonUtils.kt` → split**:
   - commonMain: `isStationOpen()` con kotlinx-datetime
     - `java.time.ZonedDateTime.now()` → `Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())`
     - `java.time.LocalTime` → `kotlinx.datetime.LocalTime`
     - `java.time.DayOfWeek` → `kotlinx.datetime.DayOfWeek`
   - commonMain: `expect fun getAppVersion(): String`
   - androidMain: `actual fun getAppVersion()` usando BuildConfig
   - iosMain: `actual fun getAppVersion()` usando `NSBundle.mainBundle`
5. **`CoroutineModule.kt` → commonMain con expect/actual para IO dispatcher**:
   - commonMain: `expect val ioDispatcher: CoroutineDispatcher`
   - androidMain: `actual val ioDispatcher = Dispatchers.IO`
   - iosMain: `actual val ioDispatcher = Dispatchers.Default` (Kotlin/Native no tiene IO dispatcher dedicado)
6. **`LocationUtils.kt` → androidMain sin cambios**
7. **Gestionar BuildConfig**: El `build.gradle.kts` actual lee `versions.properties` y genera BuildConfig fields. Mover esa lógica al `androidMain` build variant o usar un file-based approach con `expect/actual`.
8. **Actualizar dependencias**:
   ```kotlin
   commonMain.dependencies {
       implementation(projects.core.model)
       implementation(libs.kotlinx.coroutines.core)
       implementation(libs.koin.core)
       implementation(libs.kotlinx.datetime)
   }
   androidMain.dependencies {
       implementation(libs.androidx.core.ktx)
       implementation(libs.appcompat)
       implementation(libs.material)
       api(libs.play.services.maps)
       implementation(libs.maps.compose)
       implementation(libs.koin.android)
   }
   ```

### Tests
- Test existente: `testImplementation(libs.junit)` — verificar qué test existe y moverlo
- commonTest: tests para `distanceTo()` con coordenadas conocidas, `isStationOpen()` con horarios fijos
- androidTest: tests para `LocationUtils` extensiones

### Conceptos KMP a aprender
- Declaraciones `expect`/`actual` — el mecanismo fundamental de KMP
- Diferencias de runtime entre JVM y Kotlin/Native (IO dispatcher, reflexión)
- kotlinx-datetime como reemplazo completo de java.time
- Cómo acceder a APIs nativas iOS desde Kotlin (NSBundle)

### Criterio de "done"
- `./gradlew :core:common:build` compila para Android + iOS
- Todos los módulos downstream compilan
- `distanceTo()` devuelve los mismos valores que antes (test de regresión)
- `isStationOpen()` funciona con kotlinx-datetime

### Archivos a modificar
- `core/common/build.gradle.kts` — reescribir para KMP
- `core/common/src/commonMain/kotlin/.../GeoUtils.kt`
- `core/common/src/commonMain/kotlin/.../CommonUtils.kt` (schedule parsing)
- `core/common/src/commonMain/kotlin/.../KoinQualifiers.kt`
- `core/common/src/commonMain/kotlin/.../CoroutineModule.kt`
- `core/common/src/androidMain/kotlin/.../LocationUtils.kt`
- `core/common/src/androidMain/kotlin/.../AppVersion.kt` (actual)
- `core/common/src/iosMain/kotlin/.../AppVersion.kt` (actual)
- `core/common/src/iosMain/kotlin/.../IoDispatcher.kt` (actual)

---

## Phase 3: `:core:database` — Room KMP

**Objetivo**: Migrar Room a KMP para que entities, DAOs, migrations y type converters estén en `commonMain`. Solo la instanciación de la base de datos es platform-specific.

**Módulo**: `:core:database`

### Análisis de type converters

| Converter | Usa Moshi? | Acción |
|-----------|-----------|--------|
| `UserDataConverters` | No — conversiones enum↔String puras | Mover a commonMain sin cambios |
| `FilterTypeConverter` | No — conversión enum↔String pura | Mover a commonMain sin cambios |
| `ListConverters` | **Sí** — usa `Moshi.Builder()`, `KotlinJsonAdapterFactory`, `Types.newParameterizedType` | **Reescribir con kotlinx-serialization**: `Json.encodeToString(list)` / `Json.decodeFromString(data)` |

### Pasos técnicos

1. **Cambiar plugin**: `gasguru.android.library` + `gasguru.room` → `gasguru.kmp.room` (nuevo plugin de Phase 0)
2. **Migrar `ListConverters.kt`** de Moshi a kotlinx-serialization:
   ```kotlin
   // commonMain
   class ListConverters {
       @TypeConverter
       fun fromList(list: List<String>): String = Json.encodeToString(list)

       @TypeConverter
       fun toList(data: String): List<String> = Json.decodeFromString(data)
   }
   ```
3. **Mover a commonMain**: Todas las entities, DAOs, type converters, migrations, `GasGuruDatabase` abstract class
4. **Room KMP requiere**:
   - Añadir `@ConstructedBy(GasGuruDatabaseConstructor::class)` a `GasGuruDatabase`
   - Crear `expect object GasGuruDatabaseConstructor : RoomDatabaseConstructor<GasGuruDatabase>`
   - androidMain: `actual object GasGuruDatabaseConstructor` (Room genera la implementación automáticamente)
   - iosMain: igual, Room genera la implementación
5. **DI split**:
   - androidMain: `DatabaseModule.kt` con `Room.databaseBuilder(context, ...)` + migrations
   - iosMain: `DatabaseModule.kt` con `Room.databaseBuilder<GasGuruDatabase>(name = dbPath)` + migrations
   - commonMain: `DaoModule.kt` (los DAOs se obtienen del database)
6. **Actualizar build.gradle.kts**:
   ```kotlin
   plugins {
       alias(libs.plugins.gasguru.kmp.room)
       alias(libs.plugins.gasguru.koin)
       alias(libs.plugins.kotlin.serialization)
   }
   kotlin {
       sourceSets {
           commonMain.dependencies {
               implementation(projects.core.model)
               implementation(libs.kotlinx.coroutines.core)
               implementation(libs.kotlinx.serialization.json)
           }
       }
   }
   ```
7. **Eliminar dependencia de Moshi** (`moshi-kotlin`) del módulo

### Tests
- Los tests instrumentados de Android (`androidTest`) siguen funcionando — verifican migraciones y DAOs en device real
- Añadir `commonTest` con tests unitarios para type converters (serialización/deserialización)
- Verificar las 13 migraciones en Android (regresión crítica)
- iosTest: test básico de creación de BD + CRUD simple

### Conceptos KMP a aprender
- Room KMP: `@ConstructedBy`, `RoomDatabaseConstructor`, KSP en KMP
- Diferencias entre SQLite en Android (android.database.sqlite) vs SQLite en iOS (via Room KMP driver)
- Cómo Room KMP genera código para cada plataforma

### Riesgos
- **Migraciones en iOS**: Las 13 migraciones de Android aplican a nuevas instalaciones en iOS (no hay BD previa). Pero Room las ejecuta secuencialmente de v1→v13 por si un usuario de iOS instala una versión futura con una BD existente. Verificar que SQL es compatible con SQLite iOS.
- **Moshi → kotlinx-serialization en ListConverters**: El formato JSON generado debe ser idéntico para no romper BDs existentes en Android. `Json.encodeToString(listOf("a","b"))` genera `["a","b"]` — mismo formato que Moshi.

### Criterio de "done"
- `./gradlew :core:database:build` compila para Android + iOS
- Tests de migración Android pasan
- ListConverters genera el mismo JSON que antes (test de compatibilidad)
- `:core:data` sigue compilando

### Archivos a modificar
- `core/database/build.gradle.kts` — reescribir para KMP
- `core/database/src/commonMain/kotlin/.../GasGuruDatabase.kt` — añadir `@ConstructedBy`
- `core/database/src/commonMain/kotlin/.../converters/ListConverters.kt` — Moshi → kotlinx-serialization
- Todas las entities, DAOs, migrations → mover a commonMain
- `core/database/src/androidMain/kotlin/.../di/DatabaseModule.kt` — platform-specific builder
- `core/database/src/iosMain/kotlin/.../di/DatabaseModule.kt` — platform-specific builder
- `build-logic/.../KmpRoomConventionPlugin.kt` (creado en Phase 0)

---

## Phase 4: Lógica de Negocio — Data + Domain + Supabase + Notifications

**Objetivo**: Migrar toda la capa de negocio a KMP. Tras esta fase, toda la lógica (repositories, use cases, data sources) es compartida. Es la fase con mayor impacto.

**Módulos**: `:core:supabase`, `:core:notifications`, `:core:data`, `:core:domain`

### 4A: `:core:supabase`

**Estado actual**: Supabase SDK (3.2.6) ya es KMP. Usa `ktor-client-android`.

| Destino | Archivos |
|---------|----------|
| commonMain | `SupabaseManager.kt` (interfaz), `SupabaseManagerImpl.kt`, `PriceAlertSupabase.kt`, `SupabaseModule.kt` (Koin) |

**Cambios**:
- Plugin → `gasguru.kmp.library` + `gasguru.koin`
- Reemplazar `ktor-client-android` por engines platform-specific en dependencies
- commonMain: `supabase-postgrest`, `ktor-client-core`
- androidMain: `ktor-client-okhttp`
- iosMain: `ktor-client-darwin`

### 4B: `:core:notifications`

**Estado actual**: OneSignal Android SDK only.

| Destino | Archivos |
|---------|----------|
| commonMain | `OneSignalManager.kt` (interfaz) |
| androidMain | `OneSignalManagerImpl.kt`, `PushNotificationService.kt`, `NotificationModule.kt` |
| iosMain | `OneSignalManagerNoOp.kt` (V1: no-op, V2: integrar OneSignal iOS SDK) |

**Cambios**:
- Plugin → `gasguru.kmp.library` + `gasguru.koin`
- La interfaz `OneSignalManager` va a commonMain
- V1 iOS: implementación no-op que no envía notificaciones (funcionalidad de alertas de precio limitada en iOS V1)
- V2 iOS: integrar OneSignal iOS SDK via cinterop o Swift bridging

### 4C: `:core:data` — La migración más compleja

**Análisis de cada archivo**:

| Archivo | Destino | Dependencias JVM/Android | Acción |
|---------|---------|--------------------------|--------|
| Repository interfaces (7) | commonMain | Ninguna | Mover sin cambios |
| `OfflineFuelStationRepository` | commonMain | `maps-utils` (distance) | Reemplazar con `GeoUtils.distanceTo()` de `:core:common` |
| `FilterRepositoryImpl` | commonMain | Ninguna | Mover sin cambios |
| `OfflineRecentSearchRepositoryImp` | commonMain | Ninguna | Mover sin cambios |
| `OfflineUserDataRepository` | commonMain | Ninguna | Mover sin cambios |
| `RoutesRepositoryImpl` | commonMain | Ninguna | Mover sin cambios |
| `PriceAlertRepositoryImpl` | commonMain | Ninguna (usa interfaces) | Mover sin cambios |
| `SyncManager` | commonMain | Ninguna (usa interfaces) | Mover sin cambios |
| `GoogleStaticMapRepository` | commonMain | Ninguna (construye URL string) | Mover sin cambios |
| Mappers (4-5) | commonMain | Verificar | Mover, adaptar si hay deps Android |
| `LocationTrackerRepository` | androidMain | FusedLocationProviderClient, BroadcastReceiver, Context | Mantener en Android |
| `ConnectivityManagerNetworkMonitor` | androidMain | ConnectivityManager, Context | Mantener en Android |
| `GeocoderAddressImpl` | androidMain | android.location.Geocoder | Mantener en Android |
| `PlacesRepositoryImp` | androidMain | Google Places SDK (PlacesClient) | Mantener en Android |
| `DataModule.kt` | split | Koin module | Separar common/android/ios |

**Implementaciones iOS necesarias**:

| Interfaz | Implementación iOS | API nativa |
|----------|-------------------|-----------|
| `LocationTracker` | `LocationTrackerIos` | `CLLocationManager` (CoreLocation) |
| `NetworkMonitor` | `NetworkMonitorIos` | `NWPathMonitor` (Network framework) |
| `GeocoderAddress` | `GeocoderAddressIos` | `CLGeocoder` (CoreLocation) |
| `PlacesRepository` | `PlacesRepositoryIos` | `MKLocalSearch` (MapKit) |

**DI (Koin) split**:
- `commonMain/di/DataModule.kt`: bind repos que están en commonMain
- `androidMain/di/DataAndroidModule.kt`: bind LocationTracker, NetworkMonitor, Geocoder, Places → implementaciones Android
- `iosMain/di/DataIosModule.kt`: bind → implementaciones iOS

### 4D: `:core:domain`

**Estado actual**: 100% Kotlin puro. Solo tiene use cases que dependen de interfaces de repository.

**Acción**: Mover TODO a commonMain sin cambios funcionales.
- Plugin → `gasguru.kmp.library` + `gasguru.koin`
- `DomainModule.kt` (Koin) → commonMain (solo usa `factory {}`)
- Dependencias: `:core:data`, `:core:model`, `:core:common`, `:core:notifications` (todas ya KMP)

### Tests Phase 4

| Módulo | commonTest | androidTest | iosTest |
|--------|-----------|-------------|---------|
| `:core:supabase` | Tests con Ktor MockEngine | — | — |
| `:core:notifications` | — | Tests Android existentes | Test no-op iOS |
| `:core:data` | Tests de repos con fakes (Turbine, kotlin.test) | Tests Android-specific (LocationTracker, etc.) | Tests iOS-specific (CLLocationManager mock) |
| `:core:domain` | **Todos los tests** — use cases son Kotlin puro | — | — |

**Migración de framework de testing**: En commonTest, reemplazar JUnit5 por `kotlin.test`:
- `@Test` (org.junit.jupiter) → `@Test` (kotlin.test)
- `assertEquals` → `kotlin.test.assertEquals`
- `assertTrue` → `kotlin.test.assertTrue`
- Turbine funciona en commonMain (es KMP-ready)

### Conceptos KMP a aprender
- `cinterop` para acceder a APIs nativas iOS (CLLocationManager, NWPathMonitor, CLGeocoder, MKLocalSearch)
- Patrones de integración con frameworks iOS nativos desde Kotlin
- Koin modules platform-specific y cómo combinarlos en el arranque
- Diferencias entre testing en JVM vs Kotlin/Native

### Criterio de "done"
- Los 4 módulos compilan para Android + iOS
- Todos los tests existentes pasan (movidos o adaptados)
- Use cases ejecutan idéntico en ambas plataformas
- iOS tiene implementaciones funcionales de Location, Network, Geocoder, Places (al menos stubs compilables)

### Archivos clave
- `core/data/src/main/java/com/gasguru/core/data/di/DataModule.kt` → split en 3
- `core/data/src/main/java/com/gasguru/core/data/repository/` → todos los archivos
- `core/domain/src/main/java/com/gasguru/core/domain/` → todos los use cases
- `core/supabase/src/main/java/` → todo el módulo
- `core/notifications/src/main/java/` → todo el módulo

---

## Phase 5: Infraestructura — Navigation + Testing + MockNetwork

**Objetivo**: Hacer que la infraestructura de navegación, testing y mocks sea multiplataforma.

### 5A: `:navigation`

**Estado actual**: `NavigationManager`, `NavigationDestination`, `NavigationCommand` son Kotlin puro. Los handlers y NavHost son Android-specific.

| Destino | Archivos |
|---------|----------|
| commonMain | `NavigationManager.kt`, `NavigationManagerImpl.kt`, `NavigationDestination.kt`, `NavigationCommand.kt`, `NavigationKeys.kt`, `DeepLinkStateHolder.kt`, `NavigationManagerModule.kt` |
| androidMain | `NavigationExtensions.kt`, `GlobalCompositionLocal.kt`, `GasGuruNavHost.kt`, `NavigationHandler.kt` |
| iosMain | (vacío en V1 — la app iOS consumirá NavigationManager directamente) |

**Cambios adicionales**:
- Si `PlaceArgs.kt` o `RoutePlanArgs.kt` usan `@Parcelize`, eliminar y usar `@Serializable`
- Eliminar plugin `kotlin-parcelize` del módulo

### 5B: `:core:testing`

| Destino | Archivos |
|---------|----------|
| commonMain | Las 14 fakes (implementan interfaces que ya están en commonMain) |
| androidMain | `BaseTest.kt` (Compose test infrastructure), `CoroutinesTestRuleExtension.kt` (JUnit5) |
| commonTest dependencies | `kotlinx-coroutines-test`, `kotlin-test`, `turbine` |

### 5C: `:mocknetwork`

- Ya usa Ktor MockEngine → mover todo a commonMain
- Eliminar dependencia de Android assets — cargar JSON desde resources de commonMain

### Criterio de "done"
- NavigationManager funciona idénticamente desde common code
- Todas las fakes compilan para todas las plataformas
- MockNetwork funciona con Ktor MockEngine en commonTest

---

## Phase 6: UI Compartida — Compose Multiplatform

**Objetivo**: Migrar componentes UI a CMP para compartir la capa de presentación.

### 6A: `:core:ui` (mappers + modelos UI)

- Plugin → `gasguru.kmp.library.compose` (nuevo de Phase 0)
- Mover mappers y modelos UI a commonMain (usan `@Stable` de Compose, disponible en CMP)
- `InAppReviewManager`: androidMain (play-review-ktx) + iosMain (SKStoreReviewController)

### 6B: `:core:uikit` (design system)

- Plugin → `gasguru.kmp.library.compose`
- Mover `GasGuruTheme`, `GasGuruColors`, componentes Material3 a commonMain
- **Lottie** → Reemplazar con `compottie` (librería KMP que lee archivos .json de Lottie) o Compose animations nativas
- **ConstraintLayout Compose** → Refactorizar layouts afectados a Column/Row/Box con Modifier
- **Coil** → Migrar a Coil 3 (tiene soporte KMP) para carga de imágenes

### 6C: `:core:components` (SearchBar)

- Plugin → `gasguru.kmp.library.compose`
- ViewModel + Composable → commonMain
- Koin module → commonMain

### Tests
- UI mapper tests → commonTest (kotlin.test)
- Compose UI tests → androidInstrumentedTest (CMP testing aún madurando)
- ViewModel tests del SearchBar → commonTest (Turbine)

### Conceptos KMP a aprender
- Compose Multiplatform: diferencias con Jetpack Compose Android
- Material3 en CMP
- Carga de recursos (strings, images) en CMP: `Res` system de JetBrains
- Alternativas a librerías Android-only (Lottie, ConstraintLayout)

### Criterio de "done"
- Todos los componentes renderizan en Android e iOS preview
- Theme dark/light funciona en ambas plataformas
- No quedan dependencias de Lottie ni ConstraintLayout

---

## Phase 7: Features + App iOS — El Push Final

**Objetivo**: Migrar las pantallas de features a CMP y crear el target de app iOS.

### Orden de migración (menor a mayor complejidad)

| Orden | Feature | Complejidad | Razón |
|-------|---------|-------------|-------|
| 1 | `:feature:onboarding` | Baja | UI simple, pocos deps |
| 2 | `:feature:profile` | Baja | Settings screen, sin mapas |
| 3 | `:feature:favorite-list-station` | Baja | Lista, sin mapas |
| 4 | `:feature:search` | Media | Usa SearchBar (ya migrado) |
| 5 | `:feature:detail-station` | Media | Static map image, navegación |
| 6 | `:feature:route-planner` | Alta | Usa mapas y Places |
| 7 | `:feature:station-map` | Alta | Google Maps Compose, la más compleja |

### Patrón para cada feature

1. Plugin → `gasguru.kmp.library.compose` + `gasguru.koin`
2. Mover ViewModel + UiState + Events → commonMain
3. Mover Composables → commonMain (CMP)
4. Para features con mapa: `expect`/`actual` Composable
   ```kotlin
   // commonMain
   @Composable
   expect fun PlatformMapView(
       stations: List<FuelStationUiModel>,
       cameraPosition: LatLng,
       zoom: Float,
       onStationClick: (Int) -> Unit,
       modifier: Modifier,
   )

   // androidMain: GoogleMap composable
   // iosMain: UIKitView { MKMapView() }
   ```
5. Reemplazar `koin-androidx-compose` por `koin-compose` (KMP version)

### App iOS

1. Crear módulo `:iosApp` con el target de aplicación iOS
2. `iosApp/iosApp/iOSApp.swift` — punto de entrada SwiftUI que hostea CMP
3. Configurar Koin initialization para iOS:
   ```kotlin
   // shared/src/iosMain/kotlin/KoinInit.kt
   fun initKoin() {
       startKoin {
           modules(
               commonModules + iosSpecificModules
           )
       }
   }
   ```
4. Generar framework XCFramework con `./gradlew assembleXCFramework`

### `:auto:common` — Se queda Android-only
- Android Auto usa CarAppService (API Android exclusiva)
- No se migra a KMP
- En V2, crear `:carplay:common` como módulo iOS-only equivalente

### Tests
- ViewModel tests → commonTest (Turbine + kotlin.test)
- Compose UI tests → androidInstrumentedTest
- iOS: tests manuales + snapshot tests cuando CMP testing madure

### Criterio de "done" (V1 completo)
- App Android funciona idénticamente a la versión pre-migración
- Android Auto sigue funcionando sin cambios
- App iOS compila, instala en simulador y muestra todas las pantallas
- Navegación entre pantallas funciona en iOS
- Mapa muestra estaciones en iOS (MapKit)
- Búsqueda de Places funciona en iOS (MKLocalSearch)
- Localización funciona en iOS (CLLocationManager)

---

## Estrategia de Migración de Tests

### Inventario actual

El proyecto tiene **35 archivos de test** distribuidos así:

| Tipo | Cantidad | Framework | Ubicación actual |
|------|----------|-----------|------------------|
| ViewModel tests | 8 | JUnit5 + Turbine + CoroutinesTestExtension | `src/test/` en app, features y components |
| DAO tests (Room) | 5 | JUnit5 + Room in-memory + Turbine | `core/database/src/androidTest/` |
| DataSource tests | 2 | JUnit5 + Ktor MockEngine / MockK | `core/network/src/androidUnitTest/` |
| UseCase tests | 1 | JUnit5 + MockK + CoroutinesTestExtension | `core/domain/src/test/` |
| Compose UI tests | 8 | JUnit5 + BaseTest (Compose) | `src/androidTest/` en uikit y features |
| Skeletons vacíos | 2 | — | `core/data`, `core/domain` |
| **Fakes** | **14** | — | `core/testing/src/main/` |

### Infraestructura de testing actual

**`:core:testing`** — Librería central con:
- `BaseTest.kt` — Clase base JUnit5 con soporte Compose (`createComposeExtension()`, `getStringResource()`)
- `CoroutinesTestRule.kt` — JUnit4 Rule con `StandardTestDispatcher`
- `CoroutineTestRuleExtension.kt` — JUnit5 Extension con `StandardTestDispatcher`
- **14 fakes** que implementan interfaces de repository/DAO/services usando `MutableStateFlow` y listas mutables para tracking de llamadas

**`:mocknetwork`** — Mock HTTP server con Ktor MockEngine + JSON desde Android assets

### Decisiones de migración de testing

| Decisión | Justificación |
|----------|---------------|
| **JUnit5 → `kotlin.test` en commonTest** | JUnit5 es JVM-only. `kotlin.test` es el framework multiplataforma de Kotlin. La API es similar (`@Test`, `assertEquals`, `assertTrue`) |
| **Turbine se mantiene** | Turbine es KMP-ready, funciona en commonTest sin cambios |
| **MockK se mantiene solo en androidTest** | MockK no soporta KMP. Los tests que usan MockK (PlacesDataSourceTest, GetAddressFromLocationUseCaseTest) se quedan en `androidUnitTest` |
| **Fakes a commonMain** | Las 14 fakes implementan interfaces que estarán en commonMain → las fakes también van a commonMain |
| **CoroutinesTestExtension se adapta** | Crear versión `kotlin.test` para commonTest. La versión JUnit5 se mantiene en androidMain para tests Android-specific |
| **BaseTest se queda en androidMain** | Usa `ApplicationProvider`, `createComposeExtension()` y Android resources — 100% Android |
| **Compose UI tests se quedan en androidTest** | CMP testing aún está madurando. Los 8 tests de UI se mantienen como `androidInstrumentedTest` |

### Migración por fase

#### Phase 1: `:core:model`
- **Estado**: Sin tests existentes
- **Acción**: Crear `src/commonTest/kotlin/` con tests para `FuelStation.formatDistance()`, `FuelStation.formatDirection()`, `FuelStation.formatName()` usando `kotlin.test`
- **Framework**: `kotlin.test` + `kotlinx-coroutines-test`

#### Phase 2: `:core:common`
- **Test existente**: 1 test con JUnit (verificar contenido)
- **Acción**: Migrar test a `commonTest` con `kotlin.test`. Añadir tests para `distanceTo()` (regresión con coordenadas conocidas) y `isStationOpen()` (varios horarios)
- **Nota**: `LocationUtils` tests van a `androidUnitTest` (dependen de Google Maps types)

#### Phase 3: `:core:database`
- **Tests existentes**: 5 DAO tests en `androidTest/` (FiltersDaoTest, FuelStationDaoTest, PriceAlertDaoTest, RecentSearchQueryDaoTest, UserDataDaoTest)
- **Acción**: Los 5 DAO tests **se mantienen en `androidInstrumentedTest`** — usan Room in-memory database con `Room.inMemoryDatabaseBuilder(context, ...)` que requiere Android Context
- **Añadir**: `commonTest` con test de compatibilidad de `ListConverters` (verificar que JSON generado por kotlinx-serialization es idéntico al de Moshi)
- **Regresión crítica**: Todos los DAO tests deben pasar sin cambios funcionales

#### Phase 4: `:core:data`, `:core:domain`, `:core:supabase`, `:core:notifications`

**`:core:data`**:
- **Test existente**: `OfflineFuelStationRepositoryTest.kt` (skeleton vacío con JUnit4 + MockK)
- **Acción**: Los tests de repositories que usen fakes (no MockK) van a `commonTest`. Tests que necesiten Android Context se quedan en `androidUnitTest`

**`:core:domain`**:
- **Tests existentes**:
  - `GetAddressFromLocationUseCaseTest.kt` — usa MockK → **se queda en `androidUnitTest`**
  - `GetFuelStationUseCaseTest.kt` — skeleton vacío
- **Acción**: Los use cases que no usen MockK se testan en `commonTest` con fakes. Los que usan MockK se mantienen en `androidUnitTest`

**`:core:network`** (ya KMP):
- **Tests existentes**:
  - `RemoteDataSourceTest.kt` — usa `NetworkMockEngine` (Ktor MockEngine) → **migrar a `commonTest`** (Ktor MockEngine es KMP)
  - `PlacesDataSourceTest.kt` — usa MockK para `PlacesClient` (Google Places) → **se queda en `androidUnitTest`**

#### Phase 5: `:core:testing`, `:mocknetwork`, `:navigation`

**`:core:testing`** — Migración de la infraestructura:

| Archivo | Destino | Cambios |
|---------|---------|---------|
| 14 fakes | `commonMain` | Sin cambios funcionales — implementan interfaces que ya estarán en commonMain |
| `CoroutineTestRuleExtension.kt` | `androidMain` | Se mantiene para tests JUnit5 Android |
| `CoroutinesTestRule.kt` | `androidMain` | Se mantiene para tests JUnit4 legacy |
| `BaseTest.kt` | `androidMain` | Se mantiene — usa Android Context y Compose test APIs |
| Nuevo: `CoroutineTestHelper.kt` | `commonMain` | Helper para `runTest` + `StandardTestDispatcher` compatible con `kotlin.test` |

**`:mocknetwork`**:
- **Acción**: Mover a commonMain. `MockWebServerManagerImp` carga JSON desde Android assets → **refactorizar para cargar desde commonMain resources** (usar `Res` de CMP o embebir JSONs como strings)
- `MockRemoteDataSource` y `MockWebServerManager` interfaz → commonMain sin cambios

#### Phase 6: `:core:ui`, `:core:uikit`, `:core:components`

**`:core:uikit`** — 5 Compose UI tests:
- `GasGuruAlertDialogTest`, `FuelStationItemTest`, `FuelListSelectionTest`, `RouteNavigationCardTest`, `SelectedItemTest`
- **Acción**: **Se mantienen en `androidInstrumentedTest`** — usan `BaseTest`, `ComposeContentTestRule`, Android resources
- Cuando CMP testing madure, evaluar migración a commonTest

**`:core:components`**:
- `GasGuruSearchBarViewModelTest.kt` — JUnit5 + Turbine + fakes
- **Acción**: Migrar a `commonTest` con `kotlin.test` + Turbine (no usa MockK ni Android APIs)

#### Phase 7: Features + App iOS

**ViewModel tests** (8 archivos) — todos usan JUnit5 + Turbine + CoroutinesTestExtension + fakes:

| Test | Usa MockK? | Destino |
|------|-----------|---------|
| `SplashViewModelTest` | No | `commonTest` |
| `StationMapViewModelTest` | No | `commonTest` |
| `DetailStationViewModelTest` | No | `commonTest` |
| `FavoriteListStationViewModelTest` | No | `commonTest` |
| `ProfileViewModelTest` | No | `commonTest` |
| `RoutePlannerViewModelTest` | No | `commonTest` |
| `NewOnboardingViewModelTest` | No | `commonTest` |
| `GasGuruSearchBarViewModelTest` | No | `commonTest` (Phase 6) |

Todos los ViewModel tests usan fakes (no MockK) → **todos migran a `commonTest`**.

**Compose Screen tests** (3 archivos) — usan BaseTest + Compose test APIs:
- `DetailStationScreenTest`, `FavoriteListScreenTest`, `OnboardingFuelPreferencesTest`, `ProfileScreenTest`
- **Acción**: **Se mantienen en `androidInstrumentedTest`**

### Resumen de destinos finales

| Source set | Qué contiene | Framework |
|-----------|-------------|-----------|
| `commonTest` | ViewModel tests, UseCase tests (sin MockK), Repository tests con fakes, DataSource tests (Ktor MockEngine), model tests, type converter tests | `kotlin.test` + Turbine + `kotlinx-coroutines-test` |
| `androidUnitTest` | Tests que usan MockK (PlacesDataSource, GetAddressFromLocationUseCase), tests que necesitan Android Context | JUnit5 + MockK + CoroutinesTestExtension |
| `androidInstrumentedTest` | DAO tests (Room in-memory), Compose UI tests (BaseTest) | JUnit5 + Room + Compose test + Turbine |
| `iosTest` | Tests de implementaciones iOS nativas (CLLocationManager, NWPathMonitor, etc.) | `kotlin.test` |

### Cambios en dependencias de testing

```kotlin
// commonTest (nuevo, para módulos KMP)
commonTest.dependencies {
    implementation(kotlin("test"))
    implementation(libs.kotlinx.coroutines.test)
    implementation(libs.turbine)
    implementation(projects.core.testing)  // fakes
}

// androidUnitTest (mantener para tests con MockK/JUnit5)
androidUnitTest.dependencies {
    implementation(libs.junit5.api)
    implementation(libs.junit5.engine)
    implementation(libs.mockk)
    implementation(libs.kotlinx.coroutines.test)
    implementation(projects.core.testing)
}

// androidInstrumentedTest (sin cambios)
androidInstrumentedTest.dependencies {
    implementation(libs.junit5.api)
    implementation(libs.junit5.extensions)
    implementation(libs.junit5.runner)
    implementation(libs.junit5.compose)
    implementation(libs.androidx.test.core)
    implementation(libs.turbine)
    implementation(projects.core.testing)
}
```

### Regla de oro
> **Ningún test se elimina durante la migración.** Cada test o se mueve a `commonTest` (si es Kotlin puro + fakes + Turbine) o se mantiene en `androidUnitTest`/`androidInstrumentedTest` (si usa MockK, Android Context, Room in-memory o Compose test APIs). Al finalizar cada fase, `./gradlew test` y `./gradlew connectedAndroidTest` deben pasar igual que antes.

---

## Riesgos y Mitigación

| Riesgo | Probabilidad | Impacto | Mitigación |
|--------|-------------|---------|------------|
| Room KMP: SQL incompatible en iOS SQLite | Baja | Alto | Testear todas las migraciones contra SQLite iOS. iOS son instalaciones nuevas (no hay BD previa) |
| ListConverters: JSON format Moshi ≠ kotlinx-serialization | Baja | Alto | Verificar que `["a","b"]` es idéntico en ambas libs. Escribir test de compatibilidad |
| cinterop con APIs iOS es complejo | Media | Medio | Empezar con stubs simples. Considerar escribir wrappers en Swift y exponerlos via @ObjCName |
| Build time KMP significativamente mayor | Alta | Medio | Habilitar Gradle build cache, configuration cache, incremental compilation. CI con más recursos |
| Compose Multiplatform: diferencias visuales Android vs iOS | Media | Medio | Testing visual frecuente en simulador iOS. Usar `@Preview` unificado de CMP 1.10 |
| Lottie sin soporte CMP | Cierta | Bajo | Evaluar compottie al inicio de Phase 6. Si no funciona, reemplazar con Compose animations |
| Google Maps no existe en CMP | Cierta | Alto | Diseñar la abstracción de mapa desde Phase 4 (interfaces). Implementar MapKit en Phase 7 |
| OneSignal sin SDK KMP | Cierta | Bajo | V1 iOS: no-op. V2: integrar SDK nativo iOS |

---

## Decisiones V2-Ready (tomar desde el principio)

1. **Navegación**: El `NavigationManager` basado en SharedFlow es agnóstico de plataforma → funciona para CarPlay y Web
2. **No usar Parcelize**: Usar `@Serializable` para argumentos de navegación → funciona en todas las plataformas
3. **No hardcodear Google Maps**: La abstracción `PlatformMapView` expect/actual soportará cualquier implementación futura
4. **Web targets en convention plugin**: Cuando se añada Web, solo hay que añadir `wasmJs()` al `KmpLibraryConventionPlugin`
5. **Resources CMP**: Usar el sistema de resources de Compose Multiplatform (`Res.string`, `Res.drawable`) desde Phase 6

---

## Verificación End-to-End

Para cada fase, ejecutar en este orden:
1. `./gradlew build` — compila todo el proyecto
2. `./gradlew test` — ejecuta tests unitarios
3. Instalar en dispositivo Android — verificar que funciona igual que antes
4. (Desde Phase 4) Compilar iOS framework — verificar que compila
5. (Desde Phase 7) Instalar en simulador iOS — verificar funcionalidad

---

## Próximos Pasos Inmediatos

1. **Hoy**: Crear la rama `feature/kmp-migration` desde `develop`
2. **Phase 0**: Crear los convention plugins nuevos (~1-2 días)
3. **Phase 1**: Migrar `:core:model` (~1 día, cambio mínimo)
4. **Phase 2**: Migrar `:core:common` (~2-3 días, primer contacto real con expect/actual)
5. **Validar**: Compilar proyecto completo tras cada fase