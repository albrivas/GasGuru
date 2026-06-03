# KMP Phase 9C — iOS Mapa Interactivo con MapKit (MVP)

## Objetivo

Sustituir el stub bloqueante #5 de iOS que impedía ver el mapa en `StationMapScreen`:

| Stub | Módulo | Antes | Después |
|------|--------|-------|---------|
| `PlatformMapView` iosMain | `:feature:station-map` | `Box` con color de fondo | `MKMapView` real vía `UIKitView` + cinterop MapKit |

Tras esta fase, la pantalla principal de la app iOS muestra un mapa funcional con estaciones tap-eables, polyline de ruta, punto azul de ubicación y overlay de carga.

## Decisión técnica: MapKit nativo vs Google Maps SDK iOS

| Criterio | MapKit nativo | Google Maps SDK iOS (pod `GoogleMaps`) |
|----------|--------------|----------------------------------------|
| **Costo** | Gratis, framework del sistema | Gratis hasta quota; billing activado |
| **Integración** | Cinterop automático `platform.MapKit.*` — cero pods | Pod `GoogleMaps` + `pod install` + `cocoapods {}` en build.gradle |
| **Tamaño binario** | +0 MB (framework Apple preinstalado) | +~30 MB |
| **Paridad visual con Android** | Distinta (estética Apple Maps) | Idéntica |
| **Markers personalizados** | `MKMarkerAnnotationView` default; custom requiere `UIImage` via Core Graphics | `GMSMarker.iconView` admite cualquier UIView |
| **Clustering** | `MKClusterAnnotation` + `clusteringIdentifier` (manual) | `GMUClusterManager` oficial |
| **Mantenimiento** | Apple mantiene; zero deps externas | Google mantiene; pod puede romper en major iOS updates |
| **API key** | No necesaria | Necesaria (ya configurada para Android, pero habilitarla para iOS Maps SDK requiere restricciones adicionales) |

**Decisión: MapKit nativo.** Cero dependencias externas, sin pods, sin API key específica para iOS Maps. La paridad visual exacta se prioriza en 9C.2 (clustering + markers con precio); para el MVP, el mapa funcional es lo bloqueante.

## Alcance MVP (esta fase)

- Render del mapa con pan/zoom nativo
- Marcadores coloreados por `PriceCategory` (`MKMarkerAnnotationView` con `markerTintColor`)
- Tap en marcador → `onStationClick(id)` → navega al detalle
- Centrado en `mapBounds` (bounds de las estaciones cargadas)
- Centrado en `userLocationToCenter` (botón "mi ubicación")
- Overlay de polyline para la ruta (RoutePlanner → StationMapScreen)
- Punto azul del usuario (`showsUserLocation = isLocationPermissionGranted`)
- Loading overlay translúcido con `GasGuruLoading`
- Dark mode automático (MapKit sigue la apariencia del sistema sin configuración extra)

**Fuera de alcance en este MVP** → Phase 9C.2:
- Clustering de marcadores (`MKClusterAnnotation`)
- Markers con precio y logo de marca (render offscreen de `StationMarker` composable o `UIImage` via Core Graphics)

## Patrones de implementación

### `UIKitView<MKMapView>` con delegate NSObject

```kotlin
val mapDelegate = remember {
    object : NSObject(), MKMapViewDelegateProtocol {
        var lastRoute: RouteUiModel? = null
        var currentPolyline: MKPolyline? = null

        override fun mapView(mapView: MKMapView, didSelectAnnotationView: MKAnnotationView) {
            val annotation = didSelectAnnotationView.annotation
            if (annotation is StationAnnotation) currentOnStationClick(annotation.stationId)
        }

        override fun mapView(mapView: MKMapView, rendererForOverlay: MKOverlayProtocol): MKOverlayRenderer {
            if (rendererForOverlay is MKPolyline) {
                return MKPolylineRenderer(overlay = rendererForOverlay).apply {
                    strokeColor = UIColor(red = 0.0, green = 0.48, blue = 1.0, alpha = 1.0)
                    lineWidth = 6.0
                }
            }
            return MKOverlayRenderer(overlay = rendererForOverlay)
        }

        override fun mapView(mapView: MKMapView, viewForAnnotation: MKAnnotationProtocol): MKAnnotationView? {
            if (viewForAnnotation !is StationAnnotation) return null
            val view = (mapView.dequeueReusableAnnotationViewWithIdentifier(STATION_MARKER_REUSE_ID)
                as? MKMarkerAnnotationView)
                ?: MKMarkerAnnotationView(annotation = viewForAnnotation, reuseIdentifier = STATION_MARKER_REUSE_ID)
            view.annotation = viewForAnnotation
            view.canShowCallout = false
            view.markerTintColor = viewForAnnotation.priceCategory.toMarkerColor()
            return view
        }
    }
}
```

