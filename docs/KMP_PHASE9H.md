# KMP Phase 9H — OneSignal Push Notifications iOS

## Objetivo

Cerrar el stub #12 (el último stub activo de Phase 9): sustituir las implementaciones no-op de `OneSignalManagerIos` y `PushNotificationServiceIos` por una integración real con el SDK oficial OneSignal iOS, usando el mismo patrón de Swift bridge introducido en Phase 9G.

---

## Contexto

En Android, `PriceAlertRepositoryImpl` registra alertas de precio en Supabase incluyendo el `onesignalPlayerId` del dispositivo, y `PushNotificationService` registra un click listener para manejar el tap en la notificación. En iOS, ambos retornaban vacíos/no-op, de modo que:
- Las alertas se registraban en Supabase con `onesignalPlayerId = ""` → los pushes nunca llegaban al device iOS.
- El tap en notificación no navegaba a la estación.

---

## Problema: OneSignalXCFramework es Swift puro

El pod `OneSignalXCFramework` v5.x es un framework Swift sin headers ObjC que cinterop pueda leer. El mismo problema que `Mixpanel-swift` en Phase 9G.

---

## Solución: Swift Bridge (mismo patrón que Phase 9G)

```
OneSignalManager (Kotlin interface) → OneSignalManager (ObjC protocol) → OneSignalManagerIos (Swift class)
NotificationService (Kotlin interface) → NotificationService (ObjC protocol) → PushNotificationServiceIos (Swift class)
```

El pod solo se añade a `iosApp/Podfile`. Ningún módulo Kotlin declara el SDK iOS.

---

## Decisiones técnicas

| Decisión | Elección | Razón |
|----------|----------|-------|
| SDK iOS | `OneSignalXCFramework >= 5.2.9, < 6.0` | Alinea con Android 5.4.1. SPM no soportado por K/N |
| Integración | Swift implementa protocolos ObjC generados por K/N | Mismo patrón que Phase 9G — sin cinterop |
| App ID | Mismo `onesignalAppId` que Android | OneSignal soporta multi-plataforma con un proyecto. Sin config extra |
| Cuándo pedir permiso | Al crear primera price alert | Mirror Android. `requestPermission` es idempotente si ya respondido |
| `optOut()` | NO llamar al desactivar alertas | Perder subscription ID rompería futuras alertas. Solo se actualiza el tag `enable_stations_alerts` |
| Notification Service Extension | NO (V1) | Basic push suficiente. NSE requiere target Xcode extra, App Group y certs adicionales |
| Entitlements | Nuevo `iosApp.entitlements` con `aps-environment = development` | Primer entitlements file del proyecto |
| `NotificationService.init()` | Renombrado a `start()` | `init` colisiona con keyword Swift; se exportaría como `doInit()`, confuso |
| `launchOptions` | Propagado desde `AppDelegate.didFinishLaunchingWithOptions` | SDK necesita detectar cold-start desde tap en notificación |
| `initKoin` return value | Devuelve `DeepLinkStateHolder` | `KoinPlatform`/`GlobalContext` no disponibles en Koin 4.x iOS; usar el return de `startKoin {...}.koin` |

---

## Archivos modificados

| Archivo | Cambio |
|---------|--------|
| `core/notifications/build.gradle.kts` | Añadido plugin `buildkonfig`, bloque `buildkonfig { NotificationsSecrets }` con `ONESIGNAL_APP_ID` |
| `core/notifications/src/commonMain/.../OneSignalManager.kt` | Añadido `@ObjCName("OneSignalManager", exact = true)` |
| `core/notifications/src/commonMain/.../NotificationService.kt` | Añadido `@ObjCName("NotificationService", exact = true)`; `init()` → `start()` |
| `core/notifications/src/androidMain/.../PushNotificationService.kt` | `init()` → `start()` |
| `core/notifications/src/iosMain/.../OneSignalManagerIos.kt` | **Eliminado** — reemplazado por clase Swift |
| `core/notifications/src/iosMain/.../PushNotificationServiceIos.kt` | **Eliminado** — reemplazado por clase Swift |
| `core/notifications/src/iosMain/.../di/NotificationModule.kt` | Reescrito: `val notificationModule` → `fun provideNotificationModuleIos(oneSignalManager)` |
| `app/src/main/java/com/gasguru/GasGuruApplication.kt` | `pushNotificationService.init()` → `.start()` |
| `composeApp/build.gradle.kts` | `export(projects.core.notifications)` en framework; `api(projects.core.notifications)` en iosMain |
| `composeApp/src/iosMain/.../KoinInit.kt` | Quitado `notificationModule` fijo; `initKoin` devuelve `DeepLinkStateHolder` |
| `iosApp/Podfile` | Añadido `pod 'OneSignalXCFramework', '>= 5.2.9', '< 6.0'` |
| `iosApp/iosApp/OneSignalManagerIos.swift` | **NUEVO** — inicializa OneSignal SDK, `enablePriceNotificationAlert`, `getPlayerId` |
| `iosApp/iosApp/PushNotificationServiceIos.swift` | **NUEVO** — registra click listener, extrae `station_id`, trackea analytics, llama `setPendingStationId` |
| `iosApp/iosApp/iOSApp.swift` | Propaga `launchOptions`; instancia `OneSignalManagerIos`; pasa `notificationModule` a `doInitKoin`; retiene `pushService` |
| `iosApp/iosApp/Info.plist` | Añadido `UIBackgroundModes → remote-notification` |
| `iosApp/iosApp/iosApp.entitlements` | **NUEVO** — `aps-environment = development` |
| `iosApp/project.yml` | Añadido `CODE_SIGN_ENTITLEMENTS: iosApp/iosApp.entitlements` |

