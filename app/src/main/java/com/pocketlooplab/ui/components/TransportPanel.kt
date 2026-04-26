package com.pocketlooplab.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.pocketlooplab.model.TransportUiModel

private val ColorChipBg = Color(0xFF0B1118)
private val ColorChipBorder = Color(0xFF26313F)
private val ColorChipText = Color(0xFFB8C4CC)
private val ColorPanelBg = Color(0xFF151D27)
private val ColorPanelBorder = Color(0xFF303B48)

@Composable
fun TransportPanel(
    transport: TransportUiModel,
    modifier: Modifier = Modifier,
    onFreeTempoToggle: () -> Unit = {},
    onLocalOnlyToggle: () -> Unit = {},
    onInputArmToggle: () -> Unit = {}
) {
    // Track toggle states for visual feedback
    var freeTempoActive by remember { mutableStateOf(false) }
    var localOnlyActive by remember { mutableStateOf(false) }
    var inputArmedActive by remember { mutableStateOf(true) } // Default armed

    Column(
        modifier = modifier
            .semantics { contentDescription = "Transport panel" }
            .clip(RoundedCornerShape(26.dp))
            .background(ColorPanelBg)
            .border(1.dp, ColorPanelBorder, RoundedCornerShape(26.dp))
            .padding(16.dp)
    ) {
        Text(transport.masterLabel, color = Color(0xFFEAF2F5), fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Row(Modifier.fillMaxWidth().padding(top = 12.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            TransportChip(
                label = transport.bpmLabel,
                modifier = Modifier.weight(1f),
                isActive = freeTempoActive,
                onClick = {
                    freeTempoActive = !freeTempoActive
                    onFreeTempoToggle()
                }
            )
            TransportChip(
                label = transport.syncLabel,
                modifier = Modifier.weight(1f),
                isActive = localOnlyActive,
                onClick = {
                    localOnlyActive = !localOnlyActive
                    onLocalOnlyToggle()
                }
            )
            TransportChip(
                label = transport.armedLabel,
                modifier = Modifier.weight(1f),
                isActive = inputArmedActive,
                onClick = {
                    inputArmedActive = !inputArmedActive
                    onInputArmToggle()
                }
            )
        }
    }
}

@Composable
private fun TransportChip(
    label: String,
    modifier: Modifier = Modifier,
    isActive: Boolean = false,
    onClick: () -> Unit = {}
) {
    val backgroundColor = if (isActive) Color(0xFF1A3D2E) else ColorChipBg
    val textColor = if (isActive) Color(0xFF5BE6C7) else ColorChipText
    val borderColor = if (isActive) Color(0xFF5BE6C7) else ColorChipBorder

    Text(
        text = label,
        color = textColor,
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = modifier
            .semantics { contentDescription = label }
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 10.dp)
    )
}