El delegate se guarda en `remember { }` para que sobreviva mientras vive la composición (el `delegate` de `MKMapView` es `weak`; si no lo retiene nadie, se libera y los callbacks dejan de llegar). Mismo patrón que `CLLocationManagerDelegateProtocol` en Phase 9B.

### `StationAnnotation` — asociar `idServiceStation` al marker

```kotlin
private class StationAnnotation(
    val stationId: Int,
    val priceCategory: PriceCategory,
    private val storedCoordinate: CValue<CLLocationCoordinate2D>,
    private val stationTitle: String?,
) : NSObject(), MKAnnotationProtocol {
    override fun coordinate(): CValue<CLLocationCoordinate2D> = storedCoordinate
    override fun title(): String? = stationTitle
    override fun subtitle(): String? = null
}
```

En `didSelectAnnotationView`, el delegate hace `annotation as? StationAnnotation` para obtener `stationId` y disparar `onStationClick`. El campo `coordinate()` almacena el `CValue<CLLocationCoordinate2D>` calculado en construcción — no se recalcula en cada `coordinate()`.

### Diff idempotente de annotations

El `update` block de `UIKitView` se ejecuta en cada recomposición. Para evitar re-añadir las mismas 200 stations en cada frame:

```kotlin
val newStationIds = stations.map { it.fuelStation.idServiceStation }.toSet()
val existingAnnotations = mv.annotations.filterIsInstance<StationAnnotation>()
val existingIds = existingAnnotations.map { it.stationId }.toSet()

existingAnnotations.filter { it.stationId !in newStationIds }.forEach { mv.removeAnnotation(it) }
stations.filter { it.fuelStation.idServiceStation !in existingIds }.forEach { station ->
    mv.addAnnotation(StationAnnotation(...))
}
```

### Gestión de overlay de ruta (stored reference)

En lugar de consultar `mv.overlays` (extension en `platform.MapKit` que requiere import explícito y no se usa fuera de aquí), se guarda la referencia actual en el delegate:

```kotlin
if (route !== mapDelegate.lastRoute) {
    mapDelegate.currentPolyline?.let { mv.removeOverlay(overlay = it) }
    mapDelegate.currentPolyline = null
    val routePoints = route?.route
    if (routePoints != null && routePoints.isNotEmpty()) {
        val newPolyline = createMKPolyline(routePoints)
        mv.addOverlay(overlay = newPolyline)
        mapDelegate.currentPolyline = newPolyline
    }
    mapDelegate.lastRoute = route
}
```

La referencia `===` (identidad de objeto) detecta si la ruta cambió, evitando reconstruir la polyline en cada recomposición.

### Polyline con `allocArray` + initializer

`CLLocationCoordinate2D` es un struct C. `allocArrayOf` solo funciona para primitivos y `CPointer`; la aritmética de punteros (`+`) solo está definida para `CVar` primitivos. La forma correcta en K/N para arrays de structs es `allocArray<T>(n) { index -> ... }` con el initializer que recibe `this = T` (el elemento mutable del array):

```kotlin
@OptIn(ExperimentalForeignApi::class)
internal fun createMKPolyline(points: List<LatLng>): MKPolyline = memScoped {
    val coordsBuffer = allocArray<CLLocationCoordinate2D>(points.size) { index ->
        latitude = points[index].latitude
        longitude = points[index].longitude
    }
    MKPolyline.polylineWithCoordinates(coordsBuffer, points.size.toULong())
}
```

