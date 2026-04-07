# Analytics — GasGuru

## Overview

GasGuru usa **Mixpanel** para analíticas de producto. El sistema sigue el patrón NowInAndroid:
- `AnalyticsHelper` interface inyectable via **Koin** en ViewModels y clases non-composable
- `LocalAnalyticsHelper` CompositionLocal para acceso desde Composables
- `NoOpAnalyticsHelper` para tests y previews (sin side effects)

La app **no tiene login**, por lo que no se usa People Analytics ni `identify()`. La identificación
es anónima por dispositivo (`distinctId` auto-generado por el SDK de Mixpanel).

---

## Estructura del módulo `core:analytics`

```
core/analytics/
├── build.gradle.kts
└── src/
    ├── main/java/com/gasguru/core/analytics/
    │   ├── AnalyticsEvent.kt           — data class + Types, Categories y ParamKeys
    │   ├── AnalyticsHelper.kt          — interface: logEvent + updateSuperProperties
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
    fun updateSuperProperties(properties: Map<String, Any>)
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

Inyecta `Context` y la instancia de `MixpanelAPI` via Koin. En el bloque `init` registra
super properties estáticas una sola vez (`registerSuperPropertiesOnce`). El método
`updateSuperProperties` permite actualizar las propiedades dinámicas:

```kotlin
class MixpanelAnalyticsHelper(
    private val context: Context,
    private val mixpanel: MixpanelAPI,
) : AnalyticsHelper {
    init {
        val appVersion = context.packageManager.getPackageInfo(context.packageName, 0).versionName
        mixpanel.registerSuperPropertiesOnce(JSONObject().apply {
            put("app_version", appVersion)
            put("platform", "android")
        })
    }

    override fun logEvent(event: AnalyticsEvent) {
        val properties = JSONObject()
        properties.put(ParamKeys.CATEGORY, event.category)
        event.extras.forEach { param -> properties.put(param.key, param.value) }
        mixpanel.track(event.type, properties)
    }

    override fun updateSuperProperties(properties: Map<String, Any>) {
        val jsonProperties = JSONObject()
        properties.forEach { (key, value) -> jsonProperties.put(key, value) }
        mixpanel.registerSuperProperties(jsonProperties)
    }
}
```

#### `AnalyticsModule` — Koin

```kotlin
val analyticsModule = module {
    single<MixpanelAPI> {
        MixpanelAPI.getInstance(androidContext(), null, true)
    }
    single<AnalyticsHelper> {
        if (BuildConfig.DEBUG) LogcatAnalyticsHelper()
        else MixpanelAnalyticsHelper(context = androidContext(), mixpanel = get())
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
y sus parámetros como pares `key=value`. El método `updateSuperProperties` escribe
las propiedades con tag `⚙ [super_properties]`:

```
D/Analytics: ▶ [vehicle] vehicle_created | vehicle_type=CAR, fuel_type=GASOLINE_95
D/Analytics: ▶ [session] app_opened | source=direct
D/Analytics: ⚙ [super_properties] primary_fuel_type=GASOLINE_95, vehicle_count=1
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

## Super Properties

Las super properties se adjuntan automáticamente a **todos** los eventos vía
`mixpanel.registerSuperProperties()`, sin intervención del caller.

| Propiedad | Tipo | Cuándo se actualiza |
|-----------|------|---------------------|
| `app_version` | `String` | Al iniciar la app (estática, `registerSuperPropertiesOnce`) |
| `platform` | `String` (`"android"`) | Al iniciar la app (estática, `registerSuperPropertiesOnce`) |
| `primary_fuel_type` | `String` | Al arrancar la app tras cargar `getUserDataUseCase` |
| `vehicle_count` | `Int` | Al arrancar la app tras cargar `getUserDataUseCase` |

**Actualizar super properties dinámicas:**

```kotlin
analyticsHelper.updateSuperProperties(
    mapOf(
        "primary_fuel_type" to "GASOLINE_95",
        "vehicle_count" to 2,
    )
)
```

---

## Sistema de categorías

Cada evento se enriquece automáticamente con una propiedad `category` en Mixpanel.
Esto permite filtrar, segmentar y construir funnels por área funcional sin configuración adicional.

| Categoría | Constante | Qué mide |
|-----------|-----------|----------|
| `session` | `Categories.SESSION` | Aperturas de la app y retención |
| `onboarding` | `Categories.ONBOARDING` | Funnel de activación primera vez |
| `vehicle` | `Categories.VEHICLE` | Configuración y gestión de vehículos |
| `map` | `Categories.MAP` | Interacciones con el mapa principal |
| `station_detail` | `Categories.STATION_DETAIL` | Detalle de gasolinera: favoritos, alertas, compartir |
| `search` | `Categories.SEARCH` | Búsqueda de ubicaciones |
| `route_planner` | `Categories.ROUTE_PLANNER` | Planificador de rutas |
| `profile` | `Categories.PROFILE` | Personalización y ajustes |
| `sync` | `Categories.SYNC` | Workers y sincronización de alertas en background (solo errores) |
| `api` | `Categories.API` | Llamadas a la API de gasolineras (solo errores) |
| `push` | `Categories.PUSH` | Notificaciones push |
| `widget` | `Categories.WIDGET` | Widget de pantalla de inicio |
| `auto` | `Categories.AUTO` | Android Auto |
| `permissions` | `Categories.PERMISSIONS` | Permisos de ubicación y notificaciones |

La categoría se deriva automáticamente en `Categories.fromType(type)`. Al añadir un nuevo
evento hay que registrarlo en ese `when` para evitar que caiga en `unknown`.

---

## Catálogo de eventos por categoría

---

### session

**Dónde se instrumenta:** `MainActivity`

| Evento | Tipo | Parámetros |
|--------|------|-----------|
| App abierta | `APP_OPENED` | `source: String` (direct / notification / widget / auto) |

---

### onboarding

**Dónde se instrumenta:** `NewOnboardingViewModel`, `OnboardingViewModel`, `CapacityTankViewModel`

| Evento | Tipo | Parámetros |
|--------|------|-----------|
| Onboarding iniciado | `ONBOARDING_STARTED` | — |
| Página visualizada | `ONBOARDING_PAGE_VIEWED` | `page_number: Int` |
| Onboarding saltado | `ONBOARDING_SKIPPED` | `page_number: Int` (página en la que se abandona) |
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

**Dónde se instrumenta:** `StationMapViewModel`, `StationMapScreen`

| Evento | Tipo | Parámetros |
|--------|------|-----------|
| Gasolinera seleccionada | `STATION_SELECTED` | `station_brand: String`, `selection_source: String` (map/search/favorites) |
| Filtro de marca cambiado | `FILTER_BRAND_CHANGED` | `brand_count: Int`, `brand_names: String` (comma-separated) |
| Filtro de distancia cambiado | `FILTER_NEARBY_CHANGED` | `nearby_km: String` |
| Filtro de horario cambiado | `FILTER_SCHEDULE_CHANGED` | `schedule: String` |
| Tab cambiado (Precio/Distancia) | `MAP_TAB_CHANGED` | `tab: String` |
| Ruta iniciada | `ROUTE_STARTED` | `station_brand: String` |
| Ruta cancelada | `ROUTE_CANCELLED` | `station_brand: String` |
| Permiso de ubicación concedido | `LOCATION_PERMISSION_GRANTED` | — |
| Permiso de ubicación denegado | `LOCATION_PERMISSION_DENIED` | — |

> Los eventos de permiso se instrumentan en `StationMapScreen` dentro del callback `rememberLauncherForActivityResult`.

---

### station_detail

**Dónde se instrumenta:** `DetailStationViewModel`, `DetailStationScreen`

| Evento | Tipo | Parámetros |
|--------|------|-----------|
| Detalle de gasolinera visto | `STATION_DETAIL_VIEWED` | `station_brand: String`, `is_favorite: Boolean`, `has_price_alert: Boolean` |
| Gasolinera añadida a favoritos | `STATION_FAVORITED` | `station_brand: String` |
| Gasolinera eliminada de favoritos | `STATION_UNFAVORITED` | `station_brand: String`, `source: String` (detail / list) |
| Gasolinera compartida | `STATION_SHARED` | `station_brand: String` |
| Alerta de precio activada | `PRICE_ALERT_ENABLED` | — (fuel via super property `primary_fuel_type`) |
| Alerta de precio desactivada | `PRICE_ALERT_DISABLED` | — |
| Alerta de precio disparada | `PRICE_ALERT_TRIGGERED` | — |
| Permiso de notificaciones concedido | `NOTIFICATION_PERMISSION_GRANTED` | — |
| Permiso de notificaciones denegado | `NOTIFICATION_PERMISSION_DENIED` | — |

> `STATION_UNFAVORITED` unifica los eventos antes separados (desde detalle y desde lista). El parámetro
> `source` indica el origen: `"detail"` desde `DetailStationViewModel` y `"list"` desde `FavoriteListStationViewModel`.
>
> `PRICE_ALERT_TRIGGERED` se dispara server-side (Supabase/OneSignal) — no desde el cliente Android.

---

### search

**Dónde se instrumenta:** `GasGuruSearchBarViewModel`

| Evento | Tipo | Parámetros |
|--------|------|-----------|
| Búsqueda realizada | `SEARCH_PERFORMED` | `query_length: Int`, `result_count: Int` |
| Lugar seleccionado de resultados | `SEARCH_PLACE_SELECTED` | — |

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

### sync

**Dónde se instrumenta:** `PriceAlertRepositoryImpl`, `StationSyncWorker`

Solo se registran eventos de error — los eventos de éxito se eliminaron por ser ruido técnico.

| Evento | Tipo | Parámetros |
|--------|------|-----------|
| Sincronización de alertas fallida | `ALERTS_SYNC_FAILED` | `error_message: String`, `error_type: String` |
| Worker reintentando | `STATION_SYNC_WORKER_RETRIED` | `error_message: String`, `error_type: String` |

---

### api

**Dónde se instrumenta:** `RemoteDataSourceImp`, `SupabaseRemoteDataSource`

Solo se registra el error — los eventos de inicio y éxito se eliminaron por ser ruido técnico.

| Evento | Tipo | Parámetros |
|--------|------|-----------|
| Fetch de gasolineras fallido | `API_STATIONS_FETCH_FAILED` | `error_message: String`, `error_type: String` |

---

### push

**Dónde se instrumenta:** `PushNotificationService`

| Evento | Tipo | Parámetros |
|--------|------|-----------|
| Notificación push pulsada | `PUSH_NOTIFICATION_TAPPED` | `notification_type: String` |

---

### widget

**Dónde se instrumenta:** `WidgetStationClickCallback`, Glance widget `onEnabled()`

| Evento | Tipo | Parámetros |
|--------|------|-----------|
| Gasolinera pulsada en el widget | `WIDGET_STATION_TAPPED` | — |
| Widget añadido a la pantalla de inicio | `WIDGET_ADDED_TO_HOME` | — |

---

### auto

**Dónde se instrumenta:** `GasGuruSession`, `MapAutomotiveScreen`, `NearbyStationsScreen`, `FavoriteStationsScreen`

| Evento | Tipo | Parámetros |
|--------|------|-----------|
| Sesión de Android Auto iniciada | `AUTO_SESSION_STARTED` | — |
| Pantalla de gasolineras cercanas abierta | `AUTO_NEARBY_STATIONS_OPENED` | — |
| Pantalla de favoritos abierta | `AUTO_FAVORITE_STATIONS_OPENED` | — |
| Navegación a gasolinera iniciada | `AUTO_STATION_NAVIGATION_STARTED` | `station_brand: String` |

---

### permissions

**Dónde se instrumenta:** `StationMapScreen` (ubicación), `DetailStationScreen` (notificaciones)

| Evento | Tipo | Parámetros |
|--------|------|-----------|
| Permiso de ubicación concedido | `LOCATION_PERMISSION_GRANTED` | — |
| Permiso de ubicación denegado | `LOCATION_PERMISSION_DENIED` | — |
| Permiso de notificaciones concedido | `NOTIFICATION_PERMISSION_GRANTED` | — |
| Permiso de notificaciones denegado | `NOTIFICATION_PERMISSION_DENIED` | — |

---

## Resumen de ParamKeys

| Clave | Tipo | Descripción |
|-------|------|-------------|
| `category` | `String` | Categoría del evento (inyectada automáticamente) |
| `source` | `String` | Origen de la acción (direct/notification/detail/list) |
| `page_number` | `Int` | Número de página en onboarding |
| `fuel_type` | `String` | Tipo de combustible (ej. `GASOLINE_95`) |
| `capacity_litres` | `Int` | Capacidad del depósito en litros |
| `vehicle_type` | `String` | Tipo de vehículo (ej. `CAR`, `MOTORCYCLE`) |
| `is_principal` | `Boolean` | Si el vehículo es el principal |
| `was_principal` | `Boolean` | Si el vehículo eliminado era el principal |
| `vehicles_remaining` | `Int` | Vehículos restantes tras eliminar |
| `station_brand` | `String` | Marca de la gasolinera (ej. `REPSOL`, `CEPSA`) — baja cardinalidad |
| `selection_source` | `String` | Cómo llegó el usuario a la gasolinera (`map` / `search` / `favorites`) |
| `is_favorite` | `Boolean` | Si la gasolinera es favorita |
| `has_price_alert` | `Boolean` | Si la gasolinera tiene alerta de precio activa |
| `brand_count` | `Int` | Número de marcas seleccionadas en filtro |
| `brand_names` | `String` | Marcas seleccionadas separadas por coma (ej. `"REPSOL,CEPSA"`) |
| `nearby_km` | `String` | Distancia en km del filtro de proximidad |
| `schedule` | `String` | Tipo de horario del filtro |
| `tab` | `String` | Tab seleccionado |
| `is_current_location` | `Boolean` | Si el destino es la ubicación actual |
| `theme_mode` | `String` | Modo de tema (LIGHT / DARK / SYSTEM) |
| `query_length` | `Int` | Longitud de la consulta de búsqueda |
| `result_count` | `Int` | Número de resultados de búsqueda |
| `notification_type` | `String` | Tipo de notificación push (ej. `price_alert`) |
| `error_message` | `String` | Mensaje de la excepción capturada en eventos de error |
| `error_type` | `String` | Nombre de la clase de la excepción (ej. `RuntimeException`) |

---

## Cómo usar

### Patrón: extension functions por módulo

Cada módulo define sus propias extension functions sobre `AnalyticsHelper` en un subpackage
`analytics/`. Esto mantiene la construcción de `AnalyticsEvent` centralizada y los call sites
limpios (1 línea).

**Estructura:**
```
feature/detail-station/.../analytics/StationDetailAnalyticsExt.kt
feature/station-map/.../analytics/MapAnalyticsExt.kt
feature/profile/.../analytics/ProfileAnalyticsExt.kt
feature/vehicle/.../analytics/VehicleAnalyticsExt.kt
feature/favorite-list-station/.../analytics/FavoriteListAnalyticsExt.kt
feature/onboarding/.../analytics/OnboardingAnalyticsExt.kt
feature/route-planner/.../analytics/RoutePlannerAnalyticsExt.kt
feature/widget/.../analytics/WidgetAnalyticsExt.kt
core/data/.../analytics/SyncAnalyticsExt.kt
core/network/.../analytics/ApiAnalyticsExt.kt
core/supabase/.../analytics/ApiAnalyticsExt.kt
core/notifications/.../analytics/PushAnalyticsExt.kt
auto/common/.../analytics/AutoAnalyticsExt.kt
app/.../analytics/SessionAnalyticsExt.kt
app/.../analytics/WorkerAnalyticsExt.kt
```

**Definición de una extension function:**
```kotlin
// feature/detail-station/.../analytics/StationDetailAnalyticsExt.kt
fun AnalyticsHelper.trackStationFavorited(brand: String) {
    logEvent(
        AnalyticsEvent(
            type = AnalyticsEvent.Types.STATION_FAVORITED,
            extras = listOf(
                AnalyticsEvent.Param(key = AnalyticsEvent.ParamKeys.STATION_BRAND, value = brand),
            ),
        )
    )
}
```

**Uso en el ViewModel:**
```kotlin
class DetailStationViewModel(
    private val analyticsHelper: AnalyticsHelper,
) : ViewModel() {

    fun onFavoriteClick() {
        analyticsHelper.trackStationFavorited(brand = station.brand)
    }
}
```

Registrar en el módulo Koin:

```kotlin
viewModel { MyViewModel(analyticsHelper = get()) }
```

### Actualizar super properties dinámicas

Llamar a `updateSuperProperties` cuando cambien datos globales relevantes
(vehículo principal, número de vehículos, etc.):

```kotlin
analyticsHelper.updateSuperProperties(
    mapOf(
        "primary_fuel_type" to userData.principalVehicle().fuelType.name,
        "vehicle_count" to userData.vehicles.size,
    )
)
```

### En un Composable (CompositionLocal)

```kotlin
@Composable
fun MyScreen() {
    val analyticsHelper = LocalAnalyticsHelper.current

    Button(onClick = {
        analyticsHelper.trackStationSelected(brand = station.brand)
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

O usar `FakeAnalyticsHelper` (en `core:testing`) para verificar eventos emitidos:

```kotlin
import com.gasguru.core.testing.fakes.analytics.FakeAnalyticsHelper

val fakeAnalyticsHelper = FakeAnalyticsHelper()
val viewModel = MyViewModel(analyticsHelper = fakeAnalyticsHelper)
// ...
assertTrue(fakeAnalyticsHelper.hasEvent(AnalyticsEvent.Types.STATION_FAVORITED))
// O con acceso completo:
val event = fakeAnalyticsHelper.eventsOfType(AnalyticsEvent.Types.ALERTS_SYNC_FAILED).first()
assertEquals("network timeout", event.extras.first { it.key == "error_message" }.value)
```

---

## Añadir un nuevo evento

1. Añadir la constante de tipo en `AnalyticsEvent.Types`.
2. Añadir las claves de parámetro necesarias en `AnalyticsEvent.ParamKeys` si hacen falta nuevas.
3. Registrar el tipo en `Categories.fromType()` mapeándolo a la categoría correcta.
4. Crear (o actualizar) la extension function en el archivo `analytics/XxxAnalyticsExt.kt`
   del módulo donde se instrumenta.
5. Llamar a la extension en el ViewModel o clase correspondiente.
6. Actualizar este documento.

**Criterio para incluir un nuevo evento:** debe ser una **acción de usuario** o un **error de sistema**
medible y accionable. Los eventos de infraestructura (inicio/fin de workers, polling, conectividad)
no deben añadirse — solo los errores.

---

## Inicialización de Mixpanel

Mixpanel se inicializa en `GasGuruApplication.mixpanelSetUp()` con el token del proyecto
desde `BuildConfig.mixpanelProjectToken`. `MixpanelAnalyticsHelper` recibe el singleton
ya inicializado via Koin — no se produce una segunda inicialización.

Las super properties estáticas (`app_version`, `platform`) se registran en el `init` de
`MixpanelAnalyticsHelper` con `registerSuperPropertiesOnce` — solo se escriben si no existen aún.
Las dinámicas se actualizan con `registerSuperProperties` cada vez que cambia el estado del usuario.
