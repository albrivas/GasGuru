# Claude Code Skills - Mini Guía

## 1. ¿Qué es una Skill?

Comando personalizado que Claude puede invocar manual (`/nombre`) o automáticamente cuando detecta keywords.

## 2. Estructura de una Skill

```
.claude/skills/nombre-skill/
├── SKILL.md              # Obligatorio
└── rules/                # Opcional (si necesitas modularizar)
    ├── regla-1.md
    └── regla-2.md
```

## 3. Flujo de Carga

### Al Iniciar Conversación
- Claude carga **SOLO** el campo `description` de SKILL.md
- NO carga el contenido completo ni las rules
- Budget: 15K caracteres máximo (ver punto 6)

### Al Invocar la Skill (manual o automática)
1. Carga SKILL.md completo
2. Carga todos los archivos en rules/
3. Aplica las instrucciones

## 4. Skills vs Commands

**Ahora son lo mismo** (merged en versiones recientes)

| Feature | Commands | Skills |
|---------|----------|--------|
| Slash command | ✅ | ✅ |
| Auto-invocación | ❌ | ✅ |
| Archivos adicionales | ❌ | ✅ |

**Recomendación:** Usar skills (más features)

## 5. Contenido de una Skill (estructura interna)

```yaml
---
name: mi-skill
description: Qué hace y cuándo usarla (keywords para auto-invocación)
disable-model-invocation: true  # Opcional: solo manual
---

# Título

Instrucciones en markdown para Claude...

## Secciones

- Ejemplos de código
- Referencias a archivos
- Inyección dinámica: !`comando shell`
```

## 6. Budget de Caracteres

**Límite:** 15,000 caracteres (por defecto)

**¿Qué cuenta?**
- Solo el campo `description` de cada SKILL.md

**¿Qué NO cuenta?**
- Contenido completo de SKILL.md
- Archivos rules/*.md
- Ejemplos

**Aumentar budget:**
```bash
export SLASH_COMMAND_TOOL_CHAR_BUDGET=30000
```

**En la práctica:**
- 5-10 skills → No hay problema
- 50+ skills → Puede exceder límite