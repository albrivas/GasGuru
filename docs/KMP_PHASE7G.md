# KMP Phase 7G — `:feature:station-map` → Compose Multiplatform

## Objetivo

Migrar `:feature:station-map` de `gasguru.android.library` a `gasguru.kmp.compose.library`, moviendo toda la UI no-mapa a `commonMain` y encapsulando Google Maps Compose (Android-only) y la gestión de permisos de ubicación mediante `expect/actual` dentro del propio módulo.

---

## Hallazgos clave

- **Google Maps Compose es Android-only**: no existe librería KMP de mapas. La solución es `PlatformMapView` `expect/actual` — Android usa `GoogleMap`, iOS V1 usa un `Box` stub (coherente con los demás stubs `iosMain` del proyecto: `LocationTrackerIos`, `PlacesRepositoryIos`, etc.).
- **`LatLngBounds` filtraba al ViewModel**: el state (`StationMapUiState`) usaba `com.google.android.gms.maps.model.LatLngBounds`, impidiendo mover el ViewModel a `commonMain`. Reemplazado por `GeoBounds` (nuevo modelo común en el módulo).
- **`FilterUiState.fromTranslatedString(String, Context)`**: reverse-lookup frágil que buscaba un enum desde el string ya traducido. Reemplazado por un `labelToSchedule` map construido en composición con `stringResource`, eliminando la dependencia de `Context`.
- **Accompanist permissions**: declarado en `libs.versions.toml` pero sin ningún uso real en el proyecto. Se mantiene en el toml (limpieza de dependencias no usadas está fuera del alcance de esta fase).

---

## Cambios principales

### `build.gradle.kts`

- Plugin `gasguru.android.library` + `gasguru.compose.library` → `gasguru.kmp.compose.library`
- Deps comunes en `commonMain.dependencies`, Google Maps en `androidMain.dependencies`
- Tests en `commonTest` (kotlin.test + Turbine + JUnit5) + `androidUnitTest` (JUnit5 engine)

### Modelo común: `GeoBounds`

```kotlin
// commonMain/kotlin/.../ui/model/GeoBounds.kt
data class GeoBounds(val southwest: LatLng, val northeast: LatLng) {
    companion object {
        fun fromPoints(points: List<LatLng>): GeoBounds? { ... }
    }
}
```

Reemplaza `com.google.android.gms.maps.model.LatLngBounds` en `StationMapUiState` y `StationMapViewModel`. La conversión a `LatLngBounds` ocurre únicamente en `androidMain/platform/Mappers.kt`.

### APIs Android-only → `expect/actual` en `platform/`

| Archivo | `expect` (commonMain) | `actual` Android | `actual` iOS |
|---------|----------------------|-----------------|--------------|
| `PlatformMapView.kt` | `@Composable expect fun PlatformMapView(...)` | `GoogleMap` + `MarkerComposable` + `Polyline` | `Box` stub (V2: `UIKitView { MKMapView() }`) |
| `LocationPermission.kt` | `@Composable expect fun rememberLocationPermissionState(): LocationPermissionState` | `DisposableEffect` ON_RESUME + `rememberLauncherForActivityResult(RequestMultiplePermissions)` | `LocationPermissionState(isGranted = true, ...)` |

`LocationPermissionState` es una clase `@Stable` con `isGranted`, `isDenied`, `requestPermission: () -> Unit`, `openSettings: () -> Unit`.

### `StationMapScreen.kt`

- Eliminados todos los imports Android/Google: `ContextCompat`, `Intent`, `LifecycleEventObserver`, `LocalContext`, `rememberLauncherForActivityResult`, `R.string.*`, `BuildConfig`, `com.google.maps.*`, `com.google.android.gms.maps.*`
- `stringResource(R.string.X)` → `stringResource(Res.string.X)` (CMP, con alias `StationMapRes`)
- `rememberLocationPermissionState()` desde `platform/`
- `PlatformMapView(...)` reemplaza el composable `MapView` interno
- `openingHoursLabel(@Composable)`: helper que mapea `FilterUiState.OpeningHours` a `stringResource`, reemplazando `resId: Int`
- `koinViewModel` de `org.koin.compose.viewmodel.koinViewModel`

