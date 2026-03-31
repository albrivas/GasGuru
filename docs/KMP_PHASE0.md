# Phase 0: Build Infrastructure — Guía y Explicación

Este documento explica qué se ha implementado en la Phase 0 de la migración KMP, por qué cada pieza es necesaria y cómo encajan entre sí.

---

## ¿Qué es un Convention Plugin y por qué son importantes para KMP?

Los convention plugins (en `build-logic/convention/`) son plugins de Gradle reutilizables que centralizan la configuración de build. En lugar de repetir configuración en cada `build.gradle.kts` de cada módulo, un convention plugin la encapsula una vez.

Ejemplo: en lugar de que cada módulo KMP declare los targets iOS y configure el SDK de Android, el plugin `gasguru.kmp.library` lo hace automáticamente.

---

## Cambios en `libs.versions.toml`

### Ktor KMP (traído de `feature/migrate-to-ktor`)

```toml
ktorClientAndroid = "3.3.1"   # versión legacy para ktor-client-android (Supabase)
ktor = "3.4.0"                # versión KMP para los nuevos clientes KMP
```

Se mantienen dos versiones de Ktor porque:
- `ktor-client-android` lo usa Supabase internamente y necesita su propia versión compatible
- Las nuevas librerías KMP de Ktor (`ktor-client-core`, `ktor-client-okhttp`, `ktor-client-darwin`, etc.) usan la versión 3.4.0 con soporte multiplataforma completo

### Ktor KMP Libraries

```toml
ktor-client-core        # cliente base KMP (commonMain)
ktor-client-okhttp      # motor Android/JVM
ktor-client-darwin      # motor iOS (usa URLSession de Apple)
ktor-client-content-negotiation  # serialización del cuerpo HTTP
ktor-client-logging     # logs de peticiones
ktor-serialization-kotlinx-json  # serialización JSON vía kotlinx
ktor-client-mock        # cliente mock para tests
```

En KMP, el cliente HTTP se configura con un **engine** específico por plataforma:
- Android → OkHttp
- iOS → Darwin (URLSession nativo de Apple)
- Tests → Mock

### kotlinx-datetime y kotlinx-coroutines-core

```toml
kotlinxDatetime = "0.6.2"
kotlinx-coroutines-core   # versión KMP de coroutines (para commonMain)
```

- `kotlinx-datetime` reemplaza `java.time` en código común. `java.time` solo existe en la JVM; en iOS no hay JVM, por lo que `java.util.Date` o `java.time.LocalDate` no compilan en `commonMain`.
- `kotlinx-coroutines-core` es la versión KMP de coroutines. El módulo `kotlinx-coroutines-android` es solo para Android; el `core` funciona en todos los targets.

### Compose Multiplatform (CMP)

```toml
composeMultiplatform = "1.10.0"
compose-multiplatform-runtime
compose-multiplatform-foundation
compose-multiplatform-material3
compose-multiplatform-ui
compose-multiplatform-components-resources
compose-multiplatform-gradlePlugin  # plugin de Gradle para habilitar CMP
```

CMP usa el mismo modelo de renderizado (Skiko, basado en Skia) en todas las plataformas. Las librerías de `org.jetbrains.compose.*` son la versión multiplataforma de `androidx.compose.*`. Las de AndroidX siguen siendo necesarias para el target Android.

### Nuevos Plugin IDs

```toml
gasguru-kmp-library          # id = "gasguru.kmp.library"
gasguru-kmp-compose-library  # id = "gasguru.kmp.library.compose"
gasguru-kmp-room             # id = "gasguru.kmp.room"
```

Los IDs en el catálogo son la "dirección" que usan los módulos para aplicar los plugins:
```kotlin
// En un módulo que migre a KMP:
alias(libs.plugins.gasguru.kmp.library)
```

---

## Convention Plugins creados

### `KmpLibraryConventionPlugin` (`gasguru.kmp.library`)

