---
name: skill-gasguru-vm-testing
description: Genera y actualiza tests unitarios de ViewModel en GasGuru con fakes reales. Usar cuando el usuario pida testear, crear o añadir tests a un ViewModel, use case o cualquier clase con lógica — por ejemplo "crea el test de X", "testea Y", "añade tests a Z", "le falta test". También activar cuando se crea una clase nueva sin cobertura.
metadata:
  short-description: GasGuru VM tests with fakes
---

# GasGuru VM Testing

Usar esta skill al escribir o actualizar tests unitarios de ViewModel en GasGuru.

## Flujo de trabajo
1. Identificar las dependencias del VM (use cases, repositorios, dispatchers).
2. Preferir **use cases reales** con **repositorios/DAOs fake** de `core/testing`.
3. Si no existe un fake, crearlo en `core/testing/src/main/java/com/gasguru/core/testing/fakes/data/<capa>/`.
4. Ubicar los tests en `src/test/kotlin/...` (tests JVM) con **JUnit5** y **Turbine**.
5. Usar `CoroutinesTestExtension`; inyectar el test dispatcher en el VM si es necesario (no depender de `Dispatchers.IO/Default`).
6. Al hacer assertions sobre `StateFlow`, tolerar valores iniciales y asegurarse de que hay un cambio real de estado antes de esperar nuevas emisiones.

## Referencia
- Ver `references/TESTING.md` para convenciones, ejemplos y checklist.