### `FilterUiState.kt`

- Eliminado `resId: Int` del enum `OpeningHours`
- Eliminado `fromTranslatedString(String, Context)` (dependencia de `Context` y `R.string`)
- Añadido `fromName(String?): OpeningHours` (lookup por `enum.name`)
- El label de cada opción se calcula en composición con `stringResource`

### Conversiones internas (`androidMain`)

```kotlin
// platform/Mappers.kt — solo visible desde el actual Android
internal fun GeoBounds.toGoogleLatLngBounds(): LatLngBounds = ...
internal fun LatLng.toGoogleLatLng(): GoogleLatLng = ...
```

---

## Estructura de directorios resultante

```
feature/station-map/src/
├── commonMain/
│   ├── composeResources/
│   │   ├── values/strings.xml        # 14 strings (EN)
│   │   └── values-es/strings.xml     # 14 strings (ES)
│   └── kotlin/com/gasguru/feature/station_map/
│       ├── analytics/
│       ├── di/
│       ├── navigation/
│       ├── platform/                  # expect declarations
│       │   ├── LocationPermission.kt
│       │   └── PlatformMapView.kt
│       └── ui/
│           ├── model/GeoBounds.kt
│           ├── models/RouteUiModel.kt
│           ├── FilterUiState.kt
│           ├── StationMapEffect.kt
│           ├── StationMapEvent.kt
│           ├── StationMapScreen.kt
│           ├── StationMapUiState.kt
│           └── StationMapViewModel.kt
├── androidMain/
│   ├── kotlin/.../platform/
│   │   ├── LocationPermission.kt     # actual Android (launcher + lifecycle)
│   │   ├── Mappers.kt                # GeoBounds/LatLng → Google types (internal)
│   │   └── PlatformMapView.kt        # actual Android (GoogleMap)
│   └── res/values/google_maps_api.xml
├── iosMain/
│   └── kotlin/.../platform/
│       ├── LocationPermission.kt     # actual iOS stub
│       └── PlatformMapView.kt        # actual iOS stub (Box)
└── commonTest/
    └── kotlin/.../ui/
        ├── StationMapDeepLinkTest.kt
        └── StationMapViewModelTest.kt
```

---

## Decisiones técnicas

| Decisión | Alternativa rechazada | Motivo |
|----------|-----------------------|--------|
| `PlatformMapView` `expect/actual` dentro de la feature | Librería KMP de mapas | No existe ninguna con paridad funcional a Google Maps Compose |
| iOS V1 = `Box` stub | MapKit funcional en V1 | Sin `:iosApp` ni `LocationTrackerIos` funcional, un mapa real no aporta valor. Consistente con los demás stubs `iosMain` |
| `GeoBounds` local al módulo | Promover a `:core:model` | Único consumidor. Se puede promover cuando haya un segundo módulo que lo necesite |
| `LocationPermissionState` clase `@Stable` | Callbacks puros (`onGranted`, `onDenied`) | El state class permite inspeccionar `isGranted`/`isDenied` en composición sin un frame de flash cuando ya está concedido |
| `fromName(String?)` en `FilterUiState.OpeningHours` | Mantener `fromTranslatedString(String, Context)` | El reverse-lookup por string traducido es frágil (cambia con el idioma). El `name` del enum es estable |

---

## Verificación

```bash
./gradlew :feature:station-map:compileDebugKotlinAndroid   # ✅
./gradlew :feature:station-map:testDebugUnitTest           # ✅ (19 tests)
./gradlew :app:assembleDebug                               # ✅
```

Los errores de `compileKotlinIosX64` son **pre-existentes** en `core:uikit` y `core:analytics:cinteropMixpanel` — no relacionados con esta fase.

---

## Próxima fase

**Phase 7 final**: crear módulo `:iosApp` con target iOS (Xcode project + SwiftUI entry point + Koin initialization). Desbloqueado al completar la migración de todas las features a CMP.

**Phase 8**: añadir target `jvm()` para tests de Compose sin emulador (desbloqueado al eliminar todas las deps Android-only de `commonMain`).
