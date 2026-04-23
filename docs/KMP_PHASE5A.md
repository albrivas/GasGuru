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

### Cambio de contrato: `BackWithData.value: Any → String`

Los args de navegación (`PlaceArgs`, `RoutePlanArgs`) dejaron de ser `@Parcelize`/`Parcelable` y pasaron a ser `@Serializable`. Esto implica que `BackWithData.value` cambió de `Any` a `String` (JSON serializado):

```kotlin
// Antes
navigationManager.navigateBackWithData(key = KEY, value = PlaceArgs(...))

// Después
navigationManager.navigateBackWithData(
    key = KEY,
    value = Json.encodeToString(PlaceArgs(...)),
)
```

En el receptor:
```kotlin
// Antes
val result = navBackResult.getPreviousResult<PlaceArgs?>(KEY)

// Después
val resultJson = navBackResult.getPreviousResult<String?>(KEY)
val result = resultJson?.let { Json.decodeFromString<PlaceArgs>(it) }
```

Los datos siguen persistiendo correctamente en `savedStateHandle` y sobreviven a process death (los `String` son soportados nativamente por `Bundle`).

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
| `feature:search` | `Json.encodeToString(PlaceArgs(...))` en call site + `kotlinx-serialization-json` dep |
| `feature:route-planner` | `Json.encodeToString(RoutePlanArgs(...))` + `Json.decodeFromString<PlaceArgs>()` + `kotlinx-serialization-json` dep |
| `core:testing` | `FakeNavigationManager.navigateBackWithData` signature `Any → String` |

---

## Lecciones aprendidas

- **Navigation Compose CMP** (`org.jetbrains.androidx.navigation:navigation-compose`) es el camino estándar para KMP, no requiere cambiar la API ni los imports en los consumidores.
- Con CMP navigation, **todos** los archivos del módulo pueden ir a `commonMain` — incluyendo los que usan `NavController` y `staticCompositionLocalOf`.
- Cambiar `BackWithData.value: Any → String` hace el contrato explícito y elimina la dependencia de `Parcelable`.
