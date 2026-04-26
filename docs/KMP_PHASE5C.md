# KMP Phase 5C — Migración de `:core:network` y `:mocknetwork`

## Objetivo

Limpiar el código muerto de la API del gobierno, migrar `:core:network` a KMP con Ktor y simplificar `:mocknetwork` para eliminar la dependencia de Retrofit/MockWebServer.

---

## Cambios en `:core:network`

### Antes → Después

| Aspecto | Antes | Después |
|---------|-------|---------|
| Plugin | `gasguru.android.library` | `gasguru.kmp.library` |
| Cliente HTTP | Retrofit + OkHttp | Ktor HttpClient |
| Serialización | Moshi (`@JsonClass`, `@Json`) | kotlinx-serialization (`@Serializable`, `@SerialName`) |
| Interceptor Routes | `RoutesInterceptor` (OkHttp) | `routesPlugin` (Ktor, expect/actual) |
| DI | `networkModule()` (función) | `ktorModule` (val Koin) |

### Ficheros eliminados (código muerto)

Toda la infraestructura de la API del gobierno ya no se usa — los datos de gasolineras vienen de Supabase:

- `datasource/RemoteDataSource.kt` + `RemoteDataSourceImp.kt`
- `retrofit/ApiService.kt`
- `model/NetworkFuelStation.kt` + `NetworkPriceFuelStation.kt`
- `analytics/ApiAnalyticsExt.kt`
- Tests y JSONs asociados

### Estructura KMP

```
core/network/src/
  commonMain/
    common/NetworkUtils.kt              (tryCall con Arrow Either)
    datasource/RoutesDataSource.kt      (interface)
    datasource/RoutesDataSourceImpl.kt  (Ktor HttpClient)
    model/route/*.kt                    (19 modelos, @Serializable)
    request/RequestRoute.kt             (5 data classes, @Serializable)
    RoutesPlugin.kt                     (expect)
  androidMain/
    datasource/PlacesDataSource.kt      (interface)
    datasource/PlacesDataSourceImp.kt   (Google Places SDK)
    di/KtorModule.kt                    (HttpClient + RoutesDataSource)
    di/PlacesModule.kt                  (PlacesClient)
    RoutesPlugin.kt                     (actual: X-Goog-* headers)
  iosMain/
    RoutesPlugin.kt                     (actual: X-Ios-Bundle-Identifier)
  androidUnitTest/
    datasource/PlacesDataSourceTest.kt  (MockK)
    datasource/RoutesDataSourceTest.kt  (Ktor MockEngine)
```

### DI — `KtorModule.kt`

Reemplaza el antiguo `networkModule()`. Es un `val` (no función) que provee:

- `Json` con `ignoreUnknownKeys = true` e `isLenient = true`
- `HttpClient(OkHttp)` con qualifier `ROUTE_HTTP_CLIENT`: timeout 60s, ContentNegotiation, Logging, `routesPlugin`, base URL `https://routes.googleapis.com/`
- `RoutesDataSource` → `RoutesDataSourceImpl(httpClient)`

```kotlin
// GasGuruApplication.kt — antes
networkModule()

// GasGuruApplication.kt — después
ktorModule
```

### `RoutesPlugin` expect/actual

Reemplaza `RoutesInterceptor` (OkHttp). El plugin Ktor añade los headers de autenticación de Google Routes API.

```kotlin
// commonMain
expect fun routesPlugin(packageName: String): HttpClientPlugin<*, *>

// androidMain
actual fun routesPlugin(packageName: String) = createClientPlugin("RoutesPlugin") {
    onRequest { request, _ ->
        request.header("X-Goog-Api-Key", BuildConfig.googleApiKey)
        request.header("X-Goog-FieldMask", "*")
        request.header("X-Android-Package", packageName)
        request.header("X-Android-Cert", cert)
    }
}
```

---

## Cambios en `:mocknetwork`

### Problema anterior

`MockRemoteDataSource` implementaba la interfaz de Supabase pero internamente usaba `ApiService` (Retrofit, API gobierno) + `MockWebServer` (OkHttp) para cargar un JSON en formato gobierno y mapearlo manualmente a `SupabaseFuelStation`. Un shim innecesariamente complejo.

### Solución nueva

Lee directamente `mock-fuel-stations.json` (formato Supabase) de assets con kotlinx-serialization. Sin Retrofit, sin MockWebServer, sin mapeo.

```kotlin
class MockRemoteDataSource(...) : RemoteDataSource {
    override suspend fun getListFuelStations() = try {
        val stations = withContext(ioDispatcher) {
            val jsonString = context.assets.open("mock-fuel-stations.json")
                .bufferedReader().use { it.readText() }
            json.decodeFromString<List<SupabaseFuelStation>>(jsonString)
        }
        stations.right()
    } catch (exception: Exception) {
        NetworkError(exception = exception).left()
    }
}
```

### `mock-fuel-stations.json`

11.957 estaciones en formato Supabase, convertidas desde el JSON original de la API del gobierno. Coordenadas y precios ya como `Double`/`Double?`, no como strings.

### `build.gradle.kts` simplificado

Eliminado: `projects.core.network`, `bundles.moshi`, `ksp`, `bundles.com.squareup.retrofit2`, `mock.webserver`, `gasguru.secrets.google`.

Nuevo:
```kotlin
dependencies {
    implementation(projects.core.supabase)
    implementation(projects.core.common)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.io.arrow.kt.arrow.core)
}
```

---

## `KoinQualifiers.kt`

Eliminados los qualifiers de Retrofit/OkHttp (`FUEL_OK_HTTP`, `FUEL_RETROFIT`, `ROUTE_OK_HTTP`, `ROUTE_RETROFIT`). Mantenido `GOOGLE_API_KEY` (usado por `GoogleStaticMapRepository` en `core:data`).

---

## Verificación

```bash
./gradlew :core:network:assembleDebug
./gradlew :core:network:compileKotlinIosSimulatorArm64
./gradlew :core:network:testDebugUnitTest
./gradlew :mocknetwork:assembleDebug
./gradlew :app:assembleProdDebug
./gradlew :app:assembleMockDebug
```
