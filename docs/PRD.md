---
schema: output/v0.1
run_id: 2026-04-23_185938_design-pocket-loop-lab-001
team_id: app-factory
step_id: prd_from_locked_mockup
status: completed
created_at: 2026-04-24T19:17:34Z
source_reference: verification/reference-mockup.jpg
implementation_planning: next_gate_after_critique_and_controller_verification
---

# Pocket Loop Lab — PRD From Locked Mockup

## 1. Product summary

Pocket Loop Lab is a phone-native four-slot sound looper. The user captures short sounds into large tactile loop pads, then plays, mutes, overdubs, edits, and clears those loops without entering a DAW-style workspace.

The locked reference mockup is the source-of-truth design target for the first APK. It shows a dark, tactile, field-recorder-like loop surface with four large LoopPad cards, a transport row, and a contextual Pad 1 edit sheet. The implementation should back into that visible product instead of generating more design variants.

### MVP promise

> Open the app, hold an empty LoopPad to record a sound, release to loop it, tap recorded pads to mute/play, select a pad to edit simple playback properties, and verify the resulting UI against the locked mockup with screenshots.

## 2. Source-of-truth design interpretation

### Literal from the locked mockup

The first APK should preserve these visible structures:

- Dark Android portrait app surface.
- Header with app identity: `Pocket Loop Lab`.
- Main section labeled `Live Lab Surface`.
- Session meta area showing elapsed/session time and a mode label such as `LoFi`.
- A 2x2 grid of four large rounded LoopPad cards.
- Each LoopPad contains a circular/tactile recording/playback surface.
- Pad 1 is selected/active and tied to the bottom edit sheet.
- Pad 1 has a mint/turquoise loop progress ring.
- Pad 2 communicates recording/overdub activity with red/yellow indicators.
- Pad 3 and Pad 4 communicate empty/ready-to-record behavior with `Hold to Record`/`Hold to record` copy.
- A `Transport` section exists beneath the pad grid.
- A bottom `Pad 1 Edit Sheet` exists with trim, volume, speed, waveform, reverse, overdub, and clear controls.
- Visual style: dark, rounded, tactile, hardware/field-recorder inspired, high-contrast labels, soft shadows, mint progress, red recording, yellow overdub, blue waveform/meter.

### Normalized from mockup artifacts

The mockup contains generator artifacts and contradictions. The implementation must correct them rather than reproduce nonsense exactly.

| Mockup artifact | Product decision |
|---|---|
| Pad 2 shows both `Recording / Overdub` and `Stopped` | Pad 2 state is `Layering` or `Overdub armed`; `Stop` is an action, not simultaneous stopped state. |
| Transport label appears like `Plas/Foward` | Use `Play`, `Forward`, or omit forward from MVP; no typo. |
| `Trim` and `Speed` appear near dB values | Trim is time/range; Volume uses dB; Speed uses `0.5x`, `1x`, `2x`. |
| Blank edit-sheet button | Remove it from MVP. |
| Empty pads also show `Play`/`Muted`/`+6dB` | Empty pads prioritize `Empty` and `Hold to record`; no play action until audio exists. |
| Generic `Status`, `Time`, `Levels` labels | Replace with functional labels only when useful: state chip, duration, gain, or input/output meter. |
| Phone frame/status bar | Decorative for mockup; native app must respect Android safe areas but does not implement fake phone chrome. |

## 3. Target user and jobs

### Target user

A casual sound tinkerer using an Android phone: someone who wants to capture voice, claps, table taps, room sounds, short melodies, or found sounds quickly and layer them into a tiny loop performance.

### Jobs to be done

- Capture a sound quickly without setup.
- Understand which pads contain audio and which are empty.
- Hear loops play in sync.
- Mute/unmute loops while performing.
- Add a simple overdub/layer to an existing loop.
- Adjust a selected loop enough to make it usable: trim, volume, speed, reverse, clear.
- Keep everything local on-device for the MVP.

## 4. First-run path

