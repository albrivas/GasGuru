# CLAUDE.md — GasGuru

## Módulos y reglas
- Permitido: `feature → core`, `app → features/core`
- Prohibido: `feature ↔ feature`, `UI ↔ data sources directos`
- Navegación: pasar IDs y cargar datos en ViewModel vía UseCases
- Cuando una pantalla recibe un ID por navegación, leerlo en el ViewModel con `savedStateHandle.toRoute<MyRoute>().myParam` en el bloque `init {}`. La ruta debe ser una `@Serializable data class`. Inyectar `SavedStateHandle` vía Koin con `get()`. En tests, pasar `SavedStateHandle(mapOf("myParam" to value))`. Añadir `unitTests.isReturnDefaultValues = true` en el `testOptions` del módulo para evitar errores de métodos de Android no mockeados.

## Compose & Estado
- VM expone: `UiState` sellada + `events`
- Usar `collectAsStateWithLifecycle`, evitar `!!`
- Componentes deben ser `@Stable` cuando aplique
- El modelo de cada componente va siempre en un archivo separado (`NombreModel.kt`) en el mismo paquete que el composable

## Theming
- Usar siempre `GasGuruColors` para Light y Dark
- Prohibido hardcodear colores (usar solo desde el tema)
- Mantener coherencia visual entre modos claro y oscuro

