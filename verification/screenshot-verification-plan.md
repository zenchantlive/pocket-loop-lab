# Screenshot Verification Plan

## Purpose

Verify Pocket Loop Lab UI work against the locked reference mockup from the beginning of implementation.

## Fixed assumptions

- Human path: Internal storage / Documents / Pocket Loop Lab
- Termux path: /storage/emulated/0/Documents/Pocket Loop Lab
- Reference: verification/reference-mockup.jpg
- Primary device class: portrait Android phone.
- Reference screenshots freeze progress, clock, waveform samples, meter values, and animation.

## Capture methods

1. Compose/instrumented screenshot tests when available.
2. Device/emulator capture with adb screencap when available.
3. Manual Android screenshot imported into verification/snapshots when automation is unavailable.
4. Slice 0 may use generated/preview smoke PNG only to prove harness plumbing.

## Artifact contract

- Screenshot: verification/snapshots/<slice>-<state>.png
- Comparison: verification/comparisons/<slice>-<state>.md
- Slice report: verification/slice-reports/slice-N.md

Every comparison records reference path, captured path, device/profile, capture method, PRD checklist, visual issues, and controller verdict.
