# CI/CD

## Overview
Hay dos workflows principales:
- PRs a `develop`: `.github/workflows/main.yml`
- Deploy a Play Store al hacer push a `main`: `.github/workflows/deploy-to-playstore.yml`

## PRs (CI)
Workflow: `CI - Pull Request to Develop` en `.github/workflows/main.yml`

Trigger:
- `pull_request` (cualquier PR, incluido hacia `main`)
- `workflow_dispatch`

Se omiten PRs de:
- `dependabot[bot]`
- ramas `sync/*`
- ramas `docs/*`

Pasos principales:
1) Checkout (con historial completo).
2) Setup JDK 17 y caches (Gradle + Sonar).
3) Genera `local.properties` y `app/google-services.json` desde secretos.
4) Detekt (`./gradlew codeCheck`).
5) Unit tests + JaCoCo (prod) y reporte agregado.
6) SonarCloud Scan con el XML de JaCoCo.
7) Emulator: `connectedCheck`, `assembleProdRelease` y tests con Maestro.

Resultados:
- SonarCloud comenta en la PR con Quality Gate, issues y coverage.
- JaCoCo HTML en `build/reports/jacoco/jacocoRootReport/html/index.html`.

## main (CD)
Workflow: `Deploy to Play Store` en `.github/workflows/deploy-to-playstore.yml`

Trigger:
- `push` a `main`

Pasos principales:
1) Checkout y JDK 17
2) Genera `local.properties` y `app/google-services.json`
3) Lee `versions.properties` y actualiza `versionCode` / `versionName`
4) `bundleProdRelease`
5) Firma del AAB
6) Subida a Google Play (track production)
7) Crea GitHub Release

## Secretos necesarios
Se usan secrets en GitHub Actions:
- Google Maps, OneSignal, Mixpanel, Supabase
- Google Services JSON (base64)
- Keystore y Play Store
- SonarCloud (`SONAR_TOKEN`)

## Notas de cobertura
Para que Sonar muestre cobertura real en PRs:
- Generar JaCoCo antes del scan.
- La ruta del XML se define en el plugin de Sonar (no hace falta pasarla en el workflow).
  `build-logic/convention/src/main/java/SonarConventionPlugin.kt`
