# Analytics — GasGuru

## Overview

GasGuru usa **Mixpanel** para analíticas de producto. El sistema sigue el patrón NowInAndroid:
- `AnalyticsHelper` interface inyectable via **Koin** en ViewModels y clases non-composable
- `LocalAnalyticsHelper` CompositionLocal para acceso desde Composables
- `NoOpAnalyticsHelper` para tests y previews (sin side effects)

---

## Estructura del módulo `core:analytics`

```
core/analytics/
├── build.gradle.kts
└── src/
    ├── main/java/com/gasguru/core/analytics/
    │   ├── AnalyticsEvent.kt           — data class + constantes de tipos y parámetros
    │   ├── AnalyticsHelper.kt          — interface: fun logEvent(event: AnalyticsEvent)
    │   ├── NoOpAnalyticsHelper.kt      — implementación vacía para tests y previews
    │   ├── LogcatAnalyticsHelper.kt    — implementación de debug (Log.d por evento)
    │   ├── MixpanelAnalyticsHelper.kt  — implementación de producción (wraps MixpanelAPI)
    │   ├── LocalAnalyticsHelper.kt     — staticCompositionLocalOf<AnalyticsHelper>
    │   └── di/
    │       └── AnalyticsModule.kt      — Koin single<AnalyticsHelper> binding (debug/release)
    └── test/java/com/gasguru/core/analytics/
        ├── LogcatAnalyticsHelperTest.kt
        └── MixpanelAnalyticsHelperTest.kt
```

### Clases principales

#### `AnalyticsHelper` — Interface

```kotlin
interface AnalyticsHelper {
    fun logEvent(event: AnalyticsEvent)
}
```

#### `AnalyticsEvent` — Modelo de evento

```kotlin
data class AnalyticsEvent(
    val type: String,
    val extras: List<Param> = emptyList(),
) {
    data class Param(val key: String, val value: String)

    object Types {
        // constantes de nombre de evento (ej. "onboarding_started")
    }

    object ParamKeys {
        // constantes de clave de parámetro (ej. "fuel_type")
    }
}
```

#### `MixpanelAnalyticsHelper` — Producción

Obtiene la instancia ya inicializada de Mixpanel (sin re-inicializar) y envía cada
evento con sus parámetros como propiedades JSON:

```kotlin
class MixpanelAnalyticsHelper(private val context: Context) : AnalyticsHelper {
    private val mixpanel get() = MixpanelAPI.getInstance(context, null, true)

    override fun logEvent(event: AnalyticsEvent) {
        val properties = JSONObject()
        event.extras.forEach { param -> properties.put(param.key, param.value) }
        mixpanel.track(event.type, properties)
    }
}
```

#### `LocalAnalyticsHelper` — CompositionLocal

```kotlin
val LocalAnalyticsHelper = staticCompositionLocalOf<AnalyticsHelper> { NoOpAnalyticsHelper() }
```

Provisto en `MainActivity` mediante `CompositionLocalProvider(LocalAnalyticsHelper provides analyticsHelper)`.

#### `LogcatAnalyticsHelper` — Debug

Implementación activa únicamente en builds de debug (`BuildConfig.DEBUG = true`).
Escribe cada evento en Logcat con tag `Analytics`, el nombre del evento y sus
parámetros como pares `key=value`:

```
D/Analytics: ▶ vehicle_created | vehicle_type=CAR, fuel_type=GASOLINE_95
D/Analytics: ▶ went_offline | —
```

#### `AnalyticsModule` — Koin

Selecciona la implementación en función del build type:

```kotlin
val analyticsModule = module {
    single<AnalyticsHelper> {
        if (BuildConfig.DEBUG) LogcatAnalyticsHelper()
        else MixpanelAnalyticsHelper(context = androidContext())
    }
}
```

### Flujo de dependencias

```
app          → core:analytics  (MainActivity, StationSyncWorker)
feature:*    → core:analytics  (todos los ViewModels)
core:data    → core:analytics  (SyncManager, PriceAlertRepositoryImpl)
core:components → core:analytics (GasGuruSearchBarViewModel)
```

---

## Catálogo de eventos por funcionalidad

---

### 1. Onboarding

**Dónde se instrumenta:** `NewOnboardingViewModel`, `OnboardingViewModel`, `CapacityTankViewModel`

