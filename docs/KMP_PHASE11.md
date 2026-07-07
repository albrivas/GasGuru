# KMP Phase 11 — Config unificada Android/iOS (entorno + versionado)

## Objetivo

Completar la migración KMP cerrando el único pilar que faltaba: la gestión de **flavors/entornos y versionado de forma unificada entre Android e iOS** desde un único punto de verdad.

---

## Problema que resolvía

| Pilar | Estado antes |
|---|---|
| Entorno mock/prod | Solo Android (flavors AGP). iOS cableaba `SupabaseRemoteDataSource` inline en `KoinInit.kt`. |
| Versionado | `versions.properties` en raíz, pero el valor llegaba al APK vía la GitHub Action `chkfung/android-version-actions` que reescribía `app/build.gradle.kts` (hardcodeado en `1`/`"1.0"` localmente). iOS sin versionado propio. |
| `:mocknetwork` | Módulo Android-only (`context.assets`). No consumible desde iOS. El JSON de 12MB iría en todos los builds iOS sin un mecanismo de exclusión. |

---

## Arquitectura resultante

### Single source of truth

```
GasGuru/
├── versions.properties              ← FUENTE de versión (bumpeada por pipelines, sin cambios)
│
├── build-logic/convention/
│   └── EnvironmentsConventionPlugin.kt   ← FUENTE de entornos (lista Kotlin type-safe)
│       · Lee versions.properties
│       · Android → productFlavors + versionCode/versionName
│       · iOS     → genera iosApp/Config/gasguru-{env}.xcconfig
│
├── app/build.gradle.kts             ← aplica el plugin (Android)
└── composeApp/build.gradle.kts      ← aplica el plugin (iOS xcconfig + condiciona :mocknetwork)
```

### División de responsabilidades

| Responsabilidad | Quién |
|---|---|
| Fuente de versión | `versions.properties` |
| Lista de entornos + sufijos | `EnvironmentsConventionPlugin.kt` (Kotlin, type-safe) |
| Flavors Android + versionCode/Name en APK | `EnvironmentsConventionPlugin` → AGP |
| xcconfig iOS (versión, bundle id, GASGURU_ENV) | `EnvironmentsConventionPlugin` → tarea `generateIosEnvConfig` |
| Excluir `:mocknetwork` de iOS-prod | `composeApp/build.gradle.kts` (`if (isMockIosBuild)`) |
| Seleccionar data source en iOS en compile-time | `iOSApp.swift` con `#if MOCK` |

---

## Cambios por módulo

### `build-logic/convention/`

- **`FlavorsConventionPlugin.kt` → eliminado**, sustituido por **`EnvironmentsConventionPlugin.kt`**.
- Mismo plugin id `gasguru.flavors` — no hay cambio de alias ni de los build.gradle que lo aplican.
- El nuevo plugin:
  1. Lee `versions.properties` con `java.util.Properties` (sin deps externas).
  2. Lista `environments` como `data class Environment(name, bundleSuffix, nameSuffix, versionNameSuffix, isMock)`.
  3. Aplica `versionCode`/`versionName` en `android.defaultConfig` (elimina el hardcode `1`/`"1.0"`).
  4. Crea `productFlavors` mock/prod (mismo comportamiento que antes).
  5. Registra la tarea `generateIosEnvConfig` (grupo `gasguru`) que escribe `.xcconfig` en `iosApp/Config/`.
  6. Engancha `generateIosEnvConfig` como `dependsOn` de `embedAndSignAppleFrameworkForXcode` y `syncFramework`.

### `:mocknetwork`

- Plugin: `gasguru.android.library` → **`gasguru.kmp.compose.library`** (para acceder a `Res.readBytes`).
- `MockRemoteDataSource.kt`: movido a `commonMain`, elimina `context`, usa `Res.readBytes("files/mock-fuel-stations.json").decodeToString()`.
- `MockModule.kt`: movido a `commonMain`, elimina `androidContext()`.
- JSON (12 MB): `src/main/assets/` → `src/commonMain/composeResources/files/`.
- Directorio `src/main/` eliminado.

### `:composeApp`

