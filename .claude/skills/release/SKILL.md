---
name: release
description: Create a GasGuru release for store deployment. Use when the user asks to create a release, bump version, prepare a new version, deploy to stores, or publish a new version. Handles version bumping, whatsnew files, branch creation, and PR.
---

# Release Playbook

## Key files

- `versions.properties` — contains `versionCode`, `versionMajor`, `versionMinor`, `versionPatch`
- `distribution/whatsnew/whatsnew-en-US` — English changelog (max 4 lines)
- `distribution/whatsnew/whatsnew-es-ES` — Spanish changelog (max 4 lines)

## Steps

1. Ensure `develop` is up to date:
   ```bash
   git checkout develop && git pull
   ```

2. Read `versions.properties` in `develop` to get the current version (`versionMajor.versionMinor.versionPatch`)

3. Create a bump branch from `develop` (increment patch by default):
   ```bash
   git checkout -b chore/bump-version-X.X.X
   ```

4. Update `versions.properties`:
   - Increment `versionCode` by 1
   - Increment `versionPatch` by 1
   - Do NOT change `versionMajor` or `versionMinor` unless explicitly requested

5. Update whatsnew files:
   - These files appear in the **app store listing** — content must be user-facing (new features, UI changes, visible fixes). Never include technical changes (refactors, dependency updates, build infra, architecture changes).
   - Review commits in `develop` since the last release and identify only user-facing changes:
     ```bash
     git log origin/main..develop --oneline | head -40
     ```
   - If there are no user-facing changes, use this generic message:
     - EN: `- Bug fixes and performance improvements`
     - ES: `- Corrección de errores y mejoras de rendimiento`
   - Add new changes at the TOP of the file (newest first)
   - Remove the LAST line(s) to keep max 4 lines per file

6. Commit and push, then create a PR to `develop`:
   ```bash
   git add versions.properties distribution/whatsnew/whatsnew-en-US distribution/whatsnew/whatsnew-es-ES
   git commit -m "chore: bump version from X.X.X to X.X.X"
   git push origin chore/bump-version-X.X.X -u
   gh pr create --base develop --title "chore: bump version from X.X.X to X.X.X" --body ""
   ```
   **Do NOT proceed until this PR is merged and develop is pulled again.**

7. Pull `develop` after the bump PR is merged:
   ```bash
   git checkout develop && git pull
   ```

8. Create the release branch from `main`:
   ```bash
   git checkout main && git pull
   git checkout -b release/X.X.X
   ```

9. Merge `develop` keeping develop's changes on conflicts:
   ```bash
   git merge develop --strategy-option=theirs
   ```

   **After the merge, always run these cleanup steps:**

   a) Resolve any pending modify/delete conflicts (files deleted in develop but conflicting):
   ```bash
   git status --short | grep "^DU\|^UD" | awk '{print $2}' | xargs -I{} git rm -f {} 2>/dev/null || true
   ```

   b) Remove files that were deleted in develop but silently kept in the release branch:
   ```bash
   MERGE_BASE=$(git merge-base HEAD develop)
   git diff "$MERGE_BASE" develop --name-status | grep "^D" | awk '{print $2}' | while read f; do
     if [ -f "$f" ]; then
       echo "Removing file deleted in develop: $f"
       git rm -f "$f"
     fi
   done
   ```

   c) Remove files that exist in the release branch (from main) but do NOT exist in develop:
   ```bash
   git diff develop HEAD --name-only --diff-filter=A | while read f; do
     if [ -f "$f" ]; then
       echo "Removing file not present in develop: $f"
       git rm -f "$f"
     fi
   done
   ```

   If any files were removed in steps (b) or (c), amend the merge commit:
   ```bash
   git diff --cached --quiet || git commit --amend --no-edit
   ```

10. **Verify the project compiles** before continuing:
    ```bash
    ./gradlew assembleDebug
    ```
    If the build fails, fix the errors before proceeding. Common causes after a merge:
    - Duplicate functions or classes (keep develop's version, remove the duplicate from main)
    - References to removed methods or classes (remove the dead code)
    - Missing imports caused by merge artifacts

11. Push and create PR to `main`:
    ```bash
    git push origin release/X.X.X -u
    gh pr create --base main --title "Release - vX.X.X" --body ""
    ```

12. Return the PR URL when done.
