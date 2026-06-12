# iOS Bridge

## Qué es

`IosBridge` es la interface que define el contrato entre Swift y el código KMP. Vive en `composeApp/commonMain`, lo que la hace testeable en JVM. La implementación (`IosBridgeImpl`) recibe sus dependencias KMP por constructor y se registra en Koin como cualquier otro servicio del proyecto.

```
Swift (AppDelegate)  ──────▶  IosBridge (protocolo ObjC)  ──────▶  IosBridgeImpl (commonMain)
                                                                     └─ DeepLinkStateHolder
                                                                     └─ (futuras deps Koin)
```

**Por qué existe**: `composeApp` solo exporta a Swift los módulos `core.analytics` y `core.notifications`. El resto del grafo Koin (navegación, datos, etc.) es interno. Swift no puede acceder a esos tipos directamente. `IosBridge` actúa de fachada: recibe eventos de la plataforma con tipos primitivos y los despacha a los objetos Kotlin correctos sin que Swift los conozca.

---

## Cómo funciona

### Inicialización (ya hecha)

```swift
// AppDelegate.swift
bridge = KoinInitKt.doInitKoin(platformModules: [...])
```

`doInitKoin` arranca Koin, resuelve `IosBridge` del grafo (cuya implementación es `IosBridgeImpl` registrada en `appShellModule()`) y lo devuelve como protocolo ObjC. Swift lo retiene como `private var bridge: IosBridge?`.

### Uso en Swift

```swift
bridge?.handlePushTap(stationId: 1234)
```

Swift solo pasa primitivos (`Int`, `String`, `Bool`). Nunca accede a tipos internos de KMP.

---

## Cómo añadir nueva funcionalidad

Sigue siempre este orden de tres pasos.

### Paso 1 — Declarar el método en `IosBridge.kt`

```kotlin
// composeApp/src/commonMain/kotlin/com/gasguru/composeApp/bridge/IosBridge.kt

@OptIn(ExperimentalObjCName::class)
@ObjCName("IosBridge", exact = true)
interface IosBridge {
    fun handlePushTap(stationId: Int)

    // nuevo evento:
    fun handleDeepLink(url: String)
}
```

Reglas:
- Parámetros y retornos: **solo tipos primitivos o `Unit`** (`Int`, `String`, `Boolean`). Nunca tipos KMP internos.

### Paso 2 — Implementar en `IosBridgeImpl.kt` e inyectar la dep necesaria

```kotlin
// composeApp/src/commonMain/kotlin/com/gasguru/composeApp/bridge/IosBridgeImpl.kt

class IosBridgeImpl(
    private val deepLinkStateHolder: DeepLinkStateHolder,
    private val navigationManager: NavigationManager,   // nueva dep añadida aquí
) : IosBridge {

    override fun handlePushTap(stationId: Int) {
        deepLinkStateHolder.setPendingStationId(stationId = stationId)
    }

    override fun handleDeepLink(url: String) {
        navigationManager.handleUrl(url = url)
    }
}
```

El constructor de `IosBridgeImpl` es la única responsable de las dependencias del bridge. **No tocar `KoinInit.kt`** — Koin resuelve las deps automáticamente vía `get()`.

### Paso 3 — Llamar desde Swift

```swift
// En el lugar de Swift donde ocurre el evento
bridge?.handleDeepLink(url: "gasguru://station/1234")
```

Swift ve el nuevo método automáticamente en el protocolo ObjC — compilar `composeApp` regenera el header.

---

## Cuándo usar IosBridge vs otras alternativas

| Caso | Patrón correcto |
|------|----------------|
| Evento de plataforma iOS → navegación KMP | `IosBridge` |
| SDK iOS (OneSignal, Mixpanel) → Koin | Swift bridge `@ObjCName` (ver `docs/KMP_PHASE9G.md`) |
| Dato de Kotlin → UI Swift | `StateFlow` colectado en `MainViewController` |
| Permiso iOS pedido desde Compose | `expect/actual` Composable o lambda desde `App.kt` |

`IosBridge` es para eventos **unidireccionales** que van de iOS a KMP. No es un bus de eventos ni un canal bidireccional.

---

## Implementaciones actuales

| Método | Descripción | Desde |
|--------|-------------|-------|
| `handlePushTap(stationId: Int)` | Tap en push notification → abre detalle de estación | `PushNotificationServiceIos.onClick` |

---

## Archivos clave

| Archivo | Rol |
|---------|-----|
| `composeApp/src/commonMain/kotlin/.../bridge/IosBridge.kt` | Interface con `@ObjCName` — contrato Swift ↔ KMP |
| `composeApp/src/commonMain/kotlin/.../bridge/IosBridgeImpl.kt` | Implementación con dependencias KMP |
| `composeApp/src/commonMain/kotlin/com/gasguru/di/AppShellModule.kt` | Registra `single<IosBridge> { IosBridgeImpl(...) }` |
| `composeApp/src/iosMain/kotlin/.../KoinInit.kt` | Arranca Koin y devuelve `koin.get<IosBridge>()` |
| `iosApp/iosApp/iOSApp.swift` | Retiene el bridge (`private var bridge: IosBridge?`) |
| `composeApp/src/commonTest/kotlin/.../bridge/IosBridgeImplTest.kt` | Tests JVM del impl |

---

## Cómo verificar el bridge

Hay tres niveles de prueba, de menor a mayor cobertura:

### Nivel 1 — Test JVM (sin dispositivo, rápido)

Valida que `IosBridgeImpl` despacha correctamente al `DeepLinkStateHolder`:

```bash
./gradlew :composeApp:jvmTest
```

Cubre: `handlePushTap` → `setPendingStationId`. No cubre la capa Swift ni OneSignal.

### Nivel 2 — Debug shortcut en simulador (sin dispositivo, sin push real)

Descomenta el bloque en `iosApp/iosApp/iOSApp.swift` dentro de `initKoin`:

```swift
#if DEBUG
DispatchQueue.main.asyncAfter(deadline: .now() + 3) {
    self.bridge?.handlePushTap(stationId: 1234)
}
#endif
```

Build & Run en Xcode. A los 3 segundos la app debe navegar al detalle de la estación `1234` (o cualquier ID válido de tu base de datos). Vuelve a comentarlo cuando termines.

Cubre: el bridge Kotlin completo (`IosBridgeImpl` → `DeepLinkStateHolder` → `NavHost`). No cubre `PushNotificationServiceIos.onClick` ni OneSignal.

### Nivel 3 — Dispositivo físico con push real (cobertura total)

Desde el panel de OneSignal → **New Push** → en "Send to specific devices" filtra por subscription ID. El subscription ID de tu dispositivo aparece en los logs de Xcode al arrancar la app:

```
VERBOSE: network request ... OneSignal-Subscription-Id = "860c7c59-..."
```

El payload debe incluir `station_id` en los datos adicionales del push (el campo "Additional Data" en el panel de OneSignal):

```json
{ "station_id": "1234" }
```

Tap en la notificación → debe abrir el detalle de la estación. Este es el único nivel que prueba el camino completo: OneSignal SDK → `PushNotificationServiceIos.onClick` → `bridge.handlePushTap` → navegación.

---

## Nota sobre `LogAnalyticsHelperIos`

El stub de debug de analíticas está implementado en **Kotlin** (`core/analytics/src/iosMain/`) en lugar de Swift, porque no depende de SDKs Swift puros y puede usar `NSLog` directamente. Es la excepción al patrón "implementación Swift para SDKs externos": si no hay SDK puro Swift de por medio, la implementación puede vivir en Kotlin iosMain.