El flujo de onboarding solo se ejecuta la primera vez que el usuario abre la app
(`isOnboardingSuccess = false`). Medir este flujo permite entender la tasa de
finalización y en qué paso se abandona.

| Evento | Tipo | Parámetros | Clase |
|--------|------|-----------|-------|
| Onboarding iniciado | `ONBOARDING_STARTED` | — | `NewOnboardingViewModel.init` |
| Página visualizada | `ONBOARDING_PAGE_VIEWED` | `page_number: Int` | `NewOnboardingViewModel` → evento `PageChanged` |
| Onboarding saltado | `ONBOARDING_SKIPPED` | — | `NewOnboardingViewModel` → evento `Skip` |
| Combustible seleccionado | `ONBOARDING_FUEL_SELECTED` | `fuel_type: String` | `OnboardingViewModel.saveSelectedFuel()` |
| Capacidad de depósito configurada | `ONBOARDING_TANK_CAPACITY_SET` | `capacity_litres: Int` | `CapacityTankViewModel` → evento `Continue` |
| Onboarding completado | `ONBOARDING_COMPLETED` | — | `CapacityTankViewModel` → evento `Continue` |

---

### 2. Vehículos

**Dónde se instrumenta:** `AddVehicleViewModel`, `ProfileViewModel`

Los vehículos son el núcleo de la personalización. Saber cuántos se crean, de qué
tipo y con qué combustible ayuda a entender el perfil de los usuarios.

| Evento | Tipo | Parámetros | Clase |
|--------|------|-----------|-------|
| Vehículo creado | `VEHICLE_CREATED` | `vehicle_type`, `fuel_type`, `capacity_litres`, `is_principal` | `AddVehicleViewModel` → evento `SaveVehicle` (modo creación) |
| Vehículo editado | `VEHICLE_EDITED` | `vehicle_type`, `fuel_type` | `AddVehicleViewModel` → evento `SaveVehicle` (modo edición) |
| Vehículo eliminado | `VEHICLE_DELETED` | `was_principal: Boolean`, `vehicles_remaining: Int` | `ProfileViewModel` → evento `DeleteVehicle` |

---

### 3. Mapa de gasolineras

**Dónde se instrumenta:** `StationMapViewModel`

La pantalla principal de la app. Medir las interacciones con el mapa permite
entender cómo navegan los usuarios y qué filtros usan con más frecuencia.

| Evento | Tipo | Parámetros | Clase |
|--------|------|-----------|-------|
| Gasolineras cargadas | `MAP_STATIONS_LOADED` | `station_count: Int` | `StationMapViewModel` — collect éxito |
| Gasolinera seleccionada | `STATION_SELECTED` | `station_id: Int` | `StationMapViewModel` → evento `SelectStation` |
| Filtro de marca cambiado | `FILTER_BRAND_CHANGED` | `brand_count: Int` | `StationMapViewModel` → evento `UpdateBrandFilter` |
| Filtro de distancia cambiado | `FILTER_NEARBY_CHANGED` | `nearby_km: String` | `StationMapViewModel` → evento `UpdateNearbyFilter` |
| Filtro de horario cambiado | `FILTER_SCHEDULE_CHANGED` | `schedule: String` | `StationMapViewModel` → evento `UpdateScheduleFilter` |
| Tab cambiado (Precio/Distancia) | `MAP_TAB_CHANGED` | `tab: String` | `StationMapViewModel` → evento `ChangeTab` |
| Ruta iniciada | `ROUTE_STARTED` | — | `StationMapViewModel` → evento `StartRoute` |
| Ruta cancelada | `ROUTE_CANCELLED` | — | `StationMapViewModel` → evento `CancelRoute` |

---

### 4. Detalle de gasolinera

**Dónde se instrumenta:** `DetailStationViewModel`

Pantalla de detalle de una gasolinera individual. Los eventos de favoritos y alertas
de precio son conversiones clave de engagement.

