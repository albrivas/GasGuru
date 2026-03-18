# Fakes vs Mocks — DO / DON'T

## Regla principal

**Siempre fakes. MockK solo como último recurso.**

Un fake es una implementación in-memory real de la interfaz. Un mock es una sustitución generada que intercepta llamadas.

---

## Cuándo usar Fake

✅ **BIEN** — Dependencia con interfaz propia del proyecto:
```kotlin
// FakeVehicleRepository implementa VehicleRepository
private lateinit var fakeVehicleRepository: FakeVehicleRepository

@BeforeEach
fun setUp() {
    fakeVehicleRepository = FakeVehicleRepository()
    sut = AddVehicleUseCase(vehicleRepository = fakeVehicleRepository)
}

@Test
fun addVehiclePersistsVehicle() = runTest {
    sut(vehicle = newVehicle)
    val saved = fakeVehicleRepository.getVehiclesForUser(userId = 0L).first()
    assertEquals(1, saved.size)   // verificar vía estado del fake
}
```

✅ **BIEN** — Interfaces propias simples (siempre fake, nunca mock):
- `SupabaseManager` → `FakeSupabaseManager`
- `OneSignalManager` → `FakeOneSignalManager`
- `AnalyticsHelper` (cuando se quiere verificar eventos) → `FakeAnalyticsHelper`

---

## Cuándo usar MockK (último recurso)

✅ **BIEN** — Librería externa de Android sin interfaz propia:
```kotlin
// Context y WorkerParameters son clases Android sin interfaz
private val context = mockk<Context>(relaxed = true)
private val workerParameters = mockk<WorkerParameters>(relaxed = true)
```

✅ **BIEN** — SDK externo sin interfaz propia del proyecto:
```kotlin
// MixpanelAPI es una clase del SDK de Mixpanel
private val mixpanelApi: MixpanelAPI = mockk(relaxed = true)
```

✅ **BIEN** — Cuando el setup del fake sería más complejo que el propio test:
```kotlin
// UseCase en Worker tests (Koin lo resuelve)
private val getFuelStationUseCase = mockk<GetFuelStationUseCase>()
```

---

## MAL — Mock de interfaz propia del proyecto

❌ **MAL** — No usar mock si existe (o puede crearse) un fake:
```kotlin
// MAL: VehicleRepository es interfaz propia del proyecto
private val vehicleRepository = mockk<VehicleRepository>()

@Test
fun test() = runTest {
    coEvery { vehicleRepository.upsertVehicle(any()) } returns 1L
    sut(vehicle = newVehicle)
    coVerify { vehicleRepository.upsertVehicle(newVehicle) }
}
```

✅ **BIEN** — Usar el fake existente:
```kotlin
private lateinit var fakeVehicleRepository: FakeVehicleRepository

@BeforeEach
fun setUp() {
    fakeVehicleRepository = FakeVehicleRepository()
    sut = AddVehicleUseCase(vehicleRepository = fakeVehicleRepository)
}

@Test
fun test() = runTest {
    sut(vehicle = newVehicle)
    val saved = fakeVehicleRepository.getVehiclesForUser(userId = 0L).first()
    assertEquals(newVehicle, saved.first())
}
```

---

## Analytics: NoOp vs Fake

✅ **BIEN** — Cuando el test NO necesita verificar eventos analytics:
```kotlin
// NoOpAnalyticsHelper ya existe en core:analytics
single<AnalyticsHelper> { NoOpAnalyticsHelper() }
```

✅ **BIEN** — Cuando el test SÍ necesita verificar qué eventos se enviaron:
```kotlin
// FakeAnalyticsHelper local en el módulo que lo necesita
class FakeAnalyticsHelper : AnalyticsHelper {
    val loggedEvents = mutableListOf<AnalyticsEvent>()
    override fun logEvent(event: AnalyticsEvent) { loggedEvents.add(event) }
}

// En el test:
assertEquals(AnalyticsEvent.Types.VEHICLE_CREATED, fakeAnalytics.loggedEvents.first().type)
```

❌ **MAL** — MockK para AnalyticsHelper:
```kotlin
private val analyticsHelper = mockk<AnalyticsHelper>(relaxed = true)
// ...
verify { analyticsHelper.logEvent(any()) }
```