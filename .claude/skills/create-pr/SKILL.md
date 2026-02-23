---
name: create-pr
description: Create pull requests following GasGuru naming conventions and pre-flight checklist. Use when the user asks to create a PR, pull request, or push and create a PR. Applies the correct title format, validates a checklist before creating, and determines the base branch.
---

# Create PR

## Pre-flight checklist

Verify ALL items before creating the PR. If any item fails, warn the user and do NOT create the PR.

- No cross-dependencies between features
- Navigation passes IDs, not complex objects or network models
- Colors and styles from `GasGuruColors` and `GasGuruTheme`
- No hardcoded strings (use `stringResource`)
- Code follows module and architecture rules
- Release created following the **Release Playbook** (if applicable)

## Title format

```
<Type> - <Description>
```

- **Language**: Always English
- **Type**: `Feature`, `Bugfix`, `Release`, `Sync`, etc.
- **Description**: Starts with uppercase letter

Examples:
- `Feature - Add station search`
- `Bugfix - Fix crash on map screen`
- `Release - v2.0.1`
- `Sync - Update develop with main`

## Workflow

1. Run `git status` to check for uncommitted changes
2. Run `git log` and `git diff <base-branch>...HEAD` to understand all commits in the branch
3. Review the code changes against the **pre-flight checklist** above
4. Determine the PR type from the branch name and commits:
   - `feature/*` -> `Feature`
   - `bugfix/*` or `fix/*` -> `Bugfix`
   - `release/*` -> `Release`
   - `sync/*` -> `Sync`
5. Draft the title using the format above
6. Push the branch with `-u` if needed
7. Create the PR with `gh pr create` (empty body)

### Base branch

- Default base: `develop`
- For `release/*` branches: base is `main`
- For `sync/*` branches: base is `develop`

### Command

```bash
gh pr create --base <base-branch> --title "<Type> - <Description>" --body ""
```

Return the PR URL when done.