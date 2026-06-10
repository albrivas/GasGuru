# KMP Phase 9E — Detail Station Platform Actions iOS

## Objetivo

Cerrar los 3 stubs no-op de `:feature:detail-station/iosMain`:

- **#7 MapsNavigation** — `rememberNavigateToMapsAction()` abría nada → abre action sheet nativo
- **#8 ShareAction** — `rememberShareAction()` descartaba el texto → abre share sheet de iOS con el contenido
- **#9 NotificationPermission** — `rememberNotificationPermissionRequester` invocaba `onPermissionGranted()` directamente sin pedir permiso → solicita autorización a `UNUserNotificationCenter`

Stubs cerrados con esta fase: **#7, #8, #9**. Estado tras 9E: **9 de 12** cerrados.

---

## Decisiones técnicas

### MapsNavigation

**Patrón elegido**: `UIAlertController.actionSheet` con las apps de mapas instaladas detectadas en tiempo de ejecución.

- **Apple Maps** siempre disponible — URL `http://maps.apple.com/?daddr=lat,lng`
- **Google Maps** si `canOpenURL("comgooglemaps://")` — `comgooglemaps://?daddr=lat,lng&directionsmode=driving`
- **Waze** si `canOpenURL("waze://")` — `waze://?ll=lat,lng&navigate=yes`
- Botón **Cancel** con `UIAlertActionStyleCancel`

Alternativas descartadas:
- Abrir Google Maps directo con fallback Apple Maps — ignora Waze y la preferencia del usuario
- Solo Apple Maps — pérdida de paridad con el chooser Android

`LSApplicationQueriesSchemes` añadido a `Info.plist` con `comgooglemaps` y `waze`; sin ello, `canOpenURL` devuelve siempre `false` aunque la app esté instalada (iOS 9+).

### ShareAction

`UIActivityViewController(activityItems = listOf(shareText), applicationActivities = null)` presentado desde `topMostViewController()`. El texto ya viene preconstruido por `DetailStationState.buildShareText` (nombre, dirección, URL Google Maps con coords, precios formateados, URL Play Store). No se añade transformación.

### NotificationPermission

Flujo con `UNUserNotificationCenter`:

1. `getNotificationSettingsWithCompletionHandler` → estado actual
2. `.Authorized` / `.Provisional` → `onPermissionGranted()` directo
3. `.NotDetermined` → `requestAuthorizationWithOptions(.alert | .sound | .badge)` → si `granted` → `onPermissionGranted()`
4. `.Denied` → abrir Ajustes con `UIApplicationOpenSettingsURLString` (paridad con Android)

Threading (L031): los callbacks de `UNUserNotificationCenter` no llegan en main thread. Se envuelven con `scope.launch(Dispatchers.Main)` antes de invocar `onPermissionGranted` (que dispara `DetailStationEvent.TogglePriceAlert` → toca Compose state). Se usa `rememberCoroutineScope()` para que el scope se cancele al salir del composable.

No requiere capability "Push Notifications" en Xcode: `requestAuthorization` para local notifications funciona sin APNs. Esa capability llegará en Phase 9H (OneSignal).

### Helper `topMostViewController()`

Necesario para `UIAlertController` y `UIActivityViewController`, que se presentan desde un `UIViewController`. La app usa `ComposeUIViewController` embebido bajo `UIWindowScene`, por lo que el rootViewController no es siempre el presentador correcto.

Implementación: recorre `UIApplication.sharedApplication.connectedScenes` filtrando por `UIWindowScene` con `activationState == UISceneActivationStateForegroundActive`, toma `keyWindow.rootViewController` y baja por `presentedViewController` hasta el tope. Función `internal` en `IosUiHelpers.kt`, dentro de la feature. Si Phase 9F (In-App Review) lo necesita, se promueve a `core.ui/iosMain`.

---

## Archivos modificados

