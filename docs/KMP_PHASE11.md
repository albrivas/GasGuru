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

### 2. Qué hace el plugin (`EnvironmentsConventionPlugin`) — y qué no hace

`EnvironmentsConventionPlugin.kt` (`build-logic/convention/`, aplicado como `gasguru.flavors` en
`:app`) es **Android-only**: lee `versions.properties`, crea los `productFlavors` (`mock`/`prod`),
aplica `versionCode`/`versionName`, y fija `app_name` por combinación (flavor, buildType) vía la
variant API (el `resValue` de un buildType normalmente pisa al de un flavor, así que hace falta la
variant API para que `mockRelease` no se llame igual que `prodRelease`).

**El plugin no toca nada de iOS.** Los 4 `.xcconfig` de `iosApp/Config/` son ficheros estáticos,
escritos a mano y commiteados — nadie los genera. Esto es deliberado, no una limitación: es el patrón
estándar en proyectos KMP en producción (ver "Por qué no generamos los xcconfig desde Gradle" más
abajo). Consecuencia práctica: **al bumpear versión, hay que editar `versions.properties` Y los 4
xcconfig a mano** — ver la skill `release`, que ya incluye este paso.

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

### Por qué no generamos los xcconfig desde Gradle

Una versión anterior de esta fase sí lo hacía: un plugin de Gradle calculaba bundle id/nombre/versión
en Kotlin y escribía los 4 `.xcconfig` en cada build, enganchado a
`embedAndSignAppleFrameworkForXcode`/`syncFramework`. Funcionaba, pero es más mecanismo del que el
ecosistema KMP considera estándar. En los patrones documentados (BuildKonfig, plantilla de Futured,
guías de Tooploox y sujanpoudel.me), el flujo va al revés: el `.xcconfig` de iOS es estático y
declara una variable de entorno simple (p. ej. `KMM_FLAVOR=dev`), que se pasa **de iOS hacia Gradle**
como propiedad (`-Pbuildkonfig.flavor=...`) cuando Xcode invoca el build — nunca al revés. El
versionado tampoco se sincroniza automáticamente entre plataformas; cada una bumpea la suya.

Se revirtió a ese patrón: los 4 `.xcconfig` son ahora estáticos, y el coste es aceptado
conscientemente — bumpear la versión de iOS a mano en cada release (ver skill `release`).

---

## Objetivo

Completar la migración KMP cerrando el único pilar que faltaba: que iOS tenga su propia gestión de
**flavors/entornos y versionado**, con la misma matriz conceptual que Android (prod/mock ×
debug/release) — cada plataforma con su mecanismo nativo, sin un generador cruzado entre ambas.

---

## Problema que resolvía

| Pilar | Estado antes |
|---|---|
| Entorno mock/prod | Solo Android (flavors AGP). iOS cableaba `SupabaseRemoteDataSource` inline en `KoinInit.kt`. |
| Versionado | `versions.properties` en raíz, pero el valor llegaba al APK vía la GitHub Action `chkfung/android-version-actions` que reescribía `app/build.gradle.kts` (hardcodeado en `1`/`"1.0"` localmente). iOS sin versionado propio. |
| `:mocknetwork` | Módulo Android-only (`context.assets`). No consumible desde iOS. El JSON de 12MB iría en todos los builds iOS sin un mecanismo de exclusión. |

---

## Arquitectura resultante

### Dos fuentes de verdad, una por plataforma

```
GasGuru/
├── versions.properties                    ← FUENTE de versión para Android
│   (bumpeada a mano en cada release, junto con los 4 xcconfig de abajo)
│
├── build-logic/convention/
│   └── EnvironmentsConventionPlugin.kt    ← FUENTE de entornos Android (lista Kotlin type-safe)
│       · Lee versions.properties
│       · productFlavors + versionCode/versionName + app_name por variant
│
├── iosApp/Config/gasguru-{Prod,Mock}-{Debug,Release}.xcconfig   ← FUENTE de entornos iOS
│   (4 ficheros estáticos, commiteados, mantenidos a mano)
│
└── app/build.gradle.kts                   ← aplica el plugin (Android)
```

### División de responsabilidades

| Responsabilidad | Quién |
|---|---|
| Flavors Android + versionCode/Name en APK | `EnvironmentsConventionPlugin.kt` → AGP |
| xcconfig iOS (versión, bundle id, GASGURU_ENV) | Ficheros estáticos en `iosApp/Config/`, mantenidos a mano |
| Excluir `:mocknetwork` de iOS-prod | `composeApp/build.gradle.kts` (`if (isMockIosBuild)`) |
| Seleccionar data source en iOS en compile-time | `iOSApp.swift` con `#if MOCK` |
| Leer el entorno activo desde `commonMain` | `AppEnvironment` (enum en `:core:model`), bindeado en Koin |

### Lo que se unifica y lo que no

Android e iOS **no comparten build**: cada plataforma tiene su propia fuente de verdad de entorno
(`EnvironmentsConventionPlugin.kt` + `productFlavors` en Android, xcconfig estáticos en iOS), y ambas
listas de valores (sufijos de bundle id, nombre, etc.) se mantienen en paralelo a mano. Lo que sí
converge es el **runtime**: la selección de implementación es nativa de cada plataforma — source sets
de flavor en Android, `#if MOCK` en Swift — pero ambos caminos acaban bindeando lo mismo en Koin, un
`RemoteDataSource` y un `AppEnvironment`. Así, el código de `commonMain` nunca pregunta en qué entorno
está corriendo — lo recibe por DI — pero si necesita saberlo, puede resolver `get<AppEnvironment>()`.

