# Plan de Migración GasGuru: KMP/CMP

## Contexto

GasGuru es una app Android nativa (Kotlin + Jetpack Compose) con arquitectura Clean Architecture modular (22 módulos). El objetivo es migrar a Kotlin Multiplatform (KMP) y Compose Multiplatform (CMP) para soportar **Android + Android Auto + iOS** (V1), dejando la puerta abierta a **Apple CarPlay + Web** (V2).

El proyecto ya tiene un módulo KMP (`:core:network`) que sirve como referencia, un convention plugin `gasguru.kmp.library` funcional con targets Android + iOS (x64/arm64/simulatorArm64), y un stack técnico donde la mayoría de librerías ya tienen soporte KMP oficial.

---

## Análisis del Ecosistema KMP (estado actual)

| Librería | Soporte KMP | Versión KMP | Decisión | Impacto |
|----------|-------------|-------------|----------|---------|
| **Room** | Oficial (Google) | 2.7.0+ | **Quedarse con Room KMP** — 13 migraciones existentes, soporte oficial estable | Migrar type converters de Moshi a kotlinx-serialization |
| **Koin** | Oficial | 4.1.x | **Quedarse** — ya en uso, soporte KMP completo | Separar módulos DI en commonMain/androidMain/iosMain |
| **Ktor** | Nativo KMP | 3.4.0 | **Quedarse** — ya migrado en `:core:network` | Ninguno adicional |
| **ViewModel** | Oficial (Google) | 2.8.0+ (actual: 2.9.4) | **Quedarse con Jetpack ViewModel** — ya KMP, `viewModelScope` multiplataforma | Mover ViewModels a commonMain |
| **Navigation** | JetBrains contribuye a AndroidX Nav | CMP 1.8.0+ | **Mantener NavigationManager propio** — ya es Kotlin puro (SharedFlow), desacoplado de Jetpack Nav | Mover interfaces a commonMain, hosts platform-specific |
| **Compose MP** | Oficial (JetBrains) | 1.10.0 | **Adoptar CMP** — iOS estable desde 1.8.0, Web beta | Requiere nuevo convention plugin |
| **Supabase-kt** | Oficial comunidad | 3.x (Ktor 3) | **Quedarse** — ya KMP-ready | Cambiar engine de ktor-client-android a okhttp/darwin |
| **Arrow** | Oficial | 2.0+ | **Quedarse** — funciona en commonMain sin cambios | Ninguno |
| **OneSignal** | **NO tiene SDK KMP** | N/A | **expect/actual sobre interfaz existente** — SDK nativo en cada plataforma | iOS: integrar OneSignal iOS SDK via Swift/cinterop |
| **Firebase** | **NO oficial** — GitLive SDK comunidad | firebase-kotlin-sdk | **GitLive para Crashlytics KMP** o expect/actual | Medio plazo, no bloquea V1 |
| **Google Maps** | **NO KMP** — maps-compose es Android-only | N/A | **Platform-specific**: Google Maps (Android) + MapKit (iOS) via expect/actual Composables | Mayor esfuerzo en Phase 7 |
| **Places SDK** | **NO KMP** | N/A | **expect/actual**: Google Places (Android) + MKLocalSearch (iOS) | Implementar en Phase 4 |
| **Lottie** | **NO CMP** | N/A | **Reemplazar con compottie** (KMP Lottie) o Compose animations | Phase 6 |
| **ConstraintLayout Compose** | **NO KMP** | N/A | **Refactorizar a layouts estándar** (Column/Row/Box) | Phase 6 |
| **kotlinx-datetime** | Nativo KMP | 0.6.x | **Añadir** — reemplaza java.time en commonMain | Phase 1-2 |

---

## Patrón: SDK sin soporte KMP → Interfaz en commonMain + implementaciones por plataforma

Cuando un SDK de terceros **no tiene versión KMP** pero sí tiene SDKs nativos para cada plataforma (Android, iOS), el patrón es:

1. **Definir la interfaz en `commonMain`** — el contrato que usa el resto del código KMP
2. **Implementar en `androidMain`** con el SDK Android nativo
3. **Implementar en `iosMain`** con el SDK iOS nativo (via CocoaPods/cinterop)
4. **DI por plataforma** — cada plataforma wirea su implementación vía Koin

```
         commonMain
       ┌────────────┐
       │ MiInterfaz │  ← única referencia en todo el código compartido
       └─────┬──────┘
     ┌───────┴────────┐
androidMain        iosMain
┌──────────────┐  ┌──────────────┐
│ ImplAndroid  │  │  ImplIos     │
│ (SDK Android)│  │ (SDK iOS via │
└──────────────┘  │  CocoaPods)  │
                  └──────────────┘
```

**Ejemplo aplicado en GasGuru:** `core:analytics` con Mixpanel.  
**Próximos candidatos:** OneSignal (notificaciones push), Firebase Crashlytics.

> **Regla de orden de migración:** Si el módulo A depende de B, y B es Android-only, migrar B primero. Ejemplo: `core:supabase` dependía de `AnalyticsHelper` (Android-only) → se migró `core:analytics` primero, desbloqueando `core:supabase`.

---

## Checklist General de Progreso

### Phase 0: Build Infrastructure
- [ ] Crear rama `feature/kmp-phase0-build-infra` desde `develop`
- [ ] Traer de `feature/migrate-to-ktor`: `KmpLibraryConventionPlugin`, registro en build-logic, deps Ktor en `libs.versions.toml`
- [ ] Crear `KmpComposeLibraryConventionPlugin` → registrar como `gasguru.kmp.library.compose`
- [ ] Crear `KmpRoomConventionPlugin` → registrar como `gasguru.kmp.room`
- [ ] Actualizar `KoinConventionPlugin` para detectar KMP
- [ ] Añadir versiones CMP y kotlinx-datetime a `libs.versions.toml`
- [ ] `./gradlew :build-logic:convention:build` pasa
- [ ] PR → develop y merge

### Phase 1: `:core:model`
- [x] Crear rama `feature/kmp-phase1-core-model` desde `develop`
- [x] Cambiar plugin a `gasguru.kmp.library`
- [x] Mover archivos a `src/commonMain/kotlin/`
- [x] Reemplazar `java.util.Locale` en `FuelStation.kt`
- [x] Reemplazar `System.currentTimeMillis()` en `UserData.kt`
- [x] Añadir tests en `commonTest`
- [ ] `./gradlew :core:model:build` compila Android + iOS
- [ ] Todos los módulos downstream compilan
- [ ] PR → develop y merge

### Phase 2: `:core:common`
- [x] Crear rama `claude/kmp-migration-phase-2-0MHo4` desde `develop`
- [x] Cambiar plugin a `gasguru.kmp.library`
- [x] `GeoUtils.kt` → commonMain (reemplazar `Math.toRadians` × 4)
- [x] `KoinQualifiers.kt` → commonMain
- [x] `CoroutineModule.kt` → commonMain con expect/actual IO dispatcher
- [x] `CommonUtils.kt` → split: schedule parsing a commonMain (kotlinx-datetime), `getAppVersion()` expect/actual
- [x] `LocationUtils.kt` → androidMain
- [x] Tests en commonTest para `distanceTo()` e `isStationOpen()`
- [ ] `./gradlew :core:common:build` compila Android + iOS
- [ ] Todos los módulos downstream compilan
- [ ] PR → develop y merge

### Phase 3: `:core:database`
- [x] Crear rama `feature/kmp-phase-3` desde `develop`
- [x] Subir Room a `2.8.4` (latest stable KMP)
- [x] Cambiar plugin a `gasguru.kmp.room` + `gasguru.kmp.library` + `kotlin.serialization`
- [x] Migrar `ListConverters` de Moshi a kotlinx-serialization
- [x] Mover entities, DAOs, migrations, type converters a commonMain
- [x] Añadir `@ConstructedBy` a `GasGuruDatabase`
- [x] Actualizar 14 migraciones: `SupportSQLiteDatabase` → `SQLiteConnection` (Room KMP API)
- [x] Reemplazar `System.currentTimeMillis()` → `Clock.System.now()` (`kotlin.time`) en `PriceAlertEntity`
- [x] DI split: DatabaseModule androidMain/iosMain, DaoModule commonMain
- [x] Tests de compatibilidad JSON (Moshi vs kotlinx-serialization) en commonTest
- [x] `UserDataConvertersTest` y `ListConvertersTest` en commonTest
- [x] `DataBaseMigrationUnitTest` en androidUnitTest (actualizado para SQLiteConnection)
- [x] `./gradlew :core:database:assembleDebug` compila ✅
- [x] `./gradlew :core:database:compileKotlinIosSimulatorArm64` compila ✅
- [x] `./gradlew :core:data:assembleDebug` compila ✅
- [ ] `./gradlew :core:database:connectedAndroidTest` pasa en dispositivo
- [ ] PR → develop y merge

### Phase 4: Lógica de Negocio

#### Phase 4a: `:core:analytics` → KMP ✅
- [x] Plugin cambiado a `gasguru.kmp.library` + `kotlin("native.cocoapods")`
- [x] `AnalyticsEvent`, `AnalyticsHelper`, `NoOpAnalyticsHelper` → commonMain
- [x] `LocalAnalyticsHelper`, `LogcatAnalyticsHelper`, `MixpanelAnalyticsHelper`, `AnalyticsModule` → androidMain
- [x] `MixpanelAnalyticsHelperIos`, `AnalyticsModuleIos` → iosMain (Mixpanel iOS SDK via CocoaPods pod `Mixpanel-swift ~> 4.2`)
- [x] `AnalyticsEventCategoriesTest` → commonTest con kotlin.test
- [x] `LogcatAnalyticsHelperTest`, `MixpanelAnalyticsHelperTest` → src/test/kotlin (JUnit5+MockK, Android-specific)
- [x] `proguard-rules.pro` creado para el módulo
- [x] `assembleDebug` ✅ | `testDebugUnitTest` ✅ | `app:assembleDebug` ✅
- [ ] `compileKotlinIosArm64` ✅ (requiere CocoaPods + `pod install` cuando haya app iOS)
- [ ] PR → develop y merge

#### Phase 4b: `:core:supabase` → KMP ✅
- [x] Plugin cambiado a `gasguru.kmp.library`
- [x] `SupabaseManager`, `SupabaseManagerImpl`, modelos → `commonMain`
- [x] `SupabaseRemoteDataSource`, `ApiAnalyticsExt` → `commonMain` (desbloqueado por Phase 4a)
- [x] `SupabaseModule` (Koin + BuildConfig) → `androidMain`
- [x] `ktor-client-android` en `androidMain`, `ktor-client-darwin` en `iosMain`
- [x] Tests migrados a `commonTest` con `kotlin.test` + `FakeAnalyticsHelper` local
- [x] Sin `expect/actual` — `createSupabaseClient` es KMP nativo, credenciales vía BuildConfig en androidMain

#### Phase 4c: Restantes
- [x] `:core:notifications` → KMP
  - [x] Plugin cambiado a `gasguru.kmp.library`
  - [x] `OneSignalManager` interface → `commonMain`
  - [x] `NotificationService` interface → `commonMain` (wrapper unificador para entry points de plataforma)
  - [x] `PushAnalyticsExt` → `commonMain` (solo usa `AnalyticsHelper` KMP)
  - [x] `OneSignalManagerImpl` + `PushNotificationService` → `androidMain`
  - [x] `OneSignalManagerIos` (no-op) + `PushNotificationServiceIos` (stub V1) → `iosMain`
  - [x] DI split: `androidMain/di/NotificationModule.kt` y `iosMain/di/NotificationModule.kt`
  - [x] `GasGuruApplication` actualizado para inyectar `NotificationService` (interfaz)
  - [x] Tests `commonTest` con `FakeAnalyticsHelper` para `PushAnalyticsExt`
- [x] `:core:data` → KMP
  - [x] Plugin cambiado a `gasguru.kmp.library`
  - [x] Interfaces + repos offline + mappers → `commonMain`
  - [x] `LocationTrackerRepository`, `GeocoderAddressImpl`, `ConnectivityManagerNetworkMonitor`, `SearchPlaceMapper`, `RouteMapper`, `RoutesRepositoryImpl`, `PlacesRepositoryImp` → `androidMain`
  - [x] Stubs iOS V1: `LocationTrackerIos`, `GeocoderAddressIos`, `NetworkMonitorIos`, `PlacesRepositoryIos` → `iosMain`
  - [x] DI split: `CommonDataModule` (commonMain) + `AndroidDataModule` (androidMain) + `IosDataModule` (iosMain)
  - [x] `java.util.Locale` y `Math.toRadians` eliminados de `OfflineFuelStationRepository`
  - [x] `assembleDebug` ✅ | `testDebugUnitTest` ✅ | `app:assembleDebug` ✅
  - [ ] `compileKotlinIosSimulatorArm64` ✅ (requiere CocoaPods + Mixpanel para `core:analytics`, igual que Phase 4a)
- [x] `:core:domain` → KMP
- [ ] PR → develop y merge

