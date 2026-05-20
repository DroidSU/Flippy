package com.fliq.game_engine.models

data class Tile(
    val id: Int,
    val type: CardType = CardType.HIDDEN,
    val isRevealed: Boolean = false,
    val isIconVisible: Boolean = true,
    val lastRevealTime: Long = 0L,
    val currentDuration: Long = 0L
)

enum class CardType { COIN, BOMB, HIDDEN }
