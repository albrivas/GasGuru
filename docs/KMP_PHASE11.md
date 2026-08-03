# KMP Phase 11 — Config unificada Android/iOS (entorno + versionado)

## Cómo funciona hoy

Dos entornos (`prod`/`mock`) × dos build types (`debug`/`release`) = 4 combinaciones, iguales en
concepto en Android y en iOS, pero cableadas con el mecanismo nativo de cada plataforma.

### 1. Los ficheros específicos de entorno en iOS

Xcode no sabe nada de "flavors" — su unidad de configuración es la `.xcconfig` por **Configuration**
(el nombre que aparece en el selector de esquema/build). En este proyecto hay 4 Configurations,
declaradas en `iosApp/project.yml`: `Debug`, `Release`, `Debug-Mock`, `Release-Mock`. Cada una tiene
su propio `.xcconfig` en `iosApp/Config/`:

```
iosApp/Config/
├── gasguru-Prod-Debug.xcconfig
├── gasguru-Prod-Release.xcconfig
├── gasguru-Mock-Debug.xcconfig
└── gasguru-Mock-Release.xcconfig
```

Cada `.xcconfig` fija, para esa combinación exacta: `PRODUCT_BUNDLE_IDENTIFIER`, `PRODUCT_NAME`,
`MARKETING_VERSION`/`CURRENT_PROJECT_VERSION`, `GASGURU_ENV`, `CODE_SIGN_ENTITLEMENTS` (apuntando a
`iosApp-Debug.entitlements` o `iosApp-Release.entitlements`) y `SWIFT_ACTIVE_COMPILATION_CONDITIONS`
(con el flag `MOCK` solo en las dos variantes mock). `iosApp/project.yml` conecta cada Configuration
con su `.xcconfig` vía `configFiles:` a nivel de target, y los 2 schemes (`GasGuru-Prod`,
`GasGuru-Mock`) apuntan cada uno a su par debug/release. `Info.plist` y el código Swift solo leen
placeholders (`$(MARKETING_VERSION)`, `$(GASGURU_ENV)`, `#if MOCK`) — nunca un valor fijo.

Esto es exactamente lo que Android resuelve con `productFlavors` (`mock`/`prod`) × `buildTypes`
(`debug`/`release`): mismo concepto, mecanismo distinto porque AGP y Xcode no comparten infraestructura.

### 2. Qué hace el plugin (`EnvironmentsConventionPlugin`)

Los 4 `.xcconfig` de iOS **no se escriben a mano** — los genera `EnvironmentsConventionPlugin.kt`
(`build-logic/convention/`), aplicado como `gasguru.flavors` en `:app` y `:composeApp`. El plugin:

1. Lee `versions.properties` (única fuente de versión, la misma que bumpean los pipelines de release).
2. Mantiene, en Kotlin, la lista de entornos (`prod`, `mock`, con sus sufijos de bundle id/nombre) y de
   build variants (`Debug`, `Release`, con los suyos) — la matriz completa vive en un solo sitio.
3. En Android (`plugins.withId("com.android.application")`): crea los `productFlavors` y aplica
   `versionCode`/`versionName`; además fija `app_name` por combinación (flavor, buildType) vía la
   variant API, porque el `resValue` de un buildType normalmente pisa al de un flavor.
4. En iOS (solo si el módulo es KMP): registra la tarea `generateIosEnvConfig`, que recorre la misma
   matriz de entornos × build variants y escribe los 4 `.xcconfig` en `iosApp/Config/` — combinando
   siempre los mismos ingredientes: versión de `versions.properties`, bundle id/nombre calculados,
   entitlements y la flag `MOCK` si aplica. Esa tarea corre automáticamente antes de que Xcode
   incruste el framework de Kotlin (`dependsOn` de `embedAndSignAppleFrameworkForXcode`/`syncFramework`).

En otras palabras: **el plugin es la única fuente de verdad de la matriz de entornos**; Android
consume esa matriz vía AGP (`productFlavors`), iOS la consume vía `.xcconfig` generados en disco.
Cambiar un entorno (añadir uno nuevo, cambiar un sufijo) se hace una vez, en Kotlin, y se propaga solo.

### 3. Cómo se conecta con el runtime

