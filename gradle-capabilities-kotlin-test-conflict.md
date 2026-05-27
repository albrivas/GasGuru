---
title: "Gradle capabilities: conflicto kotlin-test-junit vs kotlin-test-junit5 en KMP"
description: "Cannot select module with conflict on capability kotlin-test-framework-impl: causa, por qué ocurre con instrumentedTestVariant KMP y cómo resolverlo con capabilitiesResolution"
keywords: "kotlin-test-framework-impl, kotlin-test-junit5, kotlin-test-junit, Gradle capabilities, capabilitiesResolution, KMP, instrumentedTestVariant, sourceSetTree, debugAndroidTestCompileClasspath, Kotlin Multiplatform, convention plugin"
tags: [KMP, Kotlin, Android]
dateFormatted: May 27, 2026
---

Al conectar `commonTest` con los instrumented tests de Android en un módulo KMP, Gradle puede lanzar un error que no da pistas obvias sobre dónde buscar:

```text
Cannot select module with conflict on capability
'org.jetbrains.kotlin:kotlin-test-framework-impl:2.x'
  also provided by [org.jetbrains.kotlin:kotlin-test-junit5:2.x]
```

El escenario concreto: un proyecto KMP con dos módulos cuyas dependencias de test son estas:

```
:core:components
├── commonTest
│   ├── kotlin("test")              ← aquí está el problema
│   └── :core:testing               ← trae kotlin-test-junit5 transitivamente
└── androidUnitTest
    ├── junit5-api
    ├── junit5-engine
    └── junit5-extensions

:core:testing
├── commonMain (api)
│   └── kotlin("test-annotations-common")
└── androidMain (api)
    ├── kotlin("test-junit5")       ← expuesto a todos los consumidores
    ├── junit5-api
    ├── junit5-extensions
    └── koin-test-junit5
```

**TL;DR**: `kotlin("test")` en `commonTest` se resuelve a `kotlin-test-junit` (JUnit4) cuando JUnit4 ya está en el classpath. Si otro módulo expone `kotlin-test-junit5` vía `api()`, ambos reclaman la misma capability de Gradle → conflicto. La solución es añadir una estrategia `capabilitiesResolution` en el convention plugin que fuerce siempre la variante JUnit5.

---

## Qué es un classpath y por qué puede tener conflictos

El **classpath** de una configuración de Gradle es el conjunto de artefactos JAR (o artefactos multiplataforma) que el compilador ve al resolver dependencias. En un proyecto Android con múltiples módulos existen decenas de configuraciones distintas: `debugCompileClasspath`, `releaseRuntimeClasspath`, `debugAndroidTestCompileClasspath`...

Cada configuración forma un grafo dirigido de dependencias. Gradle recorre ese grafo, descarga los artefactos y los combina. El problema surge cuando dos nodos del grafo aportan el mismo **componente funcional** pero con implementaciones distintas e incompatibles — ahí es donde entran las **capabilities**.

---

## Qué son las capabilities de Gradle

Una **capability** es una etiqueta que Gradle usa para declarar que un artefacto proporciona cierta funcionalidad. La notación es `grupo:nombre:versión`. Dos artefactos no pueden estar simultáneamente en el mismo classpath si ambos declaran la misma capability: sería como tener dos implementaciones contradictorias del mismo contrato.

`kotlin-test-junit` y `kotlin-test-junit5` son un ejemplo clásico: ambos implementan el bridge entre `kotlin.test` y un runner JUnit, y Kotlin los publica con la capability compartida `org.jetbrains.kotlin:kotlin-test-framework-impl`. Son mutuamente excluyentes por diseño — o usas JUnit4 o usas JUnit5.

Cuando Gradle detecta dos candidatos para la misma capability sin una resolución explícita, falla con el error `Cannot select module with conflict on capability`.

---

## Por qué ocurre en KMP con instrumentedTestVariant

El conflicto aparece al combinar tres ingredientes:

