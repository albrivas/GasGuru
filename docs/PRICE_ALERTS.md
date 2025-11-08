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
3. Elimina de base de datos local
4. Verifica si era la última alerta → deshabilita OneSignal tag
5. Si hay conexión → elimina de Supabase

### Sincronización Offline-First

**Componente**: `SyncManager`

**Funcionamiento**:
- Escucha cambios de conectividad con `NetworkMonitor`
- Cuando se recupera conexión:
  1. Obtiene alertas pendientes (`isSynced = false`)
  2. Sincroniza cada una con Supabase
  3. Marca como sincronizadas (`isSynced = true`)

**Inicialización**: Se ejecuta automáticamente en `GasGuruApplication.onCreate()`