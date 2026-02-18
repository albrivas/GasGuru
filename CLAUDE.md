# CLAUDE.md — GasGuru

## Módulos y reglas
- Permitido: `feature → core`, `app → features/core`
- Prohibido: `feature ↔ feature`, `UI ↔ data sources directos`
- Toda la navegacion está centralizado en el modulo navigation
- Navegación: pasar IDs y cargar datos en ViewModel vía UseCases

## Compose & Estado
- Usar patron de diseño MVI
- VM expone: `UiState` sellada + `events`
- Usar `collectAsStateWithLifecycle`
- Componentes deben ser `@Stable` cuando aplique

## Theming
- Usar siempre `GasGuruColors` para Light y Dark
- Prohibido hardcodear colores (usar solo desde el tema)
- Mantener coherencia visual entre modos claro y oscuro

## Code
- Añadir nombre de los argumentos. Ejemplo: `getLocation(location = loc)
- Eliminar imports sin usar
- Evitar `!!`
- Usar trailling comma siempre que se pueda

## Documentacion
- Al terminar cualquier tarea o funcionalidad, revisar si hay documentacion existente que deba actualizarse
- Consultar la tabla de documentacion al final de este archivo para identificar los docs afectados
- Si se modifica un archivo que tiene documentacion asociada, actualizar el doc correspondiente en el mismo paso

## Tests
- Si se modifica una clase que tiene tests asociados, actualizar los tests para reflejar los cambios en el mismo paso

## Dependencies
- El proyecto usa conventional plugin de gradle para el manejo centralizado de dependencias
- Siempre que añadas dependencias deben ir en el libs.versions.toml y luego referenciarlas en los build.gradle que necesiten esas depdencias 
- Lo mismo para los plugins. Todas las dependencias se centralizan en lisb.versions.toml


## Documentación

| Tema | Descripción |
|------|-------------|
| [Adding Fuel Types](docs/ADDING_FUEL_TYPE.md) | Guía para añadir nuevos tipos de combustible |
| [CI/CD](docs/CICD.md) | Integración y despliegue continuo |
| [GitFlow](docs/GITFLOW.md) | Estrategia de branching y flujo de trabajo |
| [JaCoCo](docs/JACOCO.md) | Reportes de cobertura de código |
| [Navigation](docs/NAVIGATION.md) | Arquitectura de navegación |
| [Obfuscation](docs/OBFUSCATION.md) | Configuración de ofuscación de código |
| [Price Alerts](docs/PRICE_ALERTS.md) | Funcionalidad de alertas de precio |
| [Recomposition Optimizations](docs/RECOMPOSITION_OPTIMIZATIONS.md) | Optimizaciones de recomposición en Compose |
| [Testing](docs/TESTING.md) | Estrategia y guías de testing |
| [UI Mappers](docs/UI_MAPPERS.md) | Arquitectura de mappers UI |
| [Network KMP](docs/NETWORK_KMP.md) | Migración de core/network a KMP: estructura, decisiones y conceptos clave |