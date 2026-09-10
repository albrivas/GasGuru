# iOS Project Setup

Referencia de dos cosas distintas que se suelen confundir:

1. **Estructura de un proyecto iOS/Xcode** — ficheros que existen en *cualquier* proyecto iOS (con o sin KMP) para manejar targets, schemes y variantes de build.
2. **Checklist de código para publicar en App Store** — qué debe existir en el repo antes de poder generar y subir un IPA, independientemente de lo anterior.

KMP no interviene en ninguna de las dos capas: `composeApp` se consume como un pod más (`pod 'composeApp', :path => '../composeApp'`) y todo lo de abajo es Xcode/iOS puro.

---

## 1. Estructura de proyecto iOS

| Fichero / directorio | Formato | Para qué sirve | En este repo |
|---|---|---|---|
| `iosApp.xcworkspace` | Xcode workspace | Punto de entrada real para abrir el proyecto: agrupa el proyecto de la app + el proyecto `Pods` generado por CocoaPods. Se abre esto, nunca el `.xcodeproj` suelto. | Generado por `pod install`, no está en git |
| `iosApp.xcodeproj` | Plist binario/XML (`project.pbxproj`) | Formato nativo de Xcode: targets, build phases, build settings, referencias a ficheros. Es opaco y pésimo para mergear en git. | Generado por XcodeGen, **no está en git** (`.gitignore`) |
| `project.yml` | YAML (XcodeGen) | Fuente de verdad declarativa desde la que XcodeGen genera el `.xcodeproj` cada vez: targets, `configs`, mapeo de `configFiles`, y la sección `schemes:`. Sustituye la necesidad de editar el `.xcodeproj` a mano. | `iosApp/project.yml` |
| `Config/*.xcconfig` | Texto plano `CLAVE=valor` | Build settings por combinación (scheme × configuración): bundle ID, nombre de producto, entorno, entitlements a usar, flags de compilación (`MOCK`). Es el equivalente iOS de los `productFlavors`/`buildTypes` de Android. | `iosApp/Config/gasguru-{Prod,Mock}-{Debug,Release}.xcconfig` |
| `*.xcscheme` | XML (dentro de `.xcodeproj/xcshareddata/xcschemes/`) | Qué configuración usa cada acción (`run`, `test`, `profile`, `archive`) de un scheme. Se generan a partir de la sección `schemes:` de `project.yml` — nunca se crean a mano en la UI de Xcode en este proyecto. | Generados por XcodeGen |
| `Podfile` / `Podfile.lock` | Ruby (CocoaPods DSL) | Declara dependencias nativas (Firebase, Mixpanel, OneSignal, GooglePlaces, y el propio framework `composeApp`) y fija sus versiones resueltas. | `iosApp/Podfile` |
| `Info.plist` | Plist | Metadata de la app: nombre, versión, permisos runtime (`NSLocationWhenInUseUsageDescription`), background modes, URL schemes, launch screen. Usa variables `$(...)` que vienen de los xcconfig. | `iosApp/iosApp/Info.plist` |
| `*.entitlements` | Plist | Capabilities de la app: push notifications (`aps-environment`), app groups, keychain sharing, etc. Uno por configuración si difieren (aquí: uno para Debug, otro para Release). | `iosApp/iosApp/iosApp-{Debug,Release}.entitlements` |
| `Assets.xcassets` | Catálogo de assets de Xcode | Icono de la app, colores del tema, imágenes. | `iosApp/iosApp/Assets.xcassets` |

**Regla práctica** (ya documentada en conversación con el equipo): si el cambio es "estructura del proyecto" (nuevo scheme, nuevo target, nuevo bundle ID, nuevo pod) → tocar `project.yml`/`.xcconfig`/`Podfile` y correr `./scripts/ios-setup.sh` (xcodegen + pod install). Si el cambio es "escribir lógica" → se edita el `.swift` directamente, sin regenerar nada.

---

## 2. Checklist de código para subir a App Store

Esto es independiente de lo anterior: son requisitos que Apple exige en el **contenido** del build, no en cómo está organizado el proyecto. Todo lo de esta tabla vive en el repo (no en el portal de Apple Developer / App Store Connect, que es un checklist aparte de cuentas, certificados y metadata).

