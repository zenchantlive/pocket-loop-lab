package com.pocketlooplab.audio

import com.pocketlooplab.model.LoopPadStatus
import com.pocketlooplab.model.PlaybackSpeed
import com.pocketlooplab.model.WaveformBar

/**
 * Handle to a recorded loop, returned when recording stops.
 */
data class LoopHandle(
    val padId: Int,
    val durationMs: Long,
    val filePath: String? = null,
)

/**
 * Audio engine interface — implemented by [FakeLoopAudioEngine] for prototype
 * and [AndroidLoopAudioEngine] for real audio on device.
 */
interface LoopAudioEngine {

    /** Begin recording on the given pad. Transitions pad → Recording. */
    fun startRecording(padId: Int)

    /** Stop recording on the given pad. Transitions Recording → Playing. Returns a handle. */
    fun stopRecording(padId: Int): LoopHandle?

    /** Begin playback on the given pad. Transitions Stopped/Muted → Playing. */
    fun startPlayback(padId: Int)

    /** Stop playback on the given pad. Transitions Playing → Stopped. */
    fun stopPlayback(padId: Int)

    /**
     * Toggle mute on the given pad.
     * Returns the new mute state (true = muted, false = unmuted).
     * Transitions Playing ↔ Muted.
     */
    fun toggleMute(padId: Int): Boolean

    /** Clear the loop on the given pad. Transitions any → Empty. */
    fun clear(padId: Int)

    /** Set volume in decibels (0 dB = normal, negative = quieter). */
    fun setVolume(padId: Int, volumeDb: Float)

    /** Set playback speed. */
    fun setSpeed(padId: Int, speed: PlaybackSpeed)

    /** Toggle reversed playback direction. Prototype — no-op on Slice 5. */
    fun setReversed(padId: Int, reversed: Boolean)

    /**
     * Set trim start/end in milliseconds.
     * Prototype on Slice 5 — stored in engine state but actual audio trim deferred.
     */
    fun setTrim(padId: Int, startMs: Long, endMs: Long)

    /**
     * Arm the given pad for overdub recording.
     * Transitions Playing → OverdubArmed.
     */
    fun startOverdub(padId: Int)

    /**
     * Stop the overdub arm on the given pad.
     * Transitions OverdubArmed → Layering → Playing.
     */
    fun stopOverdub(padId: Int)

    /** Returns the current waveform bars for the pad. Empty list if no loop recorded. */
    fun getWaveform(padId: Int): List<WaveformBar>

    /** Returns the current audio level for the pad meter (0.0..1.0). */
    fun getLevel(padId: Int): Float

    /** Returns the current status for the pad. */
    fun getStatus(padId: Int): LoopPadStatus
}
