# Widgets en Android: guía completa con Glance

## ¿Qué es un widget de Android?

Un widget de pantalla de inicio (*App Widget*) es una vista reducida de tu aplicación que vive directamente en el launcher del usuario. A diferencia de una notificación o una actividad, el widget se renderiza dentro de otro proceso — el del launcher — lo que impone restricciones importantes sobre cómo se construye y se actualiza.

El sistema que usa Android para renderizar widgets se llama **RemoteViews**: una serialización de una jerarquía de vistas que el launcher infla en su propio proceso. Esto significa que no puedes usar cualquier View de Android — solo un subconjunto muy limitado de vistas está permitido (`TextView`, `ImageView`, `LinearLayout`, etc.), y operaciones como listeners de click tienen que codificarse como `PendingIntent`, no como lambdas normales.

Trabajar con RemoteViews directamente es tedioso y propenso a errores. Aquí es donde entra Glance.

---

## ¿Qué es Glance?

**Glance** es una librería de Jetpack que envuelve la API de RemoteViews con una sintaxis similar a Compose. En lugar de construir la jerarquía de vistas manualmente, describes la UI con funciones `@Composable`, y Glance se encarga de traducirlo a RemoteViews internamente.

```kotlin
// Sin Glance (RemoteViews puro)
val views = RemoteViews(context.packageName, R.layout.my_widget)
views.setTextViewText(R.id.title, "Hola")
views.setOnClickPendingIntent(R.id.root, pendingIntent)

// Con Glance
@Composable
fun MyWidget() {
    Text(
        text = "Hola",
        modifier = GlanceModifier.clickable(actionStartActivity<MainActivity>()),
    )
}
```

Lo importante es entender que **Glance no es Compose**. Aunque la sintaxis es parecida, son sistemas distintos:

| | Compose | Glance |
|---|---|---|
| Namespace | `androidx.compose.*` | `androidx.glance.*` |
| Renderizado | Canvas directo | RemoteViews (via XML) |
| Recomposición | En tiempo real | Reactiva via `collectAsState` (Glance 1.1+) o manual via `update()` |
| Fuentes custom | ✅ FontFamily desde recursos | ❌ Solo fuentes del sistema |
| Animaciones | ✅ | ❌ |
| Componentes | Cualquier Composable | Solo los soportados por Glance |

No puedes mezclar ambos sistemas: un composable de Compose no puede usarse dentro de Glance ni al revés.

---

## Estructura de un widget con Glance

### 1. GlanceAppWidget

La clase principal. Aquí defines la UI y cargas los datos.

**Patrón clásico (one-shot):** carga los datos una vez y renderiza. El widget permanece estático hasta que algo externo llame a `update()` o `updateAll()`.

```kotlin
class MyWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val data = loadData() // suspending — carga una sola vez
        provideContent {
            MyWidgetContent(data = data) // estático hasta el próximo update()
        }
    }
}
```

**Patrón reactivo (Glance 1.1+, recomendado):** la sesión de Glance mantiene la composición viva mientras el widget está en pantalla. Dentro de `provideContent` se puede usar `collectAsState()` de Compose runtime sobre cualquier `Flow` (Room, DataStore…). El widget recompone automáticamente cada vez que el Flow emite, sin ninguna llamada externa.

```kotlin
class MyWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        // Transformar el Flow FUERA del composable para no recrearlo en cada recomposición
        val dataFlow = repository.observeData().map { it.toUiModel() }

        provideContent {
            val data by dataFlow.collectAsState(initial = emptyList())
            MyWidgetContent(data = data) // recompone solo cuando el Flow emite
        }
    }
}
```

> **¿Cuándo aplica la reactividad?** Solo mientras la sesión está activa, es decir, cuando el launcher está en primer plano y el widget es visible. Si el dispositivo está bloqueado o la sesión ha expirado, los cambios en el Flow no llegarán hasta que la sesión se reactive o se llame a `update()` externamente (p.ej., desde WorkManager).

### 2. GlanceAppWidgetReceiver

El `BroadcastReceiver` que conecta el widget con el sistema Android. Gestiona el ciclo de vida:

```kotlin
class MyWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget = MyWidget()

    override fun onEnabled(context: Context) {
        // Se llama cuando se añade el PRIMER widget
    }

    override fun onDisabled(context: Context) {
        // Se llama cuando se elimina el ÚLTIMO widget
    }

    override fun onUpdate(context: Context, manager: AppWidgetManager, ids: IntArray) {
        // Se llama periódicamente si updatePeriodMillis > 0
    }
}
```

