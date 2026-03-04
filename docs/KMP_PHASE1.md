# Phase 1: `:core:model` — Guía y Explicación

Este documento explica qué se ha implementado en la Phase 1 de la migración KMP, por qué cada cambio es necesario y cómo encajan entre sí.

---

## ¿Por qué `:core:model` es el primer módulo en migrar?

`:core:model` es el módulo hoja (leaf) del árbol de dependencias: todos los demás módulos del proyecto dependen de él, pero él no depende de nadie. Esto lo convierte en el candidato perfecto para empezar:

- **Riesgo mínimo**: son puras data classes y enums. Sin lógica de negocio, sin efectos secundarios, sin inyección de dependencias.
- **Impacto máximo**: una vez que está en `commonMain`, todos los módulos downstream que dependan de él ya pueden usar esos tipos desde código común cuando llegue su turno de migrar.
- **Solo dos dependencias JVM** que resolver: `java.util.Locale` y `System.currentTimeMillis()`.

---

## Cambios en `build.gradle.kts`

### Antes
```kotlin
plugins {
    alias(libs.plugins.gasguru.android.library)
    alias(libs.plugins.gasguru.proguard)
}

android {
    namespace = "com.gasguru.core.model"
}
```

### Después
```kotlin
plugins {
    alias(libs.plugins.gasguru.kmp.library)
}

android {
    namespace = "com.gasguru.core.model"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.datetime)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}
```

**¿Por qué se elimina `gasguru.proguard`?**

El plugin de ProGuard configura la ofuscación de código para el target Android. En una KMP library, el código de `commonMain` no se ofusca directamente — la ofuscación ocurre en el nivel de la app (`:app`), que consume los módulos como dependencias. Aplicar ProGuard a nivel de módulo KMP causaría problemas en el pipeline de build para los targets iOS.

**¿Por qué `gasguru.kmp.library` en lugar de `gasguru.android.library`?**

`gasguru.kmp.library` aplica los plugins necesarios para compilar el módulo para Android e iOS, y configura los tres targets iOS (x64, Arm64, SimulatorArm64). Ver la siguiente sección para entender qué plugins usa internamente y por qué.

---

## Corrección en `KmpLibraryConventionPlugin` (encontrada en Phase 1)

### El plan original (Phase 0)

La documentación de Phase 0 describía usar `com.android.kotlin.multiplatform.library`, el plugin unificado de Google que pretende reemplazar la combinación tradicional de dos plugins separados:

```kotlin
// Intención original
apply("com.android.kotlin.multiplatform.library")
```

La razón era que desde AGP 9.0 la combinación antigua (`org.jetbrains.kotlin.multiplatform` + `com.android.library`) dejará de funcionar. El plugin unificado de Google gestiona ambas responsabilidades.

### El problema al sincronizar

Al aplicar `gasguru.kmp.library` en `:core:model`, Gradle lanzó este error:

```
Failed to apply plugin 'gasguru.kmp.library'.
   > Extension of type 'LibraryExtension' does not exist.
     Currently registered extension types: [..., KotlinMultiplatformAndroidComponentsExtension, ...]
```

Y tras un primer intento de fix, el error cambió a:

```
Failed to apply plugin 'gasguru.kmp.library'.
   > Extension of type 'KotlinMultiplatformExtension' does not exist.
     Currently registered extension types: [..., KotlinMultiplatformAndroidComponentsExtension, ...]
```

### Por qué falla `com.android.kotlin.multiplatform.library`

El plugin `com.android.kotlin.multiplatform.library` registra **solo** `KotlinMultiplatformAndroidComponentsExtension`. No registra:

- `LibraryExtension` → que usa `configureKotlinAndroid()` para configurar `compileSdk`, `minSdk`, etc.
- `KotlinMultiplatformExtension` → que usa la estructura de targets KMP (`iosX64()`, `androidTarget()`, `sourceSets`, etc.)

Por tanto, cualquier `extensions.configure<LibraryExtension> {}` o `extensions.configure<KotlinMultiplatformExtension> {}` en el convention plugin falla en tiempo de configuración de Gradle.

Esto también habría roto `KmpComposeLibraryConventionPlugin`, que usa ambas extensiones al aplicar `gasguru.kmp.library`.

### La solución: combo tradicional para AGP 8.x

```kotlin
// KmpLibraryConventionPlugin.kt — implementación actual
apply("org.jetbrains.kotlin.multiplatform")  // registra KotlinMultiplatformExtension
apply("com.android.library")                 // registra LibraryExtension
apply("gasguru.jacoco")

extensions.configure<LibraryExtension> {
    configureKotlinAndroid(this)  // compileSdk=36, minSdk=26, Java 17
}

extensions.configure<KotlinMultiplatformExtension> {
    androidTarget()       // target Android dentro de la estructura KMP
    iosX64()
    iosArm64()
    iosSimulatorArm64()
}
```

Con esta combinación:

| Extension | Proveedor | Disponible |
|-----------|-----------|-----------|
| `KotlinMultiplatformExtension` | `org.jetbrains.kotlin.multiplatform` | ✅ |
| `LibraryExtension` | `com.android.library` | ✅ |
| `KotlinMultiplatformAndroidComponentsExtension` | `com.android.kotlin.multiplatform.library` | — (no necesaria) |

### Nota de compatibilidad futura

`androidTarget()` está marcado como `@Deprecated` en Kotlin con el aviso: "no será compatible con AGP 9.0.0+". El proyecto usa AGP 8.13.2, por lo que la solución es completamente válida. Cuando el proyecto migre a AGP 9.0+, habrá que reevaluar el combo de plugins, probablemente requiriendo que `com.android.kotlin.multiplatform.library` también exponga `KotlinMultiplatformExtension` (algo que puede cambiar en versiones futuras de AGP).

