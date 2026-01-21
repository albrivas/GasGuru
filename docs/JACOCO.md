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

## Excludes comunes
Para reducir ruido, se excluyen clases generadas y carpetas no relevantes:
- Hilt/Dagger (`*Hilt*`, `*Dagger*`, `*_Factory`, `*_MembersInjector`, etc.)
- `BuildConfig`, `R`, `Manifest`
- Previews y generated (`*Preview*`, `*ComposableSingletons*`, `*JsonAdapter*`)
- Tests (`*Test*`)
- Paquetes: `ui`, `uikit`, `model`, `navigation`

Si quieres volver a incluir algo, ajusta `jacocoExcludes` en el plugin.

## Por que pueden salir duplicados
JaCoCo falla si analiza la **misma clase dos veces** desde diferentes carpetas.
Por eso el plugin solo toma clases desde:
- `tmp/kotlin-classes/<variant>`
- `intermediates/javac/<variant>/classes`
y evita `intermediates/classes/**`.

## Notas
- JaCoCo mide bytecode; incluye cualquier clase compilada que no esté excluida.
- Para UI tests (instrumentation) haria falta configuracion adicional.
