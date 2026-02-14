package com.flippy.game2.data

sealed class NewGameUiEvent {
    data class ShowRipple(val x: Float, val y: Float, val id: Long) : NewGameUiEvent()
}