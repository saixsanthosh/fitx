# Fitx Developer Setup

This file is for developers building Fitx locally.

## 1. Requirements

- Android Studio (latest stable)
- JDK 17 (bundled with Android Studio is fine)
- Android SDK installed through Android Studio
- Internet for Gradle dependency download

## 2. Open project

Open this folder in Android Studio and sync Gradle.

## 3. Local API key

Add USDA key in your user or project Gradle properties:

```properties
USDA_API_KEY=your_api_key_here
```

## 4. Firebase (local only)

1. In Firebase console, create/select project.
2. Enable Authentication -> Sign-in method -> Google.
3. Register Android app with package: `com.fitx.app`.
4. Download `google-services.json`.
5. Place it at:

```text
app/google-services.json
```

Do not commit this file.

## 5. Release signing (local only)

### Create release keystore (one-time)

```powershell
keytool -genkeypair -v -keystore C:\keys\fitx-release.jks -alias fitx -keyalg RSA -keysize 2048 -validity 10000
```

### Create local `keystore.properties`

Copy `keystore.properties.example` to `keystore.properties` and fill real values.

Example:

```properties
storeFile=C:\\keys\\fitx-release.jks
storePassword=YOUR_STORE_PASSWORD
keyAlias=fitx
keyPassword=YOUR_KEY_PASSWORD
```

Do not commit this file.

## 6. Build commands

Debug build:

```powershell
.\gradlew.bat :app:assembleDebug
```

Release artifact package for GitHub:

```powershell
.\gradlew.bat :app:prepareGithubRelease
```

Release outputs:

```text
release-artifacts/fitx-vX.Y.Z-release.apk
release-artifacts/SHA256.txt
```

## 7. GitHub release

1. Push code to public GitHub repo.
2. Create tag (example `v1.0.0`).
3. Create GitHub Release for that tag.
4. Upload APK + SHA256.txt.