### Phase 5: Infraestructura
- [ ] Crear rama `feature/kmp-phase5-infrastructure` desde `develop`
- [x] 5A: `:navigation` → KMP (100% commonMain con Navigation Compose CMP)
- [x] 5B: `:core:testing` → KMP (fakes commonMain, BaseTest + CoroutinesTestRuleExtension androidMain)
- [x] 5C: `:core:network` → KMP (Ktor commonMain, modelos @Serializable, `routesPlugin` expect/actual, `KtorModule` Koin); `:mocknetwork` simplificado (lectura directa JSON Supabase, sin Retrofit/MockWebServer)
- [ ] Todas las fakes compilan para todas las plataformas
- [ ] PR → develop y merge

### Phase 6: UI Compartida (CMP)
- [ ] Crear rama `feature/kmp-phase6-compose-mp` desde `develop`
- [x] 6A: `:core:ui` → CMP (mappers commonMain, InAppReview platform-specific)
- [x] 6B: `:core:uikit` → CMP (theme, componentes; reemplazar Lottie y ConstraintLayout)
- [x] 6C: `:core:components` → CMP (SearchBar + ViewModel commonMain)
- [ ] Componentes renderizan en Android e iOS
- [ ] PR → develop y merge

### Phase 7: Features + App iOS
- [x] `:feature:onboarding` → CMP ✅
  - [x] Plugin → `gasguru.kmp.compose.library` + `gasguru.koin`
  - [x] ViewModels + UiStates + Events → commonMain (sin cambios funcionales)
  - [x] Composables → commonMain (CMP: `painterResource`/`stringResource` de composeResources)
  - [x] `OnboardingPageUiModel` → `StringResource`/`DrawableResource` CMP
  - [x] `OnboardingNavigation` → commonMain (Navigation Compose CMP)
  - [x] `String.toFuelType(context)` eliminado de `core.ui` → `FuelTypeMapper.kt` KMP en la feature
  - [x] Recursos → `composeResources/drawable/` + `values/` + `values-es/`
  - [x] Tests ViewModel → `commonTest` (JUnit5 + Turbine)
  - [x] Test UI `OnboardingFuelPreferencesTest` → `androidInstrumentedTest` (mantiene `BaseTest`)
  - [x] `assembleDebug` ✅ | `testDebugUnitTest` ✅ | `app:assembleDebug` ✅ | `app:testProdDebugUnitTest` ✅
- [x] `:feature:profile` → CMP ✅
  - [x] Plugin → `gasguru.kmp.compose.library` + `gasguru.koin`
  - [x] ViewModels + UiStates + Events → commonMain
  - [x] Composables → commonMain (CMP: `stringResource` de composeResources)
  - [x] `ProfileNavigation` → commonMain (Navigation Compose CMP)
  - [x] Recursos → `composeResources/values/` + `values-es/`
  - [x] Tests ViewModel → `commonTest` (JUnit5 + Turbine)
  - [x] Test UI `ProfileScreenTest` → `androidInstrumentedTest` (usa `getCmpString` en vez de `R.string`)
  - [x] `BaseTest.getCmpString` extendido con soporte de `vararg formatArgs`
  - [x] `compileDebugKotlinAndroid` ✅ | `testDebugUnitTest` ✅
- [x] `:feature:favorite-list-station` → CMP
  - [x] Plugin → `gasguru.kmp.compose.library`
  - [x] Fuentes → `commonMain/kotlin`
  - [x] Recursos → `composeResources/drawable/` + `values/` + `values-es/`
  - [x] `R.drawable` / `R.string` → CMP `Res.drawable` / `Res.string`
  - [x] `LocalContext` para location settings → lambda `onOpenLocationSettings` pasada desde `NavigationBarScreen` (app)
  - [x] `koinViewModel` → `org.koin.compose.viewmodel.koinViewModel`
  - [x] Tests ViewModel → `commonTest`
  - [x] Test UI `FavoriteListScreenTest` → usa `getCmpString` en vez de `R.string`
  - [x] `compileDebugKotlinAndroid` ✅ | `testDebugUnitTest` ✅
- [x] `:feature:search` → CMP ✅
  - [x] Plugin → `gasguru.kmp.compose.library` + `gasguru.koin`
  - [x] Fuentes → `commonMain/kotlin`
  - [x] `ConfigureDialogSystemBars` → `expect/actual` en `:core:ui` (androidMain actual, iosMain no-op)
  - [x] Limpieza de dependencias muertas (`maps.compose`, `play.services.maps`, `kotlin.coroutines.play`, `secrets.google`)
  - [x] Sin tests nuevos (no tiene ViewModel propio; lógica delegada a `:core:components`)
  - [x] `compileDebugKotlinAndroid` ✅ | `app:assembleDebug` ✅ | `app:testProdDebugUnitTest` ✅
- [x] `:feature:detail-station` → CMP ✅
  - [x] Plugin → `gasguru.kmp.compose.library` + `gasguru.koin`
  - [x] Fuentes → `commonMain/kotlin`
  - [x] Recursos → `composeResources/drawable/` + `values/` + `values-es/`
  - [x] `R.drawable` / `R.string` → CMP `Res.drawable` / `Res.string`
  - [x] `ConstraintLayout` → `Row(weight(1f)) + Spacer + Box`
  - [x] `Coil 2` → `Coil 3` KMP (`coil3.compose.AsyncImage` + `LocalPlatformContext`)
  - [x] Share, Maps, Notification permission → `expect/actual` en `platform/` de la feature
  - [x] `System.currentTimeMillis()` → `Clock.System.now()` (`kotlin.time`)
  - [x] `koinViewModel` → `org.koin.compose.viewmodel.koinViewModel`
  - [x] Tests ViewModel → `commonTest`
  - [x] Test UI `DetailStationScreenTest` → `androidInstrumentedTest`
  - [x] `compileDebugKotlinAndroid` ✅ | `testDebugUnitTest` ✅ | `app:assembleDebug` ✅
- [x] `:feature:route-planner` → CMP ✅
- [x] `:feature:station-map` → CMP (con mapa expect/actual) ✅
- [x] Crear módulo `:iosApp` con target iOS (`:composeApp` + `:iosApp` creados en Phase 8B; NavHost completo se migra en sub-fases 8C+)
- [x] Phase 8C: App shell (splash + navegación) a `:composeApp/commonMain` ✅
  - [x] `SplashViewModel`, `SplashUiState` → `composeApp/commonMain` (kotlin.time.Clock)
  - [x] `GasGuruApp`, `GasGuruAppState`, `NavigationBarScreen` → `composeApp/commonMain`
  - [x] `NavigationBar`, `NavigationBarState`, `TopLevelRoutes` → `composeApp/commonMain` (`StringResource` en vez de R.string)
  - [x] `GasGuruNavHost`, `NavigationHandler`, `RoutePlannerNavigationGraph`, `NavigationBarRoute` → `composeApp/commonMain`
  - [x] `App.kt` entry point real: `koinInject` para deps, theme, `CompositionLocalProvider`
  - [x] `appShellModule()` en composeApp con `SplashViewModel`
  - [x] Strings `map_nav`/`list_nav`/`profile_nav`/`not_connected` → `composeApp/composeResources`
  - [x] `MainActivity` reducido a splash screen + intent handling + analytics + `App(...)` con lambda `onOpenLocationSettings`
  - [x] `SplashViewModelTest` → `composeApp/androidUnitTest` (JUnit5 + Turbine; `kotlin("native.cocoapods")` propaga `commonTest` a iOS y JUnit5 es JVM-only)
  - [x] `:composeApp:compileDebugKotlinAndroid` ✅ | `:composeApp:testDebugUnitTest` ✅ | `:app:testProdDebugUnitTest` ✅ | `:app:assembleProdRelease` ✅
- [x] Phase 8A: BuildKonfig en `:core:supabase` — `SupabaseModule` unificado en commonMain ✅
  - [x] BuildKonfig 0.21.2 registrado en `libs.versions.toml` y root `build.gradle.kts`
  - [x] `core/supabase/build.gradle.kts`: plugin `buildkonfig` + bloque `buildkonfig { objectName = "SupabaseSecrets" }`; `local.properties` con fallback `System.getenv()`
  - [x] `SupabaseModule.kt` movido de `androidMain` → `commonMain`; importa `SupabaseSecrets` generado
  - [x] `kotlin.srcDir(tasks.named("generateBuildKonfig"))` en `commonMain` para wiring de tarea
  - [x] `:core:analytics` iOS: eliminado cinterop `Mixpanel-swift`; `analyticsModuleIos` usa `NoOpAnalyticsHelper`
  - [x] `:core:supabase:assembleDebug` ✅ | `:core:supabase:compileKotlinIosSimulatorArm64` ✅ | `:core:supabase:testDebugUnitTest` ✅
- [x] Phase 8D: Inicialización Koin desde iOS + `MainViewController` con `App()` real ✅
  - [x] `KoinInit.kt` en `composeApp/iosMain` con todos los módulos iOS equivalentes a `GasGuruApplication`
  - [x] `iOSApp.swift` llama `KoinInitKt.doInitKoin()` en `init()` (una sola vez al lanzar el proceso)
  - [x] `MainViewController` renderiza `GasGuruIosApp()`: inyecta `SplashViewModel` vía Koin, colecta `themeMode`, abre Settings con `UIApplication.openURL`
  - [x] `fullScreenDialogProperties()` expect/actual en `core.ui`: Android con `decorFitsSystemWindows = false`, iOS sin ella → 4 navegaciones de diálogo migradas
  - [x] `LocalAnalyticsHelper` movido de `core.analytics/androidMain` → `core.ui/commonMain`
  - [x] `rememberInAppReviewManager()` como expect/actual: `androidMain` existente + `iosMain` no-op
  - [x] `ProfileScreenPreviews.kt`: import corregido a `org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider`
  - [x] `PlatformMapView.kt` iOS: color corregido de `surface` (inexistente) a `neutral100`
  - [x] `NavigationBarState.topLevelRoutes`: anotación explícita `List<TopLevelRoutes>` para evitar inferencia `Any` en KMP
  - [x] `:composeApp:compileKotlinIosSimulatorArm64` ✅ | `:composeApp:compileDebugKotlinAndroid` ✅ | `:app:assembleProdDebug` ✅ | `:composeApp:testDebugUnitTest` ✅
- [ ] App iOS compila e instala en simulador (pendiente verificación manual en Xcode)
- [ ] ViewModel tests migrados a commonTest
- [ ] PR → develop y merge

### Phase 10A: `jvm()` en convention plugins ✅
- [x] Añadir `jvm()` a `KmpLibraryConventionPlugin` (guard `composeApp`)
- [x] Añadir `lifecycle-runtime-compose` JetBrains a `KmpComposeLibraryConventionPlugin`
- [x] Añadir `jvmMain` actuals no-op para core:network, core:ui, core:uikit, feature:detail-station, feature:station-map
- [x] Exponer `kotlinx.coroutines.core` y `navigation-compose` como `api` para classpath JVM
- [x] Ampliar `detekt setSource` con `jvmMain`/`jvmTest`
- [x] PR → develop y merge

