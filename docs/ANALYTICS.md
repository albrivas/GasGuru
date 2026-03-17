# Analytics — GasGuru

## Overview

GasGuru uses **Mixpanel** for product analytics. The implementation follows the NowInAndroid pattern:
an `AnalyticsHelper` interface is injected via **Koin** into ViewModels and non-composable classes,
while a `LocalAnalyticsHelper` CompositionLocal provides access from Composables.

---

## Architecture

```
core:analytics
├── AnalyticsEvent.kt          — data class + event types and param key constants
├── AnalyticsHelper.kt         — interface: fun logEvent(event: AnalyticsEvent)
├── NoOpAnalyticsHelper.kt     — no-op impl for tests and Compose previews
├── MixpanelAnalyticsHelper.kt — production impl wrapping MixpanelAPI
├── LocalAnalyticsHelper.kt    — staticCompositionLocalOf<AnalyticsHelper>
└── di/AnalyticsModule.kt      — Koin single<AnalyticsHelper> binding
```

### Dependency flow

```
app → core:analytics (MainActivity, StationSyncWorker)
feature:* → core:analytics (all ViewModels)
core:data → core:analytics (SyncManager, PriceAlertRepositoryImpl)
core:components → core:analytics (GasGuruSearchBarViewModel)
```

---

## How to use

### In a ViewModel (via Koin injection)

```kotlin
class MyViewModel(
    private val analyticsHelper: AnalyticsHelper,
) : ViewModel() {

    fun onUserAction() {
        analyticsHelper.logEvent(
            event = AnalyticsEvent(
                type = AnalyticsEvent.Types.VEHICLE_CREATED,
                extras = listOf(
                    AnalyticsEvent.Param(key = AnalyticsEvent.ParamKeys.FUEL_TYPE, value = "GASOLINE_95"),
                ),
            ),
        )
    }
}
```

Register in the Koin module:
```kotlin
viewModel { MyViewModel(analyticsHelper = get()) }
```

### In a Composable (via CompositionLocal)

```kotlin
@Composable
fun MyScreen() {
    val analyticsHelper = LocalAnalyticsHelper.current
    Button(onClick = {
        analyticsHelper.logEvent(AnalyticsEvent(type = AnalyticsEvent.Types.STATION_SELECTED))
    }) {
        Text("Select")
    }
}
```

`LocalAnalyticsHelper` is provided at the root of the composition in `MainActivity`:
```kotlin
CompositionLocalProvider(LocalAnalyticsHelper provides analyticsHelper) { ... }
```

### In tests

Use `NoOpAnalyticsHelper()` which does nothing:
```kotlin
val viewModel = MyViewModel(analyticsHelper = NoOpAnalyticsHelper())
```

---

## Events catalogue

### Onboarding

| Event | Type constant | Parameters |
|-------|---------------|-----------|
| Onboarding started | `ONBOARDING_STARTED` | — |
| Page viewed | `ONBOARDING_PAGE_VIEWED` | `page_number` |
| Skipped | `ONBOARDING_SKIPPED` | — |
| Fuel selected | `ONBOARDING_FUEL_SELECTED` | `fuel_type` |
| Tank capacity set | `ONBOARDING_TANK_CAPACITY_SET` | `capacity_litres` |
| Completed | `ONBOARDING_COMPLETED` | — |

**Tracked in:** `NewOnboardingViewModel`, `OnboardingViewModel`, `CapacityTankViewModel`

---

### Vehicles

| Event | Type constant | Parameters |
|-------|---------------|-----------|
| Vehicle created | `VEHICLE_CREATED` | `vehicle_type`, `fuel_type`, `capacity_litres`, `is_principal` |
| Vehicle edited | `VEHICLE_EDITED` | `vehicle_type`, `fuel_type` |
| Vehicle deleted | `VEHICLE_DELETED` | `was_principal`, `vehicles_remaining` |

**Tracked in:** `AddVehicleViewModel`, `ProfileViewModel`

---

### Station Map

