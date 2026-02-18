# Network KMP

## Objetivo

Migrar `core/network` a Kotlin Multiplatform (KMP) para que el código de red sea reutilizable en targets no-Android (iOS en primera instancia con CMP). La migración no implica cambios funcionales: la logica de red, los modelos y las interfaces permanecen identicos, solo cambia como se estructuran y como se inyectan las dependencias.

Ktor y kotlinx.serialization ya eran KMP-ready antes de esta migración, lo que permite mover la mayor parte del modulo a `commonMain` sin reescribir nada.

---

## Estado inicial

- Plugin Gradle: `gasguru.android.library`
- Todo el codigo en `src/main/java/`
- HTTP: Retrofit + OkHttp
- Serializacion: Moshi (`@JsonClass`, `@Json`)
- DI: `@Inject` directamente en `RemoteDataSourceImp` y `RoutesDataSourceImpl`
- Tests: `MockWebServer` (OkHttp) en `src/test/java/`, levantando un servidor HTTP real en local

---

## Estado final

Plugin Gradle: `gasguru.kmp.library`

```
core/network/src/
├── commonMain/kotlin/       ← modelos, interfaces, implementaciones (sin DI)
├── androidMain/kotlin/      ← Hilt, OkHttp engine, BuildConfig, Places API
├── iosMain/kotlin/          ← vacio (Darwin engine declarado en build.gradle.kts)
└── androidUnitTest/kotlin/  ← tests JVM con MockEngine y JSON reales
    androidUnitTest/resources/
```

### Distribucion de archivos por source set

**`commonMain`**

| Archivo | Nota |
|---------|------|
| `datasource/RemoteDataSource.kt` | interfaz |
| `datasource/RemoteDataSourceImp.kt` | sin `@Inject` ni `@FuelApi` |
| `datasource/RoutesDataSource.kt` | interfaz |
| `datasource/RoutesDataSourceImpl.kt` | sin `@Inject` ni `@RouteApi` |
| `model/*.kt` | `@Serializable` + `@SerialName` (kotlinx) |
| `request/RequestRoute.kt` | |
| `common/NetworkUtils.kt` | |

**`androidMain`**

| Archivo | Nota |
|---------|------|
| `di/KtorModule.kt` | provee los dos `HttpClient` y las implementaciones via `@Provides` |
| `di/NetworkModule.kt` | legacy (Retrofit) |
| `di/PlacesModule.kt` | |
| `di/Qualifiers.kt` | `@FuelApi`, `@RouteApi` |
| `datasource/PlacesDataSource.kt` / `PlacesDataSourceImp.kt` | Android-only (Google Places SDK) |
| `RoutesInterceptorKtor.kt` | factory `fun routesPlugin(packageName)`, usa `BuildConfig` |
| `RoutesInterceptor.kt` | legacy OkHttp |
| `retrofit/ApiService.kt` / `RouteApiServices.kt` | legacy Retrofit |

**`androidUnitTest`**

| Archivo | Nota |
|---------|------|
| `mock/NetworkMockEngine.kt` | cliente Ktor con `MockEngine` + cola de respuestas |
| `stubs/MockApiResponse.kt` | devuelve `MockEngineResponse` cargando desde JSON |
| `stubs/AssetsManager.kt` | carga JSON via `ClassLoader` |
| `stubs/StubsResponse.kt` | |
| `datasource/RemoteDataSourceTest.kt` | |
| `datasource/PlacesDataSourceTest.kt` | |
| `resources/...responses/` | JSON de respuestas reales |

---

## Conceptos clave para migrar a KMP

### 1. `@Inject` no puede estar en `commonMain`

Hilt es exclusivo de Android. Cualquier clase con `@Inject` o anotaciones de Hilt (`@Singleton`, `@HiltViewModel`, etc.) no puede compilar en `commonMain`.

Solucion: quitar `@Inject` de las implementaciones y proveerlas manualmente en el modulo Hilt de `androidMain`:

```kotlin
// androidMain — di/KtorModule.kt
@Provides
@Singleton
fun provideRemoteDataSource(@FuelApi httpClient: HttpClient): RemoteDataSource =
    RemoteDataSourceImp(httpClient = httpClient)
```

