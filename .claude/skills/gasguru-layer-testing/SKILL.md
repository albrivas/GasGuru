---
name: skill-gasguru-layer-testing
description: "Genera y actualiza tests unitarios en GasGuru con fakes reales, JUnit5 y patrones del proyecto para cualquier capa: ViewModel, UseCase, Repository, DataSource, Manager, Worker, Mapper, Extensions. Usar siempre que el usuario pida escribir, crear, añadir o actualizar tests — frases como 'crea el test de X', 'testea X', 'añade tests a X', 'le falta test', 'sin cobertura', 'actualiza los tests de X' o 'acabo de crear X ponle tests'. También activar al crear una clase nueva sin tests aunque no se mencione explícitamente. NO activar para refactors sin tests, bug fixes, configuración de JaCoCo/CI ni Espresso/UI tests."
metadata:
  short-description: GasGuru unit tests with fakes
---

# GasGuru Testing

Skill unificada para escribir o actualizar tests unitarios en GasGuru — cubre todas las capas.

## Convenciones generales (obligatorias)

> ⛔ **REGLA ABSOLUTA:** `@DisplayName` **NUNCA** va en la clase de test. Solo en cada método `@Test`.
> Una clase de test no lleva ninguna anotación `@DisplayName`. Si se añade, es un error.

- **JUnit5** (`org.junit.jupiter.api`): `@Test`, `@BeforeEach`, `@AfterEach`, `@DisplayName`
- **NO `@DisplayName` en la clase**, SÍ en cada `@Test`
- **`@DisplayName`** siempre con triple `"""` y GIVEN/WHEN/THEN en líneas separadas (nunca en una sola línea):
  ```kotlin
  @DisplayName(
      """
      GIVEN ...
      WHEN ...
      THEN ...
      """
  )
  ```
- **`sut`** para system under test — `lateinit var` inicializado en `@BeforeEach`
- **`@ExtendWith(CoroutinesTestExtension::class)`** cuando hay coroutines
- **`runTest`** de `kotlinx.coroutines.test` para tests con coroutines
- **Turbine** para Flow/StateFlow: `.test { awaitItem(); cancelAndIgnoreRemainingEvents() }`
- **Named arguments** siempre (`sut(vehicle = newVehicle)`)
- **Trailing comma** en listas y llamadas multilínea
- **Variables descriptivas** — nada de `v`, `it`, `x`
- **Fakes primero, MockK como último recurso** — ver `rules/fakes-vs-mocks.md`
- Test data como `private val` en la clase (si se reutiliza) o inline en el test

---

## Patrón 1: ViewModel

```kotlin
@ExtendWith(CoroutinesTestExtension::class)
class XxxViewModelTest {

    private lateinit var fakeXxxRepository: FakeXxxRepository
    private lateinit var sut: XxxViewModel

    @BeforeEach
    fun setUp() {
        fakeXxxRepository = FakeXxxRepository()
        sut = createViewModel()
    }

    private fun createViewModel(id: Long = 1L) = XxxViewModel(
        savedStateHandle = SavedStateHandle(mapOf("idXxx" to id)),  // navigation args
        getXxxUseCase = GetXxxUseCase(xxxRepository = fakeXxxRepository),  // use case real
        analyticsHelper = NoOpAnalyticsHelper(),                           // no verificar eventos
    )

    @Test
    @DisplayName(
        """
        GIVEN initial state
        WHEN viewmodel is created
        THEN emits Loading state
        """
    )
    fun initialStateIsLoading() = runTest {
        sut.uiState.test {
            assertEquals(XxxUiState.Loading, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    @DisplayName(
        """
        GIVEN data is available
        WHEN Load event is handled
        THEN emits Success state with data
        """
    )
    fun loadEventEmitsSuccessState() = runTest {
        fakeXxxRepository.setItems(listOf(Xxx(id = 1L)))

        sut.uiState.test {
            assertEquals(XxxUiState.Loading, awaitItem())
            sut.handleEvent(XxxEvent.Load)
            advanceUntilIdle()
            assertTrue(awaitItem() is XxxUiState.Success)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
```

