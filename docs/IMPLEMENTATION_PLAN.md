---
schema: output/v0.1
run_id: 2026-04-23_185938_design-pocket-loop-lab-001
team_id: app-factory
step_id: holistic_sliced_implementation_plan
status: completed
created_at: 2026-04-24T19:26:00Z
source_prd: outputs/prd-from-locked-mockup.md
source_reference: verification/reference-mockup.jpg
implementation_status: blocked_until_plan_critique_and_controller_verification
---

# Pocket Loop Lab — Holistic Sliced Implementation Plan

> **For Hermes:** Use `subagent-driven-development` to execute this plan task-by-task after plan critique and controller verification pass. Enforce `test-driven-development` for production behavior.

**Goal:** Build the first Android APK of Pocket Loop Lab: a four-pad phone looper that matches the locked mockup through deterministic screenshot verification.

**Architecture:** Kotlin + Jetpack Compose Android app in a dedicated Documents project folder. The UI is data-driven from a deterministic `PocketLoopLabUiState`; audio is isolated behind an interface so fake/reference audio can power screenshots while native recorder/player work is implemented slice-by-slice.

**Tech Stack:** Kotlin, Jetpack Compose, Gradle/Android plugin, Android microphone APIs/MediaRecorder or AudioRecord decision during audio slice, local file storage, Compose UI tests where available, Termux-compatible screenshot fallback, GitHub Actions APK build fallback if local AAPT2 blocks assembly.

**User-facing project location:** `Internal storage / Documents / Pocket Loop Lab`

**Canonical Termux path:** `/storage/emulated/0/Documents/Pocket Loop Lab`

---

## Global execution rules

1. Do not ask Jordan to make routine implementation decisions. Use PRD + locked mockup + controller judgment.
2. Every slice must pass:
   - targeted tests
   - full available test suite
   - agent spec review
   - agent code quality review
   - controller verification
   - screenshot verification if UI-affecting
3. Agent critiques are evidence, not truth. Controller verifies before patching or proceeding.
4. Keep the app source in its own Documents folder, not only Termux home.
5. If local APK assembly fails due to AAPT2 architecture mismatch, stop brute-forcing and use CI APK build fallback.
6. Commit after each completed slice if git is initialized.
7. Every slice must produce `verification/slice-reports/slice-N.md` with: scope completed, tests run, screenshot artifacts if applicable, subagent review summaries, controller decision, known limitations, and git commit hash if available.

## Screenshot artifact contract

For every UI-affecting slice, save:

```txt
verification/snapshots/<slice>-<state>.png
verification/comparisons/<slice>-<state>.md
```

Each comparison report must include:

- source reference path;
- captured screenshot path;
- device/profile and viewport assumptions;
- capture method and fallback used;
- PRD checklist result;
- visual issues found by agent/vision review;
- controller verdict accepting or rejecting those issues.

## Target project structure

```txt
Internal storage / Documents / Pocket Loop Lab/
├── app/
│   └── src/main/java/com/pocketlooplab/
│       ├── MainActivity.kt
│       ├── ui/
│       │   ├── PocketLoopLabScreen.kt
│       │   ├── components/LoopPadCard.kt
│       │   ├── components/TransportPanel.kt
│       │   ├── components/PadEditSheet.kt
│       │   ├── theme/Theme.kt
│       │   └── reference/ReferenceStates.kt
│       ├── model/LoopModels.kt
│       ├── audio/LoopAudioEngine.kt
│       ├── audio/FakeLoopAudioEngine.kt
│       ├── audio/AndroidLoopAudioEngine.kt
│       └── state/PocketLoopLabViewModel.kt
├── app/src/androidTest/java/com/pocketlooplab/
│   ├── PocketLoopLabUiTest.kt
│   └── ScreenshotCaptureTest.kt
├── preview/
│   ├── index.html
│   └── styles.css
├── verification/
│   ├── reference-mockup.jpg
│   ├── screenshot-verification-plan.md
│   ├── snapshots/
│   └── comparisons/
├── docs/
│   ├── PRD.md
│   ├── BUILDING.md
│   └── IMPLEMENTATION_PLAN.md
├── .github/workflows/build-debug-apk.yml
├── settings.gradle.kts
├── build.gradle.kts
├── gradlew
└── README.md
```

