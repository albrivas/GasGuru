# Creación de Fakes — DO / DON'T

## Ubicación del fake

❌ **MAL** — Fake que solo usa un módulo en `core:testing`:
```
// Si FakeSupabaseManager solo lo usa core:data, NO va aquí:
core/testing/src/main/java/com/gasguru/core/testing/fakes/data/FakeSupabaseManager.kt
```

✅ **BIEN** — Fake local en el módulo que lo necesita:
```
// Solo lo usa core:data → va en:
core/data/src/test/java/com/gasguru/data/fakes/FakeSupabaseManager.kt
```

✅ **BIEN** — Fake compartido en `core:testing` cuando lo usan múltiples módulos:
```
// FakeVehicleRepository lo usan feature/vehicle, feature/station-map, app...
core/testing/src/main/java/com/gasguru/core/testing/fakes/data/vehicle/FakeVehicleRepository.kt
```

---

## Estructura interna del fake

❌ **MAL** — Sin estado reactivo, sin hooks de aserción:
```kotlin
class FakeVehicleRepository : VehicleRepository {
    private val vehicles = mutableListOf<Vehicle>()

    override fun getVehiclesForUser(userId: Long) = flowOf(vehicles)  // no reactivo
    override suspend fun upsertVehicle(vehicle: Vehicle): Long {
        vehicles.add(vehicle)
        return vehicle.id
    }
}
```

✅ **BIEN** — Con `MutableStateFlow` reactivo + hooks de aserción:
```kotlin
class FakeVehicleRepository(
    initialVehicles: List<Vehicle> = emptyList(),   // estado inicial inyectable
) : VehicleRepository {

    private val vehiclesFlow = MutableStateFlow(initialVehicles)

    // Hooks de aserción — expuestos para verificar side effects en tests
    val deletedVehicleIds = mutableListOf<Long>()
    val updatedFuelTypes = mutableListOf<Pair<Long, FuelType>>()

    override fun getVehiclesForUser(userId: Long): Flow<List<Vehicle>> =
        vehiclesFlow.map { list -> list.filter { it.userId == userId } }

    override suspend fun upsertVehicle(vehicle: Vehicle): Long {
        vehiclesFlow.update { list ->
            val index = list.indexOfFirst { it.id == vehicle.id }
            if (index >= 0) list.toMutableList().also { it[index] = vehicle }
            else list + vehicle
        }
        return vehicle.id
    }

    override suspend fun deleteVehicle(vehicleId: Long) {
        deletedVehicleIds.add(vehicleId)           // registrar para aserción
        vehiclesFlow.update { list -> list.filter { it.id != vehicleId } }
    }

    // Setter para control del test — evita llamar a métodos del sut para setup
    fun setVehicles(vehicles: List<Vehicle>) { vehiclesFlow.value = vehicles }
}
```

---

## Simulación de errores

❌ **MAL** — Sin soporte para simular errores:
```kotlin
override fun getAll(): Flow<List<Xxx>> = vehiclesFlow
```

✅ **BIEN** — Con flag de error:
```kotlin
var shouldThrowError = false

override fun getAll(): Flow<List<Xxx>> {
    if (shouldThrowError) return flow { throw Exception("Fake error") }
    return xxxFlow
}

// Con excepción específica por operación:
var shouldThrowOnAdd: Exception? = null

override suspend fun add(item: Xxx) {
    shouldThrowOnAdd?.let { throw it }
    savedItems.add(item)
    xxxFlow.update { it + item }
}
```

---

## Fake local mínimo (solo tracking)

Cuando la dependencia es simple y solo necesitamos registrar llamadas:

✅ **BIEN** — Fake local sin MutableStateFlow (interfaz sin estado reactivo):
```kotlin
// core/data/src/test/java/com/gasguru/data/fakes/FakeSupabaseManager.kt
class FakeSupabaseManager : SupabaseManager {
    val addedAlerts = mutableListOf<Int>()
    val removedAlerts = mutableListOf<Int>()

    var shouldThrowOnAdd: Exception? = null

    override suspend fun addPriceAlert(
        stationId: Int,
        onesignalPlayerId: String,
        fuelType: String,
        lastNotifiedPrice: Double,
    ) {
        shouldThrowOnAdd?.let { throw it }
        addedAlerts.add(stationId)
    }

    override suspend fun removePriceAlert(stationId: Int) {
        removedAlerts.add(stationId)
    }
}
```

---

## Verificar con el fake — asserting side effects

❌ **MAL** — Usar verify de MockK para verificar llamadas:
```kotlin
coVerify { vehicleRepository.deleteVehicle(vehicleId = 1L) }
```

✅ **BIEN** — Verificar con los hooks del fake:
```kotlin
sut.deleteVehicle(vehicleId = 1L)
assertEquals(listOf(1L), fakeVehicleRepository.deletedVehicleIds)
```