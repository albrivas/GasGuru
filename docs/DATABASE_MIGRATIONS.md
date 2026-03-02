# Database Migrations

Historial de migraciones de la base de datos Room de GasGuru. Cada entrada explica qué cambió y por qué.

**Versión actual**: 14
**Archivo de migraciones**: `core/database/src/main/java/com/gasguru/core/database/migrations/DataBaseMigration.kt`
**Configuración de la DB**: `core/database/src/main/java/com/gasguru/core/database/di/DatabaseModule.kt`
**Tests de migración**: `core/database/src/androidTest/java/com/gasguru/core/database/migration/DatabaseMigrationTest.kt`

---

## Regla: cómo actualizar `user-data`

`UserDataDao` usa `@Insert(IGNORE)` + `@Update` (nunca `REPLACE`). El motivo: `REPLACE` en SQLite hace DELETE + INSERT, lo que dispara el `onDelete = CASCADE` de la tabla `vehicles`, borrando todos los vehículos del usuario silenciosamente. Todos los métodos que modifican `user-data` deben pasar por `OfflineUserDataRepository.upsertUserData()`.

---

## Inicialización (fresh install)

Al crear la base de datos por primera vez, `DatabaseModule` registra un `RoomDatabase.Callback.onCreate` que inserta la fila por defecto de `user-data` con `id = 0`. Esto es necesario porque `vehicles` tiene una FK constraint sobre `user-data.id`, y sin esta fila, cualquier insert de vehículo durante el onboarding fallaría.

> **Regla**: si en una versión futura se añaden tablas que necesiten datos por defecto, hay que inicializarlos aquí (para nuevos usuarios) **y** en la migración correspondiente (para usuarios existentes).

---

## Historial

### v2 → v3
**Qué**: `ALTER TABLE fuel-station ADD COLUMN lastUpdate`
**Por qué**: Necesario para saber cuándo se actualizaron por última vez los precios de cada estación y evitar recargas innecesarias.

---

### v3 → v4
**Qué**: `ALTER TABLE fuel-station ADD COLUMN isFavorite`
**Por qué**: Primera implementación de favoritos directamente como columna en la tabla de estaciones. Posteriormente reemplazado por una tabla separada (v10→v11).

---

### v4 → v5
**Qué**: `CREATE TABLE favorite_station_cross_ref`
**Por qué**: Refactor del modelo de favoritos hacia una tabla de relación n:m. La columna `isFavorite` de v3→v4 quedó obsoleta. Esta tabla también fue reemplazada en v10→v11.

---

### v5 → v6
**Qué**: `ALTER TABLE user-data ADD COLUMN lastUpdate`
**Por qué**: Registrar cuándo se actualizó por última vez el usuario, para lógica de sincronización.

---

### v6 → v7
**Qué**: `CREATE TABLE filter`
**Por qué**: Persistir los filtros de búsqueda del usuario (marca, horario, etc.) entre sesiones.

---

### v7 → v8
**Qué**: `ALTER TABLE user-data ADD COLUMN isOnboardingSuccess`
**Por qué**: Controlar si el usuario ha completado el onboarding para decidir qué pantalla mostrar al arrancar.

---

### v8 → v9
**Qué**: `ALTER TABLE user-data ADD COLUMN themeModeId`
**Por qué**: Persistir la preferencia de tema (claro / oscuro / sistema) del usuario.

---

### v9 → v10
**Qué**: `CREATE INDEX index_location ON fuel-station (latitude, longitudeWGS84)`
**Por qué**: Optimización de rendimiento para las consultas de estaciones cercanas, que filtran por coordenadas geográficas.

---

### v10 → v11
**Qué**: Reemplaza `favorite_station_cross_ref` por `favorite_stations` (tabla simplificada)
**Por qué**: La tabla de relación n:m era innecesariamente compleja para un modelo de usuario único. Se migran los datos existentes y se elimina la tabla antigua.

---

### v11 → v12
**Qué**: `CREATE TABLE price_alerts`
**Por qué**: Soporte para alertas de precio por estación, con campos para sincronización y seguimiento de modificaciones.

---

### v12 → v13
**Qué**: `ALTER TABLE fuel-station ADD COLUMN priceAdblue`
**Por qué**: Mostrar el precio del AdBlue como tipo de combustible adicional en las estaciones que lo ofrecen.

---

### v13 → v14
**Qué**:
1. `CREATE TABLE vehicles` (con FK a `user-data`)
2. Migra `fuelSelection` de `user-data` a `vehicles` como primer vehículo de cada usuario
3. Recrea `user-data` sin la columna `fuelSelection`

**Por qué**: Introducción del modelo de vehículos. La preferencia de combustible, que antes era una propiedad plana del usuario, pasa a ser un atributo del vehículo. Esto permite en el futuro gestionar múltiples vehículos por usuario, cada uno con su tipo de combustible y capacidad de depósito.