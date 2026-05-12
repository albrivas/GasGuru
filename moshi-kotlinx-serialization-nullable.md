---
layout: ../../layouts/post.astro
title: "Moshi vs kotlinx.serialization: campos nullable sin valor por defecto"
description: "Al migrar de Moshi a kotlinx.serialization, un campo nullable sin `= null` lanza excepción si la clave no está en el JSON. La causa y el fix en dos líneas."
keywords: "kotlinx.serialization, Moshi, nullable, valor por defecto, MissingFieldException, KMP, Kotlin, serialización, JSON"
tags: [Kotlin, KMP]
dateFormatted: May 1, 2026
---

Al migrar modelos de red de Moshi a kotlinx.serialization, hay una diferencia de comportamiento que no es obvia y que genera errores en runtime.

---

## El problema

En Moshi, un campo nullable sin valor por defecto funciona sin problemas aunque la clave no esté presente en el JSON:

```kotlin
// Moshi: si "duration" no viene en el JSON, duration = null
val duration: NetworkDuration?
```

En kotlinx.serialization, ese mismo campo sigue siendo **requerido**. Aunque el tipo sea nullable, si la clave no está en el JSON se lanza una excepción:

```text
kotlinx.serialization.MissingFieldException: Field 'duration' is required for type with serial name '...', but it was missing
```

La razón es que kotlinx.serialization distingue entre "campo nullable" y "campo opcional". Un campo es opcional solo si tiene un valor por defecto explícito. El tipo nullable solo indica qué valores acepta, no que el campo pueda omitirse en el JSON.

---

## La solución

Añadir `= null` como valor por defecto:

```kotlin
// Moshi: esto funciona aunque el campo no venga en el JSON
val duration: NetworkDuration?

// kotlinx.serialization: necesita el default explícito
val duration: NetworkDuration? = null
```

Con `= null`, el campo pasa a ser opcional: si la clave está en el JSON se deserializa, y si no está se asigna `null` sin lanzar excepción.

---

## ⚠️ Lo que hay que revisar al migrar

Al migrar una clase de Moshi a kotlinx.serialization, todos los campos `val campo: Tipo?` sin valor por defecto deben convertirse a `val campo: Tipo? = null`. En Moshi el comportamiento era implícito; en kotlinx.serialization hay que ser explícito.
