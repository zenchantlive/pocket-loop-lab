---
schema: verification/v0.1
slice: 1
state: reference_main_expanded_edit
status: completed
controller_verdict: pending_controller_review
---

# Slice 1 Reference Main Expanded Edit Comparison

- Source reference: `verification/reference-mockup.jpg`
- Captured screenshot: `verification/snapshots/slice-1-reference-main-expanded-edit.png`
- Capture method: `generated_png_from_deterministic_reference_state_using_stdlib_png_encoder`
- Device/profile assumptions: portrait phone proxy, 540x960. Compose/device capture remains blocked until Android test runtime is available; this artifact verifies deterministic Slice 1 state structure.

## PRD checklist

- Header `Pocket Loop Lab`: represented in Compose source and screenshot proxy.
- `Live Lab Surface`: represented.
- Four LoopPads dominate: yes.
- Pad 1 selected/playing: yes.
- Pad 2 Layering with red/yellow cues: yes.
- Pads 3/4 Empty/Hold to record: yes in source; empty representation in screenshot proxy.
- Transport visible: yes.
- Pad 1 Edit Sheet with Trim/Volume/Speed/Reverse/Overdub/Clear: yes in source and proxy blocks.
- No DAW/AI/social/cloud UI: yes.

## Visual issues

- This is a generated structural screenshot proxy, not a true device screenshot. A real Compose screenshot should replace it when Android instrumentation capture is available.
- Static UI is intentionally approximate and will need visual tuning in later slices.

## Controller decision

Pending controller verification after Slice 1 reviews.
