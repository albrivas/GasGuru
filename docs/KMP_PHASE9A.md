# KMP Phase 9A — Foundation APIs iOS: NetworkMonitor + Geocoder

## Objetivo

Sustituir los dos primeros stubs no-op de `core:data/iosMain` que no requieren permisos del usuario ni pods externos:

| Stub | Antes | Después |
|------|-------|---------|
| `NWPathMonitorNetworkMonitor` | `flowOf(true)` siempre | `NWPathMonitor` vía `callbackFlow` |
| `CLGeocoderAddress` | `flowOf(null)` | `CLGeocoder.reverseGeocodeLocation` |

## Decisiones técnicas

### NetworkMonitor: NWPathMonitor directo (sin wrapper)

**Descartado**: `KMP-NativeCoroutines` u otros wrappers — añaden dependencia innecesaria para algo que el cinterop cubre directamente.

**Elegido**: `platform.Network.nw_path_monitor_*` (cinterop automático, sin `pod()`). El monitor se arranca en una `dispatch_queue` serial dedicada y se cancela en `awaitClose`. La cadena `.distinctUntilChanged().flowOn(ioDispatcher).conflate()` es idéntica a la del `ConnectivityManagerNetworkMonitor` Android.

### Geocoder: CLGeocoder + campos primitivos de CLPlacemark

**Descartado**: `CNPostalAddressFormatter` del framework Contacts — el property `CLPlacemark.postalAddress` devuelve `objcnames/classes/CNPostalAddress?` (tipo forward-declared de CoreLocation) mientras que `CNPostalAddressFormatter.stringFromPostalAddress()` espera `platform.Contacts.CNPostalAddress`. El cast resulta en un "cast can never succeed" en el type checker de Kotlin/Native aunque funcione en runtime; la solución no es idiomática.

**Descartado**: Google Geocoding API — introduce red adicional, coste, latencia y apikey en el geocoder. `CLGeocoder` es el equivalente simétrico de `android.location.Geocoder`.

**Elegido**: `CLGeocoder.reverseGeocodeLocation` + construcción manual de la dirección desde los campos de `CLPlacemark`:
- `thoroughfare` — nombre de la calle
- `subThoroughfare` — número de portal
- `locality` — ciudad
- `postalCode` — código postal

Produce un resultado como `"Calle Gran Vía 12, Madrid, 28013"`, funcionalmente equivalente a `address.getAddressLine(0)` en Android.

### Error handling

Tanto en el monitor de red como en el geocoder, los errores emiten `null` / `false` en lugar de propagar excepciones. `DetailStationViewModel` ya maneja `null` en el geocoder mostrando la dirección vacía.

### Sin tests en iosMain

Ambas implementaciones envuelven APIs nativas de Apple que solo ejecutan en simulador/device (NWPathMonitor no funciona sin networking stack real, CLGeocoder necesita la pila de Location Services). No se crean tests unitarios en `iosMain`; la verificación es manual en simulador.

## Patrón de implementación: callbackFlow + awaitClose sobre APIs Apple

```kotlin
// NWPathMonitor
val monitor = nw_path_monitor_create()
val queue = dispatch_queue_create("com.gasguru.networkmonitor", null)
nw_path_monitor_set_update_handler(monitor) { path ->
    trySend(nw_path_get_status(path) == nw_path_status_satisfied)
}
nw_path_monitor_set_queue(monitor, queue)
nw_path_monitor_start(monitor)
awaitClose { nw_path_monitor_cancel(monitor) }
```

```kotlin
// CLGeocoder
val geocoder = CLGeocoder()
geocoder.reverseGeocodeLocation(CLLocation(lat, lng)) { placemarks, error ->
    trySend(if (error != null || placemarks == null) null else formatAddress(placemarks))
    close()
}
awaitClose { geocoder.cancelGeocode() }
```

**Regla general**: siempre `awaitClose { resource.cancel() }` para evitar que la operación nativa siga corriendo tras cancelar el flow.

## Verificación manual

1. Arrancar la app iOS en simulador iPhone 15 (iOS 17+).
2. **NetworkMonitor**: activar modo avión (`Settings → Airplane Mode`) → snackbar offline aparece. Desactivar → snackbar desaparece.
3. **Geocoder**: abrir detalle de cualquier gasolinera → la línea de dirección muestra texto legible (ej. `"Calle Gran Vía 12, Madrid, 28013"`), no vacía.

## Próximo: Phase 9B — LocationTracker + permisos (CLLocationManager)

La ubicación real del usuario (stub #3) y el flujo de pedir permisos de ubicación (stub #6). Requiere manejo de `Info.plist` y `CLLocationManagerDelegate`. Ver sección Phase 9B en `docs/KMP_MIGRATION.md`.
