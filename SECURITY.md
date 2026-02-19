# Fitx Security Policy (Repository Hygiene)

## Never commit these files

- `app/google-services.json`
- `local.properties`
- `keystore.properties`
- `*.jks`, `*.keystore`
- Build/IDE generated folders (`.gradle/`, `.idea/`, `build/`, `app/build/`)

## Why

These files contain machine-specific settings, Firebase identifiers, and signing secrets.
Leaking signing keys can permanently break secure app updates.

## Signing key policy

- Use one permanent release keystore for all production APK updates.
- Keep secure backups in at least two safe locations.
- Do not share keystore or passwords in chat/email.

## Firebase policy

- Keep Firebase config and auth setup local.
- Limit console access to trusted maintainers.
- Rotate exposed credentials immediately if leaked.

## If a secret was leaked

1. Remove leaked file from repository and history.
2. Rotate exposed secrets/passwords immediately.
3. Replace compromised keys where possible.
4. Publish a clean release signed with your trusted key.
