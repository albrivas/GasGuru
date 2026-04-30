# KMP Phase 6A — Migración de `:core:ui` a Compose Multiplatform

## Contexto

`:core:ui` contenía todos los mappers UI, modelos y el gestor de in-app review de Android. Su dependencia es unidireccional hacia `:core:model` y `:core:uikit` (ambos ya KMP tras Phase 6B), lo que lo hacía migratable sin bloqueadores.

## Estructura resultante

```
core/ui/src/
├── commonMain/
│   ├── composeResources/
│   │   ├── values/strings.xml          (39 entradas, inglés)
│   │   └── values-es/strings.xml       (español)
│   └── kotlin/com/gasguru/core/ui/
│       ├── mapper/
│       │   ├── FuelStationBrandsUiMapper.kt
│       │   ├── FuelStationUiMapper.kt
│       │   ├── FuelTypeUiMapper.kt
│       │   ├── PriceUiMapper.kt
│       │   ├── StationListUiMapper.kt
│       │   └── ThemeModeUiMapper.kt
│       ├── models/
│       │   ├── FuelStationBrandsUiModel.kt
│       │   ├── FuelStationUiModel.kt
│       │   ├── FuelTypeUiModel.kt
│       │   ├── PriceUiModel.kt
│       │   ├── ThemeModeUi.kt
│       │   └── VehicleTypeUiModel.kt
│       ├── review/
│       │   └── InAppReviewManager.kt   (expect class)
│       ├── FuelUiExtensions.kt
│       ├── RecentSearchQueriesUiState.kt
│       └── Semantics.kt
├── androidMain/
│   ├── kotlin/com/gasguru/core/ui/
│   │   ├── DialogSystemBars.kt
│   │   ├── FuelUiExtensionsAndroid.kt  (overloads para auto:common y StationMapScreen)
│   │   └── review/
│   │       ├── InAppReviewManager.kt   (actual class, Activity en constructor)
│   │       └── InAppReviewExt.kt       (rememberInAppReviewManager — nullable)
│   └── res/
│       ├── mipmap-*/                   (ic_launcher — requerido por feature:widget)
│       └── values/strings.xml          (nearby_stations, favorites — requerido por auto:common)
├── iosMain/
│   └── kotlin/com/gasguru/core/ui/
│       └── review/
│           └── InAppReviewManager.kt   (actual class, no-op V1)
└── commonTest/
    └── kotlin/com/gasguru/core/ui/
        └── mapper/
            ├── FuelStationUiMapperTest.kt
            └── PriceUiMapperTest.kt
```

## Decisiones clave

### `InAppReviewManager`: `expect/actual` con `Activity` en androidMain

La interfaz inicial fue `interface InAppReviewManager` en commonMain. Se cambió a `expect class` porque Android necesita `Activity` y `ReviewManager` en el constructor — no hay forma limpia de inyectarlos si la clase se instancia en commonMain.

```kotlin
// commonMain
expect class InAppReviewManager {
    suspend fun launchReviewFlow(
        onReviewCompleted: () -> Unit,
        onReviewFailed: (Exception) -> Unit,
    )
}

// androidMain — Activity inyectada en constructor
actual class InAppReviewManager(
    private val reviewManager: ReviewManager,
    private val activity: Activity,
) { ... }

// iosMain — no-op hasta V2
actual class InAppReviewManager { ... }
```

`rememberInAppReviewManager()` es Android-only en `androidMain/review/InAppReviewExt.kt` y devuelve `InAppReviewManager?` (nullable) para evitar crash en previews donde no hay Activity.

### `PriceUiModel.formattedPrice`: formateo manual sin `String.format`

`String.format("%.3f", price)` y `DecimalFormat("0.000")` son locale-dependientes en KMP (Kotlin/Native usa coma en locales europeos). Se reemplazó por formateo manual:

```kotlin
val rounded = (rawPrice * 1000).roundToLong()
val intPart = rounded / 1000
val fracPart = (rounded % 1000).toString().padStart(3, '0')
"$intPart.$fracPart €/l"
```

Mismo patrón que `FuelStation.formatDistance()` ya existente en `:core:model`.

### `VehicleItemCardModel.fuelTypeTranslationRes`: `String` → `StringResource`

El mapper `VehicleUiMapper` usaba `runBlocking { getString(res) }` para resolver el string en el ViewModel, lo que causa deadlock en tests de coroutines (el dispatcher de tests se bloquea). La solución es pasar `StringResource` directamente y resolverlo en el componente con `stringResource()`. Esto elimina `context: Context` del `ProfileViewModel` y de su módulo Koin.

### Strings Android-only: `androidMain/res/values/strings.xml`

`auto:common` usa `carContext.getString(R.string.nearby_stations)` — código no-composable que no puede usar CMP resources. Se mantiene un fichero Android R mínimo con las dos strings que necesita este módulo. El resto de strings migran a `composeResources`.

### Lecciones aplicadas

- **L016**: Todos los 11 consumidores actualizados en el mismo commit — se verificó con `grep -rn "com\.gasguru\.core\.ui\.R\b"`.
- **L008**: No usar `String.format` en KMP — formateo manual.
- **L010**: `runBlocking { getString() }` de Compose Resources no funciona en unit tests (sin inicialización de recursos). Mover resolución de strings al composable.

## Análiticas añadidas

Se añadieron dos eventos en `core:analytics` y el feature `detail-station`:
- `IN_APP_REVIEW_COMPLETED` — cuando el flujo de review completa
- `IN_APP_REVIEW_FAILED` — cuando falla, incluyendo el mensaje de error como parámetro

## Tests añadidos

| Fichero | Casos |
|---------|-------|
| `FuelStationUiMapperTest` | brandIcon set, formattedDistance (1500m → "2 Km"), formattedName, UNKNOWN brand |
| `PriceUiMapperTest` | hasPrice true/false, rawPrice correcto, formattedPrice "1.459 €/l" |
| `AnalyticsEventCategoriesTest` (actualizado) | `IN_APP_REVIEW_COMPLETED/FAILED` mapean a `STATION_DETAIL` |

## Deuda conocida

- `feature/detail-station/DetailStationScreen.kt` usa `DecimalFormat` para `totalCost` — queda para Phase 7 (migración de features).
- `core:uikit` tiene errores de compilación iOS (`@Preview` y `testTagsAsResourceId` en commonMain) — preexistentes desde Phase 6B, fuera del scope de 6A.
