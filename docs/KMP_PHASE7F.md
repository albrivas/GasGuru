# KMP Phase 7F — `:feature:route-planner` → Compose Multiplatform

## Objetivo

Migrar `:feature:route-planner` de `gasguru.android.library` a `gasguru.kmp.compose.library`, moviendo todas las fuentes a `commonMain` sin necesidad de `expect/actual`.

---

## Hallazgo clave

`docs/KMP_MIGRATION.md` clasificaba este módulo como **alta complejidad** por "usar mapas y Places SDK". Al revisarlo, las deps `libs.places` y `libs.kotlin.coroutines.play` eran **código muerto**: ningún archivo las referenciaba. La pantalla es un selector origen/destino que delega la búsqueda de lugares a `:feature:search` vía `getPreviousResult<PlaceArgs>(...)`. Complejidad real: similar a Phase 7C / 7D.

---

## Cambios principales

### `build.gradle.kts`

- Plugin `gasguru.android.library` + `gasguru.compose.library` → `gasguru.kmp.compose.library`
- Todas las deps → `commonMain.dependencies`
- **Deps eliminadas**: `libs.places`, `libs.kotlin.coroutines.play`, `libs.koin.androidx.compose`, `libs.kotlinx.serialization.json` (viene transitivamente desde `:navigation`)
- `compose.resources { publicResClass = true; packageOfResClass = "com.gasguru.feature.route_planner.generated.resources" }`
- Tests: `commonTest` con `kotlin("test")` + Turbine + JUnit5 API

### Recursos

- `src/main/res/drawable/*.xml` → `src/commonMain/composeResources/drawable/`
- `src/main/res/values/strings.xml` → `src/commonMain/composeResources/values/strings.xml`
- `src/main/res/values-es/strings.xml` → `src/commonMain/composeResources/values-es/strings.xml`

### `RoutePlannerScreen.kt`

- `import androidx.compose.ui.res.{painterResource,stringResource}` → `org.jetbrains.compose.resources.*`
- `import com.gasguru.feature.route_planner.R` → imports explícitos de `Res.*`
- `R.string.X` → `Res.string.X` (6 strings)
- `painterResource(id = R.drawable.X)` → `painterResource(Res.drawable.X)` (2 drawables)
- `org.koin.androidx.compose.koinViewModel` → `org.koin.compose.viewmodel.koinViewModel`

### Tests

- `src/test/kotlin/` → `src/commonTest/kotlin/` sin cambios
- JUnit5 + Turbine + `FakeOfflineRecentSearchRepository` + `NoOpAnalyticsHelper` + `CoroutinesTestExtension` funcionan directamente

---

## Estructura de directorios resultante

```
feature/route-planner/src/
├── commonMain/
│   ├── composeResources/
│   │   ├── drawable/          # ic_current_location.xml, ic_swap.xml
│   │   ├── values/strings.xml
│   │   └── values-es/strings.xml
│   └── kotlin/com/gasguru/feature/route_planner/
│       ├── analytics/
│       ├── di/
│       ├── navigation/
│       └── ui/
└── commonTest/
    └── kotlin/.../ui/         # RoutePlannerViewModelTest
```

---

## Decisiones técnicas

| Decisión | Alternativa rechazada | Motivo |
|----------|-----------------------|--------|
| Sin `expect/actual` | Lambdas Android-only | No hay APIs platform-specific: los intents/mapas/Places están en otros módulos |
| Eliminar `libs.places` y `libs.kotlin.coroutines.play` | Mantenerlas | Son deps muertas, no hay ni un import de ellas en el código |
| Eliminar `AndroidManifest.xml` | Mantenerlo vacío | `gasguru.kmp.compose.library` no lo requiere |

---

## Verificación

```bash
./gradlew :feature:route-planner:compileDebugKotlinAndroid   # ✅
./gradlew :feature:route-planner:testDebugUnitTest           # ✅
./gradlew :app:assembleDebug                                   # ✅
```

---

## Próxima fase

**Phase 7G**: `:feature:station-map` → CMP — la más compleja del proyecto (Google Maps Compose en Android, MapKit en iOS vía `expect/actual` Composable).
