# Slice 4 — Comparison Report: Permission Needed

## Capture metadata
- **Slice:** 4
- **State:** First launch — RECORD_AUDIO permission not yet granted, permission banner shown
- **Reference:** `verification/reference-mockup.jpg`
- **Captured:** `verification/snapshots/slice-4-permission-needed.png`
- **Device/Profile:** 390×844 portrait phone (design canvas)
- **Capture method:** Generated preview
- **Date:** 2026-04-25

## PRD checklist (Permission flow)
| Checkpoint | Status |
|---|---|
| Permission banner shown when RECORD_AUDIO not granted | ✅ |
| Banner shows "Mic access required" rationale | ✅ |
| Banner tap triggers system permission request | ✅ (wired via `onRequestPermission` callback) |
| Pad states show `MicNeeded` when tapped without permission | ✅ (`onPadPress` guards with permission check → `MicNeeded`) |
| After grant: all `MicNeeded` pads transition to `Empty` | ✅ (`onPermissionGranted()` transitions all `MicNeeded` → `Empty`) |
| After deny: pads stay in `MicNeeded` | ✅ (`onPermissionDenied()` called, state preserved) |
| All 4 pads in Empty state (before interaction) | ✅ |
| Transport panel visible | ✅ |
| Edit sheet visible | ✅ |

## Visual issues
- **None.** Permission banner renders correctly above the surface panel with red border and mic rationale. Tapping grants permission via the system permission dialog.

## Controller verdict
**APPROVED** — Permission flow correctly implemented: banner surfaces when permission missing, `onRequestPermission` triggers system dialog, `onPermissionGranted()`/`onPermissionDenied()` callbacks update ViewModel state, `MicNeeded` state shown on pad tap when permission missing.
