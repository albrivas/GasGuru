# KMP Phase 2: `:core:common` — Utilidades Compartidas

## Objetivo

Separar el código multiplataforma de `:core:common` del código Android-only, habilitando compilación
para iOS sin romper ninguna funcionalidad Android existente.

**Rama**: `claude/kmp-migration-phase-2-0MHo4`
**Módulo**: `:core:common`
**Plugin resultante**: `gasguru.kmp.library`

---

## Decisiones de diseño

### 1. Qué va a commonMain vs androidMain

| Archivo | Destino | Motivo |
|---------|---------|--------|
| `KoinQualifiers.kt` | commonMain | `object` con `const val` strings — puro Kotlin |
| `GeoUtils.kt` | commonMain | Solo `kotlin.math.*` — ningun API de plataforma |
| `CommonUtils.kt` | commonMain | Schedule parsing es lógica de dominio pura |
| `CoroutineModule.kt` | commonMain | Koin module — solo necesita expect/actual para IO |
| `AppVersion.kt` | commonMain (expect) + androidMain/iosMain (actual) | BuildConfig es Android-only; iOS usa NSBundle |
| `IoDispatcher.kt` | commonMain (expect) + androidMain/iosMain (actual) | `Dispatchers.IO` es JVM-only |
| `LocationUtils.kt` | androidMain | Usa `Context`, Google Maps Compose, `android.location.*` — 100% Android |

### 2. `Dispatchers.IO` no existe en Kotlin/Native

`Dispatchers.IO` está definido en `kotlinx-coroutines-core` **solo para JVM**. En iOS (Kotlin/Native)
no existe un pool de threads dedicado para I/O bloqueante — las operaciones de red y disco se hacen
de forma nativa asíncrona. Por eso usamos `Dispatchers.Default` en iosMain, que es el dispatcher de
propósito general disponible en todos los targets.

```kotlin
// commonMain
expect val ioDispatcher: CoroutineDispatcher

// androidMain
actual val ioDispatcher: CoroutineDispatcher = Dispatchers.IO

// iosMain
actual val ioDispatcher: CoroutineDispatcher = Dispatchers.Default
```

`Dispatchers.Default`, `Dispatchers.Main` y `Dispatchers.Main.immediate` sí son KMP-compatibles y
van directamente en `CoroutineModule.kt` de commonMain.

### 3. `java.time` → kotlinx-datetime

`CommonUtils.kt` usaba `java.time.ZonedDateTime`, `java.time.LocalTime` y `java.time.DayOfWeek`.
Todos reemplazados por sus equivalentes en `kotlinx-datetime`:

| Antes (java.time) | Después (kotlinx-datetime) |
|-------------------|---------------------------|
| `ZonedDateTime.now()` + `.dayOfWeek` + `.toLocalTime()` | `Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())` → `.dayOfWeek`, `.time` |
| `LocalTime.parse(str, formatter)` | `LocalTime(hour, minute)` parseando manualmente "HH:mm" |
| `DayOfWeek.value` (1-7) | `DayOfWeek.isoDayNumber` (1-7, mismo convenio ISO 8601) |
| `.isAfter()` / `.isBefore()` | Operadores `>` / `<` (LocalTime implementa Comparable) |
| `"...".uppercase(Locale.ROOT)` | `"...".uppercase()` (KMP stdlib) |

La lógica de `DateTimeFormatter.ofPattern("HH:mm")` se sustituye por split manual de "HH:mm",
ya que `DateTimeFormatter` es JVM-only:

```kotlin
val (hour, minute) = timeStr.split(":").map { it.toInt() }
val time = LocalTime(hour = hour, minute = minute)
```

### 4. `Math.toRadians()` → `kotlin.math.PI`

`GeoUtils.kt` usaba `Math.toRadians(x)` (JVM). Reemplazado por la fórmula equivalente con la
constante KMP de `kotlin.math`:

```kotlin
// Antes
Math.toRadians(x)

// Después
x * kotlin.math.PI / 180.0
```

