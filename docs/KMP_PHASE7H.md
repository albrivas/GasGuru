# KMP Phase 7H — `:feature:vehicle` → Compose Multiplatform

## Objetivo

Migrar `:feature:vehicle` de `gasguru.android.library` a `gasguru.kmp.compose.library`, moviendo todas las fuentes a `commonMain` sin necesidad de `expect/actual`. Este módulo fue omitido durante la fase 7 original.

---

## Hallazgos clave

### Migración parcial preexistente

`AddVehicleScreen.kt` tenía una migración parcial inacabada: importaba `org.jetbrains.compose.resources.stringResource as cmpStringResource` (alias para evitar colisión con `androidx.compose.ui.res.stringResource`) y lo usaba en dos llamadas para strings de `core:ui` y `core:uikit` — módulos ya migrados a KMP. Las strings propias del módulo seguían usando `R.string.*`. Este plan completa esa migración unificando todo bajo `org.jetbrains.compose.resources.stringResource`.

### Dependencia mockk sin usar

`build.gradle.kts` declaraba `testImplementation(libs.mockk)` pero ningún test del módulo lo importaba ni lo usaba. Se elimina.

### Sin expect/actual

El módulo no tiene APIs Android-only propias (no hay intents, context, share, maps). Todo el código es 100% compatible con `commonMain` sin necesidad de ningún `expect/actual`.

---

## Cambios principales

### `build.gradle.kts`

- Plugin `gasguru.android.library` + `gasguru.compose.library` → `gasguru.kmp.compose.library`
- Todas las deps → `commonMain.dependencies`
- **Deps eliminadas**: `libs.koin.androidx.compose` (el plugin `kmp.compose.library` ya lo añade a `androidMain`), `libs.mockk` (no se usaba)
- Eliminado `testOptions { unitTests.isReturnDefaultValues = true }` — innecesario en KMP porque los tests JVM corren sin el framework de Android
- `compose.resources { publicResClass = true; packageOfResClass = "com.gasguru.feature.vehicle.generated.resources" }`
- Tests: `commonTest` con `kotlin("test")` + Turbine + JUnit5 API; `androidUnitTest` con el engine

### Recursos

- `src/main/res/values/strings.xml` → `src/commonMain/composeResources/values/strings.xml`
- `src/main/res/values-es/strings.xml` → `src/commonMain/composeResources/values-es/strings.xml`

### `AddVehicleScreen.kt`

- `import androidx.compose.ui.res.stringResource` → `org.jetbrains.compose.resources.stringResource`
- `import com.gasguru.feature.vehicle.R` → imports explícitos de `Res.*` (17 strings)
- `R.string.X` → `Res.string.X` (15 ocurrencias)
- `org.jetbrains.compose.resources.stringResource as cmpStringResource` → unificado con `stringResource` (2 ocurrencias)
- `org.koin.androidx.compose.koinViewModel` → `org.koin.compose.viewmodel.koinViewModel`

### Tests

- `src/test/kotlin/` → `src/commonTest/kotlin/` sin cambios de código
- 27 tests pasan directamente — ya usaban fakes reales, JUnit5, Turbine y `CoroutinesTestExtension`

---

## Estructura de directorios resultante

```
feature/vehicle/src/
├── main/
│   └── AndroidManifest.xml
├── commonMain/
│   ├── composeResources/
│   │   ├── values/strings.xml
│   │   └── values-es/strings.xml
│   └── kotlin/com/gasguru/feature/vehicle/
│       ├── analytics/VehicleAnalyticsExt.kt
│       ├── di/VehicleModule.kt
│       ├── navigation/VehicleNavigation.kt
│       ├── ui/
│       │   ├── AddVehicleEvent.kt
│       │   └── AddVehicleScreen.kt
│       └── viewmodel/
│           ├── AddVehicleUiState.kt
│           └── AddVehicleViewModel.kt
└── commonTest/
    └── kotlin/.../viewmodel/
        └── AddVehicleViewModelTest.kt
```

---

## Decisiones técnicas

| Decisión | Alternativa rechazada | Motivo |
|----------|-----------------------|--------|
| Sin `expect/actual` | Lambdas platform-specific | No hay APIs Android-only en este módulo |
| Eliminar `libs.mockk` | Mantenerla | No hay ningún import ni uso en los tests del módulo |
| Unificar `cmpStringResource` con `stringResource` | Mantener el alias | La migración completa elimina la ambigüedad — no hay dos fuentes de strings |

---

## Verificación

```bash
./gradlew :feature:vehicle:assembleDebug        # compilación
./gradlew :feature:vehicle:testDebugUnitTest    # 27 tests
./gradlew :app:assembleDebug                    # integración con app
```
