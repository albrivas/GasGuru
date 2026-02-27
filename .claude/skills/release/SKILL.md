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

2. Check if `main` has the version bump commit from the last release that is not in `develop`:
   ```bash
   git fetch origin main
   git log origin/main --oneline -- versions.properties distribution/whatsnew/ | head -5
   ```
   Find the latest `chore: bump version from X.X.X to X.X.X` commit. Check if that commit is already in develop:
   ```bash
   git branch --contains <commit-hash> develop
   ```
   If it is NOT in develop, create a sync branch from develop and cherry-pick ONLY that commit:
   ```bash
   git checkout -b sync/main-to-develop
   git cherry-pick <commit-hash>
   git push origin sync/main-to-develop -u
   gh pr create --base develop --title "Sync - Update develop with main" --body ""
   ```
   Do NOT bring all commits from main — only cherry-pick the version bump commit.
   Do NOT proceed with the release until this sync is merged and develop is pulled again.

3. Read `versions.properties` to get the current version (`versionMajor.versionMinor.versionPatch`)

4. Create the release branch from `main` (increment patch by default):
   ```bash
   git checkout main && git pull
   git checkout -b release/X.X.X
   ```

5. Merge `develop` keeping develop's changes on conflicts:
   ```bash
   git merge develop --strategy-option=theirs
   ```
   For modify/delete conflicts (files deleted in develop but modified in main), resolve by deleting the files (`git rm <file>`), since we keep develop's changes.

6. Update `versions.properties`:
   - Increment `versionCode` by 1
   - Increment `versionPatch` by 1
   - Do NOT change `versionMajor` or `versionMinor` unless explicitly requested

7. Update whatsnew files:
   - Add new changes at the TOP of the file (newest first)
   - Remove the LAST line(s) to keep max 4 lines per file
   - Ask the user what changes to include if not provided

8. Commit (no Claude references):
   ```bash
   git add . && git commit -m "chore: bump version from X.X.X to X.X.X"
   ```

9. Push and create PR:
   ```bash
   git push origin release/X.X.X -u
   gh pr create --base main --title "Release - vX.X.X" --body ""
   ```

10. Return the PR URL when done.