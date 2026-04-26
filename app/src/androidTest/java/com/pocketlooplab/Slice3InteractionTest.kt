package com.pocketlooplab

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performGesture
import androidx.compose.ui.test.performLongPress
import androidx.compose.ui.test.performPressAndReleaseGesture
import androidx.compose.ui.test.assertIsDisplayed
import com.pocketlooplab.audio.FakeLoopAudioEngine
import com.pocketlooplab.model.LoopPadStatus
import com.pocketlooplab.model.LoopPadUiModel
import com.pocketlooplab.state.PocketLoopLabViewModel
import com.pocketlooplab.ui.PocketLoopLabScreen
import com.pocketlooplab.ui.theme.PocketLoopLabTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test

/**
 * Slice 3 Interaction Tests — verify the gesture-based state machine
 * using the FakeLoopAudioEngine.
 *
 * State machine under test:
 * - Empty + press → Listening
 * - Listening + hold 500ms → Recording
 * - Recording + release → Playing
 * - Playing + tap → Muted
 * - Muted + tap → Playing
 * - Playing + long-press → selected + editSheet
 * - Playing + overdub arm → OverdubArmed
 * - OverdubArmed + release → Layering → Playing
 * - Any pad + clear → Empty
 */
class Slice3InteractionTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val engine = FakeLoopAudioEngine()
    private val viewModel = PocketLoopLabViewModel(engine)

    private fun setScreen() {
        composeTestRule.setContent {
            PocketLoopLabTheme {
                PocketLoopLabScreen(viewModel = viewModel)
            }
        }
    }

    // ── Test helpers ─────────────────────────────────────────────────

    private fun getPad1Text(statusLabel: String) =
        composeTestRule.onNodeWithText(statusLabel, substring = true, ignoreCase = true)

    // ── Test: press empty pad → Listening ────────────────────────────

    @Test
    fun pressEmptyPad_showsListeningState() = runBlocking {
        setScreen()

        getPad1Text("Pad 1").assertIsDisplayed()

        // Simulate press on Pad 1
        viewModel.onPadPress(1)

        // Verify pad 1 transitions to Listening
        val pads = viewModel.pads.value
        assert(pads[0].status == LoopPadStatus.Listening) {
            "Expected Listening after press, got ${pads[0].status}"
        }
    }

    // ── Test: hold listening pad 500ms → Recording ──────────────────

    @Test
    fun holdListeningPad_transitionsToRecording() = runBlocking<Unit> {
        setScreen()

        viewModel.onPadPress(1)  // Empty → Listening
        delay(550L)              // Wait past the 500ms threshold

        val pads = viewModel.pads.value
        assert(pads[0].status == LoopPadStatus.Recording) {
            "Expected Recording after 500ms hold, got ${pads[0].status}"
        }
    }

    // ── Test: release recording pad → Playing ───────────────────────

    @Test
    fun releaseRecordingPad_transitionsToPlaying() = runBlocking {
        setScreen()

        viewModel.onPadPress(1)  // Empty → Listening
        delay(600L)              // Listening → Recording
        viewModel.onPadRelease(1) // Recording → Playing

        val pads = viewModel.pads.value
        assert(pads[0].status == LoopPadStatus.Playing) {
            "Expected Playing after release, got ${pads[0].status}"
        }
        assert(pads[0].actionLabel == "Tap to mute") {
            "Expected 'Tap to mute' action label, got '${pads[0].actionLabel}'"
        }
    }

    // ── Test: tap playing pad → Muted ────────────────────────────────

    @Test
    fun tapPlayingPad_transitionsToMuted() = runBlocking {
        setScreen()

        // Set up: get pad to Playing state
        viewModel.onPadPress(1)
        delay(600L)
        viewModel.onPadRelease(1)  // → Playing

        viewModel.onPadTap(1)  // Playing → Muted

        val pads = viewModel.pads.value
        assert(pads[0].status == LoopPadStatus.Muted) {
            "Expected Muted after tap, got ${pads[0].status}"
        }
        assert(pads[0].actionLabel == "Tap to unmute") {
            "Expected 'Tap to unmute', got '${pads[0].actionLabel}'"
        }
    }

    // ── Test: tap muted pad → Playing (unmute) ───────────────────────

    @Test
    fun tapMutedPad_transitionsToPlaying() = runBlocking {
        setScreen()

        viewModel.onPadPress(1)
        delay(600L)
        viewModel.onPadRelease(1)  // → Playing
        viewModel.onPadTap(1)      // → Muted

        viewModel.onPadTap(1)      // Muted → Playing

        val pads = viewModel.pads.value
        assert(pads[0].status == LoopPadStatus.Playing) {
            "Expected Playing after unmute tap, got ${pads[0].status}"
        }
    }

    // ── Test: long-press pad → selected + editSheetVisible ──────────

    @Test
    fun longPressPad_selectsPadAndOpensEditSheet() = runBlocking {
        setScreen()

        // Set pad to Playing first (long-press works on any state)
        viewModel.onPadPress(1)
        delay(600L)
        viewModel.onPadRelease(1)  // → Playing

        viewModel.onPadLongPress(1)

        assert(viewModel.selectedPadId.value == 1) {
            "Expected selectedPadId=1, got ${viewModel.selectedPadId.value}"
        }
        assert(viewModel.editSheetVisible.value) {
            "Expected editSheetVisible=true"
        }
    }

    // ── Test: clear pad → Empty ──────────────────────────────────────

    @Test
    fun clearPad_transitionsToEmpty() = runBlocking {
        setScreen()

        // Set pad to Playing
        viewModel.onPadPress(1)
        delay(600L)
        viewModel.onPadRelease(1)  // → Playing

        viewModel.onClearPad(1)

        val pads = viewModel.pads.value
        assert(pads[0].status == LoopPadStatus.Empty) {
            "Expected Empty after clear, got ${pads[0].status}"
        }
        assert(pads[0].actionLabel == "Hold to record") {
            "Expected 'Hold to record', got '${pads[0].actionLabel}'"
        }
    }

    // ── Test: overdub arm → OverdubArmed ────────────────────────────

    @Test
    fun overdubArm_transitionsToOverdubArmed() = runBlocking {
        setScreen()

        // Set pad to Playing
        viewModel.onPadPress(1)
        delay(600L)
        viewModel.onPadRelease(1)  // → Playing

        viewModel.onOverdubArm(1)  // Playing → OverdubArmed

        val pads = viewModel.pads.value
        assert(pads[0].status == LoopPadStatus.OverdubArmed) {
            "Expected OverdubArmed, got ${pads[0].status}"
        }
        assert(pads[0].actionLabel == "Release to add layer") {
            "Expected 'Release to add layer', got '${pads[0].actionLabel}'"
        }
    }

    // ── Test: overdub arm + release → Layering → Playing ────────────

    @Test
    fun releaseOverdubArmed_transitionsThroughLayeringToPlaying() = runBlocking {
        setScreen()

        viewModel.onPadPress(1)
        delay(600L)
        viewModel.onPadRelease(1)  // → Playing
        viewModel.onOverdubArm(1)  // → OverdubArmed

        viewModel.onPadRelease(1)  // OverdubArmed → Layering → Playing

        val pads = viewModel.pads.value
        // After release, state should be Layering (or already Playing if delay completed)
        assert(pads[0].status in listOf(LoopPadStatus.Layering, LoopPadStatus.Playing)) {
            "Expected Layering or Playing after overdub release, got ${pads[0].status}"
        }

        // Wait for Layering → Playing transition
        delay(700L)
        val finalPads = viewModel.pads.value
        assert(finalPads[0].status == LoopPadStatus.Playing) {
            "Expected Playing after Layering delay, got ${finalPads[0].status}"
        }
    }

    // ── Test: tap empty pad (quick press/release) → back to Empty ─────

    @Test
    fun quickTapEmptyPad_returnsToEmpty() = runBlocking {
        setScreen()

        viewModel.onPadPress(1)    // Empty → Listening
        viewModel.onPadRelease(1)  // Listening → Empty (no 500ms hold)

        val pads = viewModel.pads.value
        assert(pads[0].status == LoopPadStatus.Empty) {
            "Expected Empty after quick tap, got ${pads[0].status}"
        }
    }

    // ── Test: FakeLoopAudioEngine records and reports waveform ─────

    @Test
    fun fakeEngine_providesWaveformAfterRecording() = runBlocking {
        setScreen()

        viewModel.onPadPress(1)
        delay(600L)
        viewModel.onPadRelease(1)  // → Playing

        val waveform = engine.getWaveform(1)
        assert(waveform.isNotEmpty()) {
            "Expected non-empty waveform after recording, got empty"
        }
    }

    // ── Test: stopPlayback → Stopped ────────────────────────────────

    @Test
    fun stopPlayback_transitionsToStopped() = runBlocking {
        setScreen()

        viewModel.onPadPress(1)
        delay(600L)
        viewModel.onPadRelease(1)  // → Playing

        engine.stopPlayback(1)

        val pads = viewModel.pads.value
        assert(pads[0].status == LoopPadStatus.Stopped) {
            "Expected Stopped, got ${pads[0].status}"
        }
        assert(pads[0].actionLabel == "Tap to play") {
            "Expected 'Tap to play', got '${pads[0].actionLabel}'"
        }
    }
}
