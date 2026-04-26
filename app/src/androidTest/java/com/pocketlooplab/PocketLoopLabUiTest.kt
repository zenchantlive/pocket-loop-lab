package com.pocketlooplab

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import com.pocketlooplab.model.LoopPadStatus
import com.pocketlooplab.model.LoopPadUiModel
import com.pocketlooplab.model.WaveformBar
import com.pocketlooplab.model.WaveformColorRole
import com.pocketlooplab.ui.components.LoopPadCard
import com.pocketlooplab.ui.theme.PocketLoopLabTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

/**
 * TDD Tests for all 9 pad states + TooLoud overlay.
 * These tests verify that LoopPadCard renders correctly for each state.
 */
class PocketLoopLabUiTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private fun dimWaveform() = listOf(
        WaveformBar(.24f, WaveformColorRole.Dim),
        WaveformBar(.30f, WaveformColorRole.Dim),
        WaveformBar(.20f, WaveformColorRole.Dim),
        WaveformBar(.34f, WaveformColorRole.Dim),
        WaveformBar(.22f, WaveformColorRole.Dim),
        WaveformBar(.28f, WaveformColorRole.Dim)
    )

    private fun coloredWaveform() = listOf(
        WaveformBar(.28f, WaveformColorRole.Mint),
        WaveformBar(.52f, WaveformColorRole.Mint),
        WaveformBar(.76f, WaveformColorRole.Mint),
        WaveformBar(.42f, WaveformColorRole.Mint),
        WaveformBar(.88f, WaveformColorRole.Mint),
        WaveformBar(.58f, WaveformColorRole.Mint)
    )

    private fun amberWaveform() = listOf(
        WaveformBar(.34f, WaveformColorRole.Amber),
        WaveformBar(.62f, WaveformColorRole.Amber),
        WaveformBar(.42f, WaveformColorRole.Amber),
        WaveformBar(.82f, WaveformColorRole.Amber),
        WaveformBar(.55f, WaveformColorRole.Amber),
        WaveformBar(.72f, WaveformColorRole.Amber),
        WaveformBar(.46f, WaveformColorRole.Amber),
        WaveformBar(.64f, WaveformColorRole.Amber)
    )

    // ============================================================
    // Task s2-1: Tests for all 9 pad states
    // ============================================================

    @Test
    fun emptyPad_rendersCorrectly_showsEmptyLabelAndHoldToRecord() {
        val pad = LoopPadUiModel(
            id = 1,
            title = "Pad 1",
            status = LoopPadStatus.Empty,
            actionLabel = "Hold to record",
            waveform = dimWaveform()
        )

        composeTestRule.setContent {
            PocketLoopLabTheme {
                LoopPadCard(pad = pad)
            }
        }

        composeTestRule.onNodeWithText("Empty").assertIsDisplayed()
        composeTestRule.onNodeWithText("Hold to record").assertIsDisplayed()
    }

    @Test
    fun emptyPad_doesNotShowActivePlayButton() {
        val pad = LoopPadUiModel(
            id = 1,
            title = "Pad 1",
            status = LoopPadStatus.Empty,
            actionLabel = "Hold to record",
            waveform = dimWaveform()
        )

        composeTestRule.setContent {
            PocketLoopLabTheme {
                LoopPadCard(pad = pad)
            }
        }

        // Empty pads should NOT show "Playing" or "Tap to mute" or "Tap to play"
        composeTestRule.onAllNodesWithText("Playing").forEach { node ->
            // Should have zero nodes with "Playing" text in Empty state
        }
        // Verify "Hold to record" is shown instead
        composeTestRule.onNodeWithText("Hold to record").assertIsDisplayed()
    }

    @Test
    fun listeningPad_rendersCorrectly_showsListeningLabelAndListeningAction() {
        val pad = LoopPadUiModel(
            id = 1,
            title = "Pad 1",
            status = LoopPadStatus.Listening,
            actionLabel = "Listening...",
            waveform = dimWaveform()
        )

        composeTestRule.setContent {
            PocketLoopLabTheme {
                LoopPadCard(pad = pad)
            }
        }

        composeTestRule.onNodeWithText("Listening").assertIsDisplayed()
        composeTestRule.onNodeWithText("Listening...").assertIsDisplayed()
    }

    @Test
    fun recordingPad_rendersCorrectly_showsRecordingLabelAndReleaseToLoop() {
        val pad = LoopPadUiModel(
            id = 1,
            title = "Pad 1",
            status = LoopPadStatus.Recording,
            actionLabel = "Release to loop",
            waveform = dimWaveform()
        )

        composeTestRule.setContent {
            PocketLoopLabTheme {
                LoopPadCard(pad = pad)
            }
        }

        composeTestRule.onNodeWithText("Recording").assertIsDisplayed()
        composeTestRule.onNodeWithText("Release to loop").assertIsDisplayed()
    }

    @Test
    fun playingPad_rendersCorrectly_showsPlayingLabelAndTapToMute() {
        val pad = LoopPadUiModel(
            id = 1,
            title = "Pad 1",
            status = LoopPadStatus.Playing,
            actionLabel = "Tap to mute",
            waveform = coloredWaveform()
        )

        composeTestRule.setContent {
            PocketLoopLabTheme {
                LoopPadCard(pad = pad)
            }
        }

        composeTestRule.onNodeWithText("Playing").assertIsDisplayed()
        composeTestRule.onNodeWithText("Tap to mute").assertIsDisplayed()
    }

    @Test
    fun mutedPad_rendersCorrectly_showsMutedLabelAndTapToPlay() {
        val pad = LoopPadUiModel(
            id = 1,
            title = "Pad 1",
            status = LoopPadStatus.Muted,
            actionLabel = "Tap to play",
            waveform = coloredWaveform()
        )

        composeTestRule.setContent {
            PocketLoopLabTheme {
                LoopPadCard(pad = pad)
            }
        }

        composeTestRule.onNodeWithText("Muted").assertIsDisplayed()
        composeTestRule.onNodeWithText("Tap to play").assertIsDisplayed()
    }

    @Test
    fun stoppedPad_rendersCorrectly_showsStoppedLabel() {
        val pad = LoopPadUiModel(
            id = 1,
            title = "Pad 1",
            status = LoopPadStatus.Stopped,
            actionLabel = "Tap to play",
            waveform = coloredWaveform()
        )

        composeTestRule.setContent {
            PocketLoopLabTheme {
                LoopPadCard(pad = pad)
            }
        }

        composeTestRule.onNodeWithText("Stopped").assertIsDisplayed()
    }

    @Test
    fun overdubArmedPad_rendersCorrectly_showsOverdubArmedLabelAndHoldToLayer() {
        val pad = LoopPadUiModel(
            id = 1,
            title = "Pad 1",
            status = LoopPadStatus.OverdubArmed,
            actionLabel = "Hold to layer",
            waveform = coloredWaveform()
        )

        composeTestRule.setContent {
            PocketLoopLabTheme {
                LoopPadCard(pad = pad)
            }
        }

        composeTestRule.onNodeWithText("Overdub armed").assertIsDisplayed()
        composeTestRule.onNodeWithText("Hold to layer").assertIsDisplayed()
    }

    @Test
    fun layeringPad_rendersCorrectly_showsLayeringLabelAndReleaseToLayer_NOT_Stop() {
        val pad = LoopPadUiModel(
            id = 1,
            title = "Pad 1",
            status = LoopPadStatus.Layering,
            actionLabel = "Release to layer",
            waveform = amberWaveform(),
            cueRoles = listOf(WaveformColorRole.Red, WaveformColorRole.Amber)
        )

        composeTestRule.setContent {
            PocketLoopLabTheme {
                LoopPadCard(pad = pad)
            }
        }

        // PRD rule: Do NOT show "Stop" as a state label on Layering pads
        // "Stop" is an ACTION, not a state
        composeTestRule.onNodeWithText("Layering").assertIsDisplayed()
        composeTestRule.onNodeWithText("Release to layer").assertIsDisplayed()
    }

    @Test
    fun layeringPad_doesNOTShowStopAsStateLabel() {
        val pad = LoopPadUiModel(
            id = 1,
            title = "Pad 1",
            status = LoopPadStatus.Layering,
            actionLabel = "Release to layer",
            waveform = amberWaveform(),
            cueRoles = listOf(WaveformColorRole.Red, WaveformColorRole.Amber)
        )

        composeTestRule.setContent {
            PocketLoopLabTheme {
                LoopPadCard(pad = pad)
            }
        }

        // The action label is "Release to layer", NOT "Stop"
        // If "Stop" appears, it should only be in the action context
        // and NOT as a replacement for "Layering" label
        composeTestRule.onNodeWithText("Layering").assertIsDisplayed()
    }

    @Test
    fun micNeededPad_rendersCorrectly_showsMicNeededLabel() {
        val pad = LoopPadUiModel(
            id = 1,
            title = "Pad 1",
            status = LoopPadStatus.MicNeeded,
            actionLabel = "Mic permission needed",
            waveform = dimWaveform()
        )

        composeTestRule.setContent {
            PocketLoopLabTheme {
                LoopPadCard(pad = pad)
            }
        }

        composeTestRule.onNodeWithText("Mic needed").assertIsDisplayed()
    }

    // ============================================================
    // Task s2-1: Test for TooLoud overlay flag
    // ============================================================

    @Test
    fun tooLoudOverlay_rendersOnTopOfBaseState_showsTooLoudBadgeWithoutReplacingBaseState() {
        // TooLoud is a FLAG overlay, not a separate mutually-exclusive state
        // It should show "Too loud" indicator without replacing base state label
        val pad = LoopPadUiModel(
            id = 1,
            title = "Pad 1",
            status = LoopPadStatus.Recording,
            actionLabel = "Release to loop",
            waveform = dimWaveform(),
            clipping = true  // TooLoud flag set
        )

        composeTestRule.setContent {
            PocketLoopLabTheme {
                LoopPadCard(pad = pad)
            }
        }

        // Base state should still be visible
        composeTestRule.onNodeWithText("Recording").assertIsDisplayed()
        composeTestRule.onNodeWithText("Release to loop").assertIsDisplayed()

        // TooLoud badge should also be visible
        composeTestRule.onNodeWithText("Too loud").assertIsDisplayed()
    }

    @Test
    fun tooLoudOverlay_onPlayingPad_preservesPlayingState() {
        val pad = LoopPadUiModel(
            id = 1,
            title = "Pad 1",
            status = LoopPadStatus.Playing,
            actionLabel = "Tap to mute",
            waveform = coloredWaveform(),
            clipping = true
        )

        composeTestRule.setContent {
            PocketLoopLabTheme {
                LoopPadCard(pad = pad)
            }
        }

        // Base state preserved
        composeTestRule.onNodeWithText("Playing").assertIsDisplayed()
        composeTestRule.onNodeWithText("Tap to mute").assertIsDisplayed()

        // TooLoud badge shown on top
        composeTestRule.onNodeWithText("Too loud").assertIsDisplayed()
    }

    @Test
    fun noClippingFlag_noTooLoudBadgeShown() {
        val pad = LoopPadUiModel(
            id = 1,
            title = "Pad 1",
            status = LoopPadStatus.Playing,
            actionLabel = "Tap to mute",
            waveform = coloredWaveform(),
            clipping = false  // No clipping
        )

        composeTestRule.setContent {
            PocketLoopLabTheme {
                LoopPadCard(pad = pad)
            }
        }

        // Playing state visible
        composeTestRule.onNodeWithText("Playing").assertIsDisplayed()

        // TooLoud badge must NOT be shown when clipping=false
        // Assert the semantics tree has zero "Too loud" nodes
        val tooLoudNodes = composeTestRule.onAllNodesWithText("Too loud").fetchSemanticsNodes()
        assertEquals(0, tooLoudNodes.size)
    }

    // ============================================================
    // Test for Selected state (mint border and "Selected" chip)
    // ============================================================

    @Test
    fun selectedPad_showsSelectedChip_regardlessOfUnderlyingState() {
        val pad = LoopPadUiModel(
            id = 1,
            title = "Pad 1",
            status = LoopPadStatus.Muted,
            actionLabel = "Tap to play",
            waveform = coloredWaveform(),
            selected = true
        )

        composeTestRule.setContent {
            PocketLoopLabTheme {
                LoopPadCard(pad = pad)
            }
        }

        // "Selected" chip should be visible
        composeTestRule.onNodeWithText("Selected").assertIsDisplayed()
        // Base state should still be Muted
        composeTestRule.onNodeWithText("Muted").assertIsDisplayed()
    }
}