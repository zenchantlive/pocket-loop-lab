package com.pocketlooplab.audio

import com.pocketlooplab.model.LoopPadStatus
import com.pocketlooplab.model.PlaybackSpeed
import com.pocketlooplab.model.WaveformBar
import com.pocketlooplab.model.WaveformColorRole
import kotlin.random.Random

/**
 * In-memory fake audio engine for prototype development.
 * All state is kept in memory; no actual audio is captured.
 */
class FakeLoopAudioEngine : LoopAudioEngine {

    private data class FakePad(
        var status: LoopPadStatus = LoopPadStatus.Empty,
        val waveformBars: MutableList<WaveformBar> = mutableListOf(),
        var volumeDb: Float = 0f,
        var speed: PlaybackSpeed = PlaybackSpeed.Normal,
        var isMuted: Boolean = false,
        var isClipping: Boolean = false,
        var loopHandle: LoopHandle? = null,
    )

    private val pads = mutableMapOf(
        1 to FakePad(),
        2 to FakePad(),
        3 to FakePad(),
        4 to FakePad(),
    )

    override fun startRecording(padId: Int) {
        val pad = pads[padId] ?: return
        pad.status = LoopPadStatus.Recording
        pad.waveformBars.clear()
        pad.isClipping = false
        println("[FakeAudio] pad $padId: startRecording → Recording")
    }

    override fun stopRecording(padId: Int): LoopHandle? {
        val pad = pads[padId] ?: return null
        pad.status = LoopPadStatus.Playing
        // Generate fake waveform
        pad.waveformBars.clear()
        pad.waveformBars.addAll(generateFakeWaveform(WaveformColorRole.Mint))
        val handle = LoopHandle(padId = padId, durationMs = 30_000L)
        pad.loopHandle = handle
        println("[FakeAudio] pad $padId: stopRecording → Playing (handle=$handle)")
        return handle
    }

    override fun startPlayback(padId: Int) {
        val pad = pads[padId] ?: return
        pad.status = LoopPadStatus.Playing
        pad.isMuted = false
        println("[FakeAudio] pad $padId: startPlayback → Playing")
    }

    override fun stopPlayback(padId: Int) {
        val pad = pads[padId] ?: return
        pad.status = LoopPadStatus.Stopped
        println("[FakeAudio] pad $padId: stopPlayback → Stopped")
    }

    override fun toggleMute(padId: Int): Boolean {
        val pad = pads[padId] ?: return false
        pad.isMuted = !pad.isMuted
        pad.status = if (pad.isMuted) LoopPadStatus.Muted else LoopPadStatus.Playing
        println("[FakeAudio] pad $padId: toggleMute isMuted=${pad.isMuted} → ${pad.status}")
        return pad.isMuted
    }

    override fun clear(padId: Int) {
        val pad = pads[padId] ?: return
        pad.status = LoopPadStatus.Empty
        pad.waveformBars.clear()
        pad.loopHandle = null
        pad.isMuted = false
        pad.isClipping = false
        println("[FakeAudio] pad $padId: clear → Empty")
    }

    override fun setVolume(padId: Int, volumeDb: Float) {
        pads[padId]?.volumeDb = volumeDb
        println("[FakeAudio] pad $padId: setVolume ${volumeDb}dB")
    }

    override fun setSpeed(padId: Int, speed: PlaybackSpeed) {
        pads[padId]?.speed = speed
        println("[FakeAudio] pad $padId: setSpeed $speed")
    }

    override fun setReversed(padId: Int, reversed: Boolean) {
        // Prototype: no-op
    }

    override fun setTrim(padId: Int, startMs: Long, endMs: Long) {
        // Prototype: stored but not applied
    }

    override fun startOverdub(padId: Int) {
        val pad = pads[padId] ?: return
        pad.status = LoopPadStatus.OverdubArmed
        println("[FakeAudio] pad $padId: startOverdub → OverdubArmed")
    }

    override fun stopOverdub(padId: Int) {
        val pad = pads[padId] ?: return
        pad.status = LoopPadStatus.Layering
        println("[FakeAudio] pad $padId: stopOverdub → Layering")
        // Layering → Playing after "layer" (fake delay in ViewModel)
    }

    override fun getWaveform(padId: Int): List<WaveformBar> {
        return pads[padId]?.waveformBars ?: emptyList()
    }

    override fun getLevel(padId: Int): Float {
        val pad = pads[padId] ?: return 0f
        return when (pad.status) {
            LoopPadStatus.Recording -> Random.nextFloat() * 0.5f + 0.4f
            LoopPadStatus.Listening -> Random.nextFloat() * 0.3f + 0.1f
            LoopPadStatus.Playing -> if (pad.isMuted) 0f else Random.nextFloat() * 0.3f + 0.5f
            else -> 0f
        }
    }

    override fun getStatus(padId: Int): LoopPadStatus {
        return pads[padId]?.status ?: LoopPadStatus.Empty
    }

    private fun generateFakeWaveform(role: WaveformColorRole): List<WaveformBar> {
        val seed = listOf(0.3f, 0.65f, 0.45f, 0.8f, 0.55f, 0.9f, 0.4f, 0.7f, 0.6f, 0.75f)
        return seed.map { WaveformBar(it, role) }
    }
}