| Event | Type constant | Parameters |
|-------|---------------|-----------|
| Stations loaded | `MAP_STATIONS_LOADED` | `station_count` |
| Station selected | `STATION_SELECTED` | `station_id` |
| Brand filter changed | `FILTER_BRAND_CHANGED` | `brand_count` |
| Nearby filter changed | `FILTER_NEARBY_CHANGED` | `nearby_km` |
| Schedule filter changed | `FILTER_SCHEDULE_CHANGED` | `schedule` |
| Tab changed | `MAP_TAB_CHANGED` | `tab` |
| Route started | `ROUTE_STARTED` | — |
| Route cancelled | `ROUTE_CANCELLED` | — |

**Tracked in:** `StationMapViewModel`

---

### Station Detail

| Event | Type constant | Parameters |
|-------|---------------|-----------|
| Station favorited | `STATION_FAVORITED` | `station_id` |
| Station unfavorited | `STATION_UNFAVORITED` | `station_id` |
| Price alert enabled | `PRICE_ALERT_ENABLED` | `station_id` |
| Price alert disabled | `PRICE_ALERT_DISABLED` | `station_id` |

**Tracked in:** `DetailStationViewModel`

---

### Search

| Event | Type constant | Parameters |
|-------|---------------|-----------|
| Place selected | `SEARCH_PLACE_SELECTED` | — |
| History cleared | `SEARCH_HISTORY_CLEARED` | — |

**Tracked in:** `GasGuruSearchBarViewModel`

---

### Route Planner

| Event | Type constant | Parameters |
|-------|---------------|-----------|
| Destination set | `ROUTE_PLANNER_DESTINATION_SET` | `is_current_location` |
| Destinations swapped | `ROUTE_PLANNER_DESTINATIONS_SWAPPED` | — |
| Recent search used | `RECENT_SEARCH_USED` | — |

**Tracked in:** `RoutePlannerViewModel`

---

### Profile

| Event | Type constant | Parameters |
|-------|---------------|-----------|
| Theme changed | `THEME_CHANGED` | `theme_mode` |

**Tracked in:** `ProfileViewModel`

---

### Favorites

| Event | Type constant | Parameters |
|-------|---------------|-----------|
| Tab changed | `FAVORITES_TAB_CHANGED` | `tab` |
| Station unfavorited from list | `STATION_UNFAVORITED_FROM_LIST` | `station_id` |

**Tracked in:** `FavoriteListStationViewModel`

---

### Network / Offline

| Event | Type constant | Parameters |
|-------|---------------|-----------|
| Went offline | `WENT_OFFLINE` | — |
| Came online | `CAME_ONLINE` | — |

**Tracked in:** `SyncManager`

---

### Alert Synchronization

| Event | Type constant | Parameters |
|-------|---------------|-----------|
| Sync completed | `ALERTS_SYNC_COMPLETED` | `synced_count` |
| Sync failed | `ALERTS_SYNC_FAILED` | — |

**Tracked in:** `PriceAlertRepositoryImpl`

---

### Station Sync Worker

| Event | Type constant | Parameters |
|-------|---------------|-----------|
| Worker started | `STATION_SYNC_WORKER_STARTED` | — |
| Worker completed | `STATION_SYNC_WORKER_COMPLETED` | — |
| Worker retried | `STATION_SYNC_WORKER_RETRIED` | — |

**Tracked in:** `StationSyncWorker`

---

## Adding a new event

1. Add the event type constant to `AnalyticsEvent.Types` in `AnalyticsEvent.kt`.
2. Add any new param keys to `AnalyticsEvent.ParamKeys` if needed.
3. Call `analyticsHelper.logEvent(...)` in the appropriate ViewModel or class.
4. Update this document.

---

## Mixpanel initialisation

Mixpanel is initialised in `GasGuruApplication.mixpanelSetUp()` using the project token from
`BuildConfig.mixpanelProjectToken` (secrets-managed). `MixpanelAnalyticsHelper` retrieves the
already-initialised singleton via `MixpanelAPI.getInstance(context, null, true)` — no second
initialisation occurs.
