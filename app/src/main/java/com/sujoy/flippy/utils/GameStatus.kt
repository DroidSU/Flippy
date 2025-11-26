package com.sujoy.flippy.utils

sealed class GameStatus {
    data object PLAYING : GameStatus()
    data object WON : GameStatus()
    data object LOST : GameStatus()
}