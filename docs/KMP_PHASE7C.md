# KMP Phase 7C — `:feature:favorite-list-station` → Compose Multiplatform

## Objetivo

Migrar `:feature:favorite-list-station` a CMP/KMP como tercera feature de Phase 7. Es la pantalla de lista de gasolineras favoritas: sin mapas, con swipe-to-delete animado (ya KMP-ready vía compottie), con estado de localización desactivada.

## Módulos modificados

- `:feature:favorite-list-station` — migración completa a CMP
- `:app` — `NavigationBarScreen` actualizado para pasar `onOpenLocationSettings` al grafo de navegación

---

## Cambios realizados

### `feature/favorite-list-station/build.gradle.kts`

- Plugin `gasguru.android.library` + `gasguru.android.compose.library` → `gasguru.kmp.compose.library`
- Eliminado `libs.koin.androidx.compose` (incluido automáticamente por el plugin KMP via `koin-compose-viewmodel`)
- Dependencias movidas a `commonMain.dependencies {}`
- Tests ViewModel movidos a `commonTest.dependencies {}`
- `debugImplementation` en bloque raíz `dependencies {}` (fuera de `kotlin {}`)
- Añadido `compose.resources { publicResClass = true; packageOfResClass = ... }`
- `androidTarget { instrumentedTestVariant.sourceSetTree.set(...) }` para conectar androidTest con commonTest

### Estructura de directorios

```
src/main/java/         → src/commonMain/kotlin/
src/main/res/          → src/commonMain/composeResources/
src/main/AndroidManifest.xml → src/androidMain/AndroidManifest.xml
src/test/              → src/commonTest/kotlin/
```

### `FavoriteStationListScreen.kt`

- Eliminados imports Android: `android.content.Intent`, `android.provider.Settings`, `androidx.compose.ui.platform.LocalContext`, `androidx.compose.ui.res.stringResource`, `androidx.compose.ui.res.vectorResource`, `com.gasguru.feature.favorite_list_station.R`
- Cambiado `org.koin.androidx.compose.koinViewModel` → `org.koin.compose.viewmodel.koinViewModel`
- `ImageVector.vectorResource(id = R.drawable.ic_file_search)` → `vectorResource(resource = Res.drawable.ic_file_search)`
- `stringResource(id = R.string.*)` → `stringResource(resource = Res.string.*)`
- `cmpStringResource(Res.string.favorites)` (alias duplicado) → `stringResource(resource = CoreUiRes.string.favorites)` (import CMP unificado)
- `FavoriteListStationScreenRoute` ya no captura `LocalContext`; recibe `onOpenLocationSettings: () -> Unit = {}` como parámetro
- Trailing commas añadidas en todos los argumentos

### `FavoriteStationListNavigation.kt` / `FavoriteStationListGraph.kt`

- Ambas funciones de extensión reciben `onOpenLocationSettings: () -> Unit = {}` y lo pasan al composable

### `app/NavigationBarScreen.kt`

- Añadidos imports `android.content.Intent`, `android.provider.Settings`, `androidx.compose.ui.platform.LocalContext`
- `NavigationBarScreen` captura `LocalContext.current` y pasa el callback a `favoriteGraph(onOpenLocationSettings = { context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)) })`

### `FavoriteListStationViewModelTest.kt`

- Movido de `src/test/java/` → `src/commonTest/kotlin/` (sin cambios de contenido)

### `FavoriteListScreenTest.kt` (androidTest)

- Eliminado `import com.gasguru.feature.favorite_list_station.R`
- Añadidos imports de `com.gasguru.feature.favorite_list_station.generated.resources.Res`, `empty_favorites_title`, `empty_favorites_subtitle`
- `getStringResource(R.string.empty_favorites_title)` → `getCmpString(resource = Res.string.empty_favorites_title)`
- Trailing commas añadidas en llamadas a composables

---

## Verificación

```
./gradlew :feature:favorite-list-station:compileDebugKotlinAndroid → BUILD SUCCESSFUL
./gradlew :feature:favorite-list-station:testDebugUnitTest          → BUILD SUCCESSFUL
./gradlew :app:compileProdDebugJavaWithJavac                        → BUILD SUCCESSFUL
```

---

## Decisiones técnicas

### `LocalContext` para `Settings.ACTION_LOCATION_SOURCE_SETTINGS`

La pantalla muestra `LocationDisabledState` cuando la localización está desactivada. Este componente ya recibe un callback `onEnableClick: () -> Unit`. En el `FavoriteListStationScreenRoute` original se capturaba `LocalContext.current` para abrir la configuración del sistema.

En CMP, `LocalContext` solo existe en `androidMain`. La solución elegante es convertir el callback en un parámetro del composable Route, siguiendo el principio de "caller injects platform behavior". El `NavigationBarScreen` del `:app` (Android-only) es el lugar correcto para capturar el contexto y construir el intent.

Esta solución:
- Evita `expect/actual` (más simple)
- Mantiene el módulo de feature sin APIs Android en `commonMain`
- Sigue el mismo patrón que `LocationDisabledState` (ya usaba un callback lambda)

### `koin-compose-viewmodel` vs `koin-androidx-compose`

El plugin `gasguru.kmp.compose.library` ya incluye `koin-compose-viewmodel` en `commonMain`. Usar `org.koin.compose.viewmodel.koinViewModel` en lugar del `org.koin.androidx.compose.koinViewModel` Android-only permite que el ViewModel se cree desde `commonMain` sin dependencias de plataforma.

### `SwipeItem` con `iconAnimationFile`

El campo `iconAnimationFile: String?` en `StationListSwipeModel` apunta a un JSON de Lottie via `compottie` (KMP-ready). No requirió cambios.
