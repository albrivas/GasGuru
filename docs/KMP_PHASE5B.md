# KMP Phase 5B — Migración de `:core:testing`

## Objetivo

Migrar el módulo `:core:testing` de Android-only (`gasguru.android.library`) a KMP (`gasguru.kmp.library`), moviendo los 19 fakes a `commonMain` para que sean reutilizables en `commonTest` de cualquier módulo KMP futuro.

---

## Cambios principales

### Plugin y estructura de fuentes

| Antes | Después |
|-------|---------|
| `gasguru.android.library` | `gasguru.kmp.library` |
| `src/main/java/` (22 ficheros) | `commonMain` (20) + `androidMain` (3) |

### Ficheros movidos a `commonMain` (20)

Todos los fakes son Kotlin puro — implementan interfaces que ya estaban en `commonMain` desde fases anteriores:

| Categoría | Ficheros |
|-----------|----------|
| Analytics | `FakeAnalyticsHelper` |
| DAO (database) | `FakeFavoriteStationDao`, `FakeFuelStationDao`, `FakePriceAlertDao`, `FakeUserDataDao`, `FakeVehicleDao` |
| Repository (data) | `FakePriceAlertRepository`, `FakeFilterRepository`, `FakeGeocoderAddress`, `FakeLocationTracker`, `FakeStaticMapRepository`, `FakeNetworkMonitor`, `FakeRemoteDataSource`, `FakePlacesRepository`, `FakeRoutesRepository`, `FakeOfflineRecentSearchRepository`, `FakeUserDataRepository`, `FakeVehicleRepository` |
| Navigation | `FakeNavigationManager` |
| Nuevo helper | `CoroutineTestHelper` |

### Ficheros que permanecen en `androidMain` (2)

| Fichero | Motivo |
|---------|--------|
| `BaseTest.kt` | Usa `android.content.Context`, `ApplicationProvider` y `createComposeExtension()` |
| `CoroutinesTestRuleExtension.kt` | JUnit5 Extension API — JVM only |

`CoroutinesTestRule.kt` (JUnit4 legacy) eliminado — no se usaba en ningún módulo.

---

## Cambio de código: `FakeUserDataRepository`

`System.currentTimeMillis()` sustituido por `Clock.System.now().toEpochMilliseconds()` de `kotlin.time` (stdlib KMP):

```kotlin
// Antes
userDataFlow.update { it.copy(lastUpdate = System.currentTimeMillis()) }

// Después
import kotlin.time.Clock
userDataFlow.update { it.copy(lastUpdate = Clock.System.now().toEpochMilliseconds()) }
```

---

## Nuevo: `CoroutineTestHelper`

Helper multiplataforma para tests con coroutines en `commonTest`. Sustituye a `CoroutinesTestRuleExtension` (JUnit5) y `CoroutinesTestRule` (JUnit4) cuando se escribe en `commonTest`:

```kotlin
class MyTest {
    private val coroutineTestHelper = CoroutineTestHelper()

    @BeforeTest fun setup() { coroutineTestHelper.setup() }
    @AfterTest  fun tearDown() { coroutineTestHelper.tearDown() }
}
```

---

## Build configuration

Las dependencias se dividen entre source sets:

- **`commonMain`**: proyectos KMP (`core:analytics`, `core:data`, `core:database`, `core:model`, `core:supabase`, `navigation`), `arrow-core`, `kotlinx-coroutines-test`
- **`androidMain`**: `core:network` (Android-only), `androidx.test.*`, `espresso`, `compose.ui.test`, JUnit4/5

Adicionalmente se mantiene un bloque `dependencies {}` raíz (fuera de `kotlin {}`) con las dependencias críticas para garantizar el runtime classpath a consumers Android (Lesson L001).

---

## Fixes adicionales

- **`core/uikit/build.gradle.kts`**: `implementation(projects.core.testing)` → `androidTestImplementation(...)` — solo se usa `BaseTest` en tests instrumentados.
- **`feature/search/build.gradle.kts`**: eliminada la línea duplicada de `androidTestImplementation(projects.core.testing)`.

---

## Rama base

Esta fase se basa en `feature/kmp-phase5a-navigation` (no en `develop`) porque requiere que `:navigation` ya sea KMP para que `FakeNavigationManager` y `projects.navigation` puedan estar en `commonMain`.

---

## Verificación

```bash
./gradlew :core:testing:assembleDebug                      # Android ✅
./gradlew :app:assembleDebug                               # App completa ✅
./gradlew :feature:station-map:testDebugUnitTest           # Tests consumers ✅
./gradlew :feature:vehicle:testDebugUnitTest               # ✅
./gradlew :app:testMockDebugUnitTest                       # ✅
./gradlew :core:network:testDebugUnitTest                  # ✅
```

iOS: `compileKotlinIosSimulatorArm64` requiere CocoaPods + `pod install` por la dependencia transitiva de `core:analytics` (Mixpanel). Issue pre-existente de Phase 4a, no relacionado con Phase 5B.