1. User opens app.
2. App requests microphone permission only when recording is attempted or after a clear rationale.
3. Four empty pads are visible.
4. Empty pads show `Empty` plus `Hold to record`.
5. User holds any empty pad.
6. Pad enters `Listening`/`Recording` state with red record cue and input meter.
7. User releases.
8. App closes the first loop, starts playback, and establishes the master cycle length.
9. The selected pad shows a playing progress ring and may expose `Pad 1 Edit Sheet`.
10. User can tap recorded pad to mute/unmute or open edit controls.

## 5. MVP scope

### In scope for first APK

- Single main screen.
- Four LoopPads.
- Local microphone recording.
- Local loop playback.
- First pad establishes master loop cycle.
- Later pads can record to the existing cycle length or simple bounded loop length.
- Pad states: `Empty`, `Listening`, `Recording`, `Playing`, `Muted`, `Overdub armed`, `Layering`, `Stopped`, `Mic needed`, `Too loud`.
- Pad selection.
- Bottom edit sheet for selected recorded pad.
- Edit controls: trim range, volume, speed `0.5x/1x/2x`, reverse, overdub arm, clear with confirmation/undo.
- Transport: play/stop all, loop/cycle indicator, clear selected/all with guard, input or output meter.
- Screenshot verification harness from the beginning.

### First APK functional baseline

The first APK must prioritize the core recorder over a full audio editor:

- Must be functional: microphone recording, loop playback, mute/unmute, pad selection, clear selected pad with guard/undo, and volume adjustment for recorded pads.
- Must be represented in the UI: trim, speed, reverse, and overdub, because they are visible in the locked mockup.
- May be disabled/prototype-labeled in APK v0.1 if not implemented yet: trim, speed, reverse, and true overdub/layer mixing. Any nonfunctional control must look disabled or show concise unavailable copy; it must not pretend to work.
- Implementation planning may promote any represented control to functional if cheap, but may not delay the core record/play loop for advanced editing.

### Out of scope for first APK

- Cloud sync, accounts, social sharing, feeds, likes, comments.
- AI generation or AI editing.
- DAW timeline, piano roll, arrangement view, plugin rack, automation lanes.
- Marketplace, packs, or subscriptions.
- Multi-track mixer with sends/buses.
- Advanced quantization, tempo detection, time-stretch quality guarantees.
- Project library beyond local current session persistence unless trivial.
- Pixel-perfect reproduction of image-generator artifacts.

## 6. Screen inventory and component hierarchy

### MVP screens

1. `PocketLoopLabScreen` — main performance/edit screen.
2. Android microphone permission rationale/dialog flow.
3. Optional debug/reference screenshot activity or preview route for verification.

### Main screen hierarchy

```txt
PocketLoopLabScreen
├── SystemSafeArea
├── AppHeader
│   ├── MenuButton or local utility button
│   └── AppTitle: Pocket Loop Lab
├── LiveLabSurfaceHeader
│   ├── SectionTitle: Live Lab Surface
│   └── SessionMeta: elapsed time + mode label
├── LoopPadGrid
│   ├── LoopPadCard Pad 1
│   ├── LoopPadCard Pad 2
│   ├── LoopPadCard Pad 3
│   └── LoopPadCard Pad 4
├── TransportPanel
│   ├── TransportButton PlayStop
│   ├── TransportButton LoopCycle
│   ├── TransportButton Rewind/Reset optional
│   ├── TransportButton Clear guarded
│   └── AudioMeter waveform/meter
└── PadEditSheet
    ├── SheetHeader: Pad N Edit Sheet
    ├── TrimControl
    ├── VolumeControl
    ├── SpeedSelector
    ├── WaveformPreview
    ├── ReverseButton
    ├── OverdubButton
    └── ClearButton
```

## 7. Copy rules

### Required exact app copy

Prefer sentence case unless a chip style requires title case.