| Requisito | Dónde vive | Para qué | Estado en GasGuru |
|---|---|---|---|
| Icono de app (1024×1024) | `Assets.xcassets/AppIcon.appiconset` | Obligatorio para poder archivar. Desde Xcode 14+ basta una sola imagen de 1024×1024, no hace falta el set completo de tamaños. | ✅ Hecho |
| `UILaunchScreen` | `Info.plist` | Pantalla de arranque nativa; obligatoria. | ✅ Hecho |
| Strings de permisos runtime | `Info.plist` (`NS*UsageDescription`) | Cualquier permiso que pida la app (ubicación, cámara, etc.) necesita su string explicando el porqué, o Apple rechaza el build en review. | ✅ Hecho (`NSLocationWhenInUseUsageDescription`, único permiso usado) |
| `ITSAppUsesNonExemptEncryption` | `Info.plist` | Declara por adelantado si la app usa cifrado no-exento, para no tener que responder el formulario de "Export Compliance" en cada subida. GasGuru solo usa TLS estándar → exento. | ✅ Hecho (`false`) |
| `PrivacyInfo.xcprivacy` (del target) | `iosApp/iosApp/PrivacyInfo.xcprivacy` | Declara las "required reason APIs" que usa el binario (incluye APIs que usa el *runtime* de Kotlin/Native internamente, no solo lo que llama tu Swift) y el resumen de qué tipos de datos recoge la app y con qué propósito. | ✅ Hecho — declara `SystemBootTime` (uso interno de Kotlin/Native) + `ProductInteraction` (Mixpanel) + `DeviceID` (OneSignal push subscription id), ninguno vinculado a identidad ni usado para tracking |
| Entitlement de push en producción | `iosApp-Release.entitlements` (`aps-environment: production`) | Necesario para que las push notifications funcionen en un build firmado para distribución. | ✅ Hecho |
| Certificado + perfil de distribución | Config de firma (`project.yml` / cuenta Apple Developer) | Para archivar y subir necesitas firmar con **Apple Distribution**, no con el certificado de desarrollo. Hoy `project.yml` no fija `CODE_SIGN_STYLE` y el `.xcodeproj` generado trae `CODE_SIGN_IDENTITY = "iPhone Developer"` (identidad de desarrollo) por defecto de XcodeGen. | ⚠️ Pendiente de revisar antes de archivar para Store |
| App ID registrado en Apple Developer | Fuera del repo | El bundle ID (`com.gasguru.app` y variantes) debe existir como App ID en el portal antes de poder generar el perfil de distribución. | ⚠️ Acción manual pendiente (bundle ID cambió en la PR de Firebase) |
| App creada en App Store Connect | Fuera del repo | Con el mismo bundle ID, para poder subir builds y rellenar metadata (capturas, descripción, categoría, App Privacy "nutrition label"). | ⚠️ Acción manual |
| `ExportOptions.plist` | No existe aún | Solo hace falta si se automatiza `xcodebuild -exportArchive` por CLI/CI. Un archive+distribute manual desde la UI de Xcode no lo necesita. | N/A por ahora (no hay pipeline de CI para iOS) |

### Notas sobre el privacy manifest

Los tres SDKs de terceros usados en iOS están auditados así (verificado en código, no supuesto):

- **Mixpanel**: solo eventos de interacción de producto (taps, pantallas, filtros). `trackAutomaticEvents: false` y no se llama a `identify()`/`people.set()`/`alias()` en ningún sitio → no vinculado a identidad.
- **OneSignal**: solo `pushSubscription.id` (anónimo, gestionado por OneSignal) + un tag booleano, usados para enrutar el push de alertas de precio. No se llama a `OneSignal.login()` en ningún sitio → no vinculado a identidad.
- **Microsoft Clarity**: solo integrado en Android (`app/build.gradle.kts`), no existe en iOS/`composeApp`.
- **Firebase Crashlytics**: pendiente de añadir la entrada `NSPrivacyCollectedDataTypeCrashData` (diagnósticos de crash, no vinculado a identidad) cuando la PR de Firebase iOS se mergee — ver [Firebase iOS](FIREBASE_IOS.md).

Ninguno de los tres tiene forma de vincular datos a la identidad real del usuario porque la app no tiene login ni sistema de cuentas.
