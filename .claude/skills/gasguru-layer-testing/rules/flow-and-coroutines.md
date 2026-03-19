# Flow, Coroutines y Tests asíncronos — DO / DON'T

## @ExtendWith y runTest

❌ **MAL** — Sin `@ExtendWith` cuando hay coroutines:
```kotlin
class AddVehicleUseCaseTest {
    @Test
    fun addVehiclePersistsVehicle() = runTest {
        sut(vehicle = newVehicle)  // Dispatchers.Main puede fallar sin la extension
    }
}
```

✅ **BIEN** — Con `@ExtendWith` cuando la clase tiene coroutines:
```kotlin
@ExtendWith(CoroutinesTestExtension::class)
class AddVehicleUseCaseTest {
    @Test
    fun addVehiclePersistsVehicle() = runTest {
        sut(vehicle = newVehicle)
    }
}
```

❌ **MAL** — `@ExtendWith` en tests de funciones puras síncronas (innecesario):
```kotlin
@ExtendWith(CoroutinesTestExtension::class)  // no hace falta
class FuelStationMapperTest {
    @Test
    fun validPriceConvertsToDouble() {
        val result = "1,459".toSafeDouble()
        assertEquals(1.459, result, 0.001)
    }
}
```

✅ **BIEN** — Sin `@ExtendWith` en tests síncronos:
```kotlin
class FuelStationMapperTest {
    @Test
    fun validPriceConvertsToDouble() {
        val result = "1,459".toSafeDouble()
        assertEquals(1.459, result, 0.001)
    }
}
```

---

## Turbine para Flow/StateFlow

❌ **MAL** — Recoger el Flow sin Turbine (frágil, puede bloquear):
```kotlin
@Test
fun getVehiclesReturnsItems() = runTest {
    fakeDao.insert(entity = vehicleEntityA)
    val result = sut.getVehiclesForUser(userId = 10L).first()  // ok para suspend, no para Flow reactivo
    assertEquals(1, result.size)
}
```

✅ **BIEN** — Para repositorios con Flow reactivo, usar Turbine:
```kotlin
@Test
fun getVehiclesForUserReturnsMappedVehicles() = runTest {
    fakeVehicleDao.upsertVehicle(vehicle = vehicleEntityA)

    sut.getVehiclesForUser(userId = 10L).test {
        val vehicles = awaitItem()
        assertEquals(1, vehicles.size)
        assertEquals("Golf VII", vehicles.first().name)
        cancelAndIgnoreRemainingEvents()   // siempre al final
    }
}
```

❌ **MAL** — Olvidar `cancelAndIgnoreRemainingEvents()`:
```kotlin
sut.getVehiclesForUser(userId = 10L).test {
    val vehicles = awaitItem()
    assertEquals(1, vehicles.size)
    // Sin cancel → test puede bloquear o fallar con "unconsumed events"
}
```

✅ **BIEN** — Siempre `cancelAndIgnoreRemainingEvents()` al final del bloque test:
```kotlin
sut.getVehiclesForUser(userId = 10L).test {
    val vehicles = awaitItem()
    assertEquals(1, vehicles.size)
    cancelAndIgnoreRemainingEvents()
}
```

---

## Capturar argumentos con slot (mockk)

❌ **MAL** — Slot en `verify` después de la llamada (no captura correctamente):
```kotlin
analyticsHelper.logEvent(event = event)

val slot = slot<JSONObject>()
verify { mixpanelApi.track(any(), capture(slot)) }
assertEquals("vehicle", slot.captured.getString("category"))  // slot.captured puede ser null
```

✅ **BIEN** — Slot en `every` antes de la llamada:
```kotlin
val propertiesSlot = slot<JSONObject>()
every { mixpanelApi.track(any(), capture(propertiesSlot)) } just runs

analyticsHelper.logEvent(event = event)

assertEquals("vehicle", propertiesSlot.captured.getString("category"))
```

---

## StateFlow en ViewModel tests

❌ **MAL** — Esperar una emisión que no llega porque el estado no cambió:
```kotlin
sut.uiState.test {
    assertEquals(UiState.Loading, awaitItem())
    sut.handleEvent(Event.Load)
    assertEquals(UiState.Loading, awaitItem())  // StateFlow no emite el mismo valor dos veces
}
```

✅ **BIEN** — Esperar solo las emisiones que representan un cambio real:
```kotlin
sut.uiState.test {
    assertEquals(UiState.Loading, awaitItem())   // valor inicial
    sut.handleEvent(Event.Load)
    advanceUntilIdle()                           // forzar ejecución de coroutines
    assertEquals(UiState.Success(data), awaitItem())  // nuevo estado
    cancelAndIgnoreRemainingEvents()
}
```