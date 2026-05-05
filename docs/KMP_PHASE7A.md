# KMP Phase 7A — `:feature:onboarding` → Compose Multiplatform

## Objetivo

Migrar `:feature:onboarding` a CMP/KMP como primera feature de Phase 7. Es la feature de menor complejidad: sin mapas, sin Places, sin Lottie.

## Módulos modificados

- `:feature:onboarding` — migración completa a CMP
- `:core:ui` — eliminación de `String.toFuelType(context)` (Android-only, solo se usaba aquí)

---

## Cambios en `build.gradle.kts`

Plugin cambiado de `gasguru.android.library` + `gasguru.compose.library` a `gasguru.kmp.compose.library` + `gasguru.koin` + `gasguru.proguard` + `stability.analyzer`.

```kotlin
compose.resources {
    publicResClass = true
    packageOfResClass = "com.gasguru.feature.onboarding.generated.resources"
}
```

Tests:
- `commonTest`: JUnit5 + Turbine + `CoroutinesTestExtension` (mismo patrón que `:core:components`)
- `androidInstrumentedTest` para `OnboardingFuelPreferencesTest` (usa `BaseTest` con Compose)

---

## Decisión clave: `String.toFuelType(context)` → KMP

La pantalla `OnboardingFuelPreferencesScreen` necesitaba convertir el nombre traducido de un combustible (devuelto por `FuelListSelection`) de vuelta a su `FuelType` enum. La versión original en `core.ui.FuelUiExtensionsAndroid.kt` usaba `runBlocking { getString(...) }` y requería `Context`.

Verificado con grep que era el **único consumidor** de esa función. Decisión:

1. **Eliminar** `String.toFuelType(context)` de `core/ui/src/androidMain/...`.
2. **Crear** `FuelTypeMapper.kt` en `feature/onboarding/src/commonMain/...`:

```kotlin
internal fun String.toFuelType(): FuelType =
    FuelTypeUiModel.ALL_FUELS.firstOrNull {
        runBlocking { getString(it.translationRes) } == this
    }?.type ?: FuelType.GASOLINE_95
```

`kotlinx.coroutines.runBlocking` y `org.jetbrains.compose.resources.getString` están disponibles en commonMain (JVM + Kotlin/Native). La firma simplifica eliminando el parámetro `Context` innecesario en CMP.

**Regla derivada**: si un helper Android-only vive en un módulo compartido y solo lo usa una pantalla, eliminarlo del módulo compartido y crear su versión KMP en el módulo consumidor.

---

## Migración de strings: `values-es-rES` → `values-es`

CMP usa el locale tag sin la región en el nombre de carpeta:

| Android | CMP |
|---------|-----|
| `values-es-rES/strings.xml` | `values-es/strings.xml` |

---

## Migración de `OnboardingPageUiModel`

El enum cambió de `@StringRes Int` / `@DrawableRes Int` a `StringResource` / `DrawableResource` de CMP:

```kotlin
// Antes (Android)
enum class OnboardingPageUiModel(
    @StringRes val titleRes: Int,
    @DrawableRes val iconRes: Int,
)

// Después (CMP)
enum class OnboardingPageUiModel(
    val titleRes: StringResource,
    val iconRes: DrawableResource,
)
```

Los valores usan `Res.string.*` y `Res.drawable.*` generados por composeResources.

---

## Imports que cambian

| Antes (Android) | Después (CMP / KMP) |
|-----------------|---------------------|
| `androidx.compose.ui.res.stringResource` | `org.jetbrains.compose.resources.stringResource` |
| `androidx.compose.ui.res.painterResource` | `org.jetbrains.compose.resources.painterResource` |
| `org.koin.androidx.compose.koinViewModel` | `org.koin.compose.viewmodel.koinViewModel` |
| `R.string.x` / `R.drawable.x` | `Res.string.x` / `Res.drawable.x` |
| `LocalContext.current` | ❌ eliminado (no necesario en CMP) |

`androidx.lifecycle.ViewModel`, `viewModelScope`, `collectAsStateWithLifecycle` y `androidx.navigation.*` no cambian de paquete — el artefacto KMP de JetBrains los provee con el mismo namespace.

---

## Tests

| Test | Destino | Framework |
|------|---------|-----------|
| `NewOnboardingViewModelTest` | `commonTest` | JUnit5 + Turbine + `CoroutinesTestExtension` |
| `CapacityTankViewModelTest` | `commonTest` | JUnit5 + Turbine + `CoroutinesTestExtension` |
| `OnboardingFuelPreferencesTest` | `androidInstrumentedTest` | JUnit5 + `BaseTest` + Compose UI |

---

## Deuda iOS heredada

`compileKotlinIosSimulatorArm64` falla por causas pre-existentes no relacionadas con este PR:

- `:core:analytics`: requiere `pod install` con CocoaPods (Mixpanel iOS SDK).
- `:core:uikit`: `ThemePreviews` y `maestroTestTag` usan APIs Android-only en commonMain.

Estas deudas son idénticas a las que existían tras Phase 6C. Se abordarán en un PR de limpieza previo a la compilación real de la app iOS.

---

## Verificación

```bash
./gradlew :feature:onboarding:assembleDebug          # ✅
./gradlew :feature:onboarding:testDebugUnitTest      # ✅ (NewOnboarding + CapacityTank tests)
./gradlew :app:assembleDebug                          # ✅
./gradlew :app:testProdDebugUnitTest                  # ✅ (KoinModulesTest)
```