### 3. AppWidgetProviderInfo (XML)

Define las características del widget: tamaño, categoría, intervalo de actualización y preview:

```xml
<appwidget-provider xmlns:android="http://schemas.android.com/apk/res/android"
    android:minWidth="250dp"
    android:minHeight="200dp"
    android:targetCellWidth="4"
    android:targetCellHeight="3"
    android:resizeMode="horizontal|vertical"
    android:updatePeriodMillis="0"
    android:description="@string/widget_description"
    android:previewLayout="@layout/widget_preview" />
```

> `updatePeriodMillis` tiene un mínimo de 30 minutos y hace wakeup del dispositivo. Para actualizaciones más controladas lo mejor es dejarlo a `0` y usar WorkManager (ver más abajo).

### 4. AndroidManifest

El receiver se registra con un `intent-filter` para `APPWIDGET_UPDATE` y apunta al XML de configuración:

```xml
<receiver android:name=".MyWidgetReceiver" android:exported="true">
    <intent-filter>
        <action android:name="android.appwidget.action.APPWIDGET_UPDATE" />
    </intent-filter>
    <meta-data
        android:name="android.appwidget.provider"
        android:resource="@xml/my_widget_info" />
</receiver>
```

---

## Vistas en Glance

Glance ofrece un subconjunto de composables que mapean a RemoteViews soportadas:

```kotlin
// Layout
Column { ... }
Row { ... }
Box { ... }
LazyColumn { ... }  // scrollable vertical list

// Contenido
Text(text = "...")
Image(provider = ImageProvider(R.drawable.icon), ...)
Button(text = "Abrir", onClick = ...)

// Modificadores
GlanceModifier
    .fillMaxSize()
    .padding(16.dp)
    .background(GlanceTheme.colors.surface)
    .clickable(actionStartActivity<MainActivity>())
    .cornerRadius(12.dp)  // Android 12+
```

### Acciones (clicks)

Los clicks en widgets funcionan mediante `PendingIntent`. Glance los abstrae con funciones `action*`:

```kotlin
// Abrir una actividad
modifier = GlanceModifier.clickable(actionStartActivity<MainActivity>())

// Abrir con Intent personalizado (para pasar extras)
val intent = Intent(context, MainActivity::class.java).apply {
    putExtra("station_id", "123")
}
modifier = GlanceModifier.clickable(actionStartActivity(intent))

// Ejecutar un callback
modifier = GlanceModifier.clickable(actionRunCallback<MyActionCallback>())
```

### LazyColumn y scroll

A diferencia de Compose, en Glance el scroll solo está disponible en `LazyColumn`. No existe `LazyRow` ni scroll horizontal. El scroll se gestiona internamente por RemoteViews:

```kotlin
LazyColumn(modifier = GlanceModifier.fillMaxSize()) {
    items(stations) { station ->
        StationItem(station)
    }
}
```

---

## Theming en Glance

Glance tiene su propio sistema de theming, independiente del de Compose/Material3.

### Colores

Se usa `GlanceTheme` con `ColorProviders`, que recibe dos `ColorScheme` de Material 3 (uno para light y otro para dark). El sistema elige automáticamente según el tema del dispositivo:

```kotlin
object MyWidgetColorScheme {
    val colors = ColorProviders(
        light = lightColorScheme(
            surface = Color.White,
            onSurface = Color(0xFF212321),
            primary = Color(0xFF36CD5E),
        ),
        dark = darkColorScheme(
            surface = Color(0xFF1C1F1D),
            onSurface = Color(0xFFE8E8E8),
            primary = Color(0xFF36CD5E),
        ),
    )
}

// Aplicar en el widget
@Composable
fun MyWidgetContent() {
    GlanceTheme(colors = MyWidgetColorScheme.colors) {
        Box(modifier = GlanceModifier.background(GlanceTheme.colors.widgetBackground)) {
            Text(
                text = "Hola",
                style = TextStyle(color = GlanceTheme.colors.onSurface),
            )
        }
    }
}
```

Si usas `GlanceTheme` sin pasar `colors`, usará los colores dinámicos del sistema en Android 12+ y los colores de Material 3 por defecto en versiones anteriores.

### Estilos de texto

A diferencia de los colores, Glance **no gestiona los estilos de texto a través de GlanceTheme**. La guía oficial recomienda declararlos como variables top-level:

```kotlin
// ✅ Correcto: variables top-level
val WidgetStyleTitle = TextStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp)
val WidgetStyleBody = TextStyle(fontSize = 13.sp)

// Uso en composable
Text(text = name, style = WidgetStyleTitle.copy(color = GlanceTheme.colors.onSurface))
```

### Limitación de fuentes

Glance no puede cargar fuentes desde recursos (`res/font/`). El campo `fontFamily` de `TextStyle` acepta solo nombres de fuentes del sistema (`"sans-serif"`, `"monospace"`, etc.). Si tu app usa una fuente custom (como Inter), el widget usará la fuente del sistema como fallback.

---

## Preview en el picker

Cuando el usuario abre el selector de widgets, Android puede mostrar una preview del widget antes de añadirlo. Hay dos mecanismos:

### `previewLayout` (Android 12+, recomendado)

Se define un layout XML estático con Views normales (no Glance) que representa visualmente el widget con datos de ejemplo:

```xml
<!-- res/xml/my_widget_info.xml -->
<appwidget-provider ...
    android:previewLayout="@layout/widget_preview" />
```

```xml
<!-- res/layout/widget_preview.xml -->
<LinearLayout ...>
    <TextView android:text="Mi App" android:textStyle="bold" />
    <TextView android:text="Elemento de ejemplo" />
</LinearLayout>
```

Este layout se renderiza con el tema del launcher (light/dark), por lo que conviene definir colores en `res/values/colors.xml` y `res/values-night/colors.xml`.

### `previewImage` (legacy, Android 11 y anteriores)

Un drawable estático (PNG o vector) que se usa como preview. Al ser una imagen fija no se adapta al modo oscuro salvo que uses un selector de drawable:

```xml
<appwidget-provider ...
    android:previewImage="@drawable/widget_preview_image" />
```

Para soportar ambas versiones se pueden declarar los dos atributos: `previewLayout` tiene prioridad en Android 12+, `previewImage` actúa de fallback.

---

## Actualización de datos

### Cambios locales mientras la app está en uso

Con el patrón reactivo (Glance 1.1+), cualquier cambio en Room o DataStore que se propague a través de un `Flow` recompone el widget automáticamente, sin necesidad de coordinación externa. Esto cubre todos los casos en que el usuario interactúa con la app mientras el launcher está activo (añadir/eliminar favoritos, cambiar configuración, etc.).

No se necesita WorkManager, `updateAll()` ni ningún manager adicional para este tipo de actualizaciones. La combinación `provideContent + collectAsState` lo resuelve de forma nativa.

### Cambios de red sin la app abierta

Este es uno de los puntos más importantes y delicados de los widgets. El widget necesita datos frescos aunque el usuario no haya abierto la app en horas.

### ¿Por qué no usar `updatePeriodMillis`?

El atributo `updatePeriodMillis` del XML del provider llama a `onUpdate()` del receiver periódicamente, pero tiene serias limitaciones:
- El mínimo es **30 minutos** (Android ignora valores menores).
- Hace **wakeup del dispositivo** aunque esté en Doze, lo cual gasta batería.
- No tiene constraints (no puede esperar a tener red, por ejemplo).

### WorkManager: la solución correcta

WorkManager es la herramienta recomendada para trabajo en background que necesita ejecutarse de forma periódica y fiable:

```kotlin
// Trabajo inmediato al añadir el widget
val immediateRequest = OneTimeWorkRequestBuilder<SyncWorker>()
    .setConstraints(Constraints(requiredNetworkType = NetworkType.CONNECTED))
    .build()

// Trabajo periódico cada 30 minutos
val periodicRequest = PeriodicWorkRequestBuilder<SyncWorker>(30, TimeUnit.MINUTES)
    .setConstraints(Constraints(requiredNetworkType = NetworkType.CONNECTED))
    .build()

WorkManager.getInstance(context).enqueueUniquePeriodicWork(
    uniqueWorkName = "my_widget_sync",
    existingPeriodicWorkPolicy = ExistingPeriodicWorkPolicy.KEEP,
    request = periodicRequest,
)
```

El worker llama a `GlanceAppWidgetManager` para actualizar el widget con los nuevos datos:

```kotlin
class SyncWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val manager = GlanceAppWidgetManager(applicationContext)
        val ids = manager.getGlanceIds(MyWidget::class.java)
        ids.forEach { id ->
            MyWidget().update(applicationContext, id)
        }
        return Result.success()
    }
}
```

