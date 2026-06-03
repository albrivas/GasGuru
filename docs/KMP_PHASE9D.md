# KMP Phase 9D — PlacesRepository iOS (Google Places SDK)

## Objetivo

Sustituir el stub `PlacesRepositoryIos` (que devolvía `flowOf(emptyList())` y `flowOf(LatLng(0.0, 0.0))`) por una implementación real con **Google Places SDK iOS** para paridad exacta con Android: mismos POIs, mismo filtrado por país (`ES`) y mismos `placeId` estables round-tripeables por navegación, Room y state.

**Stub cerrado**: #4 `PlacesRepository`.  
**Estado de stubs tras esta fase**: 6/12 cerrados.

---

## Decisiones técnicas

### Google Places SDK iOS (no MKLocalSearch)

Se elige Google Places SDK en lugar de `MKLocalSearch` (Apple nativo) por:

- **Paridad exacta con Android**: mismos resultados de autocompletar y calidad de POIs (gasolineras, direcciones comerciales) para el mismo query.
- **`placeId` estable**: el ID Google se persiste en `RecentSearchQueryEntity` y se serializa en `PlaceArgs`/`RoutePlanArgs` igual que en Android. Sin sintetizar IDs ni lógica de parsing extra.
- **Reutiliza la API key existente**: `googleApiKey` ya estaba en `local.properties` para Android. Sin keys nuevas.
- **Coherencia**: aunque Phase 9C eligió MapKit para el mapa (sistema, sin coste), la búsqueda de lugares requiere la misma calidad de datos que Android → Google Places.

### BuildKonfig para la API key (patrón Phase 8A)

`core/data/build.gradle.kts` añade `alias(libs.plugins.buildkonfig)` y un bloque:

```kotlin
buildkonfig {
    packageName = "com.gasguru.core.data"
    objectName  = "DataSecrets"
    defaultConfigs {
        buildConfigField(Type.STRING, "GOOGLE_API_KEY",
            localProps.getProperty("googleApiKey")
                ?: System.getenv("GOOGLE_API_KEY").orEmpty()
        )
    }
}
```

Genera `com.gasguru.core.data.DataSecrets.GOOGLE_API_KEY` en `commonMain`. La clave se lee de `local.properties` (misma propiedad `googleApiKey` que usa Android con `secrets-gradle-plugin`) con fallback a env var `GOOGLE_API_KEY` para CI.

Android sigue usando `BuildConfig.googleApiKey` en `:core:network/androidMain` — **cero cambio en la ruta Android**.

### CocoaPods en `:core:data`

Se añade `kotlin("native.cocoapods")` y el bloque:

```kotlin
cocoapods {
    summary = "GasGuru core data layer"
    homepage = "https://github.com/gasguru/GasGuru"
    version = "1.0"
    ios.deploymentTarget = "15.0"
    pod("GooglePlaces") { version = "~> 8.5" }
}
```

Esto descarga y compila GooglePlaces para cinterop en los tasks de Kotlin/Native. Versión anclada: `8.5.0` (legacy API activa; incluye `findAutocompletePredictionsFromQuery` y `fetchPlaceFromPlaceID`).

### Pod en Podfile del iosApp

`GooglePlaces` no se propaga automáticamente desde `core/data.podspec` a `composeApp.podspec`. El motivo: `core/data` es una dependencia KMP interna del framework `ComposeApp`, no un pod referenciado por el Podfile. Para que Xcode pueda linkear `GooglePlaces.framework` al construir `iosApp`, se añade explícitamente al Podfile:

```ruby
pod 'GooglePlaces', '~> 8.5'
```

### Patrón cinterop: `suspendCancellableCoroutine` + `withContext(Dispatchers.Main)`

`GMSPlacesClient` requiere ser llamado desde el hilo principal (UIKit constraint). Patrón idéntico al de `LocationTrackerIos` (Phase 9B):

- `withContext(Dispatchers.Main)` para crear y llamar al cliente.
- `suspendCancellableCoroutine` para convertir los callbacks ObjC en `suspend fun`.
- `.flowOn(ioDispatcher)` para ejecutar el flujo fuera del main thread.
- `.catch { emit(fallback) }` para reproducir el manejo de errores de `PlacesDataSourceImp` Android.

