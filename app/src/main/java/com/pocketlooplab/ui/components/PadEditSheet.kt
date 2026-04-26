package com.pocketlooplab.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pocketlooplab.model.PadEditUiModel
import com.pocketlooplab.model.PlaybackSpeed

private val ColorSheetBg = Color(0xFF1A232E)
private val ColorSheetBorder = Color(0xFF384556)
private val ColorChipBg = Color(0xFF101821)
private val ColorChipBorder = Color(0xFF303C4C)
private val ColorDangerBg = Color(0xFF372027)
private val ColorDangerBorder = Color(0xFFFF5A66)
private val ColorTextPrimary = Color(0xFFEAF2F5)
private val ColorTextDanger = Color(0xFFFFD3D7)
private val ColorAmber = Color(0xFFFFD166)
private val ColorMint = Color(0xFF5BE6C7)

@Composable
fun PadEditSheet(
    editSheet: PadEditUiModel,
    modifier: Modifier = Modifier,
    selectedPadId: Int? = null,
    onVolumeChange: (Int, Float) -> Unit = { _, _ -> },
    onSpeedChange: (Int, PlaybackSpeed) -> Unit = { _, _ -> },
    onReverseToggle: (Int) -> Unit = { _ -> },
    onTrimToggle: (Int) -> Unit = { _ -> },
    onOverdubArm: (Int) -> Unit = { _ -> },
    onClear: (Int) -> Unit = {},
    onDismiss: () -> Unit = {}
) {
    var volumeValue by remember { mutableFloatStateOf(0f) }
    var showClearConfirm by remember { mutableStateOf(false) }
    var overdubArmed by remember { mutableStateOf(false) }

    if (showClearConfirm) {
        AlertDialog(
            onDismissRequest = { showClearConfirm = false },
            title = { Text("Clear this loop?", color = Color.White) },
            text = { Text("This cannot be undone.", color = ColorTextSecondary) },
            confirmButton = {
                TextButton(onClick = {
                    showClearConfirm = false
                    selectedPadId?.let { onClear(it) }
                }) {
                    Text("Clear", color = ColorRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirm = false }) {
                    Text("Cancel", color = ColorTextPrimary)
                }
            },
            containerColor = Color(0xFF1A232E),
            titleContentColor = Color.White,
            textContentColor = ColorTextSecondary
        )
    }

    Column(
        modifier = modifier
            .semantics { contentDescription = editSheet.title }
            .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp, bottomStart = 24.dp, bottomEnd = 24.dp))
            .background(ColorSheetBg)
            .border(1.dp, ColorSheetBorder, RoundedCornerShape(28.dp))
            .padding(18.dp)
    ) {
        Text(editSheet.title, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)

        // Waveform preview
        WaveformStrip(editSheet.waveform, modifier = Modifier.fillMaxWidth().height(58.dp).padding(top = 12.dp))

        // Volume slider
        Row(Modifier.fillMaxWidth().padding(top = 14.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            EditSlider(
                label = "Volume",
                value = volumeValue,
                onValueChange = { newValue ->
                    volumeValue = newValue
                    selectedPadId?.let { onVolumeChange(it, newValue) }
                },
                modifier = Modifier.weight(1f)
            )
        }

        // Speed chips
        Row(Modifier.fillMaxWidth().padding(top = 10.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            SpeedChip(
                label = "0.5x",
                isSelected = false,
                onClick = { selectedPadId?.let { onSpeedChange(it, PlaybackSpeed.Half) } },
                modifier = Modifier.weight(1f)
            )
            SpeedChip(
                label = "1x",
                isSelected = true,
                onClick = { selectedPadId?.let { onSpeedChange(it, PlaybackSpeed.Normal) } },
                modifier = Modifier.weight(1f)
            )
            SpeedChip(
                label = "2x",
                isSelected = false,
                onClick = { selectedPadId?.let { onSpeedChange(it, PlaybackSpeed.Double) } },
                modifier = Modifier.weight(1f)
            )
        }

        // Action chips row
        Row(Modifier.fillMaxWidth().padding(top = 10.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            PrototypeChip(editSheet.trimLabel, Modifier.weight(1f))
            PrototypeChip(editSheet.reverseLabel, Modifier.weight(1f))
            EditChip(
                label = if (overdubArmed) "Cancel overdub" else editSheet.overdubLabel,
                modifier = Modifier.weight(1f),
                onClick = {
                    overdubArmed = !overdubArmed
                    selectedPadId?.let { onOverdubArm(it) }
                }
            )
        }

        // Clear button (danger)
        Row(Modifier.fillMaxWidth().padding(top = 10.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            EditChip(
                label = editSheet.clearLabel,
                modifier = Modifier.weight(1f),
                danger = true,
                onClick = { showClearConfirm = true }
            )
        }

        // Dismiss button
        Row(Modifier.fillMaxWidth().padding(top = 10.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            EditChip(
                label = "Close",
                modifier = Modifier.weight(1f),
                onClick = onDismiss
            )
        }
    }
}

@Composable
private fun EditSlider(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "$label: ${String.format("%.1f", value)} dB",
            color = ColorTextPrimary,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = -12f..12f,
            steps = 23,
            colors = SliderDefaults.colors(
                thumbColor = ColorMint,
                activeTrackColor = ColorMint,
                inactiveTrackColor = Color(0xFF303C4C)
            ),
            modifier = Modifier.semantics { contentDescription = "$label slider" }
        )
    }
}

@Composable
private fun SpeedChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isSelected) Color(0xFF1A3D2E) else ColorChipBg
    val textColor = if (isSelected) ColorMint else ColorTextPrimary
    val borderColor = if (isSelected) ColorMint else ColorChipBorder

    Text(
        text = label,
        color = textColor,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        modifier = modifier
            .semantics { contentDescription = "Speed $label" }
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 12.dp)
    )
}

@Composable
private fun EditChip(
    label: String,
    modifier: Modifier = Modifier,
    danger: Boolean = false,
    onClick: () -> Unit = {}
) {
    val backgroundColor = if (danger) ColorDangerBg else ColorChipBg
    val textColor = if (danger) ColorTextDanger else ColorTextPrimary
    val borderColor = if (danger) ColorDangerBorder else ColorChipBorder

    Text(
        text = label,
        color = textColor,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        modifier = modifier
            .semantics { contentDescription = label }
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 12.dp)
    )
}

/** Prototype chip — visually indicates this feature is not yet functional. */
@Composable
private fun PrototypeChip(
    label: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = label,
        color = ColorAmber.copy(alpha = 0.6f),
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        modifier = modifier
            .semantics { contentDescription = "$label (prototype)" }
            .clip(RoundedCornerShape(16.dp))
            .background(ColorAmber.copy(alpha = 0.08f))
            .border(1.dp, ColorAmber.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
            .padding(horizontal = 10.dp, vertical = 12.dp)
    )
}
