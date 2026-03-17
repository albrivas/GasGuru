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
    │   ├── AnalyticsEvent.kt           — data class + Types, Categories y ParamKeys
    │   ├── AnalyticsHelper.kt          — interface: fun logEvent(event: AnalyticsEvent)
    │   ├── NoOpAnalyticsHelper.kt      — implementación vacía para tests y previews
    │   ├── LogcatAnalyticsHelper.kt    — implementación de debug (Log.d por evento)
    │   ├── MixpanelAnalyticsHelper.kt  — implementación de producción (wraps MixpanelAPI)
    │   ├── LocalAnalyticsHelper.kt     — staticCompositionLocalOf<AnalyticsHelper>
    │   └── di/
    │       └── AnalyticsModule.kt      — Koin single<AnalyticsHelper> y single<MixpanelAPI>
    └── test/java/com/gasguru/core/analytics/
        ├── AnalyticsEventCategoriesTest.kt
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

    val category: String
        get() = Categories.fromType(type)

    object Types { /* constantes de nombre de evento */ }
    object Categories { /* categorías y fromType() */ }
    object ParamKeys { /* constantes de clave de parámetro */ }
}
```

La propiedad `category` se deriva automáticamente del `type` mediante `Categories.fromType()`.
No es necesario especificarla al crear el evento.

#### `MixpanelAnalyticsHelper` — Producción

Inyecta la instancia de `MixpanelAPI` via Koin (singleton) y envía cada evento con sus
parámetros como propiedades JSON. La propiedad `category` se añade **automáticamente**
a todas las propiedades sin intervención del caller:

```kotlin
class MixpanelAnalyticsHelper(private val mixpanel: MixpanelAPI) : AnalyticsHelper {
    override fun logEvent(event: AnalyticsEvent) {
        val properties = JSONObject()
        properties.put(ParamKeys.CATEGORY, event.category)   // siempre presente
        event.extras.forEach { param -> properties.put(param.key, param.value) }
        mixpanel.track(event.type, properties)
    }
}
```

#### `AnalyticsModule` — Koin

`MixpanelAPI` se registra como singleton para evitar múltiples inicializaciones.
La selección de implementación se hace en función del build type:

```kotlin
val analyticsModule = module {
    single<MixpanelAPI> {
        MixpanelAPI.getInstance(androidContext(), null, true)
    }
    single<AnalyticsHelper> {
        if (BuildConfig.DEBUG) LogcatAnalyticsHelper()
        else MixpanelAnalyticsHelper(mixpanel = get())
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
Escribe cada evento en Logcat con tag `Analytics`, la categoría, el nombre del evento
y sus parámetros como pares `key=value`:

```
D/Analytics: ▶ [vehicle] vehicle_created | vehicle_type=CAR, fuel_type=GASOLINE_95
D/Analytics: ▶ [network] went_offline | —
D/Analytics: ▶ [widget] widget_station_tapped | station_id=1234
```

### Flujo de dependencias

```
app                → core:analytics  (MainActivity, StationSyncWorker, ProdDataSourceModule)
feature:*          → core:analytics  (todos los ViewModels)
feature:widget     → core:analytics  (WidgetStationClickCallback)
auto:common        → core:analytics  (GasGuruSession, MapAutomotiveScreen, NearbyStationsScreen, FavoriteStationsScreen)
core:data          → core:analytics  (SyncManager, PriceAlertRepositoryImpl)
core:network       → core:analytics  (RemoteDataSourceImp)
core:notifications → core:analytics  (PushNotificationService)
core:components    → core:analytics  (GasGuruSearchBarViewModel)
```

---

## Sistema de categorías

Cada evento se enriquece automáticamente con una propiedad `category` en Mixpanel.
Esto permite filtrar, segmentar y construir funnels por área funcional sin configuración adicional.

| Categoría | Constante | Qué mide |
|-----------|-----------|----------|
| `onboarding` | `Categories.ONBOARDING` | Funnel de activación primera vez |
| `vehicle` | `Categories.VEHICLE` | Configuración y gestión de vehículos |
| `map` | `Categories.MAP` | Interacciones con el mapa principal |
| `station_detail` | `Categories.STATION_DETAIL` | Favoritos, alertas y compartir desde detalle |
| `search` | `Categories.SEARCH` | Búsqueda de ubicaciones |
| `route_planner` | `Categories.ROUTE_PLANNER` | Planificador de rutas |
| `profile` | `Categories.PROFILE` | Personalización y ajustes |
| `favorites` | `Categories.FAVORITES` | Lista de gasolineras favoritas |
| `network` | `Categories.NETWORK` | Conectividad offline/online |
| `sync` | `Categories.SYNC` | Workers y sincronización de alertas en background |
| `api` | `Categories.API` | Llamadas a la API de gasolineras |
| `push` | `Categories.PUSH` | Notificaciones push |
| `widget` | `Categories.WIDGET` | Widget de pantalla de inicio |
| `auto` | `Categories.AUTO` | Android Auto |

La categoría se deriva automáticamente en `Categories.fromType(type)`. Al añadir un nuevo
evento hay que registrarlo en ese `when` para evitar que caiga en `unknown`.

---

## Catálogo de eventos por categoría

---

### onboarding

**Dónde se instrumenta:** `NewOnboardingViewModel`, `OnboardingViewModel`, `CapacityTankViewModel`

| Evento | Tipo | Parámetros |
|--------|------|-----------|
| Onboarding iniciado | `ONBOARDING_STARTED` | — |
| Página visualizada | `ONBOARDING_PAGE_VIEWED` | `page_number: Int` |
| Onboarding saltado | `ONBOARDING_SKIPPED` | — |
| Combustible seleccionado | `ONBOARDING_FUEL_SELECTED` | `fuel_type: String` |
| Capacidad de depósito configurada | `ONBOARDING_TANK_CAPACITY_SET` | `capacity_litres: Int` |
| Onboarding completado | `ONBOARDING_COMPLETED` | — |

---

### vehicle

**Dónde se instrumenta:** `AddVehicleViewModel`, `ProfileViewModel`

| Evento | Tipo | Parámetros |
|--------|------|-----------|
| Vehículo creado | `VEHICLE_CREATED` | `vehicle_type`, `fuel_type`, `capacity_litres`, `is_principal` |
| Vehículo editado | `VEHICLE_EDITED` | `vehicle_type`, `fuel_type` |
| Vehículo eliminado | `VEHICLE_DELETED` | `was_principal: Boolean`, `vehicles_remaining: Int` |

---

### map

**Dónde se instrumenta:** `StationMapViewModel`

| Evento | Tipo | Parámetros |
|--------|------|-----------|
| Gasolineras cargadas | `MAP_STATIONS_LOADED` | `station_count: Int` |
| Gasolinera seleccionada | `STATION_SELECTED` | `station_id: Int` |
| Filtro de marca cambiado | `FILTER_BRAND_CHANGED` | `brand_count: Int`, `brand_names: String` (comma-separated) |
| Filtro de distancia cambiado | `FILTER_NEARBY_CHANGED` | `nearby_km: String` |
| Filtro de horario cambiado | `FILTER_SCHEDULE_CHANGED` | `schedule: String` |
| Tab cambiado (Precio/Distancia) | `MAP_TAB_CHANGED` | `tab: String` |
| Ruta iniciada | `ROUTE_STARTED` | — |
| Ruta cancelada | `ROUTE_CANCELLED` | — |

---

### station_detail

**Dónde se instrumenta:** `DetailStationViewModel`

| Evento | Tipo | Parámetros |
|--------|------|-----------|
| Gasolinera añadida a favoritos | `STATION_FAVORITED` | `station_id: Int` |
| Gasolinera eliminada de favoritos | `STATION_UNFAVORITED` | `station_id: Int` |
| Gasolinera compartida | `STATION_SHARED` | `station_id: Int` |
| Alerta de precio activada | `PRICE_ALERT_ENABLED` | `station_id: Int` |
| Alerta de precio desactivada | `PRICE_ALERT_DISABLED` | `station_id: Int` |

---

### search

**Dónde se instrumenta:** `GasGuruSearchBarViewModel`

| Evento | Tipo | Parámetros |
|--------|------|-----------|
| Lugar seleccionado de resultados | `SEARCH_PLACE_SELECTED` | — |
| Historial de búsqueda limpiado | `SEARCH_HISTORY_CLEARED` | — |

---

### route_planner

**Dónde se instrumenta:** `RoutePlannerViewModel`

| Evento | Tipo | Parámetros |
|--------|------|-----------|
| Destino establecido | `ROUTE_PLANNER_DESTINATION_SET` | `is_current_location: Boolean` |
| Destinos intercambiados | `ROUTE_PLANNER_DESTINATIONS_SWAPPED` | — |
| Búsqueda reciente usada | `RECENT_SEARCH_USED` | — |

---

### profile

**Dónde se instrumenta:** `ProfileViewModel`

| Evento | Tipo | Parámetros |
|--------|------|-----------|
| Tema cambiado | `THEME_CHANGED` | `theme_mode: String` (LIGHT / DARK / SYSTEM) |

---

### favorites

**Dónde se instrumenta:** `FavoriteListStationViewModel`

| Evento | Tipo | Parámetros |
|--------|------|-----------|
| Tab cambiado (Precio/Distancia) | `FAVORITES_TAB_CHANGED` | `tab: Int` |
| Gasolinera eliminada desde lista | `STATION_UNFAVORITED_FROM_LIST` | `station_id: Int` |

---

### network

**Dónde se instrumenta:** `SyncManager`

| Evento | Tipo | Parámetros |
|--------|------|-----------|
| Dispositivo sin conexión | `WENT_OFFLINE` | — |
| Dispositivo con conexión | `CAME_ONLINE` | — |

---

### sync

**Dónde se instrumenta:** `PriceAlertRepositoryImpl`, `StationSyncWorker`

| Evento | Tipo | Parámetros |
|--------|------|-----------|
| Sincronización de alertas completada | `ALERTS_SYNC_COMPLETED` | `synced_count: Int` |
| Sincronización de alertas fallida | `ALERTS_SYNC_FAILED` | — |
| Worker iniciado | `STATION_SYNC_WORKER_STARTED` | — |
| Worker completado | `STATION_SYNC_WORKER_COMPLETED` | — |
| Worker reintentando | `STATION_SYNC_WORKER_RETRIED` | — |

---

### api

**Dónde se instrumenta:** `RemoteDataSourceImp`

| Evento | Tipo | Parámetros |
|--------|------|-----------|
| Fetch de gasolineras iniciado | `API_STATIONS_FETCH_STARTED` | — |
| Fetch de gasolineras completado | `API_STATIONS_FETCH_COMPLETED` | — |
| Fetch de gasolineras fallido | `API_STATIONS_FETCH_FAILED` | — |

---

### push

**Dónde se instrumenta:** `PushNotificationService`

| Evento | Tipo | Parámetros |
|--------|------|-----------|
| Notificación push pulsada | `PUSH_NOTIFICATION_TAPPED` | `station_id: String` |

---

### widget

**Dónde se instrumenta:** `WidgetStationClickCallback`

| Evento | Tipo | Parámetros |
|--------|------|-----------|
| Gasolinera pulsada en el widget | `WIDGET_STATION_TAPPED` | `station_id: Int` |

---

### auto

**Dónde se instrumenta:** `GasGuruSession`, `MapAutomotiveScreen`, `NearbyStationsScreen`, `FavoriteStationsScreen`

| Evento | Tipo | Parámetros |
|--------|------|-----------|
| Sesión de Android Auto iniciada | `AUTO_SESSION_STARTED` | — |
| Pantalla de gasolineras cercanas abierta | `AUTO_NEARBY_STATIONS_OPENED` | — |
| Pantalla de favoritos abierta | `AUTO_FAVORITE_STATIONS_OPENED` | — |
| Navegación a gasolinera iniciada | `AUTO_STATION_NAVIGATION_STARTED` | — |

---

## Resumen de ParamKeys

| Clave | Tipo | Descripción |
|-------|------|-------------|
| `category` | `String` | Categoría del evento (inyectada automáticamente) |
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
| `brand_names` | `String` | Marcas seleccionadas separadas por coma (ej. `"REPSOL,CEPSA"`) |
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
2. Añadir las claves de parámetro necesarias en `AnalyticsEvent.ParamKeys` si hacen falta nuevas.
3. Registrar el tipo en `Categories.fromType()` mapeándolo a la categoría correcta.
4. Llamar a `analyticsHelper.logEvent(...)` en el ViewModel o clase correspondiente.
5. Actualizar este documento.

---

## Inicialización de Mixpanel

Mixpanel se inicializa en `GasGuruApplication.mixpanelSetUp()` con el token del proyecto
desde `BuildConfig.mixpanelProjectToken`. `MixpanelAnalyticsHelper` recibe el singleton
ya inicializado via Koin — no se produce una segunda inicialización.
