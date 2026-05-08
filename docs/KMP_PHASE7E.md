# KMP Phase 7E — `:feature:detail-station` → Compose Multiplatform

## Objetivo

Migrar `:feature:detail-station` de `gasguru.android.library` a `gasguru.kmp.compose.library`, moviendo todas las fuentes a `commonMain` y encapsulando las APIs Android-only (share intent, navegación a mapas, permiso de notificaciones) mediante `expect/actual` Composable dentro del propio módulo.

---

## Cambios principales

### `build.gradle.kts`

- Plugin `gasguru.android.library` → `gasguru.kmp.compose.library`
- Dependencias movidas a `commonMain.dependencies`: `core.analytics`, `core.ui`, `core.domain`, `core.model`, `core.uikit`, `core.common`, `navigation`, Coil 3, `kotlinx.datetime`
- **Coil 2 → Coil 3**: `io.coil-kt.coil3:coil-compose` + `coil-network-ktor3` (KMP)
- Añadidas versiones en `gradle/libs.versions.toml`: `coil3 = "3.2.0"`
- Tests: `commonTest` (kotlin.test + Turbine) + `androidInstrumentedTest` (Compose UI)

### Recursos

- `src/main/res/` → `src/commonMain/composeResources/`
- Strings: `values/strings.xml` + `values-es/strings.xml` (36 strings, formato `%1$d` en vez de `%d`)
- Drawables: movidos a `composeResources/drawable/`

### APIs Android-only → `expect/actual` en `platform/`

Se crearon tres `expect/actual` Composable dentro del módulo para evitar pasar lambdas Android-only hacia arriba en la jerarquía de navegación:

| Archivo | `expect` (commonMain) | `actual` Android | `actual` iOS |
|---------|----------------------|-----------------|--------------|
| `ShareAction.kt` | `rememberShareAction(): (String) -> Unit` | `Intent(ACTION_SEND)` + `createChooser` | no-op stub |
| `MapsNavigation.kt` | `rememberNavigateToMapsAction(): (LatLng) -> Unit` | Google Maps + Waze chooser | no-op stub |
| `NotificationPermission.kt` | `rememberNotificationPermissionRequester(onGranted): () -> Unit` | `rememberLauncherForActivityResult(RequestPermission)` + check Android 13+ | invoca `onGranted` directamente |

**Regla aplicada**: nunca añadir intents Android-only en `GasGuruNavHost` ni en la navegación. Todo comportamiento platform-specific se encapsula en el módulo mediante `expect/actual`.

### `DetailStationScreen.kt`

- Eliminados imports Android: `android.Manifest`, `android.content.Intent`, `android.provider.Settings`, `androidx.activity.compose.*`, `androidx.constraintlayout.*`, `LocalContext`, etc.
- `stringResource` → `org.jetbrains.compose.resources.stringResource`
- `coil.compose.AsyncImage` → `coil3.compose.AsyncImage` con `LocalPlatformContext.current`
- `ConstraintLayout` → `Row(verticalAlignment = CenterVertically) { Column(Modifier.weight(1f)) + Spacer(16.dp) + Box(80.dp) }`
- `koinViewModel` de `org.koin.compose.viewmodel.koinViewModel`
- `formatPrice(value: Double): String` reemplaza `DecimalFormat` (JVM-only)

### `DateUtils.kt`

- `System.currentTimeMillis()` → `Clock.System.now().toEpochMilliseconds()` (import: `kotlin.time.Clock`)
- `TimeUnit.X.toMillis(Y)` → constantes manuales (`ONE_MINUTE_MS = 60_000L`, etc.)
- `R.string.*` → `Res.string.*`

> **Nota**: En Kotlin 2.1+ existe `kotlin.time.Clock` en la stdlib estándar (que es lo que usamos aquí, no `kotlinx.datetime.Clock`). El import correcto es `import kotlin.time.Clock`.

### `DetailStationNavigation.kt`

Sin cambios en la firma pública — `detailStationScreen()` y `detailStationScreenDialog()` no reciben lambdas adicionales porque toda la lógica platform-specific está encapsulada en `expect/actual`.

---

## Estructura de directorios resultante

```
feature/detail-station/src/
├── commonMain/
│   ├── composeResources/
│   │   ├── drawable/          # drawables migrados
│   │   ├── values/strings.xml
│   │   └── values-es/strings.xml
│   └── kotlin/com/gasguru/feature/detail_station/
│       ├── analytics/
│       ├── di/
│       ├── navigation/
│       ├── platform/          # expect declarations
│       │   ├── MapsNavigation.kt
│       │   ├── NotificationPermission.kt
│       │   └── ShareAction.kt
│       └── ui/
├── androidMain/
│   └── kotlin/.../platform/   # actual Android implementations
└── iosMain/
    └── kotlin/.../platform/   # actual iOS stubs (V1 no-op)
```

---

## Decisiones técnicas

| Decisión | Alternativa rechazada | Motivo |
|----------|-----------------------|--------|
| `expect/actual` dentro de la feature | Lambdas en `GasGuruNavHost` | Mantiene el API de navegación limpio; el módulo es autocontenido |
| Coil 3 KMP en el módulo | Mantener Coil 2 | Coil 2 es Android-only; Coil 3 tiene soporte KMP nativo |
| `kotlin.time.Clock` | `kotlinx.datetime.Clock` | Kotlin 2.1+ incluye `Clock` en la stdlib; es el import correcto en K2 |
| `Row + weight(1f)` | ConstraintLayout | ConstraintLayout Compose no tiene soporte KMP |
| Stub iOS no-op en `rememberNavigateToMapsAction` | MapKit en V1 | Navegar a mapas externos en iOS es trabajo de V2 |

---

## Verificación

```bash
./gradlew :feature:detail-station:compileDebugKotlinAndroid   # ✅
./gradlew :feature:detail-station:testDebugUnitTest           # ✅
./gradlew :app:assembleDebug                                   # ✅
```

---

## Próxima fase

**Phase 7G**: `:feature:station-map` → CMP — la más compleja del proyecto (Google Maps Compose en Android, MapKit en iOS vía `expect/actual` Composable).
