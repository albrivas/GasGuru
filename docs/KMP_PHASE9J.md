# KMP Phase 9J — iOS Background Sync

## Objetivo

Cerrar la paridad de sincronización de datos entre Android e iOS:

- **Parte A (bug)**: arrancar `SyncManager` en iOS para que las alertas de precio creadas offline se sincronicen al recuperar conectividad.
- **Parte B (paridad)**: implementar refresco periódico de la DB de estaciones en background mediante `BGTaskScheduler`, equivalente al `StationSyncWorker` WorkManager de Android.

---

## Contexto

Tras completar las fases 9A-9H, iOS tenía paridad funcional en ubicación, mapas, búsqueda, analytics y push. Sin embargo, dos mecanismos de sync de Android no tenían equivalente iOS:

1. **`SyncManager`** (`core/data/commonMain`) — observa `NetworkMonitor.isOnline` y llama a `priceAlertRepository.sync()` al recuperar red. Ya vivía en commonMain y el grafo Koin de iOS lo incluía, pero nadie invocaba `execute()`.

2. **`StationSyncWorker`** (`app/src/main/java`, WorkManager) — tarea periódica cada 30 min con constraint de red que llama a `getFuelInAllStations()` y actualiza el widget Glance. En iOS no existe el widget (Glance es Android-only), pero la actualización de la DB sí aplica.

---

## Decisiones técnicas

| Decisión | Elección | Razón |
|----------|----------|-------|
| Dónde arrancar SyncManager en iOS | `KoinInit.kt` (iosMain), tras `startKoin` | Mismo sitio que `GasGuruApplication.initSyncManager()` en Android. SyncManager ya resolvible por Koin |
| Mecanismo de refresco periódico | `BGAppRefreshTask` (BGTaskScheduler) | Equivalente iOS de WorkManager PeriodicWorkRequest. Framework del sistema, sin pods |
| Contrato Swift → KMP | `IosBridge.refreshStations(onComplete: (Boolean) -> Unit)` | Sigue el patrón establecido en `docs/IOS_BRIDGE.md`: Swift pasa solo primitivos/lambdas |
| Widget | No aplicable en iOS | Glance es Android-only. El refresco de la DB sí aplica |
| Intervalo | 30 min (espeja Android) | `earliestBeginDate` — el SO puede ejecutarlo más tarde según uso de la app |
| Callback thread | `withContext(Dispatchers.Main)` | `task.setTaskCompleted(success:)` se llama desde el handler de BGTask. Marshalear a Main para thread-safety en el boundary KMP→Swift |

---

## Parte A — SyncManager en iOS

`SyncManager` ya estaba en `commonMain` y registrado en `coroutineModule` (commonMain). Solo faltaba invocarlo en el arranque iOS.

### `composeApp/src/iosMain/kotlin/com/gasguru/composeApp/di/KoinInit.kt`

```kotlin
import com.gasguru.core.data.sync.SyncManager

fun initKoin(platformModules: List<Module>): IosBridge {
    val koin = startKoin { modules(...) }.koin
    koin.get<SyncManager>().execute()   // espeja GasGuruApplication.initSyncManager()
    return koin.get<IosBridge>()
}
```

---

## Parte B — Refresco periódico (BGTaskScheduler)

### KMP — `IosBridge` ampliado

`fun interface` → `interface` (ahora tiene 2 métodos):

```kotlin
@OptIn(ExperimentalObjCName::class)
@ObjCName("IosBridge", exact = true)
interface IosBridge {
    fun handlePushTap(stationId: Int)
    fun refreshStations(onComplete: (Boolean) -> Unit)
}
```

### KMP — `IosBridgeImpl`

```kotlin
class IosBridgeImpl(
    private val deepLinkStateHolder: DeepLinkStateHolder,
    private val analyticsHelper: AnalyticsHelper,
    private val getFuelStationUseCase: GetFuelStationUseCase,
    private val scope: CoroutineScope,
) : IosBridge {

    override fun handlePushTap(stationId: Int) {
        deepLinkStateHolder.setPendingStationId(stationId = stationId)
    }

    override fun refreshStations(onComplete: (Boolean) -> Unit) {
        scope.launch {
            val success = try {
                getFuelStationUseCase.getFuelInAllStations()
                true
            } catch (exception: Exception) {
                analyticsHelper.trackStationSyncWorkerRetried(
                    errorMessage = exception.message.orEmpty(),
                    errorType = exception::class.simpleName.orEmpty(),
                )
                false
            }
            withContext(Dispatchers.Main) { onComplete(success) }
        }
    }
}
```

### KMP — `AppShellModule.kt`

```kotlin
single<IosBridge> {
    IosBridgeImpl(
        deepLinkStateHolder = get(),
        analyticsHelper = get(),
        getFuelStationUseCase = get(),
        scope = get(named(KoinQualifiers.APPLICATION_SCOPE)),
    )
}
```

### Swift — `Info.plist`

```xml
<key>BGTaskSchedulerPermittedIdentifiers</key>
<array>
    <string>com.gasguru.stationsync</string>
</array>
<key>UIBackgroundModes</key>
<array>
    <string>fetch</string>
    <string>remote-notification</string>
</array>
```

### Swift — `iOSApp.swift`