**Reglas ViewModel:**
- `private fun createViewModel(...)` helper para construir el VM — facilita reusar con variaciones
- Usar **use cases reales** con repositorios fake — nunca mockear use cases
- `SavedStateHandle(mapOf("key" to value))` para navigation args
- `NoOpAnalyticsHelper()` si no se verifica qué eventos se mandan
- `FakeAnalyticsHelper()` local si el test verifica eventos concretos
- `advanceUntilIdle()` antes de esperar emisiones de coroutines lanzadas en `viewModelScope`
- Si el VM inyecta `ioDispatcher`: pasar `UnconfinedTestDispatcher()`
- StateFlow no emite el mismo valor dos veces — esperar solo cambios reales de estado

---

## Patrón 2: UseCase

```kotlin
@ExtendWith(CoroutinesTestExtension::class)
class XxxUseCaseTest {

    private lateinit var fakeXxxRepository: FakeXxxRepository
    private lateinit var sut: XxxUseCase

    @BeforeEach
    fun setUp() {
        fakeXxxRepository = FakeXxxRepository()
        sut = XxxUseCase(xxxRepository = fakeXxxRepository)
    }

    @Test
    @DisplayName(
        """
        GIVEN valid input
        WHEN invoke is called
        THEN item is persisted in the repository
        """
    )
    fun xxxPersistsItem() = runTest {
        val item = Xxx(...)

        sut(item = item)

        val saved = fakeXxxRepository.getAll().first()
        assertEquals(1, saved.size)
        assertEquals(item, saved.first())
    }
}
```

**Reglas UseCase:**
- `sut(param = value)` — operator invoke con named argument
- Verificar resultado vía estado del fake: `fakeRepo.savedItems` o `fakeRepo.getX().first()`

---

## Patrón 3: Repository

```kotlin
@ExtendWith(CoroutinesTestExtension::class)
class OfflineXxxRepositoryTest {

    private lateinit var fakeXxxDao: FakeXxxDao
    private lateinit var sut: OfflineXxxRepository

    private val entityA = XxxEntity(id = 1L, ...)

    @BeforeEach
    fun setUp() {
        fakeXxxDao = FakeXxxDao()
        sut = OfflineXxxRepository(xxxDao = fakeXxxDao)
    }

    @Test
    @DisplayName(
        """
        GIVEN items exist
        WHEN getAll is called
        THEN returns mapped domain models
        """
    )
    fun getAllReturnsMappedItems() = runTest {
        fakeXxxDao.insert(entity = entityA)

        sut.getAll().test {
            val items = awaitItem()
            assertEquals(1, items.size)
            assertEquals(1L, items.first().id)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
```

**Reglas Repository:**
- Fake DAO en `@BeforeEach`, preparar estado con `fakeDao.insert(...)` / `fakeDao.setItems(...)`
- Turbine siempre para flows reactivos
- Testear mapping entity→domain, CRUD completo

---

## Patrón 4: Manager / Service

```kotlin
@ExtendWith(CoroutinesTestExtension::class)
class XxxManagerTest {

    private lateinit var fakeXxxRepository: FakeXxxRepository
    private lateinit var sut: XxxManagerImpl

    @BeforeEach
    fun setUp() {
        fakeXxxRepository = FakeXxxRepository()
        sut = XxxManagerImpl(xxxRepository = fakeXxxRepository)
    }

    @Test
    @DisplayName(
        """
        GIVEN pending items exist
        WHEN sync is triggered
        THEN items are processed and marked as synced
        """
    )
    fun syncProcessesPendingItems() = runTest {
        fakeXxxRepository.setItems(listOf(Xxx(id = 1L, pending = true)))

        sut.sync()

        assertEquals(listOf(1L), fakeXxxRepository.syncedIds)
    }
}
```

---

## Patrón 5: Worker

```kotlin
@ExtendWith(CoroutinesTestExtension::class)
class XxxWorkerTest {

    private val context = mockk<Context>(relaxed = true)          // Android framework → mockk ok
    private val workerParameters = mockk<WorkerParameters>(relaxed = true)
    private val xxxUseCase = mockk<XxxUseCase>()

    @BeforeEach
    fun setUp() {
        startKoin {
            modules(module {
                single { xxxUseCase }
                single<AnalyticsHelper> { NoOpAnalyticsHelper() }
            })
        }
    }

    @AfterEach
    fun tearDown() {
        stopKoin()
        unmockkAll()
    }

    @Test
    @DisplayName(
        """
        GIVEN use case succeeds
        WHEN doWork is called
        THEN returns Result.success
        """
    )
    fun returnsSuccessWhenUseCaseSucceeds() = runTest {
        coEvery { xxxUseCase() } just Runs
        val worker = XxxWorker(context, workerParameters)
        assertEquals(Result.success(), worker.doWork())
    }

    @Test
    @DisplayName(
        """
        GIVEN use case throws an exception
        WHEN doWork is called
        THEN returns Result.retry
        """
    )
    fun returnsRetryWhenUseCaseFails() = runTest {
        coEvery { xxxUseCase() } throws RuntimeException("error")
        val worker = XxxWorker(context, workerParameters)
        assertEquals(Result.retry(), worker.doWork())
    }
}
```

