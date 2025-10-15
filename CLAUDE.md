# CLAUDE.md — GasGuru

## Módulos y reglas
- Permitido: `feature → core`, `app → features/core`
- Prohibido: `feature ↔ feature`, `UI ↔ data sources directos`
- Navegación: pasar IDs y cargar datos en ViewModel vía UseCases

## Compose & Estado
- VM expone: `UiState` sellada + `events`
- Usar `collectAsStateWithLifecycle`, evitar `!!`
- Componentes deben ser `@Stable` cuando aplique

## Theming
- Usar siempre `GasGuruColors` para Light y Dark
- Prohibido hardcodear colores (usar solo desde el tema)
- Mantener coherencia visual entre modos claro y oscuro

## Code
- Añadir nombre de los argumentos. Ejemplo: `getLocation(location = loc)
- Eliminar imports sin usar

## PR Checklist
- [ ] No hay dependencias cruzadas entre features
- [ ] Navegación pasa IDs, no objetos complejos ni modelos de red
- [ ] Colores y estilos tomados de `GasGuruColors` y `GasGuruTheme`
- [ ] Sin hardcode de strings (usar `stringResource`)
- [ ] Código cumple reglas de módulos y arquitectura
- [ ] Release creado siguiendo el **Release Playbook**

## Commits
- **Idioma**: siempre en inglés
- **Formato**: [Conventional Commits](https://www.conventionalcommits.org)
   - Ejemplo:
      - `feat: add station search by name`
      - `fix: correct map zoom level`
      - `chore: bump version from 2.0.0 to 2.0.1`
- No poner nada relacionado con claude

## Nomenclatura de PRs
- **Idioma**: siempre en inglés
- **Formato**:
  ```
  <Type> - <Description>
  ```
- **Type**: `Feature`, `Bugfix`, `Release`, `Sync`, etc.
- **Description**: empieza con mayúscula.
- Ejemplos:
   - `Feature - Add station search`
   - `Bugfix - Fix crash on map screen`
   - `Release - v2.0.1`
   - `Sync - Update develop with main`

## Release Playbook

### Crear release completo
1. Asegúrate de tener `develop` actualizado:
   ```bash
   git checkout develop && git pull
   ```
2. **IMPORTANTE**: Verificar versión actual en `main` y crear release:
   ```bash
   git checkout main && git pull
   cat versions.properties  
   git checkout -b release/X.X.X  
   ```
3. Mergear `develop` manteniendo cambios de `develop` en conflictos
4. Actualizar `versions.properties` (incrementar `versionCode` y `versionPatch`)
5. Actualizar archivos whatsnew: remover primera línea y agregar nuevos cambios de esta release
6. Commit: `chore: bump version from X.X.X to X.X.X` (sin referencias a Claude)
7. Push de la rama y crear PR con título:
   ```
   Release - vX.X.X
   ```

### Archivos clave
- `versions.properties` (versionCode, versionPatch)
- `distribution/whatsnew/whatsnew-en-US`
- `distribution/whatsnew/whatsnew-es-ES`

### Comandos resumidos
```bash
git checkout develop && git pull
git checkout main && git pull
cat versions.properties  
git checkout -b release/X.X.X
git merge develop --strategy-option=theirs
git add . && git commit -m "chore: bump version from X.X.X to X.X.X"
git push origin release/X.X.X -u
gh pr create --base main --title "Release - vX.X.X" --body ""
```

## Sync develop con main
1. No se puede mergear a `develop` directamente.
2. Crear PR con título:
   ```
   Sync - Actualizar develop con main
   ```
