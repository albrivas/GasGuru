# Dependency Injection — GasGuru

## Objetivo

GasGuru usa **Koin 4.x** como framework de inyección de dependencias. La migración desde Dagger Hilt se realizó como paso previo a la evolución del proyecto hacia **Kotlin Multiplatform (KMP) con Compose Multiplatform**.

`koin-core` es un artefacto KMP nativo: cuando los módulos `core` migren a `commonMain`, sus definiciones Koin pueden trasladarse sin cambios a la capa multiplataforma. Hilt, al depender de kapt/KSP y de las APIs de Android, no es compatible con KMP.

Beneficios adicionales:
- Sin generación de código para DI (kapt/KSP solo se usa en `core/database` para Room y en `core/network` para Moshi).
- Tiempos de compilación más rápidos.
- DSL idiomático de Kotlin, sin anotaciones ni procesadores.

---

## Estructura DI

Cada módulo Gradle declara sus propias definiciones Koin en un archivo `*Module.kt`. El módulo `app` las agrega todas en `GasGuruApplication.startKoin { }`.

```
GasGuruApplication.startKoin {
    coroutineModule          ← core/common
    databaseModule           ← core/database
    daoModule                ← core/database
    networkModule            ← core/network
    placesModule             ← core/network
    supabaseModule           ← core/supabase
    notificationModule       ← core/notifications
    dataModule               ← core/data
    dataProviderModule       ← core/data
    domainModule             ← core/domain
    navigationModule         ← navigation
    remoteDataSourceModule   ← app/src/prod|mock (flavor-specific)
    appModule                ← app
    stationMapModule         ← feature/station-map
    detailStationModule      ← feature/detail-station
    favoriteListStationModule← feature/favorite-list-station
    profileModule            ← feature/profile
    routePlannerModule       ← feature/route-planner
    onboardingModule         ← feature/onboarding
    searchBarModule          ← core/components
}
```

### Qualifiers nombrados

Los qualifiers se gestionan con `named()` y las constantes de `KoinQualifiers` en `core/common`:

```kotlin
object KoinQualifiers {
    const val IO_DISPATCHER = "ioDispatcher"
    const val DEFAULT_DISPATCHER = "defaultDispatcher"
    const val APPLICATION_SCOPE = "applicationScope"
    const val FUEL_OK_HTTP = "fuelOkHttp"
    const val ROUTE_OK_HTTP = "routeOkHttp"
    const val GOOGLE_API_KEY = "google_api_key"
    // ...
}
```

Usar siempre las constantes de `KoinQualifiers`; nunca strings literales en los módulos.

---

## Scoping

| Koin DSL | Equivalente Hilt | Comportamiento |
|----------|-----------------|----------------|
| `single { }` | `@Singleton` | Una instancia por contenedor (Application scope) |
| `factory { }` | sin scope | Nueva instancia en cada punto de inyección |
| `viewModel { }` | `@HiltViewModel` | Una instancia por ViewModel scope |

Los use cases son `factory` (sin estado). Los repositorios, datasources y managers son `single`.

---

## Tabla de equivalencias de anotaciones

| Hilt (antes) | Koin (ahora) |
|---|---|
| `@HiltAndroidApp` en Application | `startKoin { }` en `onCreate()` |
| `@AndroidEntryPoint` en Activity/Fragment | `KoinComponent` (o nativo via `by inject()`) |
| `@HiltViewModel` + `@Inject constructor` | `viewModel { MyViewModel(get(), ...) }` |
| `@Inject constructor` en clase sin scope | `factory { MyClass(get()) }` |
| `@Inject constructor` + `@Singleton` | `single { MyClass(get()) }` |
| `@Binds fun bind(impl: FooImpl): Foo` | `single<Foo> { FooImpl(get()) }` |
| `@Provides @Singleton fun provide()` | `single { provide() }` |
| `@Qualifier @FuelApi` | `named(KoinQualifiers.FUEL_OK_HTTP)` |
| `@ApplicationContext context: Context` | `androidContext()` en lambda de módulo |
| `@IoDispatcher` | `get(named(KoinQualifiers.IO_DISPATCHER))` |
| `hiltViewModel()` en Composable | `koinViewModel()` |
| `@Inject lateinit var` en Activity | `val foo: Foo by inject()` |
| `by viewModels()` en Activity | `val vm: VM by viewModel()` |
| `@EntryPoint` + `EntryPointAccessors` | `KoinComponent` + `val foo by inject()` |