```swift
import BackgroundTasks

class AppDelegate: NSObject, UIApplicationDelegate {

    private static let stationSyncId = "com.gasguru.stationsync"
    private var bridge: IosBridge?

    func application(_ application: UIApplication,
                     didFinishLaunchingWithOptions launchOptions: ...) -> Bool {
        initKoin(launchOptions: launchOptions)
        registerBackgroundTasks()
        scheduleStationSync()
        return true
    }

    func applicationDidEnterBackground(_ application: UIApplication) {
        scheduleStationSync()
    }

    private func registerBackgroundTasks() {
        BGTaskScheduler.shared.register(
            forTaskWithIdentifier: Self.stationSyncId,
            using: nil
        ) { [weak self] task in
            self?.handleStationSync(task as! BGAppRefreshTask)
        }
    }

    private func scheduleStationSync() {
        let request = BGAppRefreshTaskRequest(identifier: Self.stationSyncId)
        request.earliestBeginDate = Date(timeIntervalSinceNow: 30 * 60)
        try? BGTaskScheduler.shared.submit(request)
    }

    private func handleStationSync(_ task: BGAppRefreshTask) {
        scheduleStationSync()  // re-encolar antes de hacer trabajo
        task.expirationHandler = { task.setTaskCompleted(success: false) }
        bridge?.refreshStations { success in
            task.setTaskCompleted(success: success)
        }
    }
}
```

---

## Tests

### `IosBridgeImplTest.kt` (commonTest)

Dos nuevos casos para `refreshStations`:

| Test | Fake | Resultado esperado |
|------|------|--------------------|
| `refreshStations_whenRepositorySucceeds_callsOnCompleteWithTrue` | `SucceedingFuelStationRepository` | `onComplete(true)` |
| `refreshStations_whenRepositoryThrows_callsOnCompleteWithFalse` | `FailingFuelStationRepository` | `onComplete(false)` |

**Patrón clave**: `withContext(Dispatchers.Main)` requiere que el Main dispatcher esté configurado en tests JVM. Se usa `Dispatchers.setMain(sharedDispatcher)` + `TestScope(sharedDispatcher)` compartiendo el mismo `TestCoroutineScheduler` que el `runTest` del test, para que `advanceUntilIdle()` drene todas las corrutinas incluyendo el dispatch a Main:

```kotlin
@Test
fun refreshStations_whenRepositorySucceeds_callsOnCompleteWithTrue() = runTest {
    val sharedDispatcher = StandardTestDispatcher(testScheduler)
    Dispatchers.setMain(sharedDispatcher)
    val scope = TestScope(sharedDispatcher)
    val bridge = buildBridge(repository = SucceedingFuelStationRepository(), scope = scope)

    var result: Boolean? = null
    bridge.refreshStations { success -> result = success }
    advanceUntilIdle()

    Dispatchers.resetMain()
    assertTrue(result == true)
}
```

---

## Archivos modificados

| Archivo | Cambio |
|---------|--------|
| `composeApp/src/iosMain/kotlin/.../di/KoinInit.kt` | `koin.get<SyncManager>().execute()` |
| `composeApp/src/commonMain/kotlin/.../bridge/IosBridge.kt` | `fun interface` → `interface` + `refreshStations` |
| `composeApp/src/commonMain/kotlin/.../bridge/IosBridgeImpl.kt` | `AnalyticsHelper` + tracking en `catch` + `refreshStations` |
| `composeApp/src/commonMain/kotlin/.../di/AppShellModule.kt` | `analyticsHelper = get()` en `IosBridgeImpl(...)` |
| `composeApp/src/commonMain/kotlin/com/gasguru/analytics/WorkerAnalyticsExt.kt` | Movido desde `:app` a commonMain |
| `app/src/main/java/com/gasguru/analytics/WorkerAnalyticsExt.kt` | **Eliminado** (resuelve desde `:composeApp`) |
| `composeApp/src/commonTest/kotlin/.../bridge/IosBridgeImplTest.kt` | `RecordingAnalyticsHelper` + aserción en path de fallo |
| `iosApp/iosApp/Info.plist` | `BGTaskSchedulerPermittedIdentifiers`, `UIBackgroundModes` |
| `iosApp/iosApp/iOSApp.swift` | `import BackgroundTasks`, registro + scheduling + handler |

---

## Verificación

```bash
./gradlew :composeApp:compileKotlinIosSimulatorArm64   # ✅
./gradlew :composeApp:testDebugUnitTest                # ✅ 3 tests passing
./gradlew :app:assembleProdDebug                       # ✅ regresión Android
```

**Verificación manual en Xcode (simulador)**:

Para Parte A: crear alerta de precio sin red → recuperar red → comprobar en Supabase/logs que la alerta se sincronizó.

Para Parte B: forzar la BGTask desde el debugger LLDB:
```
e -l objc -- (void)[[BGTaskScheduler sharedScheduler] _simulateLaunchForTaskWithIdentifier:@"com.gasguru.stationsync"]
```
Confirmar que `refreshStations` ejecuta `getFuelInAllStations()` y el task completa con `success: true`.

---

## Nota sobre `BGAppRefreshTask` vs WorkManager

WorkManager con `PeriodicWorkRequest(30, MINUTES)` garantiza que el trabajo se ejecute aproximadamente cada 30 minutos si hay red. `BGAppRefreshTask` con `earliestBeginDate = 30 min` establece solo un límite inferior: el SO puede ejecutarlo más tarde (o nunca, si la app se usa poco). Esto es el comportamiento esperado y aceptado en iOS — no es un bug ni una degradación de paridad.
