# Fitx Android App

Fitx is an Android fitness app built with Kotlin + Jetpack Compose, MVVM, Clean Architecture, Room, Hilt, WorkManager, Retrofit, DataStore, Firebase Auth, and Firestore sync.

## Third-party source merge (Metrolist)

Metrolist source code has been merged into this repository under:

- `third_party/metrolist/`

Upstream project:

- https://github.com/MetrolistGroup/Metrolist
- Upstream commit snapshot: `8cad78ba3bf8b03b6aa3fc920bc003e2843ccdc4`
- Upstream license: GNU GPL v3 (`third_party/metrolist/LICENSE`)

This folder is kept for source integration and reuse planning. It is not automatically built by Fitx Gradle tasks yet.

## Free distribution model

Fitx is distributed for free outside Play Store using:
- Public GitHub repository for source code
- GitHub Releases for APK downloads
- One main downloadable file per release: `fitx-vX.Y.Z-release.apk`

## User install guide (APK)

1. Open the project **Releases** page on GitHub.
2. Download the latest `fitx-vX.Y.Z-release.apk`.
3. On Android, enable **Install unknown apps** for your browser/files app.
4. Open the APK and install.

## User update guide

1. Download the newest release APK from GitHub Releases.
2. Install it directly over the existing Fitx app.
3. Keep the same package name and signing key to preserve app updates/data.

## In-app update popup

Fitx checks `version.json` from this repository at startup (after sign-in) and compares it with `BuildConfig.VERSION_NAME`.

- If `latestVersion` is newer, the app shows an update dialog with message + download button.
- Last prompted version is saved locally, so users are not repeatedly prompted for the same version.
- Config file path: `version.json` (repo root)

## Optional integrity check (recommended)

Each release also includes `SHA256.txt`.

On Windows:

```powershell
certutil -hashfile fitx-v1.0.0-release.apk SHA256
```

Compare the result with the hash in `SHA256.txt`.

## Maintainer release workflow

1. Bump `versionCode` and `versionName` in `app/build.gradle.kts`.
2. Ensure signing secrets exist locally in `keystore.properties` (not committed).
3. Run release preparation:

```powershell
.\scripts\prepare-release.ps1
```

This script builds artifacts and updates `version.json` to match `versionName`.

4. Build release artifacts manually if needed:

```powershell
.\gradlew.bat :app:prepareGithubRelease
```

5. Output files are generated in `release-artifacts/`:
- `fitx-vX.Y.Z-release.apk`
- `SHA256.txt`
6. Create GitHub tag/release (example `v1.0.0`) and upload both files.
7. Commit and push updated `version.json` after validating links.

## Main modules

- User Profile + HealthCalculator (BMI/BMR/TDEE/calorie target/goal projection)
- Activity Tracking (walking/cycling, GPS path preview map, primary metric rotation, history + detail route map)
- Activity Finish Summary (dynamic appreciation message, count-up primary stat, share-card PNG export)
- Daily Weight Tracker (CRUD, weekly average, trend chart)
- Workout Planner (templates, logs, history, personal records)
- Routine Habits (daily streak tracking)
- Date-Based Planner (tasks by date, daily repeats)
- Nutrition Tracking (USDA pagination, custom foods, saved serving presets, local meal logs)
- Nutrition barcode lookup (manual code + camera scan)
- Today Score (activity, nutrition, tasks, consistency)
- Weekly Insights screen (distance, sessions, calories, steps, weight trend)
- Hybrid Online/Offline Sync (Room local-first + Firestore sync queue + WorkManager)
- Dashboard + Settings + smart reminder tuning (adaptive weight time + hydration cadence)
- Quick-add bottom sheet shortcuts (task/weight/meal/activity/insights)
- Home widget (pending tasks, completion progress, quick complete for top tasks)
- Google-only Firebase login

## Developer docs

- Local setup: `SETUP.md`
- Security rules: `SECURITY.md`