- `Pocket Loop Lab`
- `Live Lab Surface`
- `Empty`
- `Hold to record`
- `Listening`
- `Recording`
- `Release to loop`
- `Playing`
- `Tap to mute`
- `Muted`
- `Tap to play`
- `Overdub armed`
- `Hold to layer`
- `Layering`
- `Stopped`
- `Transport`
- `Pad 1 Edit Sheet` / `Pad 2 Edit Sheet` etc.
- `Trim`
- `Volume`
- `Speed`
- `Reverse`
- `Overdub`
- `Clear`
- `Mic needed`
- `Too loud`

### Copy corrections

- Use `Hold to record`, not inconsistent capitalization unless platform style requires title case.
- Use `Layering`, not `Recording / Overdub`, when adding audio over an existing loop.
- Do not show `Stopped` on an actively recording or layering pad.
- Do not label speed values in dB.
- Do not show placeholder labels like `Status`, `Time`, or `Levels` unless they are paired with actual values.

## 8. LoopPad state machine

### State definitions

```txt
Empty
  No audio exists. Primary action: hold to record.

Listening
  User is holding an empty pad; mic/input wake-up before actual capture.

Recording
  Audio capture is active. Release closes loop.

Playing
  Audio exists and is audible in current global transport cycle.

Muted
  Audio exists but is silenced. Tap to play/unmute.

Stopped
  Audio exists but global transport is stopped.

Overdub armed
  Audio exists and the pad is guarded for layer capture.

Layering
  Audio is being captured over existing loop. Release closes the layer.

Mic needed
  Recording attempted without microphone permission.

Too loud
  Input is clipping or near clipping.
```

### State transitions

```txt
Empty --hold--> Listening --capture-start--> Recording --release--> Playing
Playing --tap--> Muted
Muted --tap--> Playing
Playing --global-stop--> Stopped
Muted --global-stop--> Stopped muted variant
Stopped --global-play--> Playing or Muted depending mute flag
Playing --arm-overdub--> Overdub armed --hold--> Layering --release--> Playing
Any recorded state --clear-confirmed--> Empty
Any recording attempt without permission --> Mic needed
Recording/Layering with clipped input --> Too loud overlay while preserving base state
```

### Interaction rules

- Hold empty pad records into that pad.
- Release while recording closes the loop and starts playback.
- Tap the main circular pad surface on a recorded pad toggles mute/play.
- Tap the card header/edit affordance, the bottom sheet affordance, or long-press the recorded pad selects the pad and opens/focuses the edit sheet.
- If a pad is already selected, tapping its main circular surface still performs the live action (mute/play) rather than reselecting it.
- Overdub requires explicit arm before capture.
- Clear requires confirmation or undo.
- Only one pad can be actively `Recording` or `Layering` at a time.
- Empty pads should not show active play controls.

## 9. Frontend state schema

Kotlin-style data model for Compose implementation:

```kotlin
data class PocketLoopLabUiState(
    val session: LoopSessionUi,
    val pads: List<LoopPadUi>,
    val selectedPadId: String?,
    val transport: TransportUi,
    val editSheet: PadEditSheetUi,
    val permission: MicPermissionUi,
    val screenshotMode: Boolean = false
)

data class LoopSessionUi(
    val elapsedMs: Long,
    val modeName: String = "LoFi",
    val masterCycleMs: Long? = null
)

data class LoopPadUi(
    val id: String,
    val index: Int,
    val label: String,
    val state: LoopPadState,
    val selected: Boolean,
    val hasAudio: Boolean,
    val muted: Boolean,
    val durationMs: Long,
    val positionMs: Long,
    val progress: Float,
    val gainDb: Float,
    val speed: PlaybackSpeed,
    val reversed: Boolean,
    val trimStartMs: Long,
    val trimEndMs: Long,
    val waveformPeaks: List<Float>,
    val inputLevel: Float = 0f,
    val clipping: Boolean = false // overlay/flag; not a mutually exclusive base state
)

enum class LoopPadState {
    Empty, Listening, Recording, Playing, Muted, Stopped,
    OverdubArmed, Layering, MicNeeded
}

enum class PlaybackSpeed { Half, Normal, Double }

data class TransportUi(
    val playing: Boolean,
    val looping: Boolean,
    val positionMs: Long,
    val inputLevel: Float,
    val outputLevel: Float,
    val clearAvailable: Boolean
)

data class PadEditSheetUi(
    val visible: Boolean,
    val expanded: Boolean,
    val selectedPadId: String?,
    val clearConfirming: Boolean = false
)

data class MicPermissionUi(
    val granted: Boolean,
    val shouldShowRationale: Boolean,
    val permanentlyDenied: Boolean
)
```

