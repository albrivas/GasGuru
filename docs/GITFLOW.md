## Custom GitFlow

This project follows a **structured Git workflow** tailored to our development, release, and hotfix needs. Here's how it's organized:

### Main branches

- `main`: contains production-ready code. Every commit here represents a **released version**, and it's the source for deployments to the stores.
- `develop`: the active development branch. Features and improvements are integrated here through PRs.

### Branch types

| Branch type      | Prefix     | Purpose                                                                  |
|------------------|------------|--------------------------------------------------------------------------|
| Feature          | `feature/` | New features or improvements                                             |
| Bug fix          | `bugfix/`  | Fixes for issues found during development                                |
| Hotfix           | `hotfix/`  | Critical fixes in production                                             |
| Release          | `release/` | Prepares a new version (version bump, final tweaks)                      |
| Docs             | `docs/`    | Only for documentation                                                   |
| Sync (if needed) | `sync/`    | Cherry-picks from `main` to `develop` when a direct merge isn't possible |

### Typical workflow

1. Work is done in `feature/` or `bugfix/` branches from `develop`, and merged into `develop` via PR.
2. When a release is ready:
    - A `release/x.y.z` branch is created from `develop`
    - The version is bumped in `versions.properties` and any final adjustments are made
    - Two PRs are created:
        - `release/x.y.z` → `main` (**squash merge**) ✅ clean production history
        - `release/x.y.z` → `develop` (**regular merge** or squash, depending on preference) ✅ avoids cherry-picking

3. In case of an urgent hotfix:
    - A `hotfix/x.y.z` branch is created from `main`
    - The fix is made
    - A PR is created to `main` (**squash merge**)
    - Then a PR is created to `develop` to sync the fix (**regular merge** or cherry-pick as needed)

### CI/CD on PRs

- Pipelines run automatically on PRs targeting `develop`
- **Branches prefixed with `sync/*` are excluded from the pipeline**, since their changes have already been validated in `main`