# KMP Phase 8E — Limpieza :app + initKoin a composeApp/androidMain

## Objetivo

Cerrar la simetría Android/iOS en el wiring de Koin: mover `initKoin()` de `:app/GasGuruApplication` a `composeApp/androidMain`, espejando el `KoinInit.kt` ya existente en `composeApp/iosMain`. Tras esta fase, ambas plataformas arrancan Koin desde el mismo módulo (`:composeApp`) y `:app` queda como un entry point Android fino.

---

## Decisiones tomadas

### 1. Firma de `initKoin` Android

```kotlin
fun initKoin(
    application: Application,
    platformModules: List<Module> = emptyList(),
    enableDebug: Boolean = false,
)
```

Espejo de la firma iOS (`fun initKoin(platformModules: List<Module>): IosBridge`). El parámetro `platformModules` cubre lo flavor-specific (`remoteDataSourceModule()` desde `app/src/prod/` o `app/src/mock/`) y lo consumidor-específico de `:app` (`appModule()` con `WidgetFavoriteSyncManager` + `MixpanelAPI`). La alternativa de `Application.initKoin()` extension function se descartó para mantener paridad estructural con iOS.

### 2. `SessionAnalyticsExt` a `composeApp/commonMain`

`trackAppOpened()` solo depende de `AnalyticsHelper` (ya KMP). Se movió a `composeApp/commonMain/kotlin/com/gasguru/analytics/` manteniendo el mismo package para evitar cambios en los importes de `MainActivity`. `WorkerAnalyticsExt` se queda en `:app` — solo la usa `StationSyncWorker` (WorkManager Android-only).

### 3. Deps de `:app/build.gradle.kts`

Se eliminaron de `implementation` todas las features y los `core.*` que solo eran necesarios para que `GasGuruApplication.initKoin()` compilara. Se añadieron las mismas como `testImplementation` para mantener `KoinModulesTest` funcional (verifica el grafo Koin completo en JVM).

---

## Archivos modificados

| Archivo | Cambio |
|---------|--------|
| `composeApp/src/androidMain/kotlin/com/gasguru/composeApp/KoinInit.kt` | NUEVO — espejo del iOS |
| `composeApp/src/commonMain/kotlin/com/gasguru/analytics/SessionAnalyticsExt.kt` | NUEVO — movido desde `:app` |
| `composeApp/build.gradle.kts` | `androidMain.dependencies` añadido (koin.android, core.analytics, core.database, core.network, core.notifications, core.supabase, core.components) |
| `app/src/main/java/com/gasguru/GasGuruApplication.kt` | `initKoin()` local eliminado; llama al nuevo del composeApp |
| `app/src/main/java/com/gasguru/analytics/SessionAnalyticsExt.kt` | ELIMINADO (movido a composeApp) |
| `app/build.gradle.kts` | 12 deps `implementation` removidas; 9 features/core añadidas como `testImplementation` |
| `CLAUDE.md` | Entrada de la tabla de docs para Phase 8E añadida |
| `docs/KMP_MIGRATION.md` | `Post-Phase 9: Phase 8E` marcado como ✅ COMPLETADA |

---

## Estructura resultante

```
:app                           Android application (entry point fino)
  ├── GasGuruApplication       Bootstrap SDKs (OneSignal, Mixpanel, Clarity, WorkManager, widget)
  │   └── initKoin(...)        ← llama a composeApp/androidMain/KoinInit
  └── MainActivity             UI container (splash + App())

:composeApp                    KMP library (shared DI + UI)
  ├── commonMain
  │   ├── App.kt + NavHost     Compose UI compartida
  │   └── analytics/SessionAnalyticsExt.kt  ← MOVIDO desde :app
  ├── androidMain
  │   └── KoinInit.kt          ← NUEVO (espejo de iosMain)
  └── iosMain
      └── KoinInit.kt          (sin cambios)
```

---

## Verificación

- `:composeApp:compileDebugKotlinAndroid` ✅
- `:composeApp:compileKotlinIosSimulatorArm64` ✅
- `:app:assembleProdDebug` ✅
- `:app:assembleProdRelease` ✅
- `:app:assembleMockDebug` ✅
- `:composeApp:testDebugUnitTest` ✅
- `:app:testProdDebugUnitTest` ✅ (incluye `KoinModulesTest`)
- `codeCheck` ✅

---

## Próxima fase

**Phase 9I** — Validación E2E iOS: ejercitar todos los flujos en simulador/device, documentar bugs, escribir `docs/KMP_PHASE9.md`.

**Phase 9C.2** — Map polish iOS: custom markers con precio/logo + clustering en MapKit.
