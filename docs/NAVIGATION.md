# Navigation Architecture

## Visión General

GasGuru utiliza una arquitectura de navegación centralizada basada en Jetpack Compose Navigation con type-safe routes. Toda la navegación de la app pasa a través del módulo `navigation`, garantizando un flujo controlado y mantenible.

## Componentes Principales

### 1. NavigationManager

**Ubicación:** `navigation/src/main/java/com/gasguru/navigation/manager/NavigationManager.kt`

Clase central que coordina toda la navegación de la app. Usa un `SharedFlow` para emitir comandos de navegación.

```kotlin
interface NavigationManager {
    val navigationFlow: SharedFlow<NavigationCommand>
    fun navigateTo(destination: NavigationDestination)
    fun navigateBack()
    fun navigateBackTo(route: Any, inclusive: Boolean = false)
    fun navigateBackWithData(key: String, value: Any)
}
```

**Características:**
- Singleton inyectado con Hilt
- Thread-safe
- Buffer que descarta el evento más antiguo si hay overflow

### 2. NavigationDestination

**Ubicación:** `navigation/src/main/java/com/gasguru/navigation/manager/NavigationDestination.kt`

Sealed interface que define todos los destinos posibles de navegación.

```kotlin
sealed interface NavigationDestination {
    data class DetailStation(
        val idServiceStation: Int,
        val presentAsDialog: Boolean = false,
    ) : NavigationDestination

    data object OnboardingWelcome : NavigationDestination
    data object Home : NavigationDestination
    data object Search : NavigationDestination
    data object RoutePlanner : NavigationDestination
}
```

**Nota:** Los destinos representan pantallas/rutas reales. Las acciones de back se modelan como comandos.

### 3. NavigationCommand

**Ubicación:** `navigation/src/main/java/com/gasguru/navigation/manager/NavigationCommand.kt`

Sealed interface que representa acciones de navegación.

```kotlin
sealed interface NavigationCommand {
    data class To(val destination: NavigationDestination) : NavigationCommand
    data object Back : NavigationCommand
    data class BackTo(val route: Any, val inclusive: Boolean = false) : NavigationCommand
    data class BackWithData(val key: String, val value: Any) : NavigationCommand
}
```

### 4. NavigationHandler

**Ubicación:** `app/src/main/java/com/gasguru/navigation/handler/NavigationHandler.kt`

Traduce `NavigationCommand` a llamadas reales de `NavController`.

```kotlin
class NavigationHandler(private val navController: NavController) {
    fun handle(command: NavigationCommand) {
        when (command) {
            is NavigationCommand.To -> when (val destination = command.destination) {
                is NavigationDestination.DetailStation -> {
                    if (destination.presentAsDialog) {
                        navController.navigateToDetailStationAsDialog(...)
                    } else {
                        navController.navigateToDetailStation(...)
                    }
                }
                // ... otros destinos
            }
            is NavigationCommand.Back -> {
                navController.popBackStack()
            }
            is NavigationCommand.BackTo -> {
                navController.popBackStack(
                    route = command.route,
                    inclusive = command.inclusive,
                )
            }
            is NavigationCommand.BackWithData -> {
                navController.setPreviousResult(
                    key = command.key,
                    value = command.value,
                )
                navController.popBackStack()
            }
        }
    }
}
```

### 5. LocalNavigationManager

**Ubicación:** `navigation/src/main/java/com/gasguru/navigation/manager/LocalNavigationManager.kt`

CompositionLocal que permite acceder al NavigationManager desde cualquier Composable.

```kotlin
val LocalNavigationManager = compositionLocalOf<NavigationManager> {
    error("NavigationManager not provided")
}
```

## Arquitectura de NavHosts

GasGuru utiliza una arquitectura de dos niveles de NavHosts:

```
GasGuruNavHost (ROOT NavHost)
├─ NavController ROOT ← NavigationHandler usa ESTE
├─ NavigationManager observado AQUÍ
└─ NavHost {
     ├─ OnboardingWelcomeScreen
     ├─ OnboardingFuelPreferencesScreen
     │
     ├─ NavigationBarHost ← Composable wrapper
     │  └─ NavigationBarScreen
     │     ├─ StationMapScreen (siempre visible)
     │     ├─ Bottom Bar (tabs)
     │     └─ NavHost NESTED {
     │          ├─ StationMapGraph.StationMapRoute (no-op)
     │          ├─ FavoriteGraph
     │          └─ ProfileScreen
     │       }
     │
     ├─ DetailStationScreen (dialog)
     ├─ DetailStationDialogRoute (dialog)
     │
     └─ RouteSearchGraph (graph con dialogs)
        ├─ RoutePlannerScreen (dialog)
        └─ SearchScreen (dialog)
  }
```

### ROOT NavHost

**Responsabilidades:**
- Navegación principal de la app
- Onboarding flow
- NavigationBar (entry point a tabs)
- Todos los dialogs globales

### NESTED NavHost

**Responsabilidades:**
- Solo tabs del bottom navigation
- Map (siempre renderizado, controlado por overlay)
- Favorites
- Profile

**¿Por qué el nested NavHost?**
El mapa tiene un bug conocido donde se recarga y falla cada vez que sale del backstack. Por eso, el mapa SIEMPRE está renderizado en NavigationBarScreen, incluso cuando navegamos a otras tabs. El overlay se usa para ocultar el mapa cuando NO estamos en el tab Map.

## Cómo Navegar

### Desde un Composable

```kotlin
@Composable
fun MyScreen() {
    val navigationManager = LocalNavigationManager.current

    Button(onClick = {
        navigationManager.navigateTo(
            destination = NavigationDestination.DetailStation(
                idServiceStation = 123,
                presentAsDialog = true,
            )
        )
    }) {
        Text("Ver detalle")
    }

    // Volver atrás
    IconButton(onClick = { navigationManager.navigateBack() }) {
        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
    }
}
```

### Desde un ViewModel

**Opción 1: Inyectar NavigationManager (recomendado)**

```kotlin
@HiltViewModel
class MyViewModel @Inject constructor(
    private val navigationManager: NavigationManager,
) : ViewModel() {

    fun onStationClicked(stationId: Int) {
        navigationManager.navigateTo(
            destination = NavigationDestination.DetailStation(
                idServiceStation = stationId,
                presentAsDialog = true,
            )
        )
    }
}
```

**Opción 2: Exponer evento de UI**

```kotlin
@HiltViewModel
class MyViewModel @Inject constructor() : ViewModel() {

    private val _uiEvents = Channel<UiEvent>()
    val uiEvents = _uiEvents.receiveAsFlow()

    fun onStationClicked(stationId: Int) {
        _uiEvents.trySend(UiEvent.NavigateToDetail(stationId))
    }
}

// En el Composable
LaunchedEffect(Unit) {
    viewModel.uiEvents.collect { event ->
        when (event) {
            is UiEvent.NavigateToDetail -> {
                navigationManager.navigateTo(
                    NavigationDestination.DetailStation(event.stationId)
                )
            }
        }
    }
}
```

## Pasar Datos Entre Pantallas

### Forward Navigation (A → B con datos)

Los datos se pasan como parámetros en el `NavigationDestination`:

```kotlin
// Navegar con datos
navigationManager.navigateTo(
    destination = NavigationDestination.DetailStation(
        idServiceStation = 123,
        presentAsDialog = true,
    )
)

// La pantalla destino recibe los datos vía Navigation
fun NavGraphBuilder.detailStationScreen() {
    composable<DetailStationRoute> { backStackEntry ->
        val args = backStackEntry.toRoute<DetailStationRoute>()
        DetailStationScreenRoute(idServiceStation = args.idServiceStation)
    }
}
```

### Back Navigation con Datos (B → A con resultado)

Usa `BackWithData` para pasar datos al volver:

