# JaCoCo Coverage

> **NOTA IMPORTANTE:** si un modulo NO aplica `gasguru.jacoco`, no genera archivos `.exec` y el reporte agregado sale vacio o se salta. Verifica que los modulos con tests tengan aplicado `gasguru.jacoco` (via conventional plugin)

## Objetivo
Generar un reporte agregado de cobertura de tests unitarios para todo el proyecto, usando la variante **prodDebug** en `:app` y los tests JVM en el resto de modulos.

## Como se ejecuta
Comando recomendado (unit tests + reporte agregado):
```
./gradlew test -x :app:test :app:testProdDebugUnitTest jacocoRootReport --continue --stacktrace --no-daemon
```

Que hace:
1. `test`: ejecuta los unit tests de todos los modulos **library**.
2. `:app:testProdDebugUnitTest`: ejecuta los unit tests de `app` en **prodDebug**.
3. `jacocoRootReport`: genera el reporte agregado.

Salida del reporte:
- `build/reports/jacoco/jacocoRootReport/html/index.html`

## CI (SonarCloud)
Para que SonarCloud muestre cobertura real en la PR:
1) Genera el XML en CI:
```
./gradlew test -x :app:test :app:testProdDebugUnitTest jacocoRootReport
```
2) Ejecuta el analisis de SonarCloud con la ruta del XML:
```
./gradlew sonar -Dsonar.coverage.jacoco.xmlReportPaths=build/reports/jacoco/jacocoRootReport/jacocoRootReport.xml
```

Si usas la action de SonarCloud, pasa esa misma propiedad en `args`.

## Configuracion
El plugin de Jacoco vive en:
- `build-logic/convention/src/main/java/JacocoConventionPlugin.kt`
- Se aplica a los modulos library desde el plugin de convenciones: `build-logic/convention/src/main/java/AndroidLibraryConventionPlugin.kt`

El reporte agregado:
- Se registra en el **root** con la tarea `jacocoRootReport`.
- Solo incluye `:app:testProdDebugUnitTest` para evitar `mock`.
- Agrega classDirectories, sources y exec data de todos los modulos (excepto los excluidos).
 - Nota: cada modulo debe aplicar `gasguru.jacoco` para generar sus `.exec`. Sin eso, el agregado no tendra datos.

## Exclusiones

Todas las exclusiones estan centralizadas en:
- `build-logic/convention/src/main/java/CoverageExclusions.kt`

Este objeto lo consumen tanto JaCoCo como Sonar, asi que cualquier cambio se aplica a ambos.

### Modulos excluidos
Se excluyen modulos completos que no contienen logica de negocio testeable:

| Modulo | Motivo |
|--------|--------|
| `:core:testing` | Fakes, test rules y utilidades de testing |
| `:core:uikit` | Componentes UI reutilizables (se testean con tests de componentes) |
| `:core:ui` | Tema, colores y utilidades UI |
| `:navigation` | Grafo de navegacion |
| `:mocknetwork` | Mock de red para desarrollo |

### Patrones de archivos excluidos
Aplican a todos los modulos no excluidos:

| Patron | Motivo |
|--------|--------|
| `**/di/**` | Modulos de inyeccion de dependencias |
| `**/BuildConfig.*`, `**/R.class`, `**/R$*`, `**/Manifest*.*` | Clases generadas por Android |
| `**/*Test*.*` | Clases de test |
| `**/model/**`, `**/mapper/**` | Data classes y mappers |
| `**/navigation/**` | Navegacion |
| `**/*_Factory.*`, `**/*_MembersInjector.*`, `**/*_HiltModules*.*`, `**/Hilt_*.*`, `**/*Hilt*.*`, `**/*Dagger*.*` | Generados por Hilt/Dagger |
| `**/*AssistedFactory*.*`, `**/*AssistedInject*.*` | Assisted injection |
| `**/*_Impl*.*` | Implementaciones generadas (Room, etc.) |
| `**/*JsonAdapter*.*`, `**/*MapperImpl*.*` | Adaptadores JSON y mappers generados |
| `**/*ComposableSingletons*.*`, `**/*Preview*.*` | Generados por Compose |
| `**/*$*$*.*` | Clases internas/lambdas de Kotlin |
| `**/*UiState*.*` | Sealed classes de estado UI |
| `**/*Screen*.*` | Screens de Compose (se testean componentes individuales, no screens completas) |
| `**/*State.*` | Compose state holders (`rememberXxxState`) con propiedades `@Composable` no testeables en unit tests |
| `**/*Activity.*` | Activities de Android (boilerplate del framework) |
| `**/*Application.*` | Clases Application de Android (boilerplate del framework) |
| `**/*.gradle.kts` | Archivos de configuracion de Gradle (Sonar los detecta como Kotlin) |

Para modificar exclusiones, edita `CoverageExclusions.kt`. Los cambios se aplican automaticamente a JaCoCo y Sonar.

### Propiedades de exclusion en Sonar

Sonar tiene dos propiedades de exclusion distintas que se configuran en `SonarConventionPlugin.kt`:

| Propiedad | Que hace |
|-----------|----------|
| `sonar.coverage.exclusions` | Excluye archivos **solo del calculo de cobertura**, pero Sonar los sigue analizando (code smells, duplicacion, etc.) y **aparecen en la UI** con "0 lines to cover" |
| `sonar.exclusions` | Excluye archivos de **todo el analisis** de Sonar. No aparecen en la UI ni se analizan para nada |

En este proyecto usamos **ambas** con los mismos patrones para que los archivos excluidos no aparezcan en la UI de Sonar ni contaminen las metricas:

```kotlin
// SonarConventionPlugin.kt
property("sonar.exclusions", CoverageExclusions.sonarCoverageExclusions)
property("sonar.coverage.exclusions", CoverageExclusions.sonarCoverageExclusions)
```

> **Nota:** si solo usaras `sonar.coverage.exclusions`, los archivos excluidos seguirian apareciendo en la UI de Sonar con "0 lines to cover", lo que puede generar confusion sobre si realmente estan excluidos.

## Por que pueden salir duplicados
JaCoCo falla si analiza la **misma clase dos veces** desde diferentes carpetas.
Por eso el plugin solo toma clases desde:
- `tmp/kotlin-classes/<variant>`
- `intermediates/javac/<variant>/classes`
y evita `intermediates/classes/**`.

## Notas
- JaCoCo mide bytecode; incluye cualquier clase compilada que no esté excluida.
- Para UI tests (instrumentation) haria falta configuracion adicional.
