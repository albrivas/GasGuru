# KMP Phase 4b — `core:supabase`

## Motivación

Con `AnalyticsHelper` en `commonMain` (Phase 4a), `SupabaseRemoteDataSource` y `ApiAnalyticsExt` podían moverse también a `commonMain`. El SDK `supabase-kt` es nativamente KMP — no requiere `expect/actual` para la inicialización del cliente.

---

## Decisiones de diseño

### 1. Sin `expect/actual` para credenciales

`createSupabaseClient(url, key)` funciona igual en todas las plataformas. Las credenciales siguen viniendo de `BuildConfig.supabaseUrl/supabaseKey` (generado por el plugin `secrets-gradle`) en el módulo Koin de `androidMain`. No hay nada que parametrizar con expect/actual.

### 2. Motores Ktor por plataforma

`supabase-kt` usa Ktor internamente pero no provee el motor HTTP. Hay que declararlo explícitamente:

```kotlin
androidMain.dependencies {
    implementation(libs.ktor.client.android)   // OkHttp
}
iosMain.dependencies {
    implementation(libs.ktor.client.darwin)    // NSURLSession
}
```

### 3. `SupabaseModule` permanece en `androidMain`

El módulo Koin crea el `SupabaseClient` con credenciales de `BuildConfig` (Android-only) y registra todas las dependencias. Cuando exista app iOS, se creará un módulo equivalente en `iosMain` con sus propias credenciales.

### 4. `SupabaseRemoteDataSource` y `ApiAnalyticsExt` → `commonMain`

Ambos solo dependen de `AnalyticsHelper` (ahora en `commonMain`) y del SDK Supabase (KMP). No hay ninguna API Android-específica en su implementación.

### 5. Tests en `commonTest` sin MockK

`MockEngine` de Ktor es KMP. Los tests usan:
- `kotlin.test` en lugar de JUnit5
- `FakeAnalyticsHelper` local (simple implementación fake, sin `core:testing` que es Android-only)
- JSON fixture inlineado en `StubsSupabaseResponse` (sin `classLoader` que es JVM-only)

---

## Estructura final de source sets

| Source set | Ficheros |
|------------|----------|
| `commonMain` | `SupabaseManager`, `SupabaseManagerImpl`, `RemoteDataSource`, `SupabaseRemoteDataSource`, `ApiAnalyticsExt`, `NetworkError`, `PriceAlertSupabase`, `SupabaseFuelStation` |
| `androidMain` | `SupabaseModule` (Koin + BuildConfig) |
| `iosMain` | (vacío — DI iOS cuando exista la app) |
| `commonTest` | `SupabaseRemoteDataSourceTest`, `SupabaseManagerImplTest`, `FakeAnalyticsHelper`, `StubsSupabaseResponse` |

---

## Resultado de verificación

| Tarea | Resultado |
|-------|-----------|
| `:core:supabase:assembleDebug` | ✅ |
| `:core:supabase:testDebugUnitTest` | ✅ |
| `:app:assembleDebug` | ✅ (592 tareas, sin regresiones) |
| `:core:supabase:compileKotlinIosArm64` | ⏳ (requiere CocoaPods cuando haya app iOS) |
