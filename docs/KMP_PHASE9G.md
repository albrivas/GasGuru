# KMP Phase 9G — Analytics Mixpanel iOS

## Objetivo

Restaurar métricas reales en iOS sustituyendo el `NoOpAnalyticsHelper` del stub de Phase 8A por una integración real con Mixpanel, usando el SDK oficial Swift `Mixpanel-swift 6.4`.

---

## Problema: cinterop no funciona con Swift puro

El SDK `Mixpanel-swift` v6.x es un framework Swift puro sin headers Objective-C expuestos. El umbrella header (`Mixpanel-swift-umbrella.h`) solo exporta números de versión, y `Mixpanel-Swift.h` no declara ninguna clase ObjC (`@interface`). Kotlin/Native cinterop solo funciona con APIs Objective-C, por lo que no es posible importar `cocoapods.Mixpanel_swift.Mixpanel` desde código Kotlin.

El SDK ObjC (`pod 'Mixpanel'`, v5.0.8) funciona con cinterop pero lleva más de año y medio sin actualizarse — descartado.

---

## Solución: Swift Bridge via protocolo ObjC generado por Kotlin/Native

Kotlin/Native expone interfaces Kotlin como protocolos Objective-C en el framework generado (`ComposeApp.framework`). Anotando la interfaz con `@ObjCName`, Swift puede implementar ese protocolo directamente usando el SDK Mixpanel sin ningún cinterop.

```
AnalyticsHelper (Kotlin interface) → AnalyticsHelper (ObjC protocol) → MixpanelAnalyticsHelperIos (Swift class)
```

El pod `Mixpanel-swift` solo se añade a `iosApp/Podfile` — ningún módulo Kotlin lo declara.

---

## Decisiones técnicas

| Decisión | Elección | Razón |
|----------|----------|-------|
| SDK iOS | `Mixpanel-swift ~> 6.4` (Swift puro) | SDK oficial mantenido |
| Integración | Swift implementa protocolo ObjC | Evita cinterop con Swift puro |
| Token | `AnalyticsSecrets.MIXPANEL_TOKEN` vía BuildKonfig | Reutiliza secret `MIXPANEL` del CI |
| Debug strategy | `#if DEBUG` en Swift: `LogAnalyticsHelperIos` en debug, `MixpanelAnalyticsHelperIos` en release | Paridad con Android |
| `IS_DEBUG` flag | Eliminado de BuildKonfig (usamos `#if DEBUG` de Swift directamente) | Más idiomático en Swift |

---

## Archivos modificados

| Archivo | Cambio |
|---------|--------|
| `core/analytics/src/commonMain/.../AnalyticsHelper.kt` | Añadido `@ObjCName("AnalyticsHelper", exact = true)` |
| `core/analytics/src/commonMain/.../AnalyticsEvent.kt` | Añadido `@ObjCName("AnalyticsEvent")` y `@ObjCName("AnalyticsEventParam")` a `Param` |
| `core/analytics/build.gradle.kts` | Eliminados `kotlin("native.cocoapods")` y bloque `cocoapods {}`; simplificado BuildKonfig (solo `MIXPANEL_TOKEN`) |
| `core/analytics/src/iosMain/.../MixpanelAnalyticsHelperIos.kt` | **Eliminado** — reemplazado por clase Swift |
| `core/analytics/src/iosMain/.../di/AnalyticsModuleIos.kt` | Convertido de `val` a `fun analyticsModuleIos(analyticsHelper)` |
| `core/analytics/analytics.podspec` | **Eliminado** — huérfano tras quitar el plugin CocoaPods |
| `composeApp/src/iosMain/.../KoinInit.kt` | `initKoin()` ahora acepta `analyticsHelper: AnalyticsHelper` como parámetro |
| `iosApp/Podfile` | Añadido `pod 'Mixpanel-swift', '~> 6.4'` |
| `iosApp/iosApp/MixpanelAnalyticsHelper.swift` | **NUEVO** — implementa `AnalyticsHelper` usando Mixpanel Swift SDK |
| `iosApp/iosApp/iOSApp.swift` | Inicializa Mixpanel (release) o `LogAnalyticsHelperIos` (debug) y pasa el helper a `doInitKoin` |
| `gradle/libs.versions.toml` | Añadida entrada documental `mixpanel-ios = "5.0.8"` (no usada) |

---

## Patrón de implementación

### Kotlin (`commonMain`)

```kotlin
@OptIn(ExperimentalObjCName::class)
@ObjCName("AnalyticsHelper", exact = true)
interface AnalyticsHelper {
    fun logEvent(event: AnalyticsEvent)
    fun updateSuperProperties(properties: Map<String, Any>)
}
```

### Kotlin (`iosMain`)

```kotlin
fun analyticsModuleIos(analyticsHelper: AnalyticsHelper) = module {
    single<AnalyticsHelper> { analyticsHelper }
}
```

### Swift (`iosApp`)

```swift
final class MixpanelAnalyticsHelperIos: NSObject, AnalyticsHelper {
    func logEvent(event: AnalyticsEvent) {
        var properties: [String: MixpanelType] = ["category": event.category]
        let extrasSize = Int(event.extras.size)
        for i in 0..<extrasSize {
            if let param = event.extras.get(index: Int32(i)) as? AnalyticsEventParam {
                properties[param.key] = param.value
            }
        }
        Mixpanel.mainInstance().track(event: event.type, properties: properties)
    }

    func updateSuperProperties(properties: [AnyHashable: Any]) {
        let mixpanelProps = properties.reduce(into: [String: MixpanelType]()) { result, pair in
            if let key = pair.key as? String, let value = pair.value as? MixpanelType {
                result[key] = value
            }
        }
        Mixpanel.mainInstance().registerSuperProperties(mixpanelProps)
    }
}
```

### `iOSApp.swift`

```swift
init() {
    let analyticsHelper: AnalyticsHelper
    #if DEBUG
    analyticsHelper = LogAnalyticsHelperIos()
    #else
    let token = AnalyticsSecrets.shared.MIXPANEL_TOKEN
    Mixpanel.initialize(options: MixpanelOptions(token: token, trackAutomaticEvents: false))
    analyticsHelper = MixpanelAnalyticsHelperIos()
    #endif
    KoinInitKt.doInitKoin(analyticsHelper: analyticsHelper)
}
```

---

## Verificación

```
./gradlew :core:analytics:assembleDebug              ✅
./gradlew :core:analytics:testDebugUnitTest          ✅
./gradlew :composeApp:compileKotlinIosSimulatorArm64 ✅
./gradlew :app:assembleProdDebug                     ✅
```

**Manual (release build en simulador)**:
- Lanzar app iOS → en Mixpanel dashboard aparece evento `app_opened` con superprop `platform=ios`
- Navegar → `screen_viewed` en dashboard
- Build debug → consola Xcode muestra NSLog de `LogAnalyticsHelperIos`, ningún evento llega a Mixpanel