### Ciclo de vida del worker

El worker de sync de datos es una responsabilidad de la **app**, no del widget. Se programa en `Application.onCreate()` para que corra independientemente de si el usuario tiene widgets activos:

```kotlin
// En Application.onCreate()
WorkManager.getInstance(this).enqueueUniquePeriodicWork(
    "my_widget_sync",
    ExistingPeriodicWorkPolicy.KEEP,  // no resetea el timer en cada apertura
    PeriodicWorkRequestBuilder<SyncWorker>(30, TimeUnit.MINUTES)
        .setConstraints(Constraints(requiredNetworkType = NetworkType.CONNECTED))
        .build(),
)
```

Con el patrón reactivo de Glance 1.1+ (`collectAsState`), el widget se recompone automáticamente cuando el worker actualiza Room y la sesión está activa. Si la sesión no está activa, el worker llama a `updateAll()` para iniciar una nueva sesión con los datos frescos.

**Antipatrón a evitar:** programar el sync desde `onEnabled`/`onDisabled` del receiver. Esto acopla la frecuencia de actualización de datos al ciclo de vida del widget — sin widget, no hay sync.

Si la app tiene varios tamaños de widget (varios receivers), hay que asegurarse de cancelar el trabajo solo cuando **todos** los widgets estén inactivos:

```kotlin
override fun onDisabled(context: Context) {
    super.onDisabled(context)
    val manager = AppWidgetManager.getInstance(context)
    val largeIds = manager.getAppWidgetIds(ComponentName(context, LargeWidgetReceiver::class.java))
    val smallIds = manager.getAppWidgetIds(ComponentName(context, SmallWidgetReceiver::class.java))
    if (largeIds.isEmpty() && smallIds.isEmpty()) {
        WorkManager.getInstance(context).cancelUniqueWork("my_widget_sync")
    }
}
```

### Doze Mode y limitaciones de background

Android tiene mecanismos agresivos de ahorro de batería que afectan a los workers:

- **Doze Mode**: cuando el dispositivo lleva tiempo en reposo y con batería, Android congela los jobs en background. WorkManager respeta estos límites y retrasa la ejecución hasta que el dispositivo salga de Doze.
- **App Standby Buckets**: apps que el usuario no usa frecuentemente tienen cuotas de ejecución en background más restrictivas.

El resultado práctico: si el teléfono lleva horas sin usarse, los precios del widget pueden estar desactualizados. Esto es un comportamiento esperado y aceptable — la alternativa (ignorar Doze) consumiría batería innecesariamente.

---

## Tamaños y responsive: dos enfoques

Hay dos formas distintas de gestionar el tamaño de un widget. No son excluyentes, pero responden a necesidades diferentes.

---

### Enfoque A — Widget responsivo con `LocalSize` (recomendado)

Un solo widget en el picker. El usuario lo añade y puede redimensionarlo libremente pulsando largo. La UI se adapta al tamaño disponible en cada momento.

Glance expone el tamaño actual del widget mediante `LocalSize.current`, que devuelve un `DpSize`. Puedes usarlo en cualquier punto del composable para decidir qué mostrar:

```kotlin
@Composable
fun MyWidgetContent(stations: List<StationModel>) {
    val size = LocalSize.current

    when {
        size.height < 150.dp -> CompactContent(stations)   // caben 1-2 elementos
        size.height < 250.dp -> MediumContent(stations)    // caben 3-4 elementos
        else                 -> FullContent(stations)       // lista completa
    }
}
```

Para que Glance llame a `provideGlance` cada vez que el usuario redimensiona el widget, hay que declarar los tamaños soportados en el `GlanceAppWidget`:

```kotlin
class MyWidget : GlanceAppWidget() {

    // Declara los breakpoints de tamaño que tu UI soporta
    override val sizeMode = SizeMode.Responsive(
        setOf(
            DpSize(width = 250.dp, height = 110.dp),  // pequeño
            DpSize(width = 250.dp, height = 200.dp),  // mediano
            DpSize(width = 250.dp, height = 300.dp),  // grande
        )
    )

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val dataFlow = repository.observeData()
        provideContent {
            val data by dataFlow.collectAsState(initial = emptyList())
            MyWidgetContent(data)  // lee LocalSize.current internamente
        }
    }
}
```