**Qué hace:**
```kotlin
apply("com.android.kotlin.multiplatform.library")  // plugin unificado KMP+Android
apply("gasguru.jacoco")

android { configureKotlinAndroid(this) }  // compileSdk, minSdk, Java 17

kotlin {
    iosX64()           // simulador Intel
    iosArm64()         // dispositivo físico
    iosSimulatorArm64() // simulador Apple Silicon
}
```

**Por qué `com.android.kotlin.multiplatform.library`:**
Antes se combinaban dos plugins separados (`org.jetbrains.kotlin.multiplatform` + `com.android.library`). Desde AGP 9.0 esa combinación deja de funcionar. El nuevo plugin unificado gestiona ambas responsabilidades y elimina la fricción entre los dos sistemas de build.

**Por qué tres targets iOS:**
- `iosX64` — simulador de Mac Intel
- `iosArm64` — dispositivo físico (iPhone, iPad)
- `iosSimulatorArm64` — simulador de Mac Apple Silicon (M1/M2/M3)

Son arquitecturas de CPU distintas. Gradle/Kotlin generará binarios específicos para cada una.

**Qué NO incluye:**
El plugin base no añade dependencias. Cada módulo declara las suyas. El propósito del plugin es solo establecer la estructura de targets y configuración base.

---

### `KmpComposeLibraryConventionPlugin` (`gasguru.kmp.library.compose`)

**Qué hace:**
```kotlin
apply("gasguru.kmp.library")           // hereda toda la infraestructura KMP
apply("org.jetbrains.compose")         // habilita CMP
apply("org.jetbrains.kotlin.plugin.compose")  // compilador de Compose
apply("org.jetbrains.kotlin.plugin.serialization")

android { buildFeatures.compose = true }  // necesario para el target Android

kotlin {
    sourceSets["commonMain"].dependencies {
        // librerías CMP: funcionan en todos los targets (Android + iOS)
        compose.runtime, foundation, material3, ui, resources
        kotlinx.serialization.json
    }
    sourceSets["androidMain"].dependencies {
        // librerías solo para Android que no tienen equivalente CMP
        koin-androidx-compose, lifecycle-viewmodel-compose
        navigation-compose, coil-compose
    }
}
```

**¿Por qué `commonMain` tiene las librerías CMP y no las de AndroidX?**

Las librerías `org.jetbrains.compose.*` están implementadas para cada plataforma internamente:
- En Android usan el compositor de Jetpack Compose
- En iOS usan Skiko (motor de renderizado Skia portado a Kotlin/Native)

Las librerías `androidx.compose.*` son solo Android. Por eso van en `androidMain`.

**¿Y en iOS qué se renderiza?**
Skiko. No necesitas añadir nada extra para iOS en el convention plugin — las librerías CMP en `commonMain` ya incluyen la implementación para iOS.

---

### `KmpRoomConventionPlugin` (`gasguru.kmp.room`)

**Qué hace:**
```kotlin
apply("androidx.room")      // Room KMP (oficial desde 2.7.0)
apply("com.google.devtools.ksp")  // KSP para generar código Room

ksp { arg("room.generateKotlin", "true") }
room { schemaDirectory("$projectDir/schemas") }

kotlin {
    sourceSets["commonMain"].dependencies {
        androidx.room.runtime  // API de Room en código común
    }
}

// El compilador de Room necesita ejecutarse para cada target
dependencies {
    kspAndroid(room.compiler)
    kspIosX64(room.compiler)
    kspIosArm64(room.compiler)
    kspIosSimulatorArm64(room.compiler)
}
```

**¿Por qué `kspAndroid`, `kspIosX64`, etc. en lugar de `ksp`?**

En KMP, KSP (el procesador de anotaciones de Kotlin) genera código específico por plataforma. Room necesita generar las implementaciones de los DAOs para cada arquitectura por separado. Si solo usáramos `ksp`, solo procesaría el target por defecto.

