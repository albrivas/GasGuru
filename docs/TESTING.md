# Testing

## Objetivo
Guia para tests unitarios de ViewModel y uso de fakes en GasGuru. La idea es evitar mocks siempre que sea posible y usar repositorios/DAOs fake para mantener la logica real de los use case.

## Estructura de fakes
- `core/testing`: modulo con fakes reutilizables.
- Organizacion por capa:
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
Usa fakes si:
- El objeto tiene una interfaz clara.
- Necesitas registrar llamadas y devolver datos controlados.
- Quieres mantener logica de repositorios/use cases reales.

Usa mocks si:
- El objeto es complejo o dificil de instanciar.
- No aporta valor mantener su implementacion real en el test.

## Checklist rapido
- Usa `CoroutinesTestExtension`.
- Usa `Turbine` para flujos.
- Inyecta repositorios fake.
- Verifica estado y efectos (llamadas registradas en fakes).
