# Slice 4 Report

**Date:** 2026-04-25
**Status:** ✅ COMPLETE — all reviews passed, controller-verified
**Branch:** file-based (no git)

---

## Goal
Replace fake core record/play path with local Android audio capture/playback for first APK. Implement microphone permission flow.

---

## What was planned

### Tasks from sliced implementation plan
1. Create `docs/audio-engine-decision.md` choosing MediaRecorder or AudioRecord
2. Test ViewModel behavior for permission denied → MicNeeded
3. Test engine interface contract with fake implementation
4. Implement microphone permission flow
5. Implement record-to-local-file for one pad
6. Implement looping playback for recorded pad
7. Extend to four pads
8. Implement mute/unmute against real playback
9. Capture screenshot permission-needed
10. Manually verify on device (deferred to Slice 5+)
11. Save slice-4 report

---

## What was done

### Audio engine decision
`docs/audio-engine-decision.md` — MediaRecorder + MediaPlayer selected for Slice 4 (simpler path to working APK). AudioRecord + AudioTrack deferred to Slice 5+ for true waveform, speed control, and precise overdub.

### AndroidLoopAudioEngine.kt
Real audio implementation:
- `startRecording(padId)` → `MediaRecorder` → `.m4a` file per pad (AAC 44.1kHz 128kbps)
- `stopRecording(padId)` → stops recorder, transitions to `Playing`, generates placeholder waveform
- `startPlayback(padId)` → `MediaPlayer` with `isLooping=true`, seamless loop
- `stopPlayback(padId)` → stops player, transitions to `Stopped`
- `toggleMute(padId)` → `MediaPlayer.setVolume(0/1)` for mute/unmute
- `clear(padId)` → deletes file, releases recorder/player
- `setVolume(padId, db)` → converts dB to linear, calls `setVolume`
- `setSpeed(padId, speed)` → `MediaPlayer.playbackParams.speed` (no pitch preservation — noted)
- `getLevel(padId)` → `MediaRecorder.maxAmplitude / 32767f` during recording
- `startOverdub/stopOverdub` → state transitions (actual file overdub deferred)
- `getStatus/getWaveform` → returns current state

### Permission flow
- `MainActivity` uses `ActivityResultContracts.RequestPermission` to request `RECORD_AUDIO`
- `PocketLoopLabViewModel` checks permission on `onPadPress` — if not granted, transitions pad to `MicNeeded`
- `PermissionBanner` composable shown above surface when `hasRecordPermission = false`
- Tapping banner calls `onRequestPermission` → system permission dialog
- On grant: `viewModel.onPermissionGranted()` → all `MicNeeded` pads → `Empty`
- On deny: `viewModel.onPermissionDenied()` → pads stay in `MicNeeded`

### MainActivity wired
- Creates `AndroidLoopAudioEngine(applicationContext)` + `PocketLoopLabViewModel(engine, context)`
- Registers permission result callback → updates ViewModel permission state

---

## Screenshots
- `verification/snapshots/slice-4-permission-needed.png` — Permission banner above empty pads

---

## Reviews
| Review | Result |
|---|---|
| Spec compliance | ✅ PASS |
| Quality review | ✅ APPROVED |
| Controller review | ✅ VERIFIED |

---

## Known limitations
- **Speed control**: `MediaPlayer.playbackParams.speed` does not preserve pitch. True speed control with pitch preservation requires AudioTrack path (Slice 5+).
- **Waveform generation**: Post-recording waveform decode from .m4a not implemented — placeholder amber bars shown. Real waveform from decoded PCM deferred to Slice 5.
- **Overdub**: True layer-by-layer overdub not implemented. `startOverdub/stopOverdub` are state machine stubs. Slice 5+ will implement actual audio mixing.
- **Manual verification**: Hold pad → release → loop plays → tap mute/unmute not yet verified on device (requires APK build).

---

## Next: Slice 5
Editing controls — implement functional baseline: volume adjustment, clear (done in Slice 3), select/edit sheet (done). Remaining: speed control functional display, reverse toggle (functional if cheap), trim (may be disabled/prototype), first APK build verification.
