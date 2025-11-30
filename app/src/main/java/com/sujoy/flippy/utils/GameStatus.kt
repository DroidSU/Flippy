package com.sujoy.flippy.utils

sealed class GameStatus {
    data object READY : GameStatus()
    data object PLAYING : GameStatus()
    data object GAME_OVER : GameStatus()
}