# Sistema de Alertas de Precio - GasGuru

## Descripción General

El sistema de alertas de precio permite a los usuarios suscribirse a notificaciones push cuando hay cambios de precios en gasolineras específicas. El sistema funciona con una arquitectura offline-first que sincroniza automáticamente cuando hay conectividad.

## Arquitectura

### Componentes Principales

1. **Local Storage** (Room Database)
   - Tabla: `price_alerts`
   - Campos: `stationId`, `createdAt`, `isSynced`
   - Actúa como fuente de verdad

2. **Remote Storage** (Supabase)
   - Almacena alertas para personalizar notificaciones push
   - Se sincroniza automáticamente con base de datos local

3. **Push Notifications** (OneSignal)
   - Tag: `enable_stations_alerts`
   - Se habilita automáticamente al crear primera alerta
   - Se deshabilita automáticamente al eliminar última alerta

### Flujo de Datos

```
UI (DetailStation) 
    ↓ UseCase
Repository 
    ↓ DAO + OneSignal + Supabase
Database + OneSignal Tags + Remote Storage
```

## Funcionalidades

### Agregar Alerta de Precio

**Trigger**: Usuario activa toggle de alertas en DetailStationScreen

**Flujo**:
1. `addPriceAlertUseCase(stationId)`
2. `PriceAlertRepository.addPriceAlert()`
3. Verifica si es la primera alerta → habilita OneSignal tag
4. Guarda en base de datos local (`isSynced = false`)
5. Si hay conexión → sincroniza con Supabase y marca como `isSynced = true`
6. Si no hay conexión → queda pendiente para sync posterior

### Eliminar Alerta de Precio

**Trigger**: Usuario desactiva toggle de alertas

**Flujo**:
1. `removePriceAlertUseCase(stationId)`
2. `PriceAlertRepository.removePriceAlert()`
3. Según el estado de la alerta local:
   - **No sincronizada**: se elimina solo en local (nunca llegó a Supabase).
   - **Sincronizada + online**: se elimina primero en Supabase y, solo si esa llamada tiene éxito, se elimina en local. Si Supabase falla (timeout, error de red, etc.), la alerta se marca como `DELETE` pendiente en vez de perderse, para que `SyncManager`/`sync()` la reintente en la próxima sincronización.
   - **Sincronizada + offline**: se marca como `DELETE` pendiente para sync posterior.
4. Verifica si quedan alertas activas → deshabilita OneSignal tag si no queda ninguna

> Nota: si tras eliminar una alerta las notificaciones para esa estación siguen llegando, comprobar primero en el dashboard de Supabase si la fila sigue existiendo en `user_stations_alerts` — puede deberse a una policy RLS de `DELETE` mal configurada (el borrado no lanza error si RLS bloquea la fila, solo no borra nada), lo cual es un problema de configuración de Supabase y no de la app.

### Sincronización Offline-First

**Componente**: `SyncManager`

**Funcionamiento**:
- Escucha cambios de conectividad con `NetworkMonitor`
- Cuando se recupera conexión:
  1. Obtiene alertas pendientes (`isSynced = false`)
  2. Sincroniza cada una con Supabase
  3. Marca como sincronizadas (`isSynced = true`)

**Inicialización**: Se ejecuta automáticamente en `GasGuruApplication.onCreate()`