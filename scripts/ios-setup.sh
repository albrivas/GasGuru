#!/usr/bin/env bash
# Regenerates the iOS Xcode project from iosApp/project.yml and re-integrates CocoaPods.
#
# Order matters: XcodeGen overwrites iosApp.xcodeproj on every run, which wipes any
# CocoaPods integration in it, so `pod install` must always run *after* `xcodegen`.
# iosApp.xcodeproj is not tracked in git for this reason — this script is how you get one.
set -euo pipefail

cd "$(dirname "$0")/.."

echo "==> Generating iOS .xcconfig files (versions.properties + EnvironmentsConventionPlugin)"
./gradlew :composeApp:generateIosEnvConfig

echo "==> Running XcodeGen"
xcodegen generate --spec iosApp/project.yml --project iosApp

echo "==> Running pod install"
(cd iosApp && pod install)

echo "==> Done. Open iosApp/iosApp.xcworkspace in Xcode."