### Deterministic reference state

The app must expose a deterministic reference state for screenshot testing:

- App title `Pocket Loop Lab`.
- Section title `Live Lab Surface`.
- Elapsed display `12h 0m 28s` or stable mock value.
- Mode `LoFi`.
- Pad 1 selected, `Playing`, progress around 70–80%, gain `+6dB`, waveform visible.
- Pad 2 `Layering`, red/yellow cues visible, with `Stop` treated as an action rather than a contradictory `Stopped` state.
- Pad 3 `Empty`, `Hold to record`.
- Pad 4 `Empty`, `Hold to record`.
- Transport visible.
- Pad 1 edit sheet visible with trim, volume, speed, waveform, reverse, overdub, clear.

## 10. Local data/audio model

### Persistent entities

```kotlin
data class LoopProject(
    val id: String,
    val createdAtMs: Long,
    val updatedAtMs: Long,
    val modeName: String,
    val masterCycleMs: Long?
)

data class LoopClip(
    val padId: String,
    val audioFilePath: String?,
    val waveformPeaks: List<Float>,
    val durationMs: Long,
    val trimStartMs: Long,
    val trimEndMs: Long,
    val gainDb: Float,
    val speed: PlaybackSpeed,
    val reversed: Boolean,
    val muted: Boolean
)
```

### Storage requirements

- Audio files are local-only for MVP.
- Waveform peaks are generated locally from recorded audio.
- Project/session state should survive app relaunch if feasible.
- No uploads or accounts.

## 11. Audio behavior requirements

- App records from Android microphone with runtime permission.
- First recorded pad sets master cycle length.
- Playback loops continuously while global transport is playing.
- Later pad recordings should align to master cycle for MVP if feasible; otherwise document simplified behavior and visually avoid false precision claims.
- Muting is immediate and reversible.
- Overdub/layering must be explicit and guarded.
- Clear removes pad audio after confirmation or with undo.
- Input clipping should be detected enough to show `Too loud` warning.
- Latency-sensitive claims are prohibited; do not promise zero latency.

## 12. Edit sheet requirements

The edit sheet is contextual to the selected pad.

### Empty pad behavior

If selected pad has no audio:

- Do not show full edit controls.
- Show `Record this pad first` or keep sheet collapsed.

### Recorded pad behavior

For a recorded pad, show:

- `Pad N Edit Sheet`.
- Trim control with start/end semantics.
- Volume control in dB.
- Speed selector: `0.5x`, `1x`, `2x`.
- Waveform preview.
- Reverse toggle.
- Overdub arm button.
- Clear button with guard/undo.

## 13. Transport and meter requirements

### MVP transport

- Play/stop all active loops.
- Show cycle/progress summary when a loop exists.
- Clear selected pad or clear all only with guard.
- Optional rewind/reset-to-cycle-start if cheap.

### Meter

The blue waveform/meter in the mockup should be implemented as a functional input/output meter, not decoration.

- During recording: prioritize input level.
- During playback: output level or selected pad waveform may be shown.
- Clipping pairs visual indicator with text `Too loud`.

## 14. Accessibility and permissions

- All LoopPads must be large touch targets.
- Do not rely on color alone; state is always represented by text/icon/shape/motion.
- Support font scaling by preserving state/action text and dropping low-priority metadata first.
- Provide content descriptions for pads and controls.
- Provide reduced-motion behavior for progress/orbit animations.
- Microphone permission rationale must be clear: the app records local sounds into pads.
- No cloud/account language in permission copy.

