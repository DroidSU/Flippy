package com.sujoy.flippy.core.theme

import androidx.compose.ui.graphics.Color

/**
 * Flippy Twilight Slate Palette
 * A sophisticated, professional dark theme with balanced contrast.
 */

// --- Dark Mode (Twilight Slate) ---
val TwilightDeep = Color(0xFF0F172A)      // Deep Slate Background
val TwilightSurface = Color(0xFF1E293B)   // Lighter Slate Surface
val TwilightElevated = Color(0xFF334155)  // Card / Elevated Surface
val ElectricIndigo = Color(0xFF6366F1)    // Primary Action (Balanced Indigo)
val SoftMint = Color(0xFF2DD4BF)          // Secondary / Success (Mint)
val Rosewood = Color(0xFFFB7185)          // Tertiary / Fever / Bombs
val SlateText = Color(0xFFF1F5F9)         // High-contrast text
val SlateTextDim = Color(0xFF94A3B8)      // Muted slate text

// --- Light Mode (Arctic Light) ---
val ArcticWhite = Color(0xFFF8FAFC)     // Clean Slate Background
val ArcticSurface = Color(0xFFFFFFFF)   // Pure White Surface
val ArcticTeal = Color(0xFF0D9488)      // Primary action
val ArcticViolet = Color(0xFF7C3AED)    // Secondary
val ArcticRose = Color(0xFFDB2777)      // Tertiary
val ArcticDeepText = Color(0xFF0F172A)  // Contrast text
val ArcticMutedText = Color(0xFF475569) // Muted text

// --- Game Specific Effects ---
val GlowIndigo = Color(0xFF6366F1).copy(alpha = 0.4f)
val GlowRose = Color(0xFFFB7185).copy(alpha = 0.4f)

// --- Common UI Utility ---
val GlassWhite = Color(0xFFFFFFFF).copy(alpha = 0.1f)
val GlassBlack = Color(0xFF000000).copy(alpha = 0.4f)