---

## Estructura de source sets

### Antes
```
core/model/src/
└── main/
    └── java/
        └── com/gasguru/core/model/data/
            ├── FuelStation.kt
            ├── UserData.kt
            └── ... (11 archivos más)
```

### Después
```
core/model/src/
├── commonMain/
│   └── kotlin/
│       └── com/gasguru/core/model/data/
│           ├── FuelStation.kt
│           ├── UserData.kt
│           └── ... (11 archivos más)
└── commonTest/
    └── kotlin/
        └── com/gasguru/core/model/data/
            └── FuelStationTest.kt
```

La diferencia clave: `src/main/java/` es el source set del plugin `com.android.library` (solo Android). `src/commonMain/kotlin/` es el source set de KMP que compilan **todos los targets** — Android e iOS.

---

## Cambios en `FuelStation.kt`

### El problema: `java.util.Locale`

El método `formatDistance()` usaba `String.format(Locale.ROOT, ...)`:

```kotlin
// Antes — solo compila en JVM:
import java.util.Locale
String.format(Locale.ROOT, "%.2f Km", kilometers)
```

`java.util.Locale` es una clase de la JVM. En iOS (Kotlin/Native) no existe la JVM, por lo que este import no compila en `commonMain`.

### La solución: extensiones de Kotlin stdlib

```kotlin
// Después — compila en todos los targets:
"%.2f Km".format(kilometers)
```

Desde Kotlin 1.9.20, `String.format()` y los métodos de formato de string son parte de la stdlib de Kotlin común (no solo de la JVM). El proyecto usa Kotlin 2.3.0, por lo que esta API está disponible sin restricciones.

**¿Y el `Locale.ROOT`?** Se usaba para forzar el punto (`.`) como separador decimal, independientemente del locale del dispositivo. La stdlib de Kotlin en commonMain usa internamente el comportamiento correcto para representaciones de formato `%.2f` — el separador siempre es `.` cuando se usa con `%f`, por lo que `Locale.ROOT` es innecesario.

---

## Cambios en `UserData.kt`

### El problema: `System.currentTimeMillis()`

```kotlin
// Antes — solo JVM:
val lastUpdate: Long = System.currentTimeMillis(),
```

`System` es una clase de Java (`java.lang.System`). En Kotlin/Native no existe.

### La solución: `kotlinx-datetime`

```kotlin
// Después — commonMain:
import kotlinx.datetime.Clock
val lastUpdate: Long = Clock.System.now().toEpochMilliseconds(),
```

`kotlinx-datetime` es la librería oficial de JetBrains para manejo de fechas/horas en KMP. `Clock.System.now()` retorna el instante actual como `Instant`, y `.toEpochMilliseconds()` lo convierte al mismo `Long` que retornaba `System.currentTimeMillis()`. El valor resultante es idéntico — solo cambia la API.

---

## Tests en `commonTest`

### ¿Por qué `kotlin.test` en lugar de JUnit5?

JUnit5 (`org.junit.jupiter`) es una librería JVM-only. En `commonTest` (que compila para todos los targets), no se puede usar JUnit. La alternativa es `kotlin.test`, el framework de testing oficial de Kotlin para KMP:

| JUnit5 | kotlin.test |
|--------|-------------|
| `@Test` (org.junit.jupiter) | `@Test` (kotlin.test) |
| `assertEquals(expected, actual)` | `assertEquals(expected, actual)` |
| `assertTrue(condition)` | `assertTrue(condition)` |
| `@DisplayName(...)` | nombre del método con backticks |

`kotlin.test` se añade como dependencia de `commonTest`:
```kotlin
commonTest.dependencies {
    implementation(kotlin("test"))
}
```

En Android, Kotlin internamente usa JUnit4 como runner para `kotlin.test`. En iOS usa el runner nativo de Kotlin/Native. El código de test es idéntico en ambos casos.

### Tests implementados: `FuelStationTest.kt`

Se cubren los tres métodos públicos de `FuelStation`:

**`formatDistance()`** — 5 casos:
- Distancia ≥ 1000m → kilómetros con 2 decimales (`"1.50 Km"`)
- Exactamente 1000m → `"1.00 Km"`
- Número entero < 1000m → metros sin decimales (`"250 m"`)
- Número decimal < 1000m → metros con 2 decimales (`"123.45 m"`)
- Distancia 0 → `"0 m"`

**`formatDirection()`** — 3 casos:
- Dirección en mayúsculas → primera letra mayúscula, resto minúsculas
- Dirección en minúsculas → primera letra mayúscula
- Cadena vacía → cadena vacía

**`formatName()`** — 3 casos:
- Nombre en mayúsculas → primera letra mayúscula, resto minúsculas
- Nombre mixto → primera letra mayúscula, resto minúsculas
- Cadena vacía → cadena vacía

---

## Resumen: qué queda listo tras Phase 1

| Componente | Estado |
|-----------|--------|
| `gasguru.kmp.library` aplicado a `:core:model` | ✅ |
| Todos los archivos en `commonMain` | ✅ |
| `java.util.Locale` eliminado | ✅ |
| `System.currentTimeMillis()` reemplazado con `kotlinx-datetime` | ✅ |
| Tests en `commonTest` con `kotlin.test` | ✅ |
| `gasguru.proguard` eliminado | ✅ |
| `:core:model` compila para Android + iOS | ✅ |
| Módulos downstream no afectados | ✅ |
