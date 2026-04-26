package com.pocketlooplab.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Centralized color palette for Pocket Loop Lab.
 * Single source of truth for all app colors.
 *
 * Usage in composables:
 *   Text("Hello", color = AppColors.textPrimary)
 *   Column(modifier = Modifier.background(AppColors.surface))
 *
 * Material3 semantic tokens (use via MaterialTheme.colorScheme) are preferred
 * for structural elements (background, surface, primary actions).
 * AppColors is for ad-hoc UI color assignments.
 */
object AppColors {
    // ── Semantic: Mint ──────────────────────────────────────────────────
    val mint          = Color(0xFF5BE6C7)
    val mintDark      = Color(0xFF1A3D2E)  // selected chip bg
    val mintText      = Color(0xFF06231E)  // text on mint

    // ── Semantic: Amber ───────────────────────────────────────────────
    val amber         = Color(0xFFFFD166)
    val amberDark     = Color(0xFF2B2100)  // text on amber
    val amberProtoBg  = Color(0xFF1E1808)  // 8% amber: rgba(255,209,102,0.08)
    val amberProtoBorder = Color(0xFF5A3C0F) // 30% amber: rgba(255,209,102,0.3)

    // ── Semantic: Red ─────────────────────────────────────────────────
    val red           = Color(0xFFFF5A66)
    val redDark       = Color(0xFF2B070A)  // text on red
    val dangerBg      = Color(0xFF372027)
    val dangerBorder  = Color(0xFFFF5A66)

    // ── Semantic: Waveform ─────────────────────────────────────────────
    val waveformMint  = Color(0xFF5BE6C7)
    val waveformAmber = Color(0xFFFFD166)
    val waveformRed   = Color(0xFFFF5A66)
    val waveformDim   = Color(0xFF44505F)
    val waveformBg    = Color(0xFF0B1118)

    // ── Structural ────────────────────────────────────────────────────
    val background    = Color(0xFF0D1117)
    val surface       = Color(0xFF151B24)
    val surfaceLight  = Color(0xFF202A35)
    val surfaceDark    = Color(0xFF121923)
    val panelBg       = Color(0xFF1F2A37)
    val sheetBg       = Color(0xFF1A232E)
    val sheetBorder   = Color(0xFF384556)
    val transportBg   = Color(0xFF151D27)
    val chipBg        = Color(0xFF0B1118)
    val chipBorder     = Color(0xFF26313F)

    // ── Borders ────────────────────────────────────────────────────────
    val border        = Color(0xFF293241)
    val borderMedium  = Color(0xFF303B48)
    val borderLight   = Color(0xFF384556)

    // ── Text ───────────────────────────────────────────────────────────
    val textPrimary   = Color(0xFFEAF2F5)
    val textSecondary  = Color(0xFFB8C4CC)
    val textMuted     = Color(0xFF8C9AA7)

    // ── Permission banner ──────────────────────────────────────────────
    val bannerBg      = Color(0xFF2A1A1A)
    val bannerBorder  = Color(0xFFFF5A66)
}
