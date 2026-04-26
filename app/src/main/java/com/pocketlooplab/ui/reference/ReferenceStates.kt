package com.pocketlooplab.ui.reference

import com.pocketlooplab.model.LoopPadStatus
import com.pocketlooplab.model.LoopPadUiModel
import com.pocketlooplab.model.PadEditUiModel
import com.pocketlooplab.model.PocketLoopLabUiState
import com.pocketlooplab.model.TransportUiModel
import com.pocketlooplab.model.WaveformBar
import com.pocketlooplab.model.WaveformColorRole

object ReferenceStates {

    // ============================================================
    // Task s2-3: Factory functions for each pad state
    // ============================================================

    /**
     * Factory for an Empty pad with dim waveform.
     */
    fun emptyPad(id: Int): LoopPadUiModel = LoopPadUiModel(
        id = id,
        title = "Pad $id",
        status = LoopPadStatus.Empty,
        actionLabel = "Hold to record",
        waveform = dimWaveform()
    )

    /**
     * Factory for a Listening pad (mic waking up while user holds).
     */
    fun listeningPad(id: Int): LoopPadUiModel = LoopPadUiModel(
        id = id,
        title = "Pad $id",
        status = LoopPadStatus.Listening,
        actionLabel = "Listening...",
        waveform = dimWaveform()
    )

    /**
     * Factory for a Recording pad.
     */
    fun recordingPad(id: Int): LoopPadUiModel = LoopPadUiModel(
        id = id,
        title = "Pad $id",
        status = LoopPadStatus.Recording,
        actionLabel = "Release to loop",
        waveform = dimWaveform()
    )

    /**
     * Factory for a Playing pad with colored waveform.
     * @param selected Whether the pad is selected (shows mint border)
     */
    fun playingPad(id: Int, selected: Boolean = false): LoopPadUiModel = LoopPadUiModel(
        id = id,
        title = "Pad $id",
        status = LoopPadStatus.Playing,
        actionLabel = "Tap to mute",
        selected = selected,
        progress = 0.5f,
        waveform = mintWaveform()
    )

    /**
     * Factory for a Muted pad with colored waveform.
     * @param selected Whether the pad is selected (shows mint border)
     */
    fun mutedPad(id: Int, selected: Boolean = false): LoopPadUiModel = LoopPadUiModel(
        id = id,
        title = "Pad $id",
        status = LoopPadStatus.Muted,
        actionLabel = "Tap to play",
        selected = selected,
        waveform = mintWaveform()
    )

    /**
     * Factory for a Stopped pad.
     */
    fun stoppedPad(id: Int): LoopPadUiModel = LoopPadUiModel(
        id = id,
        title = "Pad $id",
        status = LoopPadStatus.Stopped,
        actionLabel = "Tap to play",
        waveform = mintWaveform()
    )

    /**
     * Factory for an OverdubArmed pad (guarded for layer capture).
     */
    fun overdubArmedPad(id: Int): LoopPadUiModel = LoopPadUiModel(
        id = id,
        title = "Pad $id",
        status = LoopPadStatus.OverdubArmed,
        actionLabel = "Hold to layer",
        waveform = mintWaveform()
    )

    /**
     * Factory for a Layering pad (audio being captured over existing loop).
     * Note: actionLabel is "Release to layer" per PRD - NOT "Stop" (which is an action, not a state).
     */
    fun layeringPad(id: Int): LoopPadUiModel = LoopPadUiModel(
        id = id,
        title = "Pad $id",
        status = LoopPadStatus.Layering,
        actionLabel = "Release to layer",
        progress = 0.48f,
        waveform = amberWaveform(),
        cueRoles = listOf(WaveformColorRole.Red, WaveformColorRole.Amber)
    )

    /**
     * Factory for a MicNeeded pad (recording attempted without permission).
     */
    fun micNeededPad(id: Int): LoopPadUiModel = LoopPadUiModel(
        id = id,
        title = "Pad $id",
        status = LoopPadStatus.MicNeeded,
        actionLabel = "Mic permission needed",
        waveform = dimWaveform()
    )

    // ============================================================
    // Task s2-3: Helper reference states for screenshot capture
    // ============================================================

