package com.sujoy.flippy.core.theme

import androidx.compose.ui.graphics.Color

/**
 * Flippy Theme Palette
 * Professional Slate-based color system for high-end UI feel.
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

// --- Light Mode (Zenith Slate - The "Pro" Look) ---
val ZenithWhite = Color(0xFFF1F5F9)       // Clean Slate 100 Background
val ZenithSurface = Color(0xFFFFFFFF)     // Pure White Surface
val ZenithElevated = Color(0xFFE2E8F0)    // Subtle Slate 200 Surface
val ZenithBorder = Color(0xFFCBD5E1)      // Precise Slate 300 Borders
val InkIndigo = Color(0xFF4F46E5)         // Primary (Indigo 600)
val InkTeal = Color(0xFF0D9488)           // Secondary (Teal 600)
val InkRose = Color(0xFFE11D48)           // Tertiary (Rose 600)
val ZenithDeepText = Color(0xFF0F172A)    // High-contrast Slate 900
val ZenithMutedText = Color(0xFF64748B)   // Muted Slate 500

// --- Game Specific Effects ---
val GlowIndigo = Color(0xFF6366F1).copy(alpha = 0.4f)
val GlowRose = Color(0xFFFB7185).copy(alpha = 0.4f)

// --- Common UI Utility ---
val GlassWhite = Color(0xFFFFFFFF).copy(alpha = 0.1f)
val GlassBlack = Color(0xFF000000).copy(alpha = 0.4f)