---

## Cómo añadir nuevas dependencias

### 1. Añadir binding en el módulo correspondiente

```kotlin
// core/data/src/main/java/.../di/DataModule.kt
val dataModule = module {
    single<MyRepository> {
        MyRepositoryImpl(
            dao = get(),
            ioDispatcher = get(named(KoinQualifiers.IO_DISPATCHER)),
        )
    }
}
```

### 2. Registrar un nuevo ViewModel

```kotlin
// feature/my-feature/src/main/.../di/MyFeatureModule.kt
import org.koin.core.module.dsl.viewModel  // ← usar este, no el de androidx

val myFeatureModule = module {
    viewModel { MyViewModel(get(), get()) }
}
```

Añadir `myFeatureModule` a la lista de módulos en `GasGuruApplication`.

### 3. Inyectar en un Composable

```kotlin
import org.koin.androidx.compose.koinViewModel

@Composable
fun MyScreen(viewModel: MyViewModel = koinViewModel()) {
    // ...
}
```

### 4. Inyectar en una Activity/Fragment

```kotlin
class MyActivity : ComponentActivity() {
    private val myDep: MyDependency by inject()
    private val viewModel: MyViewModel by viewModel()
}
```

---

## Módulos con KSP activo

KSP solo se mantiene en los módulos que lo necesitan por razones distintas a DI:

| Módulo | KSP | Cómo se aplica | Motivo |
|--------|-----|----------------|--------|
| `core/database` | Sí | vía `gasguru.room` (implícito) | Room codegen |
| `core/network` | Sí | `alias(libs.plugins.ksp)` explícito | Moshi codegen |
| `mocknetwork` | Sí | `alias(libs.plugins.ksp)` explícito | Moshi codegen |
| Resto | No | — | Hilt era el único motivo; eliminado |

> **Importante**: `KoinConventionPlugin` no aplica KSP. Los módulos que usan `ksp(...)` para Moshi codegen deben declarar explícitamente `alias(libs.plugins.ksp)` en su bloque `plugins { }`.

---

## Consideraciones importantes

### Validación en runtime (no en compilación)
Hilt valida el grafo de dependencias en **tiempo de compilación**: un binding faltante provoca error de build. Koin resuelve en **runtime**: un binding faltante provoca `NoBeanDefFoundException` al arrancar la app.

Para mitigarlo:
- Ejecutar tests de integración con `KoinTest.checkModules()` si se requiere validación automática.
- Los tests unitarios de ViewModel no requieren Koin (constructores directos).

### Dual registration (concrete + interface)
Cuando un use case inyecta la clase concreta directamente (en lugar de la interfaz), usar el patrón `bind`:

```kotlin
single { OfflineFuelStationRepository(...) } bind FuelStationRepository::class
```

Esto registra `OfflineFuelStationRepository` como su tipo concreto **y** como `FuelStationRepository`, permitiendo ambas formas de inyección.

### Módulos flavor-specific
`remoteDataSourceModule` tiene dos implementaciones:
- `app/src/prod/.../ProdDataSourceModule.kt` → `RemoteDataSourceImp`
- `app/src/mock/.../MockNetworkDataSource.kt` → `MockRemoteDataSource` (incluye `mockWebServerModule` con `includes()`)

`GasGuruApplication` solo referencia `remoteDataSourceModule`; el build system selecciona la implementación correcta según el flavor activo.

### KMP readiness
En la migración a KMP, los módulos `core` que no tengan dependencias de Android pueden mover sus Koin modules a `commonMain` sin cambios en el DSL. Los módulos con `androidContext()` permanecerán en `androidMain`.
