# CLAUDE.md — GasGuru

## Módulos y reglas
- Permitido: `feature → core`, `app → features/core`
- Prohibido: `feature ↔ feature`, `UI ↔ data sources directos`
- Navegación: pasar IDs y cargar datos en ViewModel vía UseCases
- Cuando una pantalla recibe un ID por navegación, leerlo en el ViewModel con `savedStateHandle.toRoute<MyRoute>().myParam` en el bloque `init {}`. La ruta debe ser una `@Serializable data class`. Inyectar `SavedStateHandle` vía Koin con `get()`. En tests, pasar `SavedStateHandle(mapOf("myParam" to value))`. Añadir `unitTests.isReturnDefaultValues = true` en el `testOptions` del módulo para evitar errores de métodos de Android no mockeados.

## Compose & Estado
- VM expone: `UiState` sellada + `events`
- Usar `collectAsStateWithLifecycle`, evitar `!!`
- Componentes deben ser `@Stable` cuando aplique
- El modelo de cada componente va siempre en un archivo separado (`NombreModel.kt`) en el mismo paquete que el composable

## Theming
- Usar siempre `GasGuruColors` para Light y Dark
- Prohibido hardcodear colores (usar solo desde el tema)
- Mantener coherencia visual entre modos claro y oscuro

## Code
- Añadir nombre de los argumentos. Ejemplo: `getLocation(location = loc)
- Eliminar imports sin usar
- **Nombres de variables descriptivos**: evitar abreviaciones de una sola letra o nombres genéricos (ej: `v`, `it`, `x`). Usar nombres que expresen el dominio claramente (ej: `currentVehicle`, `selectedFuelType`, `tankCapacityLitres`)

## Tests
- Toda clase nueva o funcionalidad nueva debe tener tests en el mismo paso en que se crea
- Si se modifica una clase que tiene tests asociados, actualizar los tests para reflejar los cambios en el mismo paso
- Cada feature nueva debe alcanzar un mínimo del **65% de cobertura** de código (líneas + ramas) antes de hacer merge
- **Usar siempre JUnit5** (`org.junit.jupiter.api`): `@Test`, `@BeforeEach`, `@AfterEach`, `Assertions.*`. Nunca JUnit4 (`org.junit.Test`, `org.junit.Before`).
- **Toda clase de test debe llevar `@DisplayName`** en la clase y en cada método `@Test`


- Siempre que añadas dependencias deben ir en el libs.versions.toml y luego referenciarlas en los build.gradle que necesiten esas depdencias. Lo mismo para los plugins. Todas las dependencias se centralizan en lisb.versions.toml
- Usar trailling comma siempre que se pueda

## Documentacion
- Al terminar cualquier tarea o funcionalidad, revisar si hay documentacion existente que deba actualizarse
- Consultar la tabla de documentacion al final de este archivo para identificar los docs afectados
- Si se modifica un archivo que tiene documentacion asociada, actualizar el doc correspondiente en el mismo paso

| Tema | Descripción |
|------|-------------|
| [Adding Fuel Types](docs/ADDING_FUEL_TYPE.md) | Guía para añadir nuevos tipos de combustible |
| [Database Migrations](docs/DATABASE_MIGRATIONS.md) | Historial de migraciones de la DB: qué cambió y por qué en cada versión |
| [CI/CD](docs/CICD.md) | Integración y despliegue continuo |
| [Dependency Injection](docs/DEPENDENCY_INJECTION.md) | Arquitectura DI con Koin, equivalencias con Hilt, guía para añadir dependencias |
| [GitFlow](docs/GITFLOW.md) | Estrategia de branching y flujo de trabajo |
| [JaCoCo](docs/JACOCO.md) | Reportes de cobertura de código |
| [Navigation](docs/NAVIGATION.md) | Arquitectura de navegación |
| [Obfuscation](docs/OBFUSCATION.md) | Configuración de ofuscación de código |
| [Price Alerts](docs/PRICE_ALERTS.md) | Funcionalidad de alertas de precio |
| [Recomposition Optimizations](docs/RECOMPOSITION_OPTIMIZATIONS.md) | Optimizaciones de recomposición en Compose |
| [Room KMP](docs/ROOM_KMP.md) | Guía de migración de core/database a Room KMP |
| [Testing](docs/TESTING.md) | Estrategia y guías de testing |
| [UI Mappers](docs/UI_MAPPERS.md) | Arquitectura de mappers UI |
| [KMP Migration Plan](docs/KMP_MIGRATION.md) | Plan de migración a Kotlin Multiplatform: fases, checklist y estrategia de testing |
| [KMP Phase 0](docs/KMP_PHASE0.md) | Explicación detallada de la infraestructura de build creada en Phase 0: convention plugins, source sets, dependencias |
| [KMP Phase 1](docs/KMP_PHASE1.md) | Migración de :core:model a commonMain: cambios de plugin, sustituciones de APIs JVM, tests en commonTest |
| [KMP Phase 3](docs/KMP_PHASE3.md) | Migración de :core:database a Room KMP: @ConstructedBy, SQLiteConnection API, Moshi→kotlinx-serialization, DI split |
| [KMP Phase 5C](docs/KMP_PHASE5C.md) | Migración de :core:network a KMP con Ktor y simplificación de :mocknetwork: limpieza API gobierno, KtorModule, routesPlugin expect/actual, mock JSON Supabase |
| [KMP Phase 6A](docs/KMP_PHASE6A.md) | Migración de :core:ui a CMP: mappers commonMain, InAppReview expect/actual, strings composeResources, eliminación de runBlocking getString |
| [KMP Phase 6C](docs/KMP_PHASE6C.md) | Migración de :core:components a CMP: primer ViewModel en commonMain, koin-compose-viewmodel, lifecycle KMP, strings composeResources |
| [KMP Phase 7A](docs/KMP_PHASE7A.md) | Migración de :feature:onboarding a CMP: decisiones sobre FuelTypeMapper KMP, locale values-es, patrón de eliminación de helpers Android-only de un solo consumidor |
| [KMP Phase 7B](docs/KMP_PHASE7B.md) | Migración de :feature:profile a CMP: fix test androidTest con R.string → getCmpString CMP, extensión BaseTest con vararg formatArgs |
| [KMP Phase 7C](docs/KMP_PHASE7C.md) | Migración de :feature:favorite-list-station a CMP: onOpenLocationSettings como lambda desde NavigationBarScreen, koin-compose-viewmodel |
| [KMP Phase 7D](docs/KMP_PHASE7D.md) | Migración de :feature:search a CMP: ConfigureDialogSystemBars como expect/actual en :core:ui, limpieza de deps muertas |
| [KMP Phase 7E](docs/KMP_PHASE7E.md) | Migración de :feature:detail-station a CMP: Coil 3 KMP, expect/actual para share/maps/notificaciones dentro del módulo, kotlin.time.Clock, ConstraintLayout→Row |
| [KMP Phase 7F](docs/KMP_PHASE7F.md) | Migración de :feature:route-planner a CMP: eliminación de deps muertas (places, coroutines-play), sin expect/actual — sin APIs Android-only |
| [KMP Phase 7G](docs/KMP_PHASE7G.md) | Migración de :feature:station-map a CMP: Google Maps → PlatformMapView expect/actual, GeoBounds reemplaza LatLngBounds, FilterUiState.fromTranslatedString eliminado |
| [KMP Phase 7H](docs/KMP_PHASE7H.md) | Migración de :feature:vehicle a CMP: sin expect/actual, unificación del alias cmpStringResource, eliminación de mockk sin usar |
| [KMP Phase 8A](docs/KMP_PHASE8A.md) | BuildKonfig en :core:supabase: SupabaseModule unificado en commonMain, SupabaseSecrets generado por BuildKonfig, analytics iOS no-op (sin cinterop Mixpanel) |
| [KMP Phase 8B](docs/KMP_PHASE8B.md) | Creación de :composeApp (KMP framework con App() + MainViewController) e :iosApp (Xcode project): armazón iOS mínimo que compila |
| [KMP Phase 8C](docs/KMP_PHASE8C.md) | App shell (splash + GasGuruApp + NavHost + bottom bar) movido de :app a :composeApp/commonMain. Lambda onOpenLocationSettings desde MainActivity. StringResource en TopLevelRoutes. SplashViewModel con kotlin.time.Clock |
| [KMP Phase 8D](docs/KMP_PHASE8D.md) | Inicialización Koin desde iOS: KoinInit.kt, MainViewController real, fullScreenDialogProperties expect/actual, LocalAnalyticsHelper movido a core.ui/commonMain, rememberInAppReviewManager expect/actual |
| [KMP Phase 8E](docs/KMP_PHASE8E.md) | Limpieza :app: initKoin() Android movido a composeApp/androidMain (espejo iOS), SessionAnalyticsExt a commonMain, deps redundantes eliminadas de :app/build.gradle.kts |
| [KMP Phase 9A](docs/KMP_PHASE9A.md) | iOS Foundation APIs: NetworkMonitorIos (NWPathMonitor vía callbackFlow), GeocoderAddressIos (CLGeocoder + campos CLPlacemark). Primer patrón callbackFlow + awaitClose sobre APIs nativas Apple |
| [KMP Phase 9B](docs/KMP_PHASE9B.md) | iOS LocationTracker + LocationPermission: CLLocationManager + delegate NSObject Kotlin/Native, LocalOpenLocationSettings CompositionLocal (elimina prop drilling x8 archivos), NSLocationWhenInUseUsageDescription en Info.plist |
| [KMP Phase 9C](docs/KMP_PHASE9C.md) | iOS MapKit + UIKitView: MKMapViewDelegateProtocol desde Kotlin/Native, StationAnnotation, polyline con allocArray cinterop, extension functions de categorías ObjC, diff idempotente de annotations |
| [KMP Phase 9D](docs/KMP_PHASE9D.md) | iOS PlacesRepository: Google Places SDK via CocoaPods, BuildKonfig para API key, GMSPlacesClient cinterop, pod en iosApp/Podfile, GOOGLE_API_KEY Koin qualifier real |
| [KMP Phase 9E](docs/KMP_PHASE9E.md) | iOS detail station platform actions: UIAlertController action sheet para Maps (Apple/Google/Waze con canOpenURL), UIActivityViewController para share, UNUserNotificationCenter con branching de status (NotDetermined → request, Denied → openSettings), helper topMostViewController, LSApplicationQueriesSchemes en Info.plist |
| [KMP Phase 9F](docs/KMP_PHASE9F.md) | iOS in-app review con StoreKit: SKStoreReviewController.requestReviewInScene, fire-and-forget onReviewCompleted, refactor InAppReviewManager de expect/actual class a interface en commonMain |
| [KMP Phase 9G](docs/KMP_PHASE9G.md) | iOS analytics Mixpanel: Swift bridge via `@ObjCName` en interfaz Kotlin → protocolo ObjC → clase Swift implementa con Mixpanel-swift 6.4. `KoinInit` acepta `AnalyticsHelper` como parámetro. Debug usa `LogAnalyticsHelperIos`, release usa Mixpanel. |
| [KMP Phase 9H](docs/KMP_PHASE9H.md) | iOS push notifications OneSignal: Swift bridge (mismo patrón 9G), pod `OneSignalXCFramework >= 5.2.9`, `NotificationService.init()` → `start()`, `initKoin()` devuelve `DeepLinkStateHolder`, primer `iosApp.entitlements` del proyecto |
| [KMP Phase 9J](docs/KMP_PHASE9J.md) | iOS background sync: SyncManager.execute() en KoinInit (fix bug alertas offline), IosBridge.refreshStations + BGTaskScheduler (paridad StationSyncWorker), patrón Dispatchers.Main en tests KMP |
| [KMP Phase 10A](docs/KMP_PHASE10A.md) | `jvm()` en convention plugins: guard composeApp, actuals no-op en jvmMain (13 expects), api coroutines/navigation, lifecycle-runtime-compose, detekt setSource ampliado |
| [KMP Phase 10B](docs/KMP_PHASE10B.md) | Tests de UI CMP headless en jvmTest: `runComposeUiTest` sin emulador, patrón de migración de BaseTest, resolución de strings en commonTest, exclusión por módulo |
| [iOS Bridge](docs/IOS_BRIDGE.md) | Contrato único Swift → KMP: `IosBridge` en `composeApp/iosMain`, cómo añadir métodos, cuándo usarlo vs otros patrones |
| [Widget](docs/WIDGET.md) | Widget de pantalla de inicio con gasolineras favoritas: arquitectura Glance + WorkManager, ciclo de vida y limitaciones |
| [Analytics](docs/ANALYTICS.md) | Sistema de analíticas con Mixpanel: arquitectura, catálogo de eventos, uso en ViewModels y Composables |
| [Git Worktree](docs/GIT_WORKTREE.md) | Cómo crear y eliminar worktrees, el problema de local.properties y cómo resolverlo con symlinks |
| [MVI Effects](docs/MVI_EFFECTS.md) | Patrón Channel&lt;Effect&gt; para one-shot events: snackbars, intents, dialogs — cuándo usarlo vs State y cuándo NO (navegación normal); Screen UI state vs UI element state (visibilidad de sheets/dialogs: VM vs `remember` local) |

## Orquestación del Flujo de Trabajo

### 1. Modo Planificación por Defecto
- Entra en modo planificación para CUALQUIER tarea no trivial (más de 3 pasos o decisiones arquitectónicas)
- Si algo sale mal, PARA y vuelve a planificar de inmediato; no sigas forzando
- Usa el modo planificación para los pasos de verificación, no solo para la construcción
- Escribe especificaciones detalladas por adelantado para reducir la ambigüedad

### 2. Estrategia de Subagentes
- Usa subagentes con frecuencia para mantener limpia la ventana de contexto principal
- Delega la investigación, exploración y análisis paralelo a subagentes
- Para problemas complejos, dedica más capacidad de cómputo mediante subagentes
- Una tarea por subagente para una ejecución focalizada

### 3. Bucle de Automejora
- Tras CUALQUIER corrección del usuario: actualiza tasks/lessons.md con el patrón
- Escribe reglas para ti mismo que eviten el mismo error
- Itera implacablemente sobre estas lecciones hasta que la tasa de errores disminuya
- Revisa las lecciones al inicio de la sesión para el proyecto correspondiente

### 4. Verificación antes de Finalizar
- Nunca marques una tarea como completada sin demostrar que funciona
- Compara la diferencia (diff) de comportamiento entre la rama principal y tus cambios cuando sea relevante
- Pregúntate: "¿Aprobaría esto un ingeniero senior (Staff Engineer)?"
- Ejecuta tests, comprueba los logs y demuestra la corrección del código

### 5. Exige Elegancia (Equilibrado)
- Para cambios no triviales: haz una pausa y pregunta "¿hay una forma más elegante?"
- Si un arreglo parece un parche (hacky): "Sabiendo todo lo que sé ahora, implementa la solución elegante"
- Omite esto para arreglos simples y obvios; no hagas sobreingeniería
- Cuestiona tu propio trabajo antes de presentarlo

### 6. Corrección de Errores Autónoma
- Cuando recibas un informe de error: simplemente arréglalo. No pidas que te lleven de la mano
- Identifica logs, errores o tests que fallan y luego resuélvelos
- Cero necesidad de cambio de contexto por parte del usuario
- Ve a arreglar los tests de CI que fallan sin que te digan cómo

## Gestión de Tareas

1. *Planificar Primero*: Escribe el plan en tasks/todo.md con elementos verificables
2. *Verificar Plan*: Confirma antes de comenzar la implementación
3. *Seguir el Progreso*: Marca los elementos como completados a medida que avances
4. *Explicar Cambios*: Resumen de alto nivel en cada paso
5. *Documentar Resultados*: Añade una sección de revisión a tasks/todo.md
6. *Capturar Lecciones*: Actualiza tasks/lessons.md después de las correcciones

## Principios Fundamentales

- *Simplicidad Primero*: Haz que cada cambio sea lo más simple posible. Afecta al mínimo código necesario.
- *Sin Pereza*: Encuentra las causas raíz. Nada de arreglos temporales. Estándares de desarrollador senior.
- *Impacto Mínimo*: Los cambios solo deben tocar lo necesario. Evita introducir errores.