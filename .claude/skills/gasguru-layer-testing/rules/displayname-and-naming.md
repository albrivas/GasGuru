# @DisplayName y nombres de funciones — DO / DON'T

## @DisplayName en la clase

❌ **MAL** — `@DisplayName` en la clase está prohibido:
```kotlin
@DisplayName("AddVehicleUseCaseTest")
class AddVehicleUseCaseTest {
```

✅ **BIEN** — Sin `@DisplayName` en la clase:
```kotlin
class AddVehicleUseCaseTest {
```

---

## Formato de @DisplayName en @Test

❌ **MAL** — Sin estructura GIVEN/WHEN/THEN:
```kotlin
@DisplayName("test add vehicle")
fun test1() = runTest { ... }
```

❌ **MAL** — GIVEN/WHEN/THEN en una sola línea (aunque sea corto):
```kotlin
@DisplayName("GIVEN a valid vehicle WHEN invoke is called THEN the vehicle is persisted")
fun addVehiclePersistsVehicle() = runTest { ... }
```

❌ **MAL** — Usando backticks en el nombre de función:
```kotlin
fun `add vehicle persists vehicle`() = runTest { ... }
```

✅ **BIEN** — Siempre triple `"""` con GIVEN/WHEN/THEN en líneas separadas:
```kotlin
@Test
@DisplayName(
    """
    GIVEN vehicles exist for a user
    WHEN getVehiclesForUser is called
    THEN returns mapped vehicles for that user only
    """
)
fun getVehiclesForUserReturnsMappedVehicles() = runTest { ... }
```

---

## Nombres de funciones de test

❌ **MAL** — Backticks:
```kotlin
fun `logEvent tracks event with correct type`() { ... }
```

❌ **MAL** — Nombre genérico o abreviado:
```kotlin
fun test1() { ... }
fun testAdd() { ... }
```

✅ **BIEN** — camelCase descriptivo que refleja el comportamiento:
```kotlin
fun addVehiclePersistsVehicle() = runTest { ... }
fun getVehicleByIdReturnsNullWhenNotFound() = runTest { ... }
fun emptyPriceStringReturnsZero() { ... }
```

---

## Nombre de la variable system under test

❌ **MAL** — Nombre genérico o mismo que el tipo:
```kotlin
private lateinit var useCase: AddVehicleUseCase
private lateinit var addVehicleUseCase: AddVehicleUseCase
```

✅ **BIEN** — Siempre `sut`:
```kotlin
private lateinit var sut: AddVehicleUseCase
```