### Centrado reactivo fuera del `update` block

El centrado usa `LaunchedEffect` fuera del `update` para que no compita con gestos del usuario:

```kotlin
LaunchedEffect(mapBounds, shouldCenterMap) {
    if (mapBounds != null && shouldCenterMap) {
        mapView.setRegion(region = mapBounds.toMKCoordinateRegion(), animated = true)
        currentOnMapCentered()
    }
}

LaunchedEffect(userLocationToCenter) {
    if (userLocationToCenter != null) {
        mapView.setRegion(region = userLocationToCenter.toMKCoordinateRegionCentered(), animated = true)
        currentOnUserLocationCentered()
    }
}
```

`rememberUpdatedState` envuelve los callbacks (`onMapCentered`, `onUserLocationCentered`, `onStationClick`) para que el delegate siempre llame a la versión más reciente sin re-crear el objeto en cada recomposición.

### Extension functions de `platform.MapKit` — requieren import explícito

Los métodos de categoría ObjC (`MKMapView (MKOverlayAdditions)`, etc.) se generan en K/N como extension functions en el package `platform.MapKit`, no como member functions de la clase. Necesitan import explícito aunque `MKMapView` ya esté importado:

```kotlin
import platform.MapKit.addOverlay    // fun MKMapView.addOverlay(overlay: MKOverlayProtocol)
import platform.MapKit.removeOverlay // fun MKMapView.removeOverlay(overlay: MKOverlayProtocol)
```

`overlays` (val extension property) no se usa porque guardamos la referencia en el delegate.

## Archivos modificados

| Archivo | Cambio |
|---------|--------|
| `feature/station-map/src/iosMain/kotlin/.../platform/PlatformMapView.kt` | Implementación completa con `UIKitView<MKMapView>`, `MKMapViewDelegateProtocol`, `StationAnnotation`, diff de annotations, gestión de polyline |
| `feature/station-map/src/iosMain/kotlin/.../platform/Mappers.kt` | Nuevo: `LatLng.toCLLocationCoordinate2D()`, `GeoBounds.toMKCoordinateRegion()`, `LatLng.toMKCoordinateRegionCentered()`, `createMKPolyline()` |

No se tocan:
- `iosApp/iosApp/Info.plist` — MapKit no requiere keys nuevas
- `iosApp/Podfile` — MapKit es framework del sistema
- `feature/station-map/build.gradle.kts` — `platform.MapKit.*` es cinterop automático K/N
- Código Android del módulo

## Verificación

### Compilación

```bash
./gradlew :feature:station-map:compileKotlinIosSimulatorArm64   # ✅
./gradlew :feature:station-map:compileDebugKotlinAndroid         # ✅
./gradlew :feature:station-map:testDebugUnitTest                 # ✅
./gradlew :composeApp:compileKotlinIosSimulatorArm64             # ✅
./gradlew :app:assembleProdDebug                                 # ✅
./gradlew codeCheck                                              # ✅
```

### Validación manual en simulador iPhone 15+ (iOS 17+)

1. Permiso concedido → mapa carga, dot azul aparece, estaciones se muestran como pins coloreados.
2. Tap en pin → navega al detalle de la estación correcta.
3. Pan/zoom funcionan sin interferencia de Compose.
4. Iniciar ruta desde RoutePlanner → volver al mapa → polyline visible.
5. Pulsar "mi ubicación" → mapa anima a `userLocationToCenter`, dispara `onUserLocationCentered`.
6. Modo oscuro del sistema → MapKit cambia a tiles oscuros automáticamente.
7. Loading → overlay translúcido visible durante carga inicial.
8. Cambiar de pantalla y volver → mapa se rehace sin fuga de memoria.

## Lecciones aprendidas

