# Firebase en iOS

## Contexto

Android ya usa Firebase (Crashlytics + Analytics) vía `AndroidApplicationFirebaseConventionPlugin` y `app/google-services.json`, dentro del mismo proyecto Firebase `fuelpump-422418`. El target iOS usa el **mismo proyecto Firebase**, pero solo para **Crashlytics** — no hay `FirebaseAnalytics`, ya que la analítica de producto en iOS la cubre Mixpanel (ver [Analytics](ANALYTICS.md)).

## Bundle ID

La app iOS está registrada en Firebase con `com.gasguru.app`. Para que coincida, el bundle ID del target `iosApp` se renombró de `com.gasguru` a `com.gasguru.app` (y sus variantes por configuración):

| Configuración | Bundle ID |
|---|---|
| Prod / Release | `com.gasguru.app` |
| Prod / Debug | `com.gasguru.app.debug` |
| Mock / Release | `com.gasguru.app.mock` |
| Mock / Debug | `com.gasguru.app.mock.debug` |

Se mantiene un único `GoogleService-Info.plist` (el de `com.gasguru.app`, commiteado en `iosApp/iosApp/`) para las 4 configuraciones. Debug y Mock arrancan con un warning de mismatch de bundle ID (`I-COR000008`) — es esperado y no afecta a la subida de crashes, que se asocian por `GOOGLE_APP_ID`, no por bundle real del binario.

## Dónde se inicializa

`FirebaseApp.configure()` se llama en `iosApp/iosApp/iOSApp.swift`, al principio de `application(_:didFinishLaunchingWithOptions:)`, antes de `initKoin(...)` — así Crashlytics también captura crashes durante la inicialización de Koin/OneSignal.

El pod es `FirebaseCrashlytics` (`iosApp/Podfile`), que trae transitivamente `FirebaseCore`.

## Pendiente

- **Subida de dSYMs**: no configurada todavía. Los reportes de crash de builds Release llegan a la consola sin simbolizar hasta que se añada la fase de build `${PODS_ROOT}/FirebaseCrashlytics/run` en `iosApp/project.yml` (equivalente iOS de `mappingFileUploadEnabled` en Android — ver [Obfuscation](OBFUSCATION.md)).
- **Apps por entorno**: solo existe una app Firebase iOS (`com.gasguru.app`). Si se quiere separar crashes de Debug/Mock de los de producción, registrar `com.gasguru.app.debug` / `.mock` como apps adicionales en la consola de Firebase.
- **Acciones fuera del repo** derivadas del cambio de bundle ID: actualizar el bundle de la app en OneSignal, el App ID en Apple Developer/App Store Connect, y la restricción de la API key de Google Places si está limitada por bundle ID.
