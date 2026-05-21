# KMP Phase 8A — BuildKonfig en `:core:supabase`

## Objetivo

Mover `supabaseModule` de `androidMain` a `commonMain` para que iOS pueda inicializar Koin en Phase 8D sin necesidad de pasar credenciales desde Swift.

## Problema

`android.BuildConfig.supabaseUrl` y `android.BuildConfig.supabaseKey` son Android-only — no accesibles desde `iosMain` ni `commonMain`. El `SupabaseModule` estaba en `androidMain` únicamente por eso.

## Solución

**BuildKonfig** (yshrsmz/BuildKonfig, v0.21.2) genera un objeto Kotlin accesible desde todos los source sets (`commonMain`, `androidMain`, `iosMain`) a partir de valores en `local.properties` / variables de entorno.

## Archivos modificados

| Archivo | Cambio |
|---|---|
| `gradle/libs.versions.toml` | Añadida versión `buildkonfig = "0.21.2"` y plugin |
| `build.gradle.kts` (raíz) | Registrado `alias(libs.plugins.buildkonfig) apply false` |
| `core/supabase/build.gradle.kts` | Plugin `buildkonfig` + bloque de configuración; `localProps` movido fuera de `defaultConfigs`; `srcDir(tasks.named("generateBuildKonfig"))` en `commonMain` |
| `core/supabase/src/commonMain/.../di/SupabaseModule.kt` | Creado — usa `SupabaseSecrets.SUPABASE_URL / SUPABASE_KEY` |
| `core/supabase/src/androidMain/.../di/SupabaseModule.kt` | Eliminado |
| `core/analytics/build.gradle.kts` | Eliminado `pod("Mixpanel-swift")` del bloque cocoapods |
| `core/analytics/src/iosMain/.../MixpanelAnalyticsHelperIos.kt` | Eliminado (importaba cinterop Mixpanel_swift) |
| `core/analytics/src/iosMain/.../di/AnalyticsModuleIos.kt` | Simplificado a `NoOpAnalyticsHelper` (Mixpanel iOS es V2) |

## Decisiones

- **`objectName = "SupabaseSecrets"`**: evita colisión con `android.BuildConfig`.
- **`localProps` fuera del lambda `defaultConfigs {}`**: `java.util.Properties` no resuelve dentro del DSL de BuildKonfig; hay que leerlo a nivel de script.
- **`kotlin.srcDir(tasks.named("generateBuildKonfig"))`**: BuildKonfig 0.21.2 no wirea automáticamente el task a las compilaciones KMP de Android/iOS. Registrar el task como `srcDir` crea la dependencia implícita.
- **Mixpanel iOS no-op**: El cinterop `Mixpanel-swift` requería CocoaPods instalado y el framework descargado. Para V1 iOS, analytics usa `NoOpAnalyticsHelper`. Se reintegrará en V2 cuando iOS tenga analíticas reales.

## Verificación

```
./gradlew :core:supabase:assembleDebug              # ✅
./gradlew :core:supabase:compileKotlinIosSimulatorArm64  # ✅
./gradlew :core:supabase:testDebugUnitTest          # ✅
./gradlew :core:analytics:compileKotlinIosSimulatorArm64 # ✅
./gradlew :core:analytics:testDebugUnitTest         # ✅
```

## Siguiente fase

**Phase 8D**: `KoinInit.kt` en `composeApp/iosMain` + `MainViewController` → `App()` + iOS LaunchScreen.
Con 8A completo, `supabaseModule` ya está disponible en `commonMain` y `initKoin()` en iOS no necesita parámetros.
