# KMP Phase 9B — iOS LocationTracker + LocationPermission (CLLocationManager)

## Objetivo

Sustituir los dos stubs bloqueantes de iOS que impedían cualquier funcionalidad basada en ubicación:

| Stub | Módulo | Antes | Después |
|------|--------|-------|---------|
| `LocationTrackerIos` | `:core:data` | `null`/`flowOf(null/false)` | `CLLocationManager` real vía `callbackFlow` |
| `rememberLocationPermissionState` | `:feature:station-map` (iosMain) | `isGranted=true` hardcoded | `CLAuthorizationStatus` reactivo via delegate |

Además, se aprovecha la necesidad de conectar `openSettings` en iOS para eliminar el param drilling de `onOpenLocationSettings` a través de 8 archivos intermedios, sustituyéndolo por `LocalOpenLocationSettings` (`staticCompositionLocalOf`).

## Decisiones técnicas

### Delegate como NSObject Kotlin/Native (sin Swift bridge)

`CLLocationManagerDelegateProtocol` se implementa directamente en Kotlin/Native subclasificando `NSObject`. No se requiere ningún wrapper Swift. Patrón equivalente al de Phase 9A con `callbackFlow` + `awaitClose` para limpieza del delegate.

### CLLocationManager en main thread

`CLLocationManager` y sus callbacks deben ejecutarse en el main thread iOS. Se usa `withContext(Dispatchers.Main)` en `getCurrentLocation()` para asegurar esto. Los `callbackFlow`s de los otros flows se nutren de callbacks que iOS siempre entrega en main thread (comportamiento por defecto cuando no se asigna `delegateQueue`).

### Mapping CLAuthorizationStatus → (isGranted, isDenied)

| Status iOS | isGranted | isDenied |
|------------|-----------|----------|
| `AuthorizedWhenInUse` / `AuthorizedAlways` | true | false |
| `Denied` / `Restricted` | false | true |
| `NotDetermined` | false | false |

`NotDetermined` es el estado inicial. Cuando el usuario toca "Permitir", `manager.requestWhenInUseAuthorization()` dispara el prompt nativo y el delegate `locationManagerDidChangeAuthorization` actualiza el estado automáticamente sin recomposición manual.

### Permiso lazy (paridad con Android)

El onboarding no pide ubicación en ninguna plataforma. El único consumidor de `rememberLocationPermissionState` es `StationMapScreen`. iOS mantiene la misma semántica lazy: el prompt aparece al primer acceso al mapa.

### LocalOpenLocationSettings — eliminar prop drilling

Se creó `core/ui/src/commonMain/.../LocalOpenLocationSettings.kt` siguiendo el patrón de `LocalAnalyticsHelper` (mismo módulo, mismo patrón `staticCompositionLocalOf`, `error()` como default). Se provee en `App.kt` junto al resto de `CompositionLocalProvider`s.

Archivos intermedios limpiados (ya no reciben ni reenvían el param):
- `GasGuruApp.kt` — lee el local directamente en el AlertDialog `LocationOff`
- `GasGuruNavHost.kt`
- `NavigationBarNavigation.kt`
- `NavigationBarScreen.kt`
- `FavoriteStationListGraph.kt`
- `FavoriteStationListNavigation.kt`
- `FavoriteStationListScreen.kt` (ScreenRoute lee el local; función interna conserva su param para tests y previews)

### Info.plist

Se añadió `NSLocationWhenInUseUsageDescription` con copy en inglés. La localización completa a `es.lproj/InfoPlist.strings` requiere modificar el `project.pbxproj` desde Xcode; queda pendiente como tarea de mejora V1.5 cuando se configure la localización iOS completa del proyecto.

## Patrón de implementación

### LocationTrackerIos — getCurrentLocation (one-shot)

```kotlin
override suspend fun getCurrentLocation(): LatLng? = withContext(Dispatchers.Main) {
    val manager = CLLocationManager()
    suspendCancellableCoroutine { continuation ->
        val delegate = object : NSObject(), CLLocationManagerDelegateProtocol {
            override fun locationManager(manager: CLLocationManager, didUpdateLocations: List<*>) {
                val location = (didUpdateLocations as? List<CLLocation>)?.lastOrNull()
                continuation.resume(location?.toLatLng())
            }
            override fun locationManager(manager: CLLocationManager, didFailWithError: NSError) {
                continuation.resume(null)
            }
        }
        manager.delegate = delegate          // delegate es weak → closure lo retiene fuerte
        manager.requestLocation()
        continuation.invokeOnCancellation {
            manager.delegate = null
            manager.stopUpdatingLocation()
        }
    }
}
```

La referencia fuerte al `delegate` la mantiene el closure de `suspendCancellableCoroutine` hasta que llega el callback.

### LocationPermission iOS — delegate reactivo en Composable

