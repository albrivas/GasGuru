# CLAUDE.md — GasGuru

## Módulos y reglas
- Permitido: `feature → core`, `app → features/core`
- Prohibido: `feature ↔ feature`, `UI ↔ data sources directos`
- Navegación: pasar IDs y cargar datos en ViewModel vía UseCases

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