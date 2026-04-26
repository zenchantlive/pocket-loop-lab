# Audio Engine Decision

**Date:** 2026-04-25
**Status:** DECIDED

## Options evaluated

### Option A: MediaRecorder + MediaPlayer
**Approach:** `MediaRecorder` records to a local `.m4a` file per pad. `MediaPlayer` with `setLooping(true)` plays it back.

**Pros:**
- Simple, well-tested Android API
- MediaRecorder handles encoding (.m4a/AAC), file management, max amplitude for level metering
- MediaPlayer with looping is truly seamless
- MediaPlayer.setVolume() works for mute/unmute
- No manual PCM encoding needed

**Cons:**
- MediaPlayer does not support per-pad speed change (no `PlaybackParams` for pitch-preserved speed)
- Overdub = record new file, discard old (no true layering with separate audio streams)
- Waveform must be generated post-recording from the saved file (requires decoding)

**Verdict for Slice 4:** ✅ **SELECTED** — fastest path to first working APK.满足 first APK goals (record, loop, mute).

---

### Option B: AudioRecord + AudioTrack
**Approach:** Raw PCM capture via `AudioRecord`, manual `.wav` encoding, streaming playback via `AudioTrack` in streaming mode.

**Pros:**
- Full control over PCM data — waveform bars generated in real-time during recording
- Speed change via sample rate interpolation
- Precise overdub timing by mixing PCM buffers
- Zero-latency level meter from AudioRecord read buffers

**Cons:**
- Manual WAV encoding overhead
- AudioTrack streaming requires careful buffer management
- Waveform from raw PCM still needs downsampling/quantization
- Higher implementation complexity

**Verdict:** Deferred to Slice 5+. Will be needed for: real-time waveform display, speed control, precise overdub, multi-track mixing.

---

## First APK scope with Option A

| Feature | MediaRecorder + MediaPlayer |
|---|---|
| Record to local file | ✅ |
| Seamless loop playback | ✅ |
| Mute/unmute | ✅ (via setVolume 0/1) |
| Volume adjustment | ✅ (via setVolume) |
| Speed control | ❌ |
| Real-time waveform during recording | ⚠️ (via getMaxAmplitude, quantized to level bar) |
| True overdub layering | ❌ (new file replaces old) |
| Per-pad independent playback | ✅ |
| 4 simultaneous pads | ✅ |

## Waveform strategy

During recording: use `MediaRecorder.getMaxAmplitude()` → `Float` (0.0..1.0) → update `level` field.
After recording: optionally generate waveform bars from the saved `.m4a` file using `MediaExtractor` + `MediaCodec` to decode PCM (deferred to Slice 5).

For screenshot verification: waveform shown as `List<WaveformBar>` generated from the recorded file or a placeholder pattern.
