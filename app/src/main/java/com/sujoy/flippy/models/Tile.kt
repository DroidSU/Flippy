package com.sujoy.flippy.models



data class Tile(val id : Int, val type: CardType = CardType.HIDDEN, val isRevealed : Boolean = false)