4 ocurrencias en la función `distanceTo()`.

### 5. `isStationOpen()` parametrizable para tests

La función original dependía de `Clock.System.now()` internamente, haciendo imposible los tests
deterministas. Se añadió un parámetro con valor por defecto:

```kotlin
fun FuelStation.isStationOpen(
    now: LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
): Boolean
```

Los tests pasan instantes fijos (lunes mañana, sábado, domingo, etc.) para verificar cada rama
del schedule parser sin depender del reloj del sistema.

### 6. BuildConfig permanece en Android

El `build.gradle.kts` mantiene el bloque `android { buildConfigField(...) }` que genera los campos
de versión. La implementación `actual fun getAppVersion()` en androidMain accede a estos campos.
En iosMain, la versión se lee desde `NSBundle.mainBundle.infoDictionary`.

---

## Estructura final de source sets

```
core/common/src/
├── commonMain/kotlin/com/gasguru/core/common/
│   ├── AppVersion.kt          (expect fun getAppVersion())
│   ├── CommonUtils.kt         (isStationOpen con kotlinx-datetime)
│   ├── CoroutineModule.kt     (Koin module — usa ioDispatcher)
│   ├── GeoUtils.kt            (distanceTo con kotlin.math.PI)
│   ├── IoDispatcher.kt        (expect val ioDispatcher)
│   └── KoinQualifiers.kt      (const val strings)
├── androidMain/kotlin/com/gasguru/core/common/
│   ├── AppVersion.kt          (actual — BuildConfig)
│   ├── IoDispatcher.kt        (actual — Dispatchers.IO)
│   └── LocationUtils.kt       (Context, Google Maps, permisos)
├── iosMain/kotlin/com/gasguru/core/common/
│   ├── AppVersion.kt          (actual — NSBundle)
│   └── IoDispatcher.kt        (actual — Dispatchers.Default)
└── commonTest/kotlin/com/gasguru/core/common/
    ├── CommonUtilsTest.kt     (13 tests — isStationOpen con LocalDateTime inyectable)
    └── GeoUtilsTest.kt        (4 tests — distanceTo con coordenadas conocidas)
```

---

## Dependencias añadidas

En `commonMain.dependencies`:
- `libs.kotlinx.datetime` — reemplaza `java.time.*`

En `androidMain.dependencies`:
- `libs.koin.android` — antes venía del plugin `gasguru.koin` implícitamente para Android

El plugin `gasguru.proguard` se elimina: la ofuscación ocurre a nivel de app, no de módulo KMP
(igual que en Phase 1 con `:core:model`).

---

## Tests

Framework: `kotlin.test` + `kotest-assertions-core` (mismo patrón que Phase 1).

### GeoUtilsTest (4 tests)
- Misma ubicación → 0f
- Madrid → Sevilla ≈ 391 km (tolerancia 1%)
- Simetría: `a.distanceTo(b) ≈ b.distanceTo(a)`
- Distancia crece con la diferencia de longitud

### CommonUtilsTest (13 tests)
- "L-D: 24H" → siempre true
- "l-d: 24h" lowercase → true (uppercase normalización)
- L-V: abierto en lunes mañana, cerrado en sábado/domingo, cerrado fuera de horario
- L-S: abierto en sábado, cerrado en domingo
- L-D (rango explícito): abierto en domingo dentro de horario
- Multi-parte con ";": abierto en tarde, cerrado entre partes
- Schedule vacío → false
- Schedule malformado → false

---

## Criterio de "done"

- [x] Plugin cambiado a `gasguru.kmp.library`
- [x] Todos los archivos en source sets correctos
- [x] `GeoUtils.kt` sin `Math.toRadians`
- [x] `CommonUtils.kt` sin `java.time.*`
- [x] expect/actual para `ioDispatcher` y `getAppVersion()`
- [x] `LocationUtils.kt` en androidMain
- [x] Tests en commonTest
- [ ] `./gradlew :core:common:build` compila Android + iOS *(pendiente validar en CI)*
- [ ] Módulos downstream compilan
