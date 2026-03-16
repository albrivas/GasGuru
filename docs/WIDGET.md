# Widget de gasolineras favoritas

Widget de pantalla de inicio que muestra las gasolineras favoritas del usuario con el precio del combustible de su vehículo principal.

## Funcionamiento

- Muestra las favoritas ordenadas por precio (más barato primero).
- Precio del combustible según el vehículo principal configurado en el perfil.
- Tap en una gasolinera → abre el detalle en la app.
- Sin distancia: el widget no calcula distancias (requeriría localización en background).

## Tamaño y responsive

Una sola entrada en el picker. El usuario puede redimensionarlo libremente pulsando largo sobre él.

El widget usa `SizeMode.Responsive` con dos breakpoints:

| Breakpoint | Tamaño | Modo |
|------------|--------|------|
| Compacto | 250×110dp | Header y items con padding reducido |
| Completo | 250×200dp | Layout estándar |

`LocalSize.current` dentro de `provideContent` determina el modo activo (`isCompact = height < 150.dp`). Glance genera un conjunto de RemoteViews por breakpoint al arrancar; el launcher elige cuál mostrar según el espacio disponible, sin llamadas adicionales al redimensionar.

Receiver: `FavoriteStationsWidgetReceiver`

## Módulo

`:feature:widget`

Clases principales:
- `FavoriteStationsWidget` — `GlanceAppWidget` reactivo que observa el `Flow` de favoritos con `collectAsState` dentro de `provideContent`
- `FavoriteStationsWidgetReceiver` — receiver del widget normal (4×3)
- `FavoriteStationsWidgetSmallReceiver` — receiver del widget pequeño (4×2)

## Refresco de datos

### Patrón reactivo (Glance 1.1+)

`FavoriteStationsWidget` usa el patrón reactivo de Glance: en `provideGlance`, el `Flow` de favoritos y precios se transforma fuera del composable y se colecta dentro de `provideContent` con `collectAsState`. La sesión de Glance se mantiene viva mientras el widget está en pantalla, por lo que cualquier cambio en Room (añadir/eliminar favorita, actualización de precios) provoca una recomposición automática del widget.

```kotlin
override suspend fun provideGlance(context: Context, id: GlanceId) {
    val stationsFlow = getFavoriteStationsWithoutDistanceUseCase().map { ... }
    provideContent {
        val stations by stationsFlow.collectAsState(initial = emptyList())
        FavoriteStationsWidgetContent(stations = stations)
    }
}
```

### Sincronización periódica de precios (WorkManager — nivel app)

La sincronización periódica de precios es una responsabilidad de la app, no del widget. Se programa en `GasGuruApplication.onCreate()` y corre independientemente de si el usuario tiene o no un widget activo.

`StationSyncWorker` (en `:app`) actualiza los precios en Room y llama a `updateAll()` para garantizar el refresco incluso cuando la sesión del widget no está activa (dispositivo bloqueado, Doze mode, sesión expirada). Si la sesión está activa, el `collectAsState` ya habrá recompuesto el widget al detectar el cambio en Room.

- Intervalo: cada 30 minutos, con constraint de red (`NetworkType.CONNECTED`)
- Nombre único del trabajo: `gasguru_station_sync`
- Política: `ExistingPeriodicWorkPolicy.KEEP` — no se resetea el timer en cada arranque de la app

## Theming

El widget usa el sistema de theming de Glance siguiendo la guía oficial de Android:

- **Colores** (`WidgetColorScheme.kt`): dos `ColorScheme` de Material 3 (`lightColorScheme` / `darkColorScheme`) mapeando los colores de GasGuru (`core.uikit.theme`) a los roles estándar (`surface`, `onSurface`, `onSurfaceVariant`, etc.). Se pasan a `GlanceTheme(colors = WidgetColorScheme.colors)`.
- **Estilos de texto** (`WidgetTextStyles.kt`): variables top-level de `androidx.glance.text.TextStyle` que replican los tamaños y pesos de `GasGuruTypography`. Se aplican con `.copy(color = GlanceTheme.colors.*)`.
- **Colores de acento** (`WidgetColors.kt`): `ColorProvider` para los chips de precio (verde/naranja/rojo), con variantes sólidas (texto) y al 16% alpha (fondo), replicando el diseño de `StatusChip`.

> **Limitación**: Glance no soporta fuentes personalizadas desde recursos. El font family Inter no puede usarse en el widget — se usa la tipografía del sistema (sans-serif por defecto).

## Preview en el picker

Cada variante tiene su propio layout de preview estático (Views normales, no Glance) declarado con `android:previewLayout` en el XML del provider:

- `res/layout/widget_preview.xml` — preview del widget normal
- `res/layout/widget_preview_small.xml` — preview del widget pequeño

Los colores del preview se definen en `res/values/colors.xml` (y `values-night/` para dark mode).

## Limitaciones conocidas

- **Sin distancia**: no se puede obtener la localización en background de forma fiable.
- **Doze mode**: en batería optimizada, Android puede retrasar el trabajo periódico hasta que el dispositivo salga de Doze. Los precios pueden estar desactualizados si el teléfono lleva horas sin usarse.
