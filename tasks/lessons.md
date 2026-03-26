# Lessons Learned

## L001 — KMP: `api()` en `commonMain` no garantiza runtime en consumidores Android

**Fecha**: 2026-03-20
**Contexto**: Migración de `core:common` a KMP. `isStationOpen()` usa `kotlinx.datetime.Clock.System`.

**Error**: Declarar `api(libs.kotlinx.datetime)` en `commonMain.dependencies` de un módulo KMP NO garantiza que la librería esté en el classpath de runtime de los módulos Android consumidores. Resultado: `NoClassDefFoundError: Failed resolution of: Lkotlinx/datetime/Clock$System;` al navegar a la pantalla de detalle.

**Síntoma confuso**: La pantalla "parecía que iba a abrir pero no abría" — el outer `.catch` capturaba el error y emitía `DetailStationUiState.Error`, cuyo bloque en la UI estaba vacío.

**Causa raíz**: KMP `commonMain.dependencies { api(dep) }` garantiza visibilidad en compilación pero no siempre propaga la dependencia al runtime de módulos Android no-KMP que consumen el módulo.

**Fix aplicado**: Añadir un bloque `dependencies {}` estándar de Android (fuera del bloque `kotlin {}`) en el propio módulo KMP `core:common` con `implementation(libs.kotlinx.datetime)`. Las declaraciones dentro de `kotlin { sourceSets { commonMain/androidMain } }` NO alimentan el `debugRuntimeClasspath` de Android — solo lo hace el bloque `dependencies {}` a nivel Android.

**Verificación**: `./gradlew :core:common:dependencies --configuration debugRuntimeClasspath | grep datetime` debe mostrar `kotlinx-datetime-jvm`.

**Regla**: En módulos KMP con `gasguru.kmp.library`, las dependencias de `commonMain.dependencies { api/implementation(...) }` y `androidMain.dependencies { ... }` NO aparecen en el `debugRuntimeClasspath` Android. Para que una librería esté disponible en runtime Android, declararla en el bloque `dependencies {}` estándar (fuera de `kotlin {}`) del módulo KMP que la usa.

---

## L003 — Supabase (mock flavor) fuerza versión mayor de kotlinx-datetime

**Fecha**: 2026-03-22
**Contexto**: `NoClassDefFoundError: Clock$System` persistía incluso después de declarar `kotlinx-datetime` correctamente en `core:common`.

**Causa raíz**: El flavor `mock` incluye `supabase-kt:3.2.6`, que requiere `kotlinx-datetime:0.7.1`. Gradle resuelve la versión más alta, subiendo de `0.6.2` a `0.7.1`. En `0.7.1`, `kotlinx.datetime.Clock` fue deprecado y `Clock$System` cambió — la clase no existía de la misma forma en el JAR resuelto, causando el `NoClassDefFoundError` en runtime.

**Cómo detectarlo**: `./gradlew :app:dependencies --configuration mockDebugRuntimeClasspath | grep datetime` mostraba `0.6.2 -> 0.7.1`. El culpable estaba unos niveles más arriba en el árbol junto a `co.touchlab:kermit`.

**Fix**: Actualizar `kotlinxDatetime` en `libs.versions.toml` a `0.7.1` para que sea la versión declarada explícitamente. En `0.7.1`, `Clock` viene de `kotlin.time.Clock` (stdlib) en lugar de `kotlinx.datetime.Clock` — actualizar el import en `CommonUtils.kt`.

**Regla**: Cuando haya un `NoClassDefFoundError` persistente de una dependencia KMP, comprobar si algún flavor (especialmente `mock`) arrastra una versión mayor de esa librería con `./gradlew :app:dependencies --configuration <flavor>DebugRuntimeClasspath | grep <lib>`. El conflicto de versión puede cambiar la API o estructura de clases en runtime.

---

## L002 — Hipótesis especulativas sin evidencia: no proponer sin confirmar

**Fecha**: 2026-03-20
**Contexto**: Diagnóstico del bug de la pantalla de detalle.

**Error**: Se propusieron múltiples hipótesis incorrectas (horarios "24:00", proguard, SavedStateHandle, FuelStationBrandsType.first()...) sin confirmar el error real primero. El usuario tuvo que proporcionar el mensaje de error concreto.

**Regla**: Ante una pantalla que "no abre sin dar error", pedir siempre el logcat o mensaje de error real ANTES de proponer hipótesis. El stacktrace ahorra toda la especulación.