```kotlin
// Pantalla B: Volver con datos
navigationManager.navigateTo(
    destination = NavigationDestination.BackWithData(
        key = NavigationKeys.ROUTE_PLANNER,
        value = RoutePlanArgs(
            originId = "origin123",
            destinationId = "dest456",
            destinationName = "Barcelona",
        ),
    )
)

// Pantalla A: Recibir datos
fun NavGraphBuilder.navigationBarHost() {
    composable<NavigationBarRoute> { navBackStackEntry ->
        // Observar el SavedStateHandle como Flow reactivo
        val routePlanArgs by navBackStackEntry.savedStateHandle
            .getStateFlow<RoutePlanArgs?>(
                key = NavigationKeys.ROUTE_PLANNER,
                initialValue = null
            )
            .collectAsStateWithLifecycle()

        LaunchedEffect(routePlanArgs) {
            if (routePlanArgs != null) {
                // Limpiar después de consumir
                navBackStackEntry.removePreviousResult(
                    key = NavigationKeys.ROUTE_PLANNER
                )
            }
        }

        NavigationBarScreenRoute(routePlanner = routePlanArgs)
    }
}
```

**⚠️ IMPORTANTE:** Usar `getStateFlow()` en lugar de `getPreviousResult()` para que sea reactivo. Si usas `getPreviousResult()`, solo se lee una vez y no se recompone cuando los datos cambian.

### Entre Features con Dialog Intermediario

Ejemplo: RoutePlanner → Search → RoutePlanner

```kotlin
// RoutePlannerScreen: Abrir búsqueda
navigationManager.navigateTo(NavigationDestination.Search)

// SearchScreen: Volver con lugar seleccionado
navigationManager.navigateTo(
    destination = NavigationDestination.BackWithData(
        key = NavigationKeys.SELECTED_PLACE,
        value = PlaceArgs(name = "Madrid", id = "place123"),
    )
)

// RoutePlannerScreen: Recibir lugar
fun NavGraphBuilder.routePlannerScreen() {
    dialog<RoutePlannerRoute> { navBackResult ->
        val result = navBackResult.getPreviousResult<PlaceArgs?>(
            NavigationKeys.SELECTED_PLACE
        )

        if (result != null) {
            navBackResult.removePreviousResult(NavigationKeys.SELECTED_PLACE)
        }

        RoutePlannerScreenRoute(selectedPlaceId = result)
    }
}
```

## Agregar Nueva Pantalla

### 1. Crear Route en el Feature

**Ubicación:** `feature/{feature-name}/navigation/`

```kotlin
// feature/my-feature/navigation/MyFeatureNavigation.kt
package com.gasguru.feature.my_feature.navigation

import kotlinx.serialization.Serializable

@Serializable
data object MyFeatureRoute

fun NavController.navigateToMyFeature(navOptions: NavOptions? = null) {
    navigate(MyFeatureRoute, navOptions)
}

fun NavGraphBuilder.myFeatureScreen() {
    composable<MyFeatureRoute> {
        MyFeatureScreenRoute()
    }
}
```

### 2. Agregar NavigationDestination

**Ubicación:** `navigation/src/main/java/com/gasguru/navigation/manager/NavigationDestination.kt`

```kotlin
sealed interface NavigationDestination {
    // ... otros destinos

    data object MyFeature : NavigationDestination
}
```

### 3. Manejar en NavigationHandler

**Ubicación:** `app/src/main/java/com/gasguru/navigation/handler/NavigationHandler.kt`

```kotlin
class NavigationHandler(private val navController: NavController) {
    fun handle(destination: NavigationDestination) {
        when (destination) {
            // ... otros casos

            is NavigationDestination.MyFeature -> {
                navController.navigateToMyFeature()
            }
        }
    }
}
```

### 4. Registrar en GasGuruNavHost

**Ubicación:** `app/src/main/java/com/gasguru/navigation/root/GasGuruNavHost.kt`

```kotlin
NavHost(navController = navController, startDestination = startDestination) {
    // ... otras pantallas
    myFeatureScreen()
}
```

### 5. Usar desde cualquier Screen

```kotlin
@Composable
fun AnyScreen() {
    val navigationManager = LocalNavigationManager.current

    Button(onClick = {
        navigationManager.navigateTo(NavigationDestination.MyFeature)
    }) {
        Text("Ir a Mi Feature")
    }
}
```

## Navigation Graphs

Para agrupar múltiples pantallas relacionadas:

```kotlin
// app/navigation/graphs/MyFeatureGraph.kt
@Serializable
data object MyFeatureGraph

fun NavGraphBuilder.myFeatureGraph() {
    navigation<MyFeatureGraph>(startDestination = MyFeatureRoute1) {
        myFeatureScreen1()
        myFeatureScreen2()
        myFeatureScreen3()
    }
}
```