---

## Cambios por módulo

### `build-logic/convention/`

- **`FlavorsConventionPlugin.kt` → eliminado**, sustituido por **`EnvironmentsConventionPlugin.kt`**.
- Mismo plugin id `gasguru.flavors` — no hay cambio de alias ni de los build.gradle que lo aplican.
- El plugin es Android-only:
  1. Lee `versions.properties` con `java.util.Properties` (sin deps externas).
  2. Lista `environments` como `data class Environment(name, bundleSuffix, nameSuffix, versionNameSuffix, isMock)`.
  3. Aplica `versionCode`/`versionName` en `android.defaultConfig` (elimina el hardcode `1`/`"1.0"`).
  4. Crea `productFlavors` mock/prod (mismo comportamiento que antes).
  5. Fija `app_name` por (flavor, buildType) vía la variant API (`configureAppNamePerVariant`).

### `:mocknetwork`

- Plugin: `gasguru.android.library` → **`gasguru.kmp.compose.library`** (para acceder a `Res.readBytes`).
- `MockRemoteDataSource.kt`: movido a `commonMain`, elimina `context`, usa `Res.readBytes("files/mock-fuel-stations.json").decodeToString()`.
- `MockModule.kt`: movido a `commonMain`, elimina `androidContext()`.
- JSON (12 MB): `src/main/assets/` → `src/commonMain/composeResources/files/`.
- Directorio `src/main/` eliminado.

### `:composeApp` y `:core:data`

- `:composeApp` **no** aplica el plugin de entornos (es Android-only y no-op sin `com.android.application`).
  Detecta el entorno iOS directamente del entorno de Xcode: `val isMockIosBuild = System.getenv("CONFIGURATION")?.contains("Mock", true) == true`.
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

Los xcconfig en `iosApp/Config/` están **commiteados** (son la fuente de verdad de entorno para iOS,
mantenidos a mano). **`iosApp/iosApp.xcodeproj/` e `iosApp/iosApp.xcworkspace/` sí están en
`.gitignore`**: XcodeGen sobrescribe el `.pbxproj` en cada `generate`, lo que destruye cualquier
integración de CocoaPods hecha a mano — commitear ambos a la vez solo produce un estado que ninguna
de las dos herramientas reconoce como suyo. `project.yml` es la única fuente de verdad del proyecto
Xcode; se regenera con `./scripts/ios-setup.sh` (`xcodegen generate` → `pod install`, en ese orden —
invertirlo deja el proyecto sin pods).

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

## Xcconfig estático — ejemplo `gasguru-Mock-Debug.xcconfig`

```
// Manually maintained. Bump MARKETING_VERSION/CURRENT_PROJECT_VERSION here
// alongside versions.properties when releasing (see the `release` skill).

#include? "../Pods/Target Support Files/Pods-iosApp/Pods-iosApp.debug-mock.xcconfig"

MARKETING_VERSION=3.3.17
CURRENT_PROJECT_VERSION=94
PRODUCT_BUNDLE_IDENTIFIER=com.gasguru.mock.debug
PRODUCT_NAME=GasGuru Mock
GASGURU_ENV=mock
CODE_SIGN_ENTITLEMENTS=iosApp/iosApp-Debug.entitlements
SWIFT_ACTIVE_COMPILATION_CONDITIONS=$(inherited) MOCK
```

`PRODUCT_NAME`/`app_name` no distinguen debug/release para el entorno mock (ambos son "GasGuru
Mock") — a diferencia de prod, que sí lo hace ("GasGuru" / "GasGuru Debug"). Motivo: "GasGuru Mock
Debug" (18 caracteres) supera el límite recomendado de ~15 caracteres para el nombre del Home
Screen en iOS, y tanto iOS como Android empiezan a quitar espacios antes de truncar cuando el
nombre no cabe, produciendo algo ilegible tipo "GasGuruMockD…".

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
| `./scripts/ios-setup.sh` + `xcodebuild ... -scheme GasGuru-Mock -configuration Debug-Mock` | Compila; `#if MOCK` activo de verdad, "GasGuru Mock", bundle `com.gasguru.mock.debug` |
| `./scripts/ios-setup.sh` + `xcodebuild ... -scheme GasGuru-Prod -configuration Debug` | Compila; Supabase real, "GasGuru Debug", bundle `com.gasguru.debug` |
| Bump versión | Editar `versions.properties` (Android) **y** los 4 xcconfig de `iosApp/Config/` (iOS) — dos pasos manuales, ver skill `release` |

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
- **Bump de versión manual en dos sitios.** Al aceptar xcconfig estáticos, se acepta también el
  riesgo que ya causó el bug original (versión de iOS desincronizada). Mitigado documentando el paso
  en la skill `release`, pero sigue siendo un paso manual, no una garantía del sistema de build.

## Lecciones

- **Generar `.xcconfig` desde Gradle es más mecanismo del que el ecosistema KMP considera estándar.**
  Se probó (plugin custom + tarea `generateIosEnvConfig`, matriz 4×, `dependsOn` en el embed de
  Xcode) y funcionaba, pero el patrón documentado en la comunidad (BuildKonfig, plantilla de
  Futured, Tooploox, sujanpoudel.me) va al revés: xcconfig estático en iOS, flavor pasado hacia
  Gradle como propiedad, nunca Gradle escribiendo ficheros de Xcode. Se revirtió a ese patrón — ver
  "Por qué no generamos los xcconfig desde Gradle" más arriba.

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
