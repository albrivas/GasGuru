# KMP Phase 4a — `core:analytics`

## Motivación

La migración de `core:supabase` a KMP quedó bloqueada porque `AnalyticsHelper` y `AnalyticsEvent` eran Android-only. Migrar `core:analytics` primero desbloquea todos los módulos KMP que necesiten trazar analíticas.

El SDK de Mixpanel no tiene versión KMP única, pero sí SDKs nativos para Android e iOS. El patrón aplicado: interfaz compartida en `commonMain`, implementaciones Mixpanel plataforma-específicas.

---

## Decisiones de diseño

### 1. commonMain: interfaz + modelos
`AnalyticsHelper`, `AnalyticsEvent` y `NoOpAnalyticsHelper` son Kotlin puro sin dependencias Android. Van a `commonMain` para que cualquier módulo KMP pueda depender de ellos.

### 2. androidMain: Mixpanel Android SDK
`MixpanelAnalyticsHelper` usa `com.mixpanel.android:mixpanel-android` (Android-only). `LocalAnalyticsHelper` usa `staticCompositionLocalOf` de `androidx.compose.runtime` (Android-only). Ambas quedan en `androidMain`.

### 3. iosMain: Mixpanel iOS SDK via CocoaPods
`MixpanelAnalyticsHelperIos` usa el pod `Mixpanel-swift ~> 6.2` via el plugin `kotlin("native.cocoapods")`. La importación es `cocoapods.Mixpanel_swift.Mixpanel`.

```kotlin
// build.gradle.kts
kotlin {
    cocoapods {
        pod("Mixpanel-swift") { version = "~> 6.2" }
    }
}
```

La compilación iOS (`compileKotlinIosArm64`) requiere que el pod esté instalado (`pod install`). Esto se activará cuando el proyecto iOS exista con su Podfile.

### 4. DI por plataforma
- Android: `analyticsModule` (Koin Android) — selecciona `LogcatAnalyticsHelper` en DEBUG, `MixpanelAnalyticsHelper` en PROD
- iOS: `analyticsModuleIos` (Koin core) — siempre `MixpanelAnalyticsHelperIos`

### 5. Tests
- `commonTest`: `AnalyticsEventCategoriesTest` adaptado a `kotlin.test` (sin `@Nested` ni `@DisplayName`)
- `src/test/kotlin`: `LogcatAnalyticsHelperTest` y `MixpanelAnalyticsHelperTest` siguen con JUnit5 + MockK (correcto para implementaciones Android-específicas)

---

## Impacto en consumidores

Ningún consumidor necesita cambios de imports. El package `com.gasguru.core.analytics` se mantiene. Los módulos Android-only obtienen el target Android; los módulos KMP que declaren `projects.core.analytics` en `commonMain.dependencies` ahora pueden usar `AnalyticsEvent` y `AnalyticsHelper`.

Módulo desbloqueado: `core:supabase` puede ahora mover `SupabaseRemoteDataSource` a `commonMain` ya que `AnalyticsHelper` está en `commonMain`.

---

## Resultado de verificación

| Tarea | Resultado |
|-------|-----------|
| `:core:analytics:assembleDebug` | ✅ |
| `:core:analytics:testDebugUnitTest` | ✅ |
| `:app:assembleDebug` | ✅ (595 tareas, sin regresiones) |
| `:core:analytics:compileKotlinIosArm64` | ⏳ (requiere CocoaPods cuando haya app iOS) |
