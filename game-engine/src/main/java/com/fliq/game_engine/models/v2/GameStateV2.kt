package com.fliq.game_engine.models.v2

enum class GameStateV2 {
    IDLE,        // Before start
    BRIEFING,    // Showing goals/loadout
    COUNTDOWN,   // 3, 2, 1...
    PREVIEW,     // For Memory mode: watching the pattern
    ACTION,      // The Heat: actual playing
    FEVER,       // Bonus state
    FALLOUT,     // End of match transition (Matrix effect)
    VAULT,       // Results and rewards screen
    PAUSED
}