## 15. Screenshot verification contract

Screenshot verification is a core product requirement, not a later polish task.

### Required artifacts

- `verification/reference-mockup.jpg` — locked target image.
- `verification/screenshot-verification-plan.md` — implementation-specific screenshot process.
- `verification/snapshots/` — captured app screenshots.
- `verification/comparisons/` — comparison notes, visual diffs, and agent/vision reviews.

### Operational constraints

- Use one fixed reference device profile for automated screenshots before comparing visuals, preferably a portrait phone size close to the locked mockup.
- Freeze or deterministically set animations, progress rings, waveform samples, meters, elapsed time, and selected state during reference screenshots.
- Use stable fonts/theme tokens in screenshot mode; no randomized waveform or clock values.
- Capture method may be Compose screenshot tests, emulator/device `adb`/screencap, or a documented Termux-compatible fallback. The implementation plan must choose the first available method and define the fallback.
- Store every comparison with the app screenshot, reference mockup, comparison notes, and controller decision.

### Required screenshot states

1. `reference-main-expanded-edit` — deterministic state matching the locked mockup.
2. `first-launch-empty` — four empty pads, no fake audio.
3. `recording-pad` — one pad actively recording.
4. `playing-muted-layering` — mixed pad states.
5. `permission-needed` — microphone blocked/rationale state.
6. `clear-confirmation` — destructive action guard.

### Pass/fail criteria

Early screenshot verification is not pixel-perfect. PASS requires:

- Header and `Live Lab Surface` hierarchy are present.
- Four LoopPads dominate the screen.
- Pad states match deterministic reference data.
- Empty pads show `Hold to record`.
- Selected Pad 1 visually connects to edit sheet.
- Transport row is visible and secondary to pads.
- Edit sheet controls match PRD and do not include blank/generated artifacts.
- Visual style remains dark, tactile, rounded, field-recorder/recording-object inspired.
- No DAW timeline, piano roll, mixer sprawl, AI/social/cloud UI, or generic boilerplate screen.
- Required text is readable.

FAIL if:

- The implementation drifts into a generic dashboard or DAW.
- There are fewer/more than four primary pads.
- Pad state/action copy is contradictory.
- Empty pads look disabled or playable.
- Screenshot comparison identifies major layout hierarchy mismatch and controller verification agrees.

### Agent review rule

Vision/agent screenshot comparisons are evidence, not truth. The controller must verify comparison claims before requiring changes.

## 16. First APK acceptance criteria

The first APK is acceptable when:

- It installs and launches on Android.
- It displays the main Pocket Loop Lab screen.
- It can render the deterministic reference screenshot state.
- Screenshot verification artifacts exist.
- Microphone permission flow works.
- Holding an empty pad records audio.
- Releasing creates a loop and starts playback.
- Tapping recorded pad mutes/unmutes.
- Selecting a recorded pad opens the edit sheet.
- At least volume, speed, reverse, overdub arm, and clear are represented; if any are nonfunctional in first APK, they must be visibly disabled or documented as deferred.
- No locked-design contradiction remains unaddressed.
- Tests and screenshot verification pass for implemented slices.

## 17. Risks and non-blocking questions

### Risks

- Real-time audio latency may be challenging on arbitrary Android devices.
- Screenshot tooling in Termux/Android may require fallback to `adb`/device screencap, Compose test capture, or manual screenshot import.
- Pixel-perfect comparison to an image-generator mockup is not appropriate early.
- The locked mockup shows a fairly expanded edit sheet; implementation must balance that with keeping pads dominant.

### Non-blocking decisions for implementation plan

- Choose exact Android stack and screenshot method during implementation planning.
- Decide whether first APK implements true overdub mixing or a clearly labeled arm/layer prototype.
- Decide whether trim is functional in first APK or represented as disabled until audio engine supports it.

These decisions should be made in the holistic sliced implementation plan, not by asking Jordan unless they materially change the product promise.
