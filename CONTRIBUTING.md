# Contribuir a GasGuru

Este proyecto sigue un flujo de **Spec-Driven Development (SDD)**: toda tarea empieza como una spec o un bug report escrito antes de tocar código, para que un agente de IA (o una persona) pueda implementarla sin ida y vuelta.

## 1. Cómo se organiza el trabajo

Todo el trabajo vive en el [GitHub Project](https://github.com/users/albrivas/projects/3) de GasGuru, con columnas por `Status` (Todo / In progress / Done). Cada item es una issue de este repo, etiquetada con un label de módulo/categoría (`detail-station`, `android-auto`, `kmp`, `docs`, etc. — ver [Labels](https://github.com/albrivas/GasGuru/labels)).

No empieces a trabajar en algo que no esté en el board como issue — si no está escrito primero como spec o bug, no se implementa.

## 2. Crear una tarea: Spec vs Bug

Dos templates de issue, que se eligen automáticamente al pulsar **New issue**:

- **[Spec](.github/ISSUE_TEMPLATE/spec.md)** — para features nuevas o cambios de comportamiento. Debe caber en una pantalla sin scroll: qué y por qué, requisitos numerados, fuera de alcance (solo si hay riesgo real de que se desmadre), criterios de aceptación en Given/When/Then, y notas técnicas (solo si ya hay algo concreto decidido — una firma de interfaz, un componente a reutilizar).
- **[Bug](.github/ISSUE_TEMPLATE/bug.md)** — para algo que está roto. Síntoma, pasos para reproducir, esperado vs actual, y contexto técnico (stacktrace/logcat si lo tienes).

Que sea corto es intencional. Una spec que nadie lee porque tiene 10 secciones es peor que no tener spec — el objetivo es un contrato que un agente pueda implementar sin ambigüedad, no un documento de diseño exhaustivo.

Las cosas transversales (eventos de analytics, migraciones de Room, flows E2E de Maestro) no hace falta detallarlas en la spec — las aplica automáticamente el agente vía reglas que ya sigue al tocar los ficheros correspondientes.

## 3. Pasarle una tarea a Claude Code

1. Referencia la issue por número o URL — no pegues el body, el agente la lee en vivo con `gh issue view`:
   ```
   Implementa la issue #574 de GasGuru
   ```
2. Pídele que mueva el item a **In progress** en el Project al empezar.
3. Pídele que abra el PR con `Closes #574` en el body, para que la issue se cierre sola al mergear.

## 4. Ramas y PRs

El nombrado de ramas y la estrategia de merge siguen [GitFlow](docs/GITFLOW.md) (`feature/`, `bugfix/`, `docs/`, etc.). Los títulos de PR siguen el formato `<Type> - <Description>` (`Feature - ...`, `Bugfix - ...`). La rama base es `develop`, salvo que sea una rama `release/*`.

## 5. Arquitectura y convenciones de código

Eso no va aquí — ver [CLAUDE.md](CLAUDE.md) y la tabla de documentación que enlaza, para límites de módulos, requisitos de testing y guías por área.
