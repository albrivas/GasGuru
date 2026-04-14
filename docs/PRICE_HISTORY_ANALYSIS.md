# Price History — Análisis y Funcionalidades

## 1. Fuente de datos

La aplicación consume la **API oficial del Ministerio para la Transición Ecológica** (MITERD), mediante una variante histórica del endpoint de precios:

```
GET /ServiciosRESTCarburantes/PreciosCarburantes/EstacionesTerrestresHist/FiltroMunicipioProducto/{date}/{idMunicipality}/{idProduct}
```

- **Parámetros**: fecha (`dd-MM-yyyy`), municipio e ID de producto (tipo de combustible).
- La API devuelve **todas las estaciones del municipio** para esa fecha; el precio de la estación concreta se filtra en memoria por `idServiceStation`.
- **Una llamada por día**: no existe un endpoint de rango de fechas; cada día requiere una petición independiente.

---

## 2. Datos disponibles

### Modelo de dominio: `PriceHistory`

| Campo   | Tipo     | Origen API               | Descripción                            |
|---------|----------|--------------------------|----------------------------------------|
| `date`  | `String` | `Fecha` (`dd-MM-yyyy`)   | Fecha del precio                       |
| `price` | `Double` | `PrecioProducto`         | Precio en €/L (coma → punto decimal)  |

Solo estos dos campos se exponen al dominio. La respuesta bruta contiene dirección, horario, coordenadas, margen y tipo de venta, pero se descartan en el mapper.

### Tipos de combustible soportados

| Enum                  | `idProduct` |
|-----------------------|-------------|
| `GASOLINE_95`         | 1           |
| `GASOLINE_95_PREMIUM` | 20          |
| `GASOLINE_95_E10`     | 23          |
| `GASOLINE_98`         | 3           |
| `GASOLINE_98_PREMIUM` | 21          |
| `DIESEL`              | 4           |
| `DIESEL_PLUS`         | 5           |
| `GASOIL_B`            | 6           |

---

## 3. Librería de gráficos: Vico

### 3.1 Uso actual en la rama

La rama usa **Vico 2.0.0-beta.7** con dos artefactos:

```toml
# libs.versions.toml
vico = "2.0.0-beta.7"
```

```kotlin
// feature/detail-station/build.gradle.kts
implementation(libs.vico.compose)      // com.patrykandpatrick.vico:compose
implementation(libs.vico.compose.m3)   // com.patrykandpatrick.vico:compose-m3
```

El gráfico renderizado es una línea cartesiana con área sombreada:
- Color de línea: `Primary500` (tema GasGuru)
- Gradiente bajo la línea: `Primary500` (α 0.4) → transparente
- Ejes horizontal (fechas) y vertical (precio €/L)
- Scroll horizontal habilitado, comienza desde el dato más antiguo

### 3.2 Soporte de Compose Multiplatform

Vico **sí soporta Compose Multiplatform**, pero la versión usada en la rama (2.0.0-beta.7) no lo hacía con los módulos `compose` y `compose-m3` — estos eran exclusivamente Android en v2.x.

El soporte CMP completo llegó en **v3.0.0** (disponible desde principios de 2025), donde el módulo `compose` pasó a ser multiplatform de forma oficial y el módulo `multiplatform` previo quedó eliminado:

> _"The Jetpack Compose module, previously called `compose`, has been removed in favor of the Compose Multiplatform module, which also supports Jetpack Compose and is now stable."_ — Vico v3.0.0 release notes

La versión estable más reciente es **v3.0.3** (marzo 2025).

### 3.3 Migración a v3 para soporte CMP

Actualizar de 2.0.0-beta.7 a 3.x implica cambios de API (reorganización de módulos e imports), pero la librería sigue siendo la misma y no requiere sustituirla. Al estar GasGuru en KMP Phase 1 (con Phase 7 pendiente para features), la actualización puede hacerse en el momento en que `feature/detail-station` se migre a CMP.

### 3.4 Alternativas CMP evaluadas

