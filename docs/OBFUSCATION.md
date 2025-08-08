# Code Obfuscation

## Structure

The project uses **distributed obfuscation per module** where each module manages its own ProGuard rules.

## How it Works

1. **Main module (app)**: Has `isMinifyEnabled = true` in `release` build type
2. **Propagation**: Obfuscation automatically propagates to all project modules
3. **Specific rules**: Each module has its own `proguard-rules.pro` file with rules for its specific dependencies

## Convention Plugin

Uses the **ProguardConventionPlugin** which:
- Automatically detects if `proguard-rules.pro` exists in each module
- Applies `consumerProguardFiles("proguard-rules.pro")` automatically
- Applied using `alias(libs.plugins.gasguru.proguard)` in each module

## Modules with Obfuscation Rules

All modules have obfuscation rules except **core/model**:

## Firebase Crashlytics

Mapping files are automatically uploaded to Crashlytics through:
- **AndroidApplicationFirebaseConventionPlugin** configures `mappingFileUploadEnabled = true`
- Crashes appear deobfuscated in Firebase Console
- Mapping files are located in `app/build/outputs/mapping/prodRelease/`