- **`MKMapView.delegate` es `weak`**: el delegate solo vive mientras alguien lo retiene con referencia fuerte. `remember { }` del composable lo mantiene vivo mientras vive la composición. Sin `remember`, el delegate se libera en la siguiente recomposición y los callbacks dejan de llegar.
- **Extension functions ObjC en K/N**: los métodos de categoría ObjC (`@interface MKMapView (Category)`) se generan como extension functions en el package `platform.XXX`, no como miembros de la clase. Requieren `import platform.MapKit.addOverlay` etc. aunque `MKMapView` ya esté importado.
- **`allocArray<T>(n) { index -> ... }` para structs**: `allocArrayOf` solo existe para primitivos y `CPointer`. La aritmética de punteros `CPointer + Int` solo está definida para tipos `CVar` primitivos. Para arrays de C structs, usar `allocArray` con el initializer lambda donde `this` es el elemento mutable del array.
- **Idempotencia del `update` block**: `UIKitView.update` se ejecuta en cada recomposición. Comparar con diff por ID antes de añadir/quitar annotations evita resetear el estado del mapa (posición, zoom) en cada recomposición.
- **Dark mode automático**: `MKMapView` sigue la apariencia del sistema iOS por defecto cuando no se asigna `overrideUserInterfaceStyle`. No se necesita código extra. Los `UIUserInterfaceStyleDark/Light` no están disponibles como constantes directas en K/N 2.2.x — no importar.
- **Polimorfismo `MKPolyline : MKOverlayProtocol`**: `MKPolyline` conforma `MKOverlay` en ObjC → implementa `MKOverlayProtocol` en K/N. Se puede pasar directamente a `removeOverlay(overlay:)` sin cast.

### Lecciones post-MVP (fixes durante validación manual)

- **`CLLocationManager.startUpdatingLocation()` falla silenciosamente justo tras grant de permiso**: inmediatamente después de que el usuario concede el permiso por primera vez, un nuevo `CLLocationManager.startUpdatingLocation()` puede no disparar `didUpdateLocations` (el sistema necesita un momento de inicialización). Solución: usar `requestLocation()` (one-shot) en `getCurrentLocation()` vía `suspendCancellableCoroutine`. `requestLocation()` está diseñado para este caso y es más fiable. El `startUpdatingLocation()` se mantiene solo para `getCurrentLocationFlow` (uso continuo).

- **`loading = true` por defecto + `getCurrentLocation()` colgado = loading infinito**: `StationMapUiState(loading = true)` es el valor inicial. Si `getCurrentLocationUseCase()` nunca retorna (e.g. location no llega), el loading nunca se quita. Fix: añadir `?: _state.update { it.copy(loading = false) }` tras el `?.let { }` en `getStationByCurrentLocation()`.

- **`NSData.dataWithBytes` no existe en Kotlin/Native**: el factory method `+[NSData dataWithBytes:length:]` no mapea como `NSData.dataWithBytes(...)`. En su lugar usar `NSData.create(bytes = pinned.addressOf(0), length = n)` (mapea al init `initWithBytes:length:`). Requiere `@OptIn(BetaInteropApi::class)`.

- **`UIImage` capturada de GraphicsLayer aparece 3× demasiado grande**: `GraphicsLayer.toImageBitmap()` captura a la densidad real del dispositivo (2x/3x píxeles). `UIImage.imageWithData(data)` asume escala 1x por defecto → el marker ocupa 3× su tamaño lógico en el mapa. Fix: `UIImage(data = nsData, scale = UIScreen.mainScreen.scale)` para que UIKit interprete la imagen a la densidad correcta.

- **Renderizar composable a UIImage para MapKit**: `MKAnnotationView` solo acepta `UIImage`, no composables. Patrón correcto en CMP 1.10+: `rememberGraphicsLayer()` + `Modifier.drawWithContent { graphicsLayer.record { this@drawWithContent.drawContent() } }` en un `Box` oculto (`size(0.dp) + wrapContentSize(unbounded=true)`). Capturar con `graphicsLayer.toImageBitmap()` en un `LaunchedEffect`. Actualizar la annotation view ya mostrada en el `UIKitView.update` block leyendo el `SnapshotStateMap<Int, UIImage>` (triggerea re-ejecución del `update` automáticamente vía Compose snapshot).

## Próximo: Phase 9C.2 — Map Polish

Clustering con `MKClusterAnnotation` + markers con precio y logo de marca rendereados via Core Graphics en un `UIImage`. Ver sección Phase 9C.2 en `docs/KMP_MIGRATION.md`.