    /**
     * First launch empty state - all 4 pads are Empty.
     */
    fun firstLaunchEmptyState(): PocketLoopLabUiState = PocketLoopLabUiState(
        appTitle = "Pocket Loop Lab",
        surfaceTitle = "Live Lab Surface",
        elapsedLabel = "0m 0s",
        fidelityLabel = "LoFi",
        pads = listOf(
            emptyPad(1),
            emptyPad(2),
            emptyPad(3),
            emptyPad(4)
        ),
        transport = TransportUiModel(
            bpmLabel = "Free tempo",
            syncLabel = "Local only",
            masterLabel = "Transport",
            armedLabel = "Input armed"
        ),
        editSheet = PadEditUiModel(
            title = "Pad 1 Edit Sheet",
            trimLabel = "Trim",
            volumeLabel = "Volume 0 dB",
            speedLabel = "Speed 1x",
            waveform = mintWaveform() + mintWaveform().take(4),
            reverseLabel = "Reverse",
            overdubLabel = "Overdub",
            clearLabel = "Clear"
        )
    )

    /**
     * Mixed state for screenshot capture:
     * - Pad 1: Playing + selected (mint border)
     * - Pad 2: Layering with red/yellow cue pills
     * - Pad 3: Empty
     * - Pad 4: Empty
     */
    fun mixedState(): PocketLoopLabUiState = PocketLoopLabUiState(
        appTitle = "Pocket Loop Lab",
        surfaceTitle = "Live Lab Surface",
        elapsedLabel = "12h 0m 28s",
        fidelityLabel = "LoFi",
        pads = listOf(
            playingPad(1, selected = true),
            layeringPad(2),
            emptyPad(3),
            emptyPad(4)
        ),
        transport = TransportUiModel(
            bpmLabel = "Free tempo",
            syncLabel = "Local only",
            masterLabel = "Transport",
            armedLabel = "Input armed"
        ),
        editSheet = PadEditUiModel(
            title = "Pad 1 Edit Sheet",
            trimLabel = "Trim",
            volumeLabel = "Volume 0 dB",
            speedLabel = "Speed 1x",
            waveform = mintWaveform() + mintWaveform().take(4),
            reverseLabel = "Reverse",
            overdubLabel = "Overdub",
            clearLabel = "Clear"
        )
    )

    // ============================================================
    // Legacy mockup (kept for backward compatibility)
    // ============================================================

    fun lockedMockup(): PocketLoopLabUiState = PocketLoopLabUiState(
        appTitle = "Pocket Loop Lab",
        surfaceTitle = "Live Lab Surface",
        elapsedLabel = "12h 0m 28s",
        fidelityLabel = "LoFi",
        pads = listOf(
            LoopPadUiModel(
                id = 1,
                title = "Pad 1",
                status = LoopPadStatus.Playing,
                actionLabel = "Selected loop",
                selected = true,
                progress = 0.72f,
                waveform = mintWaveform()
            ),
            LoopPadUiModel(
                id = 2,
                title = "Pad 2",
                status = LoopPadStatus.Layering,
                actionLabel = "Release to layer",  // Fixed: was "Stop" which is an action not state
                progress = 0.48f,
                waveform = amberWaveform(),
                cueRoles = listOf(WaveformColorRole.Red, WaveformColorRole.Amber)
            ),
            LoopPadUiModel(
                id = 3,
                title = "Pad 3",
                status = LoopPadStatus.Empty,
                actionLabel = "Hold to record",
                waveform = dimWaveform()
            ),
            LoopPadUiModel(
                id = 4,
                title = "Pad 4",
                status = LoopPadStatus.Empty,
                actionLabel = "Hold to record",
                waveform = dimWaveform().reversed()
            )
        ),
        transport = TransportUiModel(
            bpmLabel = "Free tempo",
            syncLabel = "Local only",
            masterLabel = "Transport",
            armedLabel = "Input armed"
        ),
        editSheet = PadEditUiModel(
            title = "Pad 1 Edit Sheet",
            trimLabel = "Trim",
            volumeLabel = "Volume 0 dB",
            speedLabel = "Speed 1x",
            waveform = mintWaveform() + mintWaveform().take(4),
            reverseLabel = "Reverse",
            overdubLabel = "Overdub",
            clearLabel = "Clear"
        )
    )

    // ============================================================
    // Private waveform generators
    // ============================================================

    private fun mintWaveform(): List<WaveformBar> = listOf(
        .28f, .52f, .76f, .42f, .88f, .58f, .66f, .34f, .72f, .49f, .81f, .38f
    ).map { WaveformBar(it, WaveformColorRole.Mint) }

    private fun dimWaveform(): List<WaveformBar> = listOf(
        .24f, .30f, .20f, .34f, .22f, .28f
    ).map { WaveformBar(it, WaveformColorRole.Dim) }

    private fun amberWaveform(): List<WaveformBar> = listOf(
        .34f, .62f, .42f, .82f, .55f, .72f, .46f, .64f
    ).map { WaveformBar(it, WaveformColorRole.Amber) }
}