Ni AGP ni Xcode saben nada de Koin. La conexión entre "qué build se compiló" y "qué implementación usa
el código" pasa por cada plataforma por separado, pero converge en el mismo sitio:

- **Android**: el flavor activo selecciona el source set (`app/src/mock/...` o `app/src/prod/...`),
  y cada uno bindea su propio `remoteDataSourceModule()` en Koin.
- **iOS**: `SWIFT_ACTIVE_COMPILATION_CONDITIONS=... MOCK` (inyectado por el `.xcconfig` del punto 1)
  activa `#if MOCK` en `iOSApp.swift`, que elige entre `MockModuleKt.mockModule()` y
  `KoinInitKt.prodRemoteDataSourceModule()`.
- **Ambos caminos acaban bindeando lo mismo en Koin**: un `RemoteDataSource` y un `AppEnvironment`
  (enum `Prod`/`Mock` en `:core:model`). Así, código de `commonMain` que necesite saber el entorno
  activo simplemente resuelve `get<AppEnvironment>()`, sin ninguna lógica de build involucrada.

El resto de este documento detalla, módulo a módulo, los cambios concretos que llevaron a este diseño.

---

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
| Leer el entorno activo desde `commonMain` | `AppEnvironment` (enum en `:core:model`), bindeado en Koin |

### Lo que se unifica y lo que no

Lo compartido entre Android e iOS es el **build**, no el runtime: una única lista `Environment` en
`EnvironmentsConventionPlugin.kt` se traduce a `productFlavors` de AGP en Android y a `.xcconfig` en
iOS. La **selección de implementación** sigue siendo nativa de cada plataforma por necesidad — source
sets de flavor en Android, `#if MOCK` en Swift — pero ambos caminos **convergen en Koin**: los dos
acaban registrando un `RemoteDataSource` (y un `AppEnvironment`) en el mismo grafo de dependencias.
Así, el código de `commonMain` nunca pregunta en qué entorno está corriendo — lo recibe por DI — pero
si necesita saberlo, puede resolver `get<AppEnvironment>()`.

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

### `:composeApp` y `:core:data`

- `:composeApp` aplica `alias(libs.plugins.gasguru.flavors)` (solo el task de iOS; el bloque Android es no-op porque el plugin usa `plugins.withId("com.android.application")`).
- Detecta el entorno iOS: `val isMockIosBuild = System.getenv("CONFIGURATION")?.contains("Mock", true) == true`.
- Añade `:mocknetwork` a `iosMain.dependencies` solo si `isMockIosBuild` → **iOS-prod sin los 12MB**. Se declara como `api`, no `implementation`: `framework { export(projects.mocknetwork) }` (también condicionado a `isMockIosBuild`) lo exige, porque `MockModuleKt.mockModule()` se llama directamente desde Swift y necesita quedar expuesto en el framework `ComposeApp`.
- Mapping de configs Xcode al build type nativo (necesario para `syncFramework`), en **ambos** módulos que aplican `kotlin("native.cocoapods")` — `:composeApp` y `:core:data` (este último por su pod `GooglePlaces`):
  ```kotlin
  xcodeConfigurationToNativeBuildType["Debug-Mock"] = NativeBuildType.DEBUG
  xcodeConfigurationToNativeBuildType["Release-Mock"] = NativeBuildType.RELEASE
  ```
  "Debug"/"Release" los resuelve el plugin de Kotlin/Native por sus propios defaults; solo hace falta declarar las variantes Mock.
- `KoinInit.kt` (iosMain): eliminado el binding inline `single<RemoteDataSource> { get<SupabaseRemoteDataSource>() }`. Ahora viene de `platformModules` (Swift).
- Nueva función pública `prodRemoteDataSourceModule()` en `iosMain` que Swift usa para el scheme prod.

### `iosApp/project.yml` (XcodeGen)

Cuatro configuraciones de Xcode, **capitalizadas** (coinciden con el nombre que Xcode usa por
convención y con las claves de `xcodeConfigurationToNativeBuildType`):

| Config | Tipo nativo | xcconfig (matriz entorno × buildType) |
|---|---|---|
| `Debug` | debug | `Config/gasguru-Prod-Debug.xcconfig` |
| `Release` | release | `Config/gasguru-Prod-Release.xcconfig` |
| `Debug-Mock` | debug | `Config/gasguru-Mock-Debug.xcconfig` |
| `Release-Mock` | release | `Config/gasguru-Mock-Release.xcconfig` |

