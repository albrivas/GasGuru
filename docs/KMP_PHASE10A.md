# KMP Phase 10A — `jvm()` en Convention Plugins

## Objetivo

Hacer que todos los módulos KMP/CMP publiquen la variante `jvm`, prerequisito para Phase 10B (migrar tests de UI a CMP con `runComposeUiTest`). Sin este target, el renderer Skia de desktop no puede ejecutar los composables en tests JVM.

## Estrategia

Añadir `jvm()` **una sola vez** en `KmpLibraryConventionPlugin` para que se propague a los ~22 módulos KMP automáticamente, en lugar de hacerlo módulo a módulo. `KmpComposeLibraryConventionPlugin` aplica internamente `gasguru.kmp.library`, por lo que hereda el target.

**Excepción**: `composeApp` queda excluido via guard `if (projectName != "composeApp")`. Es la app shell con bloque `cocoapods`, arrastra todas las features como dependencias y no ejecuta tests de UI.

## Cambios

### 1. Convention Plugin Base — `jvm()` con guard

**`build-logic/convention/src/main/java/KmpLibraryConventionPlugin.kt`**:
```kotlin
val projectName = name
extensions.configure<KotlinMultiplatformExtension> {
    androidTarget()
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    if (projectName != "composeApp") {
        jvm()
    }
}
```

El bloque `tasks.withType<Test> { useJUnitPlatform() }` ya existente cubre `jvmTest` automáticamente.

### 2. Eliminar `jvm()` manual redundante

`core/model/build.gradle.kts` y `core/database/build.gradle.kts` ya declaraban `jvm()` manualmente. Se eliminó la línea para evitar duplicado. Se conservan en core:database: el source set `jvmTest` y `add("kspJvm", libs.androidx.room.compiler)` — Room KSP genera el actual del constructor de la DB para JVM.

### 3. Actuals no-op en `jvmMain`

El target `jvm()` existe **únicamente para tests**; no es un artefacto de producción. Los actuals de APIs platform-specific son no-ops mínimos:

| Módulo | Declaración `expect` | Actual JVM |
|--------|---------------------|------------|
| `core/common` | `ioDispatcher`, `getAppVersion` | `Dispatchers.IO`, `"0.0.0 (0)"` |
| `core/network` | `routesPlugin(packageName)` | `createClientPlugin("RoutesPlugin") {}` |
| `core/ui` | `ConfigureDialogSystemBars(invertColors)` | no-op composable |
| `core/ui` | `fullScreenDialogProperties()` | `DialogProperties(usePlatformDefaultWidth = false)` |
| `core/ui` | `rememberInAppReviewManager()` | `null` |
| `core/uikit` | `SystemBarsEffect(darkTheme)` | no-op composable |
| `core/uikit` | `ThemePreviews` (annotation class) | `@Target(FUNCTION) actual annotation class ThemePreviews()` |
| `core/uikit` | `Modifier.maestroTestTag(tag)` | `this.testTag(tag)` |
| `feature/detail-station` | `rememberNavigateToMapsAction` | `remember { { _ -> } }` |
| `feature/detail-station` | `rememberNotificationPermissionRequester` | `remember { {} }` |
| `feature/detail-station` | `rememberShareAction` | `remember { { _ -> } }` |
| `feature/station-map` | `rememberLocationPermissionState` | `LocationPermissionState(false, false, {}, {})` |
| `feature/station-map` | `PlatformMapView(...)` | no-op composable vacío |

**Nota sobre `ThemePreviews`**: el actual JVM es idéntico al de iOS (sin `@Preview`), no al de Android. En JVM no existe `androidx.compose.ui.tooling.preview.Preview`.

**Nota sobre `ModifierUtils`**: el fichero jvmMain se llamó `ModifierUtilsActual.kt` para evitar "Duplicate JVM class name `ModifierUtilsKt`": `commonMain/ModifierUtils.kt` tiene funciones concretas adicionales además del `expect`, lo que generaba colisión de nombre de clase JVM al usar el mismo nombre de fichero.

### 4. Dependencias expuestas como `api` para classpath JVM

Los módulos KMP que usaban `kotlinx.coroutines.flow.Flow` sin declararlo explícitamente compilaban bien en Android/iOS pero fallaban en JVM (resolución Gradle estricta — `implementation` no expone el classpath al consumidor).

- **`core/common/build.gradle.kts`**: `kotlinx.coroutines.core` de `implementation` → `api`.
- **`navigation/build.gradle.kts`**: `jetbrains.navigation.compose` de `implementation` → `api`.
- **`KmpComposeLibraryConventionPlugin`** + **`gradle/libs.versions.toml`**: añadido `org.jetbrains.androidx.lifecycle:lifecycle-runtime-compose` como `api` en commonMain del plugin (alias: `jetbrains-lifecycle-runtime-compose`). Necesario para `collectAsStateWithLifecycle`.

### 5. Detekt `setSource`

**`build.gradle.kts` (raíz)**: añadidas rutas `src/jvmMain/kotlin` y `src/jvmTest/kotlin` al `setSource` del task `codeCheck`.

## Lecciones Aprendidas

### No usar `commonJvmAndroid` como source set intermedio
`androidMain { dependsOn(commonJvmAndroid) }` interfiere con el **default hierarchy template** de KMP, rompiendo la resolución de actuals iOS. Actuals separados en `jvmMain/` directamente.

### Classpath JVM más estricto que Android/iOS
`implementation` no expone el classpath al consumidor en el target JVM. Android e iOS tienen comportamiento más permisivo. Solución de raíz: `api` en las dependencias transversales del módulo base.

### Conflicto `ModifierUtilsKt` en KMP/JVM
Fichero `commonMain/ModifierUtils.kt` con funciones concretas + `expect`, más `jvmMain/ModifierUtils.kt` con el `actual` → doble generación de `ModifierUtilsKt`. Renombrar el actual jvmMain resuelve el conflicto.

## Siguiente Paso

**Phase 10B** — migrar los tests de UI de `androidTest` a `commonTest` con `runComposeUiTest`, reescribir `BaseTest`, y eliminar la exclusión `GasGuruSearchBarContentTest`.
