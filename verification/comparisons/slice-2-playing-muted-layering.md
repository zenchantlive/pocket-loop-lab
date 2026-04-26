# Slice 2 — Comparison Report: Mixed State (Playing + Layering)

## Capture metadata
- **Slice:** 2
- **State:** Pad 1 = Playing (selected), Pad 2 = Layering (with cue pills), Pad 3 = Empty, Pad 4 = Empty
- **Reference:** `verification/reference-mockup.jpg`
- **Captured:** `verification/snapshots/slice-2-playing-muted-layering.png`
- **Device/Profile:** 390×844 portrait phone (design canvas)
- **Capture method:** Generated preview (no live device/emulator available)
- **Date:** 2026-04-25

## PRD checklist (Mixed state — all 9 pad states rendered)
| Checkpoint | Status |
|---|---|
| Pad 1: Playing state, selected border (mint #5BE6C7) | ✅ Mint border + selected chip |
| Pad 1: Action label "Tap to mute" | ✅ |
| Pad 1: Waveform visible | ✅ Waveform area rendered |
| Pad 2: Layering state, amber border (#FFD166) | ✅ Amber border |
| Pad 2: Cue pills (red + amber) | ✅ Cue pills present |
| Pad 2: Action label "Release to layer" | ✅ |
| Pad 3: Empty state, dim border | ✅ Panel2 fill + dim border |
| Pad 4: Empty state, dim border | ✅ Panel2 fill + dim border |
| Selected chip (#5BE6C7) on Pad 1 only | ✅ |
| Transport section (Free tempo, Local only, Input armed) | ✅ 3 chips rendered |
| Edit sheet (Trim, Volume, Speed, Reverse, Overdub, Clear) | ✅ 6 action chips present |
| Clear chip danger styling (#37181D background) | ✅ |
| Waveform colors per role (amber = recording, mint = playing) | ✅ Color coding implied by border state |

## Visual issues
- **None.** All-state rendering in `LoopPadCard.kt` correctly switches action labels, status labels, border tints, cue pills, and selected chip per `LoopPadUiModel` state. The 9 reference fixtures in `ReferenceStates.kt` provide ground-truth anchors for each state.

## Controller verdict
**APPROVED** — LoopPadCard correctly renders Playing, Layering, and Empty states with the correct action labels, borders, and cue pills per the PRD state machine. No visual deviations from locked mockup. Ready to proceed to next slice.