Los 4 xcconfig se declaran vía `configFiles:` a nivel de target en `project.yml` — **no** vía
`settings.configs.<config>.baseConfiguration` (esa clave no existe en XcodeGen; se probó en el primer
intento de esta fase y XcodeGen la escribía como un build setting sin efecto, dejando el proyecto
generado sin ningún `.xcconfig` real aplicado — ver "Lecciones" más abajo).

Dos schemes compartidos:
- **`GasGuru-Prod`**: run→Debug, archive→Release.
- **`GasGuru-Mock`**: run→Debug-Mock, archive→Release-Mock.

Los xcconfig en `iosApp/Config/` están en `.gitignore` (generados por `generateIosEnvConfig`).
**`iosApp/iosApp.xcodeproj/` e `iosApp/iosApp.xcworkspace/` también están en `.gitignore`**:
XcodeGen sobrescribe el `.pbxproj` en cada `generate`, lo que destruye cualquier integración de
CocoaPods hecha a mano — commitear ambos a la vez solo produce un estado que ninguna de las dos
herramientas reconoce como suyo. `project.yml` es la única fuente de verdad; el proyecto Xcode se
regenera con `./scripts/ios-setup.sh` (`generateIosEnvConfig` → `xcodegen generate` → `pod install`,
en ese orden — invertirlo deja el proyecto sin pods).

### `iosApp/iosApp/iOSApp.swift`

```swift
#if MOCK
let dataSourceModule = MockModuleKt.mockModule()
#else
let dataSourceModule = KoinInitKt.prodRemoteDataSourceModule()
#endif
bridge = KoinInitKt.doInitKoin(platformModules: [analyticsModule, notificationModule, dataSourceModule])
```

La flag `MOCK` la inyecta `SWIFT_ACTIVE_COMPILATION_CONDITIONS=$(inherited) MOCK` en los xcconfig
`gasguru-Mock-*`.

### `AppEnvironment` (commonMain)

`enum class AppEnvironment { Prod, Mock }` en `:core:model`. Se bindea en Koin en los mismos módulos
que ya eligen el `RemoteDataSource` — `mockModule()` (`:mocknetwork`, cubre Android-mock e iOS-mock a
la vez) y los módulos prod de `:app`/`:composeApp` — así que cualquier código de `commonMain` puede
resolver `get<AppEnvironment>()` sin ninguna maquinaria de build nueva.

### Entitlements y consumo de `GASGURU_ENV`

- `iosApp/iosApp/iosApp.entitlements` (uno solo, `aps-environment=development` fijo) se sustituyó por
  `iosApp-Debug.entitlements` / `iosApp-Release.entitlements`, referenciados desde el `xcconfig` de
  cada config (`CODE_SIGN_ENTITLEMENTS=...`). Solo `Release`/`Release-Mock` usan `production`.
- `Info.plist`: `CFBundleShortVersionString`/`CFBundleVersion` pasan a `$(MARKETING_VERSION)`/
  `$(CURRENT_PROJECT_VERSION)` (antes hardcodeados a `1.0`/`1`, la versión de iOS nunca cambiaba). Se
  añade `GasGuruEnv=$(GASGURU_ENV)`, leíble en runtime vía
  `Bundle.main.object(forInfoDictionaryKey: "GasGuruEnv")`.

### CI (`deploy-to-playstore.yml`)

- Eliminado el step **"Bump version"** (`chkfung/android-version-actions`): ya no es necesario porque `EnvironmentsConventionPlugin` lee `versions.properties` en configuración de Gradle.
- Mantenido el step **"Read version.properties"**: sus variables (`versionMajor`, etc.) siguen siendo usadas por el step "Create GitHub Release" para el tag.

---

## Xcconfig generado — ejemplo `gasguru-Mock-Debug.xcconfig`

```
// Auto-generated by EnvironmentsConventionPlugin — do not edit manually.
// Source of truth: versions.properties + EnvironmentsConventionPlugin.kt

#include? "../Pods/Target Support Files/Pods-iosApp/Pods-iosApp.debug-mock.xcconfig"

MARKETING_VERSION=3.3.17
CURRENT_PROJECT_VERSION=94
PRODUCT_BUNDLE_IDENTIFIER=com.gasguru.mock.debug
PRODUCT_NAME=GasGuru Mock Debug
GASGURU_ENV=mock
CODE_SIGN_ENTITLEMENTS=iosApp/iosApp-Debug.entitlements
SWIFT_ACTIVE_COMPILATION_CONDITIONS=$(inherited) MOCK
```