**Cuándo usar graphs:**
- Agrupar pantallas de un flujo lógico (ej: onboarding, route planner)
- Facilitar `popUpTo(MyFeatureGraph)` para salir de todo el flujo
- Organización semántica

## Reglas y Mejores Prácticas

### ✅ DO

1. **Siempre usar NavigationManager para navegar**
   ```kotlin
   navigationManager.navigateTo(NavigationDestination.MyScreen)
   ```

2. **Routes en cada feature (descentralizado)**
   - Mantiene features independientes
   - Permite reutilizar features en otras apps

3. **Usar `getStateFlow()` para datos reactivos**
   ```kotlin
   val data by savedStateHandle.getStateFlow<MyData?>(key, null)
       .collectAsStateWithLifecycle()
   ```

4. **Limpiar datos después de consumir**
   ```kotlin
   LaunchedEffect(data) {
       if (data != null) {
           navBackStackEntry.removePreviousResult(key)
       }
   }
   ```

5. **Type-safe routes con @Serializable**
   ```kotlin
   @Serializable
   data class MyRoute(val id: String)
   ```

### ❌ DON'T

1. **No usar NavController directamente en screens**
   ```kotlin
   // ❌ MAL
   val navController = LocalNavController.current
   navController.navigate(...)

   // ✅ BIEN
   val navigationManager = LocalNavigationManager.current
   navigationManager.navigateTo(...)
   ```

2. **No pasar lambdas de navegación**
   ```kotlin
   // ❌ MAL
   MyScreen(onNavigate = { navController.navigate(...) })

   // ✅ BIEN
   MyScreen() // Usa LocalNavigationManager internamente
   ```

3. **No crear dependencias entre features**
   ```kotlin
   // ❌ MAL
   feature-a → feature-b

   // ✅ BIEN
   feature-a → navigation
   feature-b → navigation
   app → features + navigation
   ```

4. **No usar `getPreviousResult()` para datos que cambian**
   ```kotlin
   // ❌ MAL - Solo lee una vez
   val data = savedStateHandle.get<MyData>(key)

   // ✅ BIEN - Reactivo
   val data by savedStateHandle.getStateFlow<MyData?>(key, null)
       .collectAsStateWithLifecycle()
   ```

## Debugging

### Verificar flujo de navegación

1. **Agregar logs en NavigationHandler**
   ```kotlin
   fun handle(destination: NavigationDestination) {
       Log.d("Navigation", "Navigating to: $destination")
       when (destination) { ... }
   }
   ```

2. **Verificar backstack**
   ```kotlin
   navController.currentBackStackEntry?.destination?.route
   ```

3. **Verificar SavedStateHandle**
   ```kotlin
   LaunchedEffect(Unit) {
       savedStateHandle.keys().forEach { key ->
           Log.d("SavedState", "$key: ${savedStateHandle.get<Any?>(key)}")
       }
   }
   ```

## Arquitectura Modular

```
navigation/                     ← Módulo centralizado
├─ NavigationManager           ← Coordina navegación
├─ NavigationDestination       ← Destinos type-safe
├─ LocalNavigationManager      ← CompositionLocal
└─ NavigationExtensions        ← Helpers (setPreviousResult, etc)

feature/my-feature/            ← Feature independiente
└─ navigation/
   ├─ MyFeatureRoute           ← Route del feature
   ├─ navigateToMyFeature()    ← Extension function
   └─ myFeatureScreen()        ← NavGraphBuilder extension

app/                           ← Módulo app (orquestación)
├─ navigation/
│  ├─ handler/
│  │  └─ NavigationHandler    ← Traduce Destination → NavController
│  ├─ root/
│  │  └─ GasGuruNavHost       ← ROOT NavHost
│  └─ graphs/
│     └─ MyFeatureGraph       ← Graphs que coordinan features
```

## Resumen

- **Centralización funcional:** Toda navegación pasa por NavigationManager
- **Descentralización de contratos:** Cada feature define sus Routes
- **Type-safe:** Usa sealed interface + @Serializable
- **Reactivo:** SavedStateHandle con StateFlow
- **Testable:** NavigationManager es fácil de mockear
- **Escalable:** Agregar nuevas pantallas es simple y predecible
