# KMP Phase 8 — iOS Readiness

## Objetivo

Hacer que la app **arranque y funcione en iOS**. Phase 7G completó la migración de toda la UI a Compose Multiplatform, pero quedan dos bloqueos que impiden ejecutar en iOS:

1. **`BuildConfig` no existe en KMP**: cinco módulos (`core/supabase`, `core/network`, `core/notifications`, `core/analytics`, `core/common`) siguen siendo `androidMain`-only porque sus DI/lógica leen tokens y versiones desde `BuildConfig`. Ningún DI llega al target iOS.
2. **Stubs no-op en `iosMain`**: doce implementaciones en `core/data`, `core/notifications`, `core/ui`, `feature/station-map` y `feature/detail-station` devuelven valores vacíos o no hacen nada (CoreLocation, Network.framework, MapKit, StoreKit, OneSignal, UNUserNotificationCenter, UIActivityViewController).

---

## Sub-fases (cada una = 1 PR independiente)

| Sub-fase | Estado | Objetivo | Bloqueada por |
|----------|--------|----------|--------------|
| [8A — BuildKonfig](#8a--buildkonfig) | `[ ]` | Eliminar `BuildConfig` multiplatform → objeto `BuildKonfig` en `commonMain` | — |
| [8B — `:iosApp`](#8b--iosapp--composeapp) | `[x]` | Crear `:iosApp` (Xcode project) + `:composeApp` (KMP framework con `MainViewController`) | — |
| [8C — `core/data` iOS](#8c--coredata-ios-implementations) | `[ ]` | Implementar LocationTracker, NetworkMonitor, GeocoderAddress, PlacesRepository con CoreLocation / Network.framework / MapKit | 8B |
| [8D — `core/notifications` iOS](#8d--corenotifications-ios-onesignal) | `[ ]` | Integrar OneSignal iOS SDK vía CocoaPods | 8B |
| [8E — `core/ui` iOS (InAppReview)](#8e--coreui-ios-inappreviewer) | `[ ]` | Implementar `requestReview()` con StoreKit `SKStoreReviewController` | 8B |
| [8F — Feature platform impls iOS](#8f--feature-platform-implementations-ios) | `[ ]` | Implementar `PlatformMapView` (MKMapView), `MapsNavigation`, `ShareAction`, `NotificationPermission` y `LocationPermission` iOS | 8B + 8C |
| [8G — `core/supabase` + `core/network` → commonMain](#8g--coresupabase--corenetwork--commonmain) | `[ ]` | Mover `SupabaseModule` y `KtorModule` a `commonMain` (desbloqueado por 8A) | **8A** |

---

## 8A — BuildKonfig

> Documento detallado: `docs/KMP_PHASE8A.md` (se crea al implementar)

### Contexto

`BuildConfig` es una clase generada por el Android Gradle Plugin y **solo existe en `androidMain`**. En KMP necesitamos un objeto equivalente accesible desde `commonMain`.

Solución elegida: [**BuildKonfig**](https://github.com/yshrsmz/BuildKonfig) (`com.codingfeline.buildkonfig`, v0.17.1). Genera un objeto `BuildKonfig` en cada source-set, alimentado desde `local.properties` y `versions.properties`.

### Mapeo BuildConfig → BuildKonfig

| Field | Tipo | Consumidor actual | Nota |
|-------|------|-------------------|------|
| `versionMajor`, `versionMinor`, `versionPatch`, `versionCode` | Int | `core/common/AppVersion.kt` | Permite eliminar el `expect/actual` de `AppVersion` |
| `supabaseUrl`, `supabaseKey` | String | `core/supabase/SupabaseModule.kt` | Desbloquea 8G |
| `supabaseGoboUrl`, `supabaseGoboKey` | String | `core/network/KtorModule.kt` | Desbloquea 8G |
| `oneSignalAppId` | String | `core/notifications/OneSignalConfiguration.kt` | — |
| `mixpanelToken` | String | `core/analytics/MixpanelAnalyticsHelper.kt` | — |
| `applicationId` | String | `core/widget/.../WidgetWorker.kt` | **No migra** — Android Glance only |
| `googleStyleId` | String | `feature/station-map/androidMain/.../PlatformMapView.kt` | **No migra** — Android Google Maps only |

### Archivos a tocar

- `gradle/libs.versions.toml` — añadir BuildKonfig
- `build-logic/convention/build.gradle.kts` — registrar convention plugin
- `build-logic/convention/src/main/java/BuildKonfigConventionPlugin.kt` (NUEVO)
- `build-logic/convention/src/main/java/SecretsConventionPlugin.kt` — limpiar los 6 fields migrados
- `core/common`, `core/analytics`, `core/supabase`, `core/network`, `core/notifications` — aplicar plugin + sustituir `BuildConfig.*` por `BuildKonfig.*`
- `core/common/src/commonMain/.../AppVersion.kt` — colapsar `expect/actual` en función única

### Verificación

```bash
./gradlew :build-logic:convention:build
./gradlew :core:common:compileKotlinIosX64 :core:common:testDebugUnitTest
./gradlew :core:analytics:compileKotlinIosX64
./gradlew :core:supabase:compileKotlinIosX64
./gradlew :core:network:compileKotlinIosX64
./gradlew :core:notifications:compileKotlinIosX64
./gradlew :app:assembleDebug
```

---

## 8B — `:iosApp` + `:composeApp`

> Documento detallado: `docs/KMP_PHASE8B.md` (se crea al implementar)

### Contexto

Para ejecutar en iOS necesitamos:

1. **`:composeApp`** — módulo KMP compartido que expone un `MainViewController()` (Compose UIViewController) para iOS y contiene la raíz `App()` Composable con el NavHost. Monta todos los `:feature:*` y `:core:*`. Incluye un `Podfile` para CocoaPods.
2. **`:iosApp`** — Xcode project mínimo con un SwiftUI entry point que embebe `MainViewController`.

### Estructura objetivo

```
composeApp/
├── build.gradle.kts        # KMP + cocoapods + framework isStatic=true
└── src/
    ├── commonMain/         # App.kt, di/AppModule.kt
    ├── androidMain/        # MainActivity.kt
    └── iosMain/            # MainViewController.kt

iosApp/
├── iosApp.xcodeproj
├── Podfile
└── iosApp/
    ├── Info.plist          # permisos: Location, Notifications, Photos
    ├── iOSApp.swift        # @main SwiftUI App
    └── ContentView.swift   # ComposeUIViewController wrapper
```

### Verificación

```bash
./gradlew :composeApp:linkPodReleaseFrameworkIosArm64
./gradlew :app:assembleDebug
# Tras pod install:
xcodebuild -workspace iosApp.xcworkspace -scheme iosApp -sdk iphonesimulator build
```

Manual: lanzar en simulador → pantalla inicial visible (Map stub), sin crash.

---

## 8C — `core/data` iOS implementations

> Documento detallado: `docs/KMP_PHASE8C.md` (se crea al implementar)

Cuatro stubs en `core/data/src/iosMain` a implementar con frameworks nativos:

| Clase | Framework | API clave |
|-------|-----------|-----------|
| `LocationTrackerIos` | CoreLocation | `CLLocationManager` + delegate |
| `NetworkMonitorIos` | Network.framework | `NWPathMonitor` |
| `GeocoderAddressIos` | CoreLocation | `CLGeocoder.reverseGeocodeLocation` |
| `PlacesRepositoryIos` | MapKit | `MKLocalSearch` |

---

## 8D — `core/notifications` iOS (OneSignal)

> Documento detallado: `docs/KMP_PHASE8D.md` (se crea al implementar)

Integrar [OneSignal iOS SDK](https://documentation.onesignal.com/docs/ios-sdk-setup) vía CocoaPods en `core/notifications` — el mismo patrón de `core/analytics` con `pod("Mixpanel-swift")`.

Patrón de referencia: `core/analytics/build.gradle.kts` (ya usa `kotlin("native.cocoapods")`).

---

## 8E — `core/ui` iOS (InAppReview)

> Documento detallado: `docs/KMP_PHASE8E.md` (se crea al implementar)

Reemplazar el stub `false`/no-op de `InAppReview.kt` iosMain por:

```kotlin
import platform.StoreKit.SKStoreReviewController
// SKStoreReviewController.requestReviewInScene(scene)
```

---

## 8F — Feature platform implementations iOS

> Documento detallado: `docs/KMP_PHASE8F.md` (se crea al implementar)

Cinco stubs en features a implementar:

| Módulo | Clase | Framework | Implementación |
|--------|-------|-----------|----------------|
| `feature/station-map` | `PlatformMapView` | MapKit | `UIKitView { MKMapView() }` con `MKAnnotation`s y `MKPolyline` |
| `feature/station-map` | `LocationPermission` | CoreLocation | Leer `CLLocationManager.authorizationStatus()` |
| `feature/detail-station` | `MapsNavigation` | MapKit | `MKMapItem.openInMaps(launchOptions:)` |
| `feature/detail-station` | `ShareAction` | UIKit | `UIActivityViewController` |
| `feature/detail-station` | `NotificationPermission` | UserNotifications | `UNUserNotificationCenter.requestAuthorization` |

---

## 8G — `core/supabase` + `core/network` → commonMain

> Documento detallado: `docs/KMP_PHASE8G.md` (se crea al implementar)

Con 8A completado, `SupabaseModule.kt` y `KtorModule.kt` ya no dependen de `BuildConfig`. Se mueven de `androidMain` a `commonMain`. El cliente Supabase (`io.github.jan-tennert.supabase`) ya es KMP. El engine de Ktor ya tiene `expect/actual` con `OkHttp` (Android) y `Darwin` (iOS) de Phase 5C.

---

## Riesgos globales

| Riesgo | Mitigación |
|--------|------------|
| BuildKonfig incompatible con flavors `prod`/`mock` | Usar `defaultConfigs("prod")` y `defaultConfigs("mock")` — soportado nativamente |
| `CLLocationManager` garbage-collected en Kotlin/Native | Mantener referencia strong en singleton del `LocationTrackerIos` |
| Linker error: framework no exporta símbolos de `:feature:*` | Usar `export(projects.feature.X)` en `composeApp/build.gradle.kts` |
| CocoaPods versión incompatible con Xcode 16 | Pinear `platform :ios, '15.0'` en Podfile |
| Tamaño del framework iOS > 300 MB | `isStatic = true` + revisar `export()` para no incluir transitive deps innecesarias |

---

## Fases futuras (post-Phase 8)

- **Phase 9 — Production hardening iOS**: App Store Connect, TestFlight, CI pipeline macOS (GitHub Actions `macos-15`), `PrivacyInfo.xcprivacy`, push certs producción.
- **Phase 10 — Target `jvm()`**: tests headless sin emulador (desbloqueado cuando todo esté en `commonMain`).
