---
description: Reglas de detekt/codeCheck y cobertura de source sets en módulos KMP
paths:
  - "build.gradle.kts"
  - "**/build.gradle.kts"
---

# Code Quality / Detekt

## `setSource` debe cubrir TODOS los source sets activos

El task `codeCheck` (detekt) está definido en `build.gradle.kts` raíz dentro de `allprojects { tasks.register<Detekt>("codeCheck") { setSource(...) } }`. La lista de paths que se pasa a `setSource(...)` debe incluir cada source set activo del proyecto:

- Android puro: `src/main/java`, `src/main/kotlin`, `src/test/java`, `src/test/kotlin`
- KMP: `src/commonMain/kotlin`, `src/commonTest/kotlin`, `src/androidMain/kotlin`, `src/androidUnitTest/kotlin`, `src/iosMain/kotlin`
- Si se añade un nuevo target (`desktopMain`, `wasmJsMain`, etc.), añadir su path aquí.

**Detekt ignora silenciosamente rutas no listadas** — no falla, no avisa, simplemente no las analiza. Sin esto, los archivos en `commonMain` de módulos KMP pasan inadvertidos por la cadena de calidad.

## Al migrar un módulo a KMP

Verificar que `codeCheck` sigue cubriendo los archivos al moverlos de `src/main/...` a `src/commonMain/...`. Si los paths KMP no están en `setSource`, la migración rompe la cobertura de linting de ese módulo en silencio.

## Antes de mergear cambios en `setSource`

Ejecutar `./gradlew codeCheck` global para detectar deuda preexistente que estuviera durmiente. Ampliar el `setSource` típicamente desentierra issues que el equipo no sabía que existían — abordarlos en una PR propia separada de la que amplía el scope.

## Histórico

Esta regla viene del incidente en `bugfix/codecheck` (mayo 2026): `setSource` solo incluía rutas Android puras (`src/main/java`, `src/test/kotlin`...), por lo que tras la migración KMP de varios módulos (`core:data`, `core:database`, `core:network`, `core:uikit`, `core:ui`, `core:testing`, `feature:station-map`) detekt dejó de analizar `commonMain/` sin que nadie lo notara. Se acumularon ~150 issues invisibles durante meses. Al ampliar `setSource` con los paths KMP, todos salieron a la luz a la vez.