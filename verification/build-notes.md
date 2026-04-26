# Build Notes

## Slice 0 local validation

Command:

```bash
bash ./gradlew help --no-daemon
```

Result: PASS. Gradle project loaded and `:help` completed successfully. Direct `./gradlew` execution is blocked by Android shared-storage execute restrictions, so use `bash ./gradlew ...` in Termux from this Documents path.

## Slice 0 local APK build attempt

Command:

```bash
bash ./gradlew assembleDebug --no-daemon
```

Result: FAIL, expected/acceptable for Termux environment. Error summary:

```text
Could not determine the dependencies of task ':app:packageDebug'.
Could not create task ':app:compileDebugJavaWithJavac'.
Failed to calculate javaCompiler.
Service 'SystemInfo' is not available (os=Linux ... android ... aarch64, enabled=false).
```

Decision: Do not brute-force local APK assembly. Use `.github/workflows/build-debug-apk.yml` as CI fallback when repository is pushed. Expected artifact: `debug-apk` containing `app-debug.apk`.