El `#include?` del xcconfig de Pods es obligatorio: CocoaPods nombra sus xcconfig con el nombre
completo de la configuración de Xcode en minúsculas (`Pods-iosApp.debug-mock.xcconfig`, no
`Pods-iosApp.debug.xcconfig` — el sufijo `-mock` forma parte del nombre), y sin este include
`pod install` avisa de que "no pudo fijar la configuración base" y el link de los pods se pierde.

---

## Resultado final

| Escenario | Resultado |
|---|---|
| `./gradlew :app:assembleMockDebug` | APK con datos del JSON, bundle `.mock`, versión de `versions.properties` |
| `./gradlew :app:assembleProdDebug` | APK con Supabase real, versión correcta |
| `./gradlew :app:testMockDebugUnitTest :app:testProdDebugUnitTest` | Verde ✅ |
| `./gradlew codeCheck` | Verde ✅ |
| `./gradlew :composeApp:generateIosEnvConfig` tras editar `versions.properties` | Regenera los 4 `.xcconfig` (ya no queda UP-TO-DATE con una versión desactualizada) |
| `./scripts/ios-setup.sh` + `xcodebuild ... -scheme GasGuru-Mock -configuration Debug-Mock` | Compila; `#if MOCK` activo de verdad, "GasGuru Mock Debug", bundle `com.gasguru.mock.debug` |
| `./scripts/ios-setup.sh` + `xcodebuild ... -scheme GasGuru-Prod -configuration Debug` | Compila; Supabase real, "GasGuru Debug", bundle `com.gasguru.debug` |
| Bump versión | Editar `versions.properties` → actualiza Android e iOS desde un solo sitio |

---

## Deuda conocida (fuera de alcance de este cierre)

- **Secretos no separados por entorno.** Los `BuildKonfig` de `:core:supabase`, `:core:analytics` y
  `:core:notifications` solo tienen `defaultConfigs` (sin `flavor`): una build mock incrusta las
  claves reales de Supabase/Mixpanel/OneSignal. El dato en sí no llega a usarse en mock (el
  `RemoteDataSource` nunca llama a Supabase), pero analytics/push sí podrían contaminar producción
  desde una build mock. Resolverlo exige un segundo juego de claves y tocar los 3 bloques de secrets
  de los workflows de CI.
- **Sin CI de iOS.** Ningún workflow ejecuta `xcodegen`/`pod install`/`xcodebuild`; los 4 fallos
  descritos en "Lecciones" solo se detectaron con verificación manual.

## Lecciones

- **Una clave desconocida en `project.yml` no falla.** XcodeGen no valida contra un schema estricto:
  `settings.configs.<config>.baseConfiguration` no existe (la clave correcta es `configFiles:` a
  nivel de target), y en vez de fallar, XcodeGen la escribió como un build setting de nombre
  `baseConfiguration` sin ningún efecto. El primer intento de esta fase pasó revisión de código
  porque el `.pbxproj` generado *parecía* correcto sin abrirlo en Xcode ni compilar. Verificar
  siempre el `.pbxproj` generado (o compilar de verdad), nunca solo la spec de `project.yml`.
- **XcodeGen y CocoaPods commiteados juntos se pisan.** `xcodegen generate` sobrescribe
  `iosApp.xcodeproj` en cada ejecución, lo que borra cualquier integración de pods; si el
  `.pbxproj` está trackeado, cada regeneración produce un diff enorme y agujeros de integración
  invisibles hasta el primer build real. Con uno de los dos generado (ahora ambos vía
  `scripts/ios-setup.sh`) y el otro no tracked, este problema desaparece de raíz.

## Documentación relacionada

- `docs/CICD.md` — actualizado: step de bump eliminado
- `docs/DEPENDENCY_INJECTION.md` — actualizado: selección de data source por entorno + `AppEnvironment`
- `docs/KMP_MIGRATION.md` — Phase 11 añadida en checklist
