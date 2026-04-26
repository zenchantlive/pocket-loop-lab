package com.pocketlooplab.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pocketlooplab.model.LoopPadStatus
import com.pocketlooplab.model.LoopPadUiModel
import com.pocketlooplab.model.WaveformBar
import com.pocketlooplab.model.WaveformColorRole
import com.pocketlooplab.model.toColor

// Color constants per PRD
private val ColorMint = Color(0xFF5BE6C7)
private val ColorAmber = Color(0xFFFFD166)
private val ColorRed = Color(0xFFFF5A66)
private val ColorDim = Color(0xFF44505F)
private val ColorDarkBorder = Color(0xFF293241)
private val ColorPanelLight = Color(0xFF202A35)
private val ColorPanelDark = Color(0xFF121923)
private val ColorTextSecondary = Color(0xFFB8C4CC)
private val ColorTextPrimary = Color(0xFFEAF2F5)
private val ColorWaveformBg = Color(0xFF0B1118)

@Composable
fun LoopPadCard(
    pad: LoopPadUiModel,
    modifier: Modifier = Modifier,
    onPress: () -> Unit = {},
    onRelease: () -> Unit = {},
    onTap: () -> Unit = {},
    onLongPress: () -> Unit = {}
) {
    val borderColor = getBorderColor(pad)
    val panelBrush = Brush.verticalGradient(
        listOf(ColorPanelLight, ColorPanelDark)
    )

    Column(
        modifier = modifier
            .semantics { contentDescription = "${pad.title} ${pad.status.label} ${pad.actionLabel}" }
            .clip(RoundedCornerShape(28.dp))
            .background(panelBrush)
            .border(2.dp, borderColor, RoundedCornerShape(28.dp))
            .padding(16.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = { _ ->
                        onPress()
                    },
                    onTap = {
                        onTap()
                        onRelease()
                    },
                    onLongPress = { _ ->
                        onLongPress()
                        onRelease()
                    }
                )
            }
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Text(pad.title, color = ColorTextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.weight(1f))
            if (pad.selected) {
                Text("Selected", color = Color(0xFF06231E), fontSize = 11.sp, fontWeight = FontWeight.Bold,
                    modifier = Modifier.clip(RoundedCornerShape(50)).background(ColorMint).padding(horizontal = 8.dp, vertical = 4.dp))
            }
        }

        Spacer(Modifier.height(12.dp))
        WaveformStrip(pad.waveform, modifier = Modifier.fillMaxWidth().height(48.dp))
        Spacer(Modifier.height(12.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            StatusOrb(pad.status)
            Spacer(Modifier.width(10.dp))
            Column {
                Text(pad.status.label, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
                Text(pad.actionLabel, color = ColorTextSecondary, fontSize = 13.sp)
            }
        }

        // TooLoud overlay badge - shown when clipping flag is true
        if (pad.clipping) {
            Spacer(Modifier.height(10.dp))
            TooLoudBadge()
        }

        // Cue pills for Layering state
        if (pad.status == LoopPadStatus.Layering) {
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                CuePill("red cue", WaveformColorRole.Red.toColor())
                CuePill("yellow cue", WaveformColorRole.Amber.toColor())
            }
        }
    }
}

/**
 * Determines border color based on pad state and selection.
 * Per PRD:
 * - Selected (any base state): Mint (#5BE6C7)
 * - Empty (not selected): Dark (#293241)
 * - Listening: Red (#FF5A66)
 * - Recording: Red (#FF5A66)
 * - Playing (not selected): Dark (#293241)
 * - Muted: Amber (#FFD166)
 * - Stopped: Dark (#293241)
 * - OverdubArmed: Amber (#FFD166)
 * - Layering: Red/Yellow cues (implemented via cue pills, border stays Amber)
 * - MicNeeded: Red (#FF5A66)
 */
private fun getBorderColor(pad: LoopPadUiModel): Color {
    if (pad.selected) return ColorMint

    return when (pad.status) {
        LoopPadStatus.Empty -> ColorDarkBorder
        LoopPadStatus.Listening -> ColorRed
        LoopPadStatus.Recording -> ColorRed
        LoopPadStatus.Playing -> ColorDarkBorder
        LoopPadStatus.Muted -> ColorAmber
        LoopPadStatus.Stopped -> ColorDarkBorder
        LoopPadStatus.OverdubArmed -> ColorAmber
        LoopPadStatus.Layering -> ColorAmber  // Red/Yellow shown via cue pills
        LoopPadStatus.MicNeeded -> ColorRed
    }
}

@Composable
fun WaveformStrip(waveform: List<WaveformBar>, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(ColorWaveformBg)
            .padding(horizontal = 8.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        waveform.forEach { bar ->
            val barHeightPx = if (bar.height > 0f) (8 + 32 * bar.height).dp else 1.dp
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(barHeightPx)
                    .clip(RoundedCornerShape(8.dp))
                    .background(bar.colorRole.toColor())
            )
        }
    }
}

/**
 * Status orb color per state per PRD:
 * - Empty: Dim gray (#44505F)
 * - Listening: Red (#FF5A66)
 * - Recording: Red (#FF5A66)
 * - Playing: Mint (#5BE6C7)
 * - Muted: Amber (#FFD166)
 * - Stopped: Dim (#44505F)
 * - OverdubArmed: Amber (#FFD166)
 * - Layering: Red (#FF5A66)
 * - MicNeeded: Red (#FF5A66)
 */
@Composable
private fun StatusOrb(status: LoopPadStatus) {
    val color = when (status) {
        LoopPadStatus.Empty -> ColorDim
        LoopPadStatus.Listening -> ColorRed
        LoopPadStatus.Recording -> ColorRed
        LoopPadStatus.Playing -> ColorMint
        LoopPadStatus.Muted -> ColorAmber
        LoopPadStatus.Stopped -> ColorDim
        LoopPadStatus.OverdubArmed -> ColorAmber
        LoopPadStatus.Layering -> ColorRed
        LoopPadStatus.MicNeeded -> ColorRed
    }
    Box(Modifier.size(36.dp).clip(CircleShape).background(color.copy(alpha = .25f)).border(2.dp, color, CircleShape))
}

/**
 * TooLoud badge - small warning pill "Too loud" in red.
 * This is a FLAG overlay shown on top of the base state, not a replacement.
 */
@Composable
private fun TooLoudBadge() {
    Text(
        text = "Too loud",
        color = Color.White,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(ColorRed)
            .padding(horizontal = 9.dp, vertical = 4.dp)
    )
}

@Composable
private fun CuePill(text: String, color: Color) {
    Text(
        text = text,
        color = Color.White,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.clip(RoundedCornerShape(50)).background(color).padding(horizontal = 9.dp, vertical = 4.dp)
    )
}