- Aplica `alias(libs.plugins.gasguru.flavors)` (solo el task de iOS; el bloque Android es no-op porque el plugin usa `plugins.withId("com.android.application")`).
- Detecta el entorno iOS: `val isMockIosBuild = System.getenv("CONFIGURATION")?.contains("Mock", true) == true`.
- Añade `:mocknetwork` a `iosMain.dependencies` solo si `isMockIosBuild` → **iOS-prod sin los 12MB**.
- Mapping de configs Xcode al build type nativo (necesario para `syncFramework`):
  ```kotlin
  xcodeConfigurationToNativeBuildType["Debug-Mock"] = NativeBuildType.DEBUG
  xcodeConfigurationToNativeBuildType["Release-Mock"] = NativeBuildType.RELEASE
  ```
- `KoinInit.kt` (iosMain): eliminado el binding inline `single<RemoteDataSource> { get<SupabaseRemoteDataSource>() }`. Ahora viene de `platformModules` (Swift).
- Nueva función pública `prodRemoteDataSourceModule()` en `iosMain` que Swift usa para el scheme prod.

### `iosApp/project.yml` (XcodeGen)

Cuatro configuraciones de Xcode en vez de dos:

| Config | Tipo nativo | xcconfig |
|---|---|---|
| `debug` | debug | (inline: bundle `.debug`, nombre "GasGuru Debug") |
| `release` | release | `Config/gasguru-prod.xcconfig` |
| `debug-mock` | debug | `Config/gasguru-mock.xcconfig` |
| `release-mock` | release | `Config/gasguru-mock.xcconfig` |

Dos schemes compartidos:
- **`GasGuru-Prod`**: run→debug, archive→release.
- **`GasGuru-Mock`**: run→debug-mock, archive→release-mock.

Los xcconfig en `iosApp/Config/` están en `.gitignore` (generados en cada build).

### `iosApp/iosApp/iOSApp.swift`

```swift
#if MOCK
let dataSourceModule = MockModuleKt.mockModule()
#else
let dataSourceModule = KoinInitKt.prodRemoteDataSourceModule()
#endif
bridge = KoinInitKt.doInitKoin(platformModules: [analyticsModule, notificationModule, dataSourceModule])
```

La flag `MOCK` la inyecta `SWIFT_ACTIVE_COMPILATION_CONDITIONS=$(inherited) MOCK` en `gasguru-mock.xcconfig`.

### CI (`deploy-to-playstore.yml`)

- Eliminado el step **"Bump version"** (`chkfung/android-version-actions`): ya no es necesario porque `EnvironmentsConventionPlugin` lee `versions.properties` en configuración de Gradle.
- Mantenido el step **"Read version.properties"**: sus variables (`versionMajor`, etc.) siguen siendo usadas por el step "Create GitHub Release" para el tag.

---

## Xcconfig generado — ejemplo `gasguru-mock.xcconfig`

```
// Auto-generated by EnvironmentsConventionPlugin — do not edit manually.
// Source of truth: versions.properties + EnvironmentsConventionPlugin.kt

MARKETING_VERSION=3.3.15
CURRENT_PROJECT_VERSION=92
PRODUCT_BUNDLE_IDENTIFIER=com.gasguru.mock
PRODUCT_NAME=GasGuru Mock
GASGURU_ENV=mock
SWIFT_ACTIVE_COMPILATION_CONDITIONS=$(inherited) MOCK
```

---

## Resultado final

| Escenario | Resultado |
|---|---|
| `./gradlew :app:assembleMockDebug` | APK con datos del JSON, bundle `.mock`, versión de `versions.properties` |
| `./gradlew :app:assembleProdDebug` | APK con Supabase real, versión correcta |
| `./gradlew :app:testMockDebugUnitTest :app:testProdDebugUnitTest` | Verde ✅ |
| `./gradlew codeCheck` | Verde ✅ |
| Xcode scheme `GasGuru-Mock` | "GasGuru Mock" (bundle `.mock`), datos del JSON local, `#if MOCK` activo |
| Xcode scheme `GasGuru-Prod` | Supabase real, framework sin JSON de 12MB, `#if MOCK` inactivo |
| Bump versión | Editar `versions.properties` → actualiza Android e iOS desde un solo sitio |

---

## Documentación relacionada

- `docs/CICD.md` — actualizado: step de bump eliminado
- `docs/DEPENDENCY_INJECTION.md` — actualizado: selección de data source por entorno
- `docs/KMP_MIGRATION.md` — Phase 11 añadida en checklist