## Code
- Añadir nombre de los argumentos. Ejemplo: `getLocation(location = loc)
- Eliminar imports sin usar
- **Nombres de variables descriptivos**: evitar abreviaciones de una sola letra o nombres genéricos (ej: `v`, `it`, `x`). Usar nombres que expresen el dominio claramente (ej: `currentVehicle`, `selectedFuelType`, `tankCapacityLitres`)

## Tests
- Toda clase nueva o funcionalidad nueva debe tener tests en el mismo paso en que se crea
- Si se modifica una clase que tiene tests asociados, actualizar los tests para reflejar los cambios en el mismo paso
- Cada feature nueva debe alcanzar un mínimo del **65% de cobertura** de código (líneas + ramas) antes de hacer merge
- **Usar siempre JUnit5** (`org.junit.jupiter.api`): `@Test`, `@BeforeEach`, `@AfterEach`, `Assertions.*`. Nunca JUnit4 (`org.junit.Test`, `org.junit.Before`).
- **Toda clase de test debe llevar `@DisplayName`** en la clase y en cada método `@Test`


- Siempre que añadas dependencias deben ir en el libs.versions.toml y luego referenciarlas en los build.gradle que necesiten esas depdencias. Lo mismo para los plugins. Todas las dependencias se centralizan en lisb.versions.toml
- Usar trailling comma siempre que se pueda

## Documentacion
- Al terminar cualquier tarea o funcionalidad, revisar si hay documentacion existente que deba actualizarse
- Consultar la tabla de documentacion al final de este archivo para identificar los docs afectados
- Si se modifica un archivo que tiene documentacion asociada, actualizar el doc correspondiente en el mismo paso

| Tema | Descripción |
|------|-------------|
| [Adding Fuel Types](docs/ADDING_FUEL_TYPE.md) | Guía para añadir nuevos tipos de combustible |
| [Database Migrations](docs/DATABASE_MIGRATIONS.md) | Historial de migraciones de la DB: qué cambió y por qué en cada versión |
| [CI/CD](docs/CICD.md) | Integración y despliegue continuo |
| [Dependency Injection](docs/DEPENDENCY_INJECTION.md) | Arquitectura DI con Koin, equivalencias con Hilt, guía para añadir dependencias |
| [GitFlow](docs/GITFLOW.md) | Estrategia de branching y flujo de trabajo |
| [JaCoCo](docs/JACOCO.md) | Reportes de cobertura de código |
| [Navigation](docs/NAVIGATION.md) | Arquitectura de navegación |
| [Obfuscation](docs/OBFUSCATION.md) | Configuración de ofuscación de código |
| [Price Alerts](docs/PRICE_ALERTS.md) | Funcionalidad de alertas de precio |
| [Recomposition Optimizations](docs/RECOMPOSITION_OPTIMIZATIONS.md) | Optimizaciones de recomposición en Compose |
| [Room KMP](docs/ROOM_KMP.md) | Guía de migración de core/database a Room KMP |
| [Testing](docs/TESTING.md) | Estrategia y guías de testing |
| [UI Mappers](docs/UI_MAPPERS.md) | Arquitectura de mappers UI |
| [KMP Migration Plan](docs/KMP_MIGRATION.md) | Plan de migración a Kotlin Multiplatform: fases, checklist y estrategia de testing |
| [KMP Phase 0](docs/KMP_PHASE0.md) | Explicación detallada de la infraestructura de build creada en Phase 0: convention plugins, source sets, dependencias |
| [KMP Phase 1](docs/KMP_PHASE1.md) | Migración de :core:model a commonMain: cambios de plugin, sustituciones de APIs JVM, tests en commonTest |
| [Widget](docs/WIDGET.md) | Widget de pantalla de inicio con gasolineras favoritas: arquitectura Glance + WorkManager, ciclo de vida y limitaciones |
| [Analytics](docs/ANALYTICS.md) | Sistema de analíticas con Mixpanel: arquitectura, catálogo de eventos, uso en ViewModels y Composables |
| [Git Worktree](docs/GIT_WORKTREE.md) | Cómo crear y eliminar worktrees, el problema de local.properties y cómo resolverlo con symlinks |

## Orquestación del Flujo de Trabajo

### 1. Modo Planificación por Defecto
- Entra en modo planificación para CUALQUIER tarea no trivial (más de 3 pasos o decisiones arquitectónicas)
- Si algo sale mal, PARA y vuelve a planificar de inmediato; no sigas forzando
- Usa el modo planificación para los pasos de verificación, no solo para la construcción
- Escribe especificaciones detalladas por adelantado para reducir la ambigüedad

### 2. Estrategia de Subagentes
- Usa subagentes con frecuencia para mantener limpia la ventana de contexto principal
- Delega la investigación, exploración y análisis paralelo a subagentes
- Para problemas complejos, dedica más capacidad de cómputo mediante subagentes
- Una tarea por subagente para una ejecución focalizada

### 3. Bucle de Automejora
- Tras CUALQUIER corrección del usuario: actualiza tasks/lessons.md con el patrón
- Escribe reglas para ti mismo que eviten el mismo error
- Itera implacablemente sobre estas lecciones hasta que la tasa de errores disminuya
- Revisa las lecciones al inicio de la sesión para el proyecto correspondiente

### 4. Verificación antes de Finalizar
- Nunca marques una tarea como completada sin demostrar que funciona
- Compara la diferencia (diff) de comportamiento entre la rama principal y tus cambios cuando sea relevante
- Pregúntate: "¿Aprobaría esto un ingeniero senior (Staff Engineer)?"
- Ejecuta tests, comprueba los logs y demuestra la corrección del código

### 5. Exige Elegancia (Equilibrado)
- Para cambios no triviales: haz una pausa y pregunta "¿hay una forma más elegante?"
- Si un arreglo parece un parche (hacky): "Sabiendo todo lo que sé ahora, implementa la solución elegante"
- Omite esto para arreglos simples y obvios; no hagas sobreingeniería
- Cuestiona tu propio trabajo antes de presentarlo

### 6. Corrección de Errores Autónoma
- Cuando recibas un informe de error: simplemente arréglalo. No pidas que te lleven de la mano
- Identifica logs, errores o tests que fallan y luego resuélvelos
- Cero necesidad de cambio de contexto por parte del usuario
- Ve a arreglar los tests de CI que fallan sin que te digan cómo

## Gestión de Tareas

1. *Planificar Primero*: Escribe el plan en tasks/todo.md con elementos verificables
2. *Verificar Plan*: Confirma antes de comenzar la implementación
3. *Seguir el Progreso*: Marca los elementos como completados a medida que avances
4. *Explicar Cambios*: Resumen de alto nivel en cada paso
5. *Documentar Resultados*: Añade una sección de revisión a tasks/todo.md
6. *Capturar Lecciones*: Actualiza tasks/lessons.md después de las correcciones

## Principios Fundamentales

- *Simplicidad Primero*: Haz que cada cambio sea lo más simple posible. Afecta al mínimo código necesario.
- *Sin Pereza*: Encuentra las causas raíz. Nada de arreglos temporales. Estándares de desarrollador senior.
- *Impacto Mínimo*: Los cambios solo deben tocar lo necesario. Evita introducir errores.