---

## Slice 0 — Project setup + screenshot harness

**Objective:** Create a real Android project and verification harness before UI implementation is trusted.

**Files:**
- Create full project at `Internal storage / Documents / Pocket Loop Lab/`
- Copy: run `verification/reference-mockup.jpg` → project `verification/reference-mockup.jpg`
- Create: Gradle project files, `README.md`, `docs/BUILDING.md`, `.github/workflows/build-debug-apk.yml`
- Create: `preview/index.html` and `preview/styles.css`
- Create: `verification/screenshot-verification-plan.md`

**Tasks:**

1. Scaffold Kotlin/Compose Android project.
2. Add package `com.pocketlooplab`.
3. Add docs explaining local Termux validation vs APK build distinction.
4. Add CI workflow for debug APK.
5. Add phone-openable HTML preview placeholder matching broad locked-mockup hierarchy.
6. Capture a screenshot smoke artifact from the preview or first available app harness:
   - preferred: `verification/snapshots/slice-0-preview-smoke.png`
   - acceptable fallback: manually imported screenshot with the same filename plus notes
7. Save `verification/comparisons/slice-0-harness-smoke.md` documenting reference image availability, captured screenshot availability, capture method, fallback used, device/profile assumptions, and controller decision.
8. Define screenshot strategy:
   - primary: Compose UI test screenshot if supported
   - fallback: run app/debug activity and capture device screen with `adb exec-out screencap -p` when available
   - manual fallback: Android screenshot imported into `verification/snapshots/`
9. Define fixed screenshot device/profile assumptions.
10. Freeze animations/progress in screenshot mode.
11. Add CI artifact expectations: workflow `.github/workflows/build-debug-apk.yml`, artifact name `debug-apk`, expected file `app-debug.apk`.
12. Document any local AAPT2 failure in `docs/BUILDING.md` and `verification/build-notes.md`. GitHub auth/push is not blocking for source creation; workflow file is enough until CI can be run.

**Tests/verification:**

```bash
cd "/storage/emulated/0/Documents/Pocket Loop Lab"
./gradlew help --no-daemon
```

Attempt once:

```bash
./gradlew assembleDebug --no-daemon
```

If AAPT2 architecture mismatch appears, document it and rely on CI.

**Screenshot acceptance:**

- `verification/reference-mockup.jpg` exists in project.
- `verification/screenshot-verification-plan.md` defines capture method and fallback.
- `verification/snapshots/slice-0-preview-smoke.png` or documented equivalent exists.
- `verification/comparisons/slice-0-harness-smoke.md` exists and has controller verdict.
- Preview opens from Android file/browser.
- CI workflow declares `debug-apk` artifact containing `app-debug.apk`.
- `verification/slice-reports/slice-0.md` exists.

---

## Slice 1 — Static visual shell from deterministic reference state

**Objective:** Render the locked mockup structure with fake data and no real audio.

**Files:**
- Create: `model/LoopModels.kt`
- Create: `ui/reference/ReferenceStates.kt`
- Create: `ui/PocketLoopLabScreen.kt`
- Create: `ui/components/LoopPadCard.kt`
- Create: `ui/components/TransportPanel.kt`
- Create: `ui/components/PadEditSheet.kt`
- Create/modify: theme files
- Test: `PocketLoopLabUiTest.kt`

**Reference state:**

- Header `Pocket Loop Lab`.
- Section `Live Lab Surface`.
- Meta `12h 0m 28s`, `LoFi`.
- Pad 1 selected + `Playing`, mint progress, waveform.
- Pad 2 `Layering`, red/yellow cues, `Stop` as action.
- Pads 3/4 `Empty`, `Hold to record`.
- Transport visible.
- `Pad 1 Edit Sheet` with Trim, Volume, Speed, waveform, Reverse, Overdub, Clear.

**TDD tasks:**

