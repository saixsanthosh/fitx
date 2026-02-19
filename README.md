# Fitx Android App

Fitx is an Android fitness app built with Kotlin + Jetpack Compose, MVVM, Clean Architecture, Room, Hilt, WorkManager, Retrofit, DataStore, Firebase Auth, and Firestore sync.

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
3. Build release artifacts:

```powershell
.\gradlew.bat :app:prepareGithubRelease
```

4. Output files are generated in `release-artifacts/`:
- `fitx-vX.Y.Z-release.apk`
- `SHA256.txt`
5. Create GitHub tag/release (example `v1.0.0`) and upload both files.

## Main modules

- User Profile + HealthCalculator (BMI/BMR/TDEE/calorie target/goal projection)
- Activity Tracking (walking/cycling, GPS + steps, foreground service, history)
- Daily Weight Tracker (CRUD, weekly average, trend chart)
- Workout Planner (templates, logs, history)
- Routine Habits (daily streak tracking)
- Date-Based Planner (tasks by date, daily repeats)
- Nutrition Tracking (USDA FoodData Central + local meal logs)
- Hybrid Online/Offline Sync (Room local-first + Firestore sync queue + WorkManager)
- Dashboard + Settings + Reminder notifications (weight/water)
- Google-only Firebase login

## Developer docs

- Local setup: `SETUP.md`
- Security rules: `SECURITY.md`
