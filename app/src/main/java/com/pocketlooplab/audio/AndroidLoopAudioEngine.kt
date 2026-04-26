package com.pocketlooplab.audio

import android.content.Context
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.media.MediaRecorder.AudioSource
import android.os.Build
import com.pocketlooplab.model.LoopPadStatus
import com.pocketlooplab.model.PlaybackSpeed
import com.pocketlooplab.model.WaveformBar
import com.pocketlooplab.model.WaveformColorRole
import java.lang.Math
import java.io.File

/**
 * Real Android audio engine using [MediaRecorder] + [MediaPlayer].
 *
 * - [startRecording] uses MediaRecorder to capture to a local .m4a file per pad.
 * - [stopRecording] finalizes the file, creates a LoopHandle, switches to playback mode.
 * - [startPlayback] uses MediaPlayer with looping enabled.
 * - [toggleMute] uses MediaPlayer.setVolume().
 * - [getLevel] uses MediaRecorder.getMaxAmplitude() during recording,
 *   or last-known amplitude during playback.
 *
 * Waveform bars are populated post-recording by decoding the saved file —
 * deferred to Slice 5. During recording, placeholder amber bars are shown.
 */
class AndroidLoopAudioEngine(private val context: Context) : LoopAudioEngine {

    private data class PadState(
        var status: LoopPadStatus = LoopPadStatus.Empty,
        var recorder: MediaRecorder? = null,
        var player: MediaPlayer? = null,
        var currentFile: File? = null,
        var isMuted: Boolean = false,
        var currentVolumeDb: Float = 0f,
        var currentSpeed: PlaybackSpeed = PlaybackSpeed.Normal,
        var isReversed: Boolean = false,
        var trimStartMs: Long = 0L,
        var trimEndMs: Long = 0L,
        var lastAmplitude: Int = 0,
        var waveformBars: List<WaveformBar> = emptyList(),
    )

    private val pads = mutableMapOf(
        1 to PadState(),
        2 to PadState(),
        3 to PadState(),
        4 to PadState(),
    )

    private fun padDir(): File {
        val dir = File(context.filesDir, "loops")
        if (!dir.exists()) dir.mkdirs()
        return dir
    }

    private fun padFile(padId: Int) = File(padDir(), "pad_${padId}.m4a")

    // ── LoopAudioEngine implementation ───────────────────────────────

    override fun startRecording(padId: Int) {
        val pad = pads[padId] ?: return
        val file = padFile(padId)
        // Delete any existing file
        if (file.exists()) file.delete()

        val recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else {
            @Suppress("DEPRECATION")
            MediaRecorder()
        }
        recorder.setAudioSource(AudioSource.MIC)
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        recorder.setAudioEncodingBitRate(128_000)
        recorder.setAudioSamplingRate(44_100)
        recorder.setOutputFile(file.absolutePath)

        try {
            recorder.prepare()
            recorder.start()
            pad.recorder = recorder
            pad.currentFile = file
            pad.status = LoopPadStatus.Recording
            pad.waveformBars = emptyList() // placeholder until post-recording decode
            println("[AndroidAudio] pad $padId: startRecording → Recording")
        } catch (e: Exception) {
            println("[AndroidAudio] pad $padId: startRecording failed: $e")
            recorder.release()
        }
    }

    override fun stopRecording(padId: Int): LoopHandle? {
        val pad = pads[padId] ?: return null
        val recorder = pad.recorder ?: return null

        try {
            recorder.stop()
        } catch (e: Exception) {
            println("[AndroidAudio] pad $padId: stopRecording failed: $e")
        }
        try {
            recorder.release()
        } catch (e: Exception) {
            /* ignore */
        }
        pad.recorder = null

        val file = pad.currentFile
        pad.status = LoopPadStatus.Playing

        val durationMs = if (file != null && file.exists()) {
            // Approximate: file size / bitrate * 8 (very rough estimate)
            // More accurate duration requires MediaMetadataRetriever
            30_000L // placeholder until MediaMetadataRetriever is called
        } else 0L

        // Generate placeholder waveform (amber bars) — real waveform decode in Slice 5
        pad.waveformBars = generatePlaceholderWaveform(WaveformColorRole.Amber)

        println("[AndroidAudio] pad $padId: stopRecording → Playing (file=${file?.name})")
        return LoopHandle(padId = padId, durationMs = durationMs, filePath = file?.absolutePath)
    }

    override fun startPlayback(padId: Int) {
        val pad = pads[padId] ?: return
        val file = pad.currentFile ?: return

        val player = MediaPlayer()
        try {
            player.setDataSource(file.absolutePath)
            player.isLooping = true
            player.prepare()
            player.setVolume(
                if (pad.isMuted) 0f else 1f,
                if (pad.isMuted) 0f else 1f
            )
            player.start()
            pad.player = player
            pad.status = LoopPadStatus.Playing
            println("[AndroidAudio] pad $padId: startPlayback → Playing")
        } catch (e: Exception) {
            println("[AndroidAudio] pad $padId: startPlayback failed: $e")
            player.release()
        }
    }

    override fun stopPlayback(padId: Int) {
        val pad = pads[padId] ?: return
        pad.player?.let {
            try {
                it.stop()
                it.release()
            } catch (e: Exception) { /* ignore */ }
        }
        pad.player = null
        pad.status = LoopPadStatus.Stopped
        println("[AndroidAudio] pad $padId: stopPlayback → Stopped")
    }

    override fun toggleMute(padId: Int): Boolean {
        val pad = pads[padId] ?: return false
        pad.isMuted = !pad.isMuted
        pad.player?.setVolume(
            if (pad.isMuted) 0f else 1f,
            if (pad.isMuted) 0f else 1f
        )
        pad.status = if (pad.isMuted) LoopPadStatus.Muted else LoopPadStatus.Playing
        println("[AndroidAudio] pad $padId: toggleMute isMuted=${pad.isMuted} → ${pad.status}")
        return pad.isMuted
    }

    override fun clear(padId: Int) {
        val pad = pads[padId] ?: return
        pad.recorder?.let {
            try { it.stop(); it.release() } catch (e: Exception) { /* ignore */ }
        }
        pad.recorder = null
        pad.player?.let {
            try { it.stop(); it.release() } catch (e: Exception) { /* ignore */ }
        }
        pad.player = null
        pad.currentFile?.delete()
        pad.currentFile = null
        pad.isMuted = false
        pad.waveformBars = emptyList()
        pad.status = LoopPadStatus.Empty
        println("[AndroidAudio] pad $padId: clear → Empty")
    }

    override fun setVolume(padId: Int, volumeDb: Float) {
        pads[padId]?.let { pad ->
            pad.currentVolumeDb = volumeDb
            // MediaPlayer volume: 0.0..1.0 (dB to linear: 10^(dB/20))
            val linear = if (volumeDb <= -80f) 0f else Math.pow(10.0, volumeDb.toDouble() / 20.0).toFloat()
            pad.player?.setVolume(linear, linear)
            println("[AndroidAudio] pad $padId: setVolume ${volumeDb}dB → linear=$linear")
        }
    }

    override fun setSpeed(padId: Int, speed: PlaybackSpeed) {
        pads[padId]?.let { pad ->
            pad.currentSpeed = speed
            // MediaPlayer does not natively support pitch-preserved speed change.
            // Set playback speed via PlaybackParams (no pitch preservation).
            // For true speed control with pitch preservation, AudioTrack path needed (Slice 5+).
            try {
                pad.player?.let { player ->
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        val params = player.playbackParams
                        params.speed = when (speed) {
                            PlaybackSpeed.Half -> 0.5f
                            PlaybackSpeed.Normal -> 1.0f
                            PlaybackSpeed.Double -> 2.0f
                        }
                        player.playbackParams = params
                    }
                }
            } catch (e: Exception) {
                println("[AndroidAudio] pad $padId: setSpeed failed (no pitch preservation): $e")
            }
            println("[AndroidAudio] pad $padId: setSpeed $speed")
        }
    }

    override fun setReversed(padId: Int, reversed: Boolean) {
        // Prototype: no-op on Slice 5.
        // True reverse requires re-encoding or AudioTrack with reversed buffer.
        pads[padId]?.let { pad ->
            pad.isReversed = reversed
            println("[AndroidAudio] pad $padId: setReversed $reversed (prototype no-op)")
        }
    }

    override fun setTrim(padId: Int, startMs: Long, endMs: Long) {
        // Prototype: stored but not applied on Slice 5.
        // True trim requires re-encoding the audio file.
        pads[padId]?.let { pad ->
            pad.trimStartMs = startMs
            pad.trimEndMs = endMs
            println("[AndroidAudio] pad $padId: setTrim ${startMs}ms–${endMs}ms (prototype no-op)")
        }
    }

    override fun startOverdub(padId: Int) {
        val pad = pads[padId] ?: return
        pad.status = LoopPadStatus.OverdubArmed
        println("[AndroidAudio] pad $padId: startOverdub → OverdubArmed")
    }

    override fun stopOverdub(padId: Int) {
        val pad = pads[padId] ?: return
        pad.status = LoopPadStatus.Layering
        println("[AndroidAudio] pad $padId: stopOverdub → Layering")
    }

    override fun getWaveform(padId: Int): List<WaveformBar> {
        return pads[padId]?.waveformBars ?: emptyList()
    }

    override fun getLevel(padId: Int): Float {
        val pad = pads[padId] ?: return 0f
        return when (pad.status) {
            LoopPadStatus.Recording -> {
                pad.recorder?.let { rec ->
                    try {
                        // getMaxAmplitude returns 0..32767
                        val amp = rec.maxAmplitude
                        pad.lastAmplitude = amp
                        amp / 32767f
                    } catch (e: Exception) {
                        0f
                    }
                } ?: 0f
            }
            LoopPadStatus.Playing -> {
                if (pad.isMuted) 0f
                else (pad.lastAmplitude / 32767f).coerceIn(0f, 1f)
            }
            LoopPadStatus.Muted -> 0f
            else -> 0f
        }
    }

    override fun getStatus(padId: Int): LoopPadStatus {
        return pads[padId]?.status ?: LoopPadStatus.Empty
    }

    // ── Helpers ───────────────────────────────────────────────────────

    private fun generatePlaceholderWaveform(role: WaveformColorRole): List<WaveformBar> {
        // 10-bar placeholder representing recorded audio
        return listOf(0.3f, 0.65f, 0.45f, 0.8f, 0.55f, 0.9f, 0.4f, 0.7f, 0.6f, 0.75f)
            .map { WaveformBar(it, role) }
    }

    override fun toString() = "AndroidLoopAudioEngine"
}
