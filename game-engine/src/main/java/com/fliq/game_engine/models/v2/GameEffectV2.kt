package com.fliq.game_engine.models.v2

sealed interface GameEffectV2 {
    data class Success(val tileId: Int, val combo: Int) : GameEffectV2
    object Miss : GameEffectV2
    data class Explosion(val tileId: Int) : GameEffectV2
    data class VaultOpened(val stars: Int, val xp: Int, val coins: Int) : GameEffectV2
    object FeverStarted : GameEffectV2
    object FeverEnded : GameEffectV2
}