**`SizeMode`** controla cuándo se llama a `provideGlance`:

| Modo | Comportamiento |
|------|---------------|
| `SizeMode.Single` (default) | Se llama una sola vez con el tamaño mínimo del XML. No responde a cambios de tamaño. |
| `SizeMode.Exact` | Se llama cada vez que cambia el tamaño exacto. Un `provideGlance` por tamaño → más llamadas, más RemoteViews generadas. |
| `SizeMode.Responsive(sizes)` | Se llama una vez por cada breakpoint declarado. Glance elige el RemoteViews más adecuado según el tamaño real. Recomendado. |

Con `SizeMode.Responsive`, Glance genera un conjunto de RemoteViews en el arranque (una por breakpoint) y el launcher elige cuál mostrar según el espacio disponible — sin llamadas adicionales al redimensionar.

**Configuración del XML** con `resizeMode` habilitado:

```xml
<appwidget-provider
    android:minWidth="110dp"
    android:minHeight="110dp"
    android:targetCellWidth="2"
    android:targetCellHeight="2"
    android:resizeMode="horizontal|vertical"
    android:maxResizeWidth="500dp"
    android:maxResizeHeight="500dp" />
```

Un solo receiver, un solo XML, una sola entrada en el picker.

---

### Enfoque B — Múltiples variantes en el picker

Varias entradas distintas en el selector de widgets (como hace Gmail con "Gmail pequeño" y "Gmail grande"). Cada variante tiene un tamaño fijo predeterminado, aunque sigan siendo redimensionables.

Se necesita un receiver separado por variante, pero pueden compartir toda la lógica mediante una clase base:

```kotlin
// Toda la lógica aquí
abstract class BaseWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget = MyWidget()
}

// Receivers de una línea, solo para registrar distintas variantes en el manifest
class LargeWidgetReceiver : BaseWidgetReceiver()
class SmallWidgetReceiver : BaseWidgetReceiver()
```

Cada receiver apunta a un XML de provider distinto con diferentes `targetCellWidth` / `targetCellHeight`:

```xml
<!-- widget_large_info.xml -->
<appwidget-provider android:targetCellWidth="4" android:targetCellHeight="3" ... />

<!-- widget_small_info.xml -->
<appwidget-provider android:targetCellWidth="4" android:targetCellHeight="2" ... />
```

El `GlanceAppWidget` y su contenido son los mismos en ambas variantes. La diferencia es solo el tamaño por defecto al añadirlo.

**Cuándo usar este enfoque:** cuando quieres que el usuario elija explícitamente entre layouts conceptualmente distintos (p.ej. un widget de solo precio vs. uno con lista de gasolineras), no solo tamaños diferentes del mismo contenido.

---

### Comparativa

| | Enfoque A — Responsivo | Enfoque B — Variantes en picker |
|---|---|---|
| Entradas en el picker | 1 | N (una por variante) |
| El usuario ajusta el tamaño | Sí, pulsando largo | Sí, pero el punto de partida es fijo |
| UI se adapta al tamaño | ✅ Con `LocalSize` + `SizeMode.Responsive` | Solo si se implementa manualmente |
| Complejidad | Baja (un receiver, un XML) | Media (un receiver + XML por variante) |
| Cuándo usarlo | Contenido homogéneo en distintos tamaños | Layouts conceptualmente distintos |

---

## Resumen

| Concepto | Qué es | Cuándo usarlo |
|---|---|---|
| `GlanceAppWidget` | Define la UI y carga datos | Siempre, es el núcleo del widget |
| `GlanceAppWidgetReceiver` | Gestiona el ciclo de vida | Siempre, un receiver por variante |
| `AppWidgetProviderInfo` | Configuración del widget (XML) | Siempre, un XML por receiver |
| `collectAsState` en `provideContent` | Reactividad nativa (Glance 1.1+) | Cuando los datos cambian desde la app mientras el launcher está visible |
| `SizeMode.Responsive` + `LocalSize` | UI adaptativa al tamaño del widget | Widget responsivo con un solo receiver |
| `GlanceTheme` | Theming de colores light/dark | Cuando quieres colores adaptativos |
| `previewLayout` | Preview en el picker (Android 12+) | Siempre que sea posible |
| `WorkManager` | Sincronización periódica en background | Cuando el widget necesita datos de red aunque la app esté cerrada |
| Clase base abstract | Compartir lógica entre variantes | Cuando tienes más de una variante en el picker |
