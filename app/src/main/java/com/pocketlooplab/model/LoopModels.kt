package com.pocketlooplab.model

import androidx.compose.ui.graphics.Color

enum class LoopPadStatus(val label: String) {
    Empty("Empty"),
    Listening("Listening"),
    Recording("Recording"),
    Playing("Playing"),
    Muted("Muted"),
    Stopped("Stopped"),
    OverdubArmed("Overdub armed"),
    Layering("Layering"),
    MicNeeded("Mic needed")
    // Note: TooLoud is a flag overlay, not a base state per PRD
}

data class WaveformBar(
    val height: Float,
    val colorRole: WaveformColorRole = WaveformColorRole.Mint
)

enum class WaveformColorRole {
    Mint,
    Amber,
    Red,
    Dim
}

enum class PlaybackSpeed(val label: String) {
    Half("0.5x"),
    Normal("1x"),
    Double("2x")
}

data class LoopPadUiModel(
    val id: Int,
    val title: String,
    val status: LoopPadStatus,
    val actionLabel: String,
    val selected: Boolean = false,
    val progress: Float = 0f,
    val waveform: List<WaveformBar> = emptyList(),
    val cueRoles: List<WaveformColorRole> = emptyList(),
    val clipping: Boolean = false  // TooLoud flag overlay
)

data class TransportUiModel(
    val bpmLabel: String,
    val syncLabel: String,
    val masterLabel: String,
    val armedLabel: String
)

data class PadEditUiModel(
    val title: String,
    val trimLabel: String,
    val volumeLabel: String,
    val speedLabel: String,
    val waveform: List<WaveformBar>,
    val reverseLabel: String,
    val overdubLabel: String,
    val clearLabel: String
)

data class PocketLoopLabUiState(
    val appTitle: String,
    val surfaceTitle: String,
    val elapsedLabel: String,
    val fidelityLabel: String,
    val pads: List<LoopPadUiModel>,
    val transport: TransportUiModel,
    val editSheet: PadEditUiModel
)

fun WaveformColorRole.toColor(): Color = when (this) {
    WaveformColorRole.Mint -> Color(0xFF5BE6C7)
    WaveformColorRole.Amber -> Color(0xFFFFD166)
    WaveformColorRole.Red -> Color(0xFFFF5A66)
    WaveformColorRole.Dim -> Color(0xFF44505F)
}
