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
| Recomposición | En tiempo real | Solo al llamar `update()` |
| Fuentes custom | ✅ FontFamily desde recursos | ❌ Solo fuentes del sistema |
| Animaciones | ✅ | ❌ |
| Componentes | Cualquier Composable | Solo los soportados por Glance |

No puedes mezclar ambos sistemas: un composable de Compose no puede usarse dentro de Glance ni al revés.

---

## Estructura de un widget con Glance

### 1. GlanceAppWidget

La clase principal. Aquí defines la UI y cargas los datos:

```kotlin
class MyWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val data = loadData() // suspending, corre en un coroutine
        provideContent {
            MyWidgetContent(data = data)
        }
    }
}
```

`provideGlance` es una función suspendida, así que puedes cargar datos desde Room, DataStore o una API antes de llamar a `provideContent`. El contenido se renderiza una vez con los datos que tengas en ese momento.

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

## Actualización de datos sin la app abierta

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

```
onEnabled()  →  scheduleImmediateSync()  →  datos frescos al instante
             →  schedulePeriodicSync()   →  refresco cada 30 min

onDisabled() →  cancelUniqueWork()       →  limpieza al quitar el widget
```

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

## Múltiples tamaños del mismo widget

Para ofrecer varios tamaños en el selector de widgets (como hace Gmail con su widget 2×2 y 4×2), se necesita un receiver separado por tamaño, pero pueden compartir toda la lógica mediante una clase base:

```kotlin
// Toda la lógica aquí
abstract class BaseWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget = MyWidget()

    override fun onEnabled(context: Context) { /* scheduleSync */ }
    override fun onDisabled(context: Context) { /* cancelSync si no hay más */ }
}

// Receivers de una línea, solo para registrar distintos tamaños en el manifest
class LargeWidgetReceiver : BaseWidgetReceiver()
class SmallWidgetReceiver : BaseWidgetReceiver()
```

Cada receiver apunta a un XML de provider distinto con diferentes `targetCellWidth` / `targetCellHeight`. El widget en sí (`GlanceAppWidget`) y su contenido son exactamente los mismos.

---

## Resumen

| Concepto | Qué es | Cuándo usarlo |
|---|---|---|
| `GlanceAppWidget` | Define la UI y carga datos | Siempre, es el núcleo del widget |
| `GlanceAppWidgetReceiver` | Gestiona el ciclo de vida | Siempre, un receiver por tamaño |
| `AppWidgetProviderInfo` | Configuración del widget (XML) | Siempre, un XML por receiver |
| `GlanceTheme` | Theming de colores light/dark | Cuando quieres colores adaptativos |
| `previewLayout` | Preview en el picker (Android 12+) | Siempre que sea posible |
| `WorkManager` | Actualizaciones periódicas en background | Cuando el widget necesita datos frescos |
| Clase base abstract | Compartir lógica entre tamaños | Cuando tienes más de un tamaño |
