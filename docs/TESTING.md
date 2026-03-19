# Testing

## Objetivo
Guia para tests unitarios de ViewModel y uso de fakes en GasGuru. La idea es evitar mocks siempre que sea posible y usar repositorios/DAOs fake para mantener la logica real de los use case.

## Estructura de fakes

### Donde vive cada fake

| Ubicacion | Criterio |
|-----------|----------|
| `core/testing/src/main/.../fakes/` | El fake se reutiliza en **mas de un modulo** |
| `modulo/src/test/.../fakes/` | El fake solo lo usa ese modulo |

Poner un fake en `core:testing` cuando solo lo usa un modulo añade una dependencia innecesaria al modulo compartido. Si en el futuro el fake se necesita en otro modulo, se mueve entonces.

### Que pertenece a `core:testing`

- Fakes de DAOs (`FakePriceAlertDao`, `FakeVehicleDao`...) — usados en tests de repositorios y ViewModels de multiples features.
- Fakes de repositorios (`FakeVehicleRepository`, `FakeUserDataRepository`...) — inyectados en ViewModels de varias features.
- `FakeNetworkMonitor` — el monitor de red lo usan varios repositorios del proyecto.
- Helpers de test (`CoroutinesTestExtension`, builders de entidades...).

### Que NO pertenece a `core:testing`

- Fakes de servicios externos especificos de un modulo (`SupabaseManager`, `OneSignalManager`...) — si solo los usa `core:data`, van en `core/data/src/test/`.
- Fakes de analytics para verificar eventos concretos (`FakeAnalyticsHelper`) — `NoOpAnalyticsHelper` ya existe en `core:analytics` para cuando no importan los eventos; si un test necesita verificar eventos especificos, el fake va en ese modulo.

### Jerarquia de dependencias en `core:testing`

`core:testing` ya depende de `core:data`, `core:database` y `core:network`. **No añadir dependencias nuevas** a este modulo sin que haya al menos dos modulos consumidores que las necesiten.

### Organizacion por capa
- `fakes/data/user`, `fakes/data/location`, `fakes/data/search`, etc.
- `fakes/data/database` para DAOs fake cuando usamos repositorios offline reales.
- `fakes/data/route`, `fakes/data/filter`, `fakes/data/maps`, `fakes/data/geocoder`, etc.

## Enfoque recomendado para ViewModel
1. Usar los **use case reales**.
2. Inyectar **repositorios/DAOs fake** para controlar entradas y registrar llamadas.
3. Evitar mocks salvo que no exista interfaz clara o sea imposible crear un fake simple.

Ventajas:
- Tests mas cercanos a la logica real.
- Menos fragiles frente a refactors internos de los use case.

## Convenciones de tests
- Usar **JUnit5**.
- Usar **Turbine** para `Flow`/`StateFlow`.
- Ubicacion: `src/test/kotlin/...` (tests JVM).
- Nombres tipo `GIVEN ... WHEN ... THEN ...`.

Ejemplo basico:
```kotlin
@Test
@DisplayName("GIVEN empty query WHEN updating search query THEN emits EmptyQuery")
fun shortQueryEmitsEmptyQuery() = runTest {
    sut.searchResultUiState.test {
        assertEquals(SearchResultUiState.Loading, awaitItem())
        assertEquals(SearchResultUiState.EmptyQuery, awaitItem())

        sut.handleEvent(GasGuruSearchBarEvent.UpdateSearchQuery("ba"))
        assertTrue(awaitItem() is SearchResultUiState.Success)

        sut.handleEvent(GasGuruSearchBarEvent.UpdateSearchQuery("b"))
        assertEquals(SearchResultUiState.EmptyQuery, awaitItem())
    }
}
```

## Dispatchers en tests
- `CoroutinesTestExtension` reemplaza **solo** `Dispatchers.Main`.
- Si un VM inyecta un `Dispatcher` (por ejemplo `defaultDispatcher`), hay que pasar **el dispatcher de test** para que `advanceUntilIdle()` funcione y el test sea determinista.
- Si se usa `Dispatchers.IO/Default` en el VM sin inyeccion, los tests no controlan ese scheduler.

## Casos tipicos en VMs
- **StateFlow**: puede emitir el mismo valor solo una vez. Si el estado no cambia, no hay nueva emision.
- **Flows iniciales**: algunos tests deben tolerar que el primer valor sea el `initialValue`.

## Ejemplos en el proyecto
Tests de VM:
- `feature/favorite-list-station/src/test/...`
- `feature/route-planner/src/test/...`
- `feature/station-map/src/test/...`
- `feature/detail-station/src/test/...`
- `core/components/src/test/...`
- `app/src/test/...`

Fakes:
- `core/testing/src/main/java/com/gasguru/core/testing/fakes/...`

## Cuando usar fakes vs mocks

**Fakes primero, mocks como ultimo recurso.**

Usa **fake** si:
- El objeto tiene una interfaz clara.
- Necesitas controlar su estado o comportamiento entre llamadas (ej: devolver errores, registrar llamadas).
- Quieres mantener la logica real de repositorios/use cases en el test.

Usa **mock** solo si:
- El objeto es complejo de instanciar y un fake seria mas codigo que valor.
- La dependencia viene de una libreria externa sin interfaz propia (ej: `MixpanelAPI`).

> Ejemplo: `SupabaseManager` y `OneSignalManager` son interfaces simples propias del proyecto — siempre fake, nunca mock.

## Checklist rapido
- Usa `CoroutinesTestExtension`.
- Usa `Turbine` para flujos.
- Inyecta repositorios fake.
- Verifica estado y efectos (llamadas registradas en fakes).

## Dependency Injection en tests

Los tests unitarios de ViewModel **no requieren Koin**. Se instancian los ViewModels directamente pasando fakes por constructor:

```kotlin
private val sut = MyViewModel(
    myUseCase = FakeMyRepository(),
    ioDispatcher = UnconfinedTestDispatcher(),
)
```

Para tests de integración que necesiten el grafo completo, usar `koin-test-junit5` (disponible via `projects.core.testing`) con `KoinTest` y `checkModules()`.

Ver [DEPENDENCY_INJECTION.md](DEPENDENCY_INJECTION.md) para detalles sobre la arquitectura DI.
