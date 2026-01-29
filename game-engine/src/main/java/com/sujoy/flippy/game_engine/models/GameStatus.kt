package com.sujoy.flippy.game_engine.models

sealed class GameStatus {
    data object READY : GameStatus()
    data object PLAYING : GameStatus()
    data object GAME_OVER : GameStatus()
}