**Relación con `gasguru.kmp.library`:**
Este plugin NO aplica `gasguru.kmp.library` internamente. Se diseña para usarse junto a él:
```kotlin
// build.gradle.kts de :core:database cuando llegue Phase 3
alias(libs.plugins.gasguru.kmp.library)
alias(libs.plugins.gasguru.kmp.room)
```

---

### `KoinConventionPlugin` actualizado (`gasguru.koin`)

**Antes (solo Android):**
```kotlin
pluginManager.withPlugin("com.android.base") {
    dependencies { add("implementation", koin-android) }
}
```

**Ahora (Android + KMP):**
```kotlin
// Para módulos KMP: koin-core en commonMain, koin-android en androidMain
pluginManager.withPlugin("org.jetbrains.kotlin.multiplatform") {
    kotlin {
        sourceSets["commonMain"].dependencies { koin-core }
        sourceSets["androidMain"].dependencies { koin-android }
    }
}
// Para módulos Android puros: comportamiento original
pluginManager.withPlugin("com.android.base") {
    if (!hasPlugin("kotlin.multiplatform")) {
        dependencies { add("implementation", koin-android) }
    }
}
```

**¿Por qué `koin-core` en commonMain y `koin-android` en androidMain?**

- `koin-core` contiene la API multiplataforma de Koin: `startKoin`, `module { }`, `inject()`, etc. Funciona en todos los targets.
- `koin-android` contiene las extensiones específicas de Android: `androidContext()`, `viewModel { }`, integración con `Activity`/`Fragment`. No tiene sentido en iOS.

En iOS, Koin se inicializa con la API de `koin-core` sin las extensiones Android.

---

## Estructura de Source Sets en KMP

Cuando un módulo aplica `gasguru.kmp.library`, Gradle crea automáticamente esta estructura de directorios:

```
src/
├── commonMain/kotlin/     ← código compartido (todos los targets)
├── commonTest/kotlin/     ← tests compartidos (kotlin.test)
├── androidMain/kotlin/    ← código específico Android
├── androidUnitTest/kotlin/
├── iosMain/kotlin/        ← código específico iOS
└── iosTest/kotlin/
```

**Regla general:**
- Todo lo que compile sin importar `android.*` o `java.*` → `commonMain`
- Lo que use APIs de Android (Context, Intent, etc.) → `androidMain`
- Lo que use APIs de iOS (UIKit, CoreLocation, etc.) → `iosMain`

---

## Cambios en `build-logic/convention/build.gradle.kts`

```kotlin
dependencies {
    compileOnly(libs.compose.multiplatform.gradlePlugin)  // nuevo
}

gradlePlugin {
    plugins {
        register("kmpLibrary") { ... }
        register("kmpComposeLibrary") { ... }
        register("kmpRoom") { ... }
    }
}
```

El `compose.multiplatform.gradlePlugin` se añade como `compileOnly` porque el convention plugin necesita conocer los tipos del plugin CMP en tiempo de compilación (para aplicarlo a otros proyectos), pero en runtime Gradle lo resuelve por su cuenta desde `pluginManagement`.

---

## Resumen: qué queda listo tras Phase 0

| Componente | Estado | Usado en Phase |
|-----------|--------|---------------|
| `gasguru.kmp.library` | ✅ Listo | 1, 2, 3, 4 |
| `gasguru.kmp.library.compose` | ✅ Listo | 5, 6, 7 |
| `gasguru.kmp.room` | ✅ Listo | 3 |
| `gasguru.koin` KMP-aware | ✅ Listo | todos |
| Ktor KMP libs en catálogo | ✅ Listo | `:core:network` Phase 4 |
| kotlinx-datetime en catálogo | ✅ Listo | 1, 2 |
| CMP libs en catálogo | ✅ Listo | 5, 6, 7 |

Los módulos existentes no se ven afectados — ninguno usa aún los nuevos plugins. Phase 0 es infraestructura pura.