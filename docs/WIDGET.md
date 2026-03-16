# Widget de gasolineras favoritas

Widget de pantalla de inicio que muestra las gasolineras favoritas del usuario con el precio del combustible de su vehículo principal.

## Funcionamiento

- Muestra las favoritas ordenadas por precio (más barato primero).
- Precio del combustible según el vehículo principal configurado en el perfil.
- Tap en una gasolinera → abre el detalle en la app.
- Sin distancia: el widget no calcula distancias (requeriría localización en background).

## Tamaños disponibles

Hay dos variantes del widget en el picker:

| Variante | Celdas | Clase receiver |
|----------|--------|----------------|
| Normal | 4×3 | `FavoriteStationsWidgetReceiver` |
| Pequeño | 4×2 | `FavoriteStationsWidgetSmallReceiver` |

Ambas variantes usan el mismo `FavoriteStationsWidget` y `FavoriteStationsWidgetContent`, y son redimensionables por el usuario (`resizeMode="horizontal|vertical"`).

## Módulo

`:feature:widget`

Clases principales:
- `FavoriteStationsWidget` — `GlanceAppWidget` que carga datos con `GetFavoriteStationsWithoutDistanceUseCase`
- `FavoriteStationsWidgetReceiver` — receiver del widget normal (4×3)
- `FavoriteStationsWidgetSmallReceiver` — receiver del widget pequeño (4×2)
- `StationSyncWorker` — `CoroutineWorker` de WorkManager que refresca los datos

## Refresco de datos (WorkManager)

Al añadir cualquiera de los dos widgets se programan dos trabajos:
1. **Inmediato** (`OneTimeWorkRequest`): refresca los datos nada más añadirlo.
2. **Periódico** (`PeriodicWorkRequest`): cada 30 minutos, con constraint de red (`NetworkType.CONNECTED`).

Nombre único del trabajo: `gasguru_station_sync`. Se usa `ExistingPeriodicWorkPolicy.KEEP` para evitar duplicados cuando hay varias instancias activas.

El trabajo periódico solo se cancela cuando **ambos** tipos de widget están inactivos. Cada receiver verifica en `onDisabled` que el otro tipo tampoco tenga instancias antes de cancelar.

La lógica de refresco al abrir la app (`SplashViewModel`) se mantiene sin cambios; ambas son independientes y complementarias.

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
