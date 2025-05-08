# GasGuru
GasGuru is an app to check fuel prices at all gas stations in Spain

# Ejecución de tests en local

## Requisitos

- JDK 17
- Android SDK y ADB configurados
- Emulador Android (API 34 recomendado)

## Configuración inicial

1. Crea el archivo `local.properties` en la raíz del proyecto con el siguiente contenido:

googleApiKey=TU_GOOGLE_MAPS_API_KEY 
googleStyleId=TU_GOOGLE_STYLE_MAP_ID 
storePassword=TU_KEYSTORE_PASSWORD 
keyAlias=TU_KEYSTORE_ALIAS 
keyPassword=TU_KEY_PASSWORD

2. Coloca tu archivo `google-services.json` en `app/google-services.json`.

## Instalación de Maestro CLI

```bash
curl -Ls "https://get.maestro.mobile.dev" | bash
export PATH="$HOME/.maestro/bin:$PATH"
```


## Ejecución de tests

1. Inicia un emulador Android:

```bash
$ANDROID_HOME/emulator/emulator -avd <nombre_de_tu_emulador> -no-snapshot-save -no-window -no-boot-anim
```
2. Ejecuta los tests instrumentados (Compose):
```bash
./gradlew connectedCheck
```
3. Compila el APK de debug:
```bash
./gradlew assembleDebug
```
4. Instala el APK en el emulador:
```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```
5. Ejecuta los tests E2E con Maestro:
```bash
maestro test .maestro/config.yaml
# Si quieres guardar un video del test en local
maestro record --local config.yaml
```
