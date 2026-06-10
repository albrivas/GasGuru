# KMP Phase 9F — In-App Review iOS (StoreKit)

## Objetivo

Cerrar el stub no-op de `InAppReviewManager` en `core/ui/iosMain`:

- **#10 InAppReviewManager** — `launchReviewFlow()` no hacía nada → llama a `SKStoreReviewController.requestReviewInScene` de StoreKit

Aprovecha la fase para alinear el tipo con el patrón canónico del proyecto: `expect class InAppReviewManager` → `interface InAppReviewManager` en `commonMain`, igual que `OneSignalManager`, `LocationTracker`, `NetworkMonitor`, `GeocoderAddress`, `PlacesRepository`.

Stubs cerrados con esta fase: **#10**. Estado tras 9F: **10 de 12** cerrados.

---

## Decisiones técnicas

### API StoreKit elegida

`SKStoreReviewController.requestReviewInScene(UIWindowScene)` — disponible desde iOS 14, cubre el deployment target del proyecto (15.0).

Alternativas descartadas:
- `SKStoreReviewController.requestReview()` — deprecada en iOS 16+ sin `scene`.
- `AppStore.requestReview(in:)` (StoreKit 2, iOS 16+) — descartada para evitar elevar el deployment target de 15.0 a 16.0.

No requiere CocoaPods: `platform.StoreKit.SKStoreReviewController` está en los cinterop bindings estándar de Kotlin/Native (StoreKit es framework del sistema).

### Obtención de `UIWindowScene` activo

Helper privado `activeWindowScene()` dentro de `InAppReviewManagerIos`: itera `UIApplication.sharedApplication.connectedScenes`, filtra por `UIWindowScene`, toma el primero con `activationState == UISceneActivationStateForegroundActive`.

Si no hay scene activa (transición background→foreground puntual), el método silencia y no llama a ningún callback. El contrato `onReviewFailed` queda reservado para errores reales de la API — no hay manera de distinguir "scene no disponible" de un error técnico real en StoreKit.

### Threading

`withContext(Dispatchers.Main)` dentro de `launchReviewFlow` — StoreKit/UIKit requieren main thread.

### Semántica de callbacks iOS

`onReviewCompleted()` se llama siempre tras invocar `requestReviewInScene` (fire-and-forget). StoreKit no expone si el prompt fue mostrado, cancelado o suprimido por quota interna de Apple — llamar `onReviewCompleted` es la mejor aproximación al contrato Android ("la oportunidad se presentó").

Consecuencia consciente: la métrica `IN_APP_REVIEW_COMPLETED` en iOS representa "se intentó mostrar", no "el usuario interactuó". Igual que Android (donde `addOnCompleteListener` tampoco confirma que el usuario dejó reseña).

### Refactor `expect class` → `interface`

El contrato público `suspend fun launchReviewFlow(onCompleted, onFailed)` no cambia. Solo cambia la declaración del tipo en `commonMain` de `expect class` a `interface`. Los `actual class` desaparecen; las implementaciones pasan a ser clases ordinarias:
- `InAppReviewManagerAndroid : InAppReviewManager` en `androidMain`
- `InAppReviewManagerIos : InAppReviewManager` en `iosMain`

El `@Composable expect fun rememberInAppReviewManager(): InAppReviewManager?` se mantiene: es un factory platform-specific legítimo (Android necesita `LocalContext`/`Activity` para construir el impl; iOS no necesita nada). El call site en `DetailStationScreen` no cambia.

### Comportamiento conocido: prompt detrás del dialog de detalle

En iOS, la pantalla de detalle se presenta como un dialog en un `UIWindow` overlay con `windowLevel` superior al de la ventana principal. `SKStoreReviewController.requestReviewInScene` presenta en la ventana principal, por lo que el prompt queda detrás del detalle. Es una limitación de la API — Apple controla la presentación y no permite especificar una ventana o view controller. La solución elegida para V1 es aceptar este comportamiento. En producción, con la quota de Apple (máximo 3 prompts al año), el impacto es mínimo.

---

## Archivos modificados

| Archivo | Cambio |
|---|---|
| `core/ui/src/commonMain/.../review/InAppReviewManager.kt` | `expect class` → `interface` |
| `core/ui/src/androidMain/.../review/InAppReviewManagerAndroid.kt` | **NUEVO** — `class : InAppReviewManager` (lógica idéntica al `actual class` anterior) |
| `core/ui/src/androidMain/.../review/InAppReviewExt.kt` | Constructor `InAppReviewManager(...)` → `InAppReviewManagerAndroid(...)` |
| `core/ui/src/iosMain/.../review/InAppReviewManagerIos.kt` | **NUEVO** — implementación real con StoreKit |
| `core/ui/src/iosMain/.../review/InAppReviewExt.kt` | Constructor `InAppReviewManager()` → `InAppReviewManagerIos()` |
| `docs/KMP_MIGRATION.md` | Phase 9F ✅, stub #10 cerrado (10/12), tabla de fases |
| `CLAUDE.md` | Entrada `KMP Phase 9F` en tabla de documentación |

**Eliminados:**
- `core/ui/src/androidMain/.../review/InAppReviewManager.kt` (actual class obsoleta)
- `core/ui/src/iosMain/.../review/InAppReviewManager.kt` (actual class/stub obsoleto)

**Sin cambios:**
- `core/ui/src/commonMain/.../review/InAppReviewExt.kt` (firma `expect fun` intacta)
- `feature/detail-station/.../DetailStationScreen.kt` (call site intacto)
- `app/src/main/java/com/gasguru/MainActivity.kt`
- `composeApp/src/commonMain/.../App.kt`
- `composeApp/src/iosMain/.../MainViewController.kt`

---

## Verificación

### Compilación

```bash
./gradlew :core:ui:compileDebugKotlinAndroid              # ✅
./gradlew :core:ui:compileKotlinIosSimulatorArm64         # ✅
./gradlew :feature:detail-station:compileDebugKotlinAndroid  # ✅
./gradlew :feature:detail-station:compileKotlinIosSimulatorArm64  # ✅
./gradlew :app:assembleProdDebug                          # ✅
```

### Tests

```bash
./gradlew :core:ui:testDebugUnitTest                      # ✅
./gradlew :feature:detail-station:testDebugUnitTest       # ✅
./gradlew :app:testProdDebugUnitTest                      # ✅
```

No se añaden tests nuevos: StoreKit es fire-and-forget sin lógica testeable en commonTest. Tests preexistentes no se ven afectados.

### Manual en simulador iPhone 15

- Abrir app iOS → detalle de una estación que no es favorita → tap favorito → aparece el prompt nativo "¿Te gusta iosApp?" de App Store.
- **Nota**: el prompt aparece detrás del dialog de detalle (limitación conocida descrita arriba). El usuario puede interactuar con él al cerrar el detalle.
- **Caveat**: en simulador el prompt aparece siempre (sin quota). En producción, Apple lo suprime si ya se mostró 3 veces en 365 días — no es un bug.

### Regresión Android

- Detalle → marcar favorito → flow Google Play Review sigue funcionando igual que antes.

---

## Próximos pasos

- **Phase 9C.2** — Map polish iOS: clustering + visual de markers con precio/logo en MapKit (paridad visual Android)
- **Phase 9G** — Analytics Mixpanel iOS: restaurar `MixpanelAnalyticsHelperIos` eliminado en Phase 8A
- **Phase 9H** — Push OneSignal iOS (opcional V1)
