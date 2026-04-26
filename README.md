# Pocket Loop Lab

Slice 0 Android project skeleton for a Kotlin + Jetpack Compose four-pad phone looper.

This repository intentionally contains only the setup, documentation, CI workflow, and screenshot verification harness. Product UI and audio behavior are reserved for later slices.

## Paths

- Canonical Termux path: `/storage/emulated/0/Documents/Pocket Loop Lab`
- Human Android path: `Internal storage / Documents / Pocket Loop Lab`
- Package: `com.pocketlooplab`

## Validate locally

```bash
cd "/storage/emulated/0/Documents/Pocket Loop Lab"
bash ./gradlew help --no-daemon
bash ./gradlew assembleDebug --no-daemon
```

If local Android tooling fails in Termux because of SDK/AAPT2 architecture constraints, use the GitHub Actions workflow in `.github/workflows/build-debug-apk.yml` to build the `debug-apk` artifact.

## Preview harness

Open `preview/index.html` on the phone to see a broad hierarchy placeholder: title, live lab surface, 2x2 loop pads, transport, and Pad 1 edit sheet. The visual preview is a smoke harness only; it is not the app UI implementation.
