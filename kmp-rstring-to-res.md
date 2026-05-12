---
layout: ../../layouts/post.astro
title: "KMP strings: de R.string a Res con Compose Resources"
description: "Por qué R.string deja de funcionar al migrar a Kotlin Multiplatform y cómo reemplazarlo con Res, StringResource y stringResource de CMP"
keywords: "Res.string, StringResource, composeResources, stringResource CMP, R.string KMP, Compose Resources, Kotlin Multiplatform, Compose Multiplatform, commonMain, DrawableResource, runBlocking getString deadlock"
tags: [Android, KMP, Kotlin]
dateFormatted: Apr 29, 2026
---

Al migrar un módulo Compose a Kotlin Multiplatform, los primeros errores de compilación suelen ser de recursos. `R.string`, `R.drawable` y `@StringRes Int` son conceptos vinculados al sistema de build de Android — en `commonMain` no existen. El compilador lo dice claramente: `Unresolved reference: R`.

La alternativa oficial es **Compose Resources**, la capa de recursos multiplataforma de Compose Multiplatform. El cambio no es solo renombrar clases: afecta a cómo se estructuran los ficheros, cómo se accede a los recursos en runtime y cómo se tipan los modelos de los componentes.

---

## El problema: `R` no existe en `commonMain`

En un módulo Android puro, los recursos viven en `src/main/res/` y el compilador de Android genera la clase `R` con identificadores enteros para cada recurso. Ese mecanismo es exclusivo de Android.

Al mover código a `commonMain`, cualquier referencia a `R.string`, `R.drawable` o `@DrawableRes Int` provoca un error de compilación porque `commonMain` no conoce nada del sistema de recursos de Android.

```text
e: Unresolved reference: R
e: Cannot find a parameter with this name: R.string.filter_title
```

---

## La solución: `composeResources` y la clase `Res`

Compose Resources define una convención nueva: los recursos se colocan en `src/commonMain/composeResources/` siguiendo la misma estructura que `res/` en Android:

```
src/commonMain/composeResources/
├── drawable/
│   └── ic_fuel_station.xml
├── font/
│   ├── inter_bold.ttf
│   └── inter_medium.ttf
└── values/
    ├── strings.xml          (inglés, obligatorio)
    └── values-es/
        └── strings.xml      (español)
```

El plugin de Compose Resources genera automáticamente una clase `Res` (en el paquete configurado en `build.gradle.kts`) con propiedades tipadas para cada recurso:

```kotlin
// Antes (Android)
R.drawable.ic_fuel_station
R.string.filter_title

// Después (CMP)
Res.drawable.ic_fuel_station
Res.string.filter_title
```

Para strings en composables, `androidx.compose.ui.res.stringResource` no existe en `commonMain`. Se reemplaza por `org.jetbrains.compose.resources.stringResource`:

```kotlin
// Antes
import androidx.compose.ui.res.stringResource
Text(text = stringResource(R.string.filter_title))

// Después
import org.jetbrains.compose.resources.stringResource
Text(text = stringResource(Res.string.filter_title))
```

---

## Modelos de componentes: de `@StringRes Int` a `StringResource`

El cambio más importante no es la llamada en el composable — es el tipo en los modelos de los componentes. En Android, los modelos usaban `@DrawableRes Int` y `@StringRes Int` como convención para llevar recursos sin resolverlos:

```kotlin
// Antes (Android)
data class FuelStationItemModel(
    @DrawableRes val icon: Int,
    @StringRes val label: Int,
)
```

En CMP, los tipos pasan a ser `DrawableResource` y `StringResource`:

```kotlin
// Después (CMP)
data class FuelStationItemModel(
    val icon: DrawableResource,
    val label: StringResource,
)
```

Estos tipos son de `org.jetbrains.compose.resources` y son serializables y seguros de pasar entre capas. Se resuelven a valor final solo en el composable con `stringResource(model.label)` o `painterResource(model.icon)`.

---

## La trampa de `runBlocking { getString() }` en ViewModels

Durante la migración apareció un patrón que parece razonable pero rompe los tests: resolver el string en el ViewModel con `runBlocking`:

```kotlin
// ⚠️ Patrón problemático
val fuelTypeLabel = runBlocking { getString(Res.string.fuel_type_gasoline) }
```

El problema es doble. En producción, `runBlocking` bloquea el hilo en el que se llama — en un ViewModel sobre el dispatcher de tests esto causa un deadlock silencioso. En tests unitarios, `Res` no está inicializado (no hay entorno de Compose), por lo que la llamada lanza una excepción.

La solución correcta es **no resolver strings en el ViewModel**. El ViewModel pasa `StringResource` al estado de UI y el composable lo resuelve:

```kotlin
// ViewModel
data class FuelTypeUiState(
    val label: StringResource = Res.string.fuel_type_gasoline,
)

// Composable
Text(text = stringResource(uiState.label))
```

Esto elimina la necesidad de `Context` en el ViewModel, simplifica el módulo Koin y hace los tests unitarios triviales — no necesitan un entorno de Compose para verificar el estado.

---

## Strings que no pueden migrar: el caso de `carContext`

Hay un caso donde `composeResources` no es aplicable: código que usa `carContext.getString()` en módulos de Android Auto. `carContext` espera un `@StringRes Int` de Android, no un `StringResource` de CMP.

La solución es mantener un fichero `androidMain/res/values/strings.xml` mínimo solo con las strings que necesita ese módulo, y migrar todo lo demás a `composeResources`. Los dos sistemas coexisten sin conflicto siempre que no haya entradas duplicadas.

---

## Resumen

| Concepto | Android | CMP (commonMain) |
|---|---|---|
| Acceso a strings en Compose | `stringResource(R.string.xxx)` | `stringResource(Res.string.xxx)` |
| Acceso a drawables | `R.drawable.xxx` | `Res.drawable.xxx` |
| Tipo drawable en modelos | `@DrawableRes Int` | `DrawableResource` |
| Tipo string en modelos | `@StringRes Int` | `StringResource` |
| Localización | `res/values-es/strings.xml` | `composeResources/values-es/strings.xml` |
| Resolución en ViewModel | `runBlocking { getString(res) }` | No resolver — pasar `StringResource` al composable |
