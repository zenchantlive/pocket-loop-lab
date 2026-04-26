# Building Pocket Loop Lab

## Local Termux validation

From the project root:

```bash
cd "/storage/emulated/0/Documents/Pocket Loop Lab"
bash ./gradlew help --no-daemon
```

This verifies that the Gradle wrapper and project structure can be loaded.

## Local debug APK build

Attempt once when Android SDK tooling is available:

```bash
bash ./gradlew assembleDebug --no-daemon
```

Termux on Android may fail during Android resource packaging if the Gradle Android Plugin downloads an AAPT2 binary for an unsupported host architecture or if no Android SDK is configured. Do not brute-force installs. Record the exact failure in `verification/build-notes.md` and use CI as the build fallback.

## CI fallback

The workflow `.github/workflows/build-debug-apk.yml` builds the debug APK on Ubuntu and uploads artifact:

- Artifact name: `debug-apk`
- Expected file: `app-debug.apk`

No GitHub authentication is required to keep this workflow file in the source tree. Pushing/running CI is a separate repository operation.

## Slice 0 status

A local validation/build attempt was performed during Slice 0. See `verification/build-notes.md` for exact command output summaries.