---

## Patrón de implementación

### Kotlin `commonMain` — interfaz con `@ObjCName`

```kotlin
@OptIn(ExperimentalObjCName::class)
@ObjCName("OneSignalManager", exact = true)
interface OneSignalManager {
    suspend fun enablePriceNotificationAlert(enable: Boolean)
    suspend fun getPlayerId(): String?
}
```

### Kotlin `iosMain` — factory que acepta la impl Swift

```kotlin
fun provideNotificationModuleIos(oneSignalManager: OneSignalManager) = module {
    single<OneSignalManager> { oneSignalManager }
}
```

### `composeApp/iosMain/KoinInit.kt` — devuelve `DeepLinkStateHolder`

```kotlin
fun initKoin(platformModules: List<Module>): DeepLinkStateHolder {
    val koin = startKoin { modules(...) }.koin
    return koin.get()
}
```

### Swift — `OneSignalManagerIos.swift`

```swift
final class OneSignalManagerIos: NSObject, OneSignalManager {
    init(launchOptions: [UIApplication.LaunchOptionsKey: Any]?) {
        super.init()
        let appId = NotificationsSecrets.shared.ONESIGNAL_APP_ID
        OneSignal.initialize(appId, withLaunchOptions: launchOptions)
    }
    func enablePriceNotificationAlert(enable: Bool) async throws {
        if enable { OneSignal.Notifications.requestPermission({ _ in }, fallbackToSettings: false) }
        OneSignal.User.addTag("enable_stations_alerts", String(enable))
    }
    func getPlayerId() async throws -> String? { OneSignal.User.pushSubscription.id }
}
```

### Swift — `PushNotificationServiceIos.swift`

```swift
final class PushNotificationServiceIos: NSObject, NotificationService, OSNotificationClickListener {
    func start() { OneSignal.Notifications.addClickListener(self) }
    func onClick(event: OSNotificationClickEvent) {
        guard let data = event.notification.additionalData,
              let raw = data["station_id"] else { return }
        // parsear a Int32, llamar trackPushNotificationTapped + setPendingStationId
    }
}
```

---

## Nota sobre `NotificationService` en el DI iOS

`NotificationService` **no** se registra en Koin en iOS. En Android lo consume `GasGuruApplication.initPushNotifications()` a través de `by inject()`. En iOS el equivalente es la instancia imperativa `pushService` en `AppDelegate`, retenida como `private var` para evitar que OneSignal libere el click listener.

---

## Verificación

```
./gradlew :core:notifications:assembleDebug              ✅
./gradlew :core:notifications:compileKotlinIosSimulatorArm64 ✅
./gradlew :composeApp:compileKotlinIosSimulatorArm64     ✅
./gradlew :app:assembleProdDebug                         ✅
./gradlew :app:testProdDebugUnitTest                     ✅
./gradlew codeCheck                                      ✅
```

**Verificación manual (device físico obligatorio — push no funciona en simulador)**:
1. APNs setup en Apple Developer + OneSignal dashboard (subir .p8 key, configurar iOS)
2. Build & run en iPhone: crear primera price alert → prompt nativo de notificaciones
3. Aceptar → device aparece en OneSignal dashboard con tag `enable_stations_alerts=true`
4. Enviar push desde dashboard con `station_id=<id>` → tap → app navega a detalle
5. Evento `push_notification_tapped` visible en Mixpanel con `notification_type=price_alert`
6. Eliminar última alert → tag `enable_stations_alerts=false`; crear nueva → player ID conservado
