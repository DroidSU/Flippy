package com.sujoy.flippy.game_engine.models

import androidx.compose.ui.geometry.Offset


enum class ParticleType { COIN, BOMB }
enum class VibrationType { SHORT, LONG }

data class EffectState(
    val id: Long = System.nanoTime(),
    val position: Offset,
    val type: EffectType,
    val text: String = ""
)

enum class EffectType { SCORE, PARTICLE_COIN, PARTICLE_BOMB }

sealed class GameEffect {
    data class ScorePopup(val tileId: Int, val score: String) : GameEffect()
    data class Particle(val tileId: Int, val type: ParticleType) : GameEffect()
    data class Vibration(val type: VibrationType) : GameEffect()
    data class BackgroundRipple(val position: Offset) : GameEffect()
}