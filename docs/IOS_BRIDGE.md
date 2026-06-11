# iOS Bridge

## Qué es

`IosBridge` es el contrato único entre Swift y el código KMP. Es una clase Kotlin en `composeApp/iosMain` que Swift obtiene al arrancar Koin y retiene durante toda la vida de la app.

```
Swift (AppDelegate)  ──────▶  IosBridge  ──────▶  Koin-managed Kotlin objects
                                                    (DeepLinkStateHolder, etc.)
```

**Por qué existe**: `composeApp` solo exporta a Swift los módulos `core.analytics` y `core.notifications`. El resto del grafo Koin (navegación, datos, etc.) es interno. Swift no puede acceder a esos tipos directamente. `IosBridge` actúa de fachada: recibe eventos de la plataforma con tipos primitivos y los despacha a los objetos Kotlin correctos sin que Swift los conozca.

---

## Cómo funciona

### Inicialización (ya hecha)

```swift
// AppDelegate.swift
bridge = KoinInitKt.doInitKoin(platformModules: [...])
```

`doInitKoin` arranca Koin, construye `IosBridge` con las dependencias necesarias y lo devuelve. Swift lo retiene como `private var bridge: IosBridge?`.

### Uso en Swift

```swift
bridge?.handlePushTap(stationId: 1234)
```

Swift solo pasa primitivos (`Int`, `String`, `Bool`). Nunca accede a tipos internos de KMP.

---

## Cómo añadir nueva funcionalidad

Sigue siempre este orden de tres pasos.

### Paso 1 — Añadir el método en `IosBridge.kt`

```kotlin
// composeApp/src/iosMain/kotlin/com/gasguru/composeApp/IosBridge.kt

class IosBridge internal constructor(
    private val deepLinkStateHolder: DeepLinkStateHolder,
    // añadir nueva dependencia aquí si se necesita
) {
    fun handlePushTap(stationId: Int) { ... }

    // nuevo método:
    fun handleDeepLink(url: String) {
        // lógica interna con objetos Koin — Swift nunca los ve
    }
}
```

Reglas:
- Parámetros y retornos: **solo tipos primitivos o `void`** (`Int`, `String`, `Boolean`). Nunca tipos KMP internos.
- Si el método necesita una dependencia nueva, añádela al constructor con `internal constructor`.

### Paso 2 — Proveer la dependencia en `KoinInit.kt`

```kotlin
// composeApp/src/iosMain/kotlin/com/gasguru/composeApp/KoinInit.kt

fun initKoin(platformModules: List<Module>): IosBridge {
    val koin = startKoin { ... }.koin
    return IosBridge(
        deepLinkStateHolder = koin.get(),
        miNuevaDependencia = koin.get(),   // añadir aquí
    )
}
```

### Paso 3 — Llamar desde Swift

```swift
// En el lugar de Swift donde ocurre el evento
bridge?.handleDeepLink(url: "gasguru://station/1234")
```

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
| `composeApp/src/iosMain/kotlin/.../IosBridge.kt` | Definición del bridge con todos los métodos |
| `composeApp/src/iosMain/kotlin/.../KoinInit.kt` | Construye y devuelve el bridge tras startKoin |
| `iosApp/iosApp/iOSApp.swift` | Retiene el bridge (`private var bridge: IosBridge?`) |
