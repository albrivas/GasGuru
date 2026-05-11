# KMP Phase 6C — `:core:components` → Compose Multiplatform

## Objetivo

Migrar el módulo `:core:components` (que contiene el `GasGuruSearchBar`) a Compose Multiplatform. Tras esta fase, todos los módulos `:core:*` están en KMP/CMP. Es la primera vez que un ViewModel vive en `commonMain`.

## Módulos modificados

- `:core:components` — migración completa a CMP
- `build-logic/.../KmpComposeLibraryConventionPlugin.kt` — ampliación para cubrir ViewModel en commonMain
- `gradle/libs.versions.toml` — nuevas dependencias KMP

---

## Cambios en `KmpComposeLibraryConventionPlugin`

Se añadieron tres dependencias a `commonMain` del convention plugin para que todos los módulos CMP las hereden automáticamente:

| Dependencia | Artifact | Por qué |
|---|---|---|
| `compose.materialIconsExtended` | CMP Material Icons | Necesario para iconos en composables commonMain |
| `jetbrains.lifecycle.viewmodel.compose` | `org.jetbrains.androidx.lifecycle:lifecycle-viewmodel-compose:2.9.0` | ViewModel, viewModelScope, SavedStateHandle y collectAsStateWithLifecycle en commonMain |
| `koin.compose.viewmodel` | `io.insert-koin:koin-compose-viewmodel:4.1.1` | `koinViewModel()` en composables KMP |

Estas dependencias se añaden en `commonMain.dependencies` para que no haya que declararlas en cada módulo individual de Features en Phase 7.

---

## Primer ViewModel en commonMain

`GasGuruSearchBarViewModel` es el **primer ViewModel en commonMain** del proyecto. El patrón es:

```kotlin
// commonMain
class GasGuruSearchBarViewModel(
    private val savedStateHandle: SavedStateHandle,  // org.jetbrains.androidx.lifecycle
    ...
) : ViewModel() {                                   // androidx.lifecycle.ViewModel (KMP)
    val searchQuery = savedStateHandle.getStateFlow(key = SEARCH_QUERY, initialValue = "")
}
```

Las clases `ViewModel`, `viewModelScope` y `SavedStateHandle` están en el mismo paquete `androidx.lifecycle.*` gracias al artefacto KMP de JetBrains (`org.jetbrains.androidx.lifecycle:lifecycle-viewmodel-compose`). Los imports no cambian.

### Inyección en composables KMP

```kotlin
// commonMain — antes (Android-only)
import org.koin.androidx.compose.koinViewModel

// commonMain — después (KMP)
import org.koin.compose.viewmodel.koinViewModel
```

### collectAsStateWithLifecycle en commonMain

```kotlin
// El import es idéntico; JetBrains lifecycle-viewmodel-compose lo provee en KMP
import androidx.lifecycle.compose.collectAsStateWithLifecycle
```

---

## Migración de strings a composeResources

Solo `hint_search_bar` era utilizado en código. Las otras 4 entradas (`label_suggestion`, `label_empty_suggestions`, `label_recent`, `label_empty_recents`) eran recursos muertos: `SearchList` en `:core:uikit` ya tiene sus propias strings para esos textos. No se migraron.

Ruta: `src/commonMain/composeResources/values/strings.xml` y `values-es/strings.xml`.

Uso en composable:
```kotlin
import com.gasguru.core.components.generated.resources.Res
import com.gasguru.core.components.generated.resources.hint_search_bar
import org.jetbrains.compose.resources.stringResource

// En composable:
stringResource(Res.string.hint_search_bar)
```

---

## Limpieza

- **Eliminado `state/RecentSearchQueriesUiState.kt`**: era un duplicado local no referenciado por ningún código de producción. El código siempre importó `com.gasguru.core.ui.RecentSearchQueriesUiState` (commonMain de `:core:ui`).
- **Eliminados 4 strings muertos** de `src/main/res/values/strings.xml`.

---

## Tests

`GasGuruSearchBarViewModelTest` se movió de `src/test/kotlin/` a `src/commonTest/kotlin/`. El test usa JUnit5 + Turbine + `CoroutinesTestExtension` (de `:core:testing`, ya KMP). `SavedStateHandle` resuelve correctamente en el JVM target de commonTest.

El test añade `@DisplayName` a nivel de clase (alineado con las reglas del proyecto) y adapta el formato `GIVEN/WHEN/THEN` al bloque triple-comillas.

---

## Deuda iOS heredada

La compilación de iOS (`compileKotlinIosSimulatorArm64`) falla debido a dependencias preexistentes de `:core:uikit`:

- `maestroTestTag` usa `testTagsAsResourceId` (Android-only semántica) en commonMain.
- `ThemePreviews` usa `@Preview` de `androidx.compose.ui.tooling.preview` en commonMain.

Estos blockers se heredan sin generar regresión: son idénticos a los que ya existían tras Phase 6B. Se abordarán al inicio de Phase 7 o en un PR de limpieza previo.

---

## Verificación

```bash
./gradlew :core:components:assembleDebug          # ✅ compila Android
./gradlew :core:components:testDebugUnitTest      # ✅ 8 tests pasan
./gradlew :feature:search:assembleDebug           # ✅ consumidor OK
./gradlew :feature:station-map:assembleDebug      # ✅ consumidor OK
./gradlew :app:assembleDebug                      # ✅ app completa OK
./gradlew :app:testProdDebugUnitTest              # ✅ KoinModulesTest OK
```

---

## Referencia en grafo de dependencias

```
Phase 6C añadida tras 6B:
  :core:components
    commonMain → :core:analytics, :core:ui, :core:domain, :core:model, :core:uikit, :core:common
    commonMain → lifecycle-viewmodel-compose (KMP), koin-compose-viewmodel
```
