# Casos esquina — DO / DON'T

## El happy path es el mínimo, no el objetivo

❌ **MAL** — Solo testear el caso feliz:
```kotlin
@Test
@DisplayName("GIVEN a price string WHEN toSafeDouble is called THEN returns correct value")
fun priceStringConvertsToDouble() {
    assertEquals(1.459, "1,459".toSafeDouble(), 0.001)
}
// Solo 1 test → no sirve de nada si luego falla con string vacío en producción
```

✅ **BIEN** — Cubrir todas las ramas del código:
```kotlin
@Test
@DisplayName("GIVEN a price string with comma WHEN toSafeDouble is called THEN returns correct double")
fun priceWithCommaConverts() { assertEquals(1.459, "1,459".toSafeDouble(), 0.001) }

@Test
@DisplayName("GIVEN an empty price string WHEN toSafeDouble is called THEN returns zero")
fun emptyPriceReturnsZero() { assertEquals(0.0, "".toSafeDouble(), 0.001) }

@Test
@DisplayName("GIVEN a non-numeric price string WHEN toSafeDouble is called THEN returns zero")
fun malformedPriceReturnsZero() { assertEquals(0.0, "N/D".toSafeDouble(), 0.001) }
```

---

## Técnica: recorrer el código buscando ramas

Antes de escribir un test, leer el código fuente y buscar:

### Cada `if` / `when` / `else`
```kotlin
// Código:
fun getPriceCategory(price: Double, avg: Double): PriceCategory =
    when {
        price < avg * 0.95 -> PriceCategory.CHEAP
        price > avg * 1.05 -> PriceCategory.EXPENSIVE
        else               -> PriceCategory.NORMAL
    }

// Tests necesarios — uno por rama + límites exactos:
// @DisplayName "GIVEN price below 95% of average WHEN ... THEN category is CHEAP"
// @DisplayName "GIVEN price above 105% of average WHEN ... THEN category is EXPENSIVE"
// @DisplayName "GIVEN price equal to average WHEN ... THEN category is NORMAL"
// @DisplayName "GIVEN price at exactly 95% of average WHEN ... THEN category is NORMAL"  ← límite
// @DisplayName "GIVEN price at exactly 105% of average WHEN ... THEN category is NORMAL" ← límite
```

### Cada `?.` / `?:` / `!!`
```kotlin
// Código:
fun isStationOpen(schedule: String?): Boolean {
    if (schedule == null) return false
    ...
}
// Tests: schedule null → false / schedule válido → proceso normal
```

### Cada `catch` / `try`
```kotlin
// Código:
override suspend fun sync() {
    try { repository.fetchAndStore() }
    catch (e: Exception) { analyticsHelper.logEvent(syncFailed) }
}
// Tests: sync ok → no loguea / sync lanza excepción → loguea evento concreto
```

### Cada lista que puede ser vacía
```kotlin
// Tests: lista vacía / un elemento / múltiples elementos / elementos duplicados
```

### Cada valor numérico con comparación (`>`, `<`, `>=`, `<=`)
```kotlin
// Código: if (query.length < 3) return EmptyQuery
// Tests: length=0, length=2 (< 3), length=3 (límite exacto), length=10
```

---

## Casos esquina por dominio del proyecto

### Precios de gasolineras
- String vacío o `"N/D"` → 0.0, no crash
- String con coma (`"1,459"`) vs punto (`"1.459"`)
- Precio 0 en cálculos de categoría

### Horarios de gasolineras
- Horario null o vacío → cerrado, no crash
- Hora justo en el límite de apertura/cierre
- Horario nocturno que cruza medianoche (`22:00-06:00`)
- Formato de horario inesperado → no lanza excepción

### Vehículos
- Vehículo con nombre null
- Eliminar el vehículo principal
- Usuario sin vehículos

### Flujos reactivos
- Flow que emite error → estado de error en la UI
- StateFlow que recibe el mismo valor dos veces → no re-emite

### Repositorios / DAOs
- Upsert de elemento que ya existe → actualiza, no duplica
- Consulta de ID que no existe → null, no crash
- Delete de ID que no existe → silencioso, no crash