### Inicialización del SDK en el factory Koin

`GMSPlacesClient.provideAPIKey(DataSecrets.GOOGLE_API_KEY)` se llama dentro del `single<PlacesRepository> { ... }` factory de `IosDataModule`, espejando el patrón Android (`Places.initialize(...)` dentro de `PlacesModule.kt`). Se añade `@file:OptIn(ExperimentalForeignApi::class)` al archivo por el acceso cinterop.

### `GOOGLE_API_KEY` Koin qualifier

`IosDataModule` ahora provee `single<String>(named(KoinQualifiers.GOOGLE_API_KEY)) { DataSecrets.GOOGLE_API_KEY }` con el valor real (antes era `""`). Necesario porque `CommonDataModule` inyecta esta clave en `GoogleStaticMapRepository`.

---

## Archivos modificados

| Archivo | Cambio |
|---------|--------|
| `core/data/build.gradle.kts` | Añade `buildkonfig` + `kotlin("native.cocoapods")` + bloque `cocoapods { pod("GooglePlaces") }` + `buildkonfig { DataSecrets }` + `commonMain { kotlin.srcDir(...) }` |
| `core/data/src/iosMain/.../places/PlacesRepositoryIos.kt` | Reescritura completa con GMSPlacesClient cinterop |
| `core/data/src/iosMain/.../di/IosDataModule.kt` | Provee API key real, inicializa SDK, inyecta `ioDispatcher` en PlacesRepositoryIos |
| `iosApp/Podfile` | Añade `pod 'GooglePlaces', '~> 8.5'` |
| `docs/KMP_MIGRATION.md` | Phase 9D ✅, resumen actualizado (6/12 stubs) |
| `CLAUDE.md` | Entrada `KMP Phase 9D` en tabla de documentación |

### Archivos NO modificados

- `core/data/src/commonMain/.../PlacesRepository.kt` — contrato intacto
- `core/domain/.../GetPlacesUseCase.kt`, `GetLocationPlaceUseCase.kt` — pass-through
- `core/components/.../GasGuruSearchBarViewModel.kt` — opaco al placeId
- `feature/station-map`, `feature/search`, `feature/route-planner` — round-trip del id
- `core/model/.../SearchPlace.kt`, `Route.kt` — modelos intactos
- `core/testing` fakes — desacoplados del id real
- `:core:network` — cero cambios en la ruta Android

---

## Verificación

```bash
./gradlew :core:data:assembleDebug                         # ✅ BUILD SUCCESSFUL
./gradlew :core:data:testDebugUnitTest                     # ✅ BUILD SUCCESSFUL
./gradlew :core:data:compileKotlinIosSimulatorArm64        # ✅ BUILD SUCCESSFUL
./gradlew :composeApp:compileKotlinIosSimulatorArm64       # ✅ BUILD SUCCESSFUL
./gradlew :composeApp:compileDebugKotlinAndroid            # ✅ BUILD SUCCESSFUL
./gradlew :app:assembleProdDebug                           # ✅ BUILD SUCCESSFUL
./gradlew codeCheck                                        # ✅ BUILD SUCCESSFUL
pod install  (iosApp/)                                     # ✅ GooglePlaces 8.5.0 instalado
```

### Checklist de verificación manual (simulador iPhone 15, iOS 17+)

- [ ] Abrir barra de búsqueda → escribir "Madrid" → lista muestra resultados de España (paridad con Android)
- [ ] Seleccionar un lugar → mapa se centra correctamente (coordenadas reales)
- [ ] Cerrar y reabrir búsqueda → lugar aparece en recientes; tap → centrado correcto (placeId round-trip)
- [ ] Query vacío → no hay fetch, lista vacía
- [ ] Cambio rápido de query → no crashes (cancelación correcta)
- [ ] Route planner: seleccionar origen + destino → ruta se dibuja en el mapa
- [ ] Regresión Android: Places funcionan igual que antes en emulador

---

## Próximos pasos

- **Phase 9C.2** — Map polish iOS: clustering + custom markers con precio/logo en MapKit
- **Phase 9E** — Detail station actions: MapsNavigation + ShareAction + NotificationPermission en iOS
- **Phase 9F** — In-App Review iOS (StoreKit)