| Librería                      | CMP | Mantenimiento | Notas                                         |
|-------------------------------|-----|---------------|-----------------------------------------------|
| **Vico v3**                   | ✅  | Activo        | Opción recomendada; misma librería, upgrade de versión |
| `netguru/compose-multiplatform-charts` | ✅ | Bajo | Comunidad pequeña, menor soporte            |
| `AAY-chart`                   | ✅  | Moderado      | Más limitado en tipos de gráfico              |
| `KoalaPlot`                   | ✅  | Activo        | Alternativa sólida si Vico v3 presenta problemas |

**Recomendación**: mantener Vico y actualizarlo a v3 cuando se aborde la migración CMP de `feature/detail-station`.

---

## 4. Estado actual de la implementación

- **Rango por defecto**: 15 días hacia atrás desde hoy (16 llamadas concurrentes con `async`).
- **Sin caché**: cada apertura de pantalla lanza todas las peticiones de nuevo.
- **Fecha actual excluida** del rango (la pantalla de detalle ya muestra el precio del día en curso).
- **Sin tests** en la rama actual.

---

## 5. Viabilidad de gráficos por periodo

### 5.1 Disponibilidad histórica de la API

La API no documenta un límite máximo de antigüedad. En la práctica los datos suelen estar disponibles desde hace varios años, aunque no está garantizado por contrato oficial.

### 5.2 Coste de peticiones por periodo

| Periodo          | Días | Llamadas HTTP |
|------------------|------|---------------|
| Mensual (30d)    | 30   | 30            |
| Trimestral (90d) | 90   | 90            |
| Semestral        | 180  | 180           |
| Anual (365d)     | 365  | 365           |

### 5.3 Viabilidad por periodo

| Funcionalidad         | Viabilidad | Consideración                                              |
|-----------------------|------------|------------------------------------------------------------|
| Mensual (último mes)  | ✅ Alta    | Extensión directa del parámetro `numberOfDays`             |
| Trimestral (3 meses)  | ✅ Media   | Recomendable añadir caché local (Room)                     |
| Anual (12 meses)      | ⚠️ Baja   | Caché obligatoria; ~365 llamadas sin ella son inviables    |
| Rango libre           | ✅ Media   | El UseCase ya acepta `date` y `numberOfDays`; solo necesita UI |

---

## 6. Viabilidad de comparativas de precio

### 6.1 Variación respecto a un día concreto

Viable sin cambios en la capa de datos. Fórmula: `Δ = precioHoy − precioFechaBase`. La fecha base puede ser un día fijo (ayer, hace 7 días, hace 30 días) o una fecha elegida por el usuario con un DatePicker.

Ejemplo de muestra: `+0,05 €/L (+3,1 %) respecto al 01/03/2025`.

### 6.2 Variación respecto al mes anterior

Viable. Se compara el precio del mismo día del mes anterior, o el promedio del mes anterior si se dispone del rango completo. Requiere que los datos de ese periodo estén en caché o se carguen bajo demanda.

### 6.3 Variación respecto a un mes elegido por el usuario

Viable con rango personalizado. El usuario selecciona un mes; se calcula el promedio del mes seleccionado y se compara con el precio actual o el promedio del mes en curso.

---

## 7. Restricciones y riesgos

| Riesgo                              | Impacto | Mitigación                                              |
|-------------------------------------|---------|---------------------------------------------------------|
| API sin SLA de disponibilidad       | Alto    | Manejo de errores por fecha; mostrar datos parciales    |
| Sin caché → llamadas repetidas      | Medio   | Implementar Room con clave `(idStation, idProduct, date)` |
| Filtrado en memoria por `idStation` | Bajo    | El número de estaciones por municipio es reducido       |
| Precio ausente en fecha concreta    | Bajo    | La API puede no tener datos para festivos sin actualización |

---

## 8. Recomendaciones de implementación

1. **Caché local con Room** antes de ampliar el rango más allá de 30 días. La clave de caché sería `(idStation, idProduct, date)`.
2. **Selector de periodo en UI**: chips o radio buttons (`Mensual / Trimestral / Personalizado`).
3. **Línea de referencia en el gráfico**: línea horizontal punteada en el precio de la fecha base seleccionada para visualizar la variación.
4. **Métricas del periodo**: mostrar promedio, máximo y mínimo junto al gráfico.