| Evento | Tipo | Parámetros | Clase |
|--------|------|-----------|-------|
| Gasolinera añadida a favoritos | `STATION_FAVORITED` | `station_id: Int` | `DetailStationViewModel` → `ToggleFavorite(isFavorite = true)` |
| Gasolinera eliminada de favoritos | `STATION_UNFAVORITED` | `station_id: Int` | `DetailStationViewModel` → `ToggleFavorite(isFavorite = false)` |
| Alerta de precio activada | `PRICE_ALERT_ENABLED` | `station_id: Int` | `DetailStationViewModel` → `TogglePriceAlert(isEnabled = true)` |
| Alerta de precio desactivada | `PRICE_ALERT_DISABLED` | `station_id: Int` | `DetailStationViewModel` → `TogglePriceAlert(isEnabled = false)` |

---

### 5. Búsqueda

**Dónde se instrumenta:** `GasGuruSearchBarViewModel`

El buscador es el punto de entrada para encontrar gasolineras por ubicación. Medir
qué se busca y si se usa el historial ayuda a mejorar la UX.

| Evento | Tipo | Parámetros | Clase |
|--------|------|-----------|-------|
| Lugar seleccionado de resultados | `SEARCH_PLACE_SELECTED` | — | `GasGuruSearchBarViewModel` → evento `InsertRecentSearch` |
| Historial de búsqueda limpiado | `SEARCH_HISTORY_CLEARED` | — | `GasGuruSearchBarViewModel` → evento `ClearRecentSearches` |

---

### 6. Planificador de ruta

**Dónde se instrumenta:** `RoutePlannerViewModel`

Funcionalidad para planificar rutas entre dos puntos con sugerencia de gasolineras
en el camino. Medir el uso ayuda a entender si los usuarios aprovechan esta feature.

| Evento | Tipo | Parámetros | Clase |
|--------|------|-----------|-------|
| Destino establecido | `ROUTE_PLANNER_DESTINATION_SET` | `is_current_location: Boolean` | `RoutePlannerViewModel` → `SelectPlace` / `SelectCurrentLocation` |
| Destinos intercambiados | `ROUTE_PLANNER_DESTINATIONS_SWAPPED` | — | `RoutePlannerViewModel` → evento `ChangeDestinations` |
| Búsqueda reciente usada | `RECENT_SEARCH_USED` | — | `RoutePlannerViewModel` → evento `SelectRecentPlace` |

---

### 7. Perfil / Tema

**Dónde se instrumenta:** `ProfileViewModel`

| Evento | Tipo | Parámetros | Clase |
|--------|------|-----------|-------|
| Tema cambiado | `THEME_CHANGED` | `theme_mode: String` (LIGHT / DARK / SYSTEM) | `ProfileViewModel` → evento `Theme` |

---

### 8. Favoritos (lista)

**Dónde se instrumenta:** `FavoriteListStationViewModel`

| Evento | Tipo | Parámetros | Clase |
|--------|------|-----------|-------|
| Tab cambiado (Precio/Distancia) | `FAVORITES_TAB_CHANGED` | `tab: Int` (0 = Precio, 1 = Distancia) | `FavoriteListStationViewModel` → evento `ChangeTab` |
| Gasolinera eliminada desde lista | `STATION_UNFAVORITED_FROM_LIST` | `station_id: Int` | `FavoriteListStationViewModel` → evento `RemoveFavoriteStation` |

---

### 9. Red / Estado offline

**Dónde se instrumenta:** `SyncManager`

Permite entender con qué frecuencia los usuarios usan la app sin conexión y cuántas
operaciones quedan pendientes de sincronizar.

| Evento | Tipo | Parámetros | Clase |
|--------|------|-----------|-------|
| Dispositivo sin conexión | `WENT_OFFLINE` | — | `SyncManager` — flow `networkMonitor` → `isOnline = false` |
| Dispositivo con conexión | `CAME_ONLINE` | — | `SyncManager` — flow `networkMonitor` → `isOnline = true` |

---

### 10. Sincronización de alertas de precio

**Dónde se instrumenta:** `PriceAlertRepositoryImpl`

Las alertas de precio se sincronizan con Supabase cuando el dispositivo recupera
conexión. Medir éxitos y fallos ayuda a detectar problemas de backend.

| Evento | Tipo | Parámetros | Clase |
|--------|------|-----------|-------|
| Sincronización completada | `ALERTS_SYNC_COMPLETED` | `synced_count: Int` | `PriceAlertRepositoryImpl.sync()` — éxito |
| Sincronización fallida | `ALERTS_SYNC_FAILED` | — | `PriceAlertRepositoryImpl.sync()` — catch |

---

### 11. Worker de sincronización de gasolineras

