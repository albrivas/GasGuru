# KMP Phase 8C — App shell (splash + navegación) a `:composeApp/commonMain`

## Objetivo

Mover el armazón de la app (splash + navegación raíz + bottom bar) desde `:app` a `:composeApp/commonMain` para que sea compartible entre Android e iOS. `:app` queda reducido a `MainActivity` + `GasGuruApplication` con la inicialización Android-only (splash screen, deep links, OneSignal, Mixpanel, Clarity, WorkManager, widget).

---

## Estructura creada

```
composeApp/src/commonMain/kotlin/com/gasguru/
├── composeApp/
│   └── App.kt                              # Entry point: inyecta Koin, instala theme, renderiza GasGuruApp
├── splash/
│   ├── SplashViewModel.kt                  # ViewModel KMP (kotlin.time.Clock en lugar de System.currentTimeMillis)
│   └── SplashUiState.kt
├── ui/
│   ├── GasGuruApp.kt                       # Recibe onOpenLocationSettings lambda
│   ├── GasGuruAppState.kt
│   └── NavigationBarScreen.kt              # Propaga onOpenLocationSettings hacia favoriteGraph
├── navigation/
│   ├── navigationbar/
│   │   ├── NavigationBar.kt                # Usa StringResource en lugar de R.string
│   │   ├── NavigationBarState.kt
│   │   ├── NavigationBarNavigation.kt
│   │   └── route/
│   │       ├── NavigationBarRoute.kt
│   │       └── TopLevelRoutes.kt           # labelRes: Int → label: StringResource
│   ├── handler/NavigationHandler.kt
│   ├── graphs/RoutePlannerNavigationGraph.kt
│   └── root/GasGuruNavHost.kt              # navigationBarHost(onOpenLocationSettings = ...)
└── di/AppShellModule.kt                    # SplashViewModel registrado vía koin viewModel DSL

composeApp/src/commonMain/composeResources/
├── values/strings.xml                      # map_nav, list_nav, profile_nav, not_connected
└── values-es/strings.xml

composeApp/src/commonTest/kotlin/com/gasguru/splash/
└── SplashViewModelTest.kt                  # JUnit5 + Turbine + fakes (sin MockK)
```

---

## Cambios en `:app`

| Antes | Después |
|---|---|
| `SplashViewModel`, `SplashUiState`, `GasGuruApp`, `GasGuruAppState`, `NavigationBarScreen`, `NavigationBar*`, `GasGuruNavHost`, `NavigationHandler`, `RoutePlannerNavigationGraph`, `NavigationBarRoute`, `TopLevelRoutes` viven en `:app/src/main/java/` | Eliminados de `:app`, viven en `:composeApp/src/commonMain/kotlin/` |
| `R.string.{map_nav,list_nav,profile_nav,not_connected}` | Movidos a `composeApp/composeResources` (siguen siendo `app_name` solo en `:app`) |
| `AppModule.kt` provee MixpanelAPI + WidgetFavoriteSyncManager + SplashViewModel | Sólo MixpanelAPI + WidgetFavoriteSyncManager. `SplashViewModel` se mueve a `appShellModule()` en composeApp |
| `MainActivity` inyecta NetworkMonitor, IsLocationEnabledUseCase, etc. y construye `GasGuruApp` directamente | Llama a `App(themeMode, onOpenLocationSettings, startDestination)` desde `:composeApp` con la lambda `::openLocationSettings` |
| `GasGuruApplication` carga `appModule()` | Carga `appModule()` + `appShellModule()` |
| `app/build.gradle.kts` | Añadido `implementation(projects.composeApp)` |

`MainActivity` mantiene: `installSplashScreen()`, lifecycle splash gating, `enableEdgeToEdge()`, deep link handling (`station_id` intent extra), `trackAppOpened` analytics, `returnedFromBackground` flag.

---

## Decisiones de diseño

### `App()` inyecta dependencias internamente con `koinInject()`

`App()` recibe sólo lo que es plataforma-específico (`themeMode`, `onOpenLocationSettings`, `startDestination`) y resuelve internamente vía Koin todo lo demás (`NetworkMonitor`, `IsLocationEnabledUseCase`, `GetUserDataUseCase`, `NavigationManager`, `DeepLinkStateHolder`, `AnalyticsHelper`). Esto reduce el acoplamiento entre el host iOS (cuando se cablee en 8D) y el árbol Compose.

### `onOpenLocationSettings` como lambda elevada

Patrón ya validado en Phase 7C (`feature/favorite-list-station`): el composable común recibe una lambda. En Android la implementa `MainActivity` lanzando `Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)`. En iOS (8D+) abrirá `Settings.app` vía `UIApplication.openURL`.

### `TopLevelRoutes` usa `StringResource` en lugar de `@StringRes Int`

`labelRes: Int` era Android-only (R class). Reemplazado por `label: StringResource` apuntando a `composeApp.composeResources`. `NavigationBar` consume con `stringResource(destination.label)` de `org.jetbrains.compose.resources`.

### `SplashViewModel` usa `kotlin.time.Clock` en lugar de `System.currentTimeMillis()`

`System.currentTimeMillis()` y `java.util.concurrent.TimeUnit` no existen en commonMain. Sustituidos por `Clock.System.now().toEpochMilliseconds()` y `kotlin.time.Duration.Companion.minutes`.

### `MainViewController` en iOS sigue como placeholder

`App()` requiere Koin inicializado. Mientras no se cablee la inicialización Koin desde Swift (Phase 8D), `MainViewController` renderiza una pantalla placeholder ("GasGuru — iOS preview") usando `MyApplicationTheme`. Compila para iOS sin necesidad de `koinInject` en runtime.

---

## Comandos de verificación

```bash
# Android (Phase 8C cubierto)
./gradlew :composeApp:compileDebugKotlinAndroid       # ✅
./gradlew :composeApp:testDebugUnitTest               # ✅ (SplashViewModelTest)
./gradlew :app:compileProdDebugKotlin                 # ✅
./gradlew :app:testProdDebugUnitTest                  # ✅ (KoinModulesTest incluye appShellModule)
./gradlew :app:assembleProdDebug                      # ✅
./gradlew :app:assembleProdRelease                    # ✅ (R8 minificación incluida)
./gradlew :composeApp:codeCheck :app:codeCheck        # ✅

# iOS (bloqueado por pod install de Mixpanel — pre-existente, Phase 4a)
./gradlew :composeApp:compileKotlinIosSimulatorArm64  # requiere `cd iosApp && pod install`
```

---

## Qué NO se migra en esta sub-fase

- **Inicialización Koin desde iOS** — sub-fase 8D
- **OneSignal / Mixpanel / Clarity / WorkManager / Widget** — siguen en `GasGuruApplication.kt` Android
- **`StationSyncWorker`, `WidgetFavoriteSyncManager`** — siguen en `:app` (WorkManager y AppWidget son Android-only)
- **`:auto:common`, `:feature:widget`** — Android-only permanente
