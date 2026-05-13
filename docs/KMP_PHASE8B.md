# KMP Phase 8B — `:composeApp` + `:iosApp`

## Objetivo

Crear el armazón mínimo que permite que iOS compile y arranque, sin modificar el `:app` Android existente. El `:app` Android sigue siendo el entry point Android sin cambios funcionales.

---

## Estructura creada

```
composeApp/
├── build.gradle.kts                          # gasguru.kmp.library.compose + cocoapods
└── src/
    ├── commonMain/kotlin/com/gasguru/composeApp/
    │   └── App.kt                            # Composable raíz con placeholder iOS
    └── iosMain/kotlin/com/gasguru/composeApp/
        └── MainViewController.kt             # ComposeUIViewController { App() }

iosApp/
├── Podfile                                   # pod 'ComposeApp', :path => '../composeApp'
└── iosApp/
    ├── Info.plist
    ├── iOSApp.swift                          # @main SwiftUI entry point
    └── ContentView.swift                     # UIViewControllerRepresentable wrapper
```

**Prerequisito manual**: `iosApp.xcodeproj` debe crearse en Xcode (File → New Project → iOS → App, SwiftUI, bundle id `com.gasguru.iosApp`, iOS 15.0+). Luego `cd iosApp && pod install`.

---

## Cambios en módulos existentes

### `core/uikit` — dos fixes para compilación iOS

| Archivo | Cambio |
|---------|--------|
| `commonMain/theme/ThemePreviews.kt` | Convertido a `expect annotation class` (sin `@Preview` — solo disponible en Android) |
| `androidMain/theme/ThemePreviews.kt` | `actual` con `@Preview` (Light/Dark) para Android Studio |
| `iosMain/theme/ThemePreviews.kt` | `actual` vacío (no-op en iOS) |
| `commonMain/utils/ModifierUtils.kt` | `maestroTestTag` convertido a `expect fun` |
| `androidMain/utils/MaestroTestTag.kt` | `actual` con `testTagsAsResourceId = true` (necesario para Maestro `id:` selector) |
| `iosMain/utils/ModifierUtils.kt` | `actual` con solo `.testTag(tag)` |

**Por qué `maestroTestTag` necesita `expect/actual`**: `testTagsAsResourceId` es Android-only. El test `.maestro/onboarding/onboarding.yaml` usa `assertVisible: id: "search_bar"` que requiere que Maestro pueda encontrar el elemento por ID en Android — lo cual depende de `testTagsAsResourceId = true`.

---

## Comandos de verificación

```bash
# Android
./gradlew :composeApp:assembleDebug
./gradlew :app:assembleDebug
./gradlew :app:testProdDebugUnitTest

# iOS framework
./gradlew :composeApp:linkPodReleaseFrameworkIosSimulatorArm64

# iOS app (tras crear .xcodeproj y hacer pod install)
cd iosApp && pod install
xcodebuild -workspace iosApp.xcworkspace -scheme iosApp -sdk iphonesimulator -configuration Debug build
```

---

## Qué NO se migra en esta sub-fase

Los siguientes elementos permanecen en `:app` Android hasta sub-fases posteriores:

- `MainActivity` + `GasGuruApplication` (Android entry point)
- `GasGuruApp`, `GasGuruNavHost`, `NavigationBarScreen` (NavHost completo)
- `SplashViewModel` / `appModule` (usa `BuildConfig.*` — bloqueado por 8A)
- `feature/vehicle` (no migrado a CMP — excluido del NavHost iOS)
- `feature/widget` / `auto/common` (Android-only permanente)
- Init Koin desde iOS — entra en sub-fases 8C/8D/8E/8F

---

## Siguiente sub-fase

Con 8B completado:
- **8C** — Implementar `LocationTrackerIos`, `NetworkMonitorIos`, `GeocoderAddressIos`, `PlacesRepositoryIos` con CoreLocation / Network.framework / MapKit
- **8A** (paralelo) — BuildKonfig para eliminar `BuildConfig` de los módulos KMP
