# KMP — Modelo Mental y Arquitectura de Build

Este documento explica cómo pensar en KMP a nivel de build: qué hace cada plugin, por qué Android necesita más configuración que iOS y cómo encajan todas las piezas.

---

## KMP no es "un proyecto que compila para todo"

KMP es **varios compiladores distintos apuntando al mismo código fuente**. El código de `commonMain` tiene que ser válido para todos ellos simultáneamente.

```
                    ┌─────────────────┐
                    │   commonMain/   │  ← código Kotlin puro
                    │   (tu código)   │
                    └────────┬────────┘
                             │
              ┌──────────────┼──────────────┐
              ▼              ▼              ▼
    ┌──────────────┐  ┌──────────────┐  ┌──────────────┐
    │   Kotlin/JVM │  │ Kotlin/Native│  │ Kotlin/Native│
    │  (Android)   │  │  (iOS Arm64) │  │  (iOS x64)   │
    └──────────────┘  └──────────────┘  └──────────────┘
         ↓                  ↓                  ↓
      .class             .klib / .framework  .klib / .framework
    (bytecode JVM)      (binario nativo)    (binario nativo)
```

Por eso no puedes usar `java.util.Locale` en `commonMain` — esa clase solo existe en el compilador JVM. El compilador de iOS (Kotlin/Native) no sabe qué es.

---

## Por qué Android necesita mucho más configuración que iOS

**iOS es solo un compilador nativo.** No tiene sistema de plugins propio en Gradle. Declaras los targets y ya — Gradle sabe que tiene que correr Kotlin/Native para esas arquitecturas. El runtime de iOS lo gestiona Xcode, no Gradle.

```kotlin
iosX64()              // simulador Intel
iosArm64()            // dispositivo físico
iosSimulatorArm64()   // simulador Apple Silicon
```

Eso es todo lo que necesita iOS a nivel de Gradle.

**Android es un entorno de build complejo** con su propio plugin (AGP — Android Gradle Plugin). AGP necesita saber cosas que no existen en el mundo iOS:

| Configuración | Para qué sirve |
|---|---|
| `compileSdk` | Con qué versión de las APIs de Android compilas |
| `minSdk` | Mínimo Android soportado en tiempo de ejecución |
| `namespace` | El paquete del módulo para `R.java` y `BuildConfig` |
| `buildFeatures.compose` | Si activas Compose en este módulo |
| `testInstrumentationRunner` | Cómo correr los tests en dispositivo |

---

## Qué hace cada plugin

En un módulo KMP con target Android se aplican dos plugins independientes:

```
org.jetbrains.kotlin.multiplatform   →  "oye Gradle, esto es KMP"
                                         Registra KotlinMultiplatformExtension
                                         → el bloque kotlin { } del módulo
                                         → aquí declaras targets y sourceSets

com.android.library                  →  "oye AGP, el target Android es
                                         una librería Android"
                                         Registra LibraryExtension
                                         → el bloque android { } del módulo
                                         → aquí configuras compileSdk, minSdk, etc.
```

`LibraryExtension` no es nada especial de KMP — es exactamente lo mismo que usabas en un módulo Android puro con `com.android.library`. Lo que antes escribías en `android { }` en cualquier módulo Android, eso es `LibraryExtension`.

---

## Cómo encajan en el convention plugin

```kotlin
// Lado Android — le habla a AGP
extensions.configure<LibraryExtension> {
    compileSdk = 36
    defaultConfig { minSdk = 26 }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

// Lado KMP — le habla a JetBrains
extensions.configure<KotlinMultiplatformExtension> {
    androidTarget()       // "el target Android usa la LibraryExtension de arriba"
    iosX64()
    iosArm64()
    iosSimulatorArm64()
}
```

`androidTarget()` es el puente entre los dos mundos: le dice al plugin de JetBrains que uno de los targets KMP es Android, y que su configuración vive en `LibraryExtension`.

---

## El mapa completo de un módulo KMP

```
build.gradle.kts del módulo
│
├── plugins { alias(libs.plugins.gasguru.kmp.library) }
│                          │
│              KmpLibraryConventionPlugin
│              ├── org.jetbrains.kotlin.multiplatform
│              │       └── KotlinMultiplatformExtension
│              │           ├── androidTarget()   ← puente con AGP
│              │           ├── iosX64()
│              │           ├── iosArm64()
│              │           └── iosSimulatorArm64()
│              │
│              └── com.android.library
│                      └── LibraryExtension
│                          ├── compileSdk = 36
│                          ├── minSdk = 26
│                          └── compileOptions (Java 17)
│
├── android {
│       namespace = "com.gasguru.core.model"   ← LibraryExtension
│   }
│
└── kotlin {
        sourceSets {
            commonMain.dependencies { ... }    ← KotlinMultiplatformExtension
            commonTest.dependencies { ... }
        }
    }
```

---

## Los source sets y qué va en cada uno

```
src/
├── commonMain/kotlin/       ← compila para TODOS los targets
├── commonTest/kotlin/       ← tests para TODOS los targets
├── androidMain/kotlin/      ← código específico Android (Context, Intent, etc.)
├── androidUnitTest/kotlin/  ← tests unitarios Android
├── iosMain/kotlin/          ← código específico iOS (UIKit, etc.)
└── iosTest/kotlin/
```

**Regla**: si un archivo usa cualquier import de `android.*` o `java.*`, no puede estar en `commonMain`. Va en `androidMain`.

---

## Regla de oro al configurar algo nuevo

> ¿Afecta solo a Android (SDK version, manifest, Compose, tests instrumentados)?
> → Va en `android { }` → territorio de `LibraryExtension` / AGP.

> ¿Afecta a todos los targets (dependencias comunes, structure de sourceSets)?
> → Va en `kotlin { }` → territorio de `KotlinMultiplatformExtension` / JetBrains.

> ¿Es una dependencia solo para Android dentro de KMP?
> → Va en `kotlin { sourceSets { androidMain.dependencies { } } }`

---

## Por qué no se usa `com.android.kotlin.multiplatform.library`

Google tiene un plugin unificado (`com.android.kotlin.multiplatform.library`) que pretende reemplazar el combo de dos plugins. El problema es que, tal como está en AGP 8.x, solo registra `KotlinMultiplatformAndroidComponentsExtension` — ni `LibraryExtension` ni `KotlinMultiplatformExtension`. Eso rompe todo el código del convention plugin que configure esas extensiones.

Cuando el proyecto migre a AGP 9.0+ y Google madure ese plugin, se puede reevaluar. Por ahora, el combo tradicional es lo que funciona.
