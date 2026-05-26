# KMP Phase 8D — Inicialización Koin desde iOS

## Objetivo

Cablear Koin en `composeApp/iosMain` y renderizar la app real (`App()`) desde `MainViewController`, desbloqueando la ejecución iOS completa tras el armazón de las fases 8A-8C.

## Cambios principales

### 1. `KoinInit.kt` (nuevo — `composeApp/iosMain`)

Función `initKoin()` equivalente a `GasGuruApplication.initKoin()` adaptada a iOS:

| Android | iOS |
|---------|-----|
| `analyticsModule` (Mixpanel) | `analyticsModuleIos` (NoOp) |
| `databaseModule` (Android) | `databaseModule` (iOS — NSDocumentDirectory) |
| `notificationModule` (Android — OneSignal) | `notificationModule` (iOS — no-op stubs) |
| `networkModule()` + `placesModule()` | ❌ omitidos (no routing V1 iOS) |
| `androidDataModule()` + `dataProviderModule()` | `iosDataModule()` (stubs) |
| `appModule()` (Mixpanel, Widget) | ❌ omitido (Android-only) |
| `remoteDataSourceModule()` (en `:app/prod`) | Inline: `module { single<RemoteDataSource> { get<SupabaseRemoteDataSource>() } }` |

Módulos comunes incluidos tal cual: `coroutineModule`, `daoModule`, `supabaseModule`, `commonDataModule()`, `domainModule()`, `navigationModule()`, `appShellModule()`, todos los features, `searchBarModule()`.

### 2. `MainViewController.kt` — renderiza `GasGuruIosApp()` real

Wrapper composable `GasGuruIosApp()` en `iosMain`:
- Inyecta `SplashViewModel` vía `koinViewModel<SplashViewModel>()`
- Colecta `themeMode` con `collectAsStateWithLifecycle()`
- Pasa `onOpenLocationSettings` que abre `UIApplication.openURL("app-settings:")`

### 3. `iOSApp.swift` — llama `initKoin()` una vez

```swift
init() {
    KoinInitKt.doInitKoin()  // Kotlin/Native prefija 'init' con 'do'
}
```

### 4. `fullScreenDialogProperties()` — expect/actual en `core.ui`

`decorFitsSystemWindows` no existe en el API común de CMP `DialogProperties`.

| Platform | Resultado |
|----------|-----------|
| Android (`actual`) | `DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false)` |
| iOS (`actual`) | `DialogProperties(usePlatformDefaultWidth = false)` |

Usado en 4 navegaciones de diálogo: `detail-station`, `route-planner`, `vehicle`, `search`.

### 5. `LocalAnalyticsHelper` — movido a `core.ui/commonMain`

`core.analytics` es un módulo `kmp.library` sin Compose. `LocalAnalyticsHelper` (un `staticCompositionLocalOf`) pertenece a un módulo con Compose disponible en `commonMain`. Se movió a `core.ui/commonMain` y se añadió `implementation(projects.core.analytics)` en `core.ui/commonMain.dependencies`.

Import actualizado en: `App.kt`, `DetailStationScreen.kt`.

### 6. `rememberInAppReviewManager()` — expect/actual en `core.ui`

| Source set | Implementación |
|------------|----------------|
| `commonMain` | `expect fun rememberInAppReviewManager(): InAppReviewManager?` |
| `androidMain` | `actual` con `ReviewManagerFactory` + `LocalContext` |
| `iosMain` | `actual` retorna `InAppReviewManager()` (no-op V1) |

### 7. Correcciones adicionales de compilación iOS (pre-existentes)

| Archivo | Problema | Fix |
|---------|----------|-----|
| `ProfileScreenPreviews.kt` | `import androidx.compose.ui.tooling.preview.PreviewParameterProvider` (Android-only) | → `import org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider` |
| `PlatformMapView.kt` (iosMain) | `GasGuruTheme.colors.surface` no existe | → `GasGuruTheme.colors.neutral100` |
| `NavigationBarState.kt` | `val topLevelRoutes = listOf(...)` infiere `List<Any>` en KMP | → `val topLevelRoutes: List<TopLevelRoutes> = listOf(...)` |

## Verificación

```bash
./gradlew :composeApp:compileKotlinIosSimulatorArm64  # BUILD SUCCESSFUL ✅
./gradlew :composeApp:compileDebugKotlinAndroid         # BUILD SUCCESSFUL ✅
./gradlew :app:assembleProdDebug                        # BUILD SUCCESSFUL ✅
./gradlew :composeApp:testDebugUnitTest                 # BUILD SUCCESSFUL ✅
```

## Limitaciones conocidas (V1 iOS)

- **Mapa vacío**: `PlatformMapView` sigue siendo un `Box` de color plano — se implementa en Phase 8F con `MKMapView`.
- **Localización**: `LocationTrackerIos`, `NetworkMonitorIos`, `GeocoderAddressIos` son stubs — se implementan en Phase 8C.1 con CoreLocation / Network.framework.
- **Rutas**: `GetRouteUseCase` falla lazy si se invoca — `networkModule` no incluido en iOS V1; la pantalla Route Planner carga pero no calcula rutas.
- **Push notifications**: `OneSignalManagerIos` es no-op — se aborda en Phase 8E.
- **InAppReview**: `InAppReviewManager` iOS es no-op — se aborda en Phase 8E con `SKStoreReviewController`.

## Siguiente fase

**Phase 8C.1**: implementar `LocationTrackerIos` (CoreLocation), `NetworkMonitorIos` (Network.framework), `GeocoderAddressIos` (CLGeocoder), `PlacesRepositoryIos` (MKLocalSearch).
