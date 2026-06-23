# KMP Phase 10B — Tests de UI en CMP (headless en `jvmTest`)

## Objetivo

Migrar los tests de UI Compose de `androidTest`/`androidInstrumentedTest` a `commonTest`, para que
corran **headless en `jvmTest` sin emulador** usando el renderer Skia/Skiko de Compose Desktop.

Prerequisito: Phase 10A (`jvm()` en convention plugins). Sin el target JVM publicado en todos los módulos,
el renderer Skia no puede ejecutar composables en tests JVM.

## Módulos migrados

| Módulo | Tests migrados | Fuente original |
|--------|---------------|-----------------|
| `:core:components` | GasGuruSearchBarContentTest (6 tests) | `commonTest` — prueba de concepto (Phase 10A commit #553) |
| `:core:uikit` | GasGuruAlertDialogTest (4), TankCostCardTest (7), FuelListSelectionTest (6), SelectedItemTest (4), FuelStationItemTest (1), RouteNavigationCardTest (3), FuelTypeChipTest (3), NumberWheelPickerTest (4) | `src/androidTest/` |
| `:feature:profile` | ProfileScreenTest (4) | `src/androidTest/` |
| `:feature:favorite-list-station` | FavoriteListScreenTest (3) | `src/androidTest/` |
| `:feature:onboarding` | OnboardingFuelPreferencesTest (2) | `src/androidInstrumentedTest/` |
| `:feature:detail-station` | DetailStationScreenTest (1) | `src/androidInstrumentedTest/` |

**Total**: 42 tests de UI ahora corren sin emulador en ~40 segundos (vs ~10 min con emulador).

## Patrón de migración

### 1. `build.gradle.kts` — setup idéntico para todos los módulos

```kotlin
@file:OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)

kotlin {
    sourceSets {
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(projects.core.testing)
            implementation(compose.uiTest)       // ← nuevo
        }
        jvmTest.dependencies {
            implementation(compose.desktop.currentOs)  // ← nuevo: Skiko desktop renderer
        }
    }
}

// Excluir los tests de UI de todas las tareas Test EXCEPTO jvmTest.
// En testDebugUnitTest (Android unit), compose.uiTest resuelve al artefacto Android
// que requiere instrumentación → NullPointerException si se intenta correr sin emulador.
// JetBrains lo documenta: "you cannot run common CMP tests using android (local) configurations".
tasks.withType<Test>().configureEach {
    if (name != "jvmTest") {
        exclude(
            "**/MiComponenteTest*",
            // ... un patrón por cada clase de test UI del módulo
        )
    }
}
```

> **⚠️ No añadir `instrumentedTestVariant.sourceSetTree.set(test)`** aunque la documentación de KMP
> lo mencione para conectar `commonTest` con `connectedAndroidTest`. Si `commonTest` mezcla tests de
> UI con tests unitarios que usan nombres en backticks con espacios (`` fun `GIVEN ... WHEN ...`() ``),
> el paso `dexBuilderDebugAndroidTest` falla con
> `D8: Space characters in SimpleName ... are not allowed prior to DEX version 040`
> cuando `minSdk < 35`. Como GasGuru usa `minSdk = 26` y el objetivo real es headless en `jvmTest`,
> ese routing no es necesario.

### 2. Código del test — de `BaseTest` a `runComposeUiTest`

**Antes** (Android, JUnit5):
```kotlin
class MiComponenteTest : BaseTest() {
    @Test
    @DisplayName("GIVEN ... WHEN ... THEN ...")
    fun miTest() = extension.use {
        setContent { MyApplicationTheme { MiComponente(model = ...) } }
        onNodeWithTag("mi_tag").assertIsDisplayed()
    }
}
```

**Después** (commonTest, kotlin.test):
```kotlin
@OptIn(ExperimentalTestApi::class)
class MiComponenteTest {
    @Test
    fun miTest() = runComposeUiTest {
        setContent { MyApplicationTheme { MiComponente(model = ...) } }
        onNodeWithTag("mi_tag").assertIsDisplayed()
    }
}
```

Cambios clave:
- Sin `BaseTest`, sin `extension.use { }`.
- `@Test` de `kotlin.test` (no `org.junit.jupiter.api`).
- `assertTrue`/`assertEquals` de `kotlin.test` (no `org.junit.jupiter.api.Assertions`).
- Eliminar `@DisplayName` (kotlin.test no lo soporta; `[[feedback_displayname_kmp.md]]`).
- `@OptIn(ExperimentalTestApi::class)` en la clase.
- APIs de aserción (`onNodeWithTag`, `performClick`, `assertIsDisplayed`, `useUnmergedTree`,
  `SemanticsMatcher`, `fetchSemanticsNode().boundsInRoot`, etc.) son idénticas.

### 3. Resolución de strings de recurso en commonTest

No existe un `BaseComposeTest` compartido porque **en KMP solo el classpath `main` de un módulo se
expone a consumidores** — un source set `test` de `core:testing` no es visible desde otros módulos.
Además, no hay API síncrona para resolver `StringResource` fuera de un contexto composable (issue
JetBrains [#4349](https://github.com/JetBrains/compose-multiplatform/issues/4349) abierto).

Dos estrategias según el caso:

**Caso A — String como dato del modelo** (el componente renderiza lo que le pasamos):
```kotlin
// Usar el literal del string en lugar de getCmpString(Res.string.preview_fuel_type)
FuelItemModel(nameRes = "Gasoline 95")  // valor de preview_fuel_type en values/strings.xml
```

**Caso B — String renderizado internamente por el componente** (vía `stringResource(...)` dentro del
propio composable, no controlado desde el test):
```kotlin
fun fullTankCostLabelIsShown() = runComposeUiTest {
    var label = ""
    setContent {
        label = stringResource(Res.string.full_tank_cost)  // capturar dentro de la composición
        MyApplicationTheme { TankCostCard(model = defaultModel()) }
    }
    onNodeWithText(label).assertIsDisplayed()
}
```

**Caso especial — `getAppVersion()` en jvmMain** devuelve `"0.0.0 (0)"` (stub no-op). Si el
componente renderiza la versión de la app:
```kotlin
var versionText = ""
setContent {
    versionText = stringResource(ProfileRes.string.version, "0.0.0 (0)")
    ProfileScreen(...)
}
```

## Por qué `commonTest` y no `jvmTest` para las clases de test

El estándar recomendado por JetBrains y la comunidad KMP es poner los tests de UI en `commonTest`
(no en `jvmTest`). Las razones:

- `jvmTest` es el source set para la **dependencia** del renderer desktop (`compose.desktop.currentOs`),
  no para las clases de test.
- El bloque `exclude` es el idioma estándar para evitar que `testDebugUnitTest` los ejecute
  (limitación documentada por JetBrains, no un workaround).
- Tests en `commonTest` podrían reejecutarse en dispositivo real vía `connectedAndroidTest` usando
  `instrumentedTestVariant.sourceSetTree.set(...)`, pero **no se usa en este proyecto** porque
  mezclar ese routing con tests unitarios que usan nombres en backticks rompe D8 con `minSdk < 35`
  (ver sección Lecciones aprendidas).

## Lecciones aprendidas

- `isDisplayed()` en compose.ui.test **sí lanza** AssertionError si el nodo no existe (no es soft-assert).
- Los ViewModel tests en `commonTest` **no** se excluyen del bloque `tasks.withType<Test>` — solo los UI tests.
- Borrar completamente `src/androidTest/` / `src/androidInstrumentedTest/` una vez movidos los tests.
- No es posible compartir código de test entre módulos KMP vía source sets de test (`commonTest`):
  los consumidores solo ven el classpath `main`. Por eso los helpers de test viven en `commonMain`/
  `androidMain` de `core:testing` (p.ej. `BaseTest` en `androidMain`).
- **`instrumentedTestVariant.sourceSetTree.set(test)` + backticks + `minSdk < 35` → D8 rompe**:
  no añadir este bloque si `commonTest` contiene tests unitarios con nombres en backticks con espacios.
  `assembleAndroidTest` intenta dexar esas clases y D8 prohíbe espacios hasta DEX 040 (`minSdk >= 35`).
  El bloque `exclude` no ayuda porque solo afecta a tareas de *ejecución*, no al `dexBuilder`.