1. **`instrumentedTestVariant.sourceSetTree.set(KotlinSourceSetTree.test)`** en el módulo KMP — esto conecta `commonTest` con el source set de Android instrumented tests, de forma que los tests de `commonTest` se ejecutan también como tests instrumentados.

2. **`kotlin("test")` en `commonTest`** — en el contexto Android con instrumented tests, Gradle ve que `androidx.compose.ui.test.junit4` ya está en el classpath (depende de JUnit4) y resuelve `kotlin("test")` automáticamente a `kotlin-test-junit` (la variante JUnit4).

3. **`api(kotlin("test-junit5"))` en `androidMain` de `:core:testing`** — el módulo de testing compartido exporta `kotlin-test-junit5` transitivamente a todos sus consumidores.

El resultado es que `debugAndroidTestCompileClasspath` contiene `kotlin-test-junit` (resolución automática de `kotlin("test")`) y `kotlin-test-junit5` (transitivo desde `:core:testing`). Ambos declaran `kotlin-test-framework-impl`. Gradle no sabe cuál elegir y falla.

---

## La solución: capabilitiesResolution en el convention plugin

Gradle permite registrar una estrategia de resolución de capabilities para decidir, de forma explícita, qué artefacto debe ganar cuando hay conflicto. Al ponerla en el convention plugin base (`KmpLibraryConventionPlugin`), se aplica automáticamente a todos los módulos KMP del proyecto:

```kotlin
// build-logic/convention/src/main/java/KmpLibraryConventionPlugin.kt
configurations.all {
    resolutionStrategy.capabilitiesResolution.withCapability(
        "org.jetbrains.kotlin:kotlin-test-framework-impl",
    ) {
        val junit5Candidate = candidates.firstOrNull { candidate ->
            candidate.id.toString().contains("junit5")
        }
        if (junit5Candidate != null) {
            select(junit5Candidate)
        } else {
            selectHighestVersion()
        }
    }
}
```

`withCapability` intercepta cualquier conflicto sobre `kotlin-test-framework-impl` antes de que Gradle falle. El bloque recibe la lista de `candidates` — todos los artefactos que reclaman esa capability — y debe terminar con una llamada a `select()`. Aquí se busca el candidato cuyo ID contiene `"junit5"` y se le da prioridad. Si por algún motivo ese candidato no existe (configuración sin JUnit5), se usa `selectHighestVersion()` como fallback seguro.

El efecto es que `kotlin-test-junit` queda descartado en todas las configuraciones de todos los módulos KMP, sin tener que modificar cada `build.gradle.kts` individualmente.

---

## Lo que parece solución pero no funciona

⚠️ La primera reacción suele ser excluir `kotlin-test-junit` con `exclude`:

```kotlin
// No funciona para conflictos de capability
configurations.all {
    exclude(group = "org.jetbrains.kotlin", module = "kotlin-test-junit")
}
```

`exclude` elimina un artefacto del grafo de dependencias, pero el conflicto de capability ocurre **antes** de que Gradle resuelva qué artefactos incluir o excluir — el motor de capabilities actúa en una fase anterior. La exclusión puede parecer que funciona en algunos casos, pero no resuelve el conflicto declarado y en versiones recientes de Gradle simplemente falla o produce comportamientos inconsistentes.

La solución correcta siempre pasa por `capabilitiesResolution`.

---

## Resumen

| Artefacto | Capability | Resolución |
|---|---|---|
| `kotlin-test-junit` | `kotlin-test-framework-impl` | ❌ descartado |
| `kotlin-test-junit5` | `kotlin-test-framework-impl` | ✅ seleccionado |

La regla es directa: en proyectos que usan `instrumentedTestVariant.sourceSetTree` y mezclan JUnit4 (via `compose.ui.test.junit4`) con JUnit5 (via `core:testing`), añadir la estrategia `capabilitiesResolution` en el convention plugin base es la única solución robusta y escalable.