1. Write Compose tests asserting required text is visible.
2. Implement static UI until tests pass.
3. Add content descriptions for pads and controls.
4. Capture `reference-main-expanded-edit` screenshot.
5. Save `verification/comparisons/slice-1-reference-main-expanded-edit.md`.
6. Compare screenshot against reference using checklist, not pixel-perfect threshold.
7. Save `verification/slice-reports/slice-1.md`.

**Acceptance:**

- Four pads dominate.
- No DAW/AI/social/cloud UI.
- No blank button or typo artifacts.
- Screenshot comparison saved to `verification/comparisons/slice-1-reference-main.md`.

---

## Slice 2 — LoopPad state model and rendering

**Objective:** Make the UI fully data-driven for all required pad states.

**Files:**
- Modify: `model/LoopModels.kt`
- Modify: `ui/components/LoopPadCard.kt`
- Create/modify: state preview/reference fixtures
- Test: model/state rendering tests and Compose UI tests

**TDD tasks:**

1. Test state labels for `Empty`, `Listening`, `Recording`, `Playing`, `Muted`, `Stopped`, `OverdubArmed`, `Layering`, `MicNeeded`.
2. Test clipping as overlay flag, not base state.
3. Test empty pads show `Hold to record` and do not show active play action.
4. Test selected pad preserves underlying state.
5. Implement rendering.
6. Capture screenshots:
   - `verification/snapshots/slice-2-first-launch-empty.png`
   - `verification/comparisons/slice-2-first-launch-empty.md`
   - `verification/snapshots/slice-2-playing-muted-layering.png`
   - `verification/comparisons/slice-2-playing-muted-layering.md`
7. Save `verification/slice-reports/slice-2.md`.

**Acceptance:**

- Pad state/action copy is never contradictory.
- Pad 2 reference state is exactly `Layering`.
- Selection is visually distinct without replacing state.

---

## Slice 3 — Interaction prototype with fake audio engine

**Objective:** Implement gestures and state transitions using a fake/in-memory audio engine.

**Files:**
- Create: `audio/LoopAudioEngine.kt`
- Create: `audio/FakeLoopAudioEngine.kt`
- Create: `state/PocketLoopLabViewModel.kt`
- Modify UI to call ViewModel actions
- Test: ViewModel unit tests

**TDD tasks:**

1. Test hold empty pad transitions `Empty → Listening → Recording`.
2. Test release recording creates fake loop and transitions to `Playing`.
3. Test tap recorded pad surface toggles `Playing ↔ Muted`.
4. Test edit affordance/long-press selects pad and opens sheet.
5. Test overdub arm transitions `Playing → OverdubArmed → Layering → Playing` with fake audio.
6. Test clear with confirmation/undo returns pad to `Empty`.
7. Implement minimal ViewModel and fake engine.
8. Capture screenshots:
   - `verification/snapshots/slice-3-recording-pad.png`
   - `verification/comparisons/slice-3-recording-pad.md`
   - `verification/snapshots/slice-3-clear-confirmation.png`
   - `verification/comparisons/slice-3-clear-confirmation.md`
9. Save `verification/slice-reports/slice-3.md`.

**Acceptance:**

- Core interaction model works without real audio.
- Screenshot states match PRD.
- No UI action pretends to perform real audio if fake mode is active outside debug/reference context.

---

## Slice 4 — Local audio capture and loop playback

**Objective:** Replace fake core record/play path with local Android audio capture/playback for first APK.

**Files:**
- Create: `audio/AndroidLoopAudioEngine.kt`
- Modify: `AndroidManifest.xml` for microphone permission
- Modify: `PocketLoopLabViewModel.kt`
- Create: permission rationale UI if needed
- Tests: engine interface tests with fake; instrumentation/manual checklist for real audio

**TDD/verification tasks:**

