# Git Worktree en GasGuru

## ¿Qué es un worktree?

Un worktree permite tener **múltiples ramas activas al mismo tiempo** en carpetas distintas del disco, compartiendo el mismo repositorio `.git/`. Útil para trabajar en una feature sin tocar la rama actual.

```
/GasGuru/                    ← rama principal (ej. feature/supabase-api)
/GasGuru-format-distance/    ← worktree (ej. feature/format-distance)
         └── comparte el mismo .git/
```

## Crear un worktree

```bash
# Crear worktree con rama nueva desde develop
git worktree add ../GasGuru-<nombre> -b feature/<nombre> develop

# Ejemplo real
git worktree add ../GasGuru-format-distance -b feature/format-distance develop
```

## Eliminar un worktree

```bash
# Desde el repo principal
git worktree remove ../GasGuru-format-distance

# Si hay cambios sin commitear, forzar
git worktree remove --force ../GasGuru-format-distance
```

## Listar worktrees activos

```bash
git worktree list
```

---

## El problema de `local.properties`

`local.properties` está en `.gitignore` porque contiene rutas del SDK y claves de firma. Git no lo rastrea, por lo que el worktree **no lo hereda** automáticamente.

Sin él, Gradle falla al configurar el módulo `app`:
```
localProperties.getProperty("keyAlias") must not be null
```

### Solución: crear un symlink

Un symlink es un puntero al archivo original. El worktree "ve" el archivo sin duplicarlo.

```bash
ln -s /ruta/al/original /ruta/en/worktree

# Ejemplo real
ln -s /Users/albertorivas/Documents/Proyectos/Personal/GasGuru/local.properties \
      /Users/albertorivas/Documents/Proyectos/Personal/GasGuru-format-distance/local.properties
```

> El symlink no está trackeado por git ni se commiteará accidentalmente.

### Verificar que el symlink funciona

```bash
ls -la /Users/albertorivas/Documents/Proyectos/Personal/GasGuru-format-distance/local.properties
# Debe mostrar: local.properties -> .../GasGuru/local.properties
```

---

## Flujo completo recomendado

```bash
# 1. Crear worktree desde develop
git worktree add ../GasGuru-<feature> -b feature/<feature> develop

# 2. Crear symlink de local.properties
ln -s $(pwd)/local.properties ../GasGuru-<feature>/local.properties

# 3. Trabajar en el worktree
cd ../GasGuru-<feature>

# 4. Al terminar, volver al repo principal y eliminar el worktree
cd ../GasGuru
git worktree remove ../GasGuru-<feature>
```