| Archivo | Cambio |
|---|---|
| `feature/detail-station/src/iosMain/.../platform/IosUiHelpers.kt` | **NUEVO** — helper `topMostViewController()` |
| `feature/detail-station/src/iosMain/.../platform/MapsNavigation.kt` | Implementación con `UIAlertController.actionSheet` |
| `feature/detail-station/src/iosMain/.../platform/ShareAction.kt` | Implementación con `UIActivityViewController` |
| `feature/detail-station/src/iosMain/.../platform/NotificationPermission.kt` | Implementación con `UNUserNotificationCenter` |
| `iosApp/iosApp/Info.plist` | Añadida `LSApplicationQueriesSchemes` (`comgooglemaps`, `waze`) |
| `docs/KMP_MIGRATION.md` | Phase 9E ✅, stubs #7-#9 cerrados (9/12), tabla de fases |
| `CLAUDE.md` | Entrada `KMP Phase 9E` en tabla de documentación |

---

## Verificación

### Compilación

```bash
./gradlew :feature:detail-station:compileKotlinIosSimulatorArm64  # ✅
./gradlew :feature:detail-station:compileDebugKotlinAndroid        # ✅
./gradlew :feature:detail-station:testDebugUnitTest                # ✅
./gradlew :composeApp:compileKotlinIosSimulatorArm64               # ✅
./gradlew :app:assembleProdDebug                                   # ✅
./gradlew codeCheck                                                # ✅ (sin issues nuevos)
```

### Manual en simulador iPhone 15

- **MapsNavigation**: "Cómo llegar" → action sheet con "Apple Maps" + "Cancel". Tap "Apple Maps" abre Maps.app con el destino correcto.
- **ShareAction**: icono share → share sheet nativo con texto completo (nombre, dirección, URLs, precios). Destinos disponibles: Copy, Notes, Messages, etc.
- **NotificationPermission** (primera vez): "Activar alerta" → prompt nativo iOS → Allow → alerta queda activada.
- **NotificationPermission** (ya autorizada): segunda pulsación → no aparece prompt, toggle directo.
- **NotificationPermission** (denegada): denegar la primera vez → siguiente pulsación abre Settings.app en la página de GasGuru.

### Tests

No se añaden tests: las implementaciones son APIs de plataforma sin lógica testeable en commonTest. Tests Android preexistentes (`DetailStationViewModelTest`, `DetailStationScreenTest`) no se ven afectados.

---

## Lessons aplicadas

- **L031**: Callbacks de `UNUserNotificationCenter` no garantizan main thread → `scope.launch(Dispatchers.Main)` antes de invocar cualquier callback que toque Compose state.

---

## Polish post-merge: iPhone-only target para render correcto del ActionSheet

Tras el merge inicial de la phase, se detectó que el `UIAlertController.actionSheet` se renderizaba como Alert centrado horizontal (botones en horizontal) en lugar del ActionSheet vertical apilado desde abajo que es el patrón iOS estándar.

**Causa raíz**: El template Xcode dejaba `TARGETED_DEVICE_FAMILY = "1,2"` (Universal iPhone + iPad) en `project.pbxproj`. Con ese target, el `UIHostingController` que SwiftUI instala para `WindowGroup` puede reportar `horizontalSizeClass = .regular`. En width Regular, UIKit convierte el `actionSheet` en un popover y, al no tener `popoverPresentationController.sourceView` configurado, lo degrada silenciosamente a Alert centrado.

**Fix aplicado**: `TARGETED_DEVICE_FAMILY = "1"` en Debug y Release de `iosApp.xcodeproj/project.pbxproj`. Coherente con el producto (solo portrait, sin UI optimizada para iPad). Fuerza compact width → ActionSheet apilado vertical nativo, sin cambios en código Kotlin.

Ver lección **L033** en `tasks/lessons.md` para regla general de size class en KMP + SwiftUI host.

---

## Próximos pasos

- **Phase 9C.2** — Map polish iOS: clustering + visual de markers con precio/logo en MapKit (paridad visual Android)
- **Phase 9F** — In-App Review: `SKStoreReviewController` / `AppStore.requestReview` desde Kotlin/Native
- **Phase 9G** — Analytics Mixpanel iOS: restaurar `MixpanelAnalyticsHelperIos` eliminado en Phase 8A
- **Phase 9H** — Push OneSignal iOS (opcional V1)
