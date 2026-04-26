package com.pocketlooplab.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pocketlooplab.audio.FakeLoopAudioEngine
import com.pocketlooplab.model.PocketLoopLabUiState
import com.pocketlooplab.state.PocketLoopLabViewModel
import com.pocketlooplab.ui.components.LoopPadCard
import com.pocketlooplab.ui.components.PadEditSheet
import com.pocketlooplab.ui.components.TransportPanel
import com.pocketlooplab.ui.theme.AppColors
import com.pocketlooplab.ui.theme.PocketLoopLabTheme

@Composable
fun PocketLoopLabScreen(
    viewModel: PocketLoopLabViewModel,
    modifier: Modifier = Modifier,
    onRequestPermission: () -> Unit = {}
) {
    val pads by viewModel.pads.collectAsState()
    val selectedPadId by viewModel.selectedPadId.collectAsState()
    val editSheetVisible by viewModel.editSheetVisible.collectAsState()
    val hasPermission by viewModel.hasRecordPermission.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(AppColors.background, AppColors.surfaceDark)))
            .semantics { contentDescription = "Pocket Loop Lab" },
        contentPadding = PaddingValues(18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { ViewModelHeader() }
        if (!hasPermission) {
            item { PermissionBanner(onRequestPermission) }
        }
        item { ViewModelSurfaceSection(pads, selectedPadId, viewModel) }
        item {
            TransportPanel(
                transport = buildTransportUiModel(),
                modifier = Modifier.fillMaxWidth(),
                onFreeTempoToggle = { /* TODO: wire to ViewModel */ },
                onLocalOnlyToggle = { /* TODO: wire to ViewModel */ },
                onInputArmToggle = { /* TODO: wire to ViewModel */ }
            )
        }
        if (editSheetVisible && selectedPadId != null) {
            item {
                PadEditSheet(
                    editSheet = buildPadEditUiModel(selectedPadId!!, pads),
                    modifier = Modifier.fillMaxWidth(),
                    selectedPadId = selectedPadId,
                    onVolumeChange = { padId, db -> viewModel.onVolumeChange(padId, db) },
                    onSpeedChange = { padId, speed -> viewModel.onSpeedChange(padId, speed) },
                    onOverdubArm = { padId -> viewModel.onOverdubArm(padId) },
                    onClear = { padId -> viewModel.onClearPad(padId) },
                    onDismiss = { viewModel.onEditSheetDismiss() }
                )
            }
        }
    }
}

@Composable
private fun ViewModelHeader() {
    Column(Modifier.fillMaxWidth()) {
        Text("Pocket recorder / loop station", color = AppColors.textMuted, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        Text("Pocket Loop Lab", color = Color.White, fontSize = 34.sp, fontWeight = FontWeight.Black)
    }
}

@Composable
private fun PermissionBanner(onRequestPermission: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AppColors.bannerBg)
            .border(1.dp, AppColors.red, RoundedCornerShape(16.dp))
            .clickable { onRequestPermission() }
            .padding(16.dp)
    ) {
        Text("🎤 Mic access required", color = AppColors.red, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Text(
            "Hold any pad to start recording a loop.",
            color = AppColors.textSecondary,
            fontSize = 13.sp,
            modifier = Modifier.padding(top = 4.dp)
        )
        Text(
            "Tap to grant permission →",
            color = AppColors.mint,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = 6.dp)
        )
    }
}

@Composable
private fun ViewModelSurfaceSection(
    pads: List<com.pocketlooplab.model.LoopPadUiModel>,
    selectedPadId: Int?,
    viewModel: PocketLoopLabViewModel
) {
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(34.dp))
            .background(AppColors.surface)
            .padding(16.dp)
    ) {
        Row(Modifier.fillMaxWidth()) {
            Text("Live Lab Surface", color = AppColors.textPrimary, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
            Spacer(Modifier.weight(1f))
            Column {
                Text("0m 0s", color = AppColors.amber, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text("LoFi", color = AppColors.mint, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(Modifier.height(14.dp))
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                val pad1 = pads.find { it.id == 1 }!!
                LoopPadCard(
                    pad = pad1,
                    modifier = Modifier.weight(1f).height(190.dp),
                    onPress = { viewModel.onPadPress(1) },
                    onRelease = { viewModel.onPadRelease(1) },
                    onTap = { viewModel.onPadTap(1) },
                    onLongPress = { viewModel.onPadLongPress(1) }
                )
                val pad2 = pads.find { it.id == 2 }!!
                LoopPadCard(
                    pad = pad2,
                    modifier = Modifier.weight(1f).height(190.dp),
                    onPress = { viewModel.onPadPress(2) },
                    onRelease = { viewModel.onPadRelease(2) },
                    onTap = { viewModel.onPadTap(2) },
                    onLongPress = { viewModel.onPadLongPress(2) }
                )
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                val pad3 = pads.find { it.id == 3 }!!
                LoopPadCard(
                    pad = pad3,
                    modifier = Modifier.weight(1f).height(168.dp),
                    onPress = { viewModel.onPadPress(3) },
                    onRelease = { viewModel.onPadRelease(3) },
                    onTap = { viewModel.onPadTap(3) },
                    onLongPress = { viewModel.onPadLongPress(3) }
                )
                val pad4 = pads.find { it.id == 4 }!!
                LoopPadCard(
                    pad = pad4,
                    modifier = Modifier.weight(1f).height(168.dp),
                    onPress = { viewModel.onPadPress(4) },
                    onRelease = { viewModel.onPadRelease(4) },
                    onTap = { viewModel.onPadTap(4) },
                    onLongPress = { viewModel.onPadLongPress(4) }
                )
            }
        }
    }
}

private fun buildTransportUiModel() = com.pocketlooplab.model.TransportUiModel(
    bpmLabel = "Free tempo",
    syncLabel = "Local only",
    masterLabel = "Transport",
    armedLabel = "Input armed"
)

private fun buildPadEditUiModel(padId: Int, pads: List<com.pocketlooplab.model.LoopPadUiModel>): com.pocketlooplab.model.PadEditUiModel {
    val pad = pads.find { it.id == padId }!!
    return com.pocketlooplab.model.PadEditUiModel(
        title = "Pad $padId Edit Sheet",
        trimLabel = "Trim",
        volumeLabel = "Volume 0 dB",
        speedLabel = "Speed 1x",
        waveform = pad.waveform,
        reverseLabel = "Reverse",
        overdubLabel = "Overdub",
        clearLabel = "Clear"
    )
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun PocketLoopLabScreenPreview() {
    PocketLoopLabTheme {
        // Placeholder — state and viewmodel required for full preview
        Box(modifier = Modifier.fillMaxSize())
    }
}