---

## Patrón 6: DataSource (red / remoto)

Los DataSources de red usan `MockWebServer` (OkHttp) para simular respuestas HTTP sin red real.

```kotlin
@ExtendWith(CoroutinesTestExtension::class)
class XxxRemoteDataSourceTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var sut: XxxRemoteDataSource

    @BeforeEach
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()
        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        sut = XxxRemoteDataSource(api = retrofit.create(XxxApi::class.java))
    }

    @AfterEach
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    @DisplayName(
        """
        GIVEN a successful HTTP 200 response with valid body
        WHEN fetchXxx is called
        THEN returns Either.Right with the parsed data
        """
    )
    fun successResponseReturnsParsedData() = runTest {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody("""{"id":1,"name":"Test"}"""),
        )

        val result = sut.fetchXxx()

        assertTrue(result.isRight())
        assertEquals(1, result.getOrNull()?.id)
    }

    @Test
    @DisplayName(
        """
        GIVEN an HTTP 500 response
        WHEN fetchXxx is called
        THEN returns Either.Left with an error
        """
    )
    fun serverErrorReturnsLeft() = runTest {
        mockWebServer.enqueue(MockResponse().setResponseCode(500))

        val result = sut.fetchXxx()

        assertTrue(result.isLeft())
    }
}
```

**Reglas DataSource:**
- `MockWebServer` para datos remotos — `start()` en `@BeforeEach`, `shutdown()` en `@AfterEach`
- Testear al menos: respuesta OK, error HTTP (4xx/5xx), body malformado
- Usar `Either<L, R>` si el DataSource lo devuelve — verificar con `isRight()` / `isLeft()`
- MockK solo para dependencias de SDK externo (ej: `GoogleApiClient`) sin interfaz propia

---

## Patrón 7: Mapper / Utils / Extensions (funciones puras)

```kotlin
// SIN @ExtendWith, SIN runTest — funciones síncronas puras
class FuelStationMapperTest {

    @Test
    @DisplayName(
        """
        GIVEN a price string with comma separator
        WHEN toSafeDouble is called
        THEN returns the correct double value
        """
    )
    fun priceStringWithCommaConvertsToDouble() {
        val result = "1,459".toSafeDouble()
        assertEquals(1.459, result, 0.001)
    }

    @Test
    @DisplayName(
        """
        GIVEN an empty price string
        WHEN toSafeDouble is called
        THEN returns zero
        """
    )
    fun emptyPriceStringReturnsZero() {
        val result = "".toSafeDouble()
        assertEquals(0.0, result, 0.001)
    }
}
```

**Reglas Mapper/Utils:**
- **Sin** `@ExtendWith`, `runTest` ni Turbine
- Un `@Test` por escenario — edge cases obligatorios: null, empty, formato incorrecto, boundary values
- Usar `@Nested` para agrupar tests de funciones distintas cuando hay muchos

---

## Creación de fakes

Ver `rules/fake-creation-patterns.md` para patrón completo.

**Ubicación** (de `docs/TESTING.md`):
- Fake reutilizable en **>1 módulo** → `core/testing/src/main/java/com/gasguru/core/testing/fakes/data/<capa>/`
- Fake usado solo por **este módulo** → `<module>/src/test/java/.../fakes/`
- **NO añadir dependencias a `core:testing`** sin al menos 2 módulos consumidores

Para ver los fakes disponibles en `core:testing`, ejecutar:
```
find core/testing/src/main -name "Fake*" -type f
```

---

## Reglas

- `rules/edge-cases.md` — cómo identificar y testear casos esquina
- `rules/displayname-and-naming.md` — formato de @DisplayName y nombres
- `rules/fakes-vs-mocks.md` — cuándo usar fake vs mock
- `rules/fake-creation-patterns.md` — cómo crear fakes correctamente
- `rules/flow-and-coroutines.md` — Turbine, runTest, StateFlow