```kotlin
val manager = remember { CLLocationManager() }
var authStatus by remember { mutableStateOf(manager.authorizationStatus) }
val delegate = remember {
    object : NSObject(), CLLocationManagerDelegateProtocol {
        override fun locationManagerDidChangeAuthorization(manager: CLLocationManager) {
            authStatus = manager.authorizationStatus   // dispara recomposición
        }
    }
}
DisposableEffect(Unit) {
    manager.delegate = delegate
    onDispose { manager.delegate = null }
}
```

El delegate actualiza el `State<CLAuthorizationStatus>` directamente desde el callback de main thread, lo que dispara recomposición automática de `isGranted`/`isDenied`.

## Archivos modificados

| Archivo | Cambio |
|---------|--------|
| `core/data/src/iosMain/.../location/LocationTrackerIos.kt` | Implementación real con CLLocationManager |
| `core/data/src/iosMain/.../di/IosDataModule.kt` | Inyecta `ioDispatcher` en `LocationTrackerIos` |
| `core/ui/src/commonMain/.../LocalOpenLocationSettings.kt` | **Nuevo** — `staticCompositionLocalOf<() -> Unit>` |
| `feature/station-map/src/iosMain/.../platform/LocationPermission.kt` | Implementación real con CLAuthorizationStatus |
| `iosApp/iosApp/Info.plist` | Añade `NSLocationWhenInUseUsageDescription` |
| `composeApp/src/commonMain/.../App.kt` | Provee `LocalOpenLocationSettings` |
| `composeApp/src/commonMain/.../ui/GasGuruApp.kt` | Elimina param; lee local en AlertDialog |
| `composeApp/src/commonMain/.../navigation/root/GasGuruNavHost.kt` | Elimina param |
| `composeApp/src/commonMain/.../navigation/navigationbar/NavigationBarNavigation.kt` | Elimina param |
| `composeApp/src/commonMain/.../ui/NavigationBarScreen.kt` | Elimina param |
| `feature/favorite-list-station/.../navigation/FavoriteStationListGraph.kt` | Elimina param |
| `feature/favorite-list-station/.../navigation/FavoriteStationListNavigation.kt` | Elimina param |
| `feature/favorite-list-station/.../ui/FavoriteStationListScreen.kt` | ScreenRoute lee local; interna conserva param |

## Verificación manual

En simulador iPhone 15 (iOS 17+):

1. **Primer arranque** — abrir app → onboarding → mapa. Se muestra UI de "permiso necesario" (no pantalla vacía).
2. **Permiso concedido** — tocar "Permitir" → prompt nativo iOS aparece. Aceptar → mapa centra sobre Custom Location del simulador. Estaciones cercanas aparecen.
3. **Permiso denegado** — `Device → Erase All Content` → repetir → rechazar prompt → mapa muestra UI "denied" con botón "Abrir Ajustes" → tap abre Settings de la app iOS.
4. **Reconcecer en Settings** — conceder desde Settings → volver a la app → mapa se actualiza sin reiniciar (delegate `locationManagerDidChangeAuthorization` dispara recomposición).
5. **Servicios globales desactivados** — `Settings → Privacy → Location Services → Off` → `isLocationEnabled` emite `false` → snackbar/UI correspondiente.

## Lecciones aprendidas

- **`CLLocationManager.delegate` es `weak`**: el delegate solo vive mientras alguien lo retiene fuerte. En `callbackFlow`, el closure del bloque retiene las variables locales, así que el delegate sobrevive mientras haya collectors. En `suspendCancellableCoroutine`, el closure de la lambda retiene el delegate hasta que el callback arrive.
- **`requestLocation()` vs `startUpdatingLocation()`**: `requestLocation()` entrega una única ubicación de alta precisión y para solo; `startUpdatingLocation()` emite ubicaciones continuamente hasta `stopUpdatingLocation()`. Usar el primero en `getCurrentLocation()` (one-shot) y el segundo en `getCurrentLocationFlow` (continuo).
- **`withContext(Dispatchers.Main)` en `getCurrentLocation()`**: `CLLocationManager` debe crearse y usarse en main thread. En iOS, `Dispatchers.Main` es el main run loop, no bloqueable — `suspendCancellableCoroutine` suspende la coroutine sin bloquear el thread.
- **`authorizationStatus` de instancia (iOS 14+)**: en iOS 14+, `authorizationStatus` es una propiedad de instancia de `CLLocationManager`; en <14 era un método estático. El delegate callback `locationManagerDidChangeAuthorization` (iOS 14+) reemplaza al viejo `locationManager:didChangeAuthorizationStatus:`. Para targets modernos, usar siempre el de instancia.
- **`@OptIn(ExperimentalForeignApi::class)`**: `CLLocationCoordinate2D` es un struct C expuesto vía cinterop. Para acceder a `latitude`/`longitude` se usa `coordinate.useContents { ... }` que requiere la optin.

## Próximo: Phase 9C — Mapa interactivo (PlatformMapView)

Sustituir el stub `Box` con color de fondo de `feature/station-map/src/iosMain/.../platform/PlatformMapView.kt` por un mapa interactivo real. Decisión pendiente: MapKit nativo vs Google Maps SDK iOS. Ver sección Phase 9C en `docs/KMP_MIGRATION.md`.