1. Create `docs/audio-engine-decision.md` choosing `MediaRecorder` for simpler file-based looping or `AudioRecord` for lower-level control; document why it satisfies first APK requirements and limitations around latency, sync, overdub, waveform, and meter generation.
2. Test ViewModel behavior for permission denied → `MicNeeded`.
3. Test engine interface contract with fake implementation.
4. Implement microphone permission flow.
5. Implement record-to-local-file for one pad.
6. Implement looping playback for recorded pad.
7. Extend to four pads.
8. Implement mute/unmute against real playback.
9. Capture screenshot:
   - `verification/snapshots/slice-4-permission-needed.png`
   - `verification/comparisons/slice-4-permission-needed.md`
10. Manually verify on device: hold pad, release, loop plays, tap mute/unmute.
11. Save `verification/slice-reports/slice-4.md`.

**Acceptance:**

- First recorded pad can loop locally.
- Four pad slots can store separate clips, even if sync is simple.
- App makes no false zero-latency/perfect-sync claims.
- Audio files remain local.

---

## Slice 5 — Editing controls

**Objective:** Implement first APK edit baseline and honest disabled/prototype states for advanced controls.

**Files:**
- Modify: `PadEditSheet.kt`
- Modify: `LoopModels.kt`
- Modify: audio engine/viewmodel
- Tests: ViewModel edit tests and UI tests

**Functional baseline:**

- Functional: volume adjustment, clear, select/open edit sheet.
- Functional if cheap: reverse and speed.
- May be disabled/prototype-labeled: trim and true overdub if audio engine complexity is high.

**TDD tasks:**

1. Test volume changes stored in selected pad state.
2. Test clear requires confirmation/undo.
3. Test disabled controls are visibly disabled and do not claim success.
4. Implement functional controls incrementally.
5. Capture updated screenshot:
   - `verification/snapshots/slice-5-reference-main-expanded-edit.png`
   - `verification/comparisons/slice-5-reference-main-expanded-edit.md`
6. Save `verification/slice-reports/slice-5.md`.

**Acceptance:**

- Visible edit controls match locked mockup/PRD.
- Nonfunctional controls are honest.
- No blank button.
- Units are correct: volume dB, speed x, trim time/range.

---

## Slice 6 — Polish, accessibility, APK readiness

**Objective:** Prepare first APK for installable testing and tighten screenshot/design match.

**Files:**
- Modify UI/theme/accessibility docs
- Modify README/BUILDING
- Modify CI if needed
- Add final verification reports

**Tasks:**

1. Run UI accessibility pass: content descriptions, font scaling, color-independent state.
2. Tune dark tactile styling toward reference mockup.
3. Run screenshot comparison for all required states and save final reports:
   - `verification/comparisons/slice-6-final-reference-main-expanded-edit.md`
   - `verification/comparisons/slice-6-final-first-launch-empty.md`
   - `verification/comparisons/slice-6-final-recording-pad.md`
   - `verification/comparisons/slice-6-final-playing-muted-layering.md`
   - `verification/comparisons/slice-6-final-permission-needed.md`
   - `verification/comparisons/slice-6-final-clear-confirmation.md`
4. Controller verifies visual comparison claims.
5. Run full tests.
6. Build APK locally or via GitHub Actions.
7. If CI is used, expected artifact is `debug-apk` containing `app-debug.apk`; download/copy it to a human-visible folder when available.
8. Save APK path/documentation if build succeeds.
9. Create final implementation review report.
10. Save `verification/slice-reports/slice-6.md`.

**Acceptance:**

- Required screenshots pass controller verification.
- APK build path is proven: local or CI.
- App launches and core record/play/mute path works.
- Known limitations are documented honestly.

---

## Final integration review

After all slices:

1. Dispatch final integration reviewer.
2. Run full test suite.
3. Inspect git diff/status.
4. Verify screenshot comparison artifacts.
5. Install/open APK if available.
6. Produce final report with:
   - project path in human Android terms
   - APK/preview path
   - implemented features
   - deferred features
   - screenshot verification verdict
   - known risks

## Initial blocker policy

Proceed autonomously unless blocked by:

- missing Android SDK/tooling that cannot be installed without Jordan action
- GitHub authentication needed for CI publishing
- microphone/audio APIs blocked by device policy
- a taste/business decision not already determined by the locked mockup or PRD
