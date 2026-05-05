# KMP Phase 7B — `:feature:profile` → Compose Multiplatform

## Objetivo

Migrar `:feature:profile` a CMP/KMP como segunda feature de Phase 7. Es la feature de settings/perfil de usuario: sin mapas, sin Places, usa SwipeItem con Lottie animado (ya KMP-ready con compottie).

## Módulos modificados

- `:feature:profile` — migración completa a CMP
- `:core:testing` — extensión de `BaseTest.getCmpString` con soporte de `vararg formatArgs`

---

## Estado previo al inicio de Phase 7B

El módulo ya tenía `gasguru.kmp.compose.library` como plugin y las fuentes en `commonMain`. Lo que faltaba:

- `ProfileScreenTest.kt` importaba `com.gasguru.feature.profile.R` para `R.string.version`, que ya no existe (strings movidos a `composeResources`)
- `ProfileScreenPreviews.kt` usaba el typealias deprecated `org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider`

---

## Cambios realizados

### `feature/profile/src/androidTest/java/.../ProfileScreenTest.kt`

- Eliminado `import com.gasguru.feature.profile.R`
- Añadidos imports de `com.gasguru.feature.profile.generated.resources.Res as ProfileRes` y `version`
- Cambiado `getStringResource(id = R.string.version, "1.0.0 (12)")` por `getCmpString(ProfileRes.string.version, "1.0.0 (12)")`

### `feature/profile/src/commonMain/.../ProfileScreenPreviews.kt`

- Cambiado import de `org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider` (deprecated typealias) a `androidx.compose.ui.tooling.preview.PreviewParameterProvider` (clase correcta de CMP)

### `core/testing/src/androidMain/.../BaseTest.kt`

- Añadido overload `getCmpString(resource: StringResource, vararg formatArgs: Any)` para soportar strings con argumentos de formato desde tests instrumentados

---

## Verificación

```
./gradlew :feature:profile:compileDebugKotlinAndroid  → BUILD SUCCESSFUL
./gradlew :feature:profile:testDebugUnitTest           → BUILD SUCCESSFUL (11 tests)
./gradlew :feature:profile:compileReleaseKotlinAndroid → BUILD SUCCESSFUL
```

---

## Decisiones técnicas

### `SwipeItem` con `iconAnimationFile`

El `SwipeItemModel` usa un `String` para `iconAnimationFile` (ruta al JSON de Lottie), y `GasGuruLottie` en `core:uikit` ya usa **compottie** (KMP-compatible Lottie). No fue necesario ningún cambio: el componente ya era CMP-ready.

### `NavGraphBuilder` en commonMain

La navegación (`ProfileNavigation.kt`) usa `NavGraphBuilder` de AndroidX Navigation Compose, que está disponible en `commonMain` del plugin `gasguru.kmp.compose.library`. Es el mismo patrón de `:feature:onboarding` (Phase 7A).

### `BaseTest.getCmpString` con formato

El overload con `vararg formatArgs` delega a `org.jetbrains.compose.resources.getString(resource, *formatArgs)`, que es la API pública de CMP para strings con argumentos.
