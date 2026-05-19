package com.fliq.game_engine.models

data class Tile(val id : Int, val type: CardType = CardType.HIDDEN, val isRevealed : Boolean = false)

enum class CardType { COIN, BOMB, HIDDEN }
