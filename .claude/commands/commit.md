---
description: Create conventional commits in English without AI references
---

# Commit Changes

Create conventional commits following strict guidelines with **separate commits per functionality**.

## Instructions

1. **Review Changes**: Run `git status` and `git diff` to understand modifications

2. **Group Changes by Functionality**:
    - **CRITICAL**: Analyze and separate changes into logical units
    - Each distinct feature, fix, or task = separate commit
    - DO NOT bundle unrelated changes together
    - DO NOT create a single commit with multiple features

3. **Determine Commit Type**: Use appropriate conventional commit type:
    - `feat:` new features or capabilities
    - `fix:` bug fixes
    - `chore:` maintenance, dependencies, version bumps
    - `docs:` documentation changes
    - `refactor:` code restructuring without functionality changes
    - `style:` formatting, whitespace, code style
    - `test:` adding or modifying tests
    - `perf:` performance improvements
    - `ci:` CI/CD configuration changes
    - `build:` build system or dependency changes

4. **Craft the Commit Message**:
    - **Subject line** (required): `type: brief description in lowercase`
    - **Body** (optional): ONLY if needed for context
        - Keep body SHORT (8-12 words maximum)
        - Explain WHAT changed and WHY
        - Use imperative mood ("add", "fix", not "added", "fixed")

5. **Stage and Commit Individually**:
    - For EACH logical group of changes:
        - `git add` only files related to that specific change
        - `git commit` with focused message
        - Repeat for next group

## Critical Rules

⚠️ **NEVER**:
- Create a single commit with a list of multiple changes
- Bundle unrelated features/fixes together
- Mention "Claude", "AI", "assistant", "model"
- Reference AI-generated code or co-authors

✅ **Always**:
- Create **one commit per logical functionality**
- Write in English
- Use lowercase for descriptions
- Keep subject under 50 characters
- Use imperative mood
- Keep body concise (or omit entirely)

## Workflow Example

If you have these changes:
- Added search feature
- Fixed map bug
- Updated documentation

**Create 3 separate commits:**
```bash
# Commit 1
git add src/search/
git commit -m "feat: add station search by name"

# Commit 2
git add src/map/
git commit -m "fix: correct map zoom level on startup"

# Commit 3
git add docs/
git commit -m "docs: update API integration guide"
```

**DON'T create:**
```bash
# ❌ WRONG - Single commit with everything
git add .
git commit -m "feat: add search, fix map, update docs"
```

## Examples

**Good commits (separate by functionality):**
```
feat: add station search by name
fix: correct map zoom level on startup
chore: bump version from 2.0.0 to 2.0.1
refactor: simplify price comparison logic
docs: update API integration guide
```

**Bad commits (DO NOT USE):**
```
feat: add station search (implemented with Claude)  ❌
fix: multiple bugs and add features  ❌
Update code.kt  ❌
Added new feature  ❌
feat: add search, fix bugs, update docs  ❌
```

## Commit Format
```
type: subject line in lowercase

Optional body if needed (1-2 lines max).
Explain what and why, not how.
```