package com.pocketlooplab.state

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pocketlooplab.audio.LoopAudioEngine
import com.pocketlooplab.model.LoopPadStatus
import com.pocketlooplab.model.LoopPadUiModel
import com.pocketlooplab.model.PlaybackSpeed
import com.pocketlooplab.model.WaveformBar
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for Pocket Loop Lab — orchestrates pad state transitions
 * via the [LoopAudioEngine] interface.
 *
 * State machine (per PRD):
 * - Empty + press → Listening
 * - Listening + hold 500ms → Recording
 * - Recording + release → stopRecording() → Playing
 * - Playing + tap → toggleMute() → Muted
 * - Muted + tap → toggleMute() → Playing
 * - Stopped + tap → startPlayback() → Playing
 * - Playing + long-press → selected + edit sheet
 * - Playing + overdub arm → OverdubArmed
 * - OverdubArmed + release → Layering → Playing
 * - Any pad + clear → Empty
 */
class PocketLoopLabViewModel(
    private val engine: LoopAudioEngine,
    private val context: Context,
) : ViewModel() {

    // ── Permission state ──────────────────────────────────────────────
    private val _hasRecordPermission = MutableStateFlow(checkPermission())
    val hasRecordPermission: StateFlow<Boolean> = _hasRecordPermission.asStateFlow()

    private fun checkPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    /** Call this from your Activity/Composable when permission is granted. */
    fun onPermissionGranted() {
        _hasRecordPermission.value = true
        // Transition any MicNeeded pads back to Empty
        _pads.value = _pads.value.map { pad ->
            if (pad.status == LoopPadStatus.MicNeeded) {
                pad.copy(status = LoopPadStatus.Empty, actionLabel = "Hold to record")
            } else pad
        }
    }

    /** Call this when permission is denied. */
    fun onPermissionDenied() {
        _hasRecordPermission.value = false
    }

    // ── UI State (what the UI observes) ──────────────────────────────
    private val _pads = MutableStateFlow(createInitialPads())
    val pads: StateFlow<List<LoopPadUiModel>> = _pads.asStateFlow()

    private val _selectedPadId = MutableStateFlow<Int?>(null)
    val selectedPadId: StateFlow<Int?> = _selectedPadId.asStateFlow()

    private val _editSheetVisible = MutableStateFlow(false)
    val editSheetVisible: StateFlow<Boolean> = _editSheetVisible.asStateFlow()

    // ── Listening → Recording timeout jobs ─────────────────────────
    private val listeningJobs = mutableMapOf<Int, Job>()

    // ── State transitions ─────────────────────────────────────────────

    /** Called when the pad surface is pressed. */
    fun onPadPress(padId: Int) {
        val currentStatus = getPadStatus(padId)

        // MicNeeded state: prompt for permission
        if (currentStatus == LoopPadStatus.MicNeeded) {
            // Permission not granted — actionLabel stays "Grant mic access"
            // The UI should surface a permission rationale
            return
        }

        // Guard: require RECORD_AUDIO permission before any recording action
        if (!_hasRecordPermission.value) {
            updatePad(padId) { it.copy(status = LoopPadStatus.MicNeeded, actionLabel = "Grant mic access") }
            return
        }

        when (currentStatus) {
            LoopPadStatus.Empty -> {
                // Empty → Listening immediately
                updatePad(padId) { it.copy(status = LoopPadStatus.Listening, actionLabel = "Release to start recording") }
                // 500ms hold → Recording
                listeningJobs[padId] = viewModelScope.launch {
                    delay(500L)
                    if (getPadStatus(padId) == LoopPadStatus.Listening) {
                        engine.startRecording(padId)
                        updatePad(padId) { it.copy(status = LoopPadStatus.Recording, actionLabel = "Release to stop") }
                    }
                }
            }
            LoopPadStatus.Listening -> {
                // Stay in Listening while held (already transitioned, no-op)
            }
            LoopPadStatus.Playing,
            LoopPadStatus.Muted,
            LoopPadStatus.Stopped -> {
                // Nothing on press — action on tap
            }
            else -> { /* ignore */ }
        }
    }

    /** Called when the pad surface is released. */
    fun onPadRelease(padId: Int) {
        listeningJobs[padId]?.cancel()
        listeningJobs.remove(padId)

        when (getPadStatus(padId)) {
            LoopPadStatus.Listening -> {
                // Tap without recording long enough → go back to Empty
                updatePad(padId) { it.copy(status = LoopPadStatus.Empty, actionLabel = "Hold to record", waveform = emptyList()) }
            }
            LoopPadStatus.Recording -> {
                // Release → stop recording → Playing
                engine.stopRecording(padId)
                val waveform = engine.getWaveform(padId)
                syncPadFromEngine(padId, waveform)
            }
            LoopPadStatus.OverdubArmed -> {
                // Release while overdub armed → Layering → Playing
                engine.stopOverdub(padId)
                updatePad(padId) { it.copy(status = LoopPadStatus.Layering, actionLabel = "Release to layer") }
                viewModelScope.launch {
                    delay(600L)
                    if (getPadStatus(padId) == LoopPadStatus.Layering) {
                        updatePad(padId) { it.copy(status = LoopPadStatus.Playing, actionLabel = "Tap to mute") }
                    }
                }
            }
            else -> { /* no-op */ }
        }
    }

    /** Called on tap (press + release without hold). */
    fun onPadTap(padId: Int) {
        when (getPadStatus(padId)) {
            LoopPadStatus.Playing -> {
                engine.toggleMute(padId)
                syncPadFromEngine(padId, engine.getWaveform(padId))
            }
            LoopPadStatus.Muted -> {
                engine.toggleMute(padId)
                syncPadFromEngine(padId, engine.getWaveform(padId))
            }
            LoopPadStatus.Stopped -> {
                engine.startPlayback(padId)
                syncPadFromEngine(padId, engine.getWaveform(padId))
            }
            LoopPadStatus.OverdubArmed -> {
                // Tap while overdub armed → stop overdub (same as release)
                engine.stopOverdub(padId)
                updatePad(padId) { it.copy(status = LoopPadStatus.Layering, actionLabel = "Release to layer") }
                viewModelScope.launch {
                    delay(600L)
                    if (getPadStatus(padId) == LoopPadStatus.Layering) {
                        updatePad(padId) { it.copy(status = LoopPadStatus.Playing, actionLabel = "Tap to mute") }
                    }
                }
            }
            else -> { /* ignore */ }
        }
    }

    /** Called on long-press. Selects pad and opens edit sheet. */
    fun onPadLongPress(padId: Int) {
        _selectedPadId.value = padId
        _editSheetVisible.value = true
    }

    /** Called when edit sheet is dismissed. */
    fun onEditSheetDismiss() {
        _editSheetVisible.value = false
        _selectedPadId.value = null
    }

    /** Called when volume changes. */
    fun onVolumeChange(padId: Int, volumeDb: Float) {
        engine.setVolume(padId, volumeDb)
    }

    /** Called when speed changes. */
    fun onSpeedChange(padId: Int, speed: PlaybackSpeed) {
        engine.setSpeed(padId, speed)
    }

    /** Called when clear is confirmed. */
    fun onClearPad(padId: Int) {
        engine.clear(padId)
        updatePad(padId) { it.copy(status = LoopPadStatus.Empty, actionLabel = "Hold to record", waveform = emptyList(), clipping = false) }
    }

    /** Called when overdub arm is activated. */
    fun onOverdubArm(padId: Int) {
        engine.startOverdub(padId)
        syncPadFromEngine(padId, engine.getWaveform(padId))
    }

    // ── Internal helpers ─────────────────────────────────────────────

    private fun createInitialPads(): List<LoopPadUiModel> {
        return (1..4).map { id ->
            LoopPadUiModel(
                id = id,
                title = "Pad $id",
                status = LoopPadStatus.Empty,
                actionLabel = "Hold to record",
                selected = false,
                waveform = emptyList(),
                clipping = false
            )
        }
    }

    private fun getPadStatus(padId: Int): LoopPadStatus {
        return _pads.value.find { it.id == padId }?.status ?: LoopPadStatus.Empty
    }

    private fun updatePad(padId: Int, transform: (LoopPadUiModel) -> LoopPadUiModel) {
        _pads.value = _pads.value.map { pad ->
            if (pad.id == padId) {
                val updated = transform(pad)
                updated.copy(selected = _selectedPadId.value == padId)
            } else {
                pad.copy(selected = false)
            }
        }
    }

    private fun syncPadFromEngine(padId: Int, waveform: List<WaveformBar>) {
        updatePad(padId) { pad ->
            val status = engine.getStatus(padId)
            val clipping = waveform.any { it.colorRole == com.pocketlooplab.model.WaveformColorRole.Red }
            pad.copy(
                status = status,
                actionLabel = actionLabelFor(status),
                waveform = waveform,
                clipping = clipping
            )
        }
    }

    private fun actionLabelFor(status: LoopPadStatus): String = when (status) {
        LoopPadStatus.Empty -> "Hold to record"
        LoopPadStatus.Listening -> "Release to start recording"
        LoopPadStatus.Recording -> "Release to stop"
        LoopPadStatus.Playing -> "Tap to mute"
        LoopPadStatus.Muted -> "Tap to unmute"
        LoopPadStatus.Stopped -> "Tap to play"
        LoopPadStatus.OverdubArmed -> "Release to add layer"
        LoopPadStatus.Layering -> "Release to layer"
        LoopPadStatus.MicNeeded -> "Grant mic access"
    }
}
