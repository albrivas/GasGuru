# UI Mappers

Transformaciones de modelos domain a modelos UI siguiendo Clean Architecture.

## Ubicación

```
core/ui/src/main/java/com/gasguru/core/ui/mapper/
```

## Convenciones de Naming

| Tipo | Patrón | Ubicación |
|------|--------|-----------|
| Modelo UI | `<Entity>UiModel.kt` | `core/ui/models/` |
| Mapper | `<Entity>UiMapper.kt` | `core/ui/mapper/` |

### Funciones

```kotlin
// Domain → UI
fun DomainModel.toUiModel(): UiModel

// UI → Domain (cuando sea necesario)
fun UiModel.toDomain(): DomainModel
```

## Uso

```kotlin
import com.gasguru.core.ui.mapper.toUiModel

val uiModel = domainModel.toUiModel()
```

## Crear Nuevo Mapper

### 1. Crear archivo mapper

```kotlin
package com.gasguru.core.ui.mapper

import com.gasguru.core.model.data.YourDomain
import com.gasguru.core.ui.models.YourUiModel

/**
 * Maps [YourDomain] to [YourUiModel].
 */
fun YourDomain.toUiModel(): YourUiModel = YourUiModel(
    // mapping logic
)
```

### 2. Modelo solo contiene data class

```kotlin
package com.gasguru.core.ui.models

data class YourUiModel(
    val property: String,
)
```

## Utilities vs Mappers

| Tipo | Ubicación | Ejemplos |
|------|-----------|----------|
| **NO son mappers** | Archivos separados | • Helper functions (`FuelExtension.kt`) • Conversiones de primitivos • Funciones de utilidad genéricas |
| **SÍ son mappers** | `mapper/` | • Domain → UI transformations • UI → Domain transformations • Lógica de formateo/cálculo |