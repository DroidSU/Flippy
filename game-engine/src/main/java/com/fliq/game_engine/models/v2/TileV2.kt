package com.fliq.game_engine.models.v2

data class TileV2(
    val id: Int,
    val type: TileType = TileType.EMPTY,
    val state: TileState = TileType.EMPTY.defaultState,
    val tapCount: Int = 0,      // Number of times tapped (for multi-tap)
    val requiredTaps: Int = 1,  // Number of taps required to pop
    val lastUpdateTime: Long = 0L,
    val duration: Long = 0L
)

enum class TileType(val defaultState: TileState) {
    EMPTY(TileState.INACTIVE),
    COIN(TileState.ACTIVE),
    BOMB(TileState.ACTIVE),
    TRACE_PATH(TileState.INACTIVE),
    LOCKED_COIN(TileState.LOCKED)
}

enum class TileState {
    INACTIVE,   // Not visible/not interactable
    LOCKED,     // Visible but not interactable yet
    ACTIVE,     // Visible and interactable
    POPPED,     // Successfully tapped
    MISSED      // Vanished before being tapped
}
