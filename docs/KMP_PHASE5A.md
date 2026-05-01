# KMP Phase 5A — Migración de `:navigation`

## Objetivo

Migrar el módulo `:navigation` a Kotlin Multiplatform (KMP) usando **Navigation Compose CMP** (`org.jetbrains.androidx.navigation:navigation-compose`) en lugar del artefacto Android-only de Jetpack.

---

## Cambios principales

### Artefacto de Navigation

| Antes | Después |
|-------|---------|
| `androidx.navigation:navigation-compose:2.9.6` | `org.jetbrains.androidx.navigation:navigation-compose:2.9.2` |

El artefacto de JetBrains expone los mismos package names (`androidx.navigation.*`) y la misma API, con soporte para Android, iOS, Desktop y Web.

### Estructura de fuentes

Todos los archivos del módulo pasaron de `src/main/java/` a `src/commonMain/kotlin/`:

```
navigation/src/commonMain/kotlin/com/gasguru/navigation/
  GlobalCompositionLocal.kt            ← staticCompositionLocalOf (disponible en CMP)
  constants/NavigationKeys.kt
  deeplink/DeepLinkStateHolder.kt
  di/NavigationManagerModule.kt
  extensions/NavigationExtensions.kt   ← NavController/NavBackStackEntry (disponible en CMP nav)
  manager/NavigationCommand.kt
  manager/NavigationDestination.kt
  manager/NavigationManager.kt
  manager/NavigationManagerImpl.kt
  models/PlaceArgs.kt
  models/RoutePlanArgs.kt
```

El módulo es **100% commonMain** — sin `androidMain`.

### Cambio de contrato: `BackWithData.value: Any → Any?` (genérico)

Los args de navegación (`PlaceArgs`, `RoutePlanArgs`) dejaron de ser `@Parcelize`/`Parcelable` y pasaron a ser `@Serializable`. El contrato de `BackWithData` evolucionó en dos pasos:

**Fase 5A (inicial):** `value` se tipó como `String` y los callers codificaban a JSON manualmente.  
**Estado actual:** `value` es `Any?` y `navigateBackWithData` es genérico — `SavedStateHandle` (lifecycle 2.9+) serializa/deserializa las clases `@Serializable` automáticamente mediante kotlinx.serialization, sin JSON manual.

```kotlin
// Pasar resultado de vuelta (call site)
navigationManager.navigateBackWithData(
    key = KEY,
    value = PlaceArgs(name = "Madrid", id = "place123"),
)

// Leer en el receptor
val result = navBackResult.getPreviousResult<PlaceArgs?>(KEY)
```

Los datos persisten en `savedStateHandle` y sobreviven a process death: `lifecycle 2.9+` usa `encodeToSavedState`/`decodeFromSavedState` para serializar clases `@Serializable` al Bundle.

### Limpieza

- Eliminado `proguard-rules.pro` (contenía reglas de Hilt/Dagger obsoletas — el módulo usa Koin)
- Eliminado plugin `kotlin.parcelize` del `build.gradle.kts`

---

## Dependencias del módulo (`build.gradle.kts`)

```kotlin
plugins {
    alias(libs.plugins.gasguru.kmp.library)
    alias(libs.plugins.gasguru.koin)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.compose.multiplatform.runtime)
            implementation(libs.jetbrains.navigation.compose)
            implementation(projects.core.model)
        }
    }
}
```

---

## Módulos consumidores afectados

| Módulo | Cambio |
|--------|--------|
| `app` | `androidx.navigation.compose` → `jetbrains.navigation.compose` |
| `feature:search` | Pasa `PlaceArgs(...)` directamente (sin JSON) |
| `feature:route-planner` | Pasa `RoutePlanArgs(...)` directamente; lee `PlaceArgs` directamente vía `getPreviousResult<PlaceArgs?>` |
| `core:testing` | `FakeNavigationManager.navigateBackWithData` es genérico: `fun <T> navigateBackWithData(key, value: T)` |

---

## Lecciones aprendidas

- **Navigation Compose CMP** (`org.jetbrains.androidx.navigation:navigation-compose`) es el camino estándar para KMP, no requiere cambiar la API ni los imports en los consumidores.
- Con CMP navigation, **todos** los archivos del módulo pueden ir a `commonMain` — incluyendo los que usan `NavController` y `staticCompositionLocalOf`.
- El contrato `BackWithData.value: Any?` con `navigateBackWithData<T>` genérico es la solución KMP-nativa: elimina `Parcelable` y el JSON manual, delegando la persistencia a `SavedStateHandle` + kotlinx.serialization (lifecycle 2.9+).
