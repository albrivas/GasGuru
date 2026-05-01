# MVI Effects — Patrón Channel<Effect> para one-shot events

## 1. State vs Event vs Effect

| Concepto | Tipo | Quién emite | Quién consume | Persiste |
|---|---|---|---|---|
| **State** | `StateFlow<UiState>` | VM | UI (`collectAsStateWithLifecycle`) | Sí — último valor siempre disponible |
| **Event** | función `handleEvent(...)` | UI (input usuario) | VM | No — se procesa y muere |
| **Effect** | `Flow<Effect>` respaldado por `Channel.BUFFERED` | VM | UI (`LaunchedEffect`) | **No** — entrega única, sin replay |

**Regla de pulgar**:
- ¿Hace falta que sobreviva a rotación y se siga viendo? → **State**.
- ¿Es input del usuario que el VM debe procesar? → **Event**.
- ¿Es algo que ocurre **una vez** (Snackbar, Intent, permiso, dialog, vibración)? → **Effect**.

## 2. Por qué `Channel.BUFFERED` y no `SharedFlow`

| | `Channel.BUFFERED` + `receiveAsFlow()` | `MutableSharedFlow(replay=0, extraBufferCapacity=N)` |
|---|---|---|
| Entrega | **Una sola vez** por item — lo consume UN colector y desaparece | Multi-suscriptor — todos los activos reciben cada item |
| Sin colector activo | El item **espera en buffer** | El item **se pierde** |
| Caso típico | 1 VM ↔ 1 Screen | Bus global (varios oyentes posibles) |

Para Effects de feature la relación es siempre 1↔1, así que `Channel.BUFFERED` es la elección correcta: si la pantalla está en background o rotando, el evento espera en buffer y se entrega al reanudar.

**Excepción legítima de SharedFlow**: `NavigationManager` (`navigation/src/commonMain/kotlin/com/gasguru/navigation/manager/NavigationManagerImpl.kt`) usa `MutableSharedFlow` porque es un bus global compartido por todo el grafo de navegación.

## 3. Por qué no `collectAsStateWithLifecycle` para Effects

`collectAsStateWithLifecycle` retiene el **último valor** en una `State<T>`. Con un Effect esto provoca que, tras una rotación, el composable re-procese el último Effect recibido (Snackbar dobletado, Intent doble, etc.). No hay forma limpia de "consumir y olvidar" sin meter banderas extra en el state.

La forma correcta es `LaunchedEffect(Unit)`:

```kotlin
LaunchedEffect(Unit) {
    effects.collect { effect ->
        when (effect) { /* ... */ }
    }
}
```

`LaunchedEffect(Unit)` vuelve a suscribirse al recomponer tras un cambio de configuración. El VM sobrevive la rotación y el `Channel` sigue activo — si había un Effect pendiente, se entrega una única vez.

## 4. Convención de naming y estructura

- **Archivo**: `<Feature>Effect.kt` en el mismo paquete que `<Feature>UiState.kt`.
- **Tipo**: `sealed class <Feature>Effect` con subtypes `data object` o `data class`.
- **ViewModel**:

```kotlin
private val _effects = Channel<<Feature>Effect>(Channel.BUFFERED)
val effects: Flow<<Feature>Effect> = _effects.receiveAsFlow()

private fun emitSomething() = viewModelScope.launch {
    _effects.send(<Feature>Effect.Something)
}
```

- **Screen** (composable raíz, el mismo que ya recibe `state` y `onEvent`):

```kotlin
@Composable
internal fun <Feature>Screen(
    state: <Feature>UiState,
    effects: Flow<<Feature>Effect>,
    onEvent: (<Feature>Event) -> Unit,
) {
    LaunchedEffect(Unit) {
        effects.collect { effect ->
            when (effect) {
                is <Feature>Effect.Something -> { /* side effect imperativo */ }
            }
        }
    }
    // ...
}
```

- **Llamada desde el composable raíz**:

```kotlin
<Feature>Screen(
    state = state,
    effects = viewModel.effects,
    onEvent = viewModel::handleEvent,
)
```

## 5. Referencia funcional: `StationMapEffect`

Implementación completa ya integrada y testeada:

- **Declaración**: `feature/station-map/src/main/java/com/gasguru/feature/station_map/ui/StationMapEffect.kt`
- **VM** (Channel + emisión desde `handleRouteError`): `feature/station-map/src/main/java/com/gasguru/feature/station_map/ui/StationMapViewModel.kt`
- **Screen** (colecta + `snackbarHostState.showSnackbar`): `feature/station-map/src/main/java/com/gasguru/feature/station_map/ui/StationMapScreen.kt`
- **Test con Turbine**: `feature/station-map/src/test/kotlin/com/gasguru/feature/station_map/ui/StationMapViewModelTest.kt`

## 6. Cuándo aplicar

- Mostrar Snackbar, Toast o Dialog one-shot.
- Lanzar `Intent` (compartir, abrir Maps, llamar).
- Pedir permiso runtime.
- Disparar in-app review.
- Refrescar Glance widget desde un VM tras una acción del usuario.
- Navegar con resultado / limpiar back-stack puntual cuando NavigationManager no encaja directamente.

## 7. Cuándo NO aplicar

- **Navegación normal** — usar `NavigationManager` directamente desde el VM (`navigationManager.navigateTo(destination)`). NavigationManager ya es un bus de eventos (MutableSharedFlow) y se comporta como un Effect de forma transparente. Añadir un `*Effect` intermedio crearía indirección redundante.
- **Estado persistente** que debe sobrevivir a rotación — usar `UiState`.
- **Error con botón de reintento visible** en pantalla — debe vivir en `UiState`, no en un Effect (un Effect desaparece al consumirse; el banner de error necesita permanecer).

## 8. Tests con Turbine — patrón mínimo

Dependencia en `libs.versions.toml`: `turbine` (ya declarada; ver usages en `:feature:station-map`).

```kotlin
@Test
@DisplayName("""
    GIVEN ...
    WHEN ...
    THEN effect is emitted
""")
fun test() = runTest {
    sut.effects.test {
        sut.handleEvent(SomeEvent)
        advanceUntilIdle()
        assertInstanceOf(FeatureEffect.Something::class.java, awaitItem())
    }
}
```