### Phase 10B: Tests de UI en CMP
- [x] Añadir `compose.uiTest` en `commonTest` y `compose.desktop.currentOs` en `jvmTest` en todos los módulos afectados
- [x] Reescribir tests con `runComposeUiTest` (eliminar `BaseTest` / `createComposeExtension` / JUnit5 de los módulos UI)
- [x] `GasGuruSearchBarContentTest` en `:core:components` → `commonTest` (commit #553, prueba de concepto)
- [x] 6 tests de `:core:uikit` → `commonTest` (GasGuruAlertDialogTest, TankCostCardTest, FuelListSelectionTest, SelectedItemTest, FuelStationItemTest, RouteNavigationCardTest)
- [x] `ProfileScreenTest` → `:feature:profile/commonTest`
- [x] `FavoriteListScreenTest` → `:feature:favorite-list-station/commonTest`
- [x] `OnboardingFuelPreferencesTest` → `:feature:onboarding/commonTest`
- [x] `DetailStationScreenTest` → `:feature:detail-station/commonTest`
- [x] Verificar que todos los módulos pasan `./gradlew :<modulo>:jvmTest` sin emulador ✅
- [x] Verificar que `testDebugUnitTest` sigue pasando (ViewModel tests no afectados) ✅
- [ ] PR → develop y merge

---

## Grafo de Dependencias y Orden de Migración

```
Phase 0: build-logic (convention plugins)
    │   Rama: feature/kmp-phase0-build-infra (desde develop)
    │   Incluye: traer KmpLibraryConventionPlugin + deps de feature/migrate-to-ktor
    │
Phase 1: :core:model (sin deps)
    │   Rama: feature/kmp-phase1-core-model
    │
Phase 2: :core:common (← model)
    │   Rama: feature/kmp-phase2-core-common
    │
Phase 3: :core:database (← model, network)
    │   Rama: feature/kmp-phase3-core-database
    │
Phase 4: :core:supabase, :core:notifications, :core:data, :core:domain
    │   Rama: feature/kmp-phase4-business-logic
    │
Phase 5: :navigation, :core:testing, :mocknetwork
    │   Rama: feature/kmp-phase5-infrastructure
    │
Phase 6: :core:ui, :core:uikit, :core:components (CMP)
    │   Rama: feature/kmp-phase6-compose-mp
    │
Phase 7: Features (CMP) + iOS app target
        Rama: feature/kmp-phase7-features-ios
    │
Phase 9: iOS Feature Parity (9A → 9I, sub-PRs)
        Rama base: feature/kmp-phase9-ios-parity (o PRs directos a develop por sub-fase)
```

---

## Tabla de Fases

| Fase | Rama | Módulos | Plataformas al terminar |
|------|------|---------|-------------------------|
| 0 | feature/kmp-phase0-build-infra | build-logic | Android (sin cambios) |
| 1 | feature/kmp-phase1-core-model | :core:model | Android (sin cambios visibles) |
| 2 | feature/kmp-phase2-core-common | :core:common | Android (sin cambios visibles) |
| 3 | feature/kmp-phase3-core-database | :core:database | Android (sin cambios visibles) |
| 4 | feature/kmp-phase4-business-logic | :core:supabase, :core:notifications, :core:data, :core:domain | Android + lógica iOS compilable |
| 5 | feature/kmp-phase5-infrastructure | :navigation, :core:testing, :mocknetwork | Android + tests KMP |
| 6 | feature/kmp-phase6-compose-mp | :core:ui, :core:uikit, :core:components | Android + componentes iOS |
| 7 | feature/kmp-phase7-features-ios | Features + iOS app | **Android + Android Auto + iOS** |
| 9A-9B | feature/kmp-phase9-ios-parity | `:core:data`, `:feature:station-map` | **Android + Android Auto + iOS** (conectividad, geocoder, ubicación, permisos) |
| 9C | feature/kmp-phase9c-ios-mapkit | `:feature:station-map` | **iOS mapa interactivo** (MapKit MVP — markers, ruta, centrado) |
| 9D | feature/kmp-phase9d-places-ios | `:core:data` | **iOS búsqueda de lugares** (Google Places SDK — paridad con Android) |
| 9C.2 | (pendiente) | `:feature:station-map` | iOS map polish (clustering + markers con precio/logo) |
| 9E | feature/kmp-phase9e-detail-actions-ios | `:feature:detail-station` | **iOS detail actions** (MapsNavigation, ShareAction, NotificationPermission) |
| 9F | feature/kmp-phase9f-in-app-review-ios | `:core:ui` | **iOS in-app review** (SKStoreReviewController + refactor InAppReviewManager a interface) |
| 9G | feature/kmp-phase9g-analytics-ios | `:core:analytics`, `:composeApp`, `iosApp` | **iOS analytics** (Mixpanel-swift via Swift bridge `@ObjCName` + KoinInit param) |
| 9H | feature/kmp-phase9h-onesignal-ios | `:core:notifications` | **iOS push notifications** (OneSignal Swift bridge, prompt al crear primera alerta, deeplink desde tap) |
| 9J | feature/kmp-phase9j-ios-background-sync | `:composeApp`, `iosApp` | **iOS background sync** (SyncManager.execute() en KoinInit, IosBridge.refreshStations + BGTaskScheduler) |
| 9I | (pendiente) | Validación E2E | **Android + Android Auto + iOS funcional (paridad completa)** |

---

## Phase 0: Build Infrastructure

**Objetivo**: Preparar el sistema de build para soportar múltiples módulos KMP y CMP.

**Módulos**: `build-logic/convention`

### Pasos técnicos

1. **Añadir dependencias a `build-logic/convention/build.gradle.kts`**:
   - JetBrains Compose Multiplatform gradle plugin (para el futuro `KmpComposeLibraryConventionPlugin`)

2. **Crear `KmpComposeLibraryConventionPlugin`**:
   - Aplica `gasguru.kmp.library` + `org.jetbrains.compose` + `org.jetbrains.kotlin.plugin.compose`
   - Configura `commonMain` con compose dependencies (runtime, foundation, material3)
   - Registrar como `gasguru.kmp.library.compose`

3. **Crear `KmpRoomConventionPlugin`**:
   - Aplica `gasguru.kmp.library` + `androidx.room` + `com.google.devtools.ksp`
   - Configura Room KMP: `kspCommonMainMetadata` target para codegen
   - Añade `room-runtime` a `commonMain.dependencies`
   - Registrar como `gasguru.kmp.room`

4. **Actualizar `KoinConventionPlugin`**:
   - Detectar si el proyecto aplica `kotlin.multiplatform`
   - Si KMP: añadir `koin-core` a `commonMain`, `koin-android` a `androidMain`
   - Si no KMP: mantener comportamiento actual

5. **Añadir a `libs.versions.toml`**:
   ```toml
   compose-multiplatform = "1.10.0"
   kotlinx-datetime = "0.6.2"

   # Libraries
   compose-multiplatform-gradlePlugin = { module = "org.jetbrains.compose:compose-gradle-plugin", version.ref = "compose-multiplatform" }
   kotlinx-datetime = { module = "org.jetbrains.kotlinx:kotlinx-datetime", version.ref = "kotlinx-datetime" }

   # Plugins
   gasguru-kmp-compose-library = { id = "gasguru.kmp.library.compose" }
   gasguru-kmp-room = { id = "gasguru.kmp.room" }
   ```

### Tests
- `./gradlew :build-logic:convention:build` compila sin errores
- `:core:network` sigue compilando (sanity check)

### Conceptos KMP a aprender
- Gradle source sets KMP (commonMain, androidMain, iosMain)
- Cómo funcionan los convention plugins con KMP
- Diferencia entre `kotlin.multiplatform` y `kotlin.android` plugins

### Criterio de "done"
- Los 3 nuevos convention plugins compilan y se registran
- `./gradlew tasks` muestra las nuevas tareas
- `:core:network` sigue compilando sin cambios

---

## Phase 1: `:core:model` — Modelos de Dominio

**Objetivo**: Migrar el módulo base del que dependen todos los demás. Riesgo mínimo porque son data classes puras.

**Módulo**: `:core:model`

### Análisis de dependencias JVM

| Archivo | Dependencia JVM | Reemplazo KMP |
|---------|-----------------|---------------|
| `FuelStation.kt` | `java.util.Locale` en `String.format(Locale.ROOT, ...)` | Usar `"%.2f".format(...)` (Kotlin stdlib, disponible en common desde Kotlin 1.9.20) |
| `UserData.kt` | `System.currentTimeMillis()` | `Clock.System.now().toEpochMilliseconds()` (kotlinx-datetime) |

### Pasos técnicos

1. **Cambiar plugin**: `gasguru.android.library` → `gasguru.kmp.library` en `core/model/build.gradle.kts`
2. **Mover archivos**: `src/main/java/com/gasguru/core/model/` → `src/commonMain/kotlin/com/gasguru/core/model/`
3. **En `FuelStation.kt`**:
   - Eliminar `import java.util.Locale`
   - `String.format(Locale.ROOT, "%.2f Km", kilometers)` → `"%.2f Km".format(kilometers)` (Kotlin common)
4. **En `UserData.kt`**:
   - Añadir `import kotlinx.datetime.Clock`
   - `System.currentTimeMillis()` → `Clock.System.now().toEpochMilliseconds()`
5. **Actualizar `build.gradle.kts`**:
   ```kotlin
   plugins {
       alias(libs.plugins.gasguru.kmp.library)
   }
   kotlin {
       sourceSets {
           commonMain.dependencies {
               implementation(libs.kotlinx.datetime)
           }
       }
   }
   ```
6. **Eliminar** el plugin `gasguru.proguard` (no aplica a KMP library sin Android-specific code)

### Tests
- No hay tests existentes en `:core:model`
- Añadir `src/commonTest/kotlin/` con tests básicos para `FuelStation.formatDistance()` y `FuelStation.formatDirection()` usando `kotlin.test`
- Verificar que TODOS los módulos downstream siguen compilando

### Conceptos KMP a aprender
- Estructura de source sets: `commonMain/kotlin/` vs `src/main/java/`
- `kotlin.test` como framework de testing multiplataforma
- kotlinx-datetime como reemplazo de java.time

### Criterio de "done"
- `./gradlew :core:model:build` compila para Android + iOS
- `:core:network`, `:core:database`, `:core:common` siguen compilando
- Tests en `commonTest` pasan

### Archivos a modificar
- `core/model/build.gradle.kts`
- `core/model/src/commonMain/kotlin/com/gasguru/core/model/data/FuelStation.kt` (mover + editar)
- `core/model/src/commonMain/kotlin/com/gasguru/core/model/data/UserData.kt` (mover + editar)
- Resto de archivos en model: mover sin cambios

---

## Phase 2: `:core:common` — Utilidades Compartidas

**Objetivo**: Separar código multiplataforma (GeoUtils, KoinQualifiers, CoroutineModule) del código Android-only (LocationUtils, BuildConfig).

**Módulo**: `:core:common`

### Análisis archivo por archivo

| Archivo | Destino | Cambios necesarios |
|---------|---------|-------------------|
| `KoinQualifiers.kt` | commonMain | Ninguno — `object` con `const val` strings |
| `CoroutineModule.kt` | commonMain + androidMain | `Dispatchers.IO` no existe en iOS → expect/actual |
| `GeoUtils.kt` | commonMain | `Math.toRadians(x)` → `x * PI / 180.0` |
| `CommonUtils.kt` | commonMain + androidMain | Schedule parsing a commonMain (reemplazar java.time), `getAppVersion()` a androidMain con expect/actual |
| `LocationUtils.kt` | androidMain | 100% Android (Google Maps Compose types, Context, permissions) |

### Pasos técnicos

1. **Cambiar plugin**: `gasguru.android.library` → `gasguru.kmp.library` + `gasguru.koin`
2. **Crear estructura de source sets**
3. **`GeoUtils.kt` → commonMain**:
   - `Math.toRadians(x)` → `x * kotlin.math.PI / 180.0` (4 ocurrencias)
4. **`CommonUtils.kt` → split**:
   - commonMain: `isStationOpen()` con kotlinx-datetime
     - `java.time.ZonedDateTime.now()` → `Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())`
     - `java.time.LocalTime` → `kotlinx.datetime.LocalTime`
     - `java.time.DayOfWeek` → `kotlinx.datetime.DayOfWeek`
   - commonMain: `expect fun getAppVersion(): String`
   - androidMain: `actual fun getAppVersion()` usando BuildConfig
   - iosMain: `actual fun getAppVersion()` usando `NSBundle.mainBundle`
5. **`CoroutineModule.kt` → commonMain con expect/actual para IO dispatcher**:
   - commonMain: `expect val ioDispatcher: CoroutineDispatcher`
   - androidMain: `actual val ioDispatcher = Dispatchers.IO`
   - iosMain: `actual val ioDispatcher = Dispatchers.Default` (Kotlin/Native no tiene IO dispatcher dedicado)
6. **`LocationUtils.kt` → androidMain sin cambios**
7. **Gestionar BuildConfig**: El `build.gradle.kts` actual lee `versions.properties` y genera BuildConfig fields. Mover esa lógica al `androidMain` build variant o usar un file-based approach con `expect/actual`.
8. **Actualizar dependencias**:
   ```kotlin
   commonMain.dependencies {
       implementation(projects.core.model)
       implementation(libs.kotlinx.coroutines.core)
       implementation(libs.koin.core)
       implementation(libs.kotlinx.datetime)
   }
   androidMain.dependencies {
       implementation(libs.androidx.core.ktx)
       implementation(libs.appcompat)
       implementation(libs.material)
       api(libs.play.services.maps)
       implementation(libs.maps.compose)
       implementation(libs.koin.android)
   }
   ```

### Tests
- Test existente: `testImplementation(libs.junit)` — verificar qué test existe y moverlo
- commonTest: tests para `distanceTo()` con coordenadas conocidas, `isStationOpen()` con horarios fijos
- androidTest: tests para `LocationUtils` extensiones

### Conceptos KMP a aprender
- Declaraciones `expect`/`actual` — el mecanismo fundamental de KMP
- Diferencias de runtime entre JVM y Kotlin/Native (IO dispatcher, reflexión)
- kotlinx-datetime como reemplazo completo de java.time
- Cómo acceder a APIs nativas iOS desde Kotlin (NSBundle)

### Criterio de "done"
- `./gradlew :core:common:build` compila para Android + iOS
- Todos los módulos downstream compilan
- `distanceTo()` devuelve los mismos valores que antes (test de regresión)
- `isStationOpen()` funciona con kotlinx-datetime

### Archivos a modificar
- `core/common/build.gradle.kts` — reescribir para KMP
- `core/common/src/commonMain/kotlin/.../GeoUtils.kt`
- `core/common/src/commonMain/kotlin/.../CommonUtils.kt` (schedule parsing)
- `core/common/src/commonMain/kotlin/.../KoinQualifiers.kt`
- `core/common/src/commonMain/kotlin/.../CoroutineModule.kt`
- `core/common/src/androidMain/kotlin/.../LocationUtils.kt`
- `core/common/src/androidMain/kotlin/.../AppVersion.kt` (actual)
- `core/common/src/iosMain/kotlin/.../AppVersion.kt` (actual)
- `core/common/src/iosMain/kotlin/.../IoDispatcher.kt` (actual)

---

## Phase 3: `:core:database` — Room KMP

**Objetivo**: Migrar Room a KMP para que entities, DAOs, migrations y type converters estén en `commonMain`. Solo la instanciación de la base de datos es platform-specific.

**Módulo**: `:core:database`

### Análisis de type converters

| Converter | Usa Moshi? | Acción |
|-----------|-----------|--------|
| `UserDataConverters` | No — conversiones enum↔String puras | Mover a commonMain sin cambios |
| `FilterTypeConverter` | No — conversión enum↔String pura | Mover a commonMain sin cambios |
| `ListConverters` | **Sí** — usa `Moshi.Builder()`, `KotlinJsonAdapterFactory`, `Types.newParameterizedType` | **Reescribir con kotlinx-serialization**: `Json.encodeToString(list)` / `Json.decodeFromString(data)` |

### Pasos técnicos

1. **Cambiar plugin**: `gasguru.android.library` + `gasguru.room` → `gasguru.kmp.room` (nuevo plugin de Phase 0)
2. **Migrar `ListConverters.kt`** de Moshi a kotlinx-serialization:
   ```kotlin
   // commonMain
   class ListConverters {
       @TypeConverter
       fun fromList(list: List<String>): String = Json.encodeToString(list)

       @TypeConverter
       fun toList(data: String): List<String> = Json.decodeFromString(data)
   }
   ```
3. **Mover a commonMain**: Todas las entities, DAOs, type converters, migrations, `GasGuruDatabase` abstract class
4. **Room KMP requiere**:
   - Añadir `@ConstructedBy(GasGuruDatabaseConstructor::class)` a `GasGuruDatabase`
   - Crear `expect object GasGuruDatabaseConstructor : RoomDatabaseConstructor<GasGuruDatabase>`
   - androidMain: `actual object GasGuruDatabaseConstructor` (Room genera la implementación automáticamente)
   - iosMain: igual, Room genera la implementación
5. **DI split**:
   - androidMain: `DatabaseModule.kt` con `Room.databaseBuilder(context, ...)` + migrations
   - iosMain: `DatabaseModule.kt` con `Room.databaseBuilder<GasGuruDatabase>(name = dbPath)` + migrations
   - commonMain: `DaoModule.kt` (los DAOs se obtienen del database)
6. **Actualizar build.gradle.kts**:
   ```kotlin
   plugins {
       alias(libs.plugins.gasguru.kmp.room)
       alias(libs.plugins.gasguru.koin)
       alias(libs.plugins.kotlin.serialization)
   }
   kotlin {
       sourceSets {
           commonMain.dependencies {
               implementation(projects.core.model)
               implementation(libs.kotlinx.coroutines.core)
               implementation(libs.kotlinx.serialization.json)
           }
       }
   }
   ```
7. **Eliminar dependencia de Moshi** (`moshi-kotlin`) del módulo

### Tests
- Los tests instrumentados de Android (`androidTest`) siguen funcionando — verifican migraciones y DAOs en device real
- Añadir `commonTest` con tests unitarios para type converters (serialización/deserialización)
- Verificar las 13 migraciones en Android (regresión crítica)
- iosTest: test básico de creación de BD + CRUD simple

### Conceptos KMP a aprender
- Room KMP: `@ConstructedBy`, `RoomDatabaseConstructor`, KSP en KMP
- Diferencias entre SQLite en Android (android.database.sqlite) vs SQLite en iOS (via Room KMP driver)
- Cómo Room KMP genera código para cada plataforma

### Riesgos
- **Migraciones en iOS**: Las 13 migraciones de Android aplican a nuevas instalaciones en iOS (no hay BD previa). Pero Room las ejecuta secuencialmente de v1→v13 por si un usuario de iOS instala una versión futura con una BD existente. Verificar que SQL es compatible con SQLite iOS.
- **Moshi → kotlinx-serialization en ListConverters**: El formato JSON generado debe ser idéntico para no romper BDs existentes en Android. `Json.encodeToString(listOf("a","b"))` genera `["a","b"]` — mismo formato que Moshi.

### Criterio de "done"
- `./gradlew :core:database:build` compila para Android + iOS
- Tests de migración Android pasan
- ListConverters genera el mismo JSON que antes (test de compatibilidad)
- `:core:data` sigue compilando

### Archivos a modificar
- `core/database/build.gradle.kts` — reescribir para KMP
- `core/database/src/commonMain/kotlin/.../GasGuruDatabase.kt` — añadir `@ConstructedBy`
- `core/database/src/commonMain/kotlin/.../converters/ListConverters.kt` — Moshi → kotlinx-serialization
- Todas las entities, DAOs, migrations → mover a commonMain
- `core/database/src/androidMain/kotlin/.../di/DatabaseModule.kt` — platform-specific builder
- `core/database/src/iosMain/kotlin/.../di/DatabaseModule.kt` — platform-specific builder
- `build-logic/.../KmpRoomConventionPlugin.kt` (creado en Phase 0)

---

## Phase 4: Lógica de Negocio — Data + Domain + Supabase + Notifications

**Objetivo**: Migrar toda la capa de negocio a KMP. Tras esta fase, toda la lógica (repositories, use cases, data sources) es compartida. Es la fase con mayor impacto.

**Módulos**: `:core:supabase`, `:core:notifications`, `:core:data`, `:core:domain`

### 4A: `:core:supabase`

**Estado actual**: Módulo Android-only creado en develop (PR #480). Contiene el datasource remoto de estaciones. Supabase SDK (3.2.6) ya es KMP pero el módulo usa `ktor-client-android`.

| Destino | Archivos |
|---------|----------|
| commonMain | `RemoteDataSource.kt` (interfaz), `SupabaseRemoteDataSource.kt`, `SupabaseFuelStation.kt`, `NetworkError.kt`, `ApiAnalyticsExt.kt`, `SupabaseModule.kt` (Koin) |

**Cambios**:
- Plugin → `gasguru.kmp.library` + `gasguru.koin`
- Mover todos los archivos de `src/main/java/` → `commonMain`
- Reemplazar `ktor-client-android` por engines platform-specific
- commonMain: `supabase-postgrest`, `ktor-client-core`
- androidMain: `ktor-client-okhttp`
- iosMain: `ktor-client-darwin`

### 4B: `:core:notifications`

**Estado actual**: OneSignal Android SDK only.

| Destino | Archivos |
|---------|----------|
| commonMain | `OneSignalManager.kt` (interfaz) |
| androidMain | `OneSignalManagerImpl.kt`, `PushNotificationService.kt`, `NotificationModule.kt` |
| iosMain | `OneSignalManagerNoOp.kt` (V1: no-op, V2: integrar OneSignal iOS SDK) |

**Cambios**:
- Plugin → `gasguru.kmp.library` + `gasguru.koin`
- La interfaz `OneSignalManager` va a commonMain
- V1 iOS: implementación no-op que no envía notificaciones (funcionalidad de alertas de precio limitada en iOS V1)
- V2 iOS: integrar OneSignal iOS SDK via cinterop o Swift bridging

### 4C: `:core:data` — La migración más compleja

**Análisis de cada archivo**:

| Archivo | Destino | Dependencias JVM/Android | Acción |
|---------|---------|--------------------------|--------|
| Repository interfaces (7) | commonMain | Ninguna | Mover sin cambios |
| `OfflineFuelStationRepository` | commonMain | `maps-utils` (distance) | Reemplazar con `GeoUtils.distanceTo()` de `:core:common` |
| `FilterRepositoryImpl` | commonMain | Ninguna | Mover sin cambios |
| `OfflineRecentSearchRepositoryImp` | commonMain | Ninguna | Mover sin cambios |
| `OfflineUserDataRepository` | commonMain | Ninguna | Mover sin cambios |
| `RoutesRepositoryImpl` | commonMain | Ninguna | Mover sin cambios |
| `PriceAlertRepositoryImpl` | commonMain | Ninguna (usa interfaces) | Mover sin cambios |
| `SyncManager` | commonMain | Ninguna (usa interfaces) | Mover sin cambios |
| `GoogleStaticMapRepository` | commonMain | Ninguna (construye URL string) | Mover sin cambios |
| Mappers (4-5) | commonMain | Verificar | Mover, adaptar si hay deps Android |
| `LocationTrackerRepository` | androidMain | FusedLocationProviderClient, BroadcastReceiver, Context | Mantener en Android |
| `ConnectivityManagerNetworkMonitor` | androidMain | ConnectivityManager, Context | Mantener en Android |
| `GeocoderAddressImpl` | androidMain | android.location.Geocoder | Mantener en Android |
| `PlacesRepositoryImp` | androidMain | Google Places SDK (PlacesClient) | Mantener en Android |
| `DataModule.kt` | split | Koin module | Separar common/android/ios |

**Implementaciones iOS necesarias**:

| Interfaz | Implementación iOS | API nativa |
|----------|-------------------|-----------|
| `LocationTracker` | `LocationTrackerIos` | `CLLocationManager` (CoreLocation) |
| `NetworkMonitor` | `NetworkMonitorIos` | `NWPathMonitor` (Network framework) |
| `GeocoderAddress` | `GeocoderAddressIos` | `CLGeocoder` (CoreLocation) |
| `PlacesRepository` | `PlacesRepositoryIos` | `MKLocalSearch` (MapKit) |

**DI (Koin) split**:
- `commonMain/di/DataModule.kt`: bind repos que están en commonMain
- `androidMain/di/DataAndroidModule.kt`: bind LocationTracker, NetworkMonitor, Geocoder, Places → implementaciones Android
- `iosMain/di/DataIosModule.kt`: bind → implementaciones iOS

### 4D: `:core:domain`

**Estado actual**: 100% Kotlin puro. Solo tiene use cases que dependen de interfaces de repository.

**Acción**: Mover TODO a commonMain sin cambios funcionales.
- Plugin → `gasguru.kmp.library` + `gasguru.koin`
- `DomainModule.kt` (Koin) → commonMain (solo usa `factory {}`)
- Dependencias: `:core:data`, `:core:model`, `:core:common`, `:core:notifications` (todas ya KMP)

### Tests Phase 4

| Módulo | commonTest | androidTest | iosTest |
|--------|-----------|-------------|---------|
| `:core:supabase` | Tests con Ktor MockEngine | — | — |
| `:core:notifications` | — | Tests Android existentes | Test no-op iOS |
| `:core:data` | Tests de repos con fakes (Turbine, kotlin.test) | Tests Android-specific (LocationTracker, etc.) | Tests iOS-specific (CLLocationManager mock) |
| `:core:domain` | **Todos los tests** — use cases son Kotlin puro | — | — |

**Migración de framework de testing**: En commonTest, reemplazar JUnit5 por `kotlin.test`:
- `@Test` (org.junit.jupiter) → `@Test` (kotlin.test)
- `assertEquals` → `kotlin.test.assertEquals`
- `assertTrue` → `kotlin.test.assertTrue`
- Turbine funciona en commonMain (es KMP-ready)

### Conceptos KMP a aprender
- `cinterop` para acceder a APIs nativas iOS (CLLocationManager, NWPathMonitor, CLGeocoder, MKLocalSearch)
- Patrones de integración con frameworks iOS nativos desde Kotlin
- Koin modules platform-specific y cómo combinarlos en el arranque
- Diferencias entre testing en JVM vs Kotlin/Native

### Criterio de "done"
- Los 4 módulos compilan para Android + iOS
- Todos los tests existentes pasan (movidos o adaptados)
- Use cases ejecutan idéntico en ambas plataformas
- iOS tiene implementaciones funcionales de Location, Network, Geocoder, Places (al menos stubs compilables)

### Archivos clave
- `core/data/src/main/java/com/gasguru/core/data/di/DataModule.kt` → split en 3
- `core/data/src/main/java/com/gasguru/core/data/repository/` → todos los archivos
- `core/domain/src/main/java/com/gasguru/core/domain/` → todos los use cases
- `core/supabase/src/main/java/` → todo el módulo
- `core/notifications/src/main/java/` → todo el módulo

---

## Phase 5: Infraestructura — Navigation + Testing + MockNetwork

**Objetivo**: Hacer que la infraestructura de navegación, testing y mocks sea multiplataforma.

### 5A: `:navigation`

**Estado actual**: `NavigationManager`, `NavigationDestination`, `NavigationCommand` son Kotlin puro. Los handlers y NavHost son Android-specific.

| Destino | Archivos |
|---------|----------|
| commonMain | `NavigationManager.kt`, `NavigationManagerImpl.kt`, `NavigationDestination.kt`, `NavigationCommand.kt`, `NavigationKeys.kt`, `DeepLinkStateHolder.kt`, `NavigationManagerModule.kt` |
| androidMain | `NavigationExtensions.kt`, `GlobalCompositionLocal.kt`, `GasGuruNavHost.kt`, `NavigationHandler.kt` |
| iosMain | (vacío en V1 — la app iOS consumirá NavigationManager directamente) |

**Cambios adicionales**:
- Si `PlaceArgs.kt` o `RoutePlanArgs.kt` usan `@Parcelize`, eliminar y usar `@Serializable`
- Eliminar plugin `kotlin-parcelize` del módulo

### 5B: `:core:testing`

| Destino | Archivos |
|---------|----------|
| commonMain | Las 14 fakes (implementan interfaces que ya están en commonMain) |
| androidMain | `BaseTest.kt` (Compose test infrastructure), `CoroutinesTestRuleExtension.kt` (JUnit5) |
| commonTest dependencies | `kotlinx-coroutines-test`, `kotlin-test`, `turbine` |

### 5C: `:mocknetwork`

- Ya usa Ktor MockEngine → mover todo a commonMain
- Eliminar dependencia de Android assets — cargar JSON desde resources de commonMain

### Criterio de "done"
- NavigationManager funciona idénticamente desde common code
- Todas las fakes compilan para todas las plataformas
- MockNetwork funciona con Ktor MockEngine en commonTest

---

## Phase 6: UI Compartida — Compose Multiplatform

**Objetivo**: Migrar componentes UI a CMP para compartir la capa de presentación.

### 6A: `:core:ui` (mappers + modelos UI)

- Plugin → `gasguru.kmp.library.compose` (nuevo de Phase 0)
- Mover mappers y modelos UI a commonMain (usan `@Stable` de Compose, disponible en CMP)
- `InAppReviewManager`: androidMain (play-review-ktx) + iosMain (SKStoreReviewController)

### 6B: `:core:uikit` (design system)

- Plugin → `gasguru.kmp.library.compose`
- Mover `GasGuruTheme`, `GasGuruColors`, componentes Material3 a commonMain
- **Lottie** → Reemplazar con `compottie` (librería KMP que lee archivos .json de Lottie) o Compose animations nativas
- **ConstraintLayout Compose** → Refactorizar layouts afectados a Column/Row/Box con Modifier
- **Coil** → Migrar a Coil 3 (tiene soporte KMP) para carga de imágenes

### 6C: `:core:components` (SearchBar)

- Plugin → `gasguru.kmp.library.compose`
- ViewModel + Composable → commonMain
- Koin module → commonMain

### Tests
- UI mapper tests → commonTest (kotlin.test)
- Compose UI tests → androidInstrumentedTest (CMP testing aún madurando)
- ViewModel tests del SearchBar → commonTest (Turbine)

### Conceptos KMP a aprender
- Compose Multiplatform: diferencias con Jetpack Compose Android
- Material3 en CMP
- Carga de recursos (strings, images) en CMP: `Res` system de JetBrains
- Alternativas a librerías Android-only (Lottie, ConstraintLayout)

### Criterio de "done"
- Todos los componentes renderizan en Android e iOS preview
- Theme dark/light funciona en ambas plataformas
- No quedan dependencias de Lottie ni ConstraintLayout

---

## Phase 7: Features + App iOS — El Push Final

**Objetivo**: Migrar las pantallas de features a CMP y crear el target de app iOS.

### Orden de migración (menor a mayor complejidad)

| Orden | Feature | Complejidad | Razón |
|-------|---------|-------------|-------|
| 1 | `:feature:onboarding` | Baja | UI simple, pocos deps |
| 2 | `:feature:profile` | Baja | Settings screen, sin mapas |
| 3 | `:feature:favorite-list-station` | Baja | Lista, sin mapas |
| 4 | `:feature:search` | Media | Usa SearchBar (ya migrado) |
| 5 | `:feature:detail-station` | Media | Static map image, navegación |
| 6 | `:feature:route-planner` | Alta | Usa mapas y Places |
| 7 | `:feature:station-map` | Alta | Google Maps Compose, la más compleja |

### Patrón para cada feature

1. Plugin → `gasguru.kmp.library.compose` + `gasguru.koin`
2. Mover ViewModel + UiState + Events → commonMain
3. Mover Composables → commonMain (CMP)
4. Para features con mapa: `expect`/`actual` Composable
   ```kotlin
   // commonMain
   @Composable
   expect fun PlatformMapView(
       stations: List<FuelStationUiModel>,
       cameraPosition: LatLng,
       zoom: Float,
       onStationClick: (Int) -> Unit,
       modifier: Modifier,
   )

   // androidMain: GoogleMap composable
   // iosMain: UIKitView { MKMapView() }
   ```
5. Reemplazar `koin-androidx-compose` por `koin-compose` (KMP version)

### App iOS

1. Crear módulo `:iosApp` con el target de aplicación iOS
2. `iosApp/iosApp/iOSApp.swift` — punto de entrada SwiftUI que hostea CMP
3. Configurar Koin initialization para iOS:
   ```kotlin
   // shared/src/iosMain/kotlin/KoinInit.kt
   fun initKoin() {
       startKoin {
           modules(
               commonModules + iosSpecificModules
           )
       }
   }
   ```
4. Generar framework XCFramework con `./gradlew assembleXCFramework`

### `:auto:common` — Se queda Android-only
- Android Auto usa CarAppService (API Android exclusiva)
- No se migra a KMP
- En V2, crear `:carplay:common` como módulo iOS-only equivalente

### Tests
- ViewModel tests → commonTest (Turbine + kotlin.test)
- Compose UI tests → androidInstrumentedTest
- iOS: tests manuales + snapshot tests cuando CMP testing madure

### Criterio de "done" (V1 completo)
- App Android funciona idénticamente a la versión pre-migración
- Android Auto sigue funcionando sin cambios
- App iOS compila, instala en simulador y muestra todas las pantallas
- Navegación entre pantallas funciona en iOS
- Mapa muestra estaciones en iOS (MapKit)
- Búsqueda de Places funciona en iOS (MKLocalSearch)
- Localización funciona en iOS (CLLocationManager)

---

## Estrategia de Migración de Tests

### Inventario actual

El proyecto tiene **35 archivos de test** distribuidos así:

| Tipo | Cantidad | Framework | Ubicación actual |
|------|----------|-----------|------------------|
| ViewModel tests | 8 | JUnit5 + Turbine + CoroutinesTestExtension | `src/test/` en app, features y components |
| DAO tests (Room) | 5 | JUnit5 + Room in-memory + Turbine | `core/database/src/androidTest/` |
| DataSource tests | 2 | JUnit5 + Ktor MockEngine / MockK | `core/network/src/androidUnitTest/` |
| UseCase tests | 1 | JUnit5 + MockK + CoroutinesTestExtension | `core/domain/src/test/` |
| Compose UI tests | 8 | JUnit5 + BaseTest (Compose) | `src/androidTest/` en uikit y features |
| Skeletons vacíos | 2 | — | `core/data`, `core/domain` |
| **Fakes** | **14** | — | `core/testing/src/main/` |

### Infraestructura de testing actual

**`:core:testing`** — Librería central con:
- `BaseTest.kt` — Clase base JUnit5 con soporte Compose (`createComposeExtension()`, `getStringResource()`)
- `CoroutinesTestRule.kt` — JUnit4 Rule con `StandardTestDispatcher`
- `CoroutineTestRuleExtension.kt` — JUnit5 Extension con `StandardTestDispatcher`
- **14 fakes** que implementan interfaces de repository/DAO/services usando `MutableStateFlow` y listas mutables para tracking de llamadas

**`:mocknetwork`** — Mock HTTP server con Ktor MockEngine + JSON desde Android assets

### Decisiones de migración de testing

| Decisión | Justificación |
|----------|---------------|
| **JUnit5 → `kotlin.test` en commonTest** | JUnit5 es JVM-only. `kotlin.test` es el framework multiplataforma de Kotlin. La API es similar (`@Test`, `assertEquals`, `assertTrue`) |
| **Turbine se mantiene** | Turbine es KMP-ready, funciona en commonTest sin cambios |
| **MockK se mantiene solo en androidTest** | MockK no soporta KMP. Los tests que usan MockK (PlacesDataSourceTest, GetAddressFromLocationUseCaseTest) se quedan en `androidUnitTest` |
| **Fakes a commonMain** | Las 14 fakes implementan interfaces que estarán en commonMain → las fakes también van a commonMain |
| **CoroutinesTestExtension se adapta** | Crear versión `kotlin.test` para commonTest. La versión JUnit5 se mantiene en androidMain para tests Android-specific |
| **BaseTest se queda en androidMain** | Usa `ApplicationProvider`, `createComposeExtension()` y Android resources — 100% Android |
| **Compose UI tests se quedan en androidTest** | CMP testing aún está madurando. Los 8 tests de UI se mantienen como `androidInstrumentedTest` |

### Migración por fase

#### Phase 1: `:core:model`
- **Estado**: Sin tests existentes
- **Acción**: Crear `src/commonTest/kotlin/` con tests para `FuelStation.formatDistance()`, `FuelStation.formatDirection()`, `FuelStation.formatName()` usando `kotlin.test`
- **Framework**: `kotlin.test` + `kotlinx-coroutines-test`

#### Phase 2: `:core:common`
- **Test existente**: 1 test con JUnit (verificar contenido)
- **Acción**: Migrar test a `commonTest` con `kotlin.test`. Añadir tests para `distanceTo()` (regresión con coordenadas conocidas) y `isStationOpen()` (varios horarios)
- **Nota**: `LocationUtils` tests van a `androidUnitTest` (dependen de Google Maps types)

#### Phase 3: `:core:database`
- **Tests existentes**: 5 DAO tests en `androidTest/` (FiltersDaoTest, FuelStationDaoTest, PriceAlertDaoTest, RecentSearchQueryDaoTest, UserDataDaoTest)
- **Acción**: Los 5 DAO tests **se mantienen en `androidInstrumentedTest`** — usan Room in-memory database con `Room.inMemoryDatabaseBuilder(context, ...)` que requiere Android Context
- **Añadir**: `commonTest` con test de compatibilidad de `ListConverters` (verificar que JSON generado por kotlinx-serialization es idéntico al de Moshi)
- **Regresión crítica**: Todos los DAO tests deben pasar sin cambios funcionales

#### Phase 4: `:core:data`, `:core:domain`, `:core:supabase`, `:core:notifications`

**`:core:data`**:
- **Test existente**: `OfflineFuelStationRepositoryTest.kt` (skeleton vacío con JUnit4 + MockK)
- **Acción**: Los tests de repositories que usen fakes (no MockK) van a `commonTest`. Tests que necesiten Android Context se quedan en `androidUnitTest`

**`:core:domain`**:
- **Tests existentes**:
  - `GetAddressFromLocationUseCaseTest.kt` — usa MockK → **se queda en `androidUnitTest`**
  - `GetFuelStationUseCaseTest.kt` — skeleton vacío
- **Acción**: Los use cases que no usen MockK se testan en `commonTest` con fakes. Los que usan MockK se mantienen en `androidUnitTest`

**`:core:network`** (ya KMP):
- **Tests existentes**:
  - `RemoteDataSourceTest.kt` — usa `NetworkMockEngine` (Ktor MockEngine) → **migrar a `commonTest`** (Ktor MockEngine es KMP)
  - `PlacesDataSourceTest.kt` — usa MockK para `PlacesClient` (Google Places) → **se queda en `androidUnitTest`**

#### Phase 5: `:core:testing`, `:mocknetwork`, `:navigation`

**`:core:testing`** — Migración de la infraestructura:

| Archivo | Destino | Cambios |
|---------|---------|---------|
| 14 fakes | `commonMain` | Sin cambios funcionales — implementan interfaces que ya estarán en commonMain |
| `CoroutineTestRuleExtension.kt` | `androidMain` | Se mantiene para tests JUnit5 Android |
| `CoroutinesTestRule.kt` | `androidMain` | Se mantiene para tests JUnit4 legacy |
| `BaseTest.kt` | `androidMain` | Se mantiene — usa Android Context y Compose test APIs |
| Nuevo: `CoroutineTestHelper.kt` | `commonMain` | Helper para `runTest` + `StandardTestDispatcher` compatible con `kotlin.test` |

**`:mocknetwork`**:
- **Acción**: Mover a commonMain. `MockWebServerManagerImp` carga JSON desde Android assets → **refactorizar para cargar desde commonMain resources** (usar `Res` de CMP o embebir JSONs como strings)
- `MockRemoteDataSource` y `MockWebServerManager` interfaz → commonMain sin cambios

#### Phase 6: `:core:ui`, `:core:uikit`, `:core:components`

**`:core:uikit`** — 5 Compose UI tests:
- `GasGuruAlertDialogTest`, `FuelStationItemTest`, `FuelListSelectionTest`, `RouteNavigationCardTest`, `SelectedItemTest`
- **Acción**: **Se mantienen en `androidInstrumentedTest`** — usan `BaseTest`, `ComposeContentTestRule`, Android resources
- Cuando CMP testing madure, evaluar migración a commonTest

**`:core:components`**:
- `GasGuruSearchBarViewModelTest.kt` — JUnit5 + Turbine + fakes
- **Acción**: Migrar a `commonTest` con `kotlin.test` + Turbine (no usa MockK ni Android APIs)

#### Phase 7: Features + App iOS

**ViewModel tests** (8 archivos) — todos usan JUnit5 + Turbine + CoroutinesTestExtension + fakes:

| Test | Usa MockK? | Destino |
|------|-----------|---------|
| `SplashViewModelTest` | No | `commonTest` |
| `StationMapViewModelTest` | No | `commonTest` |
| `DetailStationViewModelTest` | No | `commonTest` |
| `FavoriteListStationViewModelTest` | No | `commonTest` |
| `ProfileViewModelTest` | No | `commonTest` |
| `RoutePlannerViewModelTest` | No | `commonTest` |
| `NewOnboardingViewModelTest` | No | `commonTest` |
| `GasGuruSearchBarViewModelTest` | No | `commonTest` (Phase 6) |

Todos los ViewModel tests usan fakes (no MockK) → **todos migran a `commonTest`**.

**Compose Screen tests** (3 archivos) — usan BaseTest + Compose test APIs:
- `DetailStationScreenTest`, `FavoriteListScreenTest`, `OnboardingFuelPreferencesTest`, `ProfileScreenTest`
- **Acción**: **Se mantienen en `androidInstrumentedTest`**

### Resumen de destinos finales

| Source set | Qué contiene | Framework |
|-----------|-------------|-----------|
| `commonTest` | ViewModel tests, UseCase tests (sin MockK), Repository tests con fakes, DataSource tests (Ktor MockEngine), model tests, type converter tests | `kotlin.test` + Turbine + `kotlinx-coroutines-test` |
| `androidUnitTest` | Tests que usan MockK (PlacesDataSource, GetAddressFromLocationUseCase), tests que necesitan Android Context | JUnit5 + MockK + CoroutinesTestExtension |
| `androidInstrumentedTest` | DAO tests (Room in-memory), Compose UI tests (BaseTest) | JUnit5 + Room + Compose test + Turbine |
| `iosTest` | Tests de implementaciones iOS nativas (CLLocationManager, NWPathMonitor, etc.) | `kotlin.test` |

### Cambios en dependencias de testing

```kotlin
// commonTest (nuevo, para módulos KMP)
commonTest.dependencies {
    implementation(kotlin("test"))
    implementation(libs.kotlinx.coroutines.test)
    implementation(libs.turbine)
    implementation(projects.core.testing)  // fakes
}

// androidUnitTest (mantener para tests con MockK/JUnit5)
androidUnitTest.dependencies {
    implementation(libs.junit5.api)
    implementation(libs.junit5.engine)
    implementation(libs.mockk)
    implementation(libs.kotlinx.coroutines.test)
    implementation(projects.core.testing)
}

// androidInstrumentedTest (sin cambios)
androidInstrumentedTest.dependencies {
    implementation(libs.junit5.api)
    implementation(libs.junit5.extensions)
    implementation(libs.junit5.runner)
    implementation(libs.junit5.compose)
    implementation(libs.androidx.test.core)
    implementation(libs.turbine)
    implementation(projects.core.testing)
}
```

### Regla de oro
> **Ningún test se elimina durante la migración.** Cada test o se mueve a `commonTest` (si es Kotlin puro + fakes + Turbine) o se mantiene en `androidUnitTest`/`androidInstrumentedTest` (si usa MockK, Android Context, Room in-memory o Compose test APIs). Al finalizar cada fase, `./gradlew test` y `./gradlew connectedAndroidTest` deben pasar igual que antes.

---

## Riesgos y Mitigación

| Riesgo | Probabilidad | Impacto | Mitigación |
|--------|-------------|---------|------------|
| Room KMP: SQL incompatible en iOS SQLite | Baja | Alto | Testear todas las migraciones contra SQLite iOS. iOS son instalaciones nuevas (no hay BD previa) |
| ListConverters: JSON format Moshi ≠ kotlinx-serialization | Baja | Alto | Verificar que `["a","b"]` es idéntico en ambas libs. Escribir test de compatibilidad |
| cinterop con APIs iOS es complejo | Media | Medio | Empezar con stubs simples. Considerar escribir wrappers en Swift y exponerlos via @ObjCName |
| Build time KMP significativamente mayor | Alta | Medio | Habilitar Gradle build cache, configuration cache, incremental compilation. CI con más recursos |
| Compose Multiplatform: diferencias visuales Android vs iOS | Media | Medio | Testing visual frecuente en simulador iOS. Usar `@Preview` unificado de CMP 1.10 |
| Lottie sin soporte CMP | Cierta | Bajo | Evaluar compottie al inicio de Phase 6. Si no funciona, reemplazar con Compose animations |
| Google Maps no existe en CMP | Cierta | Alto | Diseñar la abstracción de mapa desde Phase 4 (interfaces). Implementar MapKit en Phase 7 |
| OneSignal sin SDK KMP | Cierta | Bajo | V1 iOS: no-op. V2: integrar SDK nativo iOS |

---

## Decisiones V2-Ready (tomar desde el principio)

1. **Navegación**: El `NavigationManager` basado en SharedFlow es agnóstico de plataforma → funciona para CarPlay y Web
2. **No usar Parcelize**: Usar `@Serializable` para argumentos de navegación → funciona en todas las plataformas
3. **No hardcodear Google Maps**: La abstracción `PlatformMapView` expect/actual soportará cualquier implementación futura
4. **Web targets en convention plugin**: Cuando se añada Web, solo hay que añadir `wasmJs()` al `KmpLibraryConventionPlugin`
5. **Resources CMP**: Usar el sistema de resources de Compose Multiplatform (`Res.string`, `Res.drawable`) desde Phase 6

---

## Verificación End-to-End

Para cada fase, ejecutar en este orden:
1. `./gradlew build` — compila todo el proyecto
2. `./gradlew test` — ejecuta tests unitarios
3. Instalar en dispositivo Android — verificar que funciona igual que antes
4. (Desde Phase 4) Compilar iOS framework — verificar que compila
5. (Desde Phase 7) Instalar en simulador iOS — verificar funcionalidad

---

## Phase 10: Tests de UI en CMP sin Emulador

**Objetivo**: Ejecutar todos los tests de Compose UI directamente en la Mac, sin emulador, en segundos — igual que Flutter widget tests.

**Dos sub-fases independientes**:
- **8A** — Añadir `jvm()` a los convention plugins + actuals de JVM en los módulos que lo necesitan (andamiaje).
- **8B** — Migrar los tests de UI de `androidTest`/`androidInstrumentedTest` a `commonTest` con `runComposeUiTest` (el payoff real).

### Contexto técnico

`runComposeUiTest` de CMP usa el renderer **Skia en JVM** (el mismo que en iOS), sin Android framework. Al combinarlo con el target `jvm()` en los módulos CMP:

```bash
./gradlew :core:uikit:jvmTest        # 8 tests de componentes — en segundos, sin emulador
./gradlew :feature:profile:jvmTest   # tests de pantalla — ídem
```

**Tests de ViewModel** (en `commonTest`): ya corren sin emulador hoy vía `testDebugUnitTest`. Añadir `jvm()` no los cambia — simplemente los hace ejecutables también con `:jvmTest`.

**Tests de UI actuales** (`androidTest`/`androidInstrumentedTest`): usan `createComposeExtension()` de `de.mannodermaus.junit5.compose` (Android-only). Ninguno usa el `Context` de Android — todos ya usan `getCmpString`/`Res.*`. El único cambio al migrar es reemplazar `extension.use { setContent { } }` por `runComposeUiTest { setContent { } }`.

### Restricción de Maestro

Maestro sigue necesitando emulador/device real para tests E2E (flujos de usuario completos). `jvm()` solo elimina el emulador para tests de componentes y pantallas (árboles semánticos). Los flows de Maestro permanecen en Android/iOS.

---

### Phase 10A — `jvm()` en convention plugins

**Objetivo**: hacer que todos los módulos KMP/CMP publiquen la variante `jvm`, prerequisito para Phase 8B.

**Estrategia**: añadir `jvm()` a `KmpLibraryConventionPlugin` y `KmpComposeLibraryConventionPlugin` (en lugar de hacerlo módulo a módulo). Todos los módulos KMP/CMP tienen ya su código Android/iOS-only correctamente en `androidMain`/`iosMain` — no rompe la compilación.

**Módulos con `expect`/`actual` manuales** que necesitan actuals en `jvmMain` (los demás no tienen `expect` o usan codegen KSP):

| Módulo | `expect` que necesita actual JVM | Actual JVM |
|--------|----------------------------------|-----------|
| `:core:common` | `ioDispatcher`, `getAppVersion` | ✅ Ya hecho (`jvmMain/`) |
| `:core:network` | `routesPlugin(packageName)` | No-op / `EmptyPlugin` |
| `:core:ui` | `ConfigureDialogSystemBars`, `fullScreenDialogProperties`, `rememberInAppReviewManager` | No-ops |
| `:core:uikit` | `SystemBarsEffect`, `ThemePreviews`, `maestroTestTag` | No-ops / `@Preview` vacío |
| `feature:detail-station` | `rememberNavigateToMapsAction`, `rememberNotificationPermissionRequester`, `rememberShareAction` | No-ops |
| `feature:station-map` | `rememberLocationPermissionState`, `PlatformMapView` | No-ops / `Box` vacío |

> **Regla de actuals JVM**: como el target `jvm()` existe únicamente para tests (no es un artefacto de producción), los actuals para APIs platform-specific (mapas, permisos, notificaciones) son no-ops mínimos que satisfacen el compilador.

**`KmpRoomConventionPlugin`**: añadir `kspJvm` ya se hace por módulo (`core:database` ya lo tiene). El convention plugin de Room no necesita cambiarse — Room no se usa en JVM en ningún otro módulo.

**Verificación**:
```bash
./gradlew :core:common:compileKotlinJvm          # ya pasa
./gradlew :core:network:compileKotlinJvm
./gradlew :core:ui:compileKotlinJvm
./gradlew :core:uikit:compileKotlinJvm
./gradlew :app:assembleDebug                     # regresión Android
./gradlew :composeApp:compileKotlinIosSimulatorArm64  # regresión iOS
```

**Progreso** ✅ Completado:

| Módulo | `jvm()` | `jvmMain` actuals |
|--------|---------|-------------------|
| `:core:model` | ✅ plugin (manual eliminado) | — sin expect/actual |
| `:core:database` | ✅ plugin (manual eliminado) | — Room KSP genera en `kspJvm` |
| `:core:common` | ✅ plugin | ✅ `IoDispatcher` + `AppVersion` |
| `:core:network` | ✅ plugin | ✅ `routesPlugin` no-op |
| `:core:ui` | ✅ plugin | ✅ `ConfigureDialogSystemBars`, `fullScreenDialogProperties`, `rememberInAppReviewManager` |
| `:core:uikit` | ✅ plugin | ✅ `SystemBarsEffect`, `ThemePreviews`, `maestroTestTag` |
| `feature:detail-station` | ✅ plugin | ✅ `rememberNavigateToMapsAction`, `rememberNotificationPermissionRequester`, `rememberShareAction` |
| `feature:station-map` | ✅ plugin | ✅ `rememberLocationPermissionState`, `PlatformMapView` |
| Resto KMP/CMP (sin expect manual) | ✅ plugin | — no necesitan actuals |
| `:composeApp` | excluido (guard `name != "composeApp"`) | — cocoapods, no ejecuta tests |

**Dependencias JVM que se expusieron como `api`** (para evitar errores de classpath en el target JVM):
- `core:common`: `kotlinx.coroutines.core` → cambiado de `implementation` a `api`
- `navigation`: `jetbrains.navigation.compose` → cambiado de `implementation` a `api`
- `KmpComposeLibraryConventionPlugin`: añadido `jetbrains.lifecycle.runtime.compose` como `api` en commonMain

**Lección aprendida**: los módulos KMP que usaban `kotlinx.coroutines.flow.Flow` directamente sin declarar `implementation(kotlinx.coroutines.core)` en su `commonMain` compilaban bien para Android/iOS (resolución más permisiva) pero fallaban en JVM (resolución Gradle estricta). La solución de raíz fue cambiar la dep en `core:common` a `api` para que se propague correctamente a todos los consumidores en JVM.

---

### Phase 10B — Migrar tests de UI a `commonTest`

**Objetivo**: mover los 10 archivos de test de UI de `androidTest`/`androidInstrumentedTest` a `commonTest`, reemplazando `BaseTest` + `createComposeExtension` por `runComposeUiTest` de CMP.

**Tests a migrar**:

| Test | Módulo | Source set actual |
|------|--------|-------------------|
| `GasGuruAlertDialogTest` | `:core:uikit` | `androidTest` |
| `FuelStationItemTest` | `:core:uikit` | `androidTest` |
| `FuelListSelectionTest` | `:core:uikit` | `androidTest` |
| `FuelTypeChipTest` | `:core:uikit` | `androidTest` |
| `NumberWheelPickerTest` | `:core:uikit` | `androidTest` |
| `RouteNavigationCardTest` | `:core:uikit` | `androidTest` |
| `SelectedItemTest` | `:core:uikit` | `androidTest` |
| `TankCostCardTest` | `:core:uikit` | `androidTest` |
| `FavoriteListScreenTest` | `:feature:favorite-list-station` | `androidTest` |
| `ProfileScreenTest` | `:feature:profile` | `androidTest` |
| `DetailStationScreenTest` | `:feature:detail-station` | `androidInstrumentedTest` |
| `OnboardingFuelPreferencesTest` | `:feature:onboarding` | `androidInstrumentedTest` |

**Cambios en `BaseTest`** (en `core:testing`):
- Crear `BaseTest` en `commonTest` usando `runComposeUiTest { }` de CMP.
- El `testContext: Context` y `getStringResource(@StringRes)` se eliminan — ningún test los usa.
- `getCmpString` se mantiene (es ya CMP-compatible).
- La versión Android de `BaseTest` en `androidMain` puede eliminarse o mantenerse vacía.

**Cambio por test** (mecánico, mismo patrón en todos):
```kotlin
// Antes
fun myTest() = extension.use {
    setContent { MyComposable(...) }
    onNodeWithText("...").assertIsDisplayed()
}

// Después
@Test
fun myTest() = runComposeUiTest {
    setContent { MyComposable(...) }
    onNodeWithText("...").assertIsDisplayed()
}
```

**Dependencias a añadir** en módulos CMP con tests de UI:
```kotlin
// commonTest
implementation(compose.uiTest)

// jvmTest (renderer Skia para desktop)
implementation(compose.desktop.currentOs)
```

**Eliminar**:
- `de.mannodermaus.junit5.compose` de `core:testing` y todos los módulos que lo usen.
- `exclude("**/GasGuruSearchBarContentTest*")` en `core/components/build.gradle.kts`.

**Verificación**:
```bash
./gradlew :core:uikit:jvmTest                    # 8 tests de componentes en JVM
./gradlew :feature:profile:jvmTest               # ProfileScreenTest en JVM
./gradlew :feature:detail-station:jvmTest        # DetailStationScreenTest en JVM
./gradlew :app:assembleDebug                     # regresión Android
```

---

## Phase 9: iOS Feature Parity con Android

**Objetivo**: Sustituir los stubs no-op iOS (creados durante Phase 4-7 para no bloquear la migración) por implementaciones nativas reales, hasta que una build iOS instalada en un iPhone real ofrezca la misma funcionalidad que la Android.

### Estado actual (inventario de stubs)

| # | Módulo | Stub actual | Funcionalidad pendiente | Impacto |
|---|--------|------------|-------------------------|---------|
| 1 | `:core:data` NetworkMonitor | ~~`flowOf(true)` siempre~~ | ~~Detección de conectividad real~~ | ✅ Phase 9A |
| 2 | `:core:data` Geocoder | ~~`flowOf(null)`~~ | ~~Reverse geocoding (coords → dirección)~~ | ✅ Phase 9A |
| 3 | `:core:data` LocationTracker | ~~`null`/false en flows~~ | ~~Ubicación del usuario + permisos~~ | ✅ Phase 9B |
| 4 | `:core:data` PlacesRepository | ~~`emptyList()`~~ | ~~Búsqueda de lugares~~ | ✅ Phase 9D |
| 4b | `:core:data` RoutesRepository | ~~`flowOf(null)` stub~~ | ~~Cálculo de ruta entre dos puntos~~ | ✅ Phase 9D (post-fix, MKDirections) |
| 5 | `:feature:station-map` PlatformMapView | ~~`Box` con color de fondo~~ | ~~Mapa interactivo con marcadores~~ | ✅ Phase 9C (MVP) — polish en 9C.2 |
| 6 | `:feature:station-map` LocationPermission | ~~`isGranted=true` siempre~~ | ~~Pedir permiso de ubicación~~ | ✅ Phase 9B |
| 7 | `:feature:detail-station` MapsNavigation | ~~Lambda vacía~~ | ~~Abrir Maps externo~~ | ✅ Phase 9E |
| 8 | `:feature:detail-station` ShareAction | ~~Lambda vacía~~ | ~~Compartir contenido~~ | ✅ Phase 9E |
| 9 | `:feature:detail-station` NotificationPermission | ~~`onGranted()` sin pedir~~ | ~~Pedir permiso de notificaciones~~ | ✅ Phase 9E |
| 10 | `:core:ui` InAppReviewManager | ~~No-op~~ | ~~Solicitar review en store~~ | ✅ Phase 9F |
| 11 | `:core:analytics` | ~~`NoOpAnalyticsHelper`~~ | ~~Métricas iOS~~ | ✅ Phase 9G |
| 12 | `:core:notifications` OneSignal | ~~No-op total~~ | ~~Push notifications~~ | ✅ Phase 9H |

**Conscientemente OK como no-op** (no son trabajo de Phase 9):
- `:core:ui` `ConfigureDialogSystemBars` — iOS gestiona system bars automáticamente
- `:core:uikit` `SystemBarsEffect` — idem

### Regla transversal: Investigación técnica al iniciar cada sub-fase

**Antes de empezar a codear cualquier sub-fase 9A-9I, el primer paso es una investigación corta que evalúe las opciones técnicas disponibles y elija la mejor según el contexto del proyecto.** No prescribir aquí la solución porque varias sub-fases tienen alternativas válidas con trade-offs reales:

- **Mapa y Places**: ¿APIs nativas Apple (MapKit + MKLocalSearch) o SDK Google iOS (Google Maps SDK + Google Places SDK)? Las nativas son sin coste y se integran sin pods complejos; las de Google dan paridad visual exacta con Android, reutilizan la API key existente y tienen mejor calidad de POI en España. **A decidir al iniciar 9C/9D.**
- **Analytics**: ¿cinterop directo del pod `Mixpanel-swift` (lo que hubo antes de 8A) o wrapper Swift expuesto vía `@ObjCName`? **A decidir al iniciar 9G.**
- **Push notifications**: ¿pod `OneSignalXCFramework` directo o solo OneSignal Swift y bridge mínimo en Kotlin? **A decidir al iniciar 9H.**
- **Permisos en iOS**: ¿usar API `expect/actual` ya existente (permission state expuesto al Composable) o crear un wrapper común `PermissionManager` que abstraiga ubicación/notificaciones? **A decidir al iniciar 9B/9E.**

En cada sub-fase se lista qué decisiones técnicas hay que cerrar antes de implementar. **No se procede a implementar hasta tener esas decisiones documentadas en el PR description o en el `docs/KMP_PHASE9X.md` borrador correspondiente.**

### Orden de ejecución

```
9A NetworkMonitor + Geocoder        [Foundation APIs, sin pods, sin permisos]       ✅
   ↓
9B LocationTracker + permisos       [CoreLocation + Info.plist]                     ✅
   ↓
9C Mapa interactivo (MVP)           [MapKit — DECIDIDO: MapKit nativo]              ✅
   ↓
9D Places                           [Google Places SDK iOS — DECIDIDO: Google Places]  ✅
   ↓
9C.2 Map polish                     [Clustering + custom markers con precio/logo]
   ↓
9E MapsNav + Share + NotifPerm      [iOS standard APIs]                     ✅
   ↓
9F In-App Review                    [StoreKit]                          ✅
   ↓
9G Analytics Mixpanel               [Swift bridge via @ObjCName — DECIDIDO]      ✅
   ↓
9H Push OneSignal                   [Swift bridge, sin Extension]                ✅
   ↓
9J Background sync iOS              [SyncManager + IosBridge + BGTaskScheduler]  ✅
   ↓
9I Validación E2E + Phase 9 doc
```

---

### Phase 9A — Foundation APIs (NetworkMonitor + Geocoder) ✅ COMPLETADA

**Stubs cerrados**: #1 NetworkMonitor, #2 Geocoder. Ver `docs/KMP_PHASE9A.md`.

**Decisiones tomadas**:
- `NWPathMonitor` (`platform.Network.*`) vía `callbackFlow` + `awaitClose` — sin pods, cinterop automático.
- `CLGeocoder.reverseGeocodeLocation` + campos `thoroughfare`/`locality`/`postalCode` de `CLPlacemark` — evita dependencia de cross-framework entre CoreLocation y Contacts.
- Error → `null` (DetailStationViewModel ya maneja null gracefully).

**Archivos modificados**:
- `core/data/src/iosMain/.../util/NetworkMonitorIos.kt`
- `core/data/src/iosMain/.../repository/geocoder/GeocoderAddressIos.kt`
- `core/data/src/iosMain/.../di/IosDataModule.kt` (inyección de `ioDispatcher`)

**Verificación**: modo avión en simulador → app detecta offline; pantalla de detalle muestra dirección legible.

---

### Phase 9B — Location + permisos (CLLocationManager) ✅ COMPLETADA

**Stubs cerrados**: #3 LocationTracker, #6 LocationPermission. Ver `docs/KMP_PHASE9B.md`.

**Decisiones tomadas**:
- `NSObject` Kotlin/Native implementando `CLLocationManagerDelegateProtocol` directamente — sin Swift bridge.
- Permission flow **lazy** (en StationMapScreen, paridad con Android; onboarding no pide ubicación en ninguna plataforma).
- Solo `When In Use` — espeja Android FINE+COARSE foreground.
- `LocalOpenLocationSettings` (`staticCompositionLocalOf`) en `core.ui/commonMain` — elimina prop drilling por 8 archivos intermedios.
- `withContext(Dispatchers.Main)` en `getCurrentLocation()` para creación segura de `CLLocationManager`.
- `Info.plist` con `NSLocationWhenInUseUsageDescription` en inglés (localización `es.lproj` pendiente para V1.5).

**Archivos modificados**:
- `core/data/src/iosMain/.../location/LocationTrackerIos.kt` (implementación real)
- `core/data/src/iosMain/.../di/IosDataModule.kt` (inyección `ioDispatcher`)
- `core/ui/src/commonMain/.../LocalOpenLocationSettings.kt` (nuevo)
- `feature/station-map/src/iosMain/.../platform/LocationPermission.kt` (implementación real)
- `iosApp/iosApp/Info.plist` (`NSLocationWhenInUseUsageDescription`)
- `composeApp/src/commonMain/.../App.kt`, `GasGuruApp.kt`, `GasGuruNavHost.kt`, `NavigationBarNavigation.kt`, `NavigationBarScreen.kt` (refactor param drilling)
- `feature/favorite-list-station/.../FavoriteStationListGraph.kt`, `FavoriteStationListNavigation.kt`, `FavoriteStationListScreen.kt` (refactor param drilling)

**Verificación**: simulador pide permiso al acceder al mapa; denegado muestra UI correcta con botón "Abrir Ajustes"; aceptado centra mapa sobre ubicación del simulador.

---

### Phase 9C — Mapa interactivo (MVP) ✅ COMPLETADA

**Stub cerrado**: #5 PlatformMapView. Ver `docs/KMP_PHASE9C.md`.

**Decisión**: **MapKit nativo** — `UIKitView<MKMapView>` con cinterop automático `platform.MapKit.*`. Sin pods, sin API key, framework del sistema → cero coste de mantenimiento. La paridad visual con Android (markers con precio y clustering) se aborda en 9C.2.

**Decisiones técnicas tomadas**:
- `MKMapViewDelegateProtocol` implementado como `NSObject` Kotlin/Native (sin Swift bridge). Misma técnica que 9B.
- `StationAnnotation: NSObject(), MKAnnotationProtocol` con `stationId: Int` para asociar el ID al marcador y disparar `onStationClick` en `didSelectAnnotationView`.
- Diff idempotente de annotations (por `idServiceStation`) en el `update` block de `UIKitView`.
- Ruta: `currentPolyline` guardada en el delegate para `removeOverlay`/`addOverlay` sin consultar `mv.overlays`.
- `allocArray<CLLocationCoordinate2D>(n) { index -> latitude = ...; longitude = ... }` para el array C de la polyline — `allocArrayOf` no soporta structs, la aritmética `CPointer + Int` no está definida para `CStructVar`.
- Extension functions de ObjC categories (`addOverlay`, `removeOverlay`) requieren import explícito de `platform.MapKit`.
- `LaunchedEffect` fuera del `update` block para centrado reactivo (mapBounds y userLocationToCenter).
- Dark mode automático: `MKMapView` sigue el sistema sin `overrideUserInterfaceStyle` (las constantes `UIUserInterfaceStyleDark/Light` no son accesibles como importaciones directas en K/N 2.2.x).

**Archivos modificados**:
- `feature/station-map/src/iosMain/kotlin/.../platform/PlatformMapView.kt` (implementación completa)
- `feature/station-map/src/iosMain/kotlin/.../platform/Mappers.kt` (nuevo: `toCLLocationCoordinate2D`, `toMKCoordinateRegion`, `toMKCoordinateRegionCentered`, `createMKPolyline`)

**Verificación**: `compileKotlinIosSimulatorArm64` ✅, `compileDebugKotlinAndroid` ✅, `testDebugUnitTest` ✅, `composeApp:compileKotlinIosSimulatorArm64` ✅, `app:assembleProdDebug` ✅, `codeCheck` ✅.

---

### Phase 9C.2 — iOS Map Polish (clustering + custom markers)

**Stub a cerrar**: paridad visual con Android (markers con precio + logo, clustering).

**Investigación previa requerida**:
- **Custom markers**: ¿render offscreen del `StationMarker` composable como bitmap (vía `ComposeImageLoader` o `Canvas` nativo) o `UIImage` compuesto programáticamente via Core Graphics (dibujar fondo + precio + logo sin Compose)? La opción Compose offscreen reutiliza el componente existente pero requiere un bridge de renderizado. La opción Core Graphics es más predecible en threading pero duplica la lógica visual.
- **Clustering**: `MKClusterAnnotation` nativo con `clusteringIdentifier` en `MKMarkerAnnotationView`. Decidir qué información muestra el cluster marker (número de estaciones, precio medio, precio mínimo).
- **Reuse pool**: con custom markers, la dequeue con `STATION_MARKER_REUSE_ID` puede devolver una vista con la imagen del marker anterior. Diseñar la actualización idempotente de `UIImage` en `viewForAnnotation`.

**Archivos previstos**:
- `feature/station-map/src/iosMain/kotlin/.../platform/PlatformMapView.kt` (actualizar `viewForAnnotation`)
- Posible helper `StationMarkerImageRenderer.kt` (iosMain) para generar `UIImage` del marker

**Verificación**: markers muestran precio y colores de categoría igual que Android; grupos de estaciones se agrupan con cluster marker; performance estable con 200+ annotations.

---

### Phase 9D — Búsqueda de lugares

**Stub a cerrar**: #4 PlacesRepository.

**Investigación previa requerida (DECISIÓN GRANDE)**:
- **Opción A — MKLocalSearch (MapKit)**: sin coste, sin SDK externo, sin API key. Calidad de POI variable (en España aceptable para gasolineras, peor para nombres comerciales).
- **Opción B — Google Places SDK iOS (pod `GooglePlaces`)**: paridad exacta con Android (mismos resultados), reutiliza API key. Tiene coste por request en producción.
- **Criterio**: alineado con la decisión de 9C — Google Maps en 9C casi obliga a Google Places en 9D para coherencia.

**Archivos previstos**:
- `core/data/src/iosMain/.../repository/places/PlacesRepositoryIos.kt`
- Posible refactor de `Place` model en `commonMain` si el contrato necesita ajustes (ej. MKLocalSearch no tiene "place IDs" persistentes).

**Verificación**: search bar iOS → escribir → resultados; seleccionar → centra mapa.

---

### Phase 9E — Platform actions del detalle (Maps + Share + Notification permission) ✅ COMPLETADA

**Stubs cerrados**: #7 MapsNavigation, #8 ShareAction, #9 NotificationPermission. Ver `docs/KMP_PHASE9E.md`.

**Decisiones tomadas**:
- MapsNavigation: `UIAlertController.actionSheet` con Apple Maps siempre + Google Maps/Waze si `canOpenURL` los detecta. `LSApplicationQueriesSchemes` en Info.plist.
- ShareAction: `UIActivityViewController` con el texto preconstruido por `buildShareText`. `topMostViewController()` como helper local a la feature.
- NotificationPermission: `UNUserNotificationCenter.getNotificationSettings` + branching por status (`Authorized/Provisional` → directo, `NotDetermined` → `requestAuthorization`, `Denied` → abrir Settings). Callbacks marshaled a `Dispatchers.Main` (L031). `rememberCoroutineScope()` para cancelación automática.

**Archivos modificados**:
- `feature/detail-station/src/iosMain/.../platform/IosUiHelpers.kt` (NUEVO)
- `feature/detail-station/src/iosMain/.../platform/MapsNavigation.kt`
- `feature/detail-station/src/iosMain/.../platform/ShareAction.kt`
- `feature/detail-station/src/iosMain/.../platform/NotificationPermission.kt`
- `iosApp/iosApp/Info.plist` (`LSApplicationQueriesSchemes`)

**Verificación**: compilación iOS + Android ✅; `codeCheck` ✅; prueba manual simulador: "Cómo llegar" → action sheet con Apple Maps; "Compartir" → share sheet nativo; "Activar alerta" → prompt nativo iOS.

---

### Phase 9F — In-App Review ✅ COMPLETADA

**Stub cerrado**: #10 InAppReviewManager. Ver `docs/KMP_PHASE9F.md`.

**Decisiones tomadas**:
- `SKStoreReviewController.requestReviewInScene(UIWindowScene)` — disponible iOS 14+, cubre deployment target 15.0. Sin CocoaPods: StoreKit es framework del sistema accesible desde cinterop estándar K/N.
- `expect class InAppReviewManager` → `interface InAppReviewManager` en commonMain — alineado con el patrón canónico del proyecto. `@Composable expect fun rememberInAppReviewManager()` se mantiene (factory platform-specific legítimo). Call site en `DetailStationScreen` sin cambios.
- `onReviewCompleted()` siempre tras `requestReviewInScene` (fire-and-forget). Apple no expone si el prompt se mostró o si fue suprimido por quota.
- Prompt aparece detrás del dialog de detalle (limitación conocida: overlay UIWindow con windowLevel superior). Aceptado para V1.

**Archivos modificados**:
- `core/ui/src/commonMain/.../review/InAppReviewManager.kt` (expect class → interface)
- `core/ui/src/androidMain/.../review/InAppReviewManagerAndroid.kt` (NUEVO)
- `core/ui/src/androidMain/.../review/InAppReviewExt.kt` (constructor renombrado)
- `core/ui/src/iosMain/.../review/InAppReviewManagerIos.kt` (NUEVO — implementación StoreKit)
- `core/ui/src/iosMain/.../review/InAppReviewExt.kt` (constructor renombrado)

**Verificación**: `assembleProdDebug` ✅, `compileKotlinIosSimulatorArm64` ✅, `testDebugUnitTest` ✅; simulador → favorito → prompt nativo App Store.

---

### Phase 9G — Analytics (Mixpanel iOS)

**Stub a cerrar**: #11 — restaurar `MixpanelAnalyticsHelperIos` eliminado en Phase 8A.

**Investigación previa requerida**:
- Confirmar versión del pod `Mixpanel-swift` (4.x). Revisar changelog para breaking changes desde Phase 8A.
- Cinterop: verificar que las APIs principales (`Mixpanel.initialize`, `Mixpanel.track`, `Mixpanel.people.set`) son accesibles como ObjC headers desde Kotlin/Native.
- ¿Token Mixpanel iOS: mismo proyecto que Android o separado? Decisión de producto. Si separado, añadir clave a BuildKonfig.
- Impacto en tamaño binario y tiempo de build CI (¿requiere `pod install` en pipeline?).

**Archivos previstos**:
- `core/analytics/build.gradle.kts` (añadir `cocoapods { pod("Mixpanel-swift") }`)
- `core/analytics/src/iosMain/.../MixpanelAnalyticsHelperIos.kt` (recrear)
- `core/analytics/src/iosMain/.../di/AnalyticsModuleIos.kt` (sustituir `NoOpAnalyticsHelper`)

**Verificación**: arrancar app iOS → eventos `app_opened`, `screen_viewed` visibles en Mixpanel dashboard.

---

### Phase 9H — Push Notifications (OneSignal iOS) — OPCIONAL V1

**Stub a cerrar**: #12 OneSignalManagerIos + PushNotificationServiceIos.

**Por qué OPCIONAL**: requiere Notification Service Extension target en Xcode, APNs cert, pod OneSignal, y testing en device real (push no funciona en simulador). Puede aplazarse a V1.5.

**Investigación previa requerida**:
- ¿Pod `OneSignalXCFramework` 5.x vía CocoaPods? (Swift Package Manager no soportado por Kotlin/Native.)
- ¿Crear Notification Service Extension target en Xcode (rich notifications) o saltarlo en V1?
- Setup APNs: Apple Developer + OneSignal dashboard.
- Bridge Swift mínimo en `iosApp/iosApp/AppDelegate.swift` para lifecycle APNs.

**Archivos previstos**:
- `core/notifications/build.gradle.kts`
- `core/notifications/src/iosMain/.../OneSignalManagerIos.kt`
- `core/notifications/src/iosMain/.../PushNotificationServiceIos.kt`
- `iosApp/iosApp/iOSApp.swift` (init OneSignal)
- `iosApp/iosApp/AppDelegate.swift` (crear si no existe)
- Posible extension target en Xcode project

**Verificación**: device iOS real (no simulador) → recibir push de alerta de precio.

---

### Phase 9J — iOS background sync ✅ COMPLETADA

**Contexto**: tras completar 9A-9H, iOS seguía sin sincronizar datos en background. Había dos huecos:

- **Hueco A (bug)**: `SyncManager.execute()` (sincroniza alertas de precio offline cuando vuelve la conectividad) nunca se llamaba en iOS — solo en `GasGuruApplication.initSyncManager()` de Android.
- **Hueco B (paridad)**: `StationSyncWorker` (WorkManager periódico, 30 min con constraint de red) no tenía equivalente iOS.

**Decisión**: paridad completa — cerrar A y B en la misma rama. Ver `docs/KMP_PHASE9J.md`.

**Parte A — SyncManager (fix del bug)**:
- `KoinInit.kt` (iosMain): añadido `koin.get<SyncManager>().execute()` justo antes de devolver el bridge. `SyncManager` ya vivía en commonMain y estaba en el grafo Koin de iOS — solo faltaba invocarlo.

**Parte B — Refresco periódico (BGTaskScheduler)**:
- `IosBridge.kt`: `fun interface` → `interface`; añadido `fun refreshStations(onComplete: (Boolean) -> Unit)`.
- `IosBridgeImpl.kt`: inyecta `GetFuelStationUseCase` + `CoroutineScope(APPLICATION_SCOPE)`; `refreshStations` lanza corrutina, llama `getFuelInAllStations()`, devuelve `true`/`false` al callback via `withContext(Dispatchers.Main)`.
- `AppShellModule.kt`: `IosBridgeImpl` wired con las nuevas deps.
- `Info.plist`: `BGTaskSchedulerPermittedIdentifiers` con `com.gasguru.stationsync`; `UIBackgroundModes` con `fetch`.
- `iOSApp.swift`: registra handler `BGAppRefreshTask`, programa la tarea (`earliestBeginDate` = 30 min), re-encola en `applicationDidEnterBackground`, llama `bridge.refreshStations` desde `handleStationSync`.

**Archivos modificados**:

| Archivo | Cambio |
|---------|--------|
| `composeApp/src/iosMain/.../di/KoinInit.kt` | `koin.get<SyncManager>().execute()` |
| `composeApp/src/commonMain/.../bridge/IosBridge.kt` | `fun interface` → `interface` + `refreshStations` |
| `composeApp/src/commonMain/.../bridge/IosBridgeImpl.kt` | Implementar `refreshStations`, deps `GetFuelStationUseCase` + `CoroutineScope` |
| `composeApp/src/commonMain/.../di/AppShellModule.kt` | Nuevas deps en `IosBridgeImpl(...)` |
| `composeApp/src/commonTest/.../bridge/IosBridgeImplTest.kt` | 2 tests nuevos para `refreshStations` |
| `iosApp/iosApp/Info.plist` | `BGTaskSchedulerPermittedIdentifiers`, `UIBackgroundModes` |
| `iosApp/iosApp/iOSApp.swift` | Registro + scheduling + handler de `BGAppRefreshTask` |

**Verificación**:

```
./gradlew :composeApp:compileKotlinIosSimulatorArm64   ✅
./gradlew :composeApp:testDebugUnitTest                ✅ (3 tests: handlePushTap + 2 refreshStations)
./gradlew :app:assembleProdDebug                       ✅
```

**Nota de paridad**: BGAppRefreshTask trata `earliestBeginDate` como límite inferior, no como intervalo garantizado — el SO decide cuándo ejecutar según uso de la app y carga del sistema. Es el comportamiento esperado de `BGAppRefreshTask` (equivalente al constraint de WorkManager), no un bug.

---

### Phase 9I — Validación end-to-end + bug bash iOS

**No es código — es proceso.** Con stubs 9A-9G cerrados (9H opcional), ejercitar el flujo completo en simulador iPhone 15 (iOS 17+):

- Onboarding: fuel type, vehicle, permiso de ubicación
- Mapa: estaciones cercanas, pan/zoom, click → detalle
- Detalle: dirección legible, "Cómo llegar" abre Maps, compartir abre sheet, alerta de precio pide permiso
- Search: escribir lugar, ver resultados, seleccionar
- Favoritos: marcar/desmarcar persiste
- Profile: tema dark/light, edit
- Network: modo avión → snackbar offline
- Background → foreground: tracking analytics correcto

Documentar bugs en GitHub issues etiqueta `ios`. Cuando el checklist pase → **iOS V1 funcional, paridad con Android**.

**Output**: `docs/KMP_PHASE9.md` recopilando decisiones técnicas tomadas, checklist de paridad y screenshots iOS vs Android lado a lado.

---

### Post-Phase 9: Phase 8E (limpieza `:app`) ✅ COMPLETADA

`initKoin()` Android movido de `:app/GasGuruApplication` → `composeApp/androidMain/KoinInit.kt` como espejo del `KoinInit.kt` iOS. `SessionAnalyticsExt` movido a `composeApp/commonMain`. Deps redundantes de `:app/build.gradle.kts` eliminadas (features + varios `core.*` que ya llegan transitivamente). `KoinModulesTest` en `:app` mantiene acceso a todos los módulos vía `testImplementation`. Ver `docs/KMP_PHASE8E.md`.

---

## Próximos Pasos Inmediatos

1. **Hoy**: Crear la rama `feature/kmp-migration` desde `develop`
2. **Phase 0**: Crear los convention plugins nuevos (~1-2 días)
3. **Phase 1**: Migrar `:core:model` (~1 día, cambio mínimo)
4. **Phase 2**: Migrar `:core:common` (~2-3 días, primer contacto real con expect/actual)
5. **Validar**: Compilar proyecto completo tras cada fase

---

**Estado actual (post Phase 9H + Routes fix)**: las Phases 0-8E están completadas. Las sub-fases 9A, 9B, 9C (MVP), 9D, 9E, 9F, 9G y 9H están completadas — **todos los stubs de iOS están cerrados**, incluyendo `RoutesRepository` (fix post-9D con `MKDirections` nativo). La app iOS tiene: mapa con estaciones tap-eables, polyline de ruta calculada con `MKDirections` (MapKit nativo), punto de ubicación, búsqueda de lugares (Google Places), acciones del detalle (abrir Maps, compartir, pedir permiso de notificaciones), review nativa de App Store, métricas reales de Mixpanel y push notifications via OneSignal. El siguiente paso es **Phase 9I** (validación E2E: ejercitar todos los flujos en iPhone físico, documentar bugs, escribir `docs/KMP_PHASE9.md`) o **Phase 9C.2** (map polish: clustering + markers con precio/logo en MapKit).