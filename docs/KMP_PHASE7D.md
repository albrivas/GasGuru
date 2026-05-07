# KMP Phase 7D — `:feature:search` → Compose Multiplatform

## Objetivo

Migrar `:feature:search` a CMP/KMP como cuarta feature de Phase 7. Es la pantalla de búsqueda de lugares: se muestra como dialog, delega toda su UI y lógica al `GasGuruSearchBar` de `:core:components` (ya en `commonMain` desde Phase 6C). El módulo en sí tiene solo 3 archivos y ~75 líneas de código.

## Módulos modificados

- `:core:ui` — `ConfigureDialogSystemBars` movido a `expect/actual` (commonMain + androidMain + iosMain)
- `:feature:search` — migración completa a CMP

---

## Cambios realizados

### `core/ui/src/commonMain/kotlin/com/gasguru/core/ui/DialogSystemBars.kt` (nuevo)

```kotlin
@Composable
expect fun ConfigureDialogSystemBars(invertColors: Boolean = false)
```

### `core/ui/src/androidMain/kotlin/com/gasguru/core/ui/DialogSystemBars.kt` (modificado)

Añadido `actual` a la firma. Sin cambios en la lógica (`LocalView` + `DialogWindowProvider` + `WindowCompat`).

### `core/ui/src/iosMain/kotlin/com/gasguru/core/ui/DialogSystemBars.kt` (nuevo)

```kotlin
@Composable
actual fun ConfigureDialogSystemBars(invertColors: Boolean) {
    // V1: no-op. V2: configure system bars via SwiftUI host.
}
```

### `feature/search/build.gradle.kts`

- Plugin `gasguru.android.library` + `gasguru.compose.library` + `gasguru.secrets.google` → `gasguru.kmp.compose.library`
- `@file:OptIn(ExperimentalComposeLibrary, ExperimentalKotlinGradlePluginApi)` añadido
- `compose.resources { publicResClass = true; packageOfResClass = "com.gasguru.feature.search.generated.resources" }`
- Dependencias movidas a `commonMain.dependencies {}`
- Eliminadas dependencias muertas: `kotlin.coroutines.play`, `maps.compose`, `play.services.maps`, `kotlinx.serialization.json` (transitiva por `:navigation`), `core.domain`, `core.common`
- Eliminado bloque `androidTestImplementation` (no hay tests)

### `feature/search/proguard-rules.pro`

- Eliminadas reglas de Hilt (el proyecto usa Koin)

### Estructura de directorios

```
src/main/java/       → src/commonMain/kotlin/
src/main/AndroidManifest.xml → src/androidMain/AndroidManifest.xml
```

Sin cambios en el contenido de los 3 archivos Kotlin — ya eran 100% KMP-compatibles.

---

## Verificación

```
./gradlew :core:ui:compileDebugKotlinAndroid                → BUILD SUCCESSFUL
./gradlew :feature:search:compileDebugKotlinAndroid         → BUILD SUCCESSFUL
./gradlew :feature:route-planner:assembleDebug              → BUILD SUCCESSFUL
./gradlew :feature:vehicle:assembleDebug                    → BUILD SUCCESSFUL
./gradlew :feature:detail-station:assembleDebug             → BUILD SUCCESSFUL
./gradlew :app:assembleDebug                                → BUILD SUCCESSFUL
./gradlew :app:testProdDebugUnitTest                        → BUILD SUCCESSFUL
```

---

## Decisiones técnicas

### `ConfigureDialogSystemBars` → `expect/actual` en `:core:ui`

El helper `ConfigureDialogSystemBars` vivía en `core/ui/src/androidMain/` y usa `LocalView`, `DialogWindowProvider` y `WindowCompat` (APIs Android-only). Lo usan 4 features: `search`, `route-planner`, `vehicle` y `detail-station`.

Las opciones eran:
1. **`expect/actual`** en `:core:ui` — desbloquea las 4 features en un único cambio.
2. **Mantener `SearchNavigation.kt` en `androidMain`** — cambio mínimo pero deja el patrón sin resolver para las 3 features siguientes.
3. **Lift como lambda desde app** — válido para un único consumidor, pero con 4 consumidores añade ruido al grafo de navegación del app.

Se eligió la opción 1: `expect/actual` en `:core:ui`. El `actual` iOS es un no-op en V1 (la app iOS configurará las system bars a nivel SwiftUI host). Este patrón ya existe en el mismo módulo (ver `InAppReviewManager`).

### Sin tests nuevos

El módulo no tiene ViewModel propio ni lógica de presentación propia. Toda la lógica de búsqueda (queries, estado de búsqueda, historial reciente) reside en `GasGuruSearchBarViewModel` dentro de `:core:components`, que ya tiene cobertura de tests en `commonTest`. Añadir tests a este módulo sería testear código de otro módulo.