**Dónde se instrumenta:** `StationSyncWorker`

Worker de WorkManager que sincroniza todas las gasolineras en segundo plano (ejecución
periódica). Medir el ciclo de vida del worker permite detectar fallos recurrentes.

| Evento | Tipo | Parámetros | Clase |
|--------|------|-----------|-------|
| Worker iniciado | `STATION_SYNC_WORKER_STARTED` | — | `StationSyncWorker.doWork()` — inicio |
| Worker completado | `STATION_SYNC_WORKER_COMPLETED` | — | `StationSyncWorker.doWork()` — `Result.success()` |
| Worker reintentando | `STATION_SYNC_WORKER_RETRIED` | — | `StationSyncWorker.doWork()` — `Result.retry()` |

---

## Resumen de ParamKeys

| Clave | Tipo | Descripción |
|-------|------|-------------|
| `page_number` | `Int` | Número de página en onboarding |
| `fuel_type` | `String` | Tipo de combustible (ej. `GASOLINE_95`) |
| `capacity_litres` | `Int` | Capacidad del depósito en litros |
| `vehicle_type` | `String` | Tipo de vehículo (ej. `CAR`, `MOTORCYCLE`) |
| `is_principal` | `Boolean` | Si el vehículo es el principal |
| `was_principal` | `Boolean` | Si el vehículo eliminado era el principal |
| `vehicles_remaining` | `Int` | Vehículos restantes tras eliminar |
| `station_count` | `Int` | Número de gasolineras cargadas |
| `station_id` | `Int` | ID de la gasolinera |
| `brand_count` | `Int` | Número de marcas seleccionadas en filtro |
| `nearby_km` | `String` | Distancia en km del filtro de proximidad |
| `schedule` | `String` | Tipo de horario del filtro |
| `tab` | `String` / `Int` | Tab seleccionado |
| `is_current_location` | `Boolean` | Si el destino es la ubicación actual |
| `theme_mode` | `String` | Modo de tema (LIGHT / DARK / SYSTEM) |
| `synced_count` | `Int` | Número de alertas sincronizadas |

---

## Cómo usar

### En un ViewModel (Koin)

```kotlin
class MyViewModel(
    private val analyticsHelper: AnalyticsHelper,
) : ViewModel() {

    fun onUserAction() {
        analyticsHelper.logEvent(
            event = AnalyticsEvent(
                type = AnalyticsEvent.Types.VEHICLE_CREATED,
                extras = listOf(
                    AnalyticsEvent.Param(
                        key = AnalyticsEvent.ParamKeys.FUEL_TYPE,
                        value = "GASOLINE_95",
                    ),
                ),
            ),
        )
    }
}
```

Registrar en el módulo Koin:

```kotlin
viewModel { MyViewModel(analyticsHelper = get()) }
```

### En un Composable (CompositionLocal)

```kotlin
@Composable
fun MyScreen() {
    val analyticsHelper = LocalAnalyticsHelper.current

    Button(onClick = {
        analyticsHelper.logEvent(
            AnalyticsEvent(type = AnalyticsEvent.Types.STATION_SELECTED)
        )
    }) {
        Text("Seleccionar")
    }
}
```

`LocalAnalyticsHelper` se provee en la raíz de la composición desde `MainActivity`:

```kotlin
CompositionLocalProvider(LocalAnalyticsHelper provides analyticsHelper) {
    // contenido de la app
}
```

### En tests

Usar `NoOpAnalyticsHelper()` para no generar side effects:

```kotlin
val viewModel = MyViewModel(analyticsHelper = NoOpAnalyticsHelper())
```

---

## Añadir un nuevo evento

1. Añadir la constante de tipo en `AnalyticsEvent.Types`.
2. Añadir las claves de parámetro necesarias en `AnalyticsEvent.ParamKeys`.
3. Llamar a `analyticsHelper.logEvent(...)` en el ViewModel o clase correspondiente.
4. Actualizar este documento.

---

## Inicialización de Mixpanel

Mixpanel se inicializa en `GasGuruApplication.mixpanelSetUp()` con el token del proyecto
desde `BuildConfig.mixpanelProjectToken`. `MixpanelAnalyticsHelper` recupera el singleton
ya inicializado via `MixpanelAPI.getInstance(context, null, true)` — no se produce
una segunda inicialización.
