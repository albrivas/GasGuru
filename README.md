# GasGuru
GasGuru is an app to check fuel prices at all gas stations in Spain

# Running Tests Locally

## Requirements

- JDK 17
- Android SDK and ADB configured
- Android Emulator (API 34 recommended)

## Initial Setup

1. Create a `local.properties` file at the root of the project with the following content:

```properties
googleApiKey=YOUR_GOOGLE_MAPS_API_KEY
googleStyleId=YOUR_GOOGLE_STYLE_MAP_ID
storePassword=YOUR_KEYSTORE_PASSWORD
keyAlias=YOUR_KEYSTORE_ALIAS
keyPassword=YOUR_KEY_PASSWORD
```

2. Place your `google-services.json` file at `app/google-services.json`.

## Install Maestro (https://docs.maestro.dev/getting-started/installing-maestro)

```bash
curl -Ls "https://get.maestro.mobile.dev" | bash
export PATH="$HOME/.maestro/bin:$PATH"
```

## Test execution

1. Start an Android Emulator:

```bash
$ANDROID_HOME/emulator/emulator -avd <emulator_name> -no-snapshot-save -no-window -no-boot-anim
```
2. Run instrumented test (Compose):
```bash
./gradlew connectedCheck
```
3. Build the debug APK:
```bash
./gradlew assembleDebug
```
4. Install APK on the emulator:
```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```
5. Execute the E2E test with Maestro:
```bash
maestro test .maestro/config.yaml
# Record test in local
maestro record --local config.yaml
```