### 2. KSP en KMP: `kspAndroid` en lugar de `ksp`

En modulos KMP la configuracion `ksp` no existe. Para generar codigo Hilt hay que usar `kspAndroid`:

```kotlin
// core/network/build.gradle.kts
dependencies {
    add("kspAndroid", libs.hilt.compiler)
    add("kspAndroid", libs.moshi.codegen)
}
```

Por esto el convention plugin `gasguru.hilt` (que usa `ksp` internamente) **no se puede aplicar** en un modulo KMP. Se gestiona manualmente.

### 3. Convention plugin `gasguru.kmp.library`

Creado en `build-logic` para centralizar la configuracion KMP:

- Aplica `kotlin.multiplatform` + `android.library` + `gasguru.jacoco`
- Declara `androidTarget` (JVM 17) y targets iOS (`iosX64`, `iosArm64`, `iosSimulatorArm64`)
- Habilita JUnit Platform en todas las tareas `Test`

```kotlin
// build-logic/convention/src/main/java/KmpLibraryConventionPlugin.kt
class KmpLibraryConventionPlugin : Plugin<Project> { ... }
```

Referencia en `libs.versions.toml`:
```toml
gasguru-kmp-library = { id = "gasguru.kmp.library" }
```

### 4. Tests con recursos de fichero: `androidUnitTest`, no `commonTest`

`commonTest` corre en todos los targets. No puede usar `ClassLoader` de JVM (necesario para cargar ficheros JSON desde `resources/`). Los tests que dependen de `ClassLoader` van a `androidUnitTest`, que:

- Corre solo en JVM
- Hereda automaticamente las dependencias de `commonTest` (incluido `ktor-client-mock`)
- Puede acceder a `src/androidUnitTest/resources/`

---

## MockEngine vs MockWebServer

| | `MockWebServer` (OkHttp) | `MockEngine` (Ktor) |
|---|---|---|
| Levanta servidor | Si, servidor HTTP real en local | No, intercepta a nivel de cliente |
| KMP compatible | No | Si |
| Configuracion | `server.start()` / `server.shutdown()` | `HttpClient(MockEngine { ... })` |
| Cola de respuestas | API propia de OkHttp | `ArrayDeque` manual |

`NetworkMockEngine` implementa la cola con un `ArrayDeque<MockEngineResponse>`:

```kotlin
class NetworkMockEngine {
    private val pendingResponses = ArrayDeque<MockEngineResponse>()

    private val engine = MockEngine { _ ->
        val response = pendingResponses.removeFirst()
        respond(content = ByteReadChannel(response.body), ...)
    }

    val httpClient: HttpClient = HttpClient(engine) { ... }

    fun enqueue(response: MockEngineResponse) { pendingResponses.addLast(response) }
}
```

Uso en tests:
```kotlin
@BeforeEach fun setUp() {
    sut = RemoteDataSourceImp(httpClient = mockEngine.httpClient)
}

@Test fun fuelStationSuccess() = runTest {
    mockEngine.enqueue(mockApi.listFuelStationOK())
    assertTrue(sut.getListFuelStations().isRight())
}
```

---

## Archivos clave

| Archivo | Source set | Descripcion |
|---------|-----------|-------------|
| `build-logic/.../KmpLibraryConventionPlugin.kt` | — | Convention plugin KMP |
| `core/network/build.gradle.kts` | — | Config KMP con source sets y deps por target |
| `di/KtorModule.kt` | `androidMain` | Provee `HttpClient` y datasources via Hilt |
| `datasource/RemoteDataSourceImp.kt` | `commonMain` | Implementacion sin `@Inject` |
| `datasource/RoutesDataSourceImpl.kt` | `commonMain` | Implementacion sin `@Inject` |
| `RoutesInterceptorKtor.kt` | `androidMain` | Plugin Ktor para headers de Google Routes API |
| `mock/NetworkMockEngine.kt` | `androidUnitTest` | Cliente mock con cola